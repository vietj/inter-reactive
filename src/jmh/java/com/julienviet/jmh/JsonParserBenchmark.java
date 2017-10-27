package com.julienviet.jmh;

import com.julienviet.interreactive.bufferedjsonparser.CoroutineJsonParser;
import com.julienviet.interreactive.bufferedjsonparser.SynchronousJsonParser;
import com.julienviet.interreactive.jsonparser.JsonEvent;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.core.parsetools.impl.JsonParserImpl;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 20, time = 1)
@Measurement(iterations = 10, time = 2)
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@Fork(value = 1, jvmArgs = {
  "-XX:+UseBiasedLocking",
  "-XX:BiasedLockingStartupDelay=0",
  "-XX:+AggressiveOpts"
})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class JsonParserBenchmark {

  private Buffer buffer;
  private byte[] bytes;
  private static Function1<JsonEvent, Unit> kotlinConsume = JsonParserBenchmark::kotlinConsume;
  private static Handler<io.vertx.core.parsetools.JsonEvent> vertxConsume = JsonParserBenchmark::vertxConsume;
  private static Method parse;

  static {
    try {
      parse = JsonParserImpl.class.getDeclaredMethod("handle", byte[].class);
      parse.setAccessible(true); // We still can dude!!!! :-D
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public static Unit kotlinConsume(final JsonEvent event) {
    return null;
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public static Unit vertxConsume(final io.vertx.core.parsetools.JsonEvent event) {
    return null;
  }

  @Setup
  public void setup() {
    buffer = Buffer.buffer("{}");
    bytes = buffer.getBytes();
  }

  @Benchmark
  public void synchronousJsonParser() throws Exception {
    SynchronousJsonParser parser = new SynchronousJsonParser(kotlinConsume);
    parser.parse(buffer);
  }

  @Benchmark
  public void coroutineJsonParser() throws Exception {
    CoroutineJsonParser parser = new CoroutineJsonParser(kotlinConsume);
    parser.parseBlocking(buffer);
  }

  @Benchmark
  public void reactiveJsonParser() throws Exception {
    JsonParser parser = JsonParser.newParser();
    parser.handler(vertxConsume);
    parse.invoke(parser, bytes);
  }
}
