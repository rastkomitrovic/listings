package com.listings.listings.confg;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableElasticsearchRepositories
@EnableKafka
@EnableCaching
public class ListingsServiceConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return objectMapper;
    }

    @Bean
    public NewTopic unfilteredDataTopic() {
        return new NewTopic("listings", 1, (short) 1);
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("Listings Open API documentation")
                        .version("3.0.0")
                        .description("Open API documentation")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
