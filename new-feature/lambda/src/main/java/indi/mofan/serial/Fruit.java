package indi.mofan.serial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/6/28 20:51
 */
@Getter
@Setter
@AllArgsConstructor
public class Fruit implements Serializable {
    private static final long serialVersionUID = 4088817990272523988L;

    private String name;
    private String color;
    private double size;

    Object readResolve() throws ObjectStreamException {
        // 返回的对象不一定是可序列化的
        return new Simple("Simple");
    }
}
