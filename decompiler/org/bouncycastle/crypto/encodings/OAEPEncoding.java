package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class OAEPEncoding implements AsymmetricBlockCipher {
  private byte[] defHash;
  
  private Digest mgf1Hash;
  
  private AsymmetricBlockCipher engine;
  
  private SecureRandom random;
  
  private boolean forEncryption;
  
  public OAEPEncoding(AsymmetricBlockCipher paramAsymmetricBlockCipher) {
    this(paramAsymmetricBlockCipher, DigestFactory.createSHA1(), null);
  }
  
  public OAEPEncoding(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest) {
    this(paramAsymmetricBlockCipher, paramDigest, null);
  }
  
  public OAEPEncoding(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, byte[] paramArrayOfbyte) {
    this(paramAsymmetricBlockCipher, paramDigest, paramDigest, paramArrayOfbyte);
  }
  
  public OAEPEncoding(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest1, Digest paramDigest2, byte[] paramArrayOfbyte) {
    this.engine = paramAsymmetricBlockCipher;
    this.mgf1Hash = paramDigest2;
    this.defHash = new byte[paramDigest1.getDigestSize()];
    paramDigest1.reset();
    if (paramArrayOfbyte != null)
      paramDigest1.update(paramArrayOfbyte, 0, paramArrayOfbyte.length); 
    paramDigest1.doFinal(this.defHash, 0);
  }
  
  public AsymmetricBlockCipher getUnderlyingCipher() {
    return this.engine;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.random = parametersWithRandom.getRandom();
    } else {
      this.random = new SecureRandom();
    } 
    this.engine.init(paramBoolean, paramCipherParameters);
    this.forEncryption = paramBoolean;
  }
  
  public int getInputBlockSize() {
    int i = this.engine.getInputBlockSize();
    return this.forEncryption ? (i - 1 - 2 * this.defHash.length) : i;
  }
  
  public int getOutputBlockSize() {
    int i = this.engine.getOutputBlockSize();
    return this.forEncryption ? i : (i - 1 - 2 * this.defHash.length);
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    return this.forEncryption ? encodeBlock(paramArrayOfbyte, paramInt1, paramInt2) : decodeBlock(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public byte[] encodeBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    if (paramInt2 > getInputBlockSize())
      throw new DataLengthException("input data too long"); 
    byte[] arrayOfByte1 = new byte[getInputBlockSize() + 1 + 2 * this.defHash.length];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, arrayOfByte1.length - paramInt2, paramInt2);
    arrayOfByte1[arrayOfByte1.length - paramInt2 - 1] = 1;
    System.arraycopy(this.defHash, 0, arrayOfByte1, this.defHash.length, this.defHash.length);
    byte[] arrayOfByte2 = new byte[this.defHash.length];
    this.random.nextBytes(arrayOfByte2);
    byte[] arrayOfByte3 = maskGeneratorFunction1(arrayOfByte2, 0, arrayOfByte2.length, arrayOfByte1.length - this.defHash.length);
    int i;
    for (i = this.defHash.length; i != arrayOfByte1.length; i++)
      arrayOfByte1[i] = (byte)(arrayOfByte1[i] ^ arrayOfByte3[i - this.defHash.length]); 
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, this.defHash.length);
    arrayOfByte3 = maskGeneratorFunction1(arrayOfByte1, this.defHash.length, arrayOfByte1.length - this.defHash.length, this.defHash.length);
    for (i = 0; i != this.defHash.length; i++)
      arrayOfByte1[i] = (byte)(arrayOfByte1[i] ^ arrayOfByte3[i]); 
    return this.engine.processBlock(arrayOfByte1, 0, arrayOfByte1.length);
  }
  
  public byte[] decodeBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte1 = this.engine.processBlock(paramArrayOfbyte, paramInt1, paramInt2);
    byte[] arrayOfByte2 = new byte[this.engine.getOutputBlockSize()];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, arrayOfByte2.length - arrayOfByte1.length, arrayOfByte1.length);
    byte b = (arrayOfByte2.length < 2 * this.defHash.length + 1) ? 1 : 0;
    byte[] arrayOfByte3 = maskGeneratorFunction1(arrayOfByte2, this.defHash.length, arrayOfByte2.length - this.defHash.length, this.defHash.length);
    int i;
    for (i = 0; i != this.defHash.length; i++)
      arrayOfByte2[i] = (byte)(arrayOfByte2[i] ^ arrayOfByte3[i]); 
    arrayOfByte3 = maskGeneratorFunction1(arrayOfByte2, 0, this.defHash.length, arrayOfByte2.length - this.defHash.length);
    for (i = this.defHash.length; i != arrayOfByte2.length; i++)
      arrayOfByte2[i] = (byte)(arrayOfByte2[i] ^ arrayOfByte3[i - this.defHash.length]); 
    i = 0;
    int j;
    for (j = 0; j != this.defHash.length; j++) {
      if (this.defHash[j] != arrayOfByte2[this.defHash.length + j])
        i = 1; 
    } 
    j = arrayOfByte2.length;
    int k;
    for (k = 2 * this.defHash.length; k != arrayOfByte2.length; k++) {
      if ((((arrayOfByte2[k] != 0) ? 1 : 0) & ((j == arrayOfByte2.length) ? 1 : 0)) != 0)
        j = k; 
    } 
    k = ((j > arrayOfByte2.length - 1) ? 1 : 0) | ((arrayOfByte2[j] != 1) ? 1 : 0);
    j++;
    if ((i | b | k) != 0) {
      Arrays.fill(arrayOfByte2, (byte)0);
      throw new InvalidCipherTextException("data wrong");
    } 
    byte[] arrayOfByte4 = new byte[arrayOfByte2.length - j];
    System.arraycopy(arrayOfByte2, j, arrayOfByte4, 0, arrayOfByte4.length);
    return arrayOfByte4;
  }
  
  private void ItoOSP(int paramInt, byte[] paramArrayOfbyte) {
    paramArrayOfbyte[0] = (byte)(paramInt >>> 24);
    paramArrayOfbyte[1] = (byte)(paramInt >>> 16);
    paramArrayOfbyte[2] = (byte)(paramInt >>> 8);
    paramArrayOfbyte[3] = (byte)(paramInt >>> 0);
  }
  
  private byte[] maskGeneratorFunction1(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    byte[] arrayOfByte1 = new byte[paramInt3];
    byte[] arrayOfByte2 = new byte[this.mgf1Hash.getDigestSize()];
    byte[] arrayOfByte3 = new byte[4];
    byte b = 0;
    this.mgf1Hash.reset();
    while (b < paramInt3 / arrayOfByte2.length) {
      ItoOSP(b, arrayOfByte3);
      this.mgf1Hash.update(paramArrayOfbyte, paramInt1, paramInt2);
      this.mgf1Hash.update(arrayOfByte3, 0, arrayOfByte3.length);
      this.mgf1Hash.doFinal(arrayOfByte2, 0);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, b * arrayOfByte2.length, arrayOfByte2.length);
      b++;
    } 
    if (b * arrayOfByte2.length < paramInt3) {
      ItoOSP(b, arrayOfByte3);
      this.mgf1Hash.update(paramArrayOfbyte, paramInt1, paramInt2);
      this.mgf1Hash.update(arrayOfByte3, 0, arrayOfByte3.length);
      this.mgf1Hash.doFinal(arrayOfByte2, 0);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, b * arrayOfByte2.length, arrayOfByte1.length - b * arrayOfByte2.length);
    } 
    return arrayOfByte1;
  }
}
