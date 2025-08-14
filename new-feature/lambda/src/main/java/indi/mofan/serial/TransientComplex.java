package indi.mofan.serial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/6/28 19:40
 */
@Getter
@Setter
@AllArgsConstructor
public class TransientComplex implements Serializable {
    private static final long serialVersionUID = -6616158309969761040L;

    private String string;
    private transient Simple simple;
    private boolean bool;
}
