package com.github.peacetrue.enums;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    @ConditionalOnMissingBean(EnumController.class)
    public EnumController enumController() {
        return new EnumController();
    }

    @Bean
    @ConditionalOnMissingBean(EnumService.class)
    public EnumService<?> enumService() {
        return new DefaultMapEnumService(properties.getEnumClasses());
    }

    @Bean
    @ConditionalOnMissingBean(EnumProvider.class)
    public EnumProvider enumProvider() {
        return new DefaultEnumProvider(properties.getBasePackagePaths());
    }

    @Bean
    @ConditionalOnMissingBean(EnumNameResolver.class)
    public EnumNameResolver enumNameResolver() {
        return new DefaultEnumNameResolver();
    }

    @Bean
    @ConditionalOnMissingBean(EnumItemResolver.class)
    public EnumItemResolver enumItemResolver() {
        DefaultEnumItemResolver resolver = new DefaultEnumItemResolver();
//        resolver.setEnumOrdinal(EnumItemResolver.ENUM_ORDINAL);
//        resolver.setEnumName(EnumItemResolver.ENUM_NAME);
        return resolver;
    }

}
