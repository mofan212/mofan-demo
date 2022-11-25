package indi.mofan.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/11/25 17:53
 */
@Getter
@Setter
@Accessors(chain = true)
public class Chain implements Serializable {
    private static final long serialVersionUID = -4000589575752085658L;

    private String name;
    private Double length;

    public static Chain chainFactory() {
        return new Chain();
    }
}
