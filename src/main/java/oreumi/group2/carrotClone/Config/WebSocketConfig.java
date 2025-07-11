package oreumi.group2.carrotClone.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*
    * STOMP 엔도포인트 등록
    * 클라이언트가 해당 URL 로 WebSocket 연결
    */

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                // 클라이언트 SockJS or WebSocket 연결할 엔드 포인트
                .addEndpoint("/ws-chat")
                //모든 출처 (도메인)에서 허용
                .setAllowedOriginPatterns("*")
                // SockJS fallback (WebSocket 을 지원하지 않는 곳을 위해)
                .withSockJS();
    }
    
    /*
    * 메세지 브로커 설정
    * 서버 - 클라이언트 간 메세지 라우팅 규칙을 정의함
    * */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
            // 브로커가 직업 메세지를 중계할 대상 topic -> 1 : N 방식
            .enableSimpleBroker("/topic");
        registry
            // 클라이언트가 @messageMapping 메서드로 보낼 url
            .setApplicationDestinationPrefixes("/app");
        registry
            // 1 : 1 메세지 전송시 사용할 url
            .setUserDestinationPrefix("/user");
    }
}