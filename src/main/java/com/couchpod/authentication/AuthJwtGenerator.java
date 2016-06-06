package com.couchpod.authentication;

import com.couchpod.api.users.TokenDTO;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

public class AuthJwtGenerator {
    private byte[] secret;

    public AuthJwtGenerator(byte[] secret) {
        this.secret = secret;
    }

    public TokenDTO generateValidToken(final long userId, final long maxAgeSeconds) {
        NumericDate now = NumericDate.now();
        now.addSeconds(maxAgeSeconds);

        final JwtClaims claims = new JwtClaims();
        claims.setSubject(userId + "");
        claims.setExpirationTime(now);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(new HmacKey(secret));

        TokenDTO token = new TokenDTO();
        token.expiry = (int) now.getValue();

        try {
            token.token = jws.getCompactSerialization();
        } catch (JoseException e) {
            e.printStackTrace();
            return null;
        }

        return token;
    }
}
