package io.github.tenantmgt.security.auth;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;


@Service
public class RsaKeyProvider {
    private static RsaJsonWebKey rsaJsonWebKey = null;

    public static RsaJsonWebKey getInstance() throws JoseException {
        if(rsaJsonWebKey == null) {
            rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
        }

        return rsaJsonWebKey;
    }
}
