package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;





















@Immutable
abstract class AbstractHashFunction
  implements HashFunction
{
  public <T> HashCode hashObject(T instance, Funnel<? super T> funnel) {
    return newHasher().<T>putObject(instance, funnel).hash();
  }

  
  public HashCode hashUnencodedChars(CharSequence input) {
    int len = input.length();
    return newHasher(len * 2).putUnencodedChars(input).hash();
  }

  
  public HashCode hashString(CharSequence input, Charset charset) {
    return newHasher().putString(input, charset).hash();
  }

  
  public HashCode hashInt(int input) {
    return newHasher(4).putInt(input).hash();
  }

  
  public HashCode hashLong(long input) {
    return newHasher(8).putLong(input).hash();
  }

  
  public HashCode hashBytes(byte[] input) {
    return hashBytes(input, 0, input.length);
  }

  
  public HashCode hashBytes(byte[] input, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, input.length);
    return newHasher(len).putBytes(input, off, len).hash();
  }

  
  public HashCode hashBytes(ByteBuffer input) {
    return newHasher(input.remaining()).putBytes(input).hash();
  }

  
  public Hasher newHasher(int expectedInputSize) {
    Preconditions.checkArgument((expectedInputSize >= 0), "expectedInputSize must be >= 0 but was %s", expectedInputSize);
    
    return newHasher();
  }
}
