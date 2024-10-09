package com.exampleJWT.demoJWT.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JwtResponse {
    private String jwtToken;
    private String refreshToken;  // Thêm refreshToken
    private String username;
}