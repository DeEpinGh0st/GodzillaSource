package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class DSTU7564Digest implements ExtendedDigest, Memoable {
  private static final int ROWS = 8;
  
  private static final int REDUCTIONAL_POLYNOMIAL = 285;
  
  private static final int BITS_IN_BYTE = 8;
  
  private static final int NB_512 = 8;
  
  private static final int NB_1024 = 16;
  
  private static final int NR_512 = 10;
  
  private static final int NR_1024 = 14;
  
  private static final int STATE_BYTES_SIZE_512 = 64;
  
  private static final int STATE_BYTES_SIZE_1024 = 128;
  
  private int hashSize;
  
  private int blockSize;
  
  private int columns;
  
  private int rounds;
  
  private byte[] padded;
  
  private byte[][] state;
  
  private byte[][] tempState1;
  
  private byte[][] tempState2;
  
  private byte[] tempBuffer;
  
  private byte[] mixColumnsResult;
  
  private long[] tempLongBuffer;
  
  private long inputLength;
  
  private int bufOff;
  
  private byte[] buf;
  
  private static final byte[][] mds_matrix = new byte[][] { { 1, 1, 5, 1, 8, 6, 7, 4 }, { 4, 1, 1, 5, 1, 8, 6, 7 }, { 7, 4, 1, 1, 5, 1, 8, 6 }, { 6, 7, 4, 1, 1, 5, 1, 8 }, { 8, 6, 7, 4, 1, 1, 5, 1 }, { 1, 8, 6, 7, 4, 1, 1, 5 }, { 5, 1, 8, 6, 7, 4, 1, 1 }, { 1, 5, 1, 8, 6, 7, 4, 1 } };
  
  private static final byte[][] sBoxes = new byte[][] { { 
        -88, 67, 95, 6, 107, 117, 108, 89, 113, -33, 
        -121, -107, 23, -16, -40, 9, 109, -13, 29, -53, 
        -55, 77, 44, -81, 121, -32, -105, -3, 111, 75, 
        69, 57, 62, -35, -93, 79, -76, -74, -102, 14, 
        31, -65, 21, -31, 73, -46, -109, -58, -110, 114, 
        -98, 97, -47, 99, -6, -18, -12, 25, -43, -83, 
        88, -92, -69, -95, -36, -14, -125, 55, 66, -28, 
        122, 50, -100, -52, -85, 74, -113, 110, 4, 39, 
        46, -25, -30, 90, -106, 22, 35, 43, -62, 101, 
        102, 15, -68, -87, 71, 65, 52, 72, -4, -73, 
        106, -120, -91, 83, -122, -7, 91, -37, 56, 123, 
        -61, 30, 34, 51, 36, 40, 54, -57, -78, 59, 
        -114, 119, -70, -11, 20, -97, 8, 85, -101, 76, 
        -2, 96, 92, -38, 24, 70, -51, 125, 33, -80, 
        63, 27, -119, -1, -21, -124, 105, 58, -99, -41, 
        -45, 112, 103, 64, -75, -34, 93, 48, -111, -79, 
        120, 17, 1, -27, 0, 104, -104, -96, -59, 2, 
        -90, 116, 45, 11, -94, 118, -77, -66, -50, -67, 
        -82, -23, -118, 49, 28, -20, -15, -103, -108, -86, 
        -10, 38, 47, -17, -24, -116, 53, 3, -44, Byte.MAX_VALUE, 
        -5, 5, -63, 94, -112, 32, 61, -126, -9, -22, 
        10, 13, 126, -8, 80, 26, -60, 7, 87, -72, 
        60, 98, -29, -56, -84, 82, 100, 16, -48, -39, 
        19, 12, 18, 41, 81, -71, -49, -42, 115, -115, 
        -127, 84, -64, -19, 78, 68, -89, 42, -123, 37, 
        -26, -54, 124, -117, 86, Byte.MIN_VALUE }, { 
        -50, -69, -21, -110, -22, -53, 19, -63, -23, 58, 
        -42, -78, -46, -112, 23, -8, 66, 21, 86, -76, 
        101, 28, -120, 67, -59, 92, 54, -70, -11, 87, 
        103, -115, 49, -10, 100, 88, -98, -12, 34, -86, 
        117, 15, 2, -79, -33, 109, 115, 77, 124, 38, 
        46, -9, 8, 93, 68, 62, -97, 20, -56, -82, 
        84, 16, -40, -68, 26, 107, 105, -13, -67, 51, 
        -85, -6, -47, -101, 104, 78, 22, -107, -111, -18, 
        76, 99, -114, 91, -52, 60, 25, -95, -127, 73, 
        123, -39, 111, 55, 96, -54, -25, 43, 72, -3, 
        -106, 69, -4, 65, 18, 13, 121, -27, -119, -116, 
        -29, 32, 48, -36, -73, 108, 74, -75, 63, -105, 
        -44, 98, 45, 6, -92, -91, -125, 95, 42, -38, 
        -55, 0, 126, -94, 85, -65, 17, -43, -100, -49, 
        14, 10, 61, 81, 125, -109, 27, -2, -60, 71, 
        9, -122, 11, -113, -99, 106, 7, -71, -80, -104, 
        24, 50, 113, 75, -17, 59, 112, -96, -28, 64, 
        -1, -61, -87, -26, 120, -7, -117, 70, Byte.MIN_VALUE, 30, 
        56, -31, -72, -88, -32, 12, 35, 118, 29, 37, 
        36, 5, -15, 110, -108, 40, -102, -124, -24, -93, 
        79, 119, -45, -123, -30, 82, -14, -126, 80, 122, 
        47, 116, 83, -77, 97, -81, 57, 53, -34, -51, 
        31, -103, -84, -83, 114, 44, -35, -48, -121, -66, 
        94, -90, -20, 4, -58, 3, 52, -5, -37, 89, 
        -74, -62, 1, -16, 90, -19, -89, 102, 33, Byte.MAX_VALUE, 
        -118, 39, -57, -64, 41, -41 }, { 
        -109, -39, -102, -75, -104, 34, 69, -4, -70, 106, 
        -33, 2, -97, -36, 81, 89, 74, 23, 43, -62, 
        -108, -12, -69, -93, 98, -28, 113, -44, -51, 112, 
        22, -31, 73, 60, -64, -40, 92, -101, -83, -123, 
        83, -95, 122, -56, 45, -32, -47, 114, -90, 44, 
        -60, -29, 118, 120, -73, -76, 9, 59, 14, 65, 
        76, -34, -78, -112, 37, -91, -41, 3, 17, 0, 
        -61, 46, -110, -17, 78, 18, -99, 125, -53, 53, 
        16, -43, 79, -98, 77, -87, 85, -58, -48, 123, 
        24, -105, -45, 54, -26, 72, 86, -127, -113, 119, 
        -52, -100, -71, -30, -84, -72, 47, 21, -92, 124, 
        -38, 56, 30, 11, 5, -42, 20, 110, 108, 126, 
        102, -3, -79, -27, 96, -81, 94, 51, -121, -55, 
        -16, 93, 109, 63, -120, -115, -57, -9, 29, -23, 
        -20, -19, Byte.MIN_VALUE, 41, 39, -49, -103, -88, 80, 15, 
        55, 36, 40, 48, -107, -46, 62, 91, 64, -125, 
        -77, 105, 87, 31, 7, 28, -118, -68, 32, -21, 
        -50, -114, -85, -18, 49, -94, 115, -7, -54, 58, 
        26, -5, 13, -63, -2, -6, -14, 111, -67, -106, 
        -35, 67, 82, -74, 8, -13, -82, -66, 25, -119, 
        50, 38, -80, -22, 75, 100, -124, -126, 107, -11, 
        121, -65, 1, 95, 117, 99, 27, 35, 61, 104, 
        42, 101, -24, -111, -10, -1, 19, 88, -15, 71, 
        10, Byte.MAX_VALUE, -59, -89, -25, 97, 90, 6, 70, 68, 
        66, 4, -96, -37, 57, -122, 84, -86, -116, 52, 
        33, -117, -8, 12, 116, 103 }, { 
        104, -115, -54, 77, 115, 75, 78, 42, -44, 82, 
        38, -77, 84, 30, 25, 31, 34, 3, 70, 61, 
        45, 74, 83, -125, 19, -118, -73, -43, 37, 121, 
        -11, -67, 88, 47, 13, 2, -19, 81, -98, 17, 
        -14, 62, 85, 94, -47, 22, 60, 102, 112, 93, 
        -13, 69, 64, -52, -24, -108, 86, 8, -50, 26, 
        58, -46, -31, -33, -75, 56, 110, 14, -27, -12, 
        -7, -122, -23, 79, -42, -123, 35, -49, 50, -103, 
        49, 20, -82, -18, -56, 72, -45, 48, -95, -110, 
        65, -79, 24, -60, 44, 113, 114, 68, 21, -3, 
        55, -66, 95, -86, -101, -120, -40, -85, -119, -100, 
        -6, 96, -22, -68, 98, 12, 36, -90, -88, -20, 
        103, 32, -37, 124, 40, -35, -84, 91, 52, 126, 
        16, -15, 123, -113, 99, -96, 5, -102, 67, 119, 
        33, -65, 39, 9, -61, -97, -74, -41, 41, -62, 
        -21, -64, -92, -117, -116, 29, -5, -1, -63, -78, 
        -105, 46, -8, 101, -10, 117, 7, 4, 73, 51, 
        -28, -39, -71, -48, 66, -57, 108, -112, 0, -114, 
        111, 80, 1, -59, -38, 71, 63, -51, 105, -94, 
        -30, 122, -89, -58, -109, 15, 10, 6, -26, 43, 
        -106, -93, 28, -81, 106, 18, -124, 57, -25, -80, 
        -126, -9, -2, -99, -121, 92, -127, 53, -34, -76, 
        -91, -4, Byte.MIN_VALUE, -17, -53, -69, 107, 118, -70, 90, 
        125, 120, 11, -107, -29, -83, 116, -104, 59, 54, 
        100, 109, -36, -16, 89, -87, 76, 23, Byte.MAX_VALUE, -111, 
        -72, -55, 87, 27, -32, 97 } };
  
  public DSTU7564Digest(DSTU7564Digest paramDSTU7564Digest) {
    copyIn(paramDSTU7564Digest);
  }
  
  private void copyIn(DSTU7564Digest paramDSTU7564Digest) {
    this.hashSize = paramDSTU7564Digest.hashSize;
    this.blockSize = paramDSTU7564Digest.blockSize;
    this.columns = paramDSTU7564Digest.columns;
    this.rounds = paramDSTU7564Digest.rounds;
    this.padded = Arrays.clone(paramDSTU7564Digest.padded);
    this.state = Arrays.clone(paramDSTU7564Digest.state);
    this.tempState1 = Arrays.clone(paramDSTU7564Digest.tempState1);
    this.tempState2 = Arrays.clone(paramDSTU7564Digest.tempState2);
    this.tempBuffer = Arrays.clone(paramDSTU7564Digest.tempBuffer);
    this.mixColumnsResult = Arrays.clone(paramDSTU7564Digest.mixColumnsResult);
    this.tempLongBuffer = Arrays.clone(paramDSTU7564Digest.tempLongBuffer);
    this.inputLength = paramDSTU7564Digest.inputLength;
    this.bufOff = paramDSTU7564Digest.bufOff;
    this.buf = Arrays.clone(paramDSTU7564Digest.buf);
  }
  
  public DSTU7564Digest(int paramInt) {
    if (paramInt == 256 || paramInt == 384 || paramInt == 512) {
      this.hashSize = paramInt / 8;
    } else {
      throw new IllegalArgumentException("Hash size is not recommended. Use 256/384/512 instead");
    } 
    if (paramInt > 256) {
      this.blockSize = 128;
      this.columns = 16;
      this.rounds = 14;
      this.state = new byte[128][];
    } else {
      this.blockSize = 64;
      this.columns = 8;
      this.rounds = 10;
      this.state = new byte[64][];
    } 
    byte b;
    for (b = 0; b < this.state.length; b++)
      this.state[b] = new byte[this.columns]; 
    this.state[0][0] = (byte)this.state.length;
    this.padded = null;
    this.tempState1 = new byte[128][];
    this.tempState2 = new byte[128][];
    for (b = 0; b < this.state.length; b++) {
      this.tempState1[b] = new byte[8];
      this.tempState2[b] = new byte[8];
    } 
    this.tempBuffer = new byte[16];
    this.mixColumnsResult = new byte[8];
    this.tempLongBuffer = new long[this.columns];
    this.buf = new byte[this.blockSize];
  }
  
  public String getAlgorithmName() {
    return "DSTU7564";
  }
  
  public int getDigestSize() {
    return this.hashSize;
  }
  
  public int getByteLength() {
    return this.blockSize;
  }
  
  public void update(byte paramByte) {
    this.buf[this.bufOff++] = paramByte;
    if (this.bufOff == this.blockSize) {
      processBlock(this.buf, 0);
      this.bufOff = 0;
    } 
    this.inputLength++;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    while (this.bufOff != 0 && paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1++]);
      paramInt2--;
    } 
    if (paramInt2 > 0) {
      while (paramInt2 > this.blockSize) {
        processBlock(paramArrayOfbyte, paramInt1);
        paramInt1 += this.blockSize;
        this.inputLength += this.blockSize;
        paramInt2 -= this.blockSize;
      } 
      while (paramInt2 > 0) {
        update(paramArrayOfbyte[paramInt1++]);
        paramInt2--;
      } 
    } 
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    this.padded = pad(this.buf, 0, this.bufOff);
    int i = this.padded.length;
    int j = 0;
    while (i != 0) {
      processBlock(this.padded, j);
      j += this.blockSize;
      i -= this.blockSize;
    } 
    byte[][] arrayOfByte = new byte[128][];
    byte b1;
    for (b1 = 0; b1 < this.state.length; b1++) {
      arrayOfByte[b1] = new byte[8];
      System.arraycopy(this.state[b1], 0, arrayOfByte[b1], 0, 8);
    } 
    for (b1 = 0; b1 < this.rounds; b1++) {
      byte b4;
      for (b4 = 0; b4 < this.columns; b4++)
        arrayOfByte[b4][0] = (byte)(arrayOfByte[b4][0] ^ (byte)(b4 * 16 ^ b1)); 
      for (b4 = 0; b4 < 8; b4++) {
        for (byte b6 = 0; b6 < this.columns; b6++)
          arrayOfByte[b6][b4] = sBoxes[b4 % 4][arrayOfByte[b6][b4] & 0xFF]; 
      } 
      b4 = -1;
      byte b5;
      for (b5 = 0; b5 < 8; b5++) {
        if (b5 == 7 && this.columns == 16) {
          b4 = 11;
        } else {
          b4++;
        } 
        byte b6;
        for (b6 = 0; b6 < this.columns; b6++)
          this.tempBuffer[(b6 + b4) % this.columns] = arrayOfByte[b6][b5]; 
        for (b6 = 0; b6 < this.columns; b6++)
          arrayOfByte[b6][b5] = this.tempBuffer[b6]; 
      } 
      for (byte b = 0; b < this.columns; b++) {
        Arrays.fill(this.mixColumnsResult, (byte)0);
        byte b6;
        for (b6 = 7; b6 >= 0; b6--) {
          b5 = 0;
          for (byte b7 = 7; b7 >= 0; b7--)
            b5 = (byte)(b5 ^ multiplyGF(arrayOfByte[b][b7], mds_matrix[b6][b7])); 
          this.mixColumnsResult[b6] = b5;
        } 
        for (b6 = 0; b6 < 8; b6++)
          arrayOfByte[b][b6] = this.mixColumnsResult[b6]; 
      } 
    } 
    for (b1 = 0; b1 < 8; b1++) {
      for (byte b = 0; b < this.columns; b++)
        this.state[b][b1] = (byte)(this.state[b][b1] ^ arrayOfByte[b][b1]); 
    } 
    byte[] arrayOfByte1 = new byte[8 * this.columns];
    byte b2 = 0;
    for (byte b3 = 0; b3 < this.columns; b3++) {
      for (byte b = 0; b < 8; b++) {
        arrayOfByte1[b2] = this.state[b3][b];
        b2++;
      } 
    } 
    System.arraycopy(arrayOfByte1, arrayOfByte1.length - this.hashSize, paramArrayOfbyte, paramInt, this.hashSize);
    reset();
    return this.hashSize;
  }
  
  public void reset() {
    for (byte b = 0; b < this.state.length; b++)
      this.state[b] = new byte[this.columns]; 
    this.state[0][0] = (byte)this.state.length;
    this.inputLength = 0L;
    this.bufOff = 0;
    Arrays.fill(this.buf, (byte)0);
    if (this.padded != null)
      Arrays.fill(this.padded, (byte)0); 
  }
  
  private void processBlock(byte[] paramArrayOfbyte, int paramInt) {
    byte b;
    for (b = 0; b < this.state.length; b++) {
      Arrays.fill(this.tempState1[b], (byte)0);
      Arrays.fill(this.tempState2[b], (byte)0);
    } 
    for (b = 0; b < 8; b++) {
      for (byte b1 = 0; b1 < this.columns; b1++) {
        this.tempState1[b1][b] = (byte)(this.state[b1][b] ^ paramArrayOfbyte[b1 * 8 + b + paramInt]);
        this.tempState2[b1][b] = paramArrayOfbyte[b1 * 8 + b + paramInt];
      } 
    } 
    P();
    Q();
    for (b = 0; b < 8; b++) {
      for (byte b1 = 0; b1 < this.columns; b1++)
        this.state[b1][b] = (byte)(this.state[b1][b] ^ (byte)(this.tempState1[b1][b] ^ this.tempState2[b1][b])); 
    } 
  }
  
  private void Q() {
    for (byte b = 0; b < this.rounds; b++) {
      byte b1;
      for (b1 = 0; b1 < this.columns; b1++) {
        this.tempLongBuffer[b1] = Pack.littleEndianToLong(this.tempState2[b1], 0);
        this.tempLongBuffer[b1] = this.tempLongBuffer[b1] + (0xF0F0F0F0F0F0F3L ^ ((this.columns - b1 - 1) * 16L ^ b) << 56L);
        Pack.longToLittleEndian(this.tempLongBuffer[b1], this.tempState2[b1], 0);
      } 
      for (b1 = 0; b1 < 8; b1++) {
        for (byte b4 = 0; b4 < this.columns; b4++)
          this.tempState2[b4][b1] = sBoxes[b1 % 4][this.tempState2[b4][b1] & 0xFF]; 
      } 
      b1 = -1;
      byte b2;
      for (b2 = 0; b2 < 8; b2++) {
        if (b2 == 7 && this.columns == 16) {
          b1 = 11;
        } else {
          b1++;
        } 
        byte b4;
        for (b4 = 0; b4 < this.columns; b4++)
          this.tempBuffer[(b4 + b1) % this.columns] = this.tempState2[b4][b2]; 
        for (b4 = 0; b4 < this.columns; b4++)
          this.tempState2[b4][b2] = this.tempBuffer[b4]; 
      } 
      for (byte b3 = 0; b3 < this.columns; b3++) {
        Arrays.fill(this.mixColumnsResult, (byte)0);
        byte b4;
        for (b4 = 7; b4 >= 0; b4--) {
          b2 = 0;
          for (byte b5 = 7; b5 >= 0; b5--)
            b2 = (byte)(b2 ^ multiplyGF(this.tempState2[b3][b5], mds_matrix[b4][b5])); 
          this.mixColumnsResult[b4] = b2;
        } 
        for (b4 = 0; b4 < 8; b4++)
          this.tempState2[b3][b4] = this.mixColumnsResult[b4]; 
      } 
    } 
  }
  
  private void P() {
    for (byte b = 0; b < this.rounds; b++) {
      byte b1;
      for (b1 = 0; b1 < this.columns; b1++)
        this.tempState1[b1][0] = (byte)(this.tempState1[b1][0] ^ (byte)(b1 * 16 ^ b)); 
      for (b1 = 0; b1 < 8; b1++) {
        for (byte b4 = 0; b4 < this.columns; b4++)
          this.tempState1[b4][b1] = sBoxes[b1 % 4][this.tempState1[b4][b1] & 0xFF]; 
      } 
      b1 = -1;
      byte b2;
      for (b2 = 0; b2 < 8; b2++) {
        if (b2 == 7 && this.columns == 16) {
          b1 = 11;
        } else {
          b1++;
        } 
        byte b4;
        for (b4 = 0; b4 < this.columns; b4++)
          this.tempBuffer[(b4 + b1) % this.columns] = this.tempState1[b4][b2]; 
        for (b4 = 0; b4 < this.columns; b4++)
          this.tempState1[b4][b2] = this.tempBuffer[b4]; 
      } 
      for (byte b3 = 0; b3 < this.columns; b3++) {
        Arrays.fill(this.mixColumnsResult, (byte)0);
        byte b4;
        for (b4 = 7; b4 >= 0; b4--) {
          b2 = 0;
          for (byte b5 = 7; b5 >= 0; b5--)
            b2 = (byte)(b2 ^ multiplyGF(this.tempState1[b3][b5], mds_matrix[b4][b5])); 
          this.mixColumnsResult[b4] = b2;
        } 
        for (b4 = 0; b4 < 8; b4++)
          this.tempState1[b3][b4] = this.mixColumnsResult[b4]; 
      } 
    } 
  }
  
  private byte multiplyGF(byte paramByte1, byte paramByte2) {
    byte b = 0;
    for (byte b1 = 0; b1 < 8; b1++) {
      if ((paramByte2 & 0x1) == 1)
        b = (byte)(b ^ paramByte1); 
      byte b2 = (byte)(paramByte1 & Byte.MIN_VALUE);
      paramByte1 = (byte)(paramByte1 << 1);
      if (b2 == Byte.MIN_VALUE)
        paramByte1 = (byte)(paramByte1 ^ 0x11D); 
      paramByte2 = (byte)(paramByte2 >> 1);
    } 
    return b;
  }
  
  private byte[] pad(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte;
    if (this.blockSize - paramInt2 < 13) {
      arrayOfByte = new byte[2 * this.blockSize];
    } else {
      arrayOfByte = new byte[this.blockSize];
    } 
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    arrayOfByte[paramInt2] = Byte.MIN_VALUE;
    Pack.longToLittleEndian(this.inputLength * 8L, arrayOfByte, arrayOfByte.length - 12);
    return arrayOfByte;
  }
  
  public Memoable copy() {
    return new DSTU7564Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    DSTU7564Digest dSTU7564Digest = (DSTU7564Digest)paramMemoable;
    copyIn(dSTU7564Digest);
  }
}
