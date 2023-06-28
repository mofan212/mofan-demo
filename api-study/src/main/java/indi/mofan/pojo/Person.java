package indi.mofan.pojo;

/**
 * @author mofan
 * @date 2022/8/16 10:25
 */
public class Person {
    private static final long serialVersionUID = -3225004151519310638L;

    public static final String CONSTANT = "HELLO_WORLD";

    private String name;
    private Integer age;

    public Boolean bool;

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Person() {
    }

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public static Integer returnInt(Integer integer) {
        return integer;
    }

    private String getStr(String name, Integer age) {
        return name + " - " + age;
    }

    public long sum(int one, long two) {
        return one + two;
    }

    public void print(String string) {
        System.out.println(string);
    }

    public int subtract(int a, int b) {
        return a - b;
    }
}
