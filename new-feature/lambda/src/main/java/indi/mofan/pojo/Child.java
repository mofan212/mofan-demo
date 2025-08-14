package indi.mofan.pojo;

import indi.mofan.api.SimpleInterface;
import lombok.Getter;
import lombok.Setter;

/**
 * @author mofan
 * @date 2022/7/4 18:16
 */
@Getter
@Setter
public class Child extends Parent implements SimpleInterface {
    private String privateChildField;
    public String publicChildField;

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
