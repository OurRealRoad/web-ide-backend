package com.jinro.webide.login.service;

import com.jinro.webide.login.domian.Member;
import com.jinro.webide.login.domian.MemberRepository;
import com.jinro.webide.login.domian.Role;
import com.jinro.webide.login.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = Member.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .picture(request.getPicture())
                .createdDate(String.valueOf(LocalDateTime.now()))
                .role(Role.USER)
                .build();
        memberRepository.save(user);
        return AuthenticationResponse.builder()
                .isAccepted(true)
                .token("Not Authenticated")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e){
            return AuthenticationResponse.builder()
                    .isAccepted(false)
                    .token("Not authenticated")
                    .build();
        }
        var user = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .isAccepted(true)
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse passwordUpdate(UpdatePasswordRequest request){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e){
            return AuthenticationResponse.builder()
                    .isAccepted(false)
                    .token("Not authenticated").build();
        }
       var user = memberRepository.findByEmail(request.getEmail())
               .orElseThrow();
       if(user == null) {
           return AuthenticationResponse.builder()
                   .isAccepted(false)
                   .token("Not authenticated").build();
       }
       user.setPassword(passwordEncoder.encode(request.getNewpassword()));
        memberRepository.save(user);
       return AuthenticationResponse.builder()
               .isAccepted(true)
               .token("Not authenticated")
               .build();
    }
}
