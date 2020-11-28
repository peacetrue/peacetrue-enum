package com.github.peacetrue.enums;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 枚举控制器
 *
 * @author xiayx
 */
@Slf4j
@RequestMapping("${peacetrue.enum.mapping:/enums}")
public class EnumController {

    /** 枚举类 */
    private final Map<String, List<Map<String, Object>>> enumClasses;

    public EnumController(Map<String, List<Map<String, Object>>> enumClasses) {
        this.enumClasses = Objects.requireNonNull(enumClasses);
    }

    private List<Map<String, Object>> find(String name) {
        List<Map<String, Object>> items = enumClasses.get(name);
        if (items == null) {
            String message = String.format("%s is not a valid enum short name", name);
            throw new IllegalArgumentException(message);
        }
        return items;
    }

    @ResponseBody
    @GetMapping(params = "name")
    public List<Map<String, Object>> query(String name) {
        log.info("获取枚举[{}]的所有数据项", name);
        return find(name);
    }

    @GetMapping(params = {"name", "page"})
    public Page<Map<String, Object>> query(String name, Pageable pageable) {
        log.info("分页获取枚举[{}]的数据项", name);
        List<Map<String, Object>> items = find(name);
        //TODO zero based or one based
        if (pageable.getOffset() > items.size() - 1) return Page.empty();
        List<Map<String, Object>> itemsOfPage = items.subList((int) pageable.getOffset(),
                (int) pageable.getOffset() + pageable.getPageSize());
        return new PageImpl<>(itemsOfPage, pageable, items.size());
    }

    @ResponseBody
    @GetMapping(params = "names")
    public Map<String, List<Map<String, Object>>> query(String[] names) {
        if (log.isInfoEnabled()) log.info("获取枚举[{}]的所有数据项", Arrays.toString(names));
        return Arrays.stream(names).collect(Collectors.toMap(Function.identity(), this::find));
    }


}
