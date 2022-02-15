package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class CamelliaLightEngine implements BlockCipher {
  private static final int BLOCK_SIZE = 16;
  
  private static final int MASK8 = 255;
  
  private boolean initialized;
  
  private boolean _keyis128;
  
  private int[] subkey = new int[96];
  
  private int[] kw = new int[8];
  
  private int[] ke = new int[12];
  
  private int[] state = new int[4];
  
  private static final int[] SIGMA = new int[] { 
      -1600231809, 1003262091, -1233459112, 1286239154, -957401297, -380665154, 1426019237, -237801700, 283453434, -563598051, 
      -1336506174, -1276722691 };
  
  private static final byte[] SBOX1 = new byte[] { 
      112, -126, 44, -20, -77, 39, -64, -27, -28, -123, 
      87, 53, -22, 12, -82, 65, 35, -17, 107, -109, 
      69, 25, -91, 33, -19, 14, 79, 78, 29, 101, 
      -110, -67, -122, -72, -81, -113, 124, -21, 31, -50, 
      62, 48, -36, 95, 94, -59, 11, 26, -90, -31, 
      57, -54, -43, 71, 93, 61, -39, 1, 90, -42, 
      81, 86, 108, 77, -117, 13, -102, 102, -5, -52, 
      -80, 45, 116, 18, 43, 32, -16, -79, -124, -103, 
      -33, 76, -53, -62, 52, 126, 118, 5, 109, -73, 
      -87, 49, -47, 23, 4, -41, 20, 88, 58, 97, 
      -34, 27, 17, 28, 50, 15, -100, 22, 83, 24, 
      -14, 34, -2, 68, -49, -78, -61, -75, 122, -111, 
      36, 8, -24, -88, 96, -4, 105, 80, -86, -48, 
      -96, 125, -95, -119, 98, -105, 84, 91, 30, -107, 
      -32, -1, 100, -46, 16, -60, 0, 72, -93, -9, 
      117, -37, -118, 3, -26, -38, 9, 63, -35, -108, 
      -121, 92, -125, 2, -51, 74, -112, 51, 115, 103, 
      -10, -13, -99, Byte.MAX_VALUE, -65, -30, 82, -101, -40, 38, 
      -56, 55, -58, 59, -127, -106, 111, 75, 19, -66, 
      99, 46, -23, 121, -89, -116, -97, 110, -68, -114, 
      41, -11, -7, -74, 47, -3, -76, 89, 120, -104, 
      6, 106, -25, 70, 113, -70, -44, 37, -85, 66, 
      -120, -94, -115, -6, 114, 7, -71, 85, -8, -18, 
      -84, 10, 54, 73, 42, 104, 60, 56, -15, -92, 
      64, 40, -45, 123, -69, -55, 67, -63, 21, -29, 
      -83, -12, 119, -57, Byte.MIN_VALUE, -98 };
  
  private static int rightRotate(int paramInt1, int paramInt2) {
    return (paramInt1 >>> paramInt2) + (paramInt1 << 32 - paramInt2);
  }
  
  private static int leftRotate(int paramInt1, int paramInt2) {
    return (paramInt1 << paramInt2) + (paramInt1 >>> 32 - paramInt2);
  }
  
  private static void roldq(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    paramArrayOfint2[0 + paramInt3] = paramArrayOfint1[0 + paramInt2] << paramInt1 | paramArrayOfint1[1 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint2[1 + paramInt3] = paramArrayOfint1[1 + paramInt2] << paramInt1 | paramArrayOfint1[2 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint2[2 + paramInt3] = paramArrayOfint1[2 + paramInt2] << paramInt1 | paramArrayOfint1[3 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint2[3 + paramInt3] = paramArrayOfint1[3 + paramInt2] << paramInt1 | paramArrayOfint1[0 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint1[0 + paramInt2] = paramArrayOfint2[0 + paramInt3];
    paramArrayOfint1[1 + paramInt2] = paramArrayOfint2[1 + paramInt3];
    paramArrayOfint1[2 + paramInt2] = paramArrayOfint2[2 + paramInt3];
    paramArrayOfint1[3 + paramInt2] = paramArrayOfint2[3 + paramInt3];
  }
  
  private static void decroldq(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    paramArrayOfint2[2 + paramInt3] = paramArrayOfint1[0 + paramInt2] << paramInt1 | paramArrayOfint1[1 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint2[3 + paramInt3] = paramArrayOfint1[1 + paramInt2] << paramInt1 | paramArrayOfint1[2 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint2[0 + paramInt3] = paramArrayOfint1[2 + paramInt2] << paramInt1 | paramArrayOfint1[3 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint2[1 + paramInt3] = paramArrayOfint1[3 + paramInt2] << paramInt1 | paramArrayOfint1[0 + paramInt2] >>> 32 - paramInt1;
    paramArrayOfint1[0 + paramInt2] = paramArrayOfint2[2 + paramInt3];
    paramArrayOfint1[1 + paramInt2] = paramArrayOfint2[3 + paramInt3];
    paramArrayOfint1[2 + paramInt2] = paramArrayOfint2[0 + paramInt3];
    paramArrayOfint1[3 + paramInt2] = paramArrayOfint2[1 + paramInt3];
  }
  
  private static void roldqo32(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    paramArrayOfint2[0 + paramInt3] = paramArrayOfint1[1 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[2 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint2[1 + paramInt3] = paramArrayOfint1[2 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[3 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint2[2 + paramInt3] = paramArrayOfint1[3 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[0 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint2[3 + paramInt3] = paramArrayOfint1[0 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[1 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint1[0 + paramInt2] = paramArrayOfint2[0 + paramInt3];
    paramArrayOfint1[1 + paramInt2] = paramArrayOfint2[1 + paramInt3];
    paramArrayOfint1[2 + paramInt2] = paramArrayOfint2[2 + paramInt3];
    paramArrayOfint1[3 + paramInt2] = paramArrayOfint2[3 + paramInt3];
  }
  
  private static void decroldqo32(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    paramArrayOfint2[2 + paramInt3] = paramArrayOfint1[1 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[2 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint2[3 + paramInt3] = paramArrayOfint1[2 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[3 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint2[0 + paramInt3] = paramArrayOfint1[3 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[0 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint2[1 + paramInt3] = paramArrayOfint1[0 + paramInt2] << paramInt1 - 32 | paramArrayOfint1[1 + paramInt2] >>> 64 - paramInt1;
    paramArrayOfint1[0 + paramInt2] = paramArrayOfint2[2 + paramInt3];
    paramArrayOfint1[1 + paramInt2] = paramArrayOfint2[3 + paramInt3];
    paramArrayOfint1[2 + paramInt2] = paramArrayOfint2[0 + paramInt3];
    paramArrayOfint1[3 + paramInt2] = paramArrayOfint2[1 + paramInt3];
  }
  
  private int bytes2int(byte[] paramArrayOfbyte, int paramInt) {
    int i = 0;
    for (byte b = 0; b < 4; b++)
      i = (i << 8) + (paramArrayOfbyte[b + paramInt] & 0xFF); 
    return i;
  }
  
  private void int2bytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    for (byte b = 0; b < 4; b++) {
      paramArrayOfbyte[3 - b + paramInt2] = (byte)paramInt1;
      paramInt1 >>>= 8;
    } 
  }
  
  private byte lRot8(byte paramByte, int paramInt) {
    return (byte)(paramByte << paramInt | (paramByte & 0xFF) >>> 8 - paramInt);
  }
  
  private int sbox2(int paramInt) {
    return lRot8(SBOX1[paramInt], 1) & 0xFF;
  }
  
  private int sbox3(int paramInt) {
    return lRot8(SBOX1[paramInt], 7) & 0xFF;
  }
  
  private int sbox4(int paramInt) {
    return SBOX1[lRot8((byte)paramInt, 1) & 0xFF] & 0xFF;
  }
  
  private void camelliaF2(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    int i = paramArrayOfint1[0] ^ paramArrayOfint2[0 + paramInt];
    int k = sbox4(i & 0xFF);
    k |= sbox3(i >>> 8 & 0xFF) << 8;
    k |= sbox2(i >>> 16 & 0xFF) << 16;
    k |= (SBOX1[i >>> 24 & 0xFF] & 0xFF) << 24;
    int j = paramArrayOfint1[1] ^ paramArrayOfint2[1 + paramInt];
    int m = SBOX1[j & 0xFF] & 0xFF;
    m |= sbox4(j >>> 8 & 0xFF) << 8;
    m |= sbox3(j >>> 16 & 0xFF) << 16;
    m |= sbox2(j >>> 24 & 0xFF) << 24;
    m = leftRotate(m, 8);
    k ^= m;
    m = leftRotate(m, 8) ^ k;
    k = rightRotate(k, 8) ^ m;
    paramArrayOfint1[2] = paramArrayOfint1[2] ^ leftRotate(m, 16) ^ k;
    paramArrayOfint1[3] = paramArrayOfint1[3] ^ leftRotate(k, 8);
    i = paramArrayOfint1[2] ^ paramArrayOfint2[2 + paramInt];
    k = sbox4(i & 0xFF);
    k |= sbox3(i >>> 8 & 0xFF) << 8;
    k |= sbox2(i >>> 16 & 0xFF) << 16;
    k |= (SBOX1[i >>> 24 & 0xFF] & 0xFF) << 24;
    j = paramArrayOfint1[3] ^ paramArrayOfint2[3 + paramInt];
    m = SBOX1[j & 0xFF] & 0xFF;
    m |= sbox4(j >>> 8 & 0xFF) << 8;
    m |= sbox3(j >>> 16 & 0xFF) << 16;
    m |= sbox2(j >>> 24 & 0xFF) << 24;
    m = leftRotate(m, 8);
    k ^= m;
    m = leftRotate(m, 8) ^ k;
    k = rightRotate(k, 8) ^ m;
    paramArrayOfint1[0] = paramArrayOfint1[0] ^ leftRotate(m, 16) ^ k;
    paramArrayOfint1[1] = paramArrayOfint1[1] ^ leftRotate(k, 8);
  }
  
  private void camelliaFLs(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    paramArrayOfint1[1] = paramArrayOfint1[1] ^ leftRotate(paramArrayOfint1[0] & paramArrayOfint2[0 + paramInt], 1);
    paramArrayOfint1[0] = paramArrayOfint1[0] ^ (paramArrayOfint2[1 + paramInt] | paramArrayOfint1[1]);
    paramArrayOfint1[2] = paramArrayOfint1[2] ^ (paramArrayOfint2[3 + paramInt] | paramArrayOfint1[3]);
    paramArrayOfint1[3] = paramArrayOfint1[3] ^ leftRotate(paramArrayOfint2[2 + paramInt] & paramArrayOfint1[2], 1);
  }
  
  private void setKey(boolean paramBoolean, byte[] paramArrayOfbyte) {
    int[] arrayOfInt1 = new int[8];
    int[] arrayOfInt2 = new int[4];
    int[] arrayOfInt3 = new int[4];
    int[] arrayOfInt4 = new int[4];
    switch (paramArrayOfbyte.length) {
      case 16:
        this._keyis128 = true;
        arrayOfInt1[0] = bytes2int(paramArrayOfbyte, 0);
        arrayOfInt1[1] = bytes2int(paramArrayOfbyte, 4);
        arrayOfInt1[2] = bytes2int(paramArrayOfbyte, 8);
        arrayOfInt1[3] = bytes2int(paramArrayOfbyte, 12);
        arrayOfInt1[7] = 0;
        arrayOfInt1[6] = 0;
        arrayOfInt1[5] = 0;
        arrayOfInt1[4] = 0;
        break;
      case 24:
        arrayOfInt1[0] = bytes2int(paramArrayOfbyte, 0);
        arrayOfInt1[1] = bytes2int(paramArrayOfbyte, 4);
        arrayOfInt1[2] = bytes2int(paramArrayOfbyte, 8);
        arrayOfInt1[3] = bytes2int(paramArrayOfbyte, 12);
        arrayOfInt1[4] = bytes2int(paramArrayOfbyte, 16);
        arrayOfInt1[5] = bytes2int(paramArrayOfbyte, 20);
        arrayOfInt1[6] = arrayOfInt1[4] ^ 0xFFFFFFFF;
        arrayOfInt1[7] = arrayOfInt1[5] ^ 0xFFFFFFFF;
        this._keyis128 = false;
        break;
      case 32:
        arrayOfInt1[0] = bytes2int(paramArrayOfbyte, 0);
        arrayOfInt1[1] = bytes2int(paramArrayOfbyte, 4);
        arrayOfInt1[2] = bytes2int(paramArrayOfbyte, 8);
        arrayOfInt1[3] = bytes2int(paramArrayOfbyte, 12);
        arrayOfInt1[4] = bytes2int(paramArrayOfbyte, 16);
        arrayOfInt1[5] = bytes2int(paramArrayOfbyte, 20);
        arrayOfInt1[6] = bytes2int(paramArrayOfbyte, 24);
        arrayOfInt1[7] = bytes2int(paramArrayOfbyte, 28);
        this._keyis128 = false;
        break;
      default:
        throw new IllegalArgumentException("key sizes are only 16/24/32 bytes.");
    } 
    byte b;
    for (b = 0; b < 4; b++)
      arrayOfInt2[b] = arrayOfInt1[b] ^ arrayOfInt1[b + 4]; 
    camelliaF2(arrayOfInt2, SIGMA, 0);
    for (b = 0; b < 4; b++)
      arrayOfInt2[b] = arrayOfInt2[b] ^ arrayOfInt1[b]; 
    camelliaF2(arrayOfInt2, SIGMA, 4);
    if (this._keyis128) {
      if (paramBoolean) {
        this.kw[0] = arrayOfInt1[0];
        this.kw[1] = arrayOfInt1[1];
        this.kw[2] = arrayOfInt1[2];
        this.kw[3] = arrayOfInt1[3];
        roldq(15, arrayOfInt1, 0, this.subkey, 4);
        roldq(30, arrayOfInt1, 0, this.subkey, 12);
        roldq(15, arrayOfInt1, 0, arrayOfInt4, 0);
        this.subkey[18] = arrayOfInt4[2];
        this.subkey[19] = arrayOfInt4[3];
        roldq(17, arrayOfInt1, 0, this.ke, 4);
        roldq(17, arrayOfInt1, 0, this.subkey, 24);
        roldq(17, arrayOfInt1, 0, this.subkey, 32);
        this.subkey[0] = arrayOfInt2[0];
        this.subkey[1] = arrayOfInt2[1];
        this.subkey[2] = arrayOfInt2[2];
        this.subkey[3] = arrayOfInt2[3];
        roldq(15, arrayOfInt2, 0, this.subkey, 8);
        roldq(15, arrayOfInt2, 0, this.ke, 0);
        roldq(15, arrayOfInt2, 0, arrayOfInt4, 0);
        this.subkey[16] = arrayOfInt4[0];
        this.subkey[17] = arrayOfInt4[1];
        roldq(15, arrayOfInt2, 0, this.subkey, 20);
        roldqo32(34, arrayOfInt2, 0, this.subkey, 28);
        roldq(17, arrayOfInt2, 0, this.kw, 4);
      } else {
        this.kw[4] = arrayOfInt1[0];
        this.kw[5] = arrayOfInt1[1];
        this.kw[6] = arrayOfInt1[2];
        this.kw[7] = arrayOfInt1[3];
        decroldq(15, arrayOfInt1, 0, this.subkey, 28);
        decroldq(30, arrayOfInt1, 0, this.subkey, 20);
        decroldq(15, arrayOfInt1, 0, arrayOfInt4, 0);
        this.subkey[16] = arrayOfInt4[0];
        this.subkey[17] = arrayOfInt4[1];
        decroldq(17, arrayOfInt1, 0, this.ke, 0);
        decroldq(17, arrayOfInt1, 0, this.subkey, 8);
        decroldq(17, arrayOfInt1, 0, this.subkey, 0);
        this.subkey[34] = arrayOfInt2[0];
        this.subkey[35] = arrayOfInt2[1];
        this.subkey[32] = arrayOfInt2[2];
        this.subkey[33] = arrayOfInt2[3];
        decroldq(15, arrayOfInt2, 0, this.subkey, 24);
        decroldq(15, arrayOfInt2, 0, this.ke, 4);
        decroldq(15, arrayOfInt2, 0, arrayOfInt4, 0);
        this.subkey[18] = arrayOfInt4[2];
        this.subkey[19] = arrayOfInt4[3];
        decroldq(15, arrayOfInt2, 0, this.subkey, 12);
        decroldqo32(34, arrayOfInt2, 0, this.subkey, 4);
        roldq(17, arrayOfInt2, 0, this.kw, 0);
      } 
    } else {
      for (b = 0; b < 4; b++)
        arrayOfInt3[b] = arrayOfInt2[b] ^ arrayOfInt1[b + 4]; 
      camelliaF2(arrayOfInt3, SIGMA, 8);
      if (paramBoolean) {
        this.kw[0] = arrayOfInt1[0];
        this.kw[1] = arrayOfInt1[1];
        this.kw[2] = arrayOfInt1[2];
        this.kw[3] = arrayOfInt1[3];
        roldqo32(45, arrayOfInt1, 0, this.subkey, 16);
        roldq(15, arrayOfInt1, 0, this.ke, 4);
        roldq(17, arrayOfInt1, 0, this.subkey, 32);
        roldqo32(34, arrayOfInt1, 0, this.subkey, 44);
        roldq(15, arrayOfInt1, 4, this.subkey, 4);
        roldq(15, arrayOfInt1, 4, this.ke, 0);
        roldq(30, arrayOfInt1, 4, this.subkey, 24);
        roldqo32(34, arrayOfInt1, 4, this.subkey, 36);
        roldq(15, arrayOfInt2, 0, this.subkey, 8);
        roldq(30, arrayOfInt2, 0, this.subkey, 20);
        this.ke[8] = arrayOfInt2[1];
        this.ke[9] = arrayOfInt2[2];
        this.ke[10] = arrayOfInt2[3];
        this.ke[11] = arrayOfInt2[0];
        roldqo32(49, arrayOfInt2, 0, this.subkey, 40);
        this.subkey[0] = arrayOfInt3[0];
        this.subkey[1] = arrayOfInt3[1];
        this.subkey[2] = arrayOfInt3[2];
        this.subkey[3] = arrayOfInt3[3];
        roldq(30, arrayOfInt3, 0, this.subkey, 12);
        roldq(30, arrayOfInt3, 0, this.subkey, 28);
        roldqo32(51, arrayOfInt3, 0, this.kw, 4);
      } else {
        this.kw[4] = arrayOfInt1[0];
        this.kw[5] = arrayOfInt1[1];
        this.kw[6] = arrayOfInt1[2];
        this.kw[7] = arrayOfInt1[3];
        decroldqo32(45, arrayOfInt1, 0, this.subkey, 28);
        decroldq(15, arrayOfInt1, 0, this.ke, 4);
        decroldq(17, arrayOfInt1, 0, this.subkey, 12);
        decroldqo32(34, arrayOfInt1, 0, this.subkey, 0);
        decroldq(15, arrayOfInt1, 4, this.subkey, 40);
        decroldq(15, arrayOfInt1, 4, this.ke, 8);
        decroldq(30, arrayOfInt1, 4, this.subkey, 20);
        decroldqo32(34, arrayOfInt1, 4, this.subkey, 8);
        decroldq(15, arrayOfInt2, 0, this.subkey, 36);
        decroldq(30, arrayOfInt2, 0, this.subkey, 24);
        this.ke[2] = arrayOfInt2[1];
        this.ke[3] = arrayOfInt2[2];
        this.ke[0] = arrayOfInt2[3];
        this.ke[1] = arrayOfInt2[0];
        decroldqo32(49, arrayOfInt2, 0, this.subkey, 4);
        this.subkey[46] = arrayOfInt3[0];
        this.subkey[47] = arrayOfInt3[1];
        this.subkey[44] = arrayOfInt3[2];
        this.subkey[45] = arrayOfInt3[3];
        decroldq(30, arrayOfInt3, 0, this.subkey, 32);
        decroldq(30, arrayOfInt3, 0, this.subkey, 16);
        roldqo32(51, arrayOfInt3, 0, this.kw, 0);
      } 
    } 
  }
  
  private int processBlock128(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    for (byte b = 0; b < 4; b++) {
      this.state[b] = bytes2int(paramArrayOfbyte1, paramInt1 + b * 4);
      this.state[b] = this.state[b] ^ this.kw[b];
    } 
    camelliaF2(this.state, this.subkey, 0);
    camelliaF2(this.state, this.subkey, 4);
    camelliaF2(this.state, this.subkey, 8);
    camelliaFLs(this.state, this.ke, 0);
    camelliaF2(this.state, this.subkey, 12);
    camelliaF2(this.state, this.subkey, 16);
    camelliaF2(this.state, this.subkey, 20);
    camelliaFLs(this.state, this.ke, 4);
    camelliaF2(this.state, this.subkey, 24);
    camelliaF2(this.state, this.subkey, 28);
    camelliaF2(this.state, this.subkey, 32);
    this.state[2] = this.state[2] ^ this.kw[4];
    this.state[3] = this.state[3] ^ this.kw[5];
    this.state[0] = this.state[0] ^ this.kw[6];
    this.state[1] = this.state[1] ^ this.kw[7];
    int2bytes(this.state[2], paramArrayOfbyte2, paramInt2);
    int2bytes(this.state[3], paramArrayOfbyte2, paramInt2 + 4);
    int2bytes(this.state[0], paramArrayOfbyte2, paramInt2 + 8);
    int2bytes(this.state[1], paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  private int processBlock192or256(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    for (byte b = 0; b < 4; b++) {
      this.state[b] = bytes2int(paramArrayOfbyte1, paramInt1 + b * 4);
      this.state[b] = this.state[b] ^ this.kw[b];
    } 
    camelliaF2(this.state, this.subkey, 0);
    camelliaF2(this.state, this.subkey, 4);
    camelliaF2(this.state, this.subkey, 8);
    camelliaFLs(this.state, this.ke, 0);
    camelliaF2(this.state, this.subkey, 12);
    camelliaF2(this.state, this.subkey, 16);
    camelliaF2(this.state, this.subkey, 20);
    camelliaFLs(this.state, this.ke, 4);
    camelliaF2(this.state, this.subkey, 24);
    camelliaF2(this.state, this.subkey, 28);
    camelliaF2(this.state, this.subkey, 32);
    camelliaFLs(this.state, this.ke, 8);
    camelliaF2(this.state, this.subkey, 36);
    camelliaF2(this.state, this.subkey, 40);
    camelliaF2(this.state, this.subkey, 44);
    this.state[2] = this.state[2] ^ this.kw[4];
    this.state[3] = this.state[3] ^ this.kw[5];
    this.state[0] = this.state[0] ^ this.kw[6];
    this.state[1] = this.state[1] ^ this.kw[7];
    int2bytes(this.state[2], paramArrayOfbyte2, paramInt2);
    int2bytes(this.state[3], paramArrayOfbyte2, paramInt2 + 4);
    int2bytes(this.state[0], paramArrayOfbyte2, paramInt2 + 8);
    int2bytes(this.state[1], paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  public String getAlgorithmName() {
    return "Camellia";
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("only simple KeyParameter expected."); 
    setKey(paramBoolean, ((KeyParameter)paramCipherParameters).getKey());
    this.initialized = true;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws IllegalStateException {
    if (!this.initialized)
      throw new IllegalStateException("Camellia is not initialized"); 
    if (paramInt1 + 16 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 16 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    return this._keyis128 ? processBlock128(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : processBlock192or256(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {}
}
