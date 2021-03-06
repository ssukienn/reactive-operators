package pl.edu.agh.sukiennik.thesis.operators.transforming.concatMap;

import org.openjdk.jmh.annotations.*;
import pl.edu.agh.sukiennik.thesis.utils.ForcedGcMemoryProfiler;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Fork(1)
@State(Scope.Thread)
public class ReactorConcatMap {

    @Param({"1", "100", "1000", "10000"})
    private static int times;

    private Flux<String> characters;
    private Flux<Integer> singleConcatMapFlux;
    private Flux<Integer> multiConcatMapFlux;
    private Flux<Integer> multiConcatMapEachOnIoFlux;

    @Setup
    public void setup() {
        characters = Flux.just("A", "B");
        singleConcatMapFlux = Flux.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiConcatMapFlux = Flux.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
        multiConcatMapEachOnIoFlux = Flux.fromArray(IntStream.rangeClosed(0, times).boxed().toArray(Integer[]::new));
    }

    @TearDown(Level.Iteration)
    public void cleanup2() {
        ForcedGcMemoryProfiler.recordUsedMemory();
    }
    
    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void singleConcatMap() {
        singleConcatMapFlux
                .concatMap(integer -> characters.map(character -> character.concat(integer.toString())))
                .then()
                .block();
    }

    @Benchmark
    @Measurement(iterations = 5, time = 20)
    public void multiConcatMap() {
        Flux<String> results =  null;
        for (int i = 0; i < 10; i++) {
            if(results == null) {
                results = multiConcatMapFlux.concatMap(integer -> characters.map(character -> character.concat(integer.toString())));
            } else {
                results = results.concatMap(string -> characters.map(character -> character.concat(string)));
            }
        }
        results.then().block();
    }

    //@Benchmark
    @Measurement(iterations = 5, time = 20)
    public void multiConcatMapEachOnIo() {
        Flux<String> results =  null;
        for (int i = 0; i < 10; i++) {
            if(results == null) {
                results = multiConcatMapEachOnIoFlux
                        .publishOn(Schedulers.elastic())
                        .concatMap(integer -> characters.map(character -> character.concat(integer.toString())));
            } else {
                results = results
                        .publishOn(Schedulers.elastic())
                        .concatMap(string -> characters.map(character -> character.concat(string)));
            }
        }
        results.then().block();
    }

    public static void main(String[] args) {
        //ReactorConcatMap flatConcatMapBenchmark = new ReactorConcatMap();
        //flatConcatMapBenchmark.setup();
        //flatConcatMapBenchmark.multiConcatMap();
    }

}



