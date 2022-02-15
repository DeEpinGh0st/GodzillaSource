package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.MustBeClosed;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;


























































@GwtIncompatible
public abstract class CharSource
{
  @Beta
  public ByteSource asByteSource(Charset charset) {
    return new AsByteSource(charset);
  }


















  
  public BufferedReader openBufferedStream() throws IOException {
    Reader reader = openStream();
    return (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
  }




























  
  @Beta
  @MustBeClosed
  public Stream<String> lines() throws IOException {
    BufferedReader reader = openBufferedStream();
    return reader
      .lines()
      .onClose(() -> {
          
          try {
            reader.close();
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          } 
        });
  }














  
  @Beta
  public Optional<Long> lengthIfKnown() {
    return Optional.absent();
  }



















  
  @Beta
  public long length() throws IOException {
    Optional<Long> lengthIfKnown = lengthIfKnown();
    if (lengthIfKnown.isPresent()) {
      return ((Long)lengthIfKnown.get()).longValue();
    }
    
    Closer closer = Closer.create();
    try {
      Reader reader = closer.<Reader>register(openStream());
      return countBySkipping(reader);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }
  
  private long countBySkipping(Reader reader) throws IOException {
    long count = 0L;
    long read;
    while ((read = reader.skip(Long.MAX_VALUE)) != 0L) {
      count += read;
    }
    return count;
  }








  
  @CanIgnoreReturnValue
  public long copyTo(Appendable appendable) throws IOException {
    Preconditions.checkNotNull(appendable);
    
    Closer closer = Closer.create();
    try {
      Reader reader = closer.<Reader>register(openStream());
      return CharStreams.copy(reader, appendable);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }







  
  @CanIgnoreReturnValue
  public long copyTo(CharSink sink) throws IOException {
    Preconditions.checkNotNull(sink);
    
    Closer closer = Closer.create();
    try {
      Reader reader = closer.<Reader>register(openStream());
      Writer writer = closer.<Writer>register(sink.openStream());
      return CharStreams.copy(reader, writer);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }





  
  public String read() throws IOException {
    Closer closer = Closer.create();
    try {
      Reader reader = closer.<Reader>register(openStream());
      return CharStreams.toString(reader);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }










  
  public String readFirstLine() throws IOException {
    Closer closer = Closer.create();
    try {
      BufferedReader reader = closer.<BufferedReader>register(openBufferedStream());
      return reader.readLine();
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }











  
  public ImmutableList<String> readLines() throws IOException {
    Closer closer = Closer.create();
    try {
      BufferedReader reader = closer.<BufferedReader>register(openBufferedStream());
      List<String> result = Lists.newArrayList();
      String line;
      while ((line = reader.readLine()) != null) {
        result.add(line);
      }
      return ImmutableList.copyOf(result);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }














  
  @Beta
  @CanIgnoreReturnValue
  public <T> T readLines(LineProcessor<T> processor) throws IOException {
    Preconditions.checkNotNull(processor);
    
    Closer closer = Closer.create();
    try {
      Reader reader = closer.<Reader>register(openStream());
      return (T)CharStreams.readLines(reader, (LineProcessor)processor);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }













  
  @Beta
  public void forEachLine(Consumer<? super String> action) throws IOException {
    try (Stream<String> lines = lines()) {
      
      lines.forEachOrdered(action);
    } catch (UncheckedIOException e) {
      throw e.getCause();
    } 
  }












  
  public boolean isEmpty() throws IOException {
    Optional<Long> lengthIfKnown = lengthIfKnown();
    if (lengthIfKnown.isPresent()) {
      return (((Long)lengthIfKnown.get()).longValue() == 0L);
    }
    Closer closer = Closer.create();
    try {
      Reader reader = closer.<Reader>register(openStream());
      return (reader.read() == -1);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }











  
  public static CharSource concat(Iterable<? extends CharSource> sources) {
    return new ConcatenatedCharSource(sources);
  }


















  
  public static CharSource concat(Iterator<? extends CharSource> sources) {
    return concat((Iterable<? extends CharSource>)ImmutableList.copyOf(sources));
  }












  
  public static CharSource concat(CharSource... sources) {
    return concat((Iterable<? extends CharSource>)ImmutableList.copyOf((Object[])sources));
  }







  
  public static CharSource wrap(CharSequence charSequence) {
    return (charSequence instanceof String) ? new StringCharSource((String)charSequence) : new CharSequenceCharSource(charSequence);
  }







  
  public static CharSource empty() {
    return EmptyCharSource.INSTANCE;
  }
  
  public abstract Reader openStream() throws IOException;
  
  private final class AsByteSource extends ByteSource {
    final Charset charset;
    
    AsByteSource(Charset charset) {
      this.charset = (Charset)Preconditions.checkNotNull(charset);
    }

    
    public CharSource asCharSource(Charset charset) {
      if (charset.equals(this.charset)) {
        return CharSource.this;
      }
      return super.asCharSource(charset);
    }

    
    public InputStream openStream() throws IOException {
      return new ReaderInputStream(CharSource.this.openStream(), this.charset, 8192);
    }

    
    public String toString() {
      return CharSource.this.toString() + ".asByteSource(" + this.charset + ")";
    }
  }
  
  private static class CharSequenceCharSource
    extends CharSource {
    private static final Splitter LINE_SPLITTER = Splitter.onPattern("\r\n|\n|\r");
    
    protected final CharSequence seq;
    
    protected CharSequenceCharSource(CharSequence seq) {
      this.seq = (CharSequence)Preconditions.checkNotNull(seq);
    }

    
    public Reader openStream() {
      return new CharSequenceReader(this.seq);
    }

    
    public String read() {
      return this.seq.toString();
    }

    
    public boolean isEmpty() {
      return (this.seq.length() == 0);
    }

    
    public long length() {
      return this.seq.length();
    }

    
    public Optional<Long> lengthIfKnown() {
      return Optional.of(Long.valueOf(this.seq.length()));
    }




    
    private Iterator<String> linesIterator() {
      return (Iterator<String>)new AbstractIterator<String>() {
          Iterator<String> lines = CharSource.CharSequenceCharSource.LINE_SPLITTER.split(CharSource.CharSequenceCharSource.this.seq).iterator();

          
          protected String computeNext() {
            if (this.lines.hasNext()) {
              String next = this.lines.next();
              
              if (this.lines.hasNext() || !next.isEmpty()) {
                return next;
              }
            } 
            return (String)endOfData();
          }
        };
    }

    
    public Stream<String> lines() {
      return Streams.stream(linesIterator());
    }

    
    public String readFirstLine() {
      Iterator<String> lines = linesIterator();
      return lines.hasNext() ? lines.next() : null;
    }

    
    public ImmutableList<String> readLines() {
      return ImmutableList.copyOf(linesIterator());
    }

    
    public <T> T readLines(LineProcessor<T> processor) throws IOException {
      Iterator<String> lines = linesIterator(); do {  }
      while (lines.hasNext() && 
        processor.processLine(lines.next()));


      
      return processor.getResult();
    }

    
    public String toString() {
      return "CharSource.wrap(" + Ascii.truncate(this.seq, 30, "...") + ")";
    }
  }













  
  private static class StringCharSource
    extends CharSequenceCharSource
  {
    protected StringCharSource(String seq) {
      super(seq);
    }

    
    public Reader openStream() {
      return new StringReader((String)this.seq);
    }

    
    public long copyTo(Appendable appendable) throws IOException {
      appendable.append(this.seq);
      return this.seq.length();
    }

    
    public long copyTo(CharSink sink) throws IOException {
      Preconditions.checkNotNull(sink);
      Closer closer = Closer.create();
      try {
        Writer writer = closer.<Writer>register(sink.openStream());
        writer.write((String)this.seq);
        return this.seq.length();
      } catch (Throwable e) {
        throw closer.rethrow(e);
      } finally {
        closer.close();
      } 
    }
  }
  
  private static final class EmptyCharSource
    extends StringCharSource {
    private static final EmptyCharSource INSTANCE = new EmptyCharSource();
    
    private EmptyCharSource() {
      super("");
    }

    
    public String toString() {
      return "CharSource.empty()";
    }
  }
  
  private static final class ConcatenatedCharSource
    extends CharSource {
    private final Iterable<? extends CharSource> sources;
    
    ConcatenatedCharSource(Iterable<? extends CharSource> sources) {
      this.sources = (Iterable<? extends CharSource>)Preconditions.checkNotNull(sources);
    }

    
    public Reader openStream() throws IOException {
      return new MultiReader(this.sources.iterator());
    }

    
    public boolean isEmpty() throws IOException {
      for (CharSource source : this.sources) {
        if (!source.isEmpty()) {
          return false;
        }
      } 
      return true;
    }

    
    public Optional<Long> lengthIfKnown() {
      long result = 0L;
      for (CharSource source : this.sources) {
        Optional<Long> lengthIfKnown = source.lengthIfKnown();
        if (!lengthIfKnown.isPresent()) {
          return Optional.absent();
        }
        result += ((Long)lengthIfKnown.get()).longValue();
      } 
      return Optional.of(Long.valueOf(result));
    }

    
    public long length() throws IOException {
      long result = 0L;
      for (CharSource source : this.sources) {
        result += source.length();
      }
      return result;
    }

    
    public String toString() {
      return "CharSource.concat(" + this.sources + ")";
    }
  }
}
