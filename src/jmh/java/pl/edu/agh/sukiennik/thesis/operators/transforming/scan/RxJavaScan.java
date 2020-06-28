package pl.edu.agh.sukiennik.thesis.operators.transforming.scan;

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
public class RxJavaScan {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    private Flowable<Integer> singleScanFlowable;
    private Flowable<Integer> multiScanFlowable;
    private Flowable<Integer> multiScanEachOnIoFlowable;

    @Setup
    public void setup() {
        singleScanFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiScanFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiScanEachOnIoFlowable = Flowable.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
    }
    
    @Benchmark
    @Measurement(iterations = 5, time = 5)
    public void singleScan(Blackhole bh) {
        singleScanFlowable
                .scan(0, Integer::sum)
                .blockingSubscribe(new PerformanceSubscriber(bh));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 10)
    public void multiScan(Blackhole bh) {
        Flowable<Integer> range = multiScanFlowable;
        for (int i = 0; i < 10; i++) {
            range = range.scan(i, Integer::sum);
        }
        range.blockingSubscribe(new PerformanceSubscriber(bh));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void multiScanEachOnIo(Blackhole bh) {
        Flowable<Integer> range = multiScanEachOnIoFlowable;
        for (int i = 0; i < 10; i++) {
            range = range.observeOn(Schedulers.io()).scan(i, Integer::sum);
        }
        range.blockingSubscribe(new PerformanceSubscriber(bh));
    }


    public static void main(String[] args) {
        //RxJavaScan scanBenchmark = new RxJavaScan();
        //scanBenchmark.singleScan();
    }

}
