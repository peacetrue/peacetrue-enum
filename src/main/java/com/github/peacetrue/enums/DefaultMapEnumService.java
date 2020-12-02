package com.github.peacetrue.enums;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;

/**
 * @author : xiayx
 * @since : 2020-12-02 11:13
 **/
@Slf4j
@Data
@NoArgsConstructor
public class DefaultMapEnumService implements EnumService<Map<String, Object>>,
        ApplicationListener<ContextRefreshedEvent> {

    private static final Map<String, List<Map<String, Object>>> ENUMS = new HashMap<>();

    @Autowired
    private EnumProvider enumProvider;
    @Autowired
    private EnumNameResolver enumNameResolver;
    @Autowired
    private EnumItemResolver enumItemResolver;
    private Map<String, Class<? extends Enum>> enumClasses;

    public DefaultMapEnumService(Map<String, Class<? extends Enum>> enumClasses) {
        this.enumClasses = enumClasses;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("应用启动完成后，加载枚举信息");
        Set<Class<? extends Enum>> enumClasses = enumProvider.getEnumClasses();
        log.info("共读取[{}]个枚举类", enumClasses.size());
        for (Class<? extends Enum> enumClass : enumClasses) {
            String enumName = enumNameResolver.resolveEnumName(enumClass);
            List<Map<String, Object>> enumItems = enumItemResolver.resolveEnumItems(enumClass);
            ENUMS.put(enumName, enumItems);
            log.debug("缓存[{}]共[{}]项", enumClass.getName(), enumItems.size());
        }

        if (this.enumClasses != null) {
            for (Map.Entry<String, Class<? extends Enum>> entry : this.enumClasses.entrySet()) {
                ENUMS.put(entry.getKey(), enumItemResolver.resolveEnumItems(entry.getValue()));
            }
        }
    }

    @Override
    public List<Map<String, Object>> query(String enumName) {
        return ENUMS.getOrDefault(enumName, Collections.emptyList());
    }

    @Override
    public Map<String, Object> get(String enumName, Object id) {
        return ENUMS.get(enumName).stream().filter(item -> item.get("id").toString().equals(id.toString())).findAny().orElse(null);
    }
}
