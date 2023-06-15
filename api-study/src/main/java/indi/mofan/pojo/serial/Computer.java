package indi.mofan.pojo.serial;

import lombok.Getter;
import lombok.Setter;

import java.io.ObjectStreamField;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author mofan
 * @date 2023/6/15 21:34
 */
@Getter
@Setter
public class Computer implements Serializable {

    @Serial
    private static final long serialVersionUID = 455386781881536390L;
    @Serial
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("brand", String.class),
            // 可以序列化被 transient 字段，但不能序列化 static 字段
            new ObjectStreamField("price", BigDecimal.class)
    };

    private String brand;
    private String size;
    private transient BigDecimal price;

    public static String staticStr;
}
