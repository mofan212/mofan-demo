package indi.mofan.user.validate;

import java.util.Optional;

/**
 * @author mofan
 * @date 2024/3/18 10:49
 */
final class ValidationSupport {
    private static final ValidationResult valid = new ValidationResult() {
        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Optional<String> getReason() {
            return Optional.empty();
        }
    };

    public static ValidationResult valid() {
        return valid;
    }
}
