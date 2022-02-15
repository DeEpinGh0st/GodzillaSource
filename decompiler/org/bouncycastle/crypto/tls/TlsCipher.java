package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsCipher {
  int getPlaintextLimit(int paramInt);
  
  byte[] encodePlaintext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  byte[] decodeCiphertext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
}
