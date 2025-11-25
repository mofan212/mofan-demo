package indi.mofan.lombok.delegate;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @author mofan
 * @date 2025/11/25 15:19
 */
public interface MyDelegate {
    @JsonIgnore
    boolean isFlag();

    @JsonIgnore
    void setFlag(boolean flag);

    Integer getInteger();

    void setInteger(Integer integer);

    @JsonIgnore
    List<String> getList();
}
