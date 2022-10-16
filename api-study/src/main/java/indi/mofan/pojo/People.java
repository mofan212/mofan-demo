package indi.mofan.pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/10/16 17:42
 */
@Getter
@Setter
public class People implements Serializable {
    private static final long serialVersionUID = 3547129463753827085L;

    private String name;
}
