package pl.edu.agh.sukiennik.thesis.operators.creating.range;

import org.openjdk.jmh.annotations.*;
import pl.edu.agh.sukiennik.thesis.utils.ForcedGcMemoryProfiler;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Fork(1)
@State(Scope.Thread)
public class ReactorRange {

    @Param({"1", "1000", "1000000", "10000000"})
    private static int times;

    @TearDown(Level.Iteration)
    public void cleanup2() {
        ForcedGcMemoryProfiler.recordUsedMemory();
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void singleRange() {
        Flux.range(0, times)
                .then()
                .block();
    }

    public static void main(String[] args) {
        //ReactorRange rangeBenchmark = new ReactorRange();
        //rangeBenchmark.singleRange();
    }

}

