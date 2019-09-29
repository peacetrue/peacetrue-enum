package com.github.peacetrue.enums;

import com.github.peacetrue.bean.CodeCapable;
import com.github.peacetrue.bean.NameCapable;

/**
 * 应用系统
 *
 * @author xiayx
 */
public enum Applications2 implements CodeCapable, NameCapable {

    sf("sf", "顺丰系统"),
    personal("personal", "个人版"),
    merchant("merchant", "商户版"),
    love_gold("love_gold", "爱有金"),;

    private String code;
    private String name;

    Applications2(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}