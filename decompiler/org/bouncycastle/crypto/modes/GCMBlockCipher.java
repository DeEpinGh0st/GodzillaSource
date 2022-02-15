package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.crypto.modes.gcm.Tables1kGCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.Tables8kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GCMBlockCipher implements AEADBlockCipher {
  private static final int BLOCK_SIZE = 16;
  
  private BlockCipher cipher;
  
  private GCMMultiplier multiplier;
  
  private GCMExponentiator exp;
  
  private boolean forEncryption;
  
  private boolean initialised;
  
  private int macSize;
  
  private byte[] lastKey;
  
  private byte[] nonce;
  
  private byte[] initialAssociatedText;
  
  private byte[] H;
  
  private byte[] J0;
  
  private byte[] bufBlock;
  
  private byte[] macBlock;
  
  private byte[] S;
  
  private byte[] S_at;
  
  private byte[] S_atPre;
  
  private byte[] counter;
  
  private int blocksRemaining;
  
  private int bufOff;
  
  private long totalLength;
  
  private byte[] atBlock;
  
  private int atBlockPos;
  
  private long atLength;
  
  private long atLengthPre;
  
  public GCMBlockCipher(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, null);
  }
  
  public GCMBlockCipher(BlockCipher paramBlockCipher, GCMMultiplier paramGCMMultiplier) {
    Tables8kGCMMultiplier tables8kGCMMultiplier;
    if (paramBlockCipher.getBlockSize() != 16)
      throw new IllegalArgumentException("cipher required with a block size of 16."); 
    if (paramGCMMultiplier == null)
      tables8kGCMMultiplier = new Tables8kGCMMultiplier(); 
    this.cipher = paramBlockCipher;
    this.multiplier = (GCMMultiplier)tables8kGCMMultiplier;
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.cipher;
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName() + "/GCM";
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    KeyParameter keyParameter;
    this.forEncryption = paramBoolean;
    this.macBlock = null;
    this.initialised = true;
    byte[] arrayOfByte = null;
    if (paramCipherParameters instanceof AEADParameters) {
      AEADParameters aEADParameters = (AEADParameters)paramCipherParameters;
      arrayOfByte = aEADParameters.getNonce();
      this.initialAssociatedText = aEADParameters.getAssociatedText();
      int i = aEADParameters.getMacSize();
      if (i < 32 || i > 128 || i % 8 != 0)
        throw new IllegalArgumentException("Invalid value for MAC size: " + i); 
      this.macSize = i / 8;
      keyParameter = aEADParameters.getKey();
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      arrayOfByte = parametersWithIV.getIV();
      this.initialAssociatedText = null;
      this.macSize = 16;
      keyParameter = (KeyParameter)parametersWithIV.getParameters();
    } else {
      throw new IllegalArgumentException("invalid parameters passed to GCM");
    } 
    boolean bool = paramBoolean ? true : (16 + this.macSize);
    this.bufBlock = new byte[bool];
    if (arrayOfByte == null || arrayOfByte.length < 1)
      throw new IllegalArgumentException("IV must be at least 1 byte"); 
    if (paramBoolean && this.nonce != null && Arrays.areEqual(this.nonce, arrayOfByte)) {
      if (keyParameter == null)
        throw new IllegalArgumentException("cannot reuse nonce for GCM encryption"); 
      if (this.lastKey != null && Arrays.areEqual(this.lastKey, keyParameter.getKey()))
        throw new IllegalArgumentException("cannot reuse nonce for GCM encryption"); 
    } 
    this.nonce = arrayOfByte;
    if (keyParameter != null)
      this.lastKey = keyParameter.getKey(); 
    if (keyParameter != null) {
      this.cipher.init(true, (CipherParameters)keyParameter);
      this.H = new byte[16];
      this.cipher.processBlock(this.H, 0, this.H, 0);
      this.multiplier.init(this.H);
      this.exp = null;
    } else if (this.H == null) {
      throw new IllegalArgumentException("Key must be specified in initial init");
    } 
    this.J0 = new byte[16];
    if (this.nonce.length == 12) {
      System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
      this.J0[15] = 1;
    } else {
      gHASH(this.J0, this.nonce, this.nonce.length);
      byte[] arrayOfByte1 = new byte[16];
      Pack.longToBigEndian(this.nonce.length * 8L, arrayOfByte1, 8);
      gHASHBlock(this.J0, arrayOfByte1);
    } 
    this.S = new byte[16];
    this.S_at = new byte[16];
    this.S_atPre = new byte[16];
    this.atBlock = new byte[16];
    this.atBlockPos = 0;
    this.atLength = 0L;
    this.atLengthPre = 0L;
    this.counter = Arrays.clone(this.J0);
    this.blocksRemaining = -2;
    this.bufOff = 0;
    this.totalLength = 0L;
    if (this.initialAssociatedText != null)
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
  }
  
  public byte[] getMac() {
    return (this.macBlock == null) ? new byte[this.macSize] : Arrays.clone(this.macBlock);
  }
  
  public int getOutputSize(int paramInt) {
    int i = paramInt + this.bufOff;
    return this.forEncryption ? (i + this.macSize) : ((i < this.macSize) ? 0 : (i - this.macSize));
  }
  
  public int getUpdateOutputSize(int paramInt) {
    int i = paramInt + this.bufOff;
    if (!this.forEncryption) {
      if (i < this.macSize)
        return 0; 
      i -= this.macSize;
    } 
    return i - i % 16;
  }
  
  public void processAADByte(byte paramByte) {
    checkStatus();
    this.atBlock[this.atBlockPos] = paramByte;
    if (++this.atBlockPos == 16) {
      gHASHBlock(this.S_at, this.atBlock);
      this.atBlockPos = 0;
      this.atLength += 16L;
    } 
  }
  
  public void processAADBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    checkStatus();
    for (byte b = 0; b < paramInt2; b++) {
      this.atBlock[this.atBlockPos] = paramArrayOfbyte[paramInt1 + b];
      if (++this.atBlockPos == 16) {
        gHASHBlock(this.S_at, this.atBlock);
        this.atBlockPos = 0;
        this.atLength += 16L;
      } 
    } 
  }
  
  private void initCipher() {
    if (this.atLength > 0L) {
      System.arraycopy(this.S_at, 0, this.S_atPre, 0, 16);
      this.atLengthPre = this.atLength;
    } 
    if (this.atBlockPos > 0) {
      gHASHPartial(this.S_atPre, this.atBlock, 0, this.atBlockPos);
      this.atLengthPre += this.atBlockPos;
    } 
    if (this.atLengthPre > 0L)
      System.arraycopy(this.S_atPre, 0, this.S, 0, 16); 
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) throws DataLengthException {
    checkStatus();
    this.bufBlock[this.bufOff] = paramByte;
    if (++this.bufOff == this.bufBlock.length) {
      outputBlock(paramArrayOfbyte, paramInt);
      return 16;
    } 
    return 0;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException {
    checkStatus();
    if (paramArrayOfbyte1.length < paramInt1 + paramInt2)
      throw new DataLengthException("Input buffer too short"); 
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt2; b2++) {
      this.bufBlock[this.bufOff] = paramArrayOfbyte1[paramInt1 + b2];
      if (++this.bufOff == this.bufBlock.length) {
        outputBlock(paramArrayOfbyte2, paramInt3 + b1);
        b1 += 16;
      } 
    } 
    return b1;
  }
  
  private void outputBlock(byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte.length < paramInt + 16)
      throw new OutputLengthException("Output buffer too short"); 
    if (this.totalLength == 0L)
      initCipher(); 
    gCTRBlock(this.bufBlock, paramArrayOfbyte, paramInt);
    if (this.forEncryption) {
      this.bufOff = 0;
    } else {
      System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
      this.bufOff = this.macSize;
    } 
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, InvalidCipherTextException {
    checkStatus();
    if (this.totalLength == 0L)
      initCipher(); 
    int i = this.bufOff;
    if (this.forEncryption) {
      if (paramArrayOfbyte.length < paramInt + i + this.macSize)
        throw new OutputLengthException("Output buffer too short"); 
    } else {
      if (i < this.macSize)
        throw new InvalidCipherTextException("data too short"); 
      i -= this.macSize;
      if (paramArrayOfbyte.length < paramInt + i)
        throw new OutputLengthException("Output buffer too short"); 
    } 
    if (i > 0)
      gCTRPartial(this.bufBlock, 0, i, paramArrayOfbyte, paramInt); 
    this.atLength += this.atBlockPos;
    if (this.atLength > this.atLengthPre) {
      if (this.atBlockPos > 0)
        gHASHPartial(this.S_at, this.atBlock, 0, this.atBlockPos); 
      if (this.atLengthPre > 0L)
        GCMUtil.xor(this.S_at, this.S_atPre); 
      long l = this.totalLength * 8L + 127L >>> 7L;
      byte[] arrayOfByte = new byte[16];
      if (this.exp == null) {
        this.exp = (GCMExponentiator)new Tables1kGCMExponentiator();
        this.exp.init(this.H);
      } 
      this.exp.exponentiateX(l, arrayOfByte);
      GCMUtil.multiply(this.S_at, arrayOfByte);
      GCMUtil.xor(this.S, this.S_at);
    } 
    byte[] arrayOfByte1 = new byte[16];
    Pack.longToBigEndian(this.atLength * 8L, arrayOfByte1, 0);
    Pack.longToBigEndian(this.totalLength * 8L, arrayOfByte1, 8);
    gHASHBlock(this.S, arrayOfByte1);
    byte[] arrayOfByte2 = new byte[16];
    this.cipher.processBlock(this.J0, 0, arrayOfByte2, 0);
    GCMUtil.xor(arrayOfByte2, this.S);
    int j = i;
    this.macBlock = new byte[this.macSize];
    System.arraycopy(arrayOfByte2, 0, this.macBlock, 0, this.macSize);
    if (this.forEncryption) {
      System.arraycopy(this.macBlock, 0, paramArrayOfbyte, paramInt + this.bufOff, this.macSize);
      j += this.macSize;
    } else {
      byte[] arrayOfByte = new byte[this.macSize];
      System.arraycopy(this.bufBlock, i, arrayOfByte, 0, this.macSize);
      if (!Arrays.constantTimeAreEqual(this.macBlock, arrayOfByte))
        throw new InvalidCipherTextException("mac check in GCM failed"); 
    } 
    reset(false);
    return j;
  }
  
  public void reset() {
    reset(true);
  }
  
  private void reset(boolean paramBoolean) {
    this.cipher.reset();
    this.S = new byte[16];
    this.S_at = new byte[16];
    this.S_atPre = new byte[16];
    this.atBlock = new byte[16];
    this.atBlockPos = 0;
    this.atLength = 0L;
    this.atLengthPre = 0L;
    this.counter = Arrays.clone(this.J0);
    this.blocksRemaining = -2;
    this.bufOff = 0;
    this.totalLength = 0L;
    if (this.bufBlock != null)
      Arrays.fill(this.bufBlock, (byte)0); 
    if (paramBoolean)
      this.macBlock = null; 
    if (this.forEncryption) {
      this.initialised = false;
    } else if (this.initialAssociatedText != null) {
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
    } 
  }
  
  private void gCTRBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    byte[] arrayOfByte = getNextCounterBlock();
    GCMUtil.xor(arrayOfByte, paramArrayOfbyte1);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte2, paramInt, 16);
    gHASHBlock(this.S, this.forEncryption ? arrayOfByte : paramArrayOfbyte1);
    this.totalLength += 16L;
  }
  
  private void gCTRPartial(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    byte[] arrayOfByte = getNextCounterBlock();
    GCMUtil.xor(arrayOfByte, paramArrayOfbyte1, paramInt1, paramInt2);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte2, paramInt3, paramInt2);
    gHASHPartial(this.S, this.forEncryption ? arrayOfByte : paramArrayOfbyte1, 0, paramInt2);
    this.totalLength += paramInt2;
  }
  
  private void gHASH(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    for (byte b = 0; b < paramInt; b += 16) {
      int i = Math.min(paramInt - b, 16);
      gHASHPartial(paramArrayOfbyte1, paramArrayOfbyte2, b, i);
    } 
  }
  
  private void gHASHBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    GCMUtil.xor(paramArrayOfbyte1, paramArrayOfbyte2);
    this.multiplier.multiplyH(paramArrayOfbyte1);
  }
  
  private void gHASHPartial(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
    GCMUtil.xor(paramArrayOfbyte1, paramArrayOfbyte2, paramInt1, paramInt2);
    this.multiplier.multiplyH(paramArrayOfbyte1);
  }
  
  private byte[] getNextCounterBlock() {
    if (this.blocksRemaining == 0)
      throw new IllegalStateException("Attempt to process too many blocks"); 
    this.blocksRemaining--;
    int i = 1;
    i += this.counter[15] & 0xFF;
    this.counter[15] = (byte)i;
    i >>>= 8;
    i += this.counter[14] & 0xFF;
    this.counter[14] = (byte)i;
    i >>>= 8;
    i += this.counter[13] & 0xFF;
    this.counter[13] = (byte)i;
    i >>>= 8;
    i += this.counter[12] & 0xFF;
    this.counter[12] = (byte)i;
    byte[] arrayOfByte = new byte[16];
    this.cipher.processBlock(this.counter, 0, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  private void checkStatus() {
    if (!this.initialised) {
      if (this.forEncryption)
        throw new IllegalStateException("GCM cipher cannot be reused for encryption"); 
      throw new IllegalStateException("GCM cipher needs to be initialised");
    } 
  }
}
