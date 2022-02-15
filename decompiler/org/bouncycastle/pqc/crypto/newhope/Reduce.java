package org.bouncycastle.pqc.crypto.newhope;

class Reduce {
  static final int QInv = 12287;
  
  static final int RLog = 18;
  
  static final int RMask = 262143;
  
  static short montgomery(int paramInt) {
    int i = paramInt * 12287;
    i &= 0x3FFFF;
    i *= 12289;
    i += paramInt;
    return (short)(i >>> 18);
  }
  
  static short barrett(short paramShort) {
    int i = paramShort & 0xFFFF;
    int j = i * 5 >>> 16;
    j *= 12289;
    return (short)(i - j);
  }
}
