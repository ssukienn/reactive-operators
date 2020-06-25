package pl.edu.agh.sukiennik.thesis.operators.filtering.distinct;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.edu.agh.sukiennik.thesis.operators.PerformanceSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class RxJavaDistinct {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Integer> singleDistinctFlowable;

    @Setup
    public void setup() {
        singleDistinctFlowable = Flowable.fromArray(IntStream.concat(
                IntStream.rangeClosed(0, times), IntStream.rangeClosed(0, times)).boxed().toArray(Integer[]::new));

    }

    @Benchmark
    @Measurement(iterations = 5, time = 5)
    public void singleDistinct(Blackhole bh) {
        singleDistinctFlowable
                .distinct()
                .blockingSubscribe(new PerformanceSubscriber(bh));
    }
    
    public static void main(String[] args) {
        //RxJavaDistinct firstBenchmark = new RxJavaDistinct();
        //firstBenchmark.singleDistinct();
    }

}