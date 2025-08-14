package indi.mofan.domain;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/3/29 12:24
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 8597585718927776100L;

    private String name;
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
