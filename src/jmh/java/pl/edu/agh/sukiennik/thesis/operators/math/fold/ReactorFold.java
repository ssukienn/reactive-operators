package pl.edu.agh.sukiennik.thesis.operators.math.fold;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class ReactorFold {

    @Param({"1", "1000", "1000000", "10000000"})
    private static long times;

    private Flux<Long> singleFold;

    @Setup
    public void setup() {
        singleFold = Flux.fromArray(LongStream.rangeClosed(0, times).boxed().toArray(Long[]::new));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 1)
    public void singleFold(Blackhole bh) {
        singleFold
                .reduce(times, Long::sum)
                .then()
                .block();
    }

    public static void main(String[] args) {
        //ReactorFold foldBenchmark = new ReactorFold();
        //foldBenchmark.singleFold();
    }

}
