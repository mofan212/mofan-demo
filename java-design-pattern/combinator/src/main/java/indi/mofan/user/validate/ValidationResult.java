package indi.mofan.user.validate;

import java.util.Optional;

/**
 * @author mofan
 * @date 2024/3/18 10:49
 */
public interface ValidationResult {
    /**
     * 是否合法
     */
    boolean isValid();

    /**
     * 非法信息
     */
    Optional<String> getReason();

    /**
     * 获取一个合法的校验结果
     */
    static ValidationResult valid() {
        return ValidationSupport.valid();
    }

    /**
     * 获取一个非法的校验结果
     */
    static ValidationResult invalid(String reason) {
        return new Invalid(reason);
    }


}
