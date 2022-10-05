package indi.mofan.builder;

import indi.mofan.builder.enums.Size;
import lombok.Getter;

import java.util.Objects;

/**
 * @author mofan
 * @date 2022/10/5 13:22
 * <p>
 * 经典纽约风味披萨
 */
@Getter
public class NyPizza extends Pizza {

    private final Size size;

    public static class Builder extends Pizza.Builder<Builder> {
        private final Size size;

        public Builder(Size size) {
            this.size = Objects.requireNonNull(size);
        }

        @Override
        public NyPizza build() {
            return new NyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public NyPizza(Builder builder) {
        super(builder);
        this.size = builder.size;
    }
}
