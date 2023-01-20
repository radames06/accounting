package com.jd.accounting.security;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private final String username;
    private final String expiresIn;
    public JwtResponse(String jwttoken, String username, String expiresIn) {

        this.jwttoken = jwttoken;
        this.username = username;
        this.expiresIn = expiresIn;
    }
    public String getToken() {
        return this.jwttoken;
    }
    public String getUsername() { return this.username; }
    public String getExpiresIn() { return this.expiresIn; }
}
