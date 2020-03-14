package com.devappliance.springdocparamcustomizer.qdslpredicatecustomizer;

import com.devappliance.springdocparamcustomizer.AnnotatedParameterCustomizer;
import com.devappliance.springdocparamcustomizer.BaseTest;
import com.devappliance.springdocparamcustomizer.customizerImpl.DefaultQuerydslPredicateCustomizer;
import com.querydsl.core.types.Predicate;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springdoc.core.Constants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/

public class QdslPredicateCustomizerTest extends BaseTest {
    @Test
    public void testThatProperQdlPredicateTypeIsGenerated() throws Exception {
        mockMvc.perform(get(Constants.DEFAULT_API_DOCS_URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi", is("3.0.1")))
                .andExpect(jsonPath("$.paths./test.get.parameters[0].schema.$ref")
                        .value("#/components/schemas/DummyEntityPredicateG"))
                .andExpect(jsonPath("$.components.schemas.DummyEntityPredicateG.properties.notCode").exists());
    }

    @SpringBootApplication
    static class SpringDocTestApp {
        @Bean
        public GreetingController greetingController() {
            return new GreetingController();
        }

        @Bean
        public AnnotatedParameterCustomizer annotatedParameterCustomizer(Optional<OpenAPI> openAPI, ObjectProvider<EntityPathResolver> resolver) {
            return new AnnotatedParameterCustomizer(openAPI)
                    .addCustomizer(new DefaultQuerydslPredicateCustomizer(new QuerydslBindingsFactory(resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE))));
        }

        //Ensure to include a bean of your openapi too in your config
        @Bean
        public OpenAPI openApi() {
            return new OpenAPI();
        }
    }

    @RestController
    public static class GreetingController {

        @GetMapping("/test")
        public ResponseEntity<?> sayHello2(@QuerydslPredicate(bindings = DummyEntityPredicate.class, root = DummyEntity.class) Predicate predicate) {
            return ResponseEntity.ok().build();
        }

        @GetMapping("/test-multiple")
        public ResponseEntity<?> searchDummyEntity(@QuerydslPredicate(bindings = DummyEntityPredicate.class, root = DummyEntity.class) Predicate predicate) {
            return ResponseEntity.ok().build();
        }
    }

    public static class DummyEntityPredicate implements QuerydslBinderCustomizer<QDummyEntity> {

        @Override
        public void customize(QuerydslBindings querydslBindings, QDummyEntity qDummyEntity) {
            querydslBindings.bind(qDummyEntity.code).as("notCode").first((path, value) -> path.containsIgnoreCase(value));
        }
    }
}
