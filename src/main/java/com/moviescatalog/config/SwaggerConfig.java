package com.moviescatalog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                       .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                       .components(new Components()
                                           .addSecuritySchemes("bearerAuth",
                                                               new SecurityScheme()
                                                                       .type(SecurityScheme.Type.HTTP)
                                                                       .scheme("bearer")
                                                                       .bearerFormat("JWT")
                                                                       .in(SecurityScheme.In.HEADER)
                                                                       .name("Authorization")))
                       .info(new Info()
                                     .title("Movies Catalog API")
                                     .version("1.0")
                                     .description("API to manage and retrieve movies.")
                                     .contact(new Contact()
                                                      .name("Thallita Gyzik Teixeira")
                                                      .email("thagyzik@gmail.com")));
    }

}
