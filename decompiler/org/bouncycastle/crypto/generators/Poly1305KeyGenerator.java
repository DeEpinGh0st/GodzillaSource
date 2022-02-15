package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class Poly1305KeyGenerator extends CipherKeyGenerator {
  private static final byte R_MASK_LOW_2 = -4;
  
  private static final byte R_MASK_HIGH_4 = 15;
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    super.init(new KeyGenerationParameters(paramKeyGenerationParameters.getRandom(), 256));
  }
  
  public byte[] generateKey() {
    byte[] arrayOfByte = super.generateKey();
    clamp(arrayOfByte);
    return arrayOfByte;
  }
  
  public static void clamp(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 32)
      throw new IllegalArgumentException("Poly1305 key must be 256 bits."); 
    paramArrayOfbyte[3] = (byte)(paramArrayOfbyte[3] & 0xF);
    paramArrayOfbyte[7] = (byte)(paramArrayOfbyte[7] & 0xF);
    paramArrayOfbyte[11] = (byte)(paramArrayOfbyte[11] & 0xF);
    paramArrayOfbyte[15] = (byte)(paramArrayOfbyte[15] & 0xF);
    paramArrayOfbyte[4] = (byte)(paramArrayOfbyte[4] & 0xFFFFFFFC);
    paramArrayOfbyte[8] = (byte)(paramArrayOfbyte[8] & 0xFFFFFFFC);
    paramArrayOfbyte[12] = (byte)(paramArrayOfbyte[12] & 0xFFFFFFFC);
  }
  
  public static void checkKey(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 32)
      throw new IllegalArgumentException("Poly1305 key must be 256 bits."); 
    checkMask(paramArrayOfbyte[3], (byte)15);
    checkMask(paramArrayOfbyte[7], (byte)15);
    checkMask(paramArrayOfbyte[11], (byte)15);
    checkMask(paramArrayOfbyte[15], (byte)15);
    checkMask(paramArrayOfbyte[4], (byte)-4);
    checkMask(paramArrayOfbyte[8], (byte)-4);
    checkMask(paramArrayOfbyte[12], (byte)-4);
  }
  
  private static void checkMask(byte paramByte1, byte paramByte2) {
    if ((paramByte1 & (paramByte2 ^ 0xFFFFFFFF)) != 0)
      throw new IllegalArgumentException("Invalid format for r portion of Poly1305 key."); 
  }
}
