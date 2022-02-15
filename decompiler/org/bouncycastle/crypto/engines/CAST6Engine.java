package org.bouncycastle.crypto.engines;

public final class CAST6Engine extends CAST5Engine {
  protected static final int ROUNDS = 12;
  
  protected static final int BLOCK_SIZE = 16;
  
  protected int[] _Kr = new int[48];
  
  protected int[] _Km = new int[48];
  
  protected int[] _Tr = new int[192];
  
  protected int[] _Tm = new int[192];
  
  private int[] _workingKey = new int[8];
  
  public String getAlgorithmName() {
    return "CAST6";
  }
  
  public void reset() {}
  
  public int getBlockSize() {
    return 16;
  }
  
  protected void setKey(byte[] paramArrayOfbyte) {
    int i = 1518500249;
    int j = 1859775393;
    int k = 19;
    byte b1 = 17;
    for (byte b2 = 0; b2 < 24; b2++) {
      for (byte b = 0; b < 8; b++) {
        this._Tm[b2 * 8 + b] = i;
        i += j;
        this._Tr[b2 * 8 + b] = k;
        k = k + b1 & 0x1F;
      } 
    } 
    byte[] arrayOfByte = new byte[64];
    int m = paramArrayOfbyte.length;
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, m);
    byte b3;
    for (b3 = 0; b3 < 8; b3++)
      this._workingKey[b3] = BytesTo32bits(arrayOfByte, b3 * 4); 
    for (b3 = 0; b3 < 12; b3++) {
      int n = b3 * 2 * 8;
      this._workingKey[6] = this._workingKey[6] ^ F1(this._workingKey[7], this._Tm[n], this._Tr[n]);
      this._workingKey[5] = this._workingKey[5] ^ F2(this._workingKey[6], this._Tm[n + 1], this._Tr[n + 1]);
      this._workingKey[4] = this._workingKey[4] ^ F3(this._workingKey[5], this._Tm[n + 2], this._Tr[n + 2]);
      this._workingKey[3] = this._workingKey[3] ^ F1(this._workingKey[4], this._Tm[n + 3], this._Tr[n + 3]);
      this._workingKey[2] = this._workingKey[2] ^ F2(this._workingKey[3], this._Tm[n + 4], this._Tr[n + 4]);
      this._workingKey[1] = this._workingKey[1] ^ F3(this._workingKey[2], this._Tm[n + 5], this._Tr[n + 5]);
      this._workingKey[0] = this._workingKey[0] ^ F1(this._workingKey[1], this._Tm[n + 6], this._Tr[n + 6]);
      this._workingKey[7] = this._workingKey[7] ^ F2(this._workingKey[0], this._Tm[n + 7], this._Tr[n + 7]);
      n = (b3 * 2 + 1) * 8;
      this._workingKey[6] = this._workingKey[6] ^ F1(this._workingKey[7], this._Tm[n], this._Tr[n]);
      this._workingKey[5] = this._workingKey[5] ^ F2(this._workingKey[6], this._Tm[n + 1], this._Tr[n + 1]);
      this._workingKey[4] = this._workingKey[4] ^ F3(this._workingKey[5], this._Tm[n + 2], this._Tr[n + 2]);
      this._workingKey[3] = this._workingKey[3] ^ F1(this._workingKey[4], this._Tm[n + 3], this._Tr[n + 3]);
      this._workingKey[2] = this._workingKey[2] ^ F2(this._workingKey[3], this._Tm[n + 4], this._Tr[n + 4]);
      this._workingKey[1] = this._workingKey[1] ^ F3(this._workingKey[2], this._Tm[n + 5], this._Tr[n + 5]);
      this._workingKey[0] = this._workingKey[0] ^ F1(this._workingKey[1], this._Tm[n + 6], this._Tr[n + 6]);
      this._workingKey[7] = this._workingKey[7] ^ F2(this._workingKey[0], this._Tm[n + 7], this._Tr[n + 7]);
      this._Kr[b3 * 4] = this._workingKey[0] & 0x1F;
      this._Kr[b3 * 4 + 1] = this._workingKey[2] & 0x1F;
      this._Kr[b3 * 4 + 2] = this._workingKey[4] & 0x1F;
      this._Kr[b3 * 4 + 3] = this._workingKey[6] & 0x1F;
      this._Km[b3 * 4] = this._workingKey[7];
      this._Km[b3 * 4 + 1] = this._workingKey[5];
      this._Km[b3 * 4 + 2] = this._workingKey[3];
      this._Km[b3 * 4 + 3] = this._workingKey[1];
    } 
  }
  
  protected int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int[] arrayOfInt = new int[4];
    int i = BytesTo32bits(paramArrayOfbyte1, paramInt1);
    int j = BytesTo32bits(paramArrayOfbyte1, paramInt1 + 4);
    int k = BytesTo32bits(paramArrayOfbyte1, paramInt1 + 8);
    int m = BytesTo32bits(paramArrayOfbyte1, paramInt1 + 12);
    CAST_Encipher(i, j, k, m, arrayOfInt);
    Bits32ToBytes(arrayOfInt[0], paramArrayOfbyte2, paramInt2);
    Bits32ToBytes(arrayOfInt[1], paramArrayOfbyte2, paramInt2 + 4);
    Bits32ToBytes(arrayOfInt[2], paramArrayOfbyte2, paramInt2 + 8);
    Bits32ToBytes(arrayOfInt[3], paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  protected int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int[] arrayOfInt = new int[4];
    int i = BytesTo32bits(paramArrayOfbyte1, paramInt1);
    int j = BytesTo32bits(paramArrayOfbyte1, paramInt1 + 4);
    int k = BytesTo32bits(paramArrayOfbyte1, paramInt1 + 8);
    int m = BytesTo32bits(paramArrayOfbyte1, paramInt1 + 12);
    CAST_Decipher(i, j, k, m, arrayOfInt);
    Bits32ToBytes(arrayOfInt[0], paramArrayOfbyte2, paramInt2);
    Bits32ToBytes(arrayOfInt[1], paramArrayOfbyte2, paramInt2 + 4);
    Bits32ToBytes(arrayOfInt[2], paramArrayOfbyte2, paramInt2 + 8);
    Bits32ToBytes(arrayOfInt[3], paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  protected final void CAST_Encipher(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint) {
    byte b;
    for (b = 0; b < 6; b++) {
      int i = b * 4;
      paramInt3 ^= F1(paramInt4, this._Km[i], this._Kr[i]);
      paramInt2 ^= F2(paramInt3, this._Km[i + 1], this._Kr[i + 1]);
      paramInt1 ^= F3(paramInt2, this._Km[i + 2], this._Kr[i + 2]);
      paramInt4 ^= F1(paramInt1, this._Km[i + 3], this._Kr[i + 3]);
    } 
    for (b = 6; b < 12; b++) {
      int i = b * 4;
      paramInt4 ^= F1(paramInt1, this._Km[i + 3], this._Kr[i + 3]);
      paramInt1 ^= F3(paramInt2, this._Km[i + 2], this._Kr[i + 2]);
      paramInt2 ^= F2(paramInt3, this._Km[i + 1], this._Kr[i + 1]);
      paramInt3 ^= F1(paramInt4, this._Km[i], this._Kr[i]);
    } 
    paramArrayOfint[0] = paramInt1;
    paramArrayOfint[1] = paramInt2;
    paramArrayOfint[2] = paramInt3;
    paramArrayOfint[3] = paramInt4;
  }
  
  protected final void CAST_Decipher(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint) {
    byte b;
    for (b = 0; b < 6; b++) {
      int i = (11 - b) * 4;
      paramInt3 ^= F1(paramInt4, this._Km[i], this._Kr[i]);
      paramInt2 ^= F2(paramInt3, this._Km[i + 1], this._Kr[i + 1]);
      paramInt1 ^= F3(paramInt2, this._Km[i + 2], this._Kr[i + 2]);
      paramInt4 ^= F1(paramInt1, this._Km[i + 3], this._Kr[i + 3]);
    } 
    for (b = 6; b < 12; b++) {
      int i = (11 - b) * 4;
      paramInt4 ^= F1(paramInt1, this._Km[i + 3], this._Kr[i + 3]);
      paramInt1 ^= F3(paramInt2, this._Km[i + 2], this._Kr[i + 2]);
      paramInt2 ^= F2(paramInt3, this._Km[i + 1], this._Kr[i + 1]);
      paramInt3 ^= F1(paramInt4, this._Km[i], this._Kr[i]);
    } 
    paramArrayOfint[0] = paramInt1;
    paramArrayOfint[1] = paramInt2;
    paramArrayOfint[2] = paramInt3;
    paramArrayOfint[3] = paramInt4;
  }
}
