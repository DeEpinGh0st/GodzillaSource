package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.stream.Stream;































































@GwtIncompatible
public abstract class CharSink
{
  public abstract Writer openStream() throws IOException;
  
  public Writer openBufferedStream() throws IOException {
    Writer writer = openStream();
    return (writer instanceof BufferedWriter) ? writer : new BufferedWriter(writer);
  }







  
  public void write(CharSequence charSequence) throws IOException {
    Preconditions.checkNotNull(charSequence);
    
    Closer closer = Closer.create();
    try {
      Writer out = closer.<Writer>register(openStream());
      out.append(charSequence);
      out.flush();
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }







  
  public void writeLines(Iterable<? extends CharSequence> lines) throws IOException {
    writeLines(lines, System.getProperty("line.separator"));
  }







  
  public void writeLines(Iterable<? extends CharSequence> lines, String lineSeparator) throws IOException {
    writeLines(lines.iterator(), lineSeparator);
  }








  
  @Beta
  public void writeLines(Stream<? extends CharSequence> lines) throws IOException {
    writeLines(lines, System.getProperty("line.separator"));
  }








  
  @Beta
  public void writeLines(Stream<? extends CharSequence> lines, String lineSeparator) throws IOException {
    writeLines(lines.iterator(), lineSeparator);
  }

  
  private void writeLines(Iterator<? extends CharSequence> lines, String lineSeparator) throws IOException {
    Preconditions.checkNotNull(lineSeparator);
    
    try (Writer out = openBufferedStream()) {
      while (lines.hasNext()) {
        out.append(lines.next()).append(lineSeparator);
      }
    } 
  }








  
  @CanIgnoreReturnValue
  public long writeFrom(Readable readable) throws IOException {
    Preconditions.checkNotNull(readable);
    
    Closer closer = Closer.create();
    try {
      Writer out = closer.<Writer>register(openStream());
      long written = CharStreams.copy(readable, out);
      out.flush();
      return written;
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }
}
