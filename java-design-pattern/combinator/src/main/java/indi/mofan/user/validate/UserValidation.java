package indi.mofan.user.validate;

import indi.mofan.user.User;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author mofan
 * @date 2024/3/18 10:59
 */
public interface UserValidation extends Function<User, ValidationResult> {
    static UserValidation nameIsNotEmpty() {
        return holds(user -> !StringUtils.trim(user.name()).isEmpty(), "Name is empty.");
    }

    static UserValidation eMailContainsAtSign() {
        return holds(user -> StringUtils.contains(user.email(), "@"), "Missing @-sign.");
    }

    private static UserValidation holds(Predicate<User> predicate, String reason) {
        return user -> predicate.test(user)
                ? ValidationResult.valid()
                : ValidationResult.invalid(reason);
    }

    default UserValidation and(UserValidation other) {
        return user -> {
            ValidationResult res = this.apply(user);
            return res.isValid() ? other.apply(user) : res;
        };
    }
}
