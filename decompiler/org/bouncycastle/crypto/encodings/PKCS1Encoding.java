package org.bouncycastle.crypto.encodings;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class PKCS1Encoding implements AsymmetricBlockCipher {
  public static final String STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.strict";
  
  public static final String NOT_STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.not_strict";
  
  private static final int HEADER_LENGTH = 10;
  
  private SecureRandom random;
  
  private AsymmetricBlockCipher engine;
  
  private boolean forEncryption;
  
  private boolean forPrivateKey;
  
  private boolean useStrictLength;
  
  private int pLen = -1;
  
  private byte[] fallback = null;
  
  private byte[] blockBuffer;
  
  public PKCS1Encoding(AsymmetricBlockCipher paramAsymmetricBlockCipher) {
    this.engine = paramAsymmetricBlockCipher;
    this.useStrictLength = useStrict();
  }
  
  public PKCS1Encoding(AsymmetricBlockCipher paramAsymmetricBlockCipher, int paramInt) {
    this.engine = paramAsymmetricBlockCipher;
    this.useStrictLength = useStrict();
    this.pLen = paramInt;
  }
  
  public PKCS1Encoding(AsymmetricBlockCipher paramAsymmetricBlockCipher, byte[] paramArrayOfbyte) {
    this.engine = paramAsymmetricBlockCipher;
    this.useStrictLength = useStrict();
    this.fallback = paramArrayOfbyte;
    this.pLen = paramArrayOfbyte.length;
  }
  
  private boolean useStrict() {
    String str1 = AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public Object run() {
            return System.getProperty("org.bouncycastle.pkcs1.strict");
          }
        });
    String str2 = AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public Object run() {
            return System.getProperty("org.bouncycastle.pkcs1.not_strict");
          }
        });
    return (str2 != null) ? (!str2.equals("true")) : ((str1 == null || str1.equals("true")));
  }
  
  public AsymmetricBlockCipher getUnderlyingCipher() {
    return this.engine;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    AsymmetricKeyParameter asymmetricKeyParameter;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.random = parametersWithRandom.getRandom();
      asymmetricKeyParameter = (AsymmetricKeyParameter)parametersWithRandom.getParameters();
    } else {
      asymmetricKeyParameter = (AsymmetricKeyParameter)paramCipherParameters;
      if (!asymmetricKeyParameter.isPrivate() && paramBoolean)
        this.random = new SecureRandom(); 
    } 
    this.engine.init(paramBoolean, paramCipherParameters);
    this.forPrivateKey = asymmetricKeyParameter.isPrivate();
    this.forEncryption = paramBoolean;
    this.blockBuffer = new byte[this.engine.getOutputBlockSize()];
    if (this.pLen > 0 && this.fallback == null && this.random == null)
      throw new IllegalArgumentException("encoder requires random"); 
  }
  
  public int getInputBlockSize() {
    int i = this.engine.getInputBlockSize();
    return this.forEncryption ? (i - 10) : i;
  }
  
  public int getOutputBlockSize() {
    int i = this.engine.getOutputBlockSize();
    return this.forEncryption ? i : (i - 10);
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    return this.forEncryption ? encodeBlock(paramArrayOfbyte, paramInt1, paramInt2) : decodeBlock(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  private byte[] encodeBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    if (paramInt2 > getInputBlockSize())
      throw new IllegalArgumentException("input data too large"); 
    byte[] arrayOfByte = new byte[this.engine.getInputBlockSize()];
    if (this.forPrivateKey) {
      arrayOfByte[0] = 1;
      for (byte b = 1; b != arrayOfByte.length - paramInt2 - 1; b++)
        arrayOfByte[b] = -1; 
    } else {
      this.random.nextBytes(arrayOfByte);
      arrayOfByte[0] = 2;
      for (byte b = 1; b != arrayOfByte.length - paramInt2 - 1; b++) {
        while (arrayOfByte[b] == 0)
          arrayOfByte[b] = (byte)this.random.nextInt(); 
      } 
    } 
    arrayOfByte[arrayOfByte.length - paramInt2 - 1] = 0;
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, arrayOfByte.length - paramInt2, paramInt2);
    return this.engine.processBlock(arrayOfByte, 0, arrayOfByte.length);
  }
  
  private static int checkPkcs1Encoding(byte[] paramArrayOfbyte, int paramInt) {
    int i = 0;
    i |= paramArrayOfbyte[0] ^ 0x2;
    int j = paramArrayOfbyte.length - paramInt + 1;
    for (byte b = 1; b < j; b++) {
      byte b1 = paramArrayOfbyte[b];
      int k = b1 | b1 >> 1;
      k |= k >> 2;
      k |= k >> 4;
      i |= (k & 0x1) - 1;
    } 
    i |= paramArrayOfbyte[paramArrayOfbyte.length - paramInt + 1];
    i |= i >> 1;
    i |= i >> 2;
    i |= i >> 4;
    return (i & 0x1) - 1 ^ 0xFFFFFFFF;
  }
  
  private byte[] decodeBlockOrRandom(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte2;
    if (!this.forPrivateKey)
      throw new InvalidCipherTextException("sorry, this method is only for decryption, not for signing"); 
    byte[] arrayOfByte1 = this.engine.processBlock(paramArrayOfbyte, paramInt1, paramInt2);
    if (this.fallback == null) {
      arrayOfByte2 = new byte[this.pLen];
      this.random.nextBytes(arrayOfByte2);
    } else {
      arrayOfByte2 = this.fallback;
    } 
    byte[] arrayOfByte3 = ((this.useStrictLength & ((arrayOfByte1.length != this.engine.getOutputBlockSize()) ? 1 : 0)) != 0) ? this.blockBuffer : arrayOfByte1;
    int i = checkPkcs1Encoding(arrayOfByte3, this.pLen);
    byte[] arrayOfByte4 = new byte[this.pLen];
    for (byte b = 0; b < this.pLen; b++)
      arrayOfByte4[b] = (byte)(arrayOfByte3[b + arrayOfByte3.length - this.pLen] & (i ^ 0xFFFFFFFF) | arrayOfByte2[b] & i); 
    Arrays.fill(arrayOfByte3, (byte)0);
    return arrayOfByte4;
  }
  
  private byte[] decodeBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte2;
    boolean bool;
    if (this.pLen != -1)
      return decodeBlockOrRandom(paramArrayOfbyte, paramInt1, paramInt2); 
    byte[] arrayOfByte1 = this.engine.processBlock(paramArrayOfbyte, paramInt1, paramInt2);
    int i = this.useStrictLength & ((arrayOfByte1.length != this.engine.getOutputBlockSize()) ? 1 : 0);
    if (arrayOfByte1.length < getOutputBlockSize()) {
      arrayOfByte2 = this.blockBuffer;
    } else {
      arrayOfByte2 = arrayOfByte1;
    } 
    byte b = arrayOfByte2[0];
    if (this.forPrivateKey) {
      bool = (b != 2) ? true : false;
    } else {
      bool = (b != 1) ? true : false;
    } 
    int j = findStart(b, arrayOfByte2);
    if ((bool | ((++j < 10) ? 1 : 0)) != 0) {
      Arrays.fill(arrayOfByte2, (byte)0);
      throw new InvalidCipherTextException("block incorrect");
    } 
    if (i != 0) {
      Arrays.fill(arrayOfByte2, (byte)0);
      throw new InvalidCipherTextException("block incorrect size");
    } 
    byte[] arrayOfByte3 = new byte[arrayOfByte2.length - j];
    System.arraycopy(arrayOfByte2, j, arrayOfByte3, 0, arrayOfByte3.length);
    return arrayOfByte3;
  }
  
  private int findStart(byte paramByte, byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    byte b = -1;
    int i = 0;
    for (byte b1 = 1; b1 != paramArrayOfbyte.length; b1++) {
      byte b2 = paramArrayOfbyte[b1];
      if ((((b2 == 0) ? 1 : 0) & ((b < 0) ? 1 : 0)) != 0)
        b = b1; 
      i |= ((paramByte == 1) ? 1 : 0) & ((b < 0) ? 1 : 0) & ((b2 != -1) ? 1 : 0);
    } 
    return (i != 0) ? -1 : b;
  }
}
