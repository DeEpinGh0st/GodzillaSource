package org.bouncycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.util.Integers;

class Utils {
  static final Hashtable maxSecurityStrengths = new Hashtable<Object, Object>();
  
  static int getMaxSecurityStrength(Digest paramDigest) {
    return ((Integer)maxSecurityStrengths.get(paramDigest.getAlgorithmName())).intValue();
  }
  
  static int getMaxSecurityStrength(Mac paramMac) {
    String str = paramMac.getAlgorithmName();
    return ((Integer)maxSecurityStrengths.get(str.substring(0, str.indexOf("/")))).intValue();
  }
  
  static byte[] hash_df(Digest paramDigest, byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte1 = new byte[(paramInt + 7) / 8];
    int i = arrayOfByte1.length / paramDigest.getDigestSize();
    byte b = 1;
    byte[] arrayOfByte2 = new byte[paramDigest.getDigestSize()];
    int j;
    for (j = 0; j <= i; j++) {
      paramDigest.update((byte)b);
      paramDigest.update((byte)(paramInt >> 24));
      paramDigest.update((byte)(paramInt >> 16));
      paramDigest.update((byte)(paramInt >> 8));
      paramDigest.update((byte)paramInt);
      paramDigest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      paramDigest.doFinal(arrayOfByte2, 0);
      int k = (arrayOfByte1.length - j * arrayOfByte2.length > arrayOfByte2.length) ? arrayOfByte2.length : (arrayOfByte1.length - j * arrayOfByte2.length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, j * arrayOfByte2.length, k);
      b++;
    } 
    if (paramInt % 8 != 0) {
      j = 8 - paramInt % 8;
      int k = 0;
      for (byte b1 = 0; b1 != arrayOfByte1.length; b1++) {
        int m = arrayOfByte1[b1] & 0xFF;
        arrayOfByte1[b1] = (byte)(m >>> j | k << 8 - j);
        k = m;
      } 
    } 
    return arrayOfByte1;
  }
  
  static boolean isTooLarge(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte != null && paramArrayOfbyte.length > paramInt);
  }
  
  static {
    maxSecurityStrengths.put("SHA-1", Integers.valueOf(128));
    maxSecurityStrengths.put("SHA-224", Integers.valueOf(192));
    maxSecurityStrengths.put("SHA-256", Integers.valueOf(256));
    maxSecurityStrengths.put("SHA-384", Integers.valueOf(256));
    maxSecurityStrengths.put("SHA-512", Integers.valueOf(256));
    maxSecurityStrengths.put("SHA-512/224", Integers.valueOf(192));
    maxSecurityStrengths.put("SHA-512/256", Integers.valueOf(256));
  }
}
