package org.ldcgc.backend.security.jwt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.exception.ApiError;
import org.ldcgc.backend.exception.ApiSubError;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.TokenDto;
import org.ldcgc.backend.payload.mapper.users.TokenMapper;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.ldcgc.backend.util.conversion.Convert.convertDateToLocalDateTime;
import static org.ldcgc.backend.util.process.Threads.runInBackground;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwtExpirationMs}")
    private int jwtExpirationSeconds;

    @Setter private Boolean isRecoveryToken = false;
    @Setter private Boolean isRefreshToken = false;

    private final TokenRepository tokenRepository;
    private static final BidiMap<Integer, TokenDto> tokenLocalRepository = new DualHashBidiMap<>();
    private static final BidiMap<Integer, TokenDto> refreshTokenLocalRepository = new DualHashBidiMap<>();

    private static final Clock clock = Clock.systemUTC();

    private static final String ISSUER_URL = "https://gc8inventory.es";

    public synchronized SignedJWT generateNewToken(User user) throws ParseException, JOSEException {
        runInBackground(() -> tokenRepository.deleteAllExpiredTokensFromUser(user.getId()));

        // Generate a key pair with Ed25519 curve
        OctetKeyPair jwk = new OctetKeyPairGenerator(Curve.Ed25519)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .algorithm(JWSAlgorithm.EdDSA)
            .generate();
        OctetKeyPair publicJWK = jwk.toPublicJWK();

        // Create the EdDSA signer
        JWSSigner signer = new Ed25519Signer(jwk);

        Map<String, String> claims = new HashMap<>()
        {{
            put("email", user.getEmail());
            put("role", user.getRole().getRoleName());
            if(isRefreshToken)
                put("refresh-token", "true");
            if(isRecoveryToken)
                put("recovery-token", "true");
        }};

        Date now = new Date();
        // expiration time is set by parameter (default: 24 hours -> 86400 seconds)
        Date expirationTime = new Date(now.toInstant().plusSeconds(jwtExpirationSeconds).toEpochMilli());

        if(isRefreshToken) {
            expirationTime = new Date(expirationTime.toInstant().plus(30, ChronoUnit.DAYS).toEpochMilli());
            runInBackground(() -> tokenRepository.deleteAllTokensFromUser(user.getId()));
        }

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .jwtID(jwk.getKeyID())
            .issuer(ISSUER_URL)
            .issueTime(now)
            .subject(user.getId().toString())
            .claim("userClaims", ImmutableMap.copyOf(claims))
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
            .role(user.getRole())
            .issuedAt(convertDateToLocalDateTime(now))
            .expiresAt(convertDateToLocalDateTime(expirationTime))
            .isRecoveryToken(isRecoveryToken)
            .isRefreshToken(isRefreshToken)
            .refreshTokenId(isRefreshToken ? null : refreshTokenLocalRepository.get(user.getId()).getId())
            .signedJWT(signedJWT)
            .build();
        token = tokenRepository.saveAndFlush(token);

        // clean old tokens for this user
        cleanLocalTokensFromUserId(user.getId(), isRefreshToken);

        TokenDto tokenDto = TokenMapper.MAPPER.toDto(token);
        if(!isRefreshToken)
            tokenLocalRepository.put(user.getId(), tokenDto);
        else
            refreshTokenLocalRepository.put(user.getId(), tokenDto);

        // set again to false to future tokens since JwtUtils is a Component
        setIsRecoveryToken(false);
        setIsRefreshToken(false);

        Preconditions.checkArgument(signedJWT.verify(verifier));
        Preconditions.checkArgument(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
        Preconditions.checkArgument(signedJWT.getJWTClaimsSet().getIssuer().equals(ISSUER_URL));

        return signedJWT;

    }

    public SignedJWT generateNewRecoveryToken(User user) throws ParseException, JOSEException {
        setIsRecoveryToken(true);
        return generateNewToken(user);
    }

    public SignedJWT generateRefreshToken(User user) throws ParseException, JOSEException {
        setIsRefreshToken(true);
        return generateNewToken(user);
    }

    public String getEmailFromJwtToken(SignedJWT signedJWT) throws ParseException {
        return ((Map) signedJWT.getJWTClaimsSet().getClaim("userClaims")).get("email").toString();
    }

    public Integer getUserIdFromJwtToken(SignedJWT signedJWT) throws ParseException {
        return Integer.valueOf(signedJWT.getJWTClaimsSet().getSubject());
    }

    public Integer getUserIdFromStringToken(String token) throws ParseException {
        return Integer.valueOf(getDecodedJwt(token).getJWTClaimsSet().getSubject());
    }

    public SignedJWT getDecodedJwt(String jwt) {
        try {
            if(jwt.matches("^Bearer .*"))
                return SignedJWT.parse(jwt.split("Bearer ")[1]);
            return SignedJWT.parse(jwt);
        } catch (ParseException e) {
            ApiError apiError = ApiError.builder()
                .error(ApiSubError.builder()
                    .field("jwt")
                    .message(e.getLocalizedMessage())
                    .build())
                .build();
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_NOT_PARSEABLE, apiError);
        }
    }

    public synchronized boolean verifyJwt(SignedJWT signedJwt, String expectedAudience) throws ParseException, JOSEException, IllegalArgumentException, NullPointerException {

        int userId = getUserIdFromJwtToken(signedJwt);

        if(tokenLocalRepository.get(userId) == null && refreshTokenLocalRepository.get(userId) == null)
            Preconditions.checkArgument(tokenRepository.findByJwtID(signedJwt.getHeader().getKeyID()).isPresent());

        // parse signed token into header / claims
        JWSHeader jwsHeader = signedJwt.getHeader();

        // must exist and match the algorithm
        String kid = jwsHeader.getKeyID();
        String alg = jwsHeader.getAlgorithm().getName();

        TokenDto token;
        if(!CollectionUtils.isEmpty(tokenLocalRepository) && tokenLocalRepository.get(userId).getSignedJWT().equals(signedJwt))
            token = tokenLocalRepository.get(userId);
        else if(!CollectionUtils.isEmpty(refreshTokenLocalRepository) && refreshTokenLocalRepository.get(userId).getSignedJWT().equals(signedJwt))
            token = refreshTokenLocalRepository.get(userId);
        else {
            token = TokenMapper.MAPPER.toDto(tokenRepository.findByJwtID(kid).orElseThrow(
                () -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_NOT_FOUND))).toBuilder()
                .signedJWT(signedJwt).build();
            if(token.isRefreshToken())
                refreshTokenLocalRepository.put(token.getUserId(), token);
            else
                tokenLocalRepository.put(token.getUserId(), token);
        }

        String jwkString = new String(Base64.from(token.getJwk()).decode());

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
        Preconditions.checkArgument(!token.isExpired());
        JWSVerifier jwsVerifier = new Ed25519Verifier(publicJWK);
        return signedJwt.verify(jwsVerifier);

    }

    public static void cleanLocalTokensFromUserId(@NotNull Integer userId, boolean cleanRecoveryToken) {
        tokenLocalRepository.remove(userId);
        if(cleanRecoveryToken)
            refreshTokenLocalRepository.remove(userId);
    }

    public static TokenDto getBySignedJwtFromLocal(SignedJWT signedJWT, boolean isRecoveryToken) throws ParseException {
        Integer userId = Integer.valueOf(signedJWT.getJWTClaimsSet().getSubject());

        if(tokenLocalRepository.get(userId) != null && !isRecoveryToken)
            return tokenLocalRepository.get(userId);

        if(refreshTokenLocalRepository.get(userId) != null && isRecoveryToken)
            return refreshTokenLocalRepository.get(userId);

        return null;
    }

}
