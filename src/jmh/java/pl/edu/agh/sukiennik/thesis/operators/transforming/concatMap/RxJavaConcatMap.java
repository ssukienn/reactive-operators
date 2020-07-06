package pl.edu.agh.sukiennik.thesis.operators.transforming.concatMap;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.edu.agh.sukiennik.thesis.operators.PerformanceSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Fork(1)
@State(Scope.Thread)
public class RxJavaConcatMap {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<String> characters;
    private Flowable<Integer> singleConcatMapFlowable;
    private Flowable<Integer> multiConcatMapFlowable;
    private Flowable<Integer> multiConcatMapEachOnIoFlowable;

    @Setup
    public void setup() {
        characters = Flowable.just("A", "B");
        singleConcatMapFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiConcatMapFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiConcatMapEachOnIoFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
    }
    
    @Benchmark
    @Measurement(iterations = 5, time = 5)
    public void singleConcatMap(Blackhole bh) {
        singleConcatMapFlowable
                .concatMap(integer -> characters.map(character -> character + integer.toString()))
                .blockingSubscribe(new PerformanceSubscriber(bh));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 10)
    public void multiConcatMap(Blackhole bh) {
        Flowable<String> results = null;
        for (int i = 0; i < 10; i++) {
            if(results == null) {
                results = multiConcatMapFlowable.concatMap(integer -> characters.map(character -> character + integer.toString()));
            } else {
                results = results.concatMap(string -> characters.map(character -> character + string));
            }
        }
        results.blockingSubscribe(new PerformanceSubscriber(bh));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void multiConcatMapEachOnIo(Blackhole bh) {
        Flowable<String> results = null;
        for (int i = 0; i < 10; i++) {
            if(results == null) {
                results = multiConcatMapEachOnIoFlowable
                        .observeOn(Schedulers.io())
                        .concatMap(integer -> characters.map(character -> character + integer.toString()));
            } else {
                results = results
                        .observeOn(Schedulers.io())
                        .concatMap(string -> characters.map(character -> character + string));
            }
        }
        results.blockingSubscribe(new PerformanceSubscriber(bh));
    }


    public static void main(String[] args) {
        //RxJavaConcatMap concatMapBenchmark = new RxJavaConcatMap();
        //concatMapBenchmark.singleConcatMap();
    }

}
