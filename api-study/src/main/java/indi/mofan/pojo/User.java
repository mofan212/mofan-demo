package indi.mofan.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author mofan
 * @date 2022/12/16 10:33
 */
@Getter
@AllArgsConstructor
@ToString
public class User {
    private final String name;
    private final int age;
}
