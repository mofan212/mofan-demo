package indi.mofan.io;

import indi.mofan.PersonDtoTextInput;
import indi.mofan.dto.PersonDto;
import indi.mofan.io.core.Input;
import indi.mofan.io.core.Output;
import indi.mofan.io.core.filter.Filters;
import indi.mofan.io.core.filter.Function;
import indi.mofan.io.core.filter.Specification;
import indi.mofan.io.utils.Inputs;
import indi.mofan.io.utils.Outputs;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mofan
 * @date 2022/10/4 14:27
 */
public class IoApiTest {
    @Test
    @SneakyThrows
    public void testFileTransport() {
        File source = new File("in.txt");
        File destination = new File("out.tmp");

        Inputs.text(source).transferTo(Outputs.text(destination));
    }

    @Test
    @SneakyThrows
    public void testInterceptCountLine() {
        File source = new File("in.txt");
        File destination = new File("out.tmp");
        final AtomicInteger count = new AtomicInteger();

        Input<String, IOException> input = Inputs.text(source);

        Output<String, IOException> output = Outputs.text(destination);

        Function<String, String> function = from -> {
            count.incrementAndGet();
            return from;
        };

        input.transferTo(Filters.filter(function, output));

        System.out.println("Counter: " + count.get());
    }

    @Test
    @SneakyThrows
    public void testInterceptFilterLine() {
        File source = new File("in.txt");
        File destination = new File("out.tmp");

        Input<String, IOException> input = Inputs.text(source);

        Output<String, IOException> output = Outputs.text(destination);

        Specification<String> specification = item -> {
            // 过滤空行
            return item.trim().length() != 0;
        };

        input.transferTo(Filters.filter(specification, output));
    }

    @Test
    @SneakyThrows
    public void testPersonDtoToFileTransport() {
        PersonDto personDto = new PersonDto("Jerry", 42);
        File destination = new File("out.tmp");

        new PersonDtoTextInput(personDto).transferTo(Outputs.text(destination));
    }
}
