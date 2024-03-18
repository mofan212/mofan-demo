package indi.mofan.user.validate;

import java.util.Optional;

/**
 * @author mofan
 * @date 2024/3/18 10:56
 */
class Invalid implements ValidationResult {

    private final String reason;

    public Invalid(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public Optional<String> getReason() {
        return Optional.of(this.reason);
    }
}
