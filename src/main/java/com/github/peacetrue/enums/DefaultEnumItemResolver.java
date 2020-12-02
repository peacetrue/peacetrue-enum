package com.github.peacetrue.enums;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : xiayx
 * @since : 2020-11-28 19:58
 **/
@Data
public class DefaultEnumItemResolver implements EnumItemResolver {

//    private static final AtomicLong ID = new AtomicLong(0);

    private String enumOrdinal;
    private String enumName;

    @Override
    public Map<String, Object> resolveEnumItem(Enum<?> enums) {
        PropertyDescriptor[] descriptorArray = BeanUtils.getPropertyDescriptors(enums.getClass());
        List<PropertyDescriptor> descriptors = Arrays.stream(descriptorArray)
                .filter(descriptor -> ClassUtils.isPrimitiveOrWrapper(descriptor.getPropertyType())
                        || String.class == descriptor.getPropertyType())
                .collect(Collectors.toList());

        Map<String, Object> enumMap = new LinkedHashMap<>(2 + descriptors.size());
        //保留一个 id 列，模拟实体类记录
        enumMap.put("id", enums.ordinal());
        //内部字段
        if (enumOrdinal != null) enumMap.put(enumOrdinal, enums.ordinal());
        if (enumName != null) enumMap.put(enumName, enums.name());
        //实际属性可覆盖上面的属性
        descriptors.forEach(descriptor -> enumMap.put(
                descriptor.getName(),
                ReflectionUtils.invokeMethod(descriptor.getReadMethod(), enums)
        ));
        return enumMap;

    }
}
