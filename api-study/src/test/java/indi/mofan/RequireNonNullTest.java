package indi.mofan;

import indi.mofan.enums.MyVeryImportantSingleton;
import indi.mofan.enums.ReallyImportantSingleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2022/11/15 10:52
 */
public class RequireNonNullTest {

    /**
     * <a href = "https://talkwards.com/2018/11/03/one-thing-to-avoid-when-using-objects-requirenonnull-in-java-8/">
     * ONE THING TO AVOID WHEN USING OBJECTS.REQUIRE-NON-NULL IN JAVA 8
     * </a>
     */
    @Test
    public void testRequireNonNull() {
        try {
            MyVeryImportantSingleton.INSTANCE.set("Hello World", null);
        } catch (Throwable e) {
            // do nothing
        }

        Assertions.assertEquals("Hello World", MyVeryImportantSingleton.INSTANCE.getA());
        Assertions.assertNull(MyVeryImportantSingleton.INSTANCE.getB());


        try {
            ReallyImportantSingleton.INSTANCE.set("Hello World", null);
        } catch (Throwable e) {
            // do nothing
        }

        Assertions.assertNull(ReallyImportantSingleton.INSTANCE.getA());
        Assertions.assertNull(ReallyImportantSingleton.INSTANCE.getB());
    }
}
