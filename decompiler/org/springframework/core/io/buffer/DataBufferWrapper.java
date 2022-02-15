package org.springframework.core.io.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.IntPredicate;
import org.springframework.util.Assert;































public class DataBufferWrapper
  implements DataBuffer
{
  private final DataBuffer delegate;
  
  public DataBufferWrapper(DataBuffer delegate) {
    Assert.notNull(delegate, "Delegate must not be null");
    this.delegate = delegate;
  }



  
  public DataBuffer dataBuffer() {
    return this.delegate;
  }

  
  public DataBufferFactory factory() {
    return this.delegate.factory();
  }

  
  public int indexOf(IntPredicate predicate, int fromIndex) {
    return this.delegate.indexOf(predicate, fromIndex);
  }

  
  public int lastIndexOf(IntPredicate predicate, int fromIndex) {
    return this.delegate.lastIndexOf(predicate, fromIndex);
  }

  
  public int readableByteCount() {
    return this.delegate.readableByteCount();
  }

  
  public int writableByteCount() {
    return this.delegate.writableByteCount();
  }

  
  public int capacity() {
    return this.delegate.capacity();
  }

  
  public DataBuffer capacity(int capacity) {
    return this.delegate.capacity(capacity);
  }

  
  public DataBuffer ensureCapacity(int capacity) {
    return this.delegate.ensureCapacity(capacity);
  }

  
  public int readPosition() {
    return this.delegate.readPosition();
  }

  
  public DataBuffer readPosition(int readPosition) {
    return this.delegate.readPosition(readPosition);
  }

  
  public int writePosition() {
    return this.delegate.writePosition();
  }

  
  public DataBuffer writePosition(int writePosition) {
    return this.delegate.writePosition(writePosition);
  }

  
  public byte getByte(int index) {
    return this.delegate.getByte(index);
  }

  
  public byte read() {
    return this.delegate.read();
  }

  
  public DataBuffer read(byte[] destination) {
    return this.delegate.read(destination);
  }

  
  public DataBuffer read(byte[] destination, int offset, int length) {
    return this.delegate.read(destination, offset, length);
  }

  
  public DataBuffer write(byte b) {
    return this.delegate.write(b);
  }

  
  public DataBuffer write(byte[] source) {
    return this.delegate.write(source);
  }

  
  public DataBuffer write(byte[] source, int offset, int length) {
    return this.delegate.write(source, offset, length);
  }

  
  public DataBuffer write(DataBuffer... buffers) {
    return this.delegate.write(buffers);
  }

  
  public DataBuffer write(ByteBuffer... buffers) {
    return this.delegate.write(buffers);
  }


  
  public DataBuffer write(CharSequence charSequence, Charset charset) {
    return this.delegate.write(charSequence, charset);
  }

  
  public DataBuffer slice(int index, int length) {
    return this.delegate.slice(index, length);
  }

  
  public DataBuffer retainedSlice(int index, int length) {
    return this.delegate.retainedSlice(index, length);
  }

  
  public ByteBuffer asByteBuffer() {
    return this.delegate.asByteBuffer();
  }

  
  public ByteBuffer asByteBuffer(int index, int length) {
    return this.delegate.asByteBuffer(index, length);
  }

  
  public InputStream asInputStream() {
    return this.delegate.asInputStream();
  }

  
  public InputStream asInputStream(boolean releaseOnClose) {
    return this.delegate.asInputStream(releaseOnClose);
  }

  
  public OutputStream asOutputStream() {
    return this.delegate.asOutputStream();
  }

  
  public String toString(Charset charset) {
    return this.delegate.toString(charset);
  }

  
  public String toString(int index, int length, Charset charset) {
    return this.delegate.toString(index, length, charset);
  }
}
