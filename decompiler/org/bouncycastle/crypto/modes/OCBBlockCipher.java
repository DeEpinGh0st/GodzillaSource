package org.bouncycastle.crypto.modes;

import java.util.Vector;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class OCBBlockCipher implements AEADBlockCipher {
  private static final int BLOCK_SIZE = 16;
  
  private BlockCipher hashCipher;
  
  private BlockCipher mainCipher;
  
  private boolean forEncryption;
  
  private int macSize;
  
  private byte[] initialAssociatedText;
  
  private Vector L;
  
  private byte[] L_Asterisk;
  
  private byte[] L_Dollar;
  
  private byte[] KtopInput = null;
  
  private byte[] Stretch = new byte[24];
  
  private byte[] OffsetMAIN_0 = new byte[16];
  
  private byte[] hashBlock;
  
  private byte[] mainBlock;
  
  private int hashBlockPos;
  
  private int mainBlockPos;
  
  private long hashBlockCount;
  
  private long mainBlockCount;
  
  private byte[] OffsetHASH;
  
  private byte[] Sum;
  
  private byte[] OffsetMAIN = new byte[16];
  
  private byte[] Checksum;
  
  private byte[] macBlock;
  
  public OCBBlockCipher(BlockCipher paramBlockCipher1, BlockCipher paramBlockCipher2) {
    if (paramBlockCipher1 == null)
      throw new IllegalArgumentException("'hashCipher' cannot be null"); 
    if (paramBlockCipher1.getBlockSize() != 16)
      throw new IllegalArgumentException("'hashCipher' must have a block size of 16"); 
    if (paramBlockCipher2 == null)
      throw new IllegalArgumentException("'mainCipher' cannot be null"); 
    if (paramBlockCipher2.getBlockSize() != 16)
      throw new IllegalArgumentException("'mainCipher' must have a block size of 16"); 
    if (!paramBlockCipher1.getAlgorithmName().equals(paramBlockCipher2.getAlgorithmName()))
      throw new IllegalArgumentException("'hashCipher' and 'mainCipher' must be the same algorithm"); 
    this.hashCipher = paramBlockCipher1;
    this.mainCipher = paramBlockCipher2;
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.mainCipher;
  }
  
  public String getAlgorithmName() {
    return this.mainCipher.getAlgorithmName() + "/OCB";
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    KeyParameter keyParameter;
    byte[] arrayOfByte;
    boolean bool = this.forEncryption;
    this.forEncryption = paramBoolean;
    this.macBlock = null;
    if (paramCipherParameters instanceof AEADParameters) {
      AEADParameters aEADParameters = (AEADParameters)paramCipherParameters;
      arrayOfByte = aEADParameters.getNonce();
      this.initialAssociatedText = aEADParameters.getAssociatedText();
      int m = aEADParameters.getMacSize();
      if (m < 64 || m > 128 || m % 8 != 0)
        throw new IllegalArgumentException("Invalid value for MAC size: " + m); 
      this.macSize = m / 8;
      keyParameter = aEADParameters.getKey();
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      arrayOfByte = parametersWithIV.getIV();
      this.initialAssociatedText = null;
      this.macSize = 16;
      keyParameter = (KeyParameter)parametersWithIV.getParameters();
    } else {
      throw new IllegalArgumentException("invalid parameters passed to OCB");
    } 
    this.hashBlock = new byte[16];
    this.mainBlock = new byte[paramBoolean ? 16 : (16 + this.macSize)];
    if (arrayOfByte == null)
      arrayOfByte = new byte[0]; 
    if (arrayOfByte.length > 15)
      throw new IllegalArgumentException("IV must be no more than 15 bytes"); 
    if (keyParameter != null) {
      this.hashCipher.init(true, (CipherParameters)keyParameter);
      this.mainCipher.init(paramBoolean, (CipherParameters)keyParameter);
      this.KtopInput = null;
    } else if (bool != paramBoolean) {
      throw new IllegalArgumentException("cannot change encrypting state without providing key.");
    } 
    this.L_Asterisk = new byte[16];
    this.hashCipher.processBlock(this.L_Asterisk, 0, this.L_Asterisk, 0);
    this.L_Dollar = OCB_double(this.L_Asterisk);
    this.L = new Vector();
    this.L.addElement(OCB_double(this.L_Dollar));
    int i = processNonce(arrayOfByte);
    int j = i % 8;
    int k = i / 8;
    if (j == 0) {
      System.arraycopy(this.Stretch, k, this.OffsetMAIN_0, 0, 16);
    } else {
      for (byte b = 0; b < 16; b++) {
        int m = this.Stretch[k] & 0xFF;
        int n = this.Stretch[++k] & 0xFF;
        this.OffsetMAIN_0[b] = (byte)(m << j | n >>> 8 - j);
      } 
    } 
    this.hashBlockPos = 0;
    this.mainBlockPos = 0;
    this.hashBlockCount = 0L;
    this.mainBlockCount = 0L;
    this.OffsetHASH = new byte[16];
    this.Sum = new byte[16];
    System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
    this.Checksum = new byte[16];
    if (this.initialAssociatedText != null)
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
  }
  
  protected int processNonce(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, arrayOfByte.length - paramArrayOfbyte.length, paramArrayOfbyte.length);
    arrayOfByte[0] = (byte)(this.macSize << 4);
    arrayOfByte[15 - paramArrayOfbyte.length] = (byte)(arrayOfByte[15 - paramArrayOfbyte.length] | 0x1);
    int i = arrayOfByte[15] & 0x3F;
    arrayOfByte[15] = (byte)(arrayOfByte[15] & 0xC0);
    if (this.KtopInput == null || !Arrays.areEqual(arrayOfByte, this.KtopInput)) {
      byte[] arrayOfByte1 = new byte[16];
      this.KtopInput = arrayOfByte;
      this.hashCipher.processBlock(this.KtopInput, 0, arrayOfByte1, 0);
      System.arraycopy(arrayOfByte1, 0, this.Stretch, 0, 16);
      for (byte b = 0; b < 8; b++)
        this.Stretch[16 + b] = (byte)(arrayOfByte1[b] ^ arrayOfByte1[b + 1]); 
    } 
    return i;
  }
  
  public byte[] getMac() {
    return (this.macBlock == null) ? new byte[this.macSize] : Arrays.clone(this.macBlock);
  }
  
  public int getOutputSize(int paramInt) {
    int i = paramInt + this.mainBlockPos;
    return this.forEncryption ? (i + this.macSize) : ((i < this.macSize) ? 0 : (i - this.macSize));
  }
  
  public int getUpdateOutputSize(int paramInt) {
    int i = paramInt + this.mainBlockPos;
    if (!this.forEncryption) {
      if (i < this.macSize)
        return 0; 
      i -= this.macSize;
    } 
    return i - i % 16;
  }
  
  public void processAADByte(byte paramByte) {
    this.hashBlock[this.hashBlockPos] = paramByte;
    if (++this.hashBlockPos == this.hashBlock.length)
      processHashBlock(); 
  }
  
  public void processAADBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    for (byte b = 0; b < paramInt2; b++) {
      this.hashBlock[this.hashBlockPos] = paramArrayOfbyte[paramInt1 + b];
      if (++this.hashBlockPos == this.hashBlock.length)
        processHashBlock(); 
    } 
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) throws DataLengthException {
    this.mainBlock[this.mainBlockPos] = paramByte;
    if (++this.mainBlockPos == this.mainBlock.length) {
      processMainBlock(paramArrayOfbyte, paramInt);
      return 16;
    } 
    return 0;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException {
    if (paramArrayOfbyte1.length < paramInt1 + paramInt2)
      throw new DataLengthException("Input buffer too short"); 
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt2; b2++) {
      this.mainBlock[this.mainBlockPos] = paramArrayOfbyte1[paramInt1 + b2];
      if (++this.mainBlockPos == this.mainBlock.length) {
        processMainBlock(paramArrayOfbyte2, paramInt3 + b1);
        b1 += 16;
      } 
    } 
    return b1;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, InvalidCipherTextException {
    byte[] arrayOfByte = null;
    if (!this.forEncryption) {
      if (this.mainBlockPos < this.macSize)
        throw new InvalidCipherTextException("data too short"); 
      this.mainBlockPos -= this.macSize;
      arrayOfByte = new byte[this.macSize];
      System.arraycopy(this.mainBlock, this.mainBlockPos, arrayOfByte, 0, this.macSize);
    } 
    if (this.hashBlockPos > 0) {
      OCB_extend(this.hashBlock, this.hashBlockPos);
      updateHASH(this.L_Asterisk);
    } 
    if (this.mainBlockPos > 0) {
      if (this.forEncryption) {
        OCB_extend(this.mainBlock, this.mainBlockPos);
        xor(this.Checksum, this.mainBlock);
      } 
      xor(this.OffsetMAIN, this.L_Asterisk);
      byte[] arrayOfByte1 = new byte[16];
      this.hashCipher.processBlock(this.OffsetMAIN, 0, arrayOfByte1, 0);
      xor(this.mainBlock, arrayOfByte1);
      if (paramArrayOfbyte.length < paramInt + this.mainBlockPos)
        throw new OutputLengthException("Output buffer too short"); 
      System.arraycopy(this.mainBlock, 0, paramArrayOfbyte, paramInt, this.mainBlockPos);
      if (!this.forEncryption) {
        OCB_extend(this.mainBlock, this.mainBlockPos);
        xor(this.Checksum, this.mainBlock);
      } 
    } 
    xor(this.Checksum, this.OffsetMAIN);
    xor(this.Checksum, this.L_Dollar);
    this.hashCipher.processBlock(this.Checksum, 0, this.Checksum, 0);
    xor(this.Checksum, this.Sum);
    this.macBlock = new byte[this.macSize];
    System.arraycopy(this.Checksum, 0, this.macBlock, 0, this.macSize);
    int i = this.mainBlockPos;
    if (this.forEncryption) {
      if (paramArrayOfbyte.length < paramInt + i + this.macSize)
        throw new OutputLengthException("Output buffer too short"); 
      System.arraycopy(this.macBlock, 0, paramArrayOfbyte, paramInt + i, this.macSize);
      i += this.macSize;
    } else if (!Arrays.constantTimeAreEqual(this.macBlock, arrayOfByte)) {
      throw new InvalidCipherTextException("mac check in OCB failed");
    } 
    reset(false);
    return i;
  }
  
  public void reset() {
    reset(true);
  }
  
  protected void clear(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte != null)
      Arrays.fill(paramArrayOfbyte, (byte)0); 
  }
  
  protected byte[] getLSub(int paramInt) {
    while (paramInt >= this.L.size())
      this.L.addElement(OCB_double(this.L.lastElement())); 
    return this.L.elementAt(paramInt);
  }
  
  protected void processHashBlock() {
    updateHASH(getLSub(OCB_ntz(++this.hashBlockCount)));
    this.hashBlockPos = 0;
  }
  
  protected void processMainBlock(byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte.length < paramInt + 16)
      throw new OutputLengthException("Output buffer too short"); 
    if (this.forEncryption) {
      xor(this.Checksum, this.mainBlock);
      this.mainBlockPos = 0;
    } 
    xor(this.OffsetMAIN, getLSub(OCB_ntz(++this.mainBlockCount)));
    xor(this.mainBlock, this.OffsetMAIN);
    this.mainCipher.processBlock(this.mainBlock, 0, this.mainBlock, 0);
    xor(this.mainBlock, this.OffsetMAIN);
    System.arraycopy(this.mainBlock, 0, paramArrayOfbyte, paramInt, 16);
    if (!this.forEncryption) {
      xor(this.Checksum, this.mainBlock);
      System.arraycopy(this.mainBlock, 16, this.mainBlock, 0, this.macSize);
      this.mainBlockPos = this.macSize;
    } 
  }
  
  protected void reset(boolean paramBoolean) {
    this.hashCipher.reset();
    this.mainCipher.reset();
    clear(this.hashBlock);
    clear(this.mainBlock);
    this.hashBlockPos = 0;
    this.mainBlockPos = 0;
    this.hashBlockCount = 0L;
    this.mainBlockCount = 0L;
    clear(this.OffsetHASH);
    clear(this.Sum);
    System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
    clear(this.Checksum);
    if (paramBoolean)
      this.macBlock = null; 
    if (this.initialAssociatedText != null)
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
  }
  
  protected void updateHASH(byte[] paramArrayOfbyte) {
    xor(this.OffsetHASH, paramArrayOfbyte);
    xor(this.hashBlock, this.OffsetHASH);
    this.hashCipher.processBlock(this.hashBlock, 0, this.hashBlock, 0);
    xor(this.Sum, this.hashBlock);
  }
  
  protected static byte[] OCB_double(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[16];
    int i = shiftLeft(paramArrayOfbyte, arrayOfByte);
    arrayOfByte[15] = (byte)(arrayOfByte[15] ^ 135 >>> 1 - i << 3);
    return arrayOfByte;
  }
  
  protected static void OCB_extend(byte[] paramArrayOfbyte, int paramInt) {
    paramArrayOfbyte[paramInt] = Byte.MIN_VALUE;
    while (++paramInt < 16)
      paramArrayOfbyte[paramInt] = 0; 
  }
  
  protected static int OCB_ntz(long paramLong) {
    if (paramLong == 0L)
      return 64; 
    byte b = 0;
    while ((paramLong & 0x1L) == 0L) {
      b++;
      paramLong >>>= 1L;
    } 
    return b;
  }
  
  protected static int shiftLeft(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte b = 16;
    int i;
    for (i = 0; --b >= 0; i = j >>> 7 & 0x1) {
      int j = paramArrayOfbyte1[b] & 0xFF;
      paramArrayOfbyte2[b] = (byte)(j << 1 | i);
    } 
    return i;
  }
  
  protected static void xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    for (byte b = 15; b >= 0; b--)
      paramArrayOfbyte1[b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]); 
  }
}
