package com.devappliance.springdocparamcustomixer.operationcustomizer;

import com.devappliance.springdocparamcustomixer.BaseTest;
import com.devappliance.springdocparamcustomizer.QuerydslPredicateOperationCustomizer;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.springdoc.core.Constants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public void testThatQdslPredicateFieldsShowUpAsQueryParamInDoc() throws Exception {
        mockMvc.perform(get(Constants.DEFAULT_API_DOCS_URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi", is("3.0.1")))
                .andExpect(jsonPath("$.paths./test.get.parameters[2].name")
                        .value("name"))
                .andExpect(jsonPath("$.paths./test.get.parameters[3].name")
                        .value("notCode"))
                .andExpect(jsonPath("$.paths./test.get.parameters[4].name")
                        .value("status"))
                .andExpect(jsonPath("$.paths./test.get.parameters[4].schema.enum").isArray());
    }

    public static enum Status {
        ACTIVE, INACTIVE
    }

    @SpringBootApplication
    static class SpringDocTestApp {
        @Bean
        public GreetingController greetingController() {
            return new GreetingController();
        }

        @Bean
        public QuerydslPredicateOperationCustomizer querydslPredicateOperationCustomizer(QuerydslBindingsFactory querydslBindingsFactory) {
            return new QuerydslPredicateOperationCustomizer(querydslBindingsFactory);
        }


    }

    @RestController
    public static class GreetingController {

        @GetMapping("/test")
        public ResponseEntity<?> sayHello2(@QuerydslPredicate(bindings = DummyEntityPredicate.class, root = DummyEntity.class) Predicate predicate,
                                           @RequestParam List<Status> statuses) {
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
