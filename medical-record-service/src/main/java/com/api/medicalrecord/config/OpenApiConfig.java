package com.api.medicalrecord.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(
        new Info()
          .title("Medical Record Service API")
          .description(
            "API documentation for Medical Record Service - manages patient medical records and history"
          )
          .version("1.0.0")
          .contact(
            new Contact()
              .name("Hospital Management Team")
              .email("support@hospital.com")
          )
          .license(
            new License()
              .name("Apache 2.0")
              .url("https://www.apache.org/licenses/LICENSE-2.0")
          )
      )
      .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
      .components(new Components().addSecuritySchemes("Bearer Authentication", new SecurityScheme()
          .name("Bearer Authentication")
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")))
      .servers(
        List.of(
          new Server()
            .url("http://localhost:8080")
            .description("Gateway (Local)"),
          new Server()
            .url("http://gateway:8080")
            .description("Gateway (Docker)"),
          new Server()
            .url("http://localhost:8085")
            .description("Medical Record Service (Local)"),
          new Server()
            .url("http://medical-record-service:8085")
            .description("Medical Record Service (Docker)")
        )
      );
  }
}
