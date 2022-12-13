package com.jimmodel.internalServices.dto.Response;

import com.jimmodel.internalServices.model.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JwtTokenResponse {
    private UUID userId;
    private String accessToken;
    private Date accessTokenExpiration;

    public JwtTokenResponse(JwtToken jwtToken){
        this.accessToken = jwtToken.getAccessToken();
        this.accessTokenExpiration = jwtToken.getAccessTokenExpiration();
        this.userId = jwtToken.getUserId();
    }
}
