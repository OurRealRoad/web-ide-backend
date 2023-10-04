package com.jinro.webide.login.config;

import com.jinro.webide.login.filter.CsrfCookieFilter;
import com.jinro.webide.login.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http.securityContext(context -> context.requireExplicitSave(false));

        http.cors(cors -> cors.configurationSource(
                new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000/"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }));

        http.csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers(
                        new AntPathRequestMatcher("/api/v1/auth/authenticate"),
                        new AntPathRequestMatcher("/api/v1/auth/register"),
                        new AntPathRequestMatcher("/api/v1/auth/csrf"),
                        new AntPathRequestMatcher("/api/v1/auth/update-password"),
                new AntPathRequestMatcher("/api/v1/auth/reset-password")
                )
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository()));
        http.addFilterBefore(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(request ->
                request
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
        );
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private CsrfTokenRepository csrfTokenRepository(){
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("CSRF-TOKEN");
        return repository;
    }

}
