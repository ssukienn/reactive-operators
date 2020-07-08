package pl.edu.agh.sukiennik.thesis.operators.utility.delayElements;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class ReactorDelayElements {

    @Param({"1", "10", "50", "100"})
    private static int times;

    private Flux<Integer> singleDelayElements;

    @Setup
    public void setup() {
        singleDelayElements = Flux.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
    }

    @Benchmark
    @Measurement(iterations = 5, time = 1)
    public void singleDelayElements() {
        singleDelayElements
                .delayElements(Duration.ofMillis(25))
                .then()
                .block();
    }

    public static void main(String[] args) {
        //ReactorDelayElements delayElementsBenchmark = new ReactorDelayElements();
        //delayElementsBenchmark.setup();
        //delayElementsBenchmark.singleDelayElements();
    }

}

