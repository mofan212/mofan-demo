package indi.mofan.serial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/6/28 19:33
 */
@Getter
@Setter
@AllArgsConstructor
public class Complex implements Serializable {

    private static final long serialVersionUID = 7663091376889314225L;

    private int intField;
    private Simple simple;
}
