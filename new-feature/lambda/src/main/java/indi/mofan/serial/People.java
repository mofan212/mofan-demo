package indi.mofan.serial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/6/22 19:46
 */
@Getter
@Setter
@AllArgsConstructor
public class People implements Serializable {
    private static final long serialVersionUID = 8209986739933993768L;

    public People() {
        System.out.println("People的无参构造器");
    }

    private String name;
    private int age;


    @Override
    protected Object clone() throws CloneNotSupportedException {
        System.out.println("People的clone方法");
        return super.clone();
    }
}
