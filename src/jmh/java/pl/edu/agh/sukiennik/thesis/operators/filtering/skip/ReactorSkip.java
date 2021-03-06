package pl.edu.agh.sukiennik.thesis.operators.filtering.skip;

import org.openjdk.jmh.annotations.*;
import pl.edu.agh.sukiennik.thesis.utils.ForcedGcMemoryProfiler;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Fork(1)
@State(Scope.Thread)
public class ReactorSkip {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flux<Integer> singleSkip;
    private Flux<Integer> multipleSkip;
    private Flux<Integer> multiSkipEachOnIo;

    @Setup
    public void setup() {
        singleSkip = Flux.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multipleSkip = Flux.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiSkipEachOnIo = Flux.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
    }

    @TearDown(Level.Iteration)
    public void cleanup2() {
        ForcedGcMemoryProfiler.recordUsedMemory();
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void singleSkip() {
        singleSkip
                .skip(times / 2)
                .then()
                .block();
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void multiSkip() {
        Flux<Integer> range = multipleSkip;
        int condition = times;
        for (int i = 0; i < 10; i++) {
            condition = condition / 2;
            int finalCondition = condition;
            range = range.skip(finalCondition);
        }
        range.then().block();
    }

    //@Benchmark
    @Measurement(iterations = 5, time = 20)
    public void multiSkipEachOnIo() {
        Flux<Integer> range = multiSkipEachOnIo;
        int drop = times;
        for (int i = 0; i < 10; i++) {
            drop = drop / 2;
            int dropCount = drop;
            range = range.publishOn(Schedulers.elastic()).skip(dropCount);
        }
        range.then().block();
    }

    public static void main(String[] args) {
        //ReactorSkip skipBenchmark = new ReactorSkip();
        //skipBenchmark.singleSkip();
    }

}

