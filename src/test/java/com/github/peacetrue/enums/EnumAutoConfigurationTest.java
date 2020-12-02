package com.github.peacetrue.enums;

/**
 * @author xiayx
 */
public class EnumAutoConfigurationTest {

    public enum Anonymity {
        key1 {
            public Integer getType() {
                return null;
            }
        },
        key2 {
            public Integer getType() {
                return null;
            }
        };

        public abstract Integer getType();
    }

}
