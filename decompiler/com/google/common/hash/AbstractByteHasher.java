package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

























@CanIgnoreReturnValue
abstract class AbstractByteHasher
  extends AbstractHasher
{
  private final ByteBuffer scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);




  
  protected void update(byte[] b) {
    update(b, 0, b.length);
  }

  
  protected void update(byte[] b, int off, int len) {
    for (int i = off; i < off + len; i++) {
      update(b[i]);
    }
  }

  
  protected void update(ByteBuffer b) {
    if (b.hasArray()) {
      update(b.array(), b.arrayOffset() + b.position(), b.remaining());
      b.position(b.limit());
    } else {
      for (int remaining = b.remaining(); remaining > 0; remaining--) {
        update(b.get());
      }
    } 
  }

  
  private Hasher update(int bytes) {
    try {
      update(this.scratch.array(), 0, bytes);
    } finally {
      this.scratch.clear();
    } 
    return this;
  }

  
  public Hasher putByte(byte b) {
    update(b);
    return this;
  }

  
  public Hasher putBytes(byte[] bytes) {
    Preconditions.checkNotNull(bytes);
    update(bytes);
    return this;
  }

  
  public Hasher putBytes(byte[] bytes, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, bytes.length);
    update(bytes, off, len);
    return this;
  }

  
  public Hasher putBytes(ByteBuffer bytes) {
    update(bytes);
    return this;
  }

  
  public Hasher putShort(short s) {
    this.scratch.putShort(s);
    return update(2);
  }

  
  public Hasher putInt(int i) {
    this.scratch.putInt(i);
    return update(4);
  }

  
  public Hasher putLong(long l) {
    this.scratch.putLong(l);
    return update(8);
  }

  
  public Hasher putChar(char c) {
    this.scratch.putChar(c);
    return update(2);
  }
  
  protected abstract void update(byte paramByte);
}
