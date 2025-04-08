//package lk.ijse.back_end.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
//import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
//import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketSecurity
//@EnableWebSocketMessageBroker
//public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Bean
//    public CsrfTokenRepository csrfTokenRepository() {
//        return new HttpSessionCsrfTokenRepository();
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new CsrfChannelInterceptor(csrfTokenRepository()));
//    }
//
//    @Bean
//    public SecurityWebSocketMessageBrokerConfigurer securityWebSocketMessageBrokerConfigurer() {
//        return new SecurityWebSocketMessageBrokerConfigurer() {
//            @Override
//            protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//                messages
//                        .simpDestMatchers("/app/**").authenticated()
//                        .simpSubscribeDestMatchers("/user/**", "/topic/**").authenticated()
//                        .anyMessage().authenticated();
//            }
//        };
//    }
//}