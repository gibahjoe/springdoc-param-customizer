package com.devappliance.springdocparamcustomizer;

import com.devappliance.springdocparamcustomizer.customizer.AnnotationCustomizer;
import com.google.gson.reflect.TypeToken;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/
public class AnnotatedParameterCustomizer implements ParameterCustomizer {
    private List<AnnotationCustomizer<?>> registeredCustomizers = new ArrayList<>();
    private Optional<OpenAPI> openAPI;

    public AnnotatedParameterCustomizer(Optional<OpenAPI> openAPI) {
        this.openAPI = openAPI;
    }

    public static Parameter next(Parameter parameterModel, java.lang.reflect.Parameter parameter, AnnotatedParameterCustomizer context, HandlerMethod handlerMethod, Iterator<? extends AnnotationCustomizer<?>> annotationCustomizers) {
        if (annotationCustomizers.hasNext()) {
            AnnotationCustomizer annotationCustomizer = annotationCustomizers.next();
            return annotationCustomizer.customize(parameter.getAnnotation(annotationCustomizer.getType()), parameterModel, parameter, context, handlerMethod, annotationCustomizers);
        } else {
            return parameterModel;
        }
    }

    @Override
    public Parameter customize(Parameter parameterModel, java.lang.reflect.Parameter parameter, HandlerMethod handlerMethod) {
        List<? extends AnnotationCustomizer<?>> annotationCustomizers = registeredCustomizers.stream()
                .filter(annotationAnnotationCustomizer -> {
                    Class<?> aClass = annotationAnnotationCustomizer.getType();
                    Type t = TypeToken.get(aClass).getType();
                    for (Annotation annotation : parameter.getAnnotations()) {
                        if (annotation.annotationType() == aClass) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());

        return next(parameterModel, parameter, this, handlerMethod, annotationCustomizers.iterator());
    }

    public Optional<OpenAPI> getOpenAPI() {
        return openAPI;
    }

    public AnnotatedParameterCustomizer addCustomizer(AnnotationCustomizer<? extends Annotation> customizer) {
        registeredCustomizers.add(customizer);
        return this;
    }

    public AnnotatedParameterCustomizer removeCustomizer(AnnotationCustomizer<? extends Annotation> customizer) {
        registeredCustomizers.remove(customizer);
        return this;
    }
}
