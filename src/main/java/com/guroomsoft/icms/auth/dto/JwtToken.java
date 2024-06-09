package com.guroomsoft.icms.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken {
    private String Authentication;  // Access token
    private String RefreshToken;    // Refresh token
}
