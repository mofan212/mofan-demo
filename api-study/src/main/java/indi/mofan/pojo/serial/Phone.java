package indi.mofan.pojo.serial;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author mofan
 * @date 2023/6/15 20:42
 */
@Getter
@Setter
public class Phone implements Serializable {
    @Serial
    private static final long serialVersionUID = -2025192839395117683L;

    public static String staticStr;
    private String name;
    private transient BigDecimal price;
}
