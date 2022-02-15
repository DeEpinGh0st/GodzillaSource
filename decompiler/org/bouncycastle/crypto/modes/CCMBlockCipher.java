package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class CCMBlockCipher implements AEADBlockCipher {
  private BlockCipher cipher;
  
  private int blockSize;
  
  private boolean forEncryption;
  
  private byte[] nonce;
  
  private byte[] initialAssociatedText;
  
  private int macSize;
  
  private CipherParameters keyParam;
  
  private byte[] macBlock;
  
  private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
  
  private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();
  
  public CCMBlockCipher(BlockCipher paramBlockCipher) {
    this.cipher = paramBlockCipher;
    this.blockSize = paramBlockCipher.getBlockSize();
    this.macBlock = new byte[this.blockSize];
    if (this.blockSize != 16)
      throw new IllegalArgumentException("cipher required with a block size of 16."); 
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.cipher;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    CipherParameters cipherParameters;
    this.forEncryption = paramBoolean;
    if (paramCipherParameters instanceof AEADParameters) {
      AEADParameters aEADParameters = (AEADParameters)paramCipherParameters;
      this.nonce = aEADParameters.getNonce();
      this.initialAssociatedText = aEADParameters.getAssociatedText();
      this.macSize = aEADParameters.getMacSize() / 8;
      KeyParameter keyParameter = aEADParameters.getKey();
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      this.nonce = parametersWithIV.getIV();
      this.initialAssociatedText = null;
      this.macSize = this.macBlock.length / 2;
      cipherParameters = parametersWithIV.getParameters();
    } else {
      throw new IllegalArgumentException("invalid parameters passed to CCM: " + paramCipherParameters.getClass().getName());
    } 
    if (cipherParameters != null)
      this.keyParam = cipherParameters; 
    if (this.nonce == null || this.nonce.length < 7 || this.nonce.length > 13)
      throw new IllegalArgumentException("nonce must have length from 7 to 13 octets"); 
    reset();
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName() + "/CCM";
  }
  
  public void processAADByte(byte paramByte) {
    this.associatedText.write(paramByte);
  }
  
  public void processAADBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.associatedText.write(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    this.data.write(paramByte);
    return 0;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException, IllegalStateException {
    if (paramArrayOfbyte1.length < paramInt1 + paramInt2)
      throw new DataLengthException("Input buffer too short"); 
    this.data.write(paramArrayOfbyte1, paramInt1, paramInt2);
    return 0;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, InvalidCipherTextException {
    int i = processPacket(this.data.getBuffer(), 0, this.data.size(), paramArrayOfbyte, paramInt);
    reset();
    return i;
  }
  
  public void reset() {
    this.cipher.reset();
    this.associatedText.reset();
    this.data.reset();
  }
  
  public byte[] getMac() {
    byte[] arrayOfByte = new byte[this.macSize];
    System.arraycopy(this.macBlock, 0, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public int getUpdateOutputSize(int paramInt) {
    return 0;
  }
  
  public int getOutputSize(int paramInt) {
    int i = paramInt + this.data.size();
    return this.forEncryption ? (i + this.macSize) : ((i < this.macSize) ? 0 : (i - this.macSize));
  }
  
  public byte[] processPacket(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalStateException, InvalidCipherTextException {
    byte[] arrayOfByte;
    if (this.forEncryption) {
      arrayOfByte = new byte[paramInt2 + this.macSize];
    } else {
      if (paramInt2 < this.macSize)
        throw new InvalidCipherTextException("data too short"); 
      arrayOfByte = new byte[paramInt2 - this.macSize];
    } 
    processPacket(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public int processPacket(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalStateException, InvalidCipherTextException, DataLengthException {
    int k;
    if (this.keyParam == null)
      throw new IllegalStateException("CCM cipher unitialized."); 
    int i = this.nonce.length;
    int j = 15 - i;
    if (j < 4) {
      int i1 = 1 << 8 * j;
      if (paramInt2 >= i1)
        throw new IllegalStateException("CCM packet too large for choice of q."); 
    } 
    byte[] arrayOfByte = new byte[this.blockSize];
    arrayOfByte[0] = (byte)(j - 1 & 0x7);
    System.arraycopy(this.nonce, 0, arrayOfByte, 1, this.nonce.length);
    SICBlockCipher sICBlockCipher = new SICBlockCipher(this.cipher);
    sICBlockCipher.init(this.forEncryption, (CipherParameters)new ParametersWithIV(this.keyParam, arrayOfByte));
    int m = paramInt1;
    int n = paramInt3;
    if (this.forEncryption) {
      k = paramInt2 + this.macSize;
      if (paramArrayOfbyte2.length < k + paramInt3)
        throw new OutputLengthException("Output buffer too short."); 
      calculateMac(paramArrayOfbyte1, paramInt1, paramInt2, this.macBlock);
      byte[] arrayOfByte1 = new byte[this.blockSize];
      sICBlockCipher.processBlock(this.macBlock, 0, arrayOfByte1, 0);
      while (m < paramInt1 + paramInt2 - this.blockSize) {
        sICBlockCipher.processBlock(paramArrayOfbyte1, m, paramArrayOfbyte2, n);
        n += this.blockSize;
        m += this.blockSize;
      } 
      byte[] arrayOfByte2 = new byte[this.blockSize];
      System.arraycopy(paramArrayOfbyte1, m, arrayOfByte2, 0, paramInt2 + paramInt1 - m);
      sICBlockCipher.processBlock(arrayOfByte2, 0, arrayOfByte2, 0);
      System.arraycopy(arrayOfByte2, 0, paramArrayOfbyte2, n, paramInt2 + paramInt1 - m);
      System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte2, paramInt3 + paramInt2, this.macSize);
    } else {
      if (paramInt2 < this.macSize)
        throw new InvalidCipherTextException("data too short"); 
      k = paramInt2 - this.macSize;
      if (paramArrayOfbyte2.length < k + paramInt3)
        throw new OutputLengthException("Output buffer too short."); 
      System.arraycopy(paramArrayOfbyte1, paramInt1 + k, this.macBlock, 0, this.macSize);
      sICBlockCipher.processBlock(this.macBlock, 0, this.macBlock, 0);
      for (int i1 = this.macSize; i1 != this.macBlock.length; i1++)
        this.macBlock[i1] = 0; 
      while (m < paramInt1 + k - this.blockSize) {
        sICBlockCipher.processBlock(paramArrayOfbyte1, m, paramArrayOfbyte2, n);
        n += this.blockSize;
        m += this.blockSize;
      } 
      byte[] arrayOfByte1 = new byte[this.blockSize];
      System.arraycopy(paramArrayOfbyte1, m, arrayOfByte1, 0, k - m - paramInt1);
      sICBlockCipher.processBlock(arrayOfByte1, 0, arrayOfByte1, 0);
      System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte2, n, k - m - paramInt1);
      byte[] arrayOfByte2 = new byte[this.blockSize];
      calculateMac(paramArrayOfbyte2, paramInt3, k, arrayOfByte2);
      if (!Arrays.constantTimeAreEqual(this.macBlock, arrayOfByte2))
        throw new InvalidCipherTextException("mac check in CCM failed"); 
    } 
    return k;
  }
  
  private int calculateMac(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2) {
    CBCBlockCipherMac cBCBlockCipherMac = new CBCBlockCipherMac(this.cipher, this.macSize * 8);
    cBCBlockCipherMac.init(this.keyParam);
    byte[] arrayOfByte = new byte[16];
    if (hasAssociatedText())
      arrayOfByte[0] = (byte)(arrayOfByte[0] | 0x40); 
    arrayOfByte[0] = (byte)(arrayOfByte[0] | ((cBCBlockCipherMac.getMacSize() - 2) / 2 & 0x7) << 3);
    arrayOfByte[0] = (byte)(arrayOfByte[0] | 15 - this.nonce.length - 1 & 0x7);
    System.arraycopy(this.nonce, 0, arrayOfByte, 1, this.nonce.length);
    int i = paramInt2;
    for (byte b = 1; i > 0; b++) {
      arrayOfByte[arrayOfByte.length - b] = (byte)(i & 0xFF);
      i >>>= 8;
    } 
    cBCBlockCipherMac.update(arrayOfByte, 0, arrayOfByte.length);
    if (hasAssociatedText()) {
      int k = getAssociatedTextLength();
      if (k < 65280) {
        cBCBlockCipherMac.update((byte)(k >> 8));
        cBCBlockCipherMac.update((byte)k);
        j = 2;
      } else {
        cBCBlockCipherMac.update((byte)-1);
        cBCBlockCipherMac.update((byte)-2);
        cBCBlockCipherMac.update((byte)(k >> 24));
        cBCBlockCipherMac.update((byte)(k >> 16));
        cBCBlockCipherMac.update((byte)(k >> 8));
        cBCBlockCipherMac.update((byte)k);
        j = 6;
      } 
      if (this.initialAssociatedText != null)
        cBCBlockCipherMac.update(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
      if (this.associatedText.size() > 0)
        cBCBlockCipherMac.update(this.associatedText.getBuffer(), 0, this.associatedText.size()); 
      int j = (j + k) % 16;
      if (j != 0)
        for (int m = j; m != 16; m++)
          cBCBlockCipherMac.update((byte)0);  
    } 
    cBCBlockCipherMac.update(paramArrayOfbyte1, paramInt1, paramInt2);
    return cBCBlockCipherMac.doFinal(paramArrayOfbyte2, 0);
  }
  
  private int getAssociatedTextLength() {
    return this.associatedText.size() + ((this.initialAssociatedText == null) ? 0 : this.initialAssociatedText.length);
  }
  
  private boolean hasAssociatedText() {
    return (getAssociatedTextLength() > 0);
  }
  
  private class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
    public byte[] getBuffer() {
      return this.buf;
    }
  }
}
