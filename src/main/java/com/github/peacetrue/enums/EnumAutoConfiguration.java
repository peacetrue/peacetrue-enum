package com.github.peacetrue.enums;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiayx
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(EnumProperties.class)
public class EnumAutoConfiguration {

    private final EnumProperties properties;

    public EnumAutoConfiguration(EnumProperties properties) {
        this.properties = properties;
    }

    /** 在指定包下搜索枚举类 */
    @SuppressWarnings("unchecked")
    static Set<Class<? extends Enum>> findEnumClasses(String... basePackagePaths) {
        if (ObjectUtils.isEmpty(basePackagePaths)) return Collections.emptySet();

        return Arrays.stream(basePackagePaths)
                .flatMap(path -> new Reflections(path).getSubTypesOf(Enum.class).stream())
                .filter(enumClass -> enumClass.getSuperclass().equals(Enum.class))//防止找到非枚举的子类
                .collect(Collectors.toSet());
    }

    static Map<String, Object> enumToMap(Enum enum_) {
        List<PropertyDescriptor> descriptors = Arrays.stream(BeanUtils.getPropertyDescriptors(enum_.getClass()))
                .filter(descriptor -> ClassUtils.isPrimitiveOrWrapper(descriptor.getPropertyType()) || String.class == descriptor.getPropertyType())
                .collect(Collectors.toList());

        Map<String, Object> map = new LinkedHashMap<>(2 + descriptors.size());
        map.put("_ordinal", enum_.ordinal());
        map.put("_name", enum_.name());

        descriptors.forEach(descriptor -> map.put(descriptor.getName(), ReflectionUtils.invokeMethod(descriptor.getReadMethod(), enum_)));
        return map;
    }

    /** 命名枚举类 */
    static Map<String, Class<? extends Enum>> nameEnumClasses(Set<Class<? extends Enum>> enumClasses, EnumNameResolver resolver) {
        if (ObjectUtils.isEmpty(enumClasses)) return Collections.emptyMap();
        return enumClasses.stream().collect(Collectors.toMap(resolver::resolveEnumName, Function.identity()));
    }

    /** 将枚举类转换成枚举值数组 */
    static Map<String, List<Map<String, Object>>> toEnumArray(Map<String, Class<? extends Enum>> enumClasses) {
        return enumClasses.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        Arrays.stream(entry.getValue().getEnumConstants())
                                .map(EnumAutoConfiguration::enumToMap)
                                .collect(Collectors.toList())));
    }

    private Map<String, List<Map<String, Object>>> resolveEnums() {
        try {
            Set<Class<? extends Enum>> enumClasses = findEnumClasses(properties.getBasePackagePaths());
            Map<String, Class<? extends Enum>> enumClassMap = nameEnumClasses(enumClasses, enumNameResolver());
            if (!properties.getEnumClasses().isEmpty()) {
                enumClassMap = new HashMap<>(enumClassMap);
                enumClassMap.putAll(properties.getEnumClasses());
            }
            return toEnumArray(enumClassMap);
        } catch (Exception e) {
            log.error("解析枚举类异常", e);
            return Collections.emptyMap();
        }
    }

    @Bean
    @ConditionalOnMissingBean(EnumController.class)
    public EnumController enumController() {
        return new EnumController(resolveEnums());
    }

    @Bean
    @ConditionalOnMissingBean(EnumNameResolver.class)
    public EnumNameResolver enumNameResolver() {
        return new SimpleEnumNameResolver();
    }

}
