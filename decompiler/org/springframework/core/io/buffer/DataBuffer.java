package org.springframework.core.io.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.function.IntPredicate;
import org.springframework.util.Assert;
































































































public interface DataBuffer
{
  DataBufferFactory factory();
  
  int indexOf(IntPredicate paramIntPredicate, int paramInt);
  
  int lastIndexOf(IntPredicate paramIntPredicate, int paramInt);
  
  int readableByteCount();
  
  int writableByteCount();
  
  int capacity();
  
  DataBuffer capacity(int paramInt);
  
  default DataBuffer ensureCapacity(int capacity) {
    return this;
  }







  
  int readPosition();







  
  DataBuffer readPosition(int paramInt);







  
  int writePosition();







  
  DataBuffer writePosition(int paramInt);







  
  byte getByte(int paramInt);







  
  byte read();







  
  DataBuffer read(byte[] paramArrayOfbyte);






  
  DataBuffer read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);






  
  DataBuffer write(byte paramByte);






  
  DataBuffer write(byte[] paramArrayOfbyte);






  
  DataBuffer write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);






  
  DataBuffer write(DataBuffer... paramVarArgs);






  
  DataBuffer write(ByteBuffer... paramVarArgs);






  
  default DataBuffer write(CharSequence charSequence, Charset charset) {
    Assert.notNull(charSequence, "CharSequence must not be null");
    Assert.notNull(charset, "Charset must not be null");
    if (charSequence.length() != 0) {

      
      CharsetEncoder charsetEncoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      CharBuffer inBuffer = CharBuffer.wrap(charSequence);
      int estimatedSize = (int)(inBuffer.remaining() * charsetEncoder.averageBytesPerChar());
      
      ByteBuffer outBuffer = ensureCapacity(estimatedSize).asByteBuffer(writePosition(), writableByteCount());
      
      while (true) {
        CoderResult cr = inBuffer.hasRemaining() ? charsetEncoder.encode(inBuffer, outBuffer, true) : CoderResult.UNDERFLOW;
        if (cr.isUnderflow()) {
          cr = charsetEncoder.flush(outBuffer);
        }
        if (cr.isUnderflow()) {
          break;
        }
        if (cr.isOverflow()) {
          writePosition(writePosition() + outBuffer.position());
          int maximumSize = (int)(inBuffer.remaining() * charsetEncoder.maxBytesPerChar());
          ensureCapacity(maximumSize);
          outBuffer = asByteBuffer(writePosition(), writableByteCount());
        } 
      } 
      writePosition(writePosition() + outBuffer.position());
    } 
    return this;
  }













  
  DataBuffer slice(int paramInt1, int paramInt2);












  
  default DataBuffer retainedSlice(int index, int length) {
    return DataBufferUtils.retain(slice(index, length));
  }








  
  ByteBuffer asByteBuffer();








  
  ByteBuffer asByteBuffer(int paramInt1, int paramInt2);








  
  InputStream asInputStream();








  
  InputStream asInputStream(boolean paramBoolean);







  
  OutputStream asOutputStream();







  
  default String toString(Charset charset) {
    Assert.notNull(charset, "Charset must not be null");
    return toString(readPosition(), readableByteCount(), charset);
  }
  
  String toString(int paramInt1, int paramInt2, Charset paramCharset);
}
