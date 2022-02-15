package org.springframework.core.io.buffer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.SynchronousSink;
import reactor.util.context.Context;






















public abstract class DataBufferUtils
{
  private static final Log logger = LogFactory.getLog(DataBufferUtils.class);
  
  private static final Consumer<DataBuffer> RELEASE_CONSUMER = DataBufferUtils::release;
















  
  public static Flux<DataBuffer> readInputStream(Callable<InputStream> inputStreamSupplier, DataBufferFactory bufferFactory, int bufferSize) {
    Assert.notNull(inputStreamSupplier, "'inputStreamSupplier' must not be null");
    return readByteChannel(() -> Channels.newChannel(inputStreamSupplier.call()), bufferFactory, bufferSize);
  }











  
  public static Flux<DataBuffer> readByteChannel(Callable<ReadableByteChannel> channelSupplier, DataBufferFactory bufferFactory, int bufferSize) {
    Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
    Assert.notNull(bufferFactory, "'dataBufferFactory' must not be null");
    Assert.isTrue((bufferSize > 0), "'bufferSize' must be > 0");
    
    return Flux.using(channelSupplier, channel -> Flux.generate(new ReadableByteChannelGenerator(channel, bufferFactory, bufferSize)), DataBufferUtils::closeChannel);
  }















  
  public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, DataBufferFactory bufferFactory, int bufferSize) {
    return readAsynchronousFileChannel(channelSupplier, 0L, bufferFactory, bufferSize);
  }













  
  public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, long position, DataBufferFactory bufferFactory, int bufferSize) {
    Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
    Assert.notNull(bufferFactory, "'dataBufferFactory' must not be null");
    Assert.isTrue((position >= 0L), "'position' must be >= 0");
    Assert.isTrue((bufferSize > 0), "'bufferSize' must be > 0");
    
    Flux<DataBuffer> flux = Flux.using(channelSupplier, channel -> Flux.create(()), channel -> {
        
        });








    
    return flux.doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
  }











  
  public static Flux<DataBuffer> read(Path path, DataBufferFactory bufferFactory, int bufferSize, OpenOption... options) {
    Assert.notNull(path, "Path must not be null");
    Assert.notNull(bufferFactory, "BufferFactory must not be null");
    Assert.isTrue((bufferSize > 0), "'bufferSize' must be > 0");
    if (options.length > 0) {
      for (OpenOption option : options) {
        Assert.isTrue((option != StandardOpenOption.APPEND && option != StandardOpenOption.WRITE), "'" + option + "' not allowed");
      }
    }

    
    return readAsynchronousFileChannel(() -> AsynchronousFileChannel.open(path, options), bufferFactory, bufferSize);
  }













  
  public static Flux<DataBuffer> read(Resource resource, DataBufferFactory bufferFactory, int bufferSize) {
    return read(resource, 0L, bufferFactory, bufferSize);
  }
















  
  public static Flux<DataBuffer> read(Resource resource, long position, DataBufferFactory bufferFactory, int bufferSize) {
    try {
      if (resource.isFile()) {
        File file = resource.getFile();
        return readAsynchronousFileChannel(() -> AsynchronousFileChannel.open(file.toPath(), new OpenOption[] { StandardOpenOption.READ }), position, bufferFactory, bufferSize);
      
      }
    
    }
    catch (IOException iOException) {}

    
    Flux<DataBuffer> result = readByteChannel(resource::readableChannel, bufferFactory, bufferSize);
    return (position == 0L) ? result : skipUntilByteCount((Publisher<? extends DataBuffer>)result, position);
  }




















  
  public static Flux<DataBuffer> write(Publisher<DataBuffer> source, OutputStream outputStream) {
    Assert.notNull(source, "'source' must not be null");
    Assert.notNull(outputStream, "'outputStream' must not be null");
    
    WritableByteChannel channel = Channels.newChannel(outputStream);
    return write(source, channel);
  }















  
  public static Flux<DataBuffer> write(Publisher<DataBuffer> source, WritableByteChannel channel) {
    Assert.notNull(source, "'source' must not be null");
    Assert.notNull(channel, "'channel' must not be null");
    
    Flux<DataBuffer> flux = Flux.from(source);
    return Flux.create(sink -> {
          WritableByteChannelSubscriber subscriber = new WritableByteChannelSubscriber(sink, channel);
          sink.onDispose((Disposable)subscriber);
          flux.subscribe((CoreSubscriber)subscriber);
        });
  }
















  
  public static Flux<DataBuffer> write(Publisher<DataBuffer> source, AsynchronousFileChannel channel) {
    return write(source, channel, 0L);
  }


















  
  public static Flux<DataBuffer> write(Publisher<? extends DataBuffer> source, AsynchronousFileChannel channel, long position) {
    Assert.notNull(source, "'source' must not be null");
    Assert.notNull(channel, "'channel' must not be null");
    Assert.isTrue((position >= 0L), "'position' must be >= 0");
    
    Flux<DataBuffer> flux = Flux.from(source);
    return Flux.create(sink -> {
          WriteCompletionHandler handler = new WriteCompletionHandler(sink, channel, position);
          sink.onDispose((Disposable)handler);
          flux.subscribe((CoreSubscriber)handler);
        });
  }















  
  public static Mono<Void> write(Publisher<DataBuffer> source, Path destination, OpenOption... options) {
    Assert.notNull(source, "Source must not be null");
    Assert.notNull(destination, "Destination must not be null");
    
    Set<OpenOption> optionSet = checkWriteOptions(options);
    
    return Mono.create(sink -> {
          try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(destination, optionSet, null, (FileAttribute<?>[])new FileAttribute[0]);

            
            sink.onDispose(());
            
            write(source, channel).subscribe(DataBufferUtils::release, sink::error, sink::success);
          } catch (IOException ex) {
            sink.error(ex);
          } 
        });
  }
  
  private static Set<OpenOption> checkWriteOptions(OpenOption[] options) {
    int length = options.length;
    Set<OpenOption> result = new HashSet<>(length + 3);
    if (length == 0) {
      result.add(StandardOpenOption.CREATE);
      result.add(StandardOpenOption.TRUNCATE_EXISTING);
    } else {
      
      for (OpenOption opt : options) {
        if (opt == StandardOpenOption.READ) {
          throw new IllegalArgumentException("READ not allowed");
        }
        result.add(opt);
      } 
    } 
    result.add(StandardOpenOption.WRITE);
    return result;
  }
  
  static void closeChannel(@Nullable Channel channel) {
    if (channel != null && channel.isOpen()) {
      try {
        channel.close();
      }
      catch (IOException iOException) {}
    }
  }














  
  public static Flux<DataBuffer> takeUntilByteCount(Publisher<? extends DataBuffer> publisher, long maxByteCount) {
    Assert.notNull(publisher, "Publisher must not be null");
    Assert.isTrue((maxByteCount >= 0L), "'maxByteCount' must be a positive number");
    
    return Flux.defer(() -> {
          AtomicLong countDown = new AtomicLong(maxByteCount);
          return (Publisher)Flux.from(publisher).map(()).takeUntil(());
        });
  }





















  
  public static Flux<DataBuffer> skipUntilByteCount(Publisher<? extends DataBuffer> publisher, long maxByteCount) {
    Assert.notNull(publisher, "Publisher must not be null");
    Assert.isTrue((maxByteCount >= 0L), "'maxByteCount' must be a positive number");
    
    return Flux.defer(() -> {
          AtomicLong countDown = new AtomicLong(maxByteCount);















          
          return (Publisher)Flux.from(publisher).skipUntil(()).map(());
        }).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
  }






  
  public static <T extends DataBuffer> T retain(T dataBuffer) {
    if (dataBuffer instanceof PooledDataBuffer) {
      return (T)((PooledDataBuffer)dataBuffer).retain();
    }
    
    return dataBuffer;
  }










  
  public static <T extends DataBuffer> T touch(T dataBuffer, Object hint) {
    if (dataBuffer instanceof PooledDataBuffer) {
      return (T)((PooledDataBuffer)dataBuffer).touch(hint);
    }
    
    return dataBuffer;
  }







  
  public static boolean release(@Nullable DataBuffer dataBuffer) {
    if (dataBuffer instanceof PooledDataBuffer) {
      PooledDataBuffer pooledDataBuffer = (PooledDataBuffer)dataBuffer;
      if (pooledDataBuffer.isAllocated()) {
        try {
          return pooledDataBuffer.release();
        }
        catch (IllegalStateException ex) {
          
          if (logger.isDebugEnabled()) {
            logger.debug("Failed to release PooledDataBuffer: " + dataBuffer, ex);
          }
          return false;
        } 
      }
    } 
    return false;
  }




  
  public static Consumer<DataBuffer> releaseConsumer() {
    return RELEASE_CONSUMER;
  }















  
  public static Mono<DataBuffer> join(Publisher<? extends DataBuffer> dataBuffers) {
    return join(dataBuffers, -1);
  }












  
  public static Mono<DataBuffer> join(Publisher<? extends DataBuffer> buffers, int maxByteCount) {
    Assert.notNull(buffers, "'dataBuffers' must not be null");
    
    if (buffers instanceof Mono) {
      return (Mono)buffers;
    }
    
    return Flux.from(buffers)
      .collect(() -> new LimitedDataBufferList(maxByteCount), LimitedDataBufferList::add)
      .filter(list -> !list.isEmpty())
      .map(list -> list.get(0).factory().join(list))
      .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
  }







  
  public static Matcher matcher(byte[] delimiter) {
    return createMatcher(delimiter);
  }







  
  public static Matcher matcher(byte[]... delimiters) {
    Assert.isTrue((delimiters.length > 0), "Delimiters must not be empty");
    return (delimiters.length == 1) ? createMatcher(delimiters[0]) : new CompositeMatcher(delimiters);
  }
  
  private static NestedMatcher createMatcher(byte[] delimiter) {
    Assert.isTrue((delimiter.length > 0), "Delimiter must not be empty");
    switch (delimiter.length) {
      case 1:
        return (delimiter[0] == 10) ? SingleByteMatcher.NEWLINE_MATCHER : new SingleByteMatcher(delimiter);
      case 2:
        return new TwoByteMatcher(delimiter);
    } 
    return new KnuthMorrisPrattMatcher(delimiter);
  }






  
  public static interface Matcher
  {
    int match(DataBuffer param1DataBuffer);






    
    byte[] delimiter();





    
    void reset();
  }





  
  private static class CompositeMatcher
    implements Matcher
  {
    private static final byte[] NO_DELIMITER = new byte[0];

    
    private final DataBufferUtils.NestedMatcher[] matchers;
    
    byte[] longestDelimiter = NO_DELIMITER;
    
    CompositeMatcher(byte[][] delimiters) {
      this.matchers = initMatchers(delimiters);
    }
    
    private static DataBufferUtils.NestedMatcher[] initMatchers(byte[][] delimiters) {
      DataBufferUtils.NestedMatcher[] matchers = new DataBufferUtils.NestedMatcher[delimiters.length];
      for (int i = 0; i < delimiters.length; i++) {
        matchers[i] = DataBufferUtils.createMatcher(delimiters[i]);
      }
      return matchers;
    }

    
    public int match(DataBuffer dataBuffer) {
      this.longestDelimiter = NO_DELIMITER;
      
      for (int pos = dataBuffer.readPosition(); pos < dataBuffer.writePosition(); pos++) {
        byte b = dataBuffer.getByte(pos);
        
        for (DataBufferUtils.NestedMatcher matcher : this.matchers) {
          if (matcher.match(b) && (matcher.delimiter()).length > this.longestDelimiter.length) {
            this.longestDelimiter = matcher.delimiter();
          }
        } 
        
        if (this.longestDelimiter != NO_DELIMITER) {
          reset();
          return pos;
        } 
      } 
      return -1;
    }

    
    public byte[] delimiter() {
      Assert.state((this.longestDelimiter != NO_DELIMITER), "Illegal state!");
      return this.longestDelimiter;
    }

    
    public void reset() {
      for (DataBufferUtils.NestedMatcher matcher : this.matchers) {
        matcher.reset();
      }
    }
  }






  
  private static interface NestedMatcher
    extends Matcher
  {
    boolean match(byte param1Byte);
  }






  
  private static class SingleByteMatcher
    implements NestedMatcher
  {
    static SingleByteMatcher NEWLINE_MATCHER = new SingleByteMatcher(new byte[] { 10 });
    
    private final byte[] delimiter;
    
    SingleByteMatcher(byte[] delimiter) {
      Assert.isTrue((delimiter.length == 1), "Expected a 1 byte delimiter");
      this.delimiter = delimiter;
    }

    
    public int match(DataBuffer dataBuffer) {
      for (int pos = dataBuffer.readPosition(); pos < dataBuffer.writePosition(); pos++) {
        byte b = dataBuffer.getByte(pos);
        if (match(b)) {
          return pos;
        }
      } 
      return -1;
    }

    
    public boolean match(byte b) {
      return (this.delimiter[0] == b);
    }

    
    public byte[] delimiter() {
      return this.delimiter;
    }


    
    public void reset() {}
  }


  
  private static abstract class AbstractNestedMatcher
    implements NestedMatcher
  {
    private final byte[] delimiter;

    
    private int matches = 0;

    
    protected AbstractNestedMatcher(byte[] delimiter) {
      this.delimiter = delimiter;
    }
    
    protected void setMatches(int index) {
      this.matches = index;
    }
    
    protected int getMatches() {
      return this.matches;
    }

    
    public int match(DataBuffer dataBuffer) {
      for (int pos = dataBuffer.readPosition(); pos < dataBuffer.writePosition(); pos++) {
        byte b = dataBuffer.getByte(pos);
        if (match(b)) {
          reset();
          return pos;
        } 
      } 
      return -1;
    }

    
    public boolean match(byte b) {
      if (b == this.delimiter[this.matches]) {
        this.matches++;
        return (this.matches == (delimiter()).length);
      } 
      return false;
    }

    
    public byte[] delimiter() {
      return this.delimiter;
    }

    
    public void reset() {
      this.matches = 0;
    }
  }




  
  private static class TwoByteMatcher
    extends AbstractNestedMatcher
  {
    protected TwoByteMatcher(byte[] delimiter) {
      super(delimiter);
      Assert.isTrue((delimiter.length == 2), "Expected a 2-byte delimiter");
    }
  }


  
  private static class KnuthMorrisPrattMatcher
    extends AbstractNestedMatcher
  {
    private final int[] table;


    
    public KnuthMorrisPrattMatcher(byte[] delimiter) {
      super(delimiter);
      this.table = longestSuffixPrefixTable(delimiter);
    }
    
    private static int[] longestSuffixPrefixTable(byte[] delimiter) {
      int[] result = new int[delimiter.length];
      result[0] = 0;
      for (int i = 1; i < delimiter.length; i++) {
        int j = result[i - 1];
        while (j > 0 && delimiter[i] != delimiter[j]) {
          j = result[j - 1];
        }
        if (delimiter[i] == delimiter[j]) {
          j++;
        }
        result[i] = j;
      } 
      return result;
    }

    
    public boolean match(byte b) {
      while (getMatches() > 0 && b != delimiter()[getMatches()]) {
        setMatches(this.table[getMatches() - 1]);
      }
      return super.match(b);
    }
  }

  
  private static class ReadableByteChannelGenerator
    implements Consumer<SynchronousSink<DataBuffer>>
  {
    private final ReadableByteChannel channel;
    
    private final DataBufferFactory dataBufferFactory;
    
    private final int bufferSize;

    
    public ReadableByteChannelGenerator(ReadableByteChannel channel, DataBufferFactory dataBufferFactory, int bufferSize) {
      this.channel = channel;
      this.dataBufferFactory = dataBufferFactory;
      this.bufferSize = bufferSize;
    }

    
    public void accept(SynchronousSink<DataBuffer> sink) {
      boolean release = true;
      DataBuffer dataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
      
      try {
        ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, dataBuffer.capacity()); int read;
        if ((read = this.channel.read(byteBuffer)) >= 0) {
          dataBuffer.writePosition(read);
          release = false;
          sink.next(dataBuffer);
        } else {
          
          sink.complete();
        }
      
      } catch (IOException ex) {
        sink.error(ex);
      } finally {
        
        if (release) {
          DataBufferUtils.release(dataBuffer);
        }
      } 
    }
  }

  
  private static class ReadCompletionHandler
    implements CompletionHandler<Integer, DataBuffer>
  {
    private final AsynchronousFileChannel channel;
    
    private final FluxSink<DataBuffer> sink;
    
    private final DataBufferFactory dataBufferFactory;
    
    private final int bufferSize;
    
    private final AtomicLong position;
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);


    
    public ReadCompletionHandler(AsynchronousFileChannel channel, FluxSink<DataBuffer> sink, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
      this.channel = channel;
      this.sink = sink;
      this.position = new AtomicLong(position);
      this.dataBufferFactory = dataBufferFactory;
      this.bufferSize = bufferSize;
    }



    
    public void request(long n) {
      tryRead();
    }



    
    public void cancel() {
      this.state.getAndSet(State.DISPOSED);





      
      DataBufferUtils.closeChannel(this.channel);
    }
    
    private void tryRead() {
      if (this.sink.requestedFromDownstream() > 0L && this.state.compareAndSet(State.IDLE, State.READING)) {
        read();
      }
    }
    
    private void read() {
      DataBuffer dataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
      ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, this.bufferSize);
      this.channel.read(byteBuffer, this.position.get(), dataBuffer, this);
    }

    
    public void completed(Integer read, DataBuffer dataBuffer) {
      if (((State)this.state.get()).equals(State.DISPOSED)) {
        DataBufferUtils.release(dataBuffer);
        DataBufferUtils.closeChannel(this.channel);
        
        return;
      } 
      if (read.intValue() == -1) {
        DataBufferUtils.release(dataBuffer);
        DataBufferUtils.closeChannel(this.channel);
        this.state.set(State.DISPOSED);
        this.sink.complete();
        
        return;
      } 
      this.position.addAndGet(read.intValue());
      dataBuffer.writePosition(read.intValue());
      this.sink.next(dataBuffer);

      
      if (this.sink.requestedFromDownstream() > 0L) {
        read();
        
        return;
      } 
      
      if (this.state.compareAndSet(State.READING, State.IDLE)) {
        tryRead();
      }
    }

    
    public void failed(Throwable exc, DataBuffer dataBuffer) {
      DataBufferUtils.release(dataBuffer);
      DataBufferUtils.closeChannel(this.channel);
      this.state.set(State.DISPOSED);
      this.sink.error(exc);
    }
    
    private enum State {
      IDLE, READING, DISPOSED;
    }
  }

  
  private static class WritableByteChannelSubscriber
    extends BaseSubscriber<DataBuffer>
  {
    private final FluxSink<DataBuffer> sink;
    private final WritableByteChannel channel;
    
    public WritableByteChannelSubscriber(FluxSink<DataBuffer> sink, WritableByteChannel channel) {
      this.sink = sink;
      this.channel = channel;
    }

    
    protected void hookOnSubscribe(Subscription subscription) {
      request(1L);
    }

    
    protected void hookOnNext(DataBuffer dataBuffer) {
      try {
        ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
        while (byteBuffer.hasRemaining()) {
          this.channel.write(byteBuffer);
        }
        this.sink.next(dataBuffer);
        request(1L);
      }
      catch (IOException ex) {
        this.sink.next(dataBuffer);
        this.sink.error(ex);
      } 
    }

    
    protected void hookOnError(Throwable throwable) {
      this.sink.error(throwable);
    }

    
    protected void hookOnComplete() {
      this.sink.complete();
    }

    
    public Context currentContext() {
      return this.sink.currentContext();
    }
  }

  
  private static class WriteCompletionHandler
    extends BaseSubscriber<DataBuffer>
    implements CompletionHandler<Integer, ByteBuffer>
  {
    private final FluxSink<DataBuffer> sink;
    
    private final AsynchronousFileChannel channel;
    
    private final AtomicBoolean completed = new AtomicBoolean();
    
    private final AtomicReference<Throwable> error = new AtomicReference<>();
    
    private final AtomicLong position;
    
    private final AtomicReference<DataBuffer> dataBuffer = new AtomicReference<>();


    
    public WriteCompletionHandler(FluxSink<DataBuffer> sink, AsynchronousFileChannel channel, long position) {
      this.sink = sink;
      this.channel = channel;
      this.position = new AtomicLong(position);
    }

    
    protected void hookOnSubscribe(Subscription subscription) {
      request(1L);
    }

    
    protected void hookOnNext(DataBuffer value) {
      if (!this.dataBuffer.compareAndSet(null, value)) {
        throw new IllegalStateException();
      }
      ByteBuffer byteBuffer = value.asByteBuffer();
      this.channel.write(byteBuffer, this.position.get(), byteBuffer, this);
    }

    
    protected void hookOnError(Throwable throwable) {
      this.error.set(throwable);
      
      if (this.dataBuffer.get() == null) {
        this.sink.error(throwable);
      }
    }

    
    protected void hookOnComplete() {
      this.completed.set(true);
      
      if (this.dataBuffer.get() == null) {
        this.sink.complete();
      }
    }

    
    public void completed(Integer written, ByteBuffer byteBuffer) {
      long pos = this.position.addAndGet(written.intValue());
      if (byteBuffer.hasRemaining()) {
        this.channel.write(byteBuffer, pos, byteBuffer, this);
        return;
      } 
      sinkDataBuffer();
      
      Throwable throwable = this.error.get();
      if (throwable != null) {
        this.sink.error(throwable);
      }
      else if (this.completed.get()) {
        this.sink.complete();
      } else {
        
        request(1L);
      } 
    }

    
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
      sinkDataBuffer();
      this.sink.error(exc);
    }
    
    private void sinkDataBuffer() {
      DataBuffer dataBuffer = this.dataBuffer.get();
      Assert.state((dataBuffer != null), "DataBuffer should not be null");
      this.sink.next(dataBuffer);
      this.dataBuffer.set(null);
    }

    
    public Context currentContext() {
      return this.sink.currentContext();
    }
  }
}
