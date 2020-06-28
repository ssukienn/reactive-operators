package pl.edu.agh.sukiennik.thesis.operators.conditional.amb;

import io.reactivex.rxjava3.core.Flowable;
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
public class RxJavaAmb {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Integer> singleAmb;
    private Flowable<Integer> forAmb;

    @Setup
    public void setup() {
        singleAmb = Flowable
                .fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new))
                .delay(10, TimeUnit.MILLISECONDS);
        forAmb = Flowable.fromArray(IntStream.rangeClosed(1000, times).boxed().toArray(Integer[]::new));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 1)
    public void singleAmb(Blackhole bh) {
        Flowable.ambArray(singleAmb, forAmb)
                .blockingSubscribe(new PerformanceSubscriber(bh));
    }

    public static void main(String[] args) {
        //RxJavaAmb ambBenchmark = new RxJavaAmb();
        //ambBenchmark.setup();
        //ambBenchmark.singleAmb();
    }

}
