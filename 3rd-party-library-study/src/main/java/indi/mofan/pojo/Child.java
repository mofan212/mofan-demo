package indi.mofan.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * @author mofan
 * @date 2022/10/24 10:21
 */
@Getter
@Setter
@FieldNameConstants
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
