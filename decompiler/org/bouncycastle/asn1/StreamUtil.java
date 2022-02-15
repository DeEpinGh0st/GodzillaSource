package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

class StreamUtil {
  private static final long MAX_MEMORY = Runtime.getRuntime().maxMemory();
  
  static int findLimit(InputStream paramInputStream) {
    if (paramInputStream instanceof LimitedInputStream)
      return ((LimitedInputStream)paramInputStream).getRemaining(); 
    if (paramInputStream instanceof ASN1InputStream)
      return ((ASN1InputStream)paramInputStream).getLimit(); 
    if (paramInputStream instanceof ByteArrayInputStream)
      return ((ByteArrayInputStream)paramInputStream).available(); 
    if (paramInputStream instanceof FileInputStream)
      try {
        FileChannel fileChannel = ((FileInputStream)paramInputStream).getChannel();
        long l = (fileChannel != null) ? fileChannel.size() : 2147483647L;
        if (l < 2147483647L)
          return (int)l; 
      } catch (IOException iOException) {} 
    return (MAX_MEMORY > 2147483647L) ? Integer.MAX_VALUE : (int)MAX_MEMORY;
  }
  
  static int calculateBodyLength(int paramInt) {
    byte b = 1;
    if (paramInt > 127) {
      byte b1 = 1;
      int i = paramInt;
      while ((i >>>= 8) != 0)
        b1++; 
      for (int j = (b1 - 1) * 8; j >= 0; j -= 8)
        b++; 
    } 
    return b;
  }
  
  static int calculateTagLength(int paramInt) throws IOException {
    int i = 1;
    if (paramInt >= 31)
      if (paramInt < 128) {
        i++;
      } else {
        byte[] arrayOfByte = new byte[5];
        int j = arrayOfByte.length;
        arrayOfByte[--j] = (byte)(paramInt & 0x7F);
        while (true) {
          paramInt >>= 7;
          arrayOfByte[--j] = (byte)(paramInt & 0x7F | 0x80);
          if (paramInt <= 127) {
            i += arrayOfByte.length - j;
            break;
          } 
        } 
      }  
    return i;
  }
}
