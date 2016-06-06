package com.couchpod.api.users;

public class TokenDTO {
    public String token; // Signed Json Web Token
    public int expiry; // Expiration date as a unix timestamp in seconds
}
