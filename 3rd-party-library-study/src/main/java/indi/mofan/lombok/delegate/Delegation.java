package indi.mofan.lombok.delegate;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mofan
 * @date 2025/11/25 15:03
 */
@FieldNameConstants
public class Delegation implements MyDelegate {
    @Getter
    @Setter
    private boolean flag = true;

    @Setter
    @Getter
    private Integer integer;

    @Getter
    private final List<String> list = new ArrayList<>();
}
