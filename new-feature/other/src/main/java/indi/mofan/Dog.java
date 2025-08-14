package indi.mofan;

/**
 * @author mofan
 * @date 2023/6/13 20:01
 */
public record Dog(String name, int age) {

    private static String str;

    public static void str(String str) {
        Dog.str = str;
    }

    public static String showStr() {
        return str;
    }

    private Dog(String name) {
        this(name, 0);
    }

    public static Dog from(String name) {
        return new Dog(name);
    }

    public String getUpperCaseName() {
        return this.name.toUpperCase();
    }
}
