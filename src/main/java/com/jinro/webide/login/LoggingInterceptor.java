package com.jinro.webide.login.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("================ URL 요청 전 인터셉터 ==================");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("================ URL 요청 오류 인터셉터 ==================");
        int status = response.getStatus();
        System.out.println(response.getHeaderNames());
        System.out.println("Response Status: " + status);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        log.info("[aop-test] interceptor : ResponseInterceptor afterCompletion()");
        if (ex != null) {
            log.error("오류가 발생함");
            log.error("[aop-test] interceptor [EXCEPTION 발생] " + ex.getMessage());
        } else {
            log.error("Response 잘 보냄, 서버 문제 없음");
        }
    }
}
