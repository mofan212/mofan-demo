package indi.mofan.builder;

import indi.mofan.builder.enums.Topping;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author mofan
 * @date 2022/10/5 12:34
 */
@Getter
public abstract class Pizza {
    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        public abstract Pizza build();

        protected abstract T self();
    }

    public Pizza(Builder<?> builder) {
        this.toppings = builder.toppings.clone();
    }
}
