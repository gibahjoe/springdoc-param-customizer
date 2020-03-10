package com.devappliance.springdocparamcustomizer.customizer;

import com.devappliance.springdocparamcustomizer.AnnotatedParameterCustomizer;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.Iterator;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/
public interface AnnotationCustomizer<T extends Annotation> {

    Parameter customize(T annotation, Parameter parameterModel, java.lang.reflect.Parameter parameter, AnnotatedParameterCustomizer context, HandlerMethod handlerMethod, Iterator<? extends AnnotationCustomizer<?>> chain);

    Class<T> getType();
}
