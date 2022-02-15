package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.Immutable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@Immutable
@Beta
public interface HashFunction {
  Hasher newHasher();
  
  Hasher newHasher(int paramInt);
  
  HashCode hashInt(int paramInt);
  
  HashCode hashLong(long paramLong);
  
  HashCode hashBytes(byte[] paramArrayOfbyte);
  
  HashCode hashBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  HashCode hashBytes(ByteBuffer paramByteBuffer);
  
  HashCode hashUnencodedChars(CharSequence paramCharSequence);
  
  HashCode hashString(CharSequence paramCharSequence, Charset paramCharset);
  
  <T> HashCode hashObject(T paramT, Funnel<? super T> paramFunnel);
  
  int bits();
}
