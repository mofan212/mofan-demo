package indi.mofan.enums;

import java.util.Objects;

/**
 * @author mofan
 * @date 2022/11/15 10:48
 */
public enum MyVeryImportantSingleton {
    /**
     * 枚举项实例
     */
    INSTANCE;

    private String a;
    private String b;

    public void set(String a, String b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}
