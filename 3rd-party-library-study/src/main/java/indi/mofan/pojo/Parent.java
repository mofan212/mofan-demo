package indi.mofan.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author mofan
 * @date 2022/10/24 10:19
 */
@Getter
@Setter
public class Parent {
    private String name;
    private List<Child> children;

    @Override
    public String toString() {
        return "Parent{" +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
