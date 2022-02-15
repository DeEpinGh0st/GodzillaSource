package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;


















































@GwtIncompatible
public abstract class ByteSource
{
  public CharSource asCharSource(Charset charset) {
    return new AsCharSource(charset);
  }










  
  public abstract InputStream openStream() throws IOException;










  
  public InputStream openBufferedStream() throws IOException {
    InputStream in = openStream();
    return (in instanceof BufferedInputStream) ? in : new BufferedInputStream(in);
  }











  
  public ByteSource slice(long offset, long length) {
    return new SlicedByteSource(offset, length);
  }













  
  public boolean isEmpty() throws IOException {
    Optional<Long> sizeIfKnown = sizeIfKnown();
    if (sizeIfKnown.isPresent()) {
      return (((Long)sizeIfKnown.get()).longValue() == 0L);
    }
    Closer closer = Closer.create();
    try {
      InputStream in = closer.<InputStream>register(openStream());
      return (in.read() == -1);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }














  
  @Beta
  public Optional<Long> sizeIfKnown() {
    return Optional.absent();
  }



















  
  public long size() throws IOException {
    Optional<Long> sizeIfKnown = sizeIfKnown();
    if (sizeIfKnown.isPresent()) {
      return ((Long)sizeIfKnown.get()).longValue();
    }
    
    Closer closer = Closer.create();
    try {
      InputStream in = closer.<InputStream>register(openStream());
      return countBySkipping(in);
    } catch (IOException iOException) {
    
    } finally {
      closer.close();
    } 
    
    closer = Closer.create();
    try {
      InputStream in = closer.<InputStream>register(openStream());
      return ByteStreams.exhaust(in);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }




  
  private long countBySkipping(InputStream in) throws IOException {
    long count = 0L;
    long skipped;
    while ((skipped = ByteStreams.skipUpTo(in, 2147483647L)) > 0L) {
      count += skipped;
    }
    return count;
  }








  
  @CanIgnoreReturnValue
  public long copyTo(OutputStream output) throws IOException {
    Preconditions.checkNotNull(output);
    
    Closer closer = Closer.create();
    try {
      InputStream in = closer.<InputStream>register(openStream());
      return ByteStreams.copy(in, output);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }







  
  @CanIgnoreReturnValue
  public long copyTo(ByteSink sink) throws IOException {
    Preconditions.checkNotNull(sink);
    
    Closer closer = Closer.create();
    try {
      InputStream in = closer.<InputStream>register(openStream());
      OutputStream out = closer.<OutputStream>register(sink.openStream());
      return ByteStreams.copy(in, out);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }





  
  public byte[] read() throws IOException {
    Closer closer = Closer.create();
    try {
      InputStream in = closer.<InputStream>register(openStream());
      Optional<Long> size = sizeIfKnown();
      return size.isPresent() ? 
        ByteStreams.toByteArray(in, ((Long)size.get()).longValue()) : 
        ByteStreams.toByteArray(in);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }









  
  @Beta
  @CanIgnoreReturnValue
  public <T> T read(ByteProcessor<T> processor) throws IOException {
    Preconditions.checkNotNull(processor);
    
    Closer closer = Closer.create();
    try {
      InputStream in = closer.<InputStream>register(openStream());
      return (T)ByteStreams.readBytes(in, (ByteProcessor)processor);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }





  
  public HashCode hash(HashFunction hashFunction) throws IOException {
    Hasher hasher = hashFunction.newHasher();
    copyTo(Funnels.asOutputStream((PrimitiveSink)hasher));
    return hasher.hash();
  }






  
  public boolean contentEquals(ByteSource other) throws IOException {
    Preconditions.checkNotNull(other);
    
    byte[] buf1 = ByteStreams.createBuffer();
    byte[] buf2 = ByteStreams.createBuffer();
    
    Closer closer = Closer.create();
    try {
      InputStream in1 = closer.<InputStream>register(openStream());
      InputStream in2 = closer.<InputStream>register(other.openStream());
      while (true) {
        int read1 = ByteStreams.read(in1, buf1, 0, buf1.length);
        int read2 = ByteStreams.read(in2, buf2, 0, buf2.length);
        if (read1 != read2 || !Arrays.equals(buf1, buf2))
          return false; 
        if (read1 != buf1.length) {
          return true;
        }
      } 
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }











  
  public static ByteSource concat(Iterable<? extends ByteSource> sources) {
    return new ConcatenatedByteSource(sources);
  }


















  
  public static ByteSource concat(Iterator<? extends ByteSource> sources) {
    return concat((Iterable<? extends ByteSource>)ImmutableList.copyOf(sources));
  }












  
  public static ByteSource concat(ByteSource... sources) {
    return concat((Iterable<? extends ByteSource>)ImmutableList.copyOf((Object[])sources));
  }






  
  public static ByteSource wrap(byte[] b) {
    return new ByteArrayByteSource(b);
  }





  
  public static ByteSource empty() {
    return EmptyByteSource.INSTANCE;
  }

  
  class AsCharSource
    extends CharSource
  {
    final Charset charset;

    
    AsCharSource(Charset charset) {
      this.charset = (Charset)Preconditions.checkNotNull(charset);
    }

    
    public ByteSource asByteSource(Charset charset) {
      if (charset.equals(this.charset)) {
        return ByteSource.this;
      }
      return super.asByteSource(charset);
    }

    
    public Reader openStream() throws IOException {
      return new InputStreamReader(ByteSource.this.openStream(), this.charset);
    }








    
    public String read() throws IOException {
      return new String(ByteSource.this.read(), this.charset);
    }

    
    public String toString() {
      return ByteSource.this.toString() + ".asCharSource(" + this.charset + ")";
    }
  }
  
  private final class SlicedByteSource
    extends ByteSource
  {
    final long offset;
    final long length;
    
    SlicedByteSource(long offset, long length) {
      Preconditions.checkArgument((offset >= 0L), "offset (%s) may not be negative", offset);
      Preconditions.checkArgument((length >= 0L), "length (%s) may not be negative", length);
      this.offset = offset;
      this.length = length;
    }

    
    public InputStream openStream() throws IOException {
      return sliceStream(ByteSource.this.openStream());
    }

    
    public InputStream openBufferedStream() throws IOException {
      return sliceStream(ByteSource.this.openBufferedStream());
    }
    
    private InputStream sliceStream(InputStream in) throws IOException {
      if (this.offset > 0L) {
        long skipped;
        try {
          skipped = ByteStreams.skipUpTo(in, this.offset);
        } catch (Throwable e) {
          Closer closer = Closer.create();
          closer.register(in);
          try {
            throw closer.rethrow(e);
          } finally {
            closer.close();
          } 
        } 
        
        if (skipped < this.offset) {
          
          in.close();
          return new ByteArrayInputStream(new byte[0]);
        } 
      } 
      return ByteStreams.limit(in, this.length);
    }

    
    public ByteSource slice(long offset, long length) {
      Preconditions.checkArgument((offset >= 0L), "offset (%s) may not be negative", offset);
      Preconditions.checkArgument((length >= 0L), "length (%s) may not be negative", length);
      long maxLength = this.length - offset;
      return ByteSource.this.slice(this.offset + offset, Math.min(length, maxLength));
    }

    
    public boolean isEmpty() throws IOException {
      return (this.length == 0L || super.isEmpty());
    }

    
    public Optional<Long> sizeIfKnown() {
      Optional<Long> optionalUnslicedSize = ByteSource.this.sizeIfKnown();
      if (optionalUnslicedSize.isPresent()) {
        long unslicedSize = ((Long)optionalUnslicedSize.get()).longValue();
        long off = Math.min(this.offset, unslicedSize);
        return Optional.of(Long.valueOf(Math.min(this.length, unslicedSize - off)));
      } 
      return Optional.absent();
    }

    
    public String toString() {
      return ByteSource.this.toString() + ".slice(" + this.offset + ", " + this.length + ")";
    }
  }
  
  private static class ByteArrayByteSource
    extends ByteSource {
    final byte[] bytes;
    final int offset;
    final int length;
    
    ByteArrayByteSource(byte[] bytes) {
      this(bytes, 0, bytes.length);
    }

    
    ByteArrayByteSource(byte[] bytes, int offset, int length) {
      this.bytes = bytes;
      this.offset = offset;
      this.length = length;
    }

    
    public InputStream openStream() {
      return new ByteArrayInputStream(this.bytes, this.offset, this.length);
    }

    
    public InputStream openBufferedStream() throws IOException {
      return openStream();
    }

    
    public boolean isEmpty() {
      return (this.length == 0);
    }

    
    public long size() {
      return this.length;
    }

    
    public Optional<Long> sizeIfKnown() {
      return Optional.of(Long.valueOf(this.length));
    }

    
    public byte[] read() {
      return Arrays.copyOfRange(this.bytes, this.offset, this.offset + this.length);
    }


    
    public <T> T read(ByteProcessor<T> processor) throws IOException {
      processor.processBytes(this.bytes, this.offset, this.length);
      return processor.getResult();
    }

    
    public long copyTo(OutputStream output) throws IOException {
      output.write(this.bytes, this.offset, this.length);
      return this.length;
    }

    
    public HashCode hash(HashFunction hashFunction) throws IOException {
      return hashFunction.hashBytes(this.bytes, this.offset, this.length);
    }

    
    public ByteSource slice(long offset, long length) {
      Preconditions.checkArgument((offset >= 0L), "offset (%s) may not be negative", offset);
      Preconditions.checkArgument((length >= 0L), "length (%s) may not be negative", length);
      
      offset = Math.min(offset, this.length);
      length = Math.min(length, this.length - offset);
      int newOffset = this.offset + (int)offset;
      return new ByteArrayByteSource(this.bytes, newOffset, (int)length);
    }

    
    public String toString() {
      return "ByteSource.wrap(" + 
        Ascii.truncate(BaseEncoding.base16().encode(this.bytes, this.offset, this.length), 30, "...") + ")";
    }
  }
  
  private static final class EmptyByteSource
    extends ByteArrayByteSource
  {
    static final EmptyByteSource INSTANCE = new EmptyByteSource();
    
    EmptyByteSource() {
      super(new byte[0]);
    }

    
    public CharSource asCharSource(Charset charset) {
      Preconditions.checkNotNull(charset);
      return CharSource.empty();
    }

    
    public byte[] read() {
      return this.bytes;
    }

    
    public String toString() {
      return "ByteSource.empty()";
    }
  }
  
  private static final class ConcatenatedByteSource
    extends ByteSource {
    final Iterable<? extends ByteSource> sources;
    
    ConcatenatedByteSource(Iterable<? extends ByteSource> sources) {
      this.sources = (Iterable<? extends ByteSource>)Preconditions.checkNotNull(sources);
    }

    
    public InputStream openStream() throws IOException {
      return new MultiInputStream(this.sources.iterator());
    }

    
    public boolean isEmpty() throws IOException {
      for (ByteSource source : this.sources) {
        if (!source.isEmpty()) {
          return false;
        }
      } 
      return true;
    }

    
    public Optional<Long> sizeIfKnown() {
      if (!(this.sources instanceof java.util.Collection))
      {



        
        return Optional.absent();
      }
      long result = 0L;
      for (ByteSource source : this.sources) {
        Optional<Long> sizeIfKnown = source.sizeIfKnown();
        if (!sizeIfKnown.isPresent()) {
          return Optional.absent();
        }
        result += ((Long)sizeIfKnown.get()).longValue();
        if (result < 0L)
        {



          
          return Optional.of(Long.valueOf(Long.MAX_VALUE));
        }
      } 
      return Optional.of(Long.valueOf(result));
    }

    
    public long size() throws IOException {
      long result = 0L;
      for (ByteSource source : this.sources) {
        result += source.size();
        if (result < 0L)
        {



          
          return Long.MAX_VALUE;
        }
      } 
      return result;
    }

    
    public String toString() {
      return "ByteSource.concat(" + this.sources + ")";
    }
  }
}
