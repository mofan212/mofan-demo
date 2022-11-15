package indi.mofan.enums;

import java.util.Objects;

/**
 * @author mofan
 * @date 2022/11/15 10:58
 */
public enum ReallyImportantSingleton {
    /**
     * 枚举项实例
     */
    INSTANCE;

    private String a;
    private String b;

    public void set(String a, String b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        this.a = a;
        this.b = b;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}
