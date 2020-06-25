package pl.edu.agh.sukiennik.thesis.operators.math.count;

import io.reactivex.rxjava3.core.Flowable;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.edu.agh.sukiennik.thesis.operators.PerformanceSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class RxJavaCount {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Long> singleCountFlowable;

    @Setup
    public void setup() {
        singleCountFlowable = Flowable.fromArray(LongStream.rangeClosed(0, times).boxed().toArray(Long[]::new));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 5)
    public void singlecount(Blackhole bh) {
        singleCountFlowable.count().blockingSubscribe(new PerformanceSubscriber(bh));
    }

    public static void main(String[] args) {
        //RxJavacount countBenchmark = new RxJavacount();
        //countBenchmark.singlecount();
    }

}


