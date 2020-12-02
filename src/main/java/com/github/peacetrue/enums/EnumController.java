package com.github.peacetrue.enums;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 枚举控制器
 *
 * @author xiayx
 */
@Slf4j
@SuppressWarnings("unchecked")
@RequestMapping("${peacetrue.enum.mapping:/enums}")
public class EnumController {

    @Autowired
    private EnumService enumService;

    @ResponseBody
    @GetMapping(params = "name")
    public List<Object> queryByQueryParam(String name) {
        log.info("获取枚举[{}]的所有数据项", name);
        return enumService.query(name);
    }

    @ResponseBody
    @GetMapping(params = "names")
    public Map<String, List<Object>> queryByQueryParam(String[] names) {
        log.info("获取枚举[{}]的所有数据项", Arrays.toString(names));
        return Arrays.stream(names).collect(Collectors.toMap(Function.identity(), s -> enumService.query(s)));
    }

    @ResponseBody
    @GetMapping("/{name}")
    public List<Object> queryByPathParam(@PathVariable String name) {
        log.info("获取枚举[{}]的所有数据项", name);
        return enumService.query(name);
    }

//    @ResponseBody
//    @GetMapping(path = "/{name}", params = "id")
//    public List<Object> query(@PathVariable String name, Long[] id) {
//        return Arrays.stream(id).map(item -> enumService.get(name, item)).collect(Collectors.toList());
//    }

    @ResponseBody
    @GetMapping(path = "/{name}", params = "id")
    public List<Object> query(@PathVariable String name, String[] id) {
        return Arrays.stream(id).map(item -> enumService.get(name, item)).collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping(params = {"name", "page"})
    public Page<Object> queryByQueryParam(String name, Pageable pageable) {
        log.info("分页获取枚举[{}]的数据项", name);
        List<Object> items = enumService.query(name);
        //TODO zero based or one based
        if (pageable.getOffset() > items.size() - 1) return Page.empty();
        int end = (int) pageable.getOffset() + pageable.getPageSize();
        List<Object> itemsOfPage = items.subList((int) pageable.getOffset(), Math.min(end, items.size()));
        return new PageImpl<>(itemsOfPage, pageable, items.size());
    }

    @ResponseBody
    @GetMapping(path = "/{name}", params = "page")
    public Page<Object> queryByUrlParam(@PathVariable String name, Pageable pageable) {
        return this.queryByQueryParam(name, pageable);
    }


}
