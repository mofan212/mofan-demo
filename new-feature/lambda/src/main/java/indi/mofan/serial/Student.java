package indi.mofan.serial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/7/1 15:51
 */
@Getter
@Setter
@AllArgsConstructor
public class Student implements Serializable {
    private static final long serialVersionUID = -4539491935997507115L;

    private String name;
    private int score;

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // 调用默认的反序列化方法
        ois.defaultReadObject();

        // 逻辑判断
        if (score < 0 || score > 150) {
            throw new IllegalArgumentException("学生分数异常");
        }
    }
}
