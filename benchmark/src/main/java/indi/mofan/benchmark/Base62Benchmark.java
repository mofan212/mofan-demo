package indi.mofan.benchmark;

import cn.hutool.core.codec.Base62;
import indi.mofan.util.NumberConversionUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author mofan
 * @date 2024/1/15 15:12
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 3, time = 3)
public class Base62Benchmark {
    private List<byte[]> targetStringList;

    @Setup(Level.Iteration)
    public void prepare() {
        targetStringList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            targetStringList.add(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    @Benchmark
    public List<String> numberConvert() {
        List<String> result = new ArrayList<>();
        for (byte[] string : targetStringList) {
            result.add(NumberConversionUtil.get62(string));
        }
        return result;
    }

    @Benchmark
    public List<String> hutoolBase62() {
        List<String> result = new ArrayList<>();
        for (byte[] string : targetStringList) {
            result.add(Base62.encode(string));
        }
        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(Base62Benchmark.class.getName())
                .build();
        new Runner(options).run();
    }
}

// Benchmark                      Mode  Cnt      Score       Error  Units
// Base62Benchmark.hutoolBase62   avgt   15  39754.620 ±  5674.439  ns/op
// Base62Benchmark.numberConvert  avgt   15  48025.101 ± 12422.833  ns/op
