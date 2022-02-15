package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


































@CanIgnoreReturnValue
abstract class AbstractStreamingHasher
  extends AbstractHasher
{
  private final ByteBuffer buffer;
  private final int bufferSize;
  private final int chunkSize;
  
  protected AbstractStreamingHasher(int chunkSize) {
    this(chunkSize, chunkSize);
  }










  
  protected AbstractStreamingHasher(int chunkSize, int bufferSize) {
    Preconditions.checkArgument((bufferSize % chunkSize == 0));


    
    this.buffer = ByteBuffer.allocate(bufferSize + 7).order(ByteOrder.LITTLE_ENDIAN);
    this.bufferSize = bufferSize;
    this.chunkSize = chunkSize;
  }









  
  protected void processRemaining(ByteBuffer bb) {
    bb.position(bb.limit());
    bb.limit(this.chunkSize + 7);
    while (bb.position() < this.chunkSize) {
      bb.putLong(0L);
    }
    bb.limit(this.chunkSize);
    bb.flip();
    process(bb);
  }

  
  public final Hasher putBytes(byte[] bytes, int off, int len) {
    return putBytesInternal(ByteBuffer.wrap(bytes, off, len).order(ByteOrder.LITTLE_ENDIAN));
  }

  
  public final Hasher putBytes(ByteBuffer readBuffer) {
    ByteOrder order = readBuffer.order();
    try {
      readBuffer.order(ByteOrder.LITTLE_ENDIAN);
      return putBytesInternal(readBuffer);
    } finally {
      readBuffer.order(order);
    } 
  }

  
  private Hasher putBytesInternal(ByteBuffer readBuffer) {
    if (readBuffer.remaining() <= this.buffer.remaining()) {
      this.buffer.put(readBuffer);
      munchIfFull();
      return this;
    } 

    
    int bytesToCopy = this.bufferSize - this.buffer.position();
    for (int i = 0; i < bytesToCopy; i++) {
      this.buffer.put(readBuffer.get());
    }
    munch();

    
    while (readBuffer.remaining() >= this.chunkSize) {
      process(readBuffer);
    }

    
    this.buffer.put(readBuffer);
    return this;
  }











  
  public final Hasher putByte(byte b) {
    this.buffer.put(b);
    munchIfFull();
    return this;
  }

  
  public final Hasher putShort(short s) {
    this.buffer.putShort(s);
    munchIfFull();
    return this;
  }

  
  public final Hasher putChar(char c) {
    this.buffer.putChar(c);
    munchIfFull();
    return this;
  }

  
  public final Hasher putInt(int i) {
    this.buffer.putInt(i);
    munchIfFull();
    return this;
  }

  
  public final Hasher putLong(long l) {
    this.buffer.putLong(l);
    munchIfFull();
    return this;
  }

  
  public final HashCode hash() {
    munch();
    this.buffer.flip();
    if (this.buffer.remaining() > 0) {
      processRemaining(this.buffer);
      this.buffer.position(this.buffer.limit());
    } 
    return makeHash();
  }








  
  private void munchIfFull() {
    if (this.buffer.remaining() < 8)
    {
      munch();
    }
  }
  
  private void munch() {
    this.buffer.flip();
    while (this.buffer.remaining() >= this.chunkSize)
    {
      
      process(this.buffer);
    }
    this.buffer.compact();
  }
  
  protected abstract void process(ByteBuffer paramByteBuffer);
  
  protected abstract HashCode makeHash();
}
