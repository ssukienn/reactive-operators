package pl.edu.agh.sukiennik.thesis.operators.math.sum;

import hu.akarnokd.rxjava3.math.MathFlowable;
import io.reactivex.rxjava3.core.Flowable;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.edu.agh.sukiennik.thesis.utils.ForcedGcMemoryProfiler;
import pl.edu.agh.sukiennik.thesis.utils.PerformanceSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Fork(1)
@State(Scope.Thread)
public class RxJavaSum {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Long> singleSumFlowable;

    @Setup
    public void setup() {
        singleSumFlowable = Flowable.fromArray(LongStream.rangeClosed(0, times).boxed().toArray(Long[]::new));
    }

    @TearDown(Level.Iteration)
    public void cleanup2() {
        ForcedGcMemoryProfiler.recordUsedMemory();
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void singleSum(Blackhole bh) {
        MathFlowable.sumLong(singleSumFlowable).blockingSubscribe(new PerformanceSubscriber(bh));
    }

    public static void main(String[] args) {
        //RxJavaSum sumBenchmark = new RxJavaSum();
        //sumBenchmark.singleSum();
    }

}



