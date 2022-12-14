package com.jimmodel.services.controller;

import com.jimmodel.services.dto.Request.RefreshRequest;
import com.jimmodel.services.dto.Request.SignInRequest;
import com.jimmodel.services.dto.Response.JwtTokenResponse;
import com.jimmodel.services.domain.JwtToken;
import com.jimmodel.services.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/${api-version}/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/sign-in")
    public ResponseEntity<JwtTokenResponse> signIn(@RequestBody SignInRequest signInRequest){
        JwtToken jwtTokens = authenticationService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        JwtTokenResponse responseBody = new JwtTokenResponse(jwtTokens);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<JwtTokenResponse> getAccessToken(@RequestBody RefreshRequest refreshRequest){
        JwtToken jwtTokens = authenticationService.refresh(refreshRequest.getToken());
        JwtTokenResponse responseBody = new JwtTokenResponse(jwtTokens);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping(value = "/sign-out")
    public ResponseEntity signOut(){
        authenticationService.signOut();
        return new ResponseEntity(HttpStatus.OK);
    }
}
