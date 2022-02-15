package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;




















@CanIgnoreReturnValue
abstract class AbstractHasher
  implements Hasher
{
  public final Hasher putBoolean(boolean b) {
    return putByte(b ? 1 : 0);
  }

  
  public final Hasher putDouble(double d) {
    return putLong(Double.doubleToRawLongBits(d));
  }

  
  public final Hasher putFloat(float f) {
    return putInt(Float.floatToRawIntBits(f));
  }

  
  public Hasher putUnencodedChars(CharSequence charSequence) {
    for (int i = 0, len = charSequence.length(); i < len; i++) {
      putChar(charSequence.charAt(i));
    }
    return this;
  }

  
  public Hasher putString(CharSequence charSequence, Charset charset) {
    return putBytes(charSequence.toString().getBytes(charset));
  }

  
  public Hasher putBytes(byte[] bytes) {
    return putBytes(bytes, 0, bytes.length);
  }

  
  public Hasher putBytes(byte[] bytes, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, bytes.length);
    for (int i = 0; i < len; i++) {
      putByte(bytes[off + i]);
    }
    return this;
  }

  
  public Hasher putBytes(ByteBuffer b) {
    if (b.hasArray()) {
      putBytes(b.array(), b.arrayOffset() + b.position(), b.remaining());
      b.position(b.limit());
    } else {
      for (int remaining = b.remaining(); remaining > 0; remaining--) {
        putByte(b.get());
      }
    } 
    return this;
  }

  
  public Hasher putShort(short s) {
    putByte((byte)s);
    putByte((byte)(s >>> 8));
    return this;
  }

  
  public Hasher putInt(int i) {
    putByte((byte)i);
    putByte((byte)(i >>> 8));
    putByte((byte)(i >>> 16));
    putByte((byte)(i >>> 24));
    return this;
  }

  
  public Hasher putLong(long l) {
    for (int i = 0; i < 64; i += 8) {
      putByte((byte)(int)(l >>> i));
    }
    return this;
  }

  
  public Hasher putChar(char c) {
    putByte((byte)c);
    putByte((byte)(c >>> 8));
    return this;
  }

  
  public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
    funnel.funnel(instance, this);
    return this;
  }
}
