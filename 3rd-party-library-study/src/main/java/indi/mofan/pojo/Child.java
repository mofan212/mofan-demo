package indi.mofan.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mofan
 * @date 2022/10/24 10:21
 */
@Getter
@Setter
public class Child {
    private String name;
    private int age;

    @Override
    public String toString() {
        return "Child{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
