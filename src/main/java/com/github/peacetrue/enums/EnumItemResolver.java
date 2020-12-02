package com.github.peacetrue.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : xiayx
 * @since : 2020-11-28 19:56
 **/
public interface EnumItemResolver {

    /** {@link Enum#ordinal()} */
    String ENUM_ORDINAL = "_ordinal";
    /** {@link Enum#name()} */
    String ENUM_NAME = "_name";

    /** 解析枚举项集合 */
    default List<Map<String, Object>> resolveEnumItems(Class<? extends Enum> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(this::resolveEnumItem)
                .collect(Collectors.toList());
    }

    /** 解析枚举项 */
    Map<String, Object> resolveEnumItem(Enum<?> enums);

}
