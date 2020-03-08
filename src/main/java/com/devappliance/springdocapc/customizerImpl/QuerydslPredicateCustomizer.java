package com.devappliance.springdocapc.customizerImpl;

import com.devappliance.springdocapc.AnnotatedParameterCustomizer;
import com.devappliance.springdocapc.customizer.AnnotationCustomizer;
import com.google.gson.reflect.TypeToken;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.util.CastUtils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/
public class QuerydslPredicateCustomizer implements AnnotationCustomizer<QuerydslPredicate> {
    private QuerydslBindingsFactory querydslBindingsFactory;

    public QuerydslPredicateCustomizer(QuerydslBindingsFactory querydslBindingsFactory) {
        this.querydslBindingsFactory = querydslBindingsFactory;
    }

    @Override
    public Parameter customize(QuerydslPredicate predicate, Parameter parameterModel, java.lang.reflect.Parameter parameter,
                               HandlerMethod handlerMethod, Iterator<? extends AnnotationCustomizer<?>> chain) {

        ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(predicate.root());

        Optional<QuerydslPredicate> annotation = Optional.of(predicate);
        TypeInformation<?> domainType = classTypeInformation.getRequiredActualType();

        Optional<Class<? extends QuerydslBinderCustomizer<?>>> bindingsAnnotation = annotation //
                .map(QuerydslPredicate::bindings) //
                .map(CastUtils::cast);

        QuerydslBindings bindings = bindingsAnnotation //
                .map(it -> querydslBindingsFactory.createBindingsFor(domainType, it)) //
                .orElseGet(() -> querydslBindingsFactory.createBindingsFor(domainType));


        String generatedClassName = "com.devappliance.querydslpredicatereader." + predicate.bindings().getSimpleName() + "Generated";

        ClassPool classPool = ClassPool.getDefault();
        CtClass classPoolOrNull = classPool.getOrNull(generatedClassName);

        if (classPoolOrNull == null) {

            classPoolOrNull = classPool.makeClass(generatedClassName);

            Set<String> fieldsToAdd = Arrays.stream(predicate.root().getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());

            //remove blacklisted fields
            Set<String> blacklist = getFieldValues(bindings, "blackList");
            fieldsToAdd.removeIf(blacklist::contains);

            Set<String> whiteList = getFieldValues(bindings, "whiteList");
            Set<String> aliases = getFieldValues(bindings, "aliases");

            fieldsToAdd.addAll(aliases);
            fieldsToAdd.addAll(whiteList);

            for (String fieldName : fieldsToAdd) {
                try {
                    CtField f = new CtField(CtClass.charType, fieldName, classPoolOrNull);
                    f.setModifiers(Modifier.PUBLIC);
                    classPoolOrNull.addField(f);
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }

        }
        Class<?> toClass = null;
        try {
            toClass = classPoolOrNull.toClass(this.getClass().getClassLoader(), this.getClass().getProtectionDomain());

        } catch (CannotCompileException e) {
            e.printStackTrace();
            return AnnotatedParameterCustomizer.next(parameterModel, parameter, handlerMethod, chain);
        }

        Type type = TypeToken.get(toClass).getType();

        parameterModel.setRequired(false);
        ResolvedSchema resolvedSchema = ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(type));
        parameterModel.setSchema(resolvedSchema.schema);
        parameterModel.setName("filterPredicate");
        return AnnotatedParameterCustomizer.next(parameterModel, parameter, handlerMethod, chain);
    }


    private Set<String> getFieldValues(QuerydslBindings instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            return (Set<String>) field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    @Override
    public Class<QuerydslPredicate> getType() {
        return QuerydslPredicate.class;
    }
}
