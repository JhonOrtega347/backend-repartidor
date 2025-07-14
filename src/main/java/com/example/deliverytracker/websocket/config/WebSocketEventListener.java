package com.example.deliverytracker.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("🔗 Cliente conectado: {}", event.getUser() != null ? event.getUser().getName() : "anon");
        log.info("🔗 Detalles de conexión: {}", event.getMessage());
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("🔌 Cliente desconectado: {}", event.getUser() != null ? event.getUser().getName() : "anon");
        log.info("🔌 Detalles de desconexión: {}", event.getMessage());
        log.info("🔌 Session ID: {}", event.getSessionId());
    }
    
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        log.info("📡 Cliente suscrito: {} a {}", 
                event.getUser() != null ? event.getUser().getName() : "anon",
                event.getMessage().getHeaders().get("simpDestination"));
    }
    
    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        log.info("📡 Cliente desuscrito: {}", event.getUser() != null ? event.getUser().getName() : "anon");
    }
} 