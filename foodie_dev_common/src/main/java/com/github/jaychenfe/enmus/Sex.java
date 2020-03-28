package com.github.jaychenfe.enmus;

/**
 * 性别 枚举
 *
 * @author jaychenfe
 */
public enum Sex {
    /**
     * 女
     */
    woman(0, "女"),
    /**
     * 男
     */
    man(1, "男"),
    /**
     * 保密
     */
    secret(2, "保密");

    public final Integer type;
    public final String value;

    Sex(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
