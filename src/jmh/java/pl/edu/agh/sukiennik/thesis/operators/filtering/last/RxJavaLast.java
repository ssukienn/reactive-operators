package pl.edu.agh.sukiennik.thesis.operators.filtering.last;

import io.reactivex.rxjava3.core.Flowable;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.edu.agh.sukiennik.thesis.utils.ForcedGcMemoryProfiler;
import pl.edu.agh.sukiennik.thesis.utils.PerformanceSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Fork(1)
@State(Scope.Thread)
public class RxJavaLast {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Integer> singleLast;

    @Setup
    public void setup() {
        singleLast = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
    }

    @TearDown(Level.Iteration)
    public void cleanup2() {
        ForcedGcMemoryProfiler.recordUsedMemory();
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void singleLast(Blackhole bh) {
        singleLast
                .lastElement()
                .blockingSubscribe(new PerformanceSubscriber(bh));
    }

    public static void main(String[] args) {
        //RxJavaLast firstBenchmark = new RxJavaLast();
        //firstBenchmark.singleLast();
    }

}

