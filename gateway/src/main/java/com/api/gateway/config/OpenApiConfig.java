package com.api.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hospital Management API Gateway")
                        .description("""
                            Aggregated API documentation for Hospital Management microservices.

                            ## How to Use
                            1. Select a service from the top dropdown (e.g., user-service, doctor-service)
                            2. Use the "Server" dropdown to choose your preferred endpoint:
                               - **Gateway (Local/Docker)**: Route through the API Gateway
                               - **Specific Service**: Call the service directly
                            3. Click "Try it out" to test endpoints

                            ## Services
                            - User Service (8081)
                            - Doctor Service (8082)
                            - Appointment Service (8083)
                            - Schedule Service (8084)
                            - Medical Record Service (8085)
                            - Payment Service (8086)
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hospital Management Team")
                                .email("support@hospital.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                        .name("Bearer Authentication")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .servers(List.of(
                        // Gateway Servers
                        new Server()
                                .url("http://localhost:8080")
                                .description("Gateway (Local)"),
                        new Server()
                                .url("http://gateway:8080")
                                .description("Gateway (Docker)"),
                        // User Service
                        new Server()
                                .url("http://localhost:8081")
                                .description("User Service (Local)"),
                        new Server()
                                .url("http://user-service:8081")
                                .description("User Service (Docker)"),
                        // Doctor Service
                        new Server()
                                .url("http://localhost:8082")
                                .description("Doctor Service (Local)"),
                        new Server()
                                .url("http://doctor-service:8082")
                                .description("Doctor Service (Docker)"),
                        // Appointment Service
                        new Server()
                                .url("http://localhost:8083")
                                .description("Appointment Service (Local)"),
                        new Server()
                                .url("http://appointment-service:8083")
                                .description("Appointment Service (Docker)"),
                        // Schedule Service
                        new Server()
                                .url("http://localhost:8084")
                                .description("Schedule Service (Local)"),
                        new Server()
                                .url("http://schedule-service:8084")
                                .description("Schedule Service (Docker)"),
                        // Medical Record Service
                        new Server()
                                .url("http://localhost:8085")
                                .description("Medical Record Service (Local)"),
                        new Server()
                                .url("http://medical-record-service:8085")
                                .description("Medical Record Service (Docker)"),
                        // Payment Service
                        new Server()
                                .url("http://localhost:8086")
                                .description("Payment Service (Local)"),
                        new Server()
                                .url("http://payment-service:8086")
                                .description("Payment Service (Docker)")
                ));
    }
}
