package org.bouncycastle.crypto.engines;

import java.util.ArrayList;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class DSTU7624WrapEngine implements Wrapper {
  private static final int BYTES_IN_INTEGER = 4;
  
  private boolean forWrapping;
  
  private DSTU7624Engine engine;
  
  private byte[] B;
  
  private byte[] intArray;
  
  private byte[] checkSumArray;
  
  private byte[] zeroArray;
  
  private ArrayList<byte[]> Btemp;
  
  public DSTU7624WrapEngine(int paramInt) {
    this.engine = new DSTU7624Engine(paramInt);
    this.B = new byte[this.engine.getBlockSize() / 2];
    this.checkSumArray = new byte[this.engine.getBlockSize()];
    this.zeroArray = new byte[this.engine.getBlockSize()];
    this.Btemp = (ArrayList)new ArrayList<byte>();
    this.intArray = new byte[4];
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof ParametersWithRandom)
      paramCipherParameters = ((ParametersWithRandom)paramCipherParameters).getParameters(); 
    this.forWrapping = paramBoolean;
    if (paramCipherParameters instanceof org.bouncycastle.crypto.params.KeyParameter) {
      this.engine.init(paramBoolean, paramCipherParameters);
    } else {
      throw new IllegalArgumentException("invalid parameters passed to DSTU7624WrapEngine");
    } 
  }
  
  public String getAlgorithmName() {
    return "DSTU7624WrapEngine";
  }
  
  public byte[] wrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (!this.forWrapping)
      throw new IllegalStateException("not set for wrapping"); 
    if (paramInt2 % this.engine.getBlockSize() != 0)
      throw new DataLengthException("wrap data must be a multiple of " + this.engine.getBlockSize() + " bytes"); 
    if (paramInt1 + paramInt2 > paramArrayOfbyte.length)
      throw new DataLengthException("input buffer too short"); 
    int i = 2 * (1 + paramInt2 / this.engine.getBlockSize());
    int j = (i - 1) * 6;
    byte[] arrayOfByte = new byte[paramInt2 + this.engine.getBlockSize()];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    System.arraycopy(arrayOfByte, 0, this.B, 0, this.engine.getBlockSize() / 2);
    this.Btemp.clear();
    int k = arrayOfByte.length - this.engine.getBlockSize() / 2;
    int m;
    for (m = this.engine.getBlockSize() / 2; k != 0; m += this.engine.getBlockSize() / 2) {
      byte[] arrayOfByte1 = new byte[this.engine.getBlockSize() / 2];
      System.arraycopy(arrayOfByte, m, arrayOfByte1, 0, this.engine.getBlockSize() / 2);
      this.Btemp.add(arrayOfByte1);
      k -= this.engine.getBlockSize() / 2;
    } 
    byte b;
    for (b = 0; b < j; b++) {
      System.arraycopy(this.B, 0, arrayOfByte, 0, this.engine.getBlockSize() / 2);
      System.arraycopy(this.Btemp.get(0), 0, arrayOfByte, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
      this.engine.processBlock(arrayOfByte, 0, arrayOfByte, 0);
      intToBytes(b + 1, this.intArray, 0);
      byte b1;
      for (b1 = 0; b1 < 4; b1++)
        arrayOfByte[b1 + this.engine.getBlockSize() / 2] = (byte)(arrayOfByte[b1 + this.engine.getBlockSize() / 2] ^ this.intArray[b1]); 
      System.arraycopy(arrayOfByte, this.engine.getBlockSize() / 2, this.B, 0, this.engine.getBlockSize() / 2);
      for (b1 = 2; b1 < i; b1++)
        System.arraycopy(this.Btemp.get(b1 - 1), 0, this.Btemp.get(b1 - 2), 0, this.engine.getBlockSize() / 2); 
      System.arraycopy(arrayOfByte, 0, this.Btemp.get(i - 2), 0, this.engine.getBlockSize() / 2);
    } 
    System.arraycopy(this.B, 0, arrayOfByte, 0, this.engine.getBlockSize() / 2);
    m = this.engine.getBlockSize() / 2;
    for (b = 0; b < i - 1; b++) {
      System.arraycopy(this.Btemp.get(b), 0, arrayOfByte, m, this.engine.getBlockSize() / 2);
      m += this.engine.getBlockSize() / 2;
    } 
    return arrayOfByte;
  }
  
  public byte[] unwrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    if (this.forWrapping)
      throw new IllegalStateException("not set for unwrapping"); 
    if (paramInt2 % this.engine.getBlockSize() != 0)
      throw new DataLengthException("unwrap data must be a multiple of " + this.engine.getBlockSize() + " bytes"); 
    int i = 2 * paramInt2 / this.engine.getBlockSize();
    int j = (i - 1) * 6;
    byte[] arrayOfByte1 = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, 0, paramInt2);
    byte[] arrayOfByte2 = new byte[this.engine.getBlockSize() / 2];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, this.engine.getBlockSize() / 2);
    this.Btemp.clear();
    int k = arrayOfByte1.length - this.engine.getBlockSize() / 2;
    int m;
    for (m = this.engine.getBlockSize() / 2; k != 0; m += this.engine.getBlockSize() / 2) {
      byte[] arrayOfByte = new byte[this.engine.getBlockSize() / 2];
      System.arraycopy(arrayOfByte1, m, arrayOfByte, 0, this.engine.getBlockSize() / 2);
      this.Btemp.add(arrayOfByte);
      k -= this.engine.getBlockSize() / 2;
    } 
    byte b;
    for (b = 0; b < j; b++) {
      System.arraycopy(this.Btemp.get(i - 2), 0, arrayOfByte1, 0, this.engine.getBlockSize() / 2);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
      intToBytes(j - b, this.intArray, 0);
      byte b1;
      for (b1 = 0; b1 < 4; b1++)
        arrayOfByte1[b1 + this.engine.getBlockSize() / 2] = (byte)(arrayOfByte1[b1 + this.engine.getBlockSize() / 2] ^ this.intArray[b1]); 
      this.engine.processBlock(arrayOfByte1, 0, arrayOfByte1, 0);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, this.engine.getBlockSize() / 2);
      for (b1 = 2; b1 < i; b1++)
        System.arraycopy(this.Btemp.get(i - b1 - 1), 0, this.Btemp.get(i - b1), 0, this.engine.getBlockSize() / 2); 
      System.arraycopy(arrayOfByte1, this.engine.getBlockSize() / 2, this.Btemp.get(0), 0, this.engine.getBlockSize() / 2);
    } 
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, this.engine.getBlockSize() / 2);
    m = this.engine.getBlockSize() / 2;
    for (b = 0; b < i - 1; b++) {
      System.arraycopy(this.Btemp.get(b), 0, arrayOfByte1, m, this.engine.getBlockSize() / 2);
      m += this.engine.getBlockSize() / 2;
    } 
    System.arraycopy(arrayOfByte1, arrayOfByte1.length - this.engine.getBlockSize(), this.checkSumArray, 0, this.engine.getBlockSize());
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length - this.engine.getBlockSize()];
    if (!Arrays.areEqual(this.checkSumArray, this.zeroArray))
      throw new InvalidCipherTextException("checksum failed"); 
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length - this.engine.getBlockSize());
    return arrayOfByte3;
  }
  
  private void intToBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >> 24);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 16);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
}
