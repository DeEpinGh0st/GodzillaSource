package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class EAXBlockCipher implements AEADBlockCipher {
  private static final byte nTAG = 0;
  
  private static final byte hTAG = 1;
  
  private static final byte cTAG = 2;
  
  private SICBlockCipher cipher;
  
  private boolean forEncryption;
  
  private int blockSize;
  
  private Mac mac;
  
  private byte[] nonceMac;
  
  private byte[] associatedTextMac;
  
  private byte[] macBlock;
  
  private int macSize;
  
  private byte[] bufBlock;
  
  private int bufOff;
  
  private boolean cipherInitialized;
  
  private byte[] initialAssociatedText;
  
  public EAXBlockCipher(BlockCipher paramBlockCipher) {
    this.blockSize = paramBlockCipher.getBlockSize();
    this.mac = (Mac)new CMac(paramBlockCipher);
    this.macBlock = new byte[this.blockSize];
    this.associatedTextMac = new byte[this.mac.getMacSize()];
    this.nonceMac = new byte[this.mac.getMacSize()];
    this.cipher = new SICBlockCipher(paramBlockCipher);
  }
  
  public String getAlgorithmName() {
    return this.cipher.getUnderlyingCipher().getAlgorithmName() + "/EAX";
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.cipher.getUnderlyingCipher();
  }
  
  public int getBlockSize() {
    return this.cipher.getBlockSize();
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    byte[] arrayOfByte1;
    CipherParameters cipherParameters;
    this.forEncryption = paramBoolean;
    if (paramCipherParameters instanceof AEADParameters) {
      AEADParameters aEADParameters = (AEADParameters)paramCipherParameters;
      arrayOfByte1 = aEADParameters.getNonce();
      this.initialAssociatedText = aEADParameters.getAssociatedText();
      this.macSize = aEADParameters.getMacSize() / 8;
      KeyParameter keyParameter = aEADParameters.getKey();
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      arrayOfByte1 = parametersWithIV.getIV();
      this.initialAssociatedText = null;
      this.macSize = this.mac.getMacSize() / 2;
      cipherParameters = parametersWithIV.getParameters();
    } else {
      throw new IllegalArgumentException("invalid parameters passed to EAX");
    } 
    this.bufBlock = new byte[paramBoolean ? this.blockSize : (this.blockSize + this.macSize)];
    byte[] arrayOfByte2 = new byte[this.blockSize];
    this.mac.init(cipherParameters);
    arrayOfByte2[this.blockSize - 1] = 0;
    this.mac.update(arrayOfByte2, 0, this.blockSize);
    this.mac.update(arrayOfByte1, 0, arrayOfByte1.length);
    this.mac.doFinal(this.nonceMac, 0);
    this.cipher.init(true, (CipherParameters)new ParametersWithIV(null, this.nonceMac));
    reset();
  }
  
  private void initCipher() {
    if (this.cipherInitialized)
      return; 
    this.cipherInitialized = true;
    this.mac.doFinal(this.associatedTextMac, 0);
    byte[] arrayOfByte = new byte[this.blockSize];
    arrayOfByte[this.blockSize - 1] = 2;
    this.mac.update(arrayOfByte, 0, this.blockSize);
  }
  
  private void calculateMac() {
    byte[] arrayOfByte = new byte[this.blockSize];
    this.mac.doFinal(arrayOfByte, 0);
    for (byte b = 0; b < this.macBlock.length; b++)
      this.macBlock[b] = (byte)(this.nonceMac[b] ^ this.associatedTextMac[b] ^ arrayOfByte[b]); 
  }
  
  public void reset() {
    reset(true);
  }
  
  private void reset(boolean paramBoolean) {
    this.cipher.reset();
    this.mac.reset();
    this.bufOff = 0;
    Arrays.fill(this.bufBlock, (byte)0);
    if (paramBoolean)
      Arrays.fill(this.macBlock, (byte)0); 
    byte[] arrayOfByte = new byte[this.blockSize];
    arrayOfByte[this.blockSize - 1] = 1;
    this.mac.update(arrayOfByte, 0, this.blockSize);
    this.cipherInitialized = false;
    if (this.initialAssociatedText != null)
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
  }
  
  public void processAADByte(byte paramByte) {
    if (this.cipherInitialized)
      throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun."); 
    this.mac.update(paramByte);
  }
  
  public void processAADBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.cipherInitialized)
      throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun."); 
    this.mac.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) throws DataLengthException {
    initCipher();
    return process(paramByte, paramArrayOfbyte, paramInt);
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException {
    initCipher();
    if (paramArrayOfbyte1.length < paramInt1 + paramInt2)
      throw new DataLengthException("Input buffer too short"); 
    int i = 0;
    for (int j = 0; j != paramInt2; j++)
      i += process(paramArrayOfbyte1[paramInt1 + j], paramArrayOfbyte2, paramInt3 + i); 
    return i;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, InvalidCipherTextException {
    initCipher();
    int i = this.bufOff;
    byte[] arrayOfByte = new byte[this.bufBlock.length];
    this.bufOff = 0;
    if (this.forEncryption) {
      if (paramArrayOfbyte.length < paramInt + i + this.macSize)
        throw new OutputLengthException("Output buffer too short"); 
      this.cipher.processBlock(this.bufBlock, 0, arrayOfByte, 0);
      System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, i);
      this.mac.update(arrayOfByte, 0, i);
      calculateMac();
      System.arraycopy(this.macBlock, 0, paramArrayOfbyte, paramInt + i, this.macSize);
      reset(false);
      return i + this.macSize;
    } 
    if (i < this.macSize)
      throw new InvalidCipherTextException("data too short"); 
    if (paramArrayOfbyte.length < paramInt + i - this.macSize)
      throw new OutputLengthException("Output buffer too short"); 
    if (i > this.macSize) {
      this.mac.update(this.bufBlock, 0, i - this.macSize);
      this.cipher.processBlock(this.bufBlock, 0, arrayOfByte, 0);
      System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, i - this.macSize);
    } 
    calculateMac();
    if (!verifyMac(this.bufBlock, i - this.macSize))
      throw new InvalidCipherTextException("mac check in EAX failed"); 
    reset(false);
    return i - this.macSize;
  }
  
  public byte[] getMac() {
    byte[] arrayOfByte = new byte[this.macSize];
    System.arraycopy(this.macBlock, 0, arrayOfByte, 0, this.macSize);
    return arrayOfByte;
  }
  
  public int getUpdateOutputSize(int paramInt) {
    int i = paramInt + this.bufOff;
    if (!this.forEncryption) {
      if (i < this.macSize)
        return 0; 
      i -= this.macSize;
    } 
    return i - i % this.blockSize;
  }
  
  public int getOutputSize(int paramInt) {
    int i = paramInt + this.bufOff;
    return this.forEncryption ? (i + this.macSize) : ((i < this.macSize) ? 0 : (i - this.macSize));
  }
  
  private int process(byte paramByte, byte[] paramArrayOfbyte, int paramInt) {
    this.bufBlock[this.bufOff++] = paramByte;
    if (this.bufOff == this.bufBlock.length) {
      int i;
      if (paramArrayOfbyte.length < paramInt + this.blockSize)
        throw new OutputLengthException("Output buffer is too short"); 
      if (this.forEncryption) {
        i = this.cipher.processBlock(this.bufBlock, 0, paramArrayOfbyte, paramInt);
        this.mac.update(paramArrayOfbyte, paramInt, this.blockSize);
      } else {
        this.mac.update(this.bufBlock, 0, this.blockSize);
        i = this.cipher.processBlock(this.bufBlock, 0, paramArrayOfbyte, paramInt);
      } 
      this.bufOff = 0;
      if (!this.forEncryption) {
        System.arraycopy(this.bufBlock, this.blockSize, this.bufBlock, 0, this.macSize);
        this.bufOff = this.macSize;
      } 
      return i;
    } 
    return 0;
  }
  
  private boolean verifyMac(byte[] paramArrayOfbyte, int paramInt) {
    int i = 0;
    for (byte b = 0; b < this.macSize; b++)
      i |= this.macBlock[b] ^ paramArrayOfbyte[paramInt + b]; 
    return (i == 0);
  }
}
