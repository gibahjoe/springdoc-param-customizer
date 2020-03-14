package com.devappliance.springdocparamcustomizer;

import com.querydsl.core.types.Path;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.util.CastUtils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/
public class QuerydslPredicateOperationCustomizer implements OperationCustomizer {
    private QuerydslBindingsFactory querydslBindingsFactory;

    public QuerydslPredicateOperationCustomizer(QuerydslBindingsFactory querydslBindingsFactory) {
        this.querydslBindingsFactory = querydslBindingsFactory;
    }

    //
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

    private Map<String, Object> getPathSpec(QuerydslBindings instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            return (Map<String, Object>) field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    private Optional<Path<?>> getPath(Object instance) {
        try {
            Field field = instance.getClass().getDeclaredField("path");
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            return (Optional<Path<?>>) field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        List<io.swagger.v3.oas.models.parameters.Parameter> newParameters = new ArrayList<>();
        String[] pNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(handlerMethod.getMethod());
        for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
            Parameter parameter = parameters[i];
            QuerydslPredicate[] annotationsByType = parameter.getAnnotationsByType(QuerydslPredicate.class);
            List<io.swagger.v3.oas.models.parameters.Parameter> operationParameters = operation.getParameters();
            int finalI = i;
            io.swagger.v3.oas.models.parameters.Parameter predicateParam = operationParameters.stream().filter(parameter1 -> parameter1.getName().equals(pNames[finalI])).findFirst().orElse(null);

            if (annotationsByType.length > 0) {
                operationParameters.remove(predicateParam);
                for (QuerydslPredicate predicate : annotationsByType) {
                    Optional<QuerydslPredicate> annotation = Optional.of(predicate);
                    ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(predicate.root());
                    TypeInformation<?> domainType = classTypeInformation.getRequiredActualType();

                    Optional<Class<? extends QuerydslBinderCustomizer<?>>> bindingsAnnotation = annotation //
                            .map(QuerydslPredicate::bindings) //
                            .map(CastUtils::cast);

                    QuerydslBindings bindings = bindingsAnnotation //
                            .map(it -> querydslBindingsFactory.createBindingsFor(domainType, it)) //
                            .orElseGet(() -> querydslBindingsFactory.createBindingsFor(domainType));

                    Set<String> fieldsToAdd = Arrays.stream(predicate.root().getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());

                    //remove blacklisted fields
                    Map<String, Object> pathSpecMap = getPathSpec(bindings, "pathSpecs");
                    Set<String> blacklist = getFieldValues(bindings, "blackList");
                    fieldsToAdd.removeIf(blacklist::contains);

                    Set<String> whiteList = getFieldValues(bindings, "whiteList");
                    Set<String> aliases = getFieldValues(bindings, "aliases");

                    fieldsToAdd.addAll(aliases);
                    fieldsToAdd.addAll(whiteList);
                    for (String fieldName : fieldsToAdd) {
                        Object pathAndBinding = pathSpecMap.get(fieldName);
                        Optional<Path<?>> path = Optional.empty();
                        if (pathAndBinding != null) {
                            path = getPath(pathAndBinding);
                        }
                        Type type = path.isPresent() ? path.get().getType() : getFieldType(fieldName, predicate.root());
                        io.swagger.v3.oas.models.parameters.Parameter newParameter = buildParam(type, fieldName, "query", false, null);
                        newParameters.add(newParameter);
                    }
                }
            }
        }
        operation.getParameters().addAll(newParameters);
        return operation;
    }

    private Type getFieldType(String fieldName, Class<?> root) {
        try {
            Field declaredField = root.getDeclaredField(fieldName);
            return declaredField.getGenericType();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return String.class;
    }

    private io.swagger.v3.oas.models.parameters.Parameter buildParam(Type type, String name, String in, Boolean required,
                                                                     String defaultValue) {
        io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();

        if (StringUtils.isBlank(parameter.getName())) {
            parameter.setName(name);
        }

        if (StringUtils.isBlank(parameter.getIn())) {
            parameter.setIn(in);
        }

        if (required != null && parameter.getRequired() == null) {
            parameter.setRequired(required);
        }

        if (parameter.getSchema() == null) {
            Schema<?> schema = null;
            PrimitiveType primitiveType = PrimitiveType.fromType(type);
            if (primitiveType != null) {
                schema = primitiveType.createProperty();
            } else {
                ResolvedSchema resolvedSchema;
                if (type instanceof ParameterizedType) {
                    resolvedSchema = ModelConverters.getInstance()
                            .resolveAsResolvedSchema(
                                    new io.swagger.v3.core.converter.AnnotatedType((ParameterizedType) type).resolveAsRef(true));
                } else {
                    resolvedSchema = ModelConverters.getInstance()
                            .resolveAsResolvedSchema(
                                    new AnnotatedType(type));
                }
                schema = resolvedSchema.schema;
            }
            if (defaultValue != null)
                schema.setDefault(defaultValue);
            parameter.setSchema(schema);
        }
        return parameter;
    }
}
