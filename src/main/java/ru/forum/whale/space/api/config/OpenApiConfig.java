package ru.forum.whale.space.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    @Bean
    public OpenAPI defineOpenAPI() {
        Info info = new Info()
                .title("API для социальной сети WhaleSpace")
                .version("1.0");

        return new OpenAPI().info(info);
    }
}
