package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class KGCMBlockCipher implements AEADBlockCipher {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger MASK_1_128 = new BigInteger("340282366920938463463374607431768211456", 10);
  
  private static final BigInteger MASK_2_128 = new BigInteger("340282366920938463463374607431768211455", 10);
  
  private static final BigInteger POLYRED_128 = new BigInteger("135", 10);
  
  private static final BigInteger MASK_1_256 = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639936", 10);
  
  private static final BigInteger MASK_2_256 = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639935", 10);
  
  private static final BigInteger POLYRED_256 = new BigInteger("1061", 10);
  
  private static final BigInteger MASK_1_512 = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084096", 10);
  
  private static final BigInteger MASK_2_512 = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084095", 10);
  
  private static final BigInteger POLYRED_512 = new BigInteger("293", 10);
  
  private static final int MIN_MAC_BITS = 64;
  
  private static final int BITS_IN_BYTE = 8;
  
  private BlockCipher engine;
  
  private BufferedBlockCipher ctrEngine;
  
  private int macSize;
  
  private boolean forEncryption;
  
  private byte[] initialAssociatedText;
  
  private byte[] macBlock;
  
  private byte[] iv;
  
  private byte[] H;
  
  private byte[] b;
  
  private byte[] temp;
  
  private int lambda_o;
  
  private int lambda_c;
  
  private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
  
  private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();
  
  public KGCMBlockCipher(BlockCipher paramBlockCipher) {
    this.engine = paramBlockCipher;
    this.ctrEngine = new BufferedBlockCipher((BlockCipher)new KCTRBlockCipher(this.engine));
    this.macSize = 0;
    this.initialAssociatedText = new byte[this.engine.getBlockSize()];
    this.iv = new byte[this.engine.getBlockSize()];
    this.H = new byte[this.engine.getBlockSize()];
    this.b = new byte[this.engine.getBlockSize()];
    this.temp = new byte[this.engine.getBlockSize()];
    this.lambda_c = 0;
    this.lambda_o = 0;
    this.macBlock = null;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    KeyParameter keyParameter;
    this.forEncryption = paramBoolean;
    if (paramCipherParameters instanceof AEADParameters) {
      AEADParameters aEADParameters = (AEADParameters)paramCipherParameters;
      byte[] arrayOfByte = aEADParameters.getNonce();
      int i = this.iv.length - arrayOfByte.length;
      Arrays.fill(this.iv, (byte)0);
      System.arraycopy(arrayOfByte, 0, this.iv, i, arrayOfByte.length);
      this.initialAssociatedText = aEADParameters.getAssociatedText();
      int j = aEADParameters.getMacSize();
      if (j < 64 || j > this.engine.getBlockSize() * 8 || j % 8 != 0)
        throw new IllegalArgumentException("Invalid value for MAC size: " + j); 
      this.macSize = j / 8;
      keyParameter = aEADParameters.getKey();
      if (this.initialAssociatedText != null)
        processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      byte[] arrayOfByte = parametersWithIV.getIV();
      int i = this.iv.length - arrayOfByte.length;
      Arrays.fill(this.iv, (byte)0);
      System.arraycopy(arrayOfByte, 0, this.iv, i, arrayOfByte.length);
      this.initialAssociatedText = null;
      this.macSize = this.engine.getBlockSize();
      keyParameter = (KeyParameter)parametersWithIV.getParameters();
    } else {
      throw new IllegalArgumentException("Invalid parameter passed");
    } 
    this.macBlock = new byte[this.engine.getBlockSize()];
    this.ctrEngine.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)keyParameter, this.iv));
    this.engine.init(true, (CipherParameters)keyParameter);
  }
  
  public String getAlgorithmName() {
    return this.engine.getAlgorithmName() + "/KGCM";
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
  
  private void processAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.lambda_o = paramInt2 * 8;
    this.engine.processBlock(this.H, 0, this.H, 0);
    int i = paramInt2;
    int j;
    for (j = paramInt1; i > 0; j += this.engine.getBlockSize()) {
      for (byte b = 0; b < this.engine.getBlockSize(); b++)
        this.b[b] = (byte)(this.b[b] ^ paramArrayOfbyte[j + b]); 
      multiplyOverField(this.engine.getBlockSize() * 8, this.b, this.H, this.temp);
      this.temp = Arrays.reverse(this.temp);
      System.arraycopy(this.temp, 0, this.b, 0, this.engine.getBlockSize());
      i -= this.engine.getBlockSize();
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
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, InvalidCipherTextException {
    int j;
    int i = this.data.size();
    if (this.associatedText.size() > 0)
      processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size()); 
    if (this.forEncryption) {
      if (paramArrayOfbyte.length - paramInt < i + this.macSize)
        throw new OutputLengthException("Output buffer too short"); 
      this.lambda_c = i * 8;
      j = this.ctrEngine.processBytes(this.data.getBuffer(), 0, i, paramArrayOfbyte, paramInt);
      j += this.ctrEngine.doFinal(paramArrayOfbyte, paramInt + j);
      calculateMac(paramArrayOfbyte, paramInt, i);
    } else {
      this.lambda_c = (i - this.macSize) * 8;
      calculateMac(this.data.getBuffer(), 0, i - this.macSize);
      j = this.ctrEngine.processBytes(this.data.getBuffer(), 0, i - this.macSize, paramArrayOfbyte, paramInt);
      j += this.ctrEngine.doFinal(paramArrayOfbyte, paramInt + j);
    } 
    paramInt += j;
    if (this.macBlock == null)
      throw new IllegalStateException("mac is not calculated"); 
    if (this.forEncryption) {
      System.arraycopy(this.macBlock, 0, paramArrayOfbyte, paramInt, this.macSize);
      reset();
      return j + this.macSize;
    } 
    byte[] arrayOfByte1 = new byte[this.macSize];
    System.arraycopy(this.data.getBuffer(), j, arrayOfByte1, 0, this.macSize);
    byte[] arrayOfByte2 = new byte[this.macSize];
    System.arraycopy(this.macBlock, 0, arrayOfByte2, 0, this.macSize);
    if (!Arrays.constantTimeAreEqual(arrayOfByte1, arrayOfByte2))
      throw new InvalidCipherTextException("mac verification failed"); 
    reset();
    return j;
  }
  
  public byte[] getMac() {
    byte[] arrayOfByte = new byte[this.macSize];
    System.arraycopy(this.macBlock, 0, arrayOfByte, 0, this.macSize);
    return arrayOfByte;
  }
  
  public int getUpdateOutputSize(int paramInt) {
    return paramInt;
  }
  
  public int getOutputSize(int paramInt) {
    return this.forEncryption ? paramInt : (paramInt + this.macSize);
  }
  
  public void reset() {
    this.H = new byte[this.engine.getBlockSize()];
    this.b = new byte[this.engine.getBlockSize()];
    this.temp = new byte[this.engine.getBlockSize()];
    this.lambda_c = 0;
    this.lambda_o = 0;
    this.engine.reset();
    this.data.reset();
    this.associatedText.reset();
    if (this.initialAssociatedText != null)
      processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length); 
  }
  
  private void calculateMac(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.macBlock = new byte[this.engine.getBlockSize()];
    int i = paramInt2;
    int j;
    for (j = paramInt1; i > 0; j += this.engine.getBlockSize()) {
      for (byte b1 = 0; b1 < this.engine.getBlockSize(); b1++)
        this.b[b1] = (byte)(this.b[b1] ^ paramArrayOfbyte[b1 + j]); 
      multiplyOverField(this.engine.getBlockSize() * 8, this.b, this.H, this.temp);
      this.temp = Arrays.reverse(this.temp);
      System.arraycopy(this.temp, 0, this.b, 0, this.engine.getBlockSize());
      i -= this.engine.getBlockSize();
    } 
    Arrays.fill(this.temp, (byte)0);
    intToBytes(this.lambda_o, this.temp, 0);
    intToBytes(this.lambda_c, this.temp, this.engine.getBlockSize() / 2);
    for (byte b = 0; b < this.engine.getBlockSize(); b++)
      this.b[b] = (byte)(this.b[b] ^ this.temp[b]); 
    this.engine.processBlock(this.b, 0, this.macBlock, 0);
  }
  
  private void intToBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >> 24);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 16);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
  
  private void multiplyOverField(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    BigInteger bigInteger1;
    BigInteger bigInteger2;
    BigInteger bigInteger3;
    byte[] arrayOfByte1 = new byte[this.engine.getBlockSize()];
    byte[] arrayOfByte2 = new byte[this.engine.getBlockSize()];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte1, 0, this.engine.getBlockSize());
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte2, 0, this.engine.getBlockSize());
    arrayOfByte1 = Arrays.reverse(arrayOfByte1);
    arrayOfByte2 = Arrays.reverse(arrayOfByte2);
    switch (paramInt) {
      case 128:
        bigInteger1 = MASK_1_128;
        bigInteger2 = MASK_2_128;
        bigInteger3 = POLYRED_128;
        break;
      case 256:
        bigInteger1 = MASK_1_256;
        bigInteger2 = MASK_2_256;
        bigInteger3 = POLYRED_256;
        break;
      case 512:
        bigInteger1 = MASK_1_512;
        bigInteger2 = MASK_2_512;
        bigInteger3 = POLYRED_512;
        break;
      default:
        bigInteger1 = MASK_1_128;
        bigInteger2 = MASK_2_128;
        bigInteger3 = POLYRED_128;
        break;
    } 
    BigInteger bigInteger4 = ZERO;
    BigInteger bigInteger5 = new BigInteger(1, arrayOfByte1);
    for (BigInteger bigInteger6 = new BigInteger(1, arrayOfByte2); !bigInteger6.equals(ZERO); bigInteger6 = bigInteger6.shiftRight(1)) {
      if (bigInteger6.and(ONE).equals(ONE))
        bigInteger4 = bigInteger4.xor(bigInteger5); 
      bigInteger5 = bigInteger5.shiftLeft(1);
      if (!bigInteger5.and(bigInteger1).equals(ZERO))
        bigInteger5 = bigInteger5.xor(bigInteger3); 
    } 
    byte[] arrayOfByte3 = BigIntegers.asUnsignedByteArray(bigInteger4.and(bigInteger2));
    Arrays.fill(paramArrayOfbyte3, (byte)0);
    System.arraycopy(arrayOfByte3, 0, paramArrayOfbyte3, 0, arrayOfByte3.length);
  }
  
  private class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
    public byte[] getBuffer() {
      return this.buf;
    }
  }
}
