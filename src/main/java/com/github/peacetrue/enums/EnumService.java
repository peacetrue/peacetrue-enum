package com.github.peacetrue.enums;

import java.util.List;

/**
 * @author : xiayx
 * @since : 2020-12-02 11:11
 **/
public interface EnumService<T> {

    List<T> query(String enumName);

    T get(String enumName, Object id);
}
