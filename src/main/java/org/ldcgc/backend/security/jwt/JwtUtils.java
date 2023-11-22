package org.ldcgc.backend.security.jwt;

import com.google.common.base.Preconditions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.exception.ApiError;
import org.ldcgc.backend.exception.ApiSubError;
import org.ldcgc.backend.exception.RequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.ldcgc.backend.util.conversion.Convert.convertDateToLocalDateTime;
import static org.ldcgc.backend.util.process.Process.runInBackground;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.TOKEN_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.TOKEN_NOT_PARSEABLE;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    @Getter @Setter
    private Boolean isRecoveryToken = false;

    private final TokenRepository tokenRepository;

    private static final Clock clock = Clock.systemUTC();
    //private static final Map<String, JWK> keyCache = new HashMap<>();
    private static final String ISSUER_URL = "https://gc8inventory.es";

    public SignedJWT generateNewToken(User user) throws ParseException, JOSEException {
        // Generate a key pair with Ed25519 curve
        OctetKeyPair jwk = new OctetKeyPairGenerator(Curve.Ed25519)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .algorithm(JWSAlgorithm.EdDSA)
            .generate();
        OctetKeyPair publicJWK = jwk.toPublicJWK();

        //keyCache.put(jwk.getKeyID(), jwk);

        // Create the EdDSA signer
        JWSSigner signer = new Ed25519Signer(jwk);

        Map<String, Object> claims = Map.of(
            "email", user.getEmail(),
            "role", user.getRole().getRoleName()
        );

        Date now = new Date();
        // expiration time is set to next 24 hours
        Date expirationTime = new Date(now.toInstant().plusSeconds(24 * 60 * 60).toEpochMilli());

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .jwtID(jwk.getKeyID())
            .subject("token")
            .issuer(ISSUER_URL)
            .issueTime(now)
            .claim("userClaims", claims)
            .expirationTime(expirationTime)
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.EdDSA)
            .keyID(jwk.getKeyID())
            .build(),
            claimsSet);

        // Compute the EC signature
        signedJWT.sign(signer);

        // Serialize the JWS to compact form
        String s = signedJWT.serialize();

        // On the consumer side, parse the JWS and verify its EdDSA signature
        signedJWT = SignedJWT.parse(s);

        JWSVerifier verifier = new Ed25519Verifier(publicJWK);

        Token token = Token.builder()
            .jwtID(jwk.getKeyID())
            .jwk(Base64.encode(jwk.toJSONString().getBytes()).toString())
            .userId(user.getId())
            .issuedAt(convertDateToLocalDateTime(now))
            .expiresAt(convertDateToLocalDateTime(expirationTime))
            .refreshTokenId(null)
            .build();
        runInBackground(() -> tokenRepository.save(token));

        // clean old tokens for this user
        runInBackground(() -> tokenRepository.deleteAllExpiredTokensFromUser(user.getId()));

        Preconditions.checkArgument(signedJWT.verify(verifier));
        Preconditions.checkArgument(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
        Preconditions.checkArgument(signedJWT.getJWTClaimsSet().getIssuer().equals(ISSUER_URL));

        return signedJWT;

    }

    public String getEmailFromJwtToken(SignedJWT signedJWT) throws ParseException {
        return ((Map) signedJWT.getJWTClaimsSet().getClaim("userClaims")).get("email").toString();
    }

    public SignedJWT getDecodedJwt(String jwt) {
        try {
            return SignedJWT.parse(jwt.split("Bearer ")[1]);
        } catch (ParseException e) {
            ApiError apiError = ApiError.builder()
                .error(ApiSubError.builder()
                    .field("jwt")
                    .message(e.getLocalizedMessage())
                    .build())
                .build();
            throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(TOKEN_NOT_PARSEABLE), apiError);
        }
    }

    public boolean verifyJwt(SignedJWT signedJwt, String expectedAudience) throws ParseException, JOSEException, IllegalArgumentException, NullPointerException {

        log.debug("Validating JWT with aud {}", expectedAudience);

        // parse signed token into header / claims
        JWSHeader jwsHeader = signedJwt.getHeader();

        // must exist and match the algorithm
        String kid = jwsHeader.getKeyID();
        String alg = jwsHeader.getAlgorithm().getName();

        String jwkString = tokenRepository.findByJwtID(kid)
            .map(Token::getJwk)
            .map(Base64::from)
            .map(Base64::decode)
            .map(String::new)
            .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(TOKEN_NOT_FOUND)));

        // header must have algorithm("alg") and "kid"
        Preconditions.checkNotNull(jwsHeader.getAlgorithm());
        Preconditions.checkNotNull(jwsHeader.getKeyID());

        JWTClaimsSet claims = signedJwt.getJWTClaimsSet();

        //TODO: claims must have audience, issuer
        // |> Preconditions.checkArgument(claims.getAudience().contains(expectedAudience));
        Preconditions.checkArgument(claims.getIssuer().equals(ISSUER_URL));

        // claim must have issued at time in the past
        Date currentTime = Date.from(Instant.now(clock));
        Preconditions.checkArgument(claims.getIssueTime().before(currentTime));
        // claim must have expiration time in the future
        Preconditions.checkArgument(claims.getExpirationTime().after(currentTime));

        // must have subject, email
        Preconditions.checkNotNull(claims.getSubject());
        Preconditions.checkNotNull(((Map) claims.getClaim("userClaims")).get("email"));

        JWK jwk = JWK.parse(jwkString);
        Preconditions.checkNotNull(jwk);
        // confirm algorithm matches
        Preconditions.checkArgument(jwk.getAlgorithm().getName().equals(alg));

        // verify using public key : lookup with key id, algorithm name provided
        OctetKeyPair publicJWK = OctetKeyPair.parse(jwk.toJSONString()).toPublicJWK();

        Preconditions.checkNotNull(publicJWK);
        JWSVerifier jwsVerifier = new Ed25519Verifier(publicJWK);
        return signedJwt.verify(jwsVerifier);

    }
}
