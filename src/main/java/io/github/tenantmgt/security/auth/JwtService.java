package io.github.tenantmgt.security.auth;

import java.util.Arrays;
import java.util.List;

import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    public static final Integer EXPIRATION_TIME_DAYS = 30;

    private JwtClaims getClaims(String username, String email, String role) {
        JwtClaims claims = new JwtClaims();
        // claims.setIssuer("Issuer"); // who creates the token and signs it
        // claims.setAudience("Audience"); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(EXPIRATION_TIME_DAYS * 24 * 60);
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow(); // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
        claims.setSubject(username); // the subject/principal is whom the token is about
        claims.setClaim("email", email); // additional claims/attributes about the subject can be added
        claims.setClaim("role", role); // additional claims/attributes about the subject can be added
        return claims;
    }

    public String generate(String username, String email, String role) throws JoseException {

        JwtClaims claims = getClaims(username, email, role);
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(RsaKeyProvider.getInstance().getPrivateKey());
        jws.setKeyIdHeaderValue(RsaKeyProvider.getInstance().getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        String jwt = jws.getCompactSerialization();
        return jwt;
    }

    public boolean isValid(String jwt) throws JoseException {

        boolean validity = false;

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for
                                                  // clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setVerificationKey(RsaKeyProvider.getInstance().getKey()) // verify the signature with the public key
                .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                        ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256) // which is only RS256 here
                .build(); // create the JwtConsumer instance

        try {
            // Validate the JWT and process it to the Claims
            jwtConsumer.processToClaims(jwt).getSubject();
            validity = true;
        } catch (InvalidJwtException e) {
            System.out.println("Invalid JWT! " + e);
        } catch (MalformedClaimException e) {
            System.out.println("Invalid JWT! " + e);
        }
        return validity;
    }
}
