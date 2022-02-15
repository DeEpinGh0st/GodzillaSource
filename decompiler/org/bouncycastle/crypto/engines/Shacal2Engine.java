package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class Shacal2Engine implements BlockCipher {
  private static final int[] K = new int[] { 
      1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 
      607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 
      770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 
      113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, 
      -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 
      659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, 
      -1866530822, -1538233109, -1090935817, -965641998 };
  
  private static final int BLOCK_SIZE = 32;
  
  private boolean forEncryption = false;
  
  private static final int ROUNDS = 64;
  
  private int[] workingKey = null;
  
  public void reset() {}
  
  public String getAlgorithmName() {
    return "Shacal2";
  }
  
  public int getBlockSize() {
    return 32;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("only simple KeyParameter expected."); 
    this.forEncryption = paramBoolean;
    this.workingKey = new int[64];
    setKey(((KeyParameter)paramCipherParameters).getKey());
  }
  
  public void setKey(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length == 0 || paramArrayOfbyte.length > 64 || paramArrayOfbyte.length < 16 || paramArrayOfbyte.length % 8 != 0)
      throw new IllegalArgumentException("Shacal2-key must be 16 - 64 bytes and multiple of 8"); 
    bytes2ints(paramArrayOfbyte, this.workingKey, 0, 0);
    for (byte b = 16; b < 64; b++)
      this.workingKey[b] = ((this.workingKey[b - 2] >>> 17 | this.workingKey[b - 2] << -17) ^ (this.workingKey[b - 2] >>> 19 | this.workingKey[b - 2] << -19) ^ this.workingKey[b - 2] >>> 10) + this.workingKey[b - 7] + ((this.workingKey[b - 15] >>> 7 | this.workingKey[b - 15] << -7) ^ (this.workingKey[b - 15] >>> 18 | this.workingKey[b - 15] << -18) ^ this.workingKey[b - 15] >>> 3) + this.workingKey[b - 16]; 
  }
  
  private void encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int[] arrayOfInt = new int[8];
    byteBlockToInts(paramArrayOfbyte1, arrayOfInt, paramInt1, 0);
    for (byte b = 0; b < 64; b++) {
      int i = ((arrayOfInt[4] >>> 6 | arrayOfInt[4] << -6) ^ (arrayOfInt[4] >>> 11 | arrayOfInt[4] << -11) ^ (arrayOfInt[4] >>> 25 | arrayOfInt[4] << -25)) + (arrayOfInt[4] & arrayOfInt[5] ^ (arrayOfInt[4] ^ 0xFFFFFFFF) & arrayOfInt[6]) + arrayOfInt[7] + K[b] + this.workingKey[b];
      arrayOfInt[7] = arrayOfInt[6];
      arrayOfInt[6] = arrayOfInt[5];
      arrayOfInt[5] = arrayOfInt[4];
      arrayOfInt[4] = arrayOfInt[3] + i;
      arrayOfInt[3] = arrayOfInt[2];
      arrayOfInt[2] = arrayOfInt[1];
      arrayOfInt[1] = arrayOfInt[0];
      arrayOfInt[0] = i + ((arrayOfInt[0] >>> 2 | arrayOfInt[0] << -2) ^ (arrayOfInt[0] >>> 13 | arrayOfInt[0] << -13) ^ (arrayOfInt[0] >>> 22 | arrayOfInt[0] << -22)) + (arrayOfInt[0] & arrayOfInt[2] ^ arrayOfInt[0] & arrayOfInt[3] ^ arrayOfInt[2] & arrayOfInt[3]);
    } 
    ints2bytes(arrayOfInt, paramArrayOfbyte2, paramInt2);
  }
  
  private void decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int[] arrayOfInt = new int[8];
    byteBlockToInts(paramArrayOfbyte1, arrayOfInt, paramInt1, 0);
    for (byte b = 63; b > -1; b--) {
      int i = arrayOfInt[0] - ((arrayOfInt[1] >>> 2 | arrayOfInt[1] << -2) ^ (arrayOfInt[1] >>> 13 | arrayOfInt[1] << -13) ^ (arrayOfInt[1] >>> 22 | arrayOfInt[1] << -22)) - (arrayOfInt[1] & arrayOfInt[2] ^ arrayOfInt[1] & arrayOfInt[3] ^ arrayOfInt[2] & arrayOfInt[3]);
      arrayOfInt[0] = arrayOfInt[1];
      arrayOfInt[1] = arrayOfInt[2];
      arrayOfInt[2] = arrayOfInt[3];
      arrayOfInt[3] = arrayOfInt[4] - i;
      arrayOfInt[4] = arrayOfInt[5];
      arrayOfInt[5] = arrayOfInt[6];
      arrayOfInt[6] = arrayOfInt[7];
      arrayOfInt[7] = i - K[b] - this.workingKey[b] - ((arrayOfInt[4] >>> 6 | arrayOfInt[4] << -6) ^ (arrayOfInt[4] >>> 11 | arrayOfInt[4] << -11) ^ (arrayOfInt[4] >>> 25 | arrayOfInt[4] << -25)) - (arrayOfInt[4] & arrayOfInt[5] ^ (arrayOfInt[4] ^ 0xFFFFFFFF) & arrayOfInt[6]);
    } 
    ints2bytes(arrayOfInt, paramArrayOfbyte2, paramInt2);
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (this.workingKey == null)
      throw new IllegalStateException("Shacal2 not initialised"); 
    if (paramInt1 + 32 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 32 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.forEncryption) {
      encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } else {
      decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } 
    return 32;
  }
  
  private void byteBlockToInts(byte[] paramArrayOfbyte, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    for (int i = paramInt2; i < 8; i++)
      paramArrayOfint[i] = (paramArrayOfbyte[paramInt1++] & 0xFF) << 24 | (paramArrayOfbyte[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte[paramInt1++] & 0xFF; 
  }
  
  private void bytes2ints(byte[] paramArrayOfbyte, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    for (int i = paramInt2; i < paramArrayOfbyte.length / 4; i++)
      paramArrayOfint[i] = (paramArrayOfbyte[paramInt1++] & 0xFF) << 24 | (paramArrayOfbyte[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte[paramInt1++] & 0xFF; 
  }
  
  private void ints2bytes(int[] paramArrayOfint, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      paramArrayOfbyte[paramInt++] = (byte)(paramArrayOfint[b] >>> 24);
      paramArrayOfbyte[paramInt++] = (byte)(paramArrayOfint[b] >>> 16);
      paramArrayOfbyte[paramInt++] = (byte)(paramArrayOfint[b] >>> 8);
      paramArrayOfbyte[paramInt++] = (byte)paramArrayOfint[b];
    } 
  }
}
