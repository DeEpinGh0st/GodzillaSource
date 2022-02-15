package org.springframework.core.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.IntPredicate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
































public class NettyDataBuffer
  implements PooledDataBuffer
{
  private final ByteBuf byteBuf;
  private final NettyDataBufferFactory dataBufferFactory;
  
  NettyDataBuffer(ByteBuf byteBuf, NettyDataBufferFactory dataBufferFactory) {
    Assert.notNull(byteBuf, "ByteBuf must not be null");
    Assert.notNull(dataBufferFactory, "NettyDataBufferFactory must not be null");
    this.byteBuf = byteBuf;
    this.dataBufferFactory = dataBufferFactory;
  }





  
  public ByteBuf getNativeBuffer() {
    return this.byteBuf;
  }

  
  public NettyDataBufferFactory factory() {
    return this.dataBufferFactory;
  }

  
  public int indexOf(IntPredicate predicate, int fromIndex) {
    Assert.notNull(predicate, "IntPredicate must not be null");
    if (fromIndex < 0) {
      fromIndex = 0;
    }
    else if (fromIndex >= this.byteBuf.writerIndex()) {
      return -1;
    } 
    int length = this.byteBuf.writerIndex() - fromIndex;
    return this.byteBuf.forEachByte(fromIndex, length, predicate.negate()::test);
  }

  
  public int lastIndexOf(IntPredicate predicate, int fromIndex) {
    Assert.notNull(predicate, "IntPredicate must not be null");
    if (fromIndex < 0) {
      return -1;
    }
    fromIndex = Math.min(fromIndex, this.byteBuf.writerIndex() - 1);
    return this.byteBuf.forEachByteDesc(0, fromIndex + 1, predicate.negate()::test);
  }

  
  public int readableByteCount() {
    return this.byteBuf.readableBytes();
  }

  
  public int writableByteCount() {
    return this.byteBuf.writableBytes();
  }

  
  public int readPosition() {
    return this.byteBuf.readerIndex();
  }

  
  public NettyDataBuffer readPosition(int readPosition) {
    this.byteBuf.readerIndex(readPosition);
    return this;
  }

  
  public int writePosition() {
    return this.byteBuf.writerIndex();
  }

  
  public NettyDataBuffer writePosition(int writePosition) {
    this.byteBuf.writerIndex(writePosition);
    return this;
  }

  
  public byte getByte(int index) {
    return this.byteBuf.getByte(index);
  }

  
  public int capacity() {
    return this.byteBuf.capacity();
  }

  
  public NettyDataBuffer capacity(int capacity) {
    this.byteBuf.capacity(capacity);
    return this;
  }

  
  public DataBuffer ensureCapacity(int capacity) {
    this.byteBuf.ensureWritable(capacity);
    return this;
  }

  
  public byte read() {
    return this.byteBuf.readByte();
  }

  
  public NettyDataBuffer read(byte[] destination) {
    this.byteBuf.readBytes(destination);
    return this;
  }

  
  public NettyDataBuffer read(byte[] destination, int offset, int length) {
    this.byteBuf.readBytes(destination, offset, length);
    return this;
  }

  
  public NettyDataBuffer write(byte b) {
    this.byteBuf.writeByte(b);
    return this;
  }

  
  public NettyDataBuffer write(byte[] source) {
    this.byteBuf.writeBytes(source);
    return this;
  }

  
  public NettyDataBuffer write(byte[] source, int offset, int length) {
    this.byteBuf.writeBytes(source, offset, length);
    return this;
  }

  
  public NettyDataBuffer write(DataBuffer... buffers) {
    if (!ObjectUtils.isEmpty((Object[])buffers)) {
      if (hasNettyDataBuffers(buffers)) {
        ByteBuf[] nativeBuffers = new ByteBuf[buffers.length];
        for (int i = 0; i < buffers.length; i++) {
          nativeBuffers[i] = ((NettyDataBuffer)buffers[i]).getNativeBuffer();
        }
        write(nativeBuffers);
      } else {
        
        ByteBuffer[] byteBuffers = new ByteBuffer[buffers.length];
        for (int i = 0; i < buffers.length; i++) {
          byteBuffers[i] = buffers[i].asByteBuffer();
        }
        
        write(byteBuffers);
      } 
    }
    return this;
  }
  
  private static boolean hasNettyDataBuffers(DataBuffer[] buffers) {
    for (DataBuffer buffer : buffers) {
      if (!(buffer instanceof NettyDataBuffer)) {
        return false;
      }
    } 
    return true;
  }

  
  public NettyDataBuffer write(ByteBuffer... buffers) {
    if (!ObjectUtils.isEmpty((Object[])buffers)) {
      for (ByteBuffer buffer : buffers) {
        this.byteBuf.writeBytes(buffer);
      }
    }
    return this;
  }






  
  public NettyDataBuffer write(ByteBuf... byteBufs) {
    if (!ObjectUtils.isEmpty((Object[])byteBufs)) {
      for (ByteBuf byteBuf : byteBufs) {
        this.byteBuf.writeBytes(byteBuf);
      }
    }
    return this;
  }

  
  public DataBuffer write(CharSequence charSequence, Charset charset) {
    Assert.notNull(charSequence, "CharSequence must not be null");
    Assert.notNull(charset, "Charset must not be null");
    if (StandardCharsets.UTF_8.equals(charset)) {
      ByteBufUtil.writeUtf8(this.byteBuf, charSequence);
    }
    else if (StandardCharsets.US_ASCII.equals(charset)) {
      ByteBufUtil.writeAscii(this.byteBuf, charSequence);
    } else {
      
      return super.write(charSequence, charset);
    } 
    return this;
  }

  
  public NettyDataBuffer slice(int index, int length) {
    ByteBuf slice = this.byteBuf.slice(index, length);
    return new NettyDataBuffer(slice, this.dataBufferFactory);
  }

  
  public NettyDataBuffer retainedSlice(int index, int length) {
    ByteBuf slice = this.byteBuf.retainedSlice(index, length);
    return new NettyDataBuffer(slice, this.dataBufferFactory);
  }

  
  public ByteBuffer asByteBuffer() {
    return this.byteBuf.nioBuffer();
  }

  
  public ByteBuffer asByteBuffer(int index, int length) {
    return this.byteBuf.nioBuffer(index, length);
  }

  
  public InputStream asInputStream() {
    return (InputStream)new ByteBufInputStream(this.byteBuf);
  }

  
  public InputStream asInputStream(boolean releaseOnClose) {
    return (InputStream)new ByteBufInputStream(this.byteBuf, releaseOnClose);
  }

  
  public OutputStream asOutputStream() {
    return (OutputStream)new ByteBufOutputStream(this.byteBuf);
  }

  
  public String toString(Charset charset) {
    Assert.notNull(charset, "Charset must not be null");
    return this.byteBuf.toString(charset);
  }

  
  public String toString(int index, int length, Charset charset) {
    Assert.notNull(charset, "Charset must not be null");
    return this.byteBuf.toString(index, length, charset);
  }

  
  public boolean isAllocated() {
    return (this.byteBuf.refCnt() > 0);
  }

  
  public PooledDataBuffer retain() {
    return new NettyDataBuffer(this.byteBuf.retain(), this.dataBufferFactory);
  }

  
  public PooledDataBuffer touch(Object hint) {
    this.byteBuf.touch(hint);
    return this;
  }

  
  public boolean release() {
    return this.byteBuf.release();
  }


  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof NettyDataBuffer && this.byteBuf
      .equals(((NettyDataBuffer)other).byteBuf)));
  }

  
  public int hashCode() {
    return this.byteBuf.hashCode();
  }

  
  public String toString() {
    return this.byteBuf.toString();
  }
}
