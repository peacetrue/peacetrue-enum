package com.github.peacetrue.enums;

import lombok.Data;
import org.reflections.Reflections;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : xiayx
 * @since : 2020-12-02 10:54
 **/
@Data
public class DefaultEnumProvider implements EnumProvider {

    /** 枚举类搜索的基础类路径 */
    private String[] basePackagePaths;

    public DefaultEnumProvider(String[] basePackagePaths) {
        this.basePackagePaths = basePackagePaths;
    }

    @Override
    public Set<Class<? extends Enum>> getEnumClasses() {
        if (ObjectUtils.isEmpty(basePackagePaths)) return Collections.emptySet();
        return Arrays.stream(basePackagePaths)
                .flatMap(path -> new Reflections(path).getSubTypesOf(Enum.class).stream())
                .filter(enumClass -> enumClass.getSuperclass().equals(Enum.class))//防止找到非枚举的子类
                .collect(Collectors.toSet());

    }
}
