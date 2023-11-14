package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * @author mofan
 * @date 2023/11/14 10:23
 */
public class GetCurrentMethodNameTest implements WithAssertions {

    /**
     * 性能较差
     */
    @Test
    public void testGetUseThread() {
        // 注意索引是 1
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        assertThat(methodName).isEqualTo("testGetUseThread");
    }

    /**
     * 性能最差
     */
    @Test
    public void testGetUseException() {
        // 与使用 Thread 相比，这里的索引是 0
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        assertThat(methodName).isEqualTo("testGetUseException");
    }

    /**
     * 性能最好
     */
    @Test
    public void testGetUseInnerClass() {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        assertThat(methodName).isEqualTo("testGetUseInnerClass");
    }

    /**
     * 性能较好，可读性最好，JDK9 引入
     */
    @Test
    public void testStackWalker() {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> optional = walker.walk(i -> i.findFirst().map(StackWalker.StackFrame::getMethodName));
        assertThat(optional).isNotEmpty().hasValue("testStackWalker");
    }
}
