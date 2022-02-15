package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class RijndaelEngine implements BlockCipher {
  private static final int MAXROUNDS = 14;
  
  private static final int MAXKC = 64;
  
  private static final byte[] logtable = new byte[] { 
      0, 0, 25, 1, 50, 2, 26, -58, 75, -57, 
      27, 104, 51, -18, -33, 3, 100, 4, -32, 14, 
      52, -115, -127, -17, 76, 113, 8, -56, -8, 105, 
      28, -63, 125, -62, 29, -75, -7, -71, 39, 106, 
      77, -28, -90, 114, -102, -55, 9, 120, 101, 47, 
      -118, 5, 33, 15, -31, 36, 18, -16, -126, 69, 
      53, -109, -38, -114, -106, -113, -37, -67, 54, -48, 
      -50, -108, 19, 92, -46, -15, 64, 70, -125, 56, 
      102, -35, -3, 48, -65, 6, -117, 98, -77, 37, 
      -30, -104, 34, -120, -111, 16, 126, 110, 72, -61, 
      -93, -74, 30, 66, 58, 107, 40, 84, -6, -123, 
      61, -70, 43, 121, 10, 21, -101, -97, 94, -54, 
      78, -44, -84, -27, -13, 115, -89, 87, -81, 88, 
      -88, 80, -12, -22, -42, 116, 79, -82, -23, -43, 
      -25, -26, -83, -24, 44, -41, 117, 122, -21, 22, 
      11, -11, 89, -53, 95, -80, -100, -87, 81, -96, 
      Byte.MAX_VALUE, 12, -10, 111, 23, -60, 73, -20, -40, 67, 
      31, 45, -92, 118, 123, -73, -52, -69, 62, 90, 
      -5, 96, -79, -122, 59, 82, -95, 108, -86, 85, 
      41, -99, -105, -78, -121, -112, 97, -66, -36, -4, 
      -68, -107, -49, -51, 55, 63, 91, -47, 83, 57, 
      -124, 60, 65, -94, 109, 71, 20, 42, -98, 93, 
      86, -14, -45, -85, 68, 17, -110, -39, 35, 32, 
      46, -119, -76, 124, -72, 38, 119, -103, -29, -91, 
      103, 74, -19, -34, -59, 49, -2, 24, 13, 99, 
      -116, Byte.MIN_VALUE, -64, -9, 112, 7 };
  
  private static final byte[] aLogtable = new byte[] { 
      0, 3, 5, 15, 17, 51, 85, -1, 26, 46, 
      114, -106, -95, -8, 19, 53, 95, -31, 56, 72, 
      -40, 115, -107, -92, -9, 2, 6, 10, 30, 34, 
      102, -86, -27, 52, 92, -28, 55, 89, -21, 38, 
      106, -66, -39, 112, -112, -85, -26, 49, 83, -11, 
      4, 12, 20, 60, 68, -52, 79, -47, 104, -72, 
      -45, 110, -78, -51, 76, -44, 103, -87, -32, 59, 
      77, -41, 98, -90, -15, 8, 24, 40, 120, -120, 
      -125, -98, -71, -48, 107, -67, -36, Byte.MAX_VALUE, -127, -104, 
      -77, -50, 73, -37, 118, -102, -75, -60, 87, -7, 
      16, 48, 80, -16, 11, 29, 39, 105, -69, -42, 
      97, -93, -2, 25, 43, 125, -121, -110, -83, -20, 
      47, 113, -109, -82, -23, 32, 96, -96, -5, 22, 
      58, 78, -46, 109, -73, -62, 93, -25, 50, 86, 
      -6, 21, 63, 65, -61, 94, -30, 61, 71, -55, 
      64, -64, 91, -19, 44, 116, -100, -65, -38, 117, 
      -97, -70, -43, 100, -84, -17, 42, 126, -126, -99, 
      -68, -33, 122, -114, -119, Byte.MIN_VALUE, -101, -74, -63, 88, 
      -24, 35, 101, -81, -22, 37, 111, -79, -56, 67, 
      -59, 84, -4, 31, 33, 99, -91, -12, 7, 9, 
      27, 45, 119, -103, -80, -53, 70, -54, 69, -49, 
      74, -34, 121, -117, -122, -111, -88, -29, 62, 66, 
      -58, 81, -13, 14, 18, 54, 90, -18, 41, 123, 
      -115, -116, -113, -118, -123, -108, -89, -14, 13, 23, 
      57, 75, -35, 124, -124, -105, -94, -3, 28, 36, 
      108, -76, -57, 82, -10, 1, 3, 5, 15, 17, 
      51, 85, -1, 26, 46, 114, -106, -95, -8, 19, 
      53, 95, -31, 56, 72, -40, 115, -107, -92, -9, 
      2, 6, 10, 30, 34, 102, -86, -27, 52, 92, 
      -28, 55, 89, -21, 38, 106, -66, -39, 112, -112, 
      -85, -26, 49, 83, -11, 4, 12, 20, 60, 68, 
      -52, 79, -47, 104, -72, -45, 110, -78, -51, 76, 
      -44, 103, -87, -32, 59, 77, -41, 98, -90, -15, 
      8, 24, 40, 120, -120, -125, -98, -71, -48, 107, 
      -67, -36, Byte.MAX_VALUE, -127, -104, -77, -50, 73, -37, 118, 
      -102, -75, -60, 87, -7, 16, 48, 80, -16, 11, 
      29, 39, 105, -69, -42, 97, -93, -2, 25, 43, 
      125, -121, -110, -83, -20, 47, 113, -109, -82, -23, 
      32, 96, -96, -5, 22, 58, 78, -46, 109, -73, 
      -62, 93, -25, 50, 86, -6, 21, 63, 65, -61, 
      94, -30, 61, 71, -55, 64, -64, 91, -19, 44, 
      116, -100, -65, -38, 117, -97, -70, -43, 100, -84, 
      -17, 42, 126, -126, -99, -68, -33, 122, -114, -119, 
      Byte.MIN_VALUE, -101, -74, -63, 88, -24, 35, 101, -81, -22, 
      37, 111, -79, -56, 67, -59, 84, -4, 31, 33, 
      99, -91, -12, 7, 9, 27, 45, 119, -103, -80, 
      -53, 70, -54, 69, -49, 74, -34, 121, -117, -122, 
      -111, -88, -29, 62, 66, -58, 81, -13, 14, 18, 
      54, 90, -18, 41, 123, -115, -116, -113, -118, -123, 
      -108, -89, -14, 13, 23, 57, 75, -35, 124, -124, 
      -105, -94, -3, 28, 36, 108, -76, -57, 82, -10, 
      1 };
  
  private static final byte[] S = new byte[] { 
      99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 
      103, 43, -2, -41, -85, 118, -54, -126, -55, 125, 
      -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 
      114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 
      52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 
      35, -61, 24, -106, 5, -102, 7, 18, Byte.MIN_VALUE, -30, 
      -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 
      90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 
      83, -47, 0, -19, 32, -4, -79, 91, 106, -53, 
      -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 
      67, 77, 51, -123, 69, -7, 2, Byte.MAX_VALUE, 80, 60, 
      -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, 
      -68, -74, -38, 33, 16, -1, -13, -46, -51, 12, 
      19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 
      100, 93, 25, 115, 96, -127, 79, -36, 34, 42, 
      -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, 
      -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, 
      -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, 
      -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, 
      -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, 
      -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, 
      -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, 
      -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, 
      -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, 
      -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 
      45, 15, -80, 84, -69, 22 };
  
  private static final byte[] Si = new byte[] { 
      82, 9, 106, -43, 48, 54, -91, 56, -65, 64, 
      -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, 
      -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, 
      -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, 
      -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, 
      -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 
      109, -117, -47, 37, 114, -8, -10, 100, -122, 104, 
      -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 
      108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 
      70, 87, -89, -115, -99, -124, -112, -40, -85, 0, 
      -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 
      69, 6, -48, 44, 30, -113, -54, 63, 15, 2, 
      -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 
      17, 65, 79, 103, -36, -22, -105, -14, -49, -50, 
      -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 
      53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 
      71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 
      98, 14, -86, 24, -66, 27, -4, 86, 62, 75, 
      -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 
      90, -12, 31, -35, -88, 51, -120, 7, -57, 49, 
      -79, 18, 16, 89, 39, Byte.MIN_VALUE, -20, 95, 96, 81, 
      Byte.MAX_VALUE, -87, 25, -75, 74, 13, 45, -27, 122, -97, 
      -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, 
      -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 
      23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 
      20, 99, 85, 33, 12, 125 };
  
  private static final int[] rcon = new int[] { 
      1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 
      108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 
      151, 53, 106, 212, 179, 125, 250, 239, 197, 145 };
  
  static byte[][] shifts0 = new byte[][] { { 0, 8, 16, 24 }, { 0, 8, 16, 24 }, { 0, 8, 16, 24 }, { 0, 8, 16, 32 }, { 0, 8, 24, 32 } };
  
  static byte[][] shifts1 = new byte[][] { { 0, 24, 16, 8 }, { 0, 32, 24, 16 }, { 0, 40, 32, 24 }, { 0, 48, 40, 24 }, { 0, 56, 40, 32 } };
  
  private int BC;
  
  private long BC_MASK;
  
  private int ROUNDS;
  
  private int blockBits;
  
  private long[][] workingKey;
  
  private long A0;
  
  private long A1;
  
  private long A2;
  
  private long A3;
  
  private boolean forEncryption;
  
  private byte[] shifts0SC;
  
  private byte[] shifts1SC;
  
  private byte mul0x2(int paramInt) {
    return (paramInt != 0) ? aLogtable[25 + (logtable[paramInt] & 0xFF)] : 0;
  }
  
  private byte mul0x3(int paramInt) {
    return (paramInt != 0) ? aLogtable[1 + (logtable[paramInt] & 0xFF)] : 0;
  }
  
  private byte mul0x9(int paramInt) {
    return (paramInt >= 0) ? aLogtable[199 + paramInt] : 0;
  }
  
  private byte mul0xb(int paramInt) {
    return (paramInt >= 0) ? aLogtable[104 + paramInt] : 0;
  }
  
  private byte mul0xd(int paramInt) {
    return (paramInt >= 0) ? aLogtable[238 + paramInt] : 0;
  }
  
  private byte mul0xe(int paramInt) {
    return (paramInt >= 0) ? aLogtable[223 + paramInt] : 0;
  }
  
  private void KeyAddition(long[] paramArrayOflong) {
    this.A0 ^= paramArrayOflong[0];
    this.A1 ^= paramArrayOflong[1];
    this.A2 ^= paramArrayOflong[2];
    this.A3 ^= paramArrayOflong[3];
  }
  
  private long shift(long paramLong, int paramInt) {
    return (paramLong >>> paramInt | paramLong << this.BC - paramInt) & this.BC_MASK;
  }
  
  private void ShiftRow(byte[] paramArrayOfbyte) {
    this.A1 = shift(this.A1, paramArrayOfbyte[1]);
    this.A2 = shift(this.A2, paramArrayOfbyte[2]);
    this.A3 = shift(this.A3, paramArrayOfbyte[3]);
  }
  
  private long applyS(long paramLong, byte[] paramArrayOfbyte) {
    long l = 0L;
    for (byte b = 0; b < this.BC; b += 8)
      l |= (paramArrayOfbyte[(int)(paramLong >> b & 0xFFL)] & 0xFF) << b; 
    return l;
  }
  
  private void Substitution(byte[] paramArrayOfbyte) {
    this.A0 = applyS(this.A0, paramArrayOfbyte);
    this.A1 = applyS(this.A1, paramArrayOfbyte);
    this.A2 = applyS(this.A2, paramArrayOfbyte);
    this.A3 = applyS(this.A3, paramArrayOfbyte);
  }
  
  private void MixColumn() {
    long l4 = 0L;
    long l3 = l4;
    long l2 = l3;
    long l1 = l2;
    for (byte b = 0; b < this.BC; b += 8) {
      int i = (int)(this.A0 >> b & 0xFFL);
      int j = (int)(this.A1 >> b & 0xFFL);
      int k = (int)(this.A2 >> b & 0xFFL);
      int m = (int)(this.A3 >> b & 0xFFL);
      l1 |= ((mul0x2(i) ^ mul0x3(j) ^ k ^ m) & 0xFF) << b;
      l2 |= ((mul0x2(j) ^ mul0x3(k) ^ m ^ i) & 0xFF) << b;
      l3 |= ((mul0x2(k) ^ mul0x3(m) ^ i ^ j) & 0xFF) << b;
      l4 |= ((mul0x2(m) ^ mul0x3(i) ^ j ^ k) & 0xFF) << b;
    } 
    this.A0 = l1;
    this.A1 = l2;
    this.A2 = l3;
    this.A3 = l4;
  }
  
  private void InvMixColumn() {
    long l4 = 0L;
    long l3 = l4;
    long l2 = l3;
    long l1 = l2;
    for (byte b = 0; b < this.BC; b += 8) {
      int i = (int)(this.A0 >> b & 0xFFL);
      int j = (int)(this.A1 >> b & 0xFFL);
      int k = (int)(this.A2 >> b & 0xFFL);
      int m = (int)(this.A3 >> b & 0xFFL);
      i = (i != 0) ? (logtable[i & 0xFF] & 0xFF) : -1;
      j = (j != 0) ? (logtable[j & 0xFF] & 0xFF) : -1;
      k = (k != 0) ? (logtable[k & 0xFF] & 0xFF) : -1;
      m = (m != 0) ? (logtable[m & 0xFF] & 0xFF) : -1;
      l1 |= ((mul0xe(i) ^ mul0xb(j) ^ mul0xd(k) ^ mul0x9(m)) & 0xFF) << b;
      l2 |= ((mul0xe(j) ^ mul0xb(k) ^ mul0xd(m) ^ mul0x9(i)) & 0xFF) << b;
      l3 |= ((mul0xe(k) ^ mul0xb(m) ^ mul0xd(i) ^ mul0x9(j)) & 0xFF) << b;
      l4 |= ((mul0xe(m) ^ mul0xb(i) ^ mul0xd(j) ^ mul0x9(k)) & 0xFF) << b;
    } 
    this.A0 = l1;
    this.A1 = l2;
    this.A2 = l3;
    this.A3 = l4;
  }
  
  private long[][] generateWorkingKey(byte[] paramArrayOfbyte) {
    byte b1;
    byte b3 = 0;
    int i = paramArrayOfbyte.length * 8;
    byte[][] arrayOfByte = new byte[4][64];
    long[][] arrayOfLong = new long[15][4];
    switch (i) {
      case 128:
        b1 = 4;
        break;
      case 160:
        b1 = 5;
        break;
      case 192:
        b1 = 6;
        break;
      case 224:
        b1 = 7;
        break;
      case 256:
        b1 = 8;
        break;
      default:
        throw new IllegalArgumentException("Key length not 128/160/192/224/256 bits.");
    } 
    if (i >= this.blockBits) {
      this.ROUNDS = b1 + 6;
    } else {
      this.ROUNDS = this.BC / 8 + 6;
    } 
    byte b4 = 0;
    byte b5;
    for (b5 = 0; b5 < paramArrayOfbyte.length; b5++)
      arrayOfByte[b5 % 4][b5 / 4] = paramArrayOfbyte[b4++]; 
    byte b2 = 0;
    b5 = 0;
    while (b5 < b1 && b2 < (this.ROUNDS + 1) * this.BC / 8) {
      for (byte b = 0; b < 4; b++)
        arrayOfLong[b2 / this.BC / 8][b] = arrayOfLong[b2 / this.BC / 8][b] | (arrayOfByte[b][b5] & 0xFF) << b2 * 8 % this.BC; 
      b5++;
      b2++;
    } 
    while (b2 < (this.ROUNDS + 1) * this.BC / 8) {
      for (b5 = 0; b5 < 4; b5++)
        arrayOfByte[b5][0] = (byte)(arrayOfByte[b5][0] ^ S[arrayOfByte[(b5 + 1) % 4][b1 - 1] & 0xFF]); 
      arrayOfByte[0][0] = (byte)(arrayOfByte[0][0] ^ rcon[b3++]);
      if (b1 <= 6) {
        for (b5 = 1; b5 < b1; b5++) {
          for (byte b = 0; b < 4; b++)
            arrayOfByte[b][b5] = (byte)(arrayOfByte[b][b5] ^ arrayOfByte[b][b5 - 1]); 
        } 
      } else {
        for (b5 = 1; b5 < 4; b5++) {
          for (byte b = 0; b < 4; b++)
            arrayOfByte[b][b5] = (byte)(arrayOfByte[b][b5] ^ arrayOfByte[b][b5 - 1]); 
        } 
        for (b5 = 0; b5 < 4; b5++)
          arrayOfByte[b5][4] = (byte)(arrayOfByte[b5][4] ^ S[arrayOfByte[b5][3] & 0xFF]); 
        for (b5 = 5; b5 < b1; b5++) {
          for (byte b = 0; b < 4; b++)
            arrayOfByte[b][b5] = (byte)(arrayOfByte[b][b5] ^ arrayOfByte[b][b5 - 1]); 
        } 
      } 
      b5 = 0;
      while (b5 < b1 && b2 < (this.ROUNDS + 1) * this.BC / 8) {
        for (byte b = 0; b < 4; b++)
          arrayOfLong[b2 / this.BC / 8][b] = arrayOfLong[b2 / this.BC / 8][b] | (arrayOfByte[b][b5] & 0xFF) << b2 * 8 % this.BC; 
        b5++;
        b2++;
      } 
    } 
    return arrayOfLong;
  }
  
  public RijndaelEngine() {
    this(128);
  }
  
  public RijndaelEngine(int paramInt) {
    switch (paramInt) {
      case 128:
        this.BC = 32;
        this.BC_MASK = 4294967295L;
        this.shifts0SC = shifts0[0];
        this.shifts1SC = shifts1[0];
        break;
      case 160:
        this.BC = 40;
        this.BC_MASK = 1099511627775L;
        this.shifts0SC = shifts0[1];
        this.shifts1SC = shifts1[1];
        break;
      case 192:
        this.BC = 48;
        this.BC_MASK = 281474976710655L;
        this.shifts0SC = shifts0[2];
        this.shifts1SC = shifts1[2];
        break;
      case 224:
        this.BC = 56;
        this.BC_MASK = 72057594037927935L;
        this.shifts0SC = shifts0[3];
        this.shifts1SC = shifts1[3];
        break;
      case 256:
        this.BC = 64;
        this.BC_MASK = -1L;
        this.shifts0SC = shifts0[4];
        this.shifts1SC = shifts1[4];
        break;
      default:
        throw new IllegalArgumentException("unknown blocksize to Rijndael");
    } 
    this.blockBits = paramInt;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof KeyParameter) {
      this.workingKey = generateWorkingKey(((KeyParameter)paramCipherParameters).getKey());
      this.forEncryption = paramBoolean;
      return;
    } 
    throw new IllegalArgumentException("invalid parameter passed to Rijndael init - " + paramCipherParameters.getClass().getName());
  }
  
  public String getAlgorithmName() {
    return "Rijndael";
  }
  
  public int getBlockSize() {
    return this.BC / 2;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.workingKey == null)
      throw new IllegalStateException("Rijndael engine not initialised"); 
    if (paramInt1 + this.BC / 2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.BC / 2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.forEncryption) {
      unpackBlock(paramArrayOfbyte1, paramInt1);
      encryptBlock(this.workingKey);
      packBlock(paramArrayOfbyte2, paramInt2);
    } else {
      unpackBlock(paramArrayOfbyte1, paramInt1);
      decryptBlock(this.workingKey);
      packBlock(paramArrayOfbyte2, paramInt2);
    } 
    return this.BC / 2;
  }
  
  public void reset() {}
  
  private void unpackBlock(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramInt;
    this.A0 = (paramArrayOfbyte[i++] & 0xFF);
    this.A1 = (paramArrayOfbyte[i++] & 0xFF);
    this.A2 = (paramArrayOfbyte[i++] & 0xFF);
    this.A3 = (paramArrayOfbyte[i++] & 0xFF);
    for (byte b = 8; b != this.BC; b += 8) {
      this.A0 |= (paramArrayOfbyte[i++] & 0xFF) << b;
      this.A1 |= (paramArrayOfbyte[i++] & 0xFF) << b;
      this.A2 |= (paramArrayOfbyte[i++] & 0xFF) << b;
      this.A3 |= (paramArrayOfbyte[i++] & 0xFF) << b;
    } 
  }
  
  private void packBlock(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramInt;
    for (boolean bool = false; bool != this.BC; bool += true) {
      paramArrayOfbyte[i++] = (byte)(int)(this.A0 >> bool);
      paramArrayOfbyte[i++] = (byte)(int)(this.A1 >> bool);
      paramArrayOfbyte[i++] = (byte)(int)(this.A2 >> bool);
      paramArrayOfbyte[i++] = (byte)(int)(this.A3 >> bool);
    } 
  }
  
  private void encryptBlock(long[][] paramArrayOflong) {
    KeyAddition(paramArrayOflong[0]);
    for (byte b = 1; b < this.ROUNDS; b++) {
      Substitution(S);
      ShiftRow(this.shifts0SC);
      MixColumn();
      KeyAddition(paramArrayOflong[b]);
    } 
    Substitution(S);
    ShiftRow(this.shifts0SC);
    KeyAddition(paramArrayOflong[this.ROUNDS]);
  }
  
  private void decryptBlock(long[][] paramArrayOflong) {
    KeyAddition(paramArrayOflong[this.ROUNDS]);
    Substitution(Si);
    ShiftRow(this.shifts1SC);
    for (int i = this.ROUNDS - 1; i > 0; i--) {
      KeyAddition(paramArrayOflong[i]);
      InvMixColumn();
      Substitution(Si);
      ShiftRow(this.shifts1SC);
    } 
    KeyAddition(paramArrayOflong[0]);
  }
}
