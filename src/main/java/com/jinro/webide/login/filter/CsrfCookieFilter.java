package com.jinro.webide.login.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute((CsrfToken.class.getName()));
        System.out.println("CSRF Header Name :"+csrfToken.getHeaderName());
        System.out.println("CSRF : "+csrfToken.getToken());
        if(null != csrfToken.getHeaderName()){
            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }
//        if(null != csrfToken.getHeaderName()){
//            response.setHeader("CSRF-TOKEN", csrfToken.getToken());
//        }
        filterChain.doFilter(request, response);
    }
}
