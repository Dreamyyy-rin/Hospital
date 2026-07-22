package com.api.user.config;

import com.api.user.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private JwtFilter jwtFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth ->
        auth
          // Auth endpoints - public (both /api/auth and /users/api/auth)
          .requestMatchers("/api/auth/**", "/users/api/auth/**")
          .permitAll()
          // User GET endpoints - authenticated
          .requestMatchers(HttpMethod.GET, "/users/{id}")
          .authenticated()
          .requestMatchers(HttpMethod.GET, "/users/email/{email}")
          .authenticated()
          .requestMatchers(HttpMethod.GET, "/users/role/{role}")
          .authenticated()
          .requestMatchers(HttpMethod.GET, "/users")
          .authenticated()
          // User endpoints - ADMIN only
          .requestMatchers("/users/**")
          .hasRole("ADMIN")
          // Swagger / OpenAPI endpoints - public
          .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger/**")
          .permitAll()
          // Actuator endpoints
          .requestMatchers("/actuator/**")
          .permitAll()
          .anyRequest()
          .authenticated()
      )

      .sessionManagement(sess ->
        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      );

    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
    AuthenticationConfiguration config
  ) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
