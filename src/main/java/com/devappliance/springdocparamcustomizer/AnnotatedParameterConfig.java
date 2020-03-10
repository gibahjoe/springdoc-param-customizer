package com.devappliance.springdocparamcustomizer;

import com.devappliance.springdocparamcustomizer.customizerImpl.DefaultQuerydslPredicateCustomizer;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;

import java.util.Optional;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/
@Configuration
public class AnnotatedParameterConfig {
    @Bean
    @ConditionalOnMissingBean
    public AnnotatedParameterCustomizer annotatedParameterCustomizer(Optional<OpenAPI> openAPI, ObjectProvider<EntityPathResolver> resolver) {
        return new AnnotatedParameterCustomizer(openAPI)
                .addCustomizer(new DefaultQuerydslPredicateCustomizer(new QuerydslBindingsFactory(resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE))));
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openAPI() {
        return new OpenAPI();
    }
}
