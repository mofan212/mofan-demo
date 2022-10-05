package indi.mofan.builder;

import lombok.Getter;

/**
 * @author mofan
 * @date 2022/10/5 14:06
 *
 * 馅料内置的半月型披萨
 */
@Getter
public class Calzone extends Pizza {
    /**
     * 是否有酱汁
     */
    private final boolean sauceInside;

    public static class Builder extends Pizza.Builder<Builder> {
        private boolean sauceInside = false;

        public Builder sauceInside() {
            this.sauceInside = true;
            return this;
        }

        @Override
        public Calzone build() {
            return new Calzone(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public Calzone(Builder builder) {
        super(builder);
        this.sauceInside = builder.sauceInside;
    }
}
