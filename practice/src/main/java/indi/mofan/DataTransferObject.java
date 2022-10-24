package indi.mofan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * @author mofan
 * @date 2022/10/20 16:27
 */
@Getter
@Setter
@NoArgsConstructor
public class DataTransferObject {
    private String id;
    private List<Attribute> attrs;

    public DataTransferObject(String id) {
        this.attrs = Collections.emptyList();
        this.id = id;
    }
}
