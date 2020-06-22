package pl.edu.agh.sukiennik.thesis.operators.filtering.skipWhile;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.edu.agh.sukiennik.thesis.operators.PerformanceSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class RxJavaSkipWhile {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Integer> singleSkipWhileFlowable;
    private Flowable<Integer> multiSkipWhileFlowable;
    private Flowable<Integer> multiSkipWhileEachOnIoFlowable;

    @Setup
    public void setup() {
        singleSkipWhileFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiSkipWhileFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiSkipWhileEachOnIoFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 5)
    public void singleSkipWhile(Blackhole bh) {
        singleSkipWhileFlowable
                .skipWhile(value -> value <= times / 2)
                .blockingSubscribe(new PerformanceSubscriber(bh));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 10)
    public void multiSkipWhile(Blackhole bh) {
        Flowable<Integer> range = multiSkipWhileFlowable;
        int condition = times;
        for (int i = 0; i < 10; i++) {
            condition = condition / 2;
            int finalCondition = condition;
            range = range.skipWhile(value -> value <= finalCondition);
        }
        range.blockingSubscribe(new PerformanceSubscriber(bh));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void multiSkipWhileEachOnIo(Blackhole bh) {
        Flowable<Integer> range = multiSkipWhileEachOnIoFlowable;
        int condition = times;
        for (int i = 0; i < 10; i++) {
            condition = condition / 2;
            int finalCondition = condition;
            range = range.observeOn(Schedulers.io()).skipWhile(value -> value <= finalCondition);
        }
        range.blockingSubscribe(new PerformanceSubscriber(bh));
    }

    public static void main(String[] args) {
        //RxJavaSkipWhile firstBenchmark = new RxJavaSkipWhile();
        //firstBenchmark.singleSkipWhile();
    }
}

