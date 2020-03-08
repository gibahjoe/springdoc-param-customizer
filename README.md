# Introduction

This is a simple library that is an extension of the [Springdoc-Openapi library](https://springdoc.org/)

This library adds support of the [QueryDsl](http://www.querydsl.com/) _@QuerydslPredicate_ annotation to [Springdoc-Openapi library](https://springdoc.org/)

It is also very customizable and supports any annotation (ships with @QuerydslPredicate annotation support but more coming later)

[license](https://github.com/gibahjoe/springdoc-param-customizer/blob/master/LICENSE).

## Usage

Create a bean of type AnnotatedParameterCustomizer and add your parameter customizer. See example below using querydsl customizer

```java

    import com.devappliance.springdocapc.AnnotatedParameterCustomizer;
    import com.devappliance.springdocapc.customizerImpl.DefaultQuerydslPredicateCustomizer;

    @Configuration
    public class WebConfiguration implements WebMvcConfigurer {
    //some code
    
    @Bean
    public AnnotatedParameterCustomizer customParamCustomiser(QuerydslBindingsFactory querydslBindingsFactory) {
        return new AnnotatedParameterCustomizer()
                .addCustomizer(new DefaultQuerydslPredicateCustomizer(querydslBindingsFactory));
    }

    //other code    
    }

```

The above example uses the inbuilt _DefaultQuerydslPredicateCustomizer_ which displays QueryDslPredicate parameters properly in OpenApi document

## Features and bugs

Please file feature requests and bugs at the [issue tracker][tracker].

[tracker]: https://github.com/gibahjoe/springdoc-param-customizer/issues
