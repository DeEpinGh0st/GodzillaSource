package org.bouncycastle.crypto;

public interface Xof extends ExtendedDigest {
  int doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  int doOutput(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
}
