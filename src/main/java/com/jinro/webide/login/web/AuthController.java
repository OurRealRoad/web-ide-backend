package com.jinro.webide.login.web;

import com.jinro.webide.login.service.AuthenticationService;
import com.jinro.webide.login.web.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate (@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/email-check")
    public ResponseEntity<BasicResponse> emailCheck(@RequestParam String email){
        return ResponseEntity.ok(authenticationService.emailCheck(email));
    }

    @GetMapping("/csrf")
    public ResponseEntity<String> getCsrfToken(HttpServletRequest request, CsrfToken token){
        if(request.getHeader("X-XSRF-TOKEN") == null) {
            return ResponseEntity.ok(token.getToken());
        } else {
            return ResponseEntity.ok("Not Accepted");
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<AuthenticationResponse> updatePassword(@RequestBody UpdatePasswordRequest request){
        return ResponseEntity.ok(authenticationService.passwordUpdate(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthenticationResponse> updatePassword(@RequestBody ResetPasswordRequest request){
        return ResponseEntity.ok(authenticationService.passwordReset(request));
    }
}
