package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithSBox;

public class GOST28147Mac implements Mac {
  private int blockSize = 8;
  
  private int macSize = 4;
  
  private int bufOff = 0;
  
  private byte[] buf = new byte[this.blockSize];
  
  private byte[] mac = new byte[this.blockSize];
  
  private boolean firstStep = true;
  
  private int[] workingKey = null;
  
  private byte[] macIV = null;
  
  private byte[] S = new byte[] { 
      9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 
      14, 15, 12, 0, 13, 5, 3, 7, 14, 9, 
      8, 10, 15, 0, 5, 2, 6, 12, 11, 4, 
      13, 1, 14, 4, 6, 2, 11, 3, 13, 8, 
      12, 15, 5, 10, 0, 7, 1, 9, 14, 7, 
      10, 12, 13, 1, 3, 9, 0, 2, 11, 4, 
      15, 8, 5, 6, 11, 5, 1, 9, 8, 13, 
      15, 0, 14, 4, 2, 3, 12, 7, 10, 6, 
      3, 10, 13, 12, 1, 2, 0, 11, 7, 5, 
      9, 4, 8, 15, 14, 6, 1, 13, 2, 9, 
      7, 10, 6, 0, 8, 12, 4, 5, 15, 3, 
      11, 14, 11, 10, 15, 5, 0, 12, 14, 8, 
      6, 2, 3, 9, 1, 7, 13, 4 };
  
  private int[] generateWorkingKey(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 32)
      throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!"); 
    int[] arrayOfInt = new int[8];
    for (byte b = 0; b != 8; b++)
      arrayOfInt[b] = bytesToint(paramArrayOfbyte, b * 4); 
    return arrayOfInt;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    reset();
    this.buf = new byte[this.blockSize];
    this.macIV = null;
    if (paramCipherParameters instanceof ParametersWithSBox) {
      ParametersWithSBox parametersWithSBox = (ParametersWithSBox)paramCipherParameters;
      System.arraycopy(parametersWithSBox.getSBox(), 0, this.S, 0, (parametersWithSBox.getSBox()).length);
      if (parametersWithSBox.getParameters() != null)
        this.workingKey = generateWorkingKey(((KeyParameter)parametersWithSBox.getParameters()).getKey()); 
    } else if (paramCipherParameters instanceof KeyParameter) {
      this.workingKey = generateWorkingKey(((KeyParameter)paramCipherParameters).getKey());
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      this.workingKey = generateWorkingKey(((KeyParameter)parametersWithIV.getParameters()).getKey());
      System.arraycopy(parametersWithIV.getIV(), 0, this.mac, 0, this.mac.length);
      this.macIV = parametersWithIV.getIV();
    } else {
      throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + paramCipherParameters.getClass().getName());
    } 
  }
  
  public String getAlgorithmName() {
    return "GOST28147Mac";
  }
  
  public int getMacSize() {
    return this.macSize;
  }
  
  private int gost28147_mainStep(int paramInt1, int paramInt2) {
    int i = paramInt2 + paramInt1;
    int j = this.S[0 + (i >> 0 & 0xF)] << 0;
    j += this.S[16 + (i >> 4 & 0xF)] << 4;
    j += this.S[32 + (i >> 8 & 0xF)] << 8;
    j += this.S[48 + (i >> 12 & 0xF)] << 12;
    j += this.S[64 + (i >> 16 & 0xF)] << 16;
    j += this.S[80 + (i >> 20 & 0xF)] << 20;
    j += this.S[96 + (i >> 24 & 0xF)] << 24;
    j += this.S[112 + (i >> 28 & 0xF)] << 28;
    return j << 11 | j >>> 21;
  }
  
  private void gost28147MacFunc(int[] paramArrayOfint, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToint(paramArrayOfbyte1, paramInt1);
    int j = bytesToint(paramArrayOfbyte1, paramInt1 + 4);
    for (byte b = 0; b < 2; b++) {
      for (byte b1 = 0; b1 < 8; b1++) {
        int k = i;
        i = j ^ gost28147_mainStep(i, paramArrayOfint[b1]);
        j = k;
      } 
    } 
    intTobytes(i, paramArrayOfbyte2, paramInt2);
    intTobytes(j, paramArrayOfbyte2, paramInt2 + 4);
  }
  
  private int bytesToint(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt + 3] << 24 & 0xFF000000) + (paramArrayOfbyte[paramInt + 2] << 16 & 0xFF0000) + (paramArrayOfbyte[paramInt + 1] << 8 & 0xFF00) + (paramArrayOfbyte[paramInt] & 0xFF);
  }
  
  private void intTobytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >>> 24);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
  
  private byte[] CM5func(byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length - paramInt];
    System.arraycopy(paramArrayOfbyte1, paramInt, arrayOfByte, 0, paramArrayOfbyte2.length);
    for (byte b = 0; b != paramArrayOfbyte2.length; b++)
      arrayOfByte[b] = (byte)(arrayOfByte[b] ^ paramArrayOfbyte2[b]); 
    return arrayOfByte;
  }
  
  public void update(byte paramByte) throws IllegalStateException {
    if (this.bufOff == this.buf.length) {
      byte[] arrayOfByte = new byte[this.buf.length];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.mac.length);
      if (this.firstStep) {
        this.firstStep = false;
        if (this.macIV != null)
          arrayOfByte = CM5func(this.buf, 0, this.macIV); 
      } else {
        arrayOfByte = CM5func(this.buf, 0, this.mac);
      } 
      gost28147MacFunc(this.workingKey, arrayOfByte, 0, this.mac, 0);
      this.bufOff = 0;
    } 
    this.buf[this.bufOff++] = paramByte;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("Can't have a negative input length!"); 
    int i = this.blockSize - this.bufOff;
    if (paramInt2 > i) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, i);
      byte[] arrayOfByte = new byte[this.buf.length];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.mac.length);
      if (this.firstStep) {
        this.firstStep = false;
        if (this.macIV != null)
          arrayOfByte = CM5func(this.buf, 0, this.macIV); 
      } else {
        arrayOfByte = CM5func(this.buf, 0, this.mac);
      } 
      gost28147MacFunc(this.workingKey, arrayOfByte, 0, this.mac, 0);
      this.bufOff = 0;
      paramInt2 -= i;
      for (paramInt1 += i; paramInt2 > this.blockSize; paramInt1 += this.blockSize) {
        arrayOfByte = CM5func(paramArrayOfbyte, paramInt1, this.mac);
        gost28147MacFunc(this.workingKey, arrayOfByte, 0, this.mac, 0);
        paramInt2 -= this.blockSize;
      } 
    } 
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, paramInt2);
    this.bufOff += paramInt2;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    while (this.bufOff < this.blockSize) {
      this.buf[this.bufOff] = 0;
      this.bufOff++;
    } 
    byte[] arrayOfByte = new byte[this.buf.length];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.mac.length);
    if (this.firstStep) {
      this.firstStep = false;
    } else {
      arrayOfByte = CM5func(this.buf, 0, this.mac);
    } 
    gost28147MacFunc(this.workingKey, arrayOfByte, 0, this.mac, 0);
    System.arraycopy(this.mac, this.mac.length / 2 - this.macSize, paramArrayOfbyte, paramInt, this.macSize);
    reset();
    return this.macSize;
  }
  
  public void reset() {
    for (byte b = 0; b < this.buf.length; b++)
      this.buf[b] = 0; 
    this.bufOff = 0;
    this.firstStep = true;
  }
}
