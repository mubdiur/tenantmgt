package io.github.tenantmgt.model;

import lombok.Data;

@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken; 
}
