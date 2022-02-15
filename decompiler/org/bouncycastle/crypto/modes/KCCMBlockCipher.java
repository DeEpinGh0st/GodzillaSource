package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class KCCMBlockCipher implements AEADBlockCipher {
  private static final int BYTES_IN_INT = 4;
  
  private static final int BITS_IN_BYTE = 8;
  
  private static final int MAX_MAC_BIT_LENGTH = 512;
  
  private static final int MIN_MAC_BIT_LENGTH = 64;
  
  private BlockCipher engine;
  
  private int macSize;
  
  private boolean forEncryption;
  
  private byte[] initialAssociatedText;
  
  private byte[] mac;
  
  private byte[] macBlock;
  
  private byte[] nonce;
  
  private byte[] G1;
  
  private byte[] buffer;
  
  private byte[] s;
  
  private byte[] counter;
  
  private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
  
  private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();
  
  private int Nb_ = 4;
  
  private void setNb(int paramInt) {
    if (paramInt == 4 || paramInt == 6 || paramInt == 8) {
      this.Nb_ = paramInt;
    } else {
      throw new IllegalArgumentException("Nb = 4 is recommended by DSTU7624 but can be changed to only 6 or 8 in this implementation");
    } 
  }
  
  public KCCMBlockCipher(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, 4);
  }
  
  public KCCMBlockCipher(BlockCipher paramBlockCipher, int paramInt) {
    this.engine = paramBlockCipher;
    this.macSize = paramBlockCipher.getBlockSize();
    this.nonce = new byte[paramBlockCipher.getBlockSize()];
    this.initialAssociatedText = new byte[paramBlockCipher.getBlockSize()];
    this.mac = new byte[paramBlockCipher.getBlockSize()];
    this.macBlock = new byte[paramBlockCipher.getBlockSize()];
    this.G1 = new byte[paramBlockCipher.getBlockSize()];
    this.buffer = new byte[paramBlockCipher.getBlockSize()];
    this.s = new byte[paramBlockCipher.getBlockSize()];
    this.counter = new byte[paramBlockCipher.getBlockSize()];
    setNb(paramInt);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    CipherParameters cipherParameters;
    if (paramCipherParameters instanceof AEADParameters) {
      AEADParameters aEADParameters = (AEADParameters)paramCipherParameters;
      if (aEADParameters.getMacSize() > 512 || aEADParameters.getMacSize() < 64 || aEADParameters.getMacSize() % 8 != 0)
        throw new IllegalArgumentException("Invalid mac size specified"); 
      this.nonce = aEADParameters.getNonce();
      this.macSize = aEADParameters.getMacSize() / 8;
      this.initialAssociatedText = aEADParameters.getAssociatedText();
      KeyParameter keyParameter = aEADParameters.getKey();
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      this.nonce = ((ParametersWithIV)paramCipherParameters).getIV();
      this.macSize = this.engine.getBlockSize();
      this.initialAssociatedText = null;
      cipherParameters = ((ParametersWithIV)paramCipherParameters).getParameters();
    } else {
      throw new IllegalArgumentException("Invalid parameters specified");
    } 
    this.mac = new byte[this.macSize];
    this.forEncryption = paramBoolean;
    this.engine.init(true, cipherParameters);
    this.counter[0] = 1;
    if (this.initialAssociatedText != null)
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
  }
  
  public String getAlgorithmName() {
    return this.engine.getAlgorithmName() + "/KCCM";
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.engine;
  }
  
  public void processAADByte(byte paramByte) {
    this.associatedText.write(paramByte);
  }
  
  public void processAADBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.associatedText.write(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  private void processAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 - paramInt1 < this.engine.getBlockSize())
      throw new IllegalArgumentException("authText buffer too short"); 
    if (paramInt2 % this.engine.getBlockSize() != 0)
      throw new IllegalArgumentException("padding not supported"); 
    System.arraycopy(this.nonce, 0, this.G1, 0, this.nonce.length - this.Nb_ - 1);
    intToBytes(paramInt3, this.buffer, 0);
    System.arraycopy(this.buffer, 0, this.G1, this.nonce.length - this.Nb_ - 1, 4);
    this.G1[this.G1.length - 1] = getFlag(true, this.macSize);
    this.engine.processBlock(this.G1, 0, this.macBlock, 0);
    intToBytes(paramInt2, this.buffer, 0);
    if (paramInt2 <= this.engine.getBlockSize() - this.Nb_) {
      byte b;
      for (b = 0; b < paramInt2; b++)
        this.buffer[b + this.Nb_] = (byte)(this.buffer[b + this.Nb_] ^ paramArrayOfbyte[paramInt1 + b]); 
      for (b = 0; b < this.engine.getBlockSize(); b++)
        this.macBlock[b] = (byte)(this.macBlock[b] ^ this.buffer[b]); 
      this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
      return;
    } 
    int i;
    for (i = 0; i < this.engine.getBlockSize(); i++)
      this.macBlock[i] = (byte)(this.macBlock[i] ^ this.buffer[i]); 
    this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
    for (i = paramInt2; i != 0; i -= this.engine.getBlockSize()) {
      for (byte b = 0; b < this.engine.getBlockSize(); b++)
        this.macBlock[b] = (byte)(this.macBlock[b] ^ paramArrayOfbyte[b + paramInt1]); 
      this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
      paramInt1 += this.engine.getBlockSize();
    } 
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    this.data.write(paramByte);
    return 0;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException, IllegalStateException {
    if (paramArrayOfbyte1.length < paramInt1 + paramInt2)
      throw new DataLengthException("input buffer too short"); 
    this.data.write(paramArrayOfbyte1, paramInt1, paramInt2);
    return 0;
  }
  
  public int processPacket(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalStateException, InvalidCipherTextException {
    if (paramArrayOfbyte1.length - paramInt1 < paramInt2)
      throw new DataLengthException("input buffer too short"); 
    if (paramArrayOfbyte2.length - paramInt3 < paramInt2)
      throw new OutputLengthException("output buffer too short"); 
    if (this.associatedText.size() > 0)
      if (this.forEncryption) {
        processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size());
      } else {
        processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size() - this.macSize);
      }  
    if (this.forEncryption) {
      if (paramInt2 % this.engine.getBlockSize() != 0)
        throw new DataLengthException("partial blocks not supported"); 
      CalculateMac(paramArrayOfbyte1, paramInt1, paramInt2);
      this.engine.processBlock(this.nonce, 0, this.s, 0);
      int j = paramInt2;
      while (j > 0) {
        ProcessBlock(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
        j -= this.engine.getBlockSize();
        paramInt1 += this.engine.getBlockSize();
        paramInt3 += this.engine.getBlockSize();
      } 
      byte b1;
      for (b1 = 0; b1 < this.counter.length; b1++)
        this.s[b1] = (byte)(this.s[b1] + this.counter[b1]); 
      this.engine.processBlock(this.s, 0, this.buffer, 0);
      for (b1 = 0; b1 < this.macSize; b1++)
        paramArrayOfbyte2[paramInt3 + b1] = (byte)(this.buffer[b1] ^ this.macBlock[b1]); 
      System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
      reset();
      return paramInt2 + this.macSize;
    } 
    if ((paramInt2 - this.macSize) % this.engine.getBlockSize() != 0)
      throw new DataLengthException("partial blocks not supported"); 
    this.engine.processBlock(this.nonce, 0, this.s, 0);
    int i = paramInt2 / this.engine.getBlockSize();
    byte b;
    for (b = 0; b < i; b++) {
      ProcessBlock(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
      paramInt1 += this.engine.getBlockSize();
      paramInt3 += this.engine.getBlockSize();
    } 
    if (paramInt2 > paramInt1) {
      for (b = 0; b < this.counter.length; b++)
        this.s[b] = (byte)(this.s[b] + this.counter[b]); 
      this.engine.processBlock(this.s, 0, this.buffer, 0);
      for (b = 0; b < this.macSize; b++)
        paramArrayOfbyte2[paramInt3 + b] = (byte)(this.buffer[b] ^ paramArrayOfbyte1[paramInt1 + b]); 
      paramInt3 += this.macSize;
    } 
    for (b = 0; b < this.counter.length; b++)
      this.s[b] = (byte)(this.s[b] + this.counter[b]); 
    this.engine.processBlock(this.s, 0, this.buffer, 0);
    System.arraycopy(paramArrayOfbyte2, paramInt3 - this.macSize, this.buffer, 0, this.macSize);
    CalculateMac(paramArrayOfbyte2, 0, paramInt3 - this.macSize);
    System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
    byte[] arrayOfByte = new byte[this.macSize];
    System.arraycopy(this.buffer, 0, arrayOfByte, 0, this.macSize);
    if (!Arrays.constantTimeAreEqual(this.mac, arrayOfByte))
      throw new InvalidCipherTextException("mac check failed"); 
    reset();
    return paramInt2 - this.macSize;
  }
  
  private void ProcessBlock(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    byte b;
    for (b = 0; b < this.counter.length; b++)
      this.s[b] = (byte)(this.s[b] + this.counter[b]); 
    this.engine.processBlock(this.s, 0, this.buffer, 0);
    for (b = 0; b < this.engine.getBlockSize(); b++)
      paramArrayOfbyte2[paramInt3 + b] = (byte)(this.buffer[b] ^ paramArrayOfbyte1[paramInt1 + b]); 
  }
  
  private void CalculateMac(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = paramInt2;
    while (i > 0) {
      for (byte b = 0; b < this.engine.getBlockSize(); b++)
        this.macBlock[b] = (byte)(this.macBlock[b] ^ paramArrayOfbyte[paramInt1 + b]); 
      this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
      i -= this.engine.getBlockSize();
      paramInt1 += this.engine.getBlockSize();
    } 
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, InvalidCipherTextException {
    int i = processPacket(this.data.getBuffer(), 0, this.data.size(), paramArrayOfbyte, paramInt);
    reset();
    return i;
  }
  
  public byte[] getMac() {
    return Arrays.clone(this.mac);
  }
  
  public int getUpdateOutputSize(int paramInt) {
    return paramInt;
  }
  
  public int getOutputSize(int paramInt) {
    return paramInt + this.macSize;
  }
  
  public void reset() {
    Arrays.fill(this.G1, (byte)0);
    Arrays.fill(this.buffer, (byte)0);
    Arrays.fill(this.counter, (byte)0);
    Arrays.fill(this.macBlock, (byte)0);
    this.counter[0] = 1;
    this.data.reset();
    this.associatedText.reset();
    if (this.initialAssociatedText != null)
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
  }
  
  private void intToBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >> 24);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 16);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
  
  private byte getFlag(boolean paramBoolean, int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramBoolean) {
      stringBuffer.append("1");
    } else {
      stringBuffer.append("0");
    } 
    switch (paramInt) {
      case 8:
        stringBuffer.append("010");
        break;
      case 16:
        stringBuffer.append("011");
        break;
      case 32:
        stringBuffer.append("100");
        break;
      case 48:
        stringBuffer.append("101");
        break;
      case 64:
        stringBuffer.append("110");
        break;
    } 
    String str;
    for (str = Integer.toBinaryString(this.Nb_ - 1); str.length() < 4; str = (new StringBuffer(str)).insert(0, "0").toString());
    stringBuffer.append(str);
    return (byte)Integer.parseInt(stringBuffer.toString(), 2);
  }
  
  private class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
    public byte[] getBuffer() {
      return this.buf;
    }
  }
}
