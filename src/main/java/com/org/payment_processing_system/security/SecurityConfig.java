package com.org.payment_processing_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.cors(AbstractHttpConfigurer::disable);
        security.csrf(AbstractHttpConfigurer::disable);
        security.authorizeHttpRequests(request->
                request.requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().permitAll()
                );
        security.oauth2ResourceServer(authServer->authServer.jwt(Customizer.withDefaults()));
        return security.build();
    }
}
