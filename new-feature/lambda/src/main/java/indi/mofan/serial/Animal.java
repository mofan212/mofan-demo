package indi.mofan.serial;

import lombok.Getter;
import lombok.Setter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author mofan
 * @date 2022/6/28 20:59
 */
@Getter
@Setter
public class Animal implements Externalizable {
    private static final long serialVersionUID = -3303909578015151048L;

    private String type;
    private int age;

    public Animal() {
        // Externalizable 反序列化时会调用无参构造方法
        System.out.println("Animal 的无参构造器");
    }

    public Animal(String type, int age) {
        this.type = type;
        this.age = age;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // 序列化时使用
        out.writeObject(this.type);
        out.writeInt(this.age);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // 反序列化使用。设置字段值的顺序要和序列化时一致。
        this.type = (String) in.readObject();
        this.age = in.readInt();
    }
}
