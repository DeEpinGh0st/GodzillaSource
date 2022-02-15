package org.bouncycastle.crypto.prng;

public class EntropyUtil {
  public static byte[] generateSeed(EntropySource paramEntropySource, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    if (paramInt * 8 <= paramEntropySource.entropySize()) {
      byte[] arrayOfByte1 = paramEntropySource.getEntropy();
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, 0, arrayOfByte.length);
    } else {
      int i = paramEntropySource.entropySize() / 8;
      int j;
      for (j = 0; j < arrayOfByte.length; j += i) {
        byte[] arrayOfByte1 = paramEntropySource.getEntropy();
        if (arrayOfByte1.length <= arrayOfByte.length - j) {
          System.arraycopy(arrayOfByte1, 0, arrayOfByte, j, arrayOfByte1.length);
        } else {
          System.arraycopy(arrayOfByte1, 0, arrayOfByte, j, arrayOfByte.length - j);
        } 
      } 
    } 
    return arrayOfByte;
  }
}
