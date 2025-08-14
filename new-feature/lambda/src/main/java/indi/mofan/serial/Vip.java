package indi.mofan.serial;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/7/2 21:38
 */
@Getter
@Setter
public class Vip extends User implements Serializable {

    private static final long serialVersionUID = 5125855385522887545L;

    private String level;
}
