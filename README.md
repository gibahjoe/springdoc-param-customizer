
[![](https://jitpack.io/v/gibahjoe/springdoc-param-customizer.svg)](https://jitpack.io/#gibahjoe/springdoc-param-customizer)

# Introduction

This is a simple library that is an extension of the [Springdoc-Openapi library](https://springdoc.org/)

This library adds support of the [QueryDsl](http://www.querydsl.com/) _@QuerydslPredicate_ annotation to [Springdoc-Openapi library](https://springdoc.org/)

It is also very customizable and supports any annotation (ships with @QuerydslPredicate annotation support but more coming later)

[license](https://github.com/gibahjoe/springdoc-param-customizer/blob/master/LICENSE).

## Installation

Add to your build.gradle

```groovy
allprojects {
    repositories {
	    maven { url 'https://jitpack.io' }
	}
}
```

```groovy
dependencies {
    implementation 'com.github.gibahjoe:springdoc-param-customizer:[version]'
}
```

## Usage

Create a bean of type AnnotatedParameterCustomizer and add your parameter customizer. See example below using querydsl customizer

```java

import com.devappliance.springdocparamcustomizer.AnnotatedParameterCustomizer;
import com.devappliance.springdocparamcustomizer.customizerImpl.DefaultQuerydslPredicateCustomizer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    //some config
    
    @Bean
   public AnnotatedParameterCustomizer annotatedParameterCustomizer(Optional<OpenAPI> openAPI, ObjectProvider<EntityPathResolver> resolver) {
       return new AnnotatedParameterCustomizer(openAPI)
                   .addCustomizer(new DefaultQuerydslPredicateCustomizer(new QuerydslBindingsFactory(resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE))));
   }

    //Ensure to include a bean of your open api too in your config
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI();
    }

    //other config    
}

```

The above example uses the inbuilt _DefaultQuerydslPredicateCustomizer_ which displays QueryDslPredicate parameters properly in OpenApi document

## Features and bugs

Please file feature requests and bugs at the [issue tracker][tracker].

[tracker]: https://github.com/gibahjoe/springdoc-param-customizer/issues
