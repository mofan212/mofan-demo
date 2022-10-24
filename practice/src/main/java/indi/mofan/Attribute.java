package indi.mofan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author mofan
 * @date 2022/10/20 16:27
 */
@Getter
@Setter
@AllArgsConstructor
public class Attribute {
    private AttrCategory category;
    private String dataType;
    private AttrType type;

    public Attribute(String dataType, AttrType type) {
        this.category = AttrCategory.COMPOSITE;
        this.dataType = dataType;
        this.type = type;
    }
}
