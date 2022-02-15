package org.bouncycastle.util.encoders;

public class HexTranslator implements Translator {
  private static final byte[] hexTable = new byte[] { 
      48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 
      97, 98, 99, 100, 101, 102 };
  
  public int getEncodedBlockSize() {
    return 2;
  }
  
  public int encode(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    byte b1 = 0;
    for (byte b2 = 0; b1 < paramInt2; b2 += 2) {
      paramArrayOfbyte2[paramInt3 + b2] = hexTable[paramArrayOfbyte1[paramInt1] >> 4 & 0xF];
      paramArrayOfbyte2[paramInt3 + b2 + 1] = hexTable[paramArrayOfbyte1[paramInt1] & 0xF];
      paramInt1++;
      b1++;
    } 
    return paramInt2 * 2;
  }
  
  public int getDecodedBlockSize() {
    return 1;
  }
  
  public int decode(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    int i = paramInt2 / 2;
    for (byte b = 0; b < i; b++) {
      byte b1 = paramArrayOfbyte1[paramInt1 + b * 2];
      byte b2 = paramArrayOfbyte1[paramInt1 + b * 2 + 1];
      if (b1 < 97) {
        paramArrayOfbyte2[paramInt3] = (byte)(b1 - 48 << 4);
      } else {
        paramArrayOfbyte2[paramInt3] = (byte)(b1 - 97 + 10 << 4);
      } 
      if (b2 < 97) {
        paramArrayOfbyte2[paramInt3] = (byte)(paramArrayOfbyte2[paramInt3] + (byte)(b2 - 48));
      } else {
        paramArrayOfbyte2[paramInt3] = (byte)(paramArrayOfbyte2[paramInt3] + (byte)(b2 - 97 + 10));
      } 
      paramInt3++;
    } 
    return i;
  }
}
