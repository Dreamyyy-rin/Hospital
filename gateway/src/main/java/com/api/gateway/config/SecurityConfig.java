package com.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .csrf(csrf -> csrf.disable())
      .authorizeExchange(exchanges -> exchanges
        .pathMatchers("/actuator/**").permitAll()
        .pathMatchers("/swagger-ui/**").permitAll()
        .pathMatchers("/swagger-ui.html").permitAll()
        .pathMatchers("/v3/api-docs/**").permitAll()
        .pathMatchers("/webjars/**").permitAll()
        .pathMatchers("/users/**").permitAll()
        .pathMatchers("/doctors/**").permitAll()
        .pathMatchers("/appointments/**").permitAll()
        .pathMatchers("/schedules/**").permitAll()
        .pathMatchers("/payments/**").permitAll()
        .pathMatchers("/medical-records/**").permitAll()
        .pathMatchers("/api/auth/**").permitAll()
        .anyExchange().permitAll()
      )
      .build();
  }
}
