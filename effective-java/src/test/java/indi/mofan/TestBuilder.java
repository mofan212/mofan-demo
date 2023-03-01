package indi.mofan;

import indi.mofan.builder.Calzone;
import indi.mofan.builder.NyPizza;
import indi.mofan.builder.enums.Size;
import indi.mofan.builder.enums.Topping;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mofan
 * @date 2022/10/5 14:13
 */
public class TestBuilder {
    @Test
    public void testPizza() {
        NyPizza pizza = new NyPizza.Builder(Size.SMALL)
                .addTopping(Topping.SAUSAGE)
                .addTopping(Topping.ONION)
                .build();
        assertEquals(Size.SMALL, pizza.getSize());
        assertTrue(pizza.getToppings().containsAll(new HashSet<>(Arrays.asList(Topping.SAUSAGE, Topping.ONION))));

        Calzone calzone = new Calzone.Builder()
                .addTopping(Topping.HAM)
                .sauceInside()
                .build();
        assertTrue(calzone.isSauceInside());
        assertEquals(1, calzone.getToppings().size());
        assertTrue(calzone.getToppings().contains(Topping.HAM));
    }
}
