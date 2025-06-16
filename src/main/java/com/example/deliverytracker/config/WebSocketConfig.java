package com.example.deliverytracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Habilita el manejo de mensajes de WebSocket a través de un MessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple basado en memoria para prefijos de destino.
        // Los clientes pueden suscribirse a destinos que comiencen con "/topic" o "/queue".
        config.enableSimpleBroker("/topic", "/queue");

        // Define el prefijo para los destinos de la aplicación.
        // Los mensajes que el cliente envía al servidor deben ir precedidos de este prefijo.
        // Por ejemplo, un cliente enviaría un mensaje a "/app/update-location".
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra el endpoint donde los clientes de WebSocket se conectarán.
        // Permite a todos los orígenes (importante para React Native en desarrollo, CORS).
        // Si usas ngrok, tu URL de frontend de Expo Go, etc., asegúrate de que sea compatible aquí.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        // .withSockJS() es un fallback para navegadores que no soportan WebSockets
        // para React Native, a menudo no es estrictamente necesario, pero no estorba.
    }
}