package indi.mofan.serial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/6/28 19:58
 */
@Getter
@Setter
@AllArgsConstructor
public class City implements Serializable {
    private static final long serialVersionUID = 2376268232459336285L;

    private String name;
    private int area;

    Object writeReplace() throws ObjectStreamException {
        // 返回的对象必须是可序列化的
        return new People("mofan", 20);
    }
}
