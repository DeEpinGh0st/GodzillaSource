package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class ARIAEngine implements BlockCipher {
  private static final byte[][] C = new byte[][] { Hex.decode("517cc1b727220a94fe13abe8fa9a6ee0"), Hex.decode("6db14acc9e21c820ff28b1d5ef5de2b0"), Hex.decode("db92371d2126e9700324977504e8c90e") };
  
  private static final byte[] SB1_sbox = new byte[] { 
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
  
  private static final byte[] SB2_sbox = new byte[] { 
      -30, 78, 84, -4, -108, -62, 74, -52, 98, 13, 
      106, 70, 60, 77, -117, -47, 94, -6, 100, -53, 
      -76, -105, -66, 43, -68, 119, 46, 3, -45, 25, 
      89, -63, 29, 6, 65, 107, 85, -16, -103, 105, 
      -22, -100, 24, -82, 99, -33, -25, -69, 0, 115, 
      102, -5, -106, 76, -123, -28, 58, 9, 69, -86, 
      15, -18, 16, -21, 45, Byte.MAX_VALUE, -12, 41, -84, -49, 
      -83, -111, -115, 120, -56, -107, -7, 47, -50, -51, 
      8, 122, -120, 56, 92, -125, 42, 40, 71, -37, 
      -72, -57, -109, -92, 18, 83, -1, -121, 14, 49, 
      54, 33, 88, 72, 1, -114, 55, 116, 50, -54, 
      -23, -79, -73, -85, 12, -41, -60, 86, 66, 38, 
      7, -104, 96, -39, -74, -71, 17, 64, -20, 32, 
      -116, -67, -96, -55, -124, 4, 73, 35, -15, 79, 
      80, 31, 19, -36, -40, -64, -98, 87, -29, -61, 
      123, 101, 59, 2, -113, 62, -24, 37, -110, -27, 
      21, -35, -3, 23, -87, -65, -44, -102, 126, -59, 
      57, 103, -2, 118, -99, 67, -89, -31, -48, -11, 
      104, -14, 27, 52, 112, 5, -93, -118, -43, 121, 
      -122, -88, 48, -58, 81, 75, 30, -90, 39, -10, 
      53, -46, 110, 36, 22, -126, 95, -38, -26, 117, 
      -94, -17, 44, -78, 28, -97, 93, 111, Byte.MIN_VALUE, 10, 
      114, 68, -101, 108, -112, 11, 91, 51, 125, 90, 
      82, -13, 97, -95, -9, -80, -42, 63, 124, 109, 
      -19, 20, -32, -91, 61, 34, -77, -8, -119, -34, 
      113, 26, -81, -70, -75, -127 };
  
  private static final byte[] SB3_sbox = new byte[] { 
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
  
  private static final byte[] SB4_sbox = new byte[] { 
      48, 104, -103, 27, -121, -71, 33, 120, 80, 57, 
      -37, -31, 114, 9, 98, 60, 62, 126, 94, -114, 
      -15, -96, -52, -93, 42, 29, -5, -74, -42, 32, 
      -60, -115, -127, 101, -11, -119, -53, -99, 119, -58, 
      87, 67, 86, 23, -44, 64, 26, 77, -64, 99, 
      108, -29, -73, -56, 100, 106, 83, -86, 56, -104, 
      12, -12, -101, -19, Byte.MAX_VALUE, 34, 118, -81, -35, 58, 
      11, 88, 103, -120, 6, -61, 53, 13, 1, -117, 
      -116, -62, -26, 95, 2, 36, 117, -109, 102, 30, 
      -27, -30, 84, -40, 16, -50, 122, -24, 8, 44, 
      18, -105, 50, -85, -76, 39, 10, 35, -33, -17, 
      -54, -39, -72, -6, -36, 49, 107, -47, -83, 25, 
      73, -67, 81, -106, -18, -28, -88, 65, -38, -1, 
      -51, 85, -122, 54, -66, 97, 82, -8, -69, 14, 
      -126, 72, 105, -102, -32, 71, -98, 92, 4, 75, 
      52, 21, 121, 38, -89, -34, 41, -82, -110, -41, 
      -124, -23, -46, -70, 93, -13, -59, -80, -65, -92, 
      59, 113, 68, 70, 43, -4, -21, 111, -43, -10, 
      20, -2, 124, 112, 90, 125, -3, 47, 24, -125, 
      22, -91, -111, 31, 5, -107, 116, -87, -63, 91, 
      74, -123, 109, 19, 7, 79, 78, 69, -78, 15, 
      -55, 28, -90, -68, -20, 115, -112, 123, -49, 89, 
      -113, -95, -7, 45, -14, -79, 0, -108, 55, -97, 
      -48, 46, -100, 110, 40, 63, Byte.MIN_VALUE, -16, 61, -45, 
      37, -118, -75, -25, 66, -77, -57, -22, -9, 76, 
      17, 51, 3, -94, -84, 96 };
  
  protected static final int BLOCK_SIZE = 16;
  
  private byte[][] roundKeys;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("invalid parameter passed to ARIA init - " + paramCipherParameters.getClass().getName()); 
    this.roundKeys = keySchedule(paramBoolean, ((KeyParameter)paramCipherParameters).getKey());
  }
  
  public String getAlgorithmName() {
    return "ARIA";
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (this.roundKeys == null)
      throw new IllegalStateException("ARIA engine not initialised"); 
    if (paramInt1 > paramArrayOfbyte1.length - 16)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 > paramArrayOfbyte2.length - 16)
      throw new OutputLengthException("output buffer too short"); 
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(paramArrayOfbyte1, paramInt1, arrayOfByte, 0, 16);
    byte b = 0;
    int i = this.roundKeys.length - 3;
    while (b < i) {
      FO(arrayOfByte, this.roundKeys[b++]);
      FE(arrayOfByte, this.roundKeys[b++]);
    } 
    FO(arrayOfByte, this.roundKeys[b++]);
    xor(arrayOfByte, this.roundKeys[b++]);
    SL2(arrayOfByte);
    xor(arrayOfByte, this.roundKeys[b]);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte2, paramInt2, 16);
    return 16;
  }
  
  public void reset() {}
  
  protected static void A(byte[] paramArrayOfbyte) {
    byte b1 = paramArrayOfbyte[0];
    byte b2 = paramArrayOfbyte[1];
    byte b3 = paramArrayOfbyte[2];
    byte b4 = paramArrayOfbyte[3];
    byte b5 = paramArrayOfbyte[4];
    byte b6 = paramArrayOfbyte[5];
    byte b7 = paramArrayOfbyte[6];
    byte b8 = paramArrayOfbyte[7];
    byte b9 = paramArrayOfbyte[8];
    byte b10 = paramArrayOfbyte[9];
    byte b11 = paramArrayOfbyte[10];
    byte b12 = paramArrayOfbyte[11];
    byte b13 = paramArrayOfbyte[12];
    byte b14 = paramArrayOfbyte[13];
    byte b15 = paramArrayOfbyte[14];
    byte b16 = paramArrayOfbyte[15];
    paramArrayOfbyte[0] = (byte)(b4 ^ b5 ^ b7 ^ b9 ^ b10 ^ b14 ^ b15);
    paramArrayOfbyte[1] = (byte)(b3 ^ b6 ^ b8 ^ b9 ^ b10 ^ b13 ^ b16);
    paramArrayOfbyte[2] = (byte)(b2 ^ b5 ^ b7 ^ b11 ^ b12 ^ b13 ^ b16);
    paramArrayOfbyte[3] = (byte)(b1 ^ b6 ^ b8 ^ b11 ^ b12 ^ b14 ^ b15);
    paramArrayOfbyte[4] = (byte)(b1 ^ b3 ^ b6 ^ b9 ^ b12 ^ b15 ^ b16);
    paramArrayOfbyte[5] = (byte)(b2 ^ b4 ^ b5 ^ b10 ^ b11 ^ b15 ^ b16);
    paramArrayOfbyte[6] = (byte)(b1 ^ b3 ^ b8 ^ b10 ^ b11 ^ b13 ^ b14);
    paramArrayOfbyte[7] = (byte)(b2 ^ b4 ^ b7 ^ b9 ^ b12 ^ b13 ^ b14);
    paramArrayOfbyte[8] = (byte)(b1 ^ b2 ^ b5 ^ b8 ^ b11 ^ b14 ^ b16);
    paramArrayOfbyte[9] = (byte)(b1 ^ b2 ^ b6 ^ b7 ^ b12 ^ b13 ^ b15);
    paramArrayOfbyte[10] = (byte)(b3 ^ b4 ^ b6 ^ b7 ^ b9 ^ b14 ^ b16);
    paramArrayOfbyte[11] = (byte)(b3 ^ b4 ^ b5 ^ b8 ^ b10 ^ b13 ^ b15);
    paramArrayOfbyte[12] = (byte)(b2 ^ b3 ^ b7 ^ b8 ^ b10 ^ b12 ^ b13);
    paramArrayOfbyte[13] = (byte)(b1 ^ b4 ^ b7 ^ b8 ^ b9 ^ b11 ^ b14);
    paramArrayOfbyte[14] = (byte)(b1 ^ b4 ^ b5 ^ b6 ^ b10 ^ b12 ^ b15);
    paramArrayOfbyte[15] = (byte)(b2 ^ b3 ^ b5 ^ b6 ^ b9 ^ b11 ^ b16);
  }
  
  protected static void FE(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    xor(paramArrayOfbyte1, paramArrayOfbyte2);
    SL2(paramArrayOfbyte1);
    A(paramArrayOfbyte1);
  }
  
  protected static void FO(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    xor(paramArrayOfbyte1, paramArrayOfbyte2);
    SL1(paramArrayOfbyte1);
    A(paramArrayOfbyte1);
  }
  
  protected static byte[][] keySchedule(boolean paramBoolean, byte[] paramArrayOfbyte) {
    int i = paramArrayOfbyte.length;
    if (i < 16 || i > 32 || (i & 0x7) != 0)
      throw new IllegalArgumentException("Key length not 128/192/256 bits."); 
    int j = (i >>> 3) - 2;
    byte[] arrayOfByte1 = C[j];
    byte[] arrayOfByte2 = C[(j + 1) % 3];
    byte[] arrayOfByte3 = C[(j + 2) % 3];
    byte[] arrayOfByte4 = new byte[16];
    byte[] arrayOfByte5 = new byte[16];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte4, 0, 16);
    System.arraycopy(paramArrayOfbyte, 16, arrayOfByte5, 0, i - 16);
    byte[] arrayOfByte6 = new byte[16];
    byte[] arrayOfByte7 = new byte[16];
    byte[] arrayOfByte8 = new byte[16];
    byte[] arrayOfByte9 = new byte[16];
    System.arraycopy(arrayOfByte4, 0, arrayOfByte6, 0, 16);
    System.arraycopy(arrayOfByte6, 0, arrayOfByte7, 0, 16);
    FO(arrayOfByte7, arrayOfByte1);
    xor(arrayOfByte7, arrayOfByte5);
    System.arraycopy(arrayOfByte7, 0, arrayOfByte8, 0, 16);
    FE(arrayOfByte8, arrayOfByte2);
    xor(arrayOfByte8, arrayOfByte6);
    System.arraycopy(arrayOfByte8, 0, arrayOfByte9, 0, 16);
    FO(arrayOfByte9, arrayOfByte3);
    xor(arrayOfByte9, arrayOfByte7);
    int k = 12 + j * 2;
    byte[][] arrayOfByte = new byte[k + 1][16];
    keyScheduleRound(arrayOfByte[0], arrayOfByte6, arrayOfByte7, 19);
    keyScheduleRound(arrayOfByte[1], arrayOfByte7, arrayOfByte8, 19);
    keyScheduleRound(arrayOfByte[2], arrayOfByte8, arrayOfByte9, 19);
    keyScheduleRound(arrayOfByte[3], arrayOfByte9, arrayOfByte6, 19);
    keyScheduleRound(arrayOfByte[4], arrayOfByte6, arrayOfByte7, 31);
    keyScheduleRound(arrayOfByte[5], arrayOfByte7, arrayOfByte8, 31);
    keyScheduleRound(arrayOfByte[6], arrayOfByte8, arrayOfByte9, 31);
    keyScheduleRound(arrayOfByte[7], arrayOfByte9, arrayOfByte6, 31);
    keyScheduleRound(arrayOfByte[8], arrayOfByte6, arrayOfByte7, 67);
    keyScheduleRound(arrayOfByte[9], arrayOfByte7, arrayOfByte8, 67);
    keyScheduleRound(arrayOfByte[10], arrayOfByte8, arrayOfByte9, 67);
    keyScheduleRound(arrayOfByte[11], arrayOfByte9, arrayOfByte6, 67);
    keyScheduleRound(arrayOfByte[12], arrayOfByte6, arrayOfByte7, 97);
    if (k > 12) {
      keyScheduleRound(arrayOfByte[13], arrayOfByte7, arrayOfByte8, 97);
      keyScheduleRound(arrayOfByte[14], arrayOfByte8, arrayOfByte9, 97);
      if (k > 14) {
        keyScheduleRound(arrayOfByte[15], arrayOfByte9, arrayOfByte6, 97);
        keyScheduleRound(arrayOfByte[16], arrayOfByte6, arrayOfByte7, 109);
      } 
    } 
    if (!paramBoolean) {
      reverseKeys(arrayOfByte);
      for (byte b = 1; b < k; b++)
        A(arrayOfByte[b]); 
    } 
    return arrayOfByte;
  }
  
  protected static void keyScheduleRound(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt) {
    int i = paramInt >>> 3;
    int j = paramInt & 0x7;
    int k = 8 - j;
    int m = paramArrayOfbyte3[15 - i] & 0xFF;
    for (byte b = 0; b < 16; b++) {
      int n = paramArrayOfbyte3[b - i & 0xF] & 0xFF;
      int i1 = m << k | n >>> j;
      i1 ^= paramArrayOfbyte2[b] & 0xFF;
      paramArrayOfbyte1[b] = (byte)i1;
      m = n;
    } 
  }
  
  protected static void reverseKeys(byte[][] paramArrayOfbyte) {
    int i = paramArrayOfbyte.length;
    int j = i / 2;
    int k = i - 1;
    for (byte b = 0; b < j; b++) {
      byte[] arrayOfByte = paramArrayOfbyte[b];
      paramArrayOfbyte[b] = paramArrayOfbyte[k - b];
      paramArrayOfbyte[k - b] = arrayOfByte;
    } 
  }
  
  protected static byte SB1(byte paramByte) {
    return SB1_sbox[paramByte & 0xFF];
  }
  
  protected static byte SB2(byte paramByte) {
    return SB2_sbox[paramByte & 0xFF];
  }
  
  protected static byte SB3(byte paramByte) {
    return SB3_sbox[paramByte & 0xFF];
  }
  
  protected static byte SB4(byte paramByte) {
    return SB4_sbox[paramByte & 0xFF];
  }
  
  protected static void SL1(byte[] paramArrayOfbyte) {
    paramArrayOfbyte[0] = SB1(paramArrayOfbyte[0]);
    paramArrayOfbyte[1] = SB2(paramArrayOfbyte[1]);
    paramArrayOfbyte[2] = SB3(paramArrayOfbyte[2]);
    paramArrayOfbyte[3] = SB4(paramArrayOfbyte[3]);
    paramArrayOfbyte[4] = SB1(paramArrayOfbyte[4]);
    paramArrayOfbyte[5] = SB2(paramArrayOfbyte[5]);
    paramArrayOfbyte[6] = SB3(paramArrayOfbyte[6]);
    paramArrayOfbyte[7] = SB4(paramArrayOfbyte[7]);
    paramArrayOfbyte[8] = SB1(paramArrayOfbyte[8]);
    paramArrayOfbyte[9] = SB2(paramArrayOfbyte[9]);
    paramArrayOfbyte[10] = SB3(paramArrayOfbyte[10]);
    paramArrayOfbyte[11] = SB4(paramArrayOfbyte[11]);
    paramArrayOfbyte[12] = SB1(paramArrayOfbyte[12]);
    paramArrayOfbyte[13] = SB2(paramArrayOfbyte[13]);
    paramArrayOfbyte[14] = SB3(paramArrayOfbyte[14]);
    paramArrayOfbyte[15] = SB4(paramArrayOfbyte[15]);
  }
  
  protected static void SL2(byte[] paramArrayOfbyte) {
    paramArrayOfbyte[0] = SB3(paramArrayOfbyte[0]);
    paramArrayOfbyte[1] = SB4(paramArrayOfbyte[1]);
    paramArrayOfbyte[2] = SB1(paramArrayOfbyte[2]);
    paramArrayOfbyte[3] = SB2(paramArrayOfbyte[3]);
    paramArrayOfbyte[4] = SB3(paramArrayOfbyte[4]);
    paramArrayOfbyte[5] = SB4(paramArrayOfbyte[5]);
    paramArrayOfbyte[6] = SB1(paramArrayOfbyte[6]);
    paramArrayOfbyte[7] = SB2(paramArrayOfbyte[7]);
    paramArrayOfbyte[8] = SB3(paramArrayOfbyte[8]);
    paramArrayOfbyte[9] = SB4(paramArrayOfbyte[9]);
    paramArrayOfbyte[10] = SB1(paramArrayOfbyte[10]);
    paramArrayOfbyte[11] = SB2(paramArrayOfbyte[11]);
    paramArrayOfbyte[12] = SB3(paramArrayOfbyte[12]);
    paramArrayOfbyte[13] = SB4(paramArrayOfbyte[13]);
    paramArrayOfbyte[14] = SB1(paramArrayOfbyte[14]);
    paramArrayOfbyte[15] = SB2(paramArrayOfbyte[15]);
  }
  
  protected static void xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    for (byte b = 0; b < 16; b++)
      paramArrayOfbyte1[b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]); 
  }
}
