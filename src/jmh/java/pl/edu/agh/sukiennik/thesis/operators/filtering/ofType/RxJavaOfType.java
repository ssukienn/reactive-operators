package pl.edu.agh.sukiennik.thesis.operators.filtering.ofType;

import io.reactivex.rxjava3.core.Flowable;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.edu.agh.sukiennik.thesis.utils.ForcedGcMemoryProfiler;
import pl.edu.agh.sukiennik.thesis.utils.PerformanceSubscriber;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Fork(1)
@State(Scope.Thread)
public class RxJavaOfType {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Number> singleOfType;

    @Setup
    public void setup() {
        Stream<Integer> streamOfInts = Arrays.stream(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        Stream<Double> streamOfDoubles = Arrays.stream(IntStream.rangeClosed(0, times).asDoubleStream().boxed().toArray(Double[]::new));
        singleOfType = Flowable.fromArray(Stream.concat(streamOfInts, streamOfDoubles).toArray(Number[]::new));
    }

    @TearDown(Level.Iteration)
    public void cleanup2() {
        ForcedGcMemoryProfiler.recordUsedMemory();
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void singleOfType(Blackhole bh) {
        singleOfType
                .ofType(Double.class)
                .blockingSubscribe(new PerformanceSubscriber(bh));
    }

    public static void main(String[] args) {
        //RxJavaOfType ofTypeBenchmark = new RxJavaOfType();
        //ofTypeBenchmark.singleOfType();
    }

}

