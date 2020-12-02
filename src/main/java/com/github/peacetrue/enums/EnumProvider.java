package com.github.peacetrue.enums;

import java.util.Set;

/**
 * @author : xiayx
 * @since : 2020-11-28 19:56
 **/
public interface EnumProvider {

    Set<Class<? extends Enum>> getEnumClasses();

}
