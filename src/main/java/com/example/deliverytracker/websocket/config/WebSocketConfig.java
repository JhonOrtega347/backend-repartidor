package com.example.deliverytracker.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
    
    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String userId = "anon"; // Valor por defecto
                URI uri = request.getURI();
                String query = uri.getQuery();

                if (query != null) {
                    for (String param : query.split("&")) {
                        String[] parts = param.split("=");
                        if (parts.length == 2 && parts[0].equals("userId")) {
                            userId = parts[1];
                            break;
                        }
                    }
                }

                final String finalUserId = userId; // ✅ Aquí se vuelve "efectivamente final"
                log.info("🔗 Nueva conexión WebSocket para usuario: {}", finalUserId);
                return () -> finalUserId;
            }
        };
    }
    
    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, org.springframework.http.server.ServerHttpResponse response, 
                                         WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                log.info("🤝 Iniciando handshake WebSocket para: {}", request.getURI());
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, org.springframework.http.server.ServerHttpResponse response, 
                                     WebSocketHandler wsHandler, Exception exception) {
                if (exception != null) {
                    log.error("❌ Error en handshake WebSocket: {}", exception.getMessage());
                } else {
                    log.info("✅ Handshake WebSocket completado exitosamente");
                }
            }
        };
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-location")
                .setAllowedOriginPatterns("*") // CAMBIO AQUÍ
                .setHandshakeHandler(handshakeHandler())
                .addInterceptors(handshakeInterceptor())
                .withSockJS();
    }
}
