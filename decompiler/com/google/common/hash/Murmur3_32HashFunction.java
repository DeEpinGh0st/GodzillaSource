package com.google.common.hash;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;





































@Immutable
final class Murmur3_32HashFunction
  extends AbstractHashFunction
  implements Serializable
{
  static final HashFunction MURMUR3_32 = new Murmur3_32HashFunction(0);
  
  static final HashFunction GOOD_FAST_HASH_32 = new Murmur3_32HashFunction(Hashing.GOOD_FAST_HASH_SEED);
  
  private static final int CHUNK_SIZE = 4;
  
  private static final int C1 = -862048943;
  
  private static final int C2 = 461845907;
  private final int seed;
  private static final long serialVersionUID = 0L;
  
  Murmur3_32HashFunction(int seed) {
    this.seed = seed;
  }

  
  public int bits() {
    return 32;
  }

  
  public Hasher newHasher() {
    return new Murmur3_32Hasher(this.seed);
  }

  
  public String toString() {
    return "Hashing.murmur3_32(" + this.seed + ")";
  }

  
  public boolean equals(Object object) {
    if (object instanceof Murmur3_32HashFunction) {
      Murmur3_32HashFunction other = (Murmur3_32HashFunction)object;
      return (this.seed == other.seed);
    } 
    return false;
  }

  
  public int hashCode() {
    return getClass().hashCode() ^ this.seed;
  }

  
  public HashCode hashInt(int input) {
    int k1 = mixK1(input);
    int h1 = mixH1(this.seed, k1);
    
    return fmix(h1, 4);
  }

  
  public HashCode hashLong(long input) {
    int low = (int)input;
    int high = (int)(input >>> 32L);
    
    int k1 = mixK1(low);
    int h1 = mixH1(this.seed, k1);
    
    k1 = mixK1(high);
    h1 = mixH1(h1, k1);
    
    return fmix(h1, 8);
  }

  
  public HashCode hashUnencodedChars(CharSequence input) {
    int h1 = this.seed;

    
    for (int i = 1; i < input.length(); i += 2) {
      int k1 = input.charAt(i - 1) | input.charAt(i) << 16;
      k1 = mixK1(k1);
      h1 = mixH1(h1, k1);
    } 

    
    if ((input.length() & 0x1) == 1) {
      int k1 = input.charAt(input.length() - 1);
      k1 = mixK1(k1);
      h1 ^= k1;
    } 
    
    return fmix(h1, 2 * input.length());
  }


  
  public HashCode hashString(CharSequence input, Charset charset) {
    if (Charsets.UTF_8.equals(charset)) {
      int utf16Length = input.length();
      int h1 = this.seed;
      int i = 0;
      int len = 0;

      
      while (i + 4 <= utf16Length) {
        char c0 = input.charAt(i);
        char c1 = input.charAt(i + 1);
        char c2 = input.charAt(i + 2);
        char c3 = input.charAt(i + 3);
        if (c0 < '' && c1 < '' && c2 < '' && c3 < '') {
          int j = c0 | c1 << 8 | c2 << 16 | c3 << 24;
          j = mixK1(j);
          h1 = mixH1(h1, j);
          i += 4;
          len += 4;
        } 
      } 


      
      long buffer = 0L;
      int shift = 0;
      for (; i < utf16Length; i++) {
        char c = input.charAt(i);
        if (c < '') {
          buffer |= c << shift;
          shift += 8;
          len++;
        } else if (c < 'ࠀ') {
          buffer |= charToTwoUtf8Bytes(c) << shift;
          shift += 16;
          len += 2;
        } else if (c < '?' || c > '?') {
          buffer |= charToThreeUtf8Bytes(c) << shift;
          shift += 24;
          len += 3;
        } else {
          int codePoint = Character.codePointAt(input, i);
          if (codePoint == c)
          {
            return hashBytes(input.toString().getBytes(charset));
          }
          i++;
          buffer |= codePointToFourUtf8Bytes(codePoint) << shift;
          len += 4;
        } 
        
        if (shift >= 32) {
          int j = mixK1((int)buffer);
          h1 = mixH1(h1, j);
          buffer >>>= 32L;
          shift -= 32;
        } 
      } 
      
      int k1 = mixK1((int)buffer);
      h1 ^= k1;
      return fmix(h1, len);
    } 
    return hashBytes(input.toString().getBytes(charset));
  }


  
  public HashCode hashBytes(byte[] input, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, input.length);
    int h1 = this.seed;
    int i;
    for (i = 0; i + 4 <= len; i += 4) {
      int j = mixK1(getIntLittleEndian(input, off + i));
      h1 = mixH1(h1, j);
    } 
    
    int k1 = 0;
    for (int shift = 0; i < len; i++, shift += 8) {
      k1 ^= UnsignedBytes.toInt(input[off + i]) << shift;
    }
    h1 ^= mixK1(k1);
    return fmix(h1, len);
  }
  
  private static int getIntLittleEndian(byte[] input, int offset) {
    return Ints.fromBytes(input[offset + 3], input[offset + 2], input[offset + 1], input[offset]);
  }
  
  private static int mixK1(int k1) {
    k1 *= -862048943;
    k1 = Integer.rotateLeft(k1, 15);
    k1 *= 461845907;
    return k1;
  }
  
  private static int mixH1(int h1, int k1) {
    h1 ^= k1;
    h1 = Integer.rotateLeft(h1, 13);
    h1 = h1 * 5 + -430675100;
    return h1;
  }

  
  private static HashCode fmix(int h1, int length) {
    h1 ^= length;
    h1 ^= h1 >>> 16;
    h1 *= -2048144789;
    h1 ^= h1 >>> 13;
    h1 *= -1028477387;
    h1 ^= h1 >>> 16;
    return HashCode.fromInt(h1);
  }
  
  @CanIgnoreReturnValue
  private static final class Murmur3_32Hasher extends AbstractHasher {
    private int h1;
    private long buffer;
    private int shift;
    private int length;
    private boolean isDone;
    
    Murmur3_32Hasher(int seed) {
      this.h1 = seed;
      this.length = 0;
      this.isDone = false;
    }

    
    private void update(int nBytes, long update) {
      this.buffer |= (update & 0xFFFFFFFFL) << this.shift;
      this.shift += nBytes * 8;
      this.length += nBytes;
      
      if (this.shift >= 32) {
        this.h1 = Murmur3_32HashFunction.mixH1(this.h1, Murmur3_32HashFunction.mixK1((int)this.buffer));
        this.buffer >>>= 32L;
        this.shift -= 32;
      } 
    }

    
    public Hasher putByte(byte b) {
      update(1, (b & 0xFF));
      return this;
    }

    
    public Hasher putBytes(byte[] bytes, int off, int len) {
      Preconditions.checkPositionIndexes(off, off + len, bytes.length);
      int i;
      for (i = 0; i + 4 <= len; i += 4) {
        update(4, Murmur3_32HashFunction.getIntLittleEndian(bytes, off + i));
      }
      for (; i < len; i++) {
        putByte(bytes[off + i]);
      }
      return this;
    }

    
    public Hasher putBytes(ByteBuffer buffer) {
      ByteOrder bo = buffer.order();
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      while (buffer.remaining() >= 4) {
        putInt(buffer.getInt());
      }
      while (buffer.hasRemaining()) {
        putByte(buffer.get());
      }
      buffer.order(bo);
      return this;
    }

    
    public Hasher putInt(int i) {
      update(4, i);
      return this;
    }

    
    public Hasher putLong(long l) {
      update(4, (int)l);
      update(4, l >>> 32L);
      return this;
    }

    
    public Hasher putChar(char c) {
      update(2, c);
      return this;
    }


    
    public Hasher putString(CharSequence input, Charset charset) {
      if (Charsets.UTF_8.equals(charset)) {
        int utf16Length = input.length();
        int i = 0;

        
        while (i + 4 <= utf16Length) {
          char c0 = input.charAt(i);
          char c1 = input.charAt(i + 1);
          char c2 = input.charAt(i + 2);
          char c3 = input.charAt(i + 3);
          if (c0 < '' && c1 < '' && c2 < '' && c3 < '') {
            update(4, (c0 | c1 << 8 | c2 << 16 | c3 << 24));
            i += 4;
          } 
        } 


        
        for (; i < utf16Length; i++) {
          char c = input.charAt(i);
          if (c < '') {
            update(1, c);
          } else if (c < 'ࠀ') {
            update(2, Murmur3_32HashFunction.charToTwoUtf8Bytes(c));
          } else if (c < '?' || c > '?') {
            update(3, Murmur3_32HashFunction.charToThreeUtf8Bytes(c));
          } else {
            int codePoint = Character.codePointAt(input, i);
            if (codePoint == c) {
              
              putBytes(input.subSequence(i, utf16Length).toString().getBytes(charset));
              return this;
            } 
            i++;
            update(4, Murmur3_32HashFunction.codePointToFourUtf8Bytes(codePoint));
          } 
        } 
        return this;
      } 
      return super.putString(input, charset);
    }


    
    public HashCode hash() {
      Preconditions.checkState(!this.isDone);
      this.isDone = true;
      this.h1 ^= Murmur3_32HashFunction.mixK1((int)this.buffer);
      return Murmur3_32HashFunction.fmix(this.h1, this.length);
    }
  }
  
  private static long codePointToFourUtf8Bytes(int codePoint) {
    return (0xF0L | (codePoint >>> 18)) & 0xFFL | (0x80L | (0x3F & codePoint >>> 12)) << 8L | (0x80L | (0x3F & codePoint >>> 6)) << 16L | (0x80L | (0x3F & codePoint)) << 24L;
  }



  
  private static long charToThreeUtf8Bytes(char c) {
    return ((0x1E0 | c >>> 12) & 0xFF | (0x80 | 0x3F & c >>> 6) << 8 | (0x80 | 0x3F & c) << 16);
  }


  
  private static long charToTwoUtf8Bytes(char c) {
    return ((0x3C0 | c >>> 6) & 0xFF | (0x80 | 0x3F & c) << 8);
  }
}
