package com.jinro.webide.config;

import com.jinro.webide.controller.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private final WebSocketHandler webSocketHandler;

    // Websocket 핸들러 등록
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/api/v1/terminal")
                .setAllowedOrigins("*");
    }

    // Stomp 관련 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메세지 받을 경로
        // 메세지 받는곳
        registry.enableSimpleBroker("/sub");
        // 메세지 보내는 곳
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // 웹소켓 연결을 위한STOMP 엔드포인트 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS(); // SocketJS를 연결한다는 설정
    }
}





