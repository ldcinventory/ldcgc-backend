package org.ldcgc.backend.base.mock;

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
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.platform.commons.util.StringUtils;
import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.db.model.users.User;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.ldcgc.backend.util.conversion.Convert.dateToLocalDateTime;

public class MockedToken {

    private static final String ISSUER_URL = "https://gc8inventory.es";
    @Setter private static Boolean isRecoveryToken = false;
    @Setter private static Boolean isRefreshToken = false;

    public static Token generateNewToken(User user, boolean isRecoveryToken, boolean isRefreshToken) throws ParseException, JOSEException {
        OctetKeyPair jwk = generateJWK();

        return Token.builder()
            .jwtID(jwk.getKeyID())
            .jwk(Base64.encode(jwk.toJSONString().getBytes()).toString())
            .userId(user.getId())
            .role(user.getRole())
            .issuedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(1))
            .isRecoveryToken(isRecoveryToken)
            .isRefreshToken(isRefreshToken)
            .refreshTokenId(isRefreshToken
                ? null
                : Integer.valueOf(RandomStringUtils.randomNumeric(3)))
            .signedJWT(isRefreshToken
                ? generateSignedRefreshToken(user)
                : generateSignedToken(user))
            .build();
    }

    public static SignedJWT generateSignedToken(User user) throws ParseException, JOSEException {
        OctetKeyPair jwk = generateJWK();
        OctetKeyPair publicJWK = jwk.toPublicJWK();

        // Create the EdDSA signer
        JWSSigner signer = new Ed25519Signer(jwk);

        Map<String, String> claims = new HashMap<>()
        {{
            put("email", user.getEmail());
            put("role", user.getRole().getRoleName());
            if(isRefreshToken) put("refresh-token", "true");
            if(isRecoveryToken) put("recovery-token", "true");
        }};

        Date now = new Date();
        // expiration time is set by parameter (default: 24 hours -> 86400 seconds)
        Date expirationTime = new Date(now.toInstant().plusSeconds(86400).toEpochMilli());

        if(isRefreshToken)
            expirationTime = new Date(expirationTime.toInstant().plus(30, ChronoUnit.DAYS).toEpochMilli());

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

        Preconditions.checkArgument(signedJWT.verify(verifier));
        Preconditions.checkArgument(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
        Preconditions.checkArgument(signedJWT.getJWTClaimsSet().getIssuer().equals(ISSUER_URL));

        return signedJWT;

    }

    private static OctetKeyPair generateJWK() throws JOSEException {
        // Generate a key pair with Ed25519 curve
        return new OctetKeyPairGenerator(Curve.Ed25519)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .algorithm(JWSAlgorithm.EdDSA)
            .generate();
    }

    public static String generateSignedStringToken(User user) {
        try {
            return generateSignedToken(user).getParsedString();
        } catch (ParseException | JOSEException e) {
            return null;
        }
    }

    public static SignedJWT generateSignedRefreshToken(User user) throws ParseException, JOSEException {
        setIsRefreshToken(true);
        return generateSignedToken(user);
    }

    public static String getHeaderFromToken(SignedJWT signedJWT) {
        return signedJWT.getParsedParts()[0].toString();
    }

    public static String getPayloadFromToken(SignedJWT signedJWT) {
        return signedJWT.getParsedParts()[1].toString();
    }

    public static String getSignatureFromToken(SignedJWT signedJWT) {
        return signedJWT.getParsedParts()[2].toString();
    }

    public static String getHeaderPayloadFromToken(SignedJWT signedJWT) {
        return String.format("%s.%s", getHeaderFromToken(signedJWT), getPayloadFromToken(signedJWT));
    }

}
