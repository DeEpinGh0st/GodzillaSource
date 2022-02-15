package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.pqc.crypto.MessageEncryptor;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import org.bouncycastle.pqc.math.linearalgebra.IntegerFunctions;

public class McElieceKobaraImaiCipher implements MessageEncryptor {
  public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2.3";
  
  private static final String DEFAULT_PRNG_NAME = "SHA1PRNG";
  
  public static final byte[] PUBLIC_CONSTANT = "a predetermined public constant".getBytes();
  
  private Digest messDigest;
  
  private SecureRandom sr;
  
  McElieceCCA2KeyParameters key;
  
  private int n;
  
  private int k;
  
  private int t;
  
  private boolean forEncryption;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.forEncryption = paramBoolean;
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.sr = parametersWithRandom.getRandom();
        this.key = (McElieceCCA2PublicKeyParameters)parametersWithRandom.getParameters();
        initCipherEncrypt((McElieceCCA2PublicKeyParameters)this.key);
      } else {
        this.sr = new SecureRandom();
        this.key = (McElieceCCA2PublicKeyParameters)paramCipherParameters;
        initCipherEncrypt((McElieceCCA2PublicKeyParameters)this.key);
      } 
    } else {
      this.key = (McElieceCCA2PrivateKeyParameters)paramCipherParameters;
      initCipherDecrypt((McElieceCCA2PrivateKeyParameters)this.key);
    } 
  }
  
  public int getKeySize(McElieceCCA2KeyParameters paramMcElieceCCA2KeyParameters) {
    if (paramMcElieceCCA2KeyParameters instanceof McElieceCCA2PublicKeyParameters)
      return ((McElieceCCA2PublicKeyParameters)paramMcElieceCCA2KeyParameters).getN(); 
    if (paramMcElieceCCA2KeyParameters instanceof McElieceCCA2PrivateKeyParameters)
      return ((McElieceCCA2PrivateKeyParameters)paramMcElieceCCA2KeyParameters).getN(); 
    throw new IllegalArgumentException("unsupported type");
  }
  
  private void initCipherEncrypt(McElieceCCA2PublicKeyParameters paramMcElieceCCA2PublicKeyParameters) {
    this.messDigest = Utils.getDigest(paramMcElieceCCA2PublicKeyParameters.getDigest());
    this.n = paramMcElieceCCA2PublicKeyParameters.getN();
    this.k = paramMcElieceCCA2PublicKeyParameters.getK();
    this.t = paramMcElieceCCA2PublicKeyParameters.getT();
  }
  
  private void initCipherDecrypt(McElieceCCA2PrivateKeyParameters paramMcElieceCCA2PrivateKeyParameters) {
    this.messDigest = Utils.getDigest(paramMcElieceCCA2PrivateKeyParameters.getDigest());
    this.n = paramMcElieceCCA2PrivateKeyParameters.getN();
    this.k = paramMcElieceCCA2PrivateKeyParameters.getK();
    this.t = paramMcElieceCCA2PrivateKeyParameters.getT();
  }
  
  public byte[] messageEncrypt(byte[] paramArrayOfbyte) {
    if (!this.forEncryption)
      throw new IllegalStateException("cipher initialised for decryption"); 
    int i = this.messDigest.getDigestSize();
    int j = this.k >> 3;
    int k = IntegerFunctions.binomial(this.n, this.t).bitLength() - 1 >> 3;
    int m = j + k - i - PUBLIC_CONSTANT.length;
    if (paramArrayOfbyte.length > m)
      m = paramArrayOfbyte.length; 
    int n = m + PUBLIC_CONSTANT.length;
    int i1 = n + i - j - k;
    byte[] arrayOfByte1 = new byte[n];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, paramArrayOfbyte.length);
    System.arraycopy(PUBLIC_CONSTANT, 0, arrayOfByte1, m, PUBLIC_CONSTANT.length);
    byte[] arrayOfByte2 = new byte[i];
    this.sr.nextBytes(arrayOfByte2);
    DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator((Digest)new SHA1Digest());
    digestRandomGenerator.addSeedMaterial(arrayOfByte2);
    byte[] arrayOfByte3 = new byte[n];
    digestRandomGenerator.nextBytes(arrayOfByte3);
    for (int i2 = n - 1; i2 >= 0; i2--)
      arrayOfByte3[i2] = (byte)(arrayOfByte3[i2] ^ arrayOfByte1[i2]); 
    byte[] arrayOfByte4 = new byte[this.messDigest.getDigestSize()];
    this.messDigest.update(arrayOfByte3, 0, arrayOfByte3.length);
    this.messDigest.doFinal(arrayOfByte4, 0);
    for (int i3 = i - 1; i3 >= 0; i3--)
      arrayOfByte4[i3] = (byte)(arrayOfByte4[i3] ^ arrayOfByte2[i3]); 
    byte[] arrayOfByte5 = ByteUtils.concatenate(arrayOfByte4, arrayOfByte3);
    byte[] arrayOfByte6 = new byte[0];
    if (i1 > 0) {
      arrayOfByte6 = new byte[i1];
      System.arraycopy(arrayOfByte5, 0, arrayOfByte6, 0, i1);
    } 
    byte[] arrayOfByte7 = new byte[k];
    System.arraycopy(arrayOfByte5, i1, arrayOfByte7, 0, k);
    byte[] arrayOfByte8 = new byte[j];
    System.arraycopy(arrayOfByte5, i1 + k, arrayOfByte8, 0, j);
    GF2Vector gF2Vector1 = GF2Vector.OS2VP(this.k, arrayOfByte8);
    GF2Vector gF2Vector2 = Conversions.encode(this.n, this.t, arrayOfByte7);
    byte[] arrayOfByte9 = McElieceCCA2Primitives.encryptionPrimitive((McElieceCCA2PublicKeyParameters)this.key, gF2Vector1, gF2Vector2).getEncoded();
    return (i1 > 0) ? ByteUtils.concatenate(arrayOfByte6, arrayOfByte9) : arrayOfByte9;
  }
  
  public byte[] messageDecrypt(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    byte[] arrayOfByte1;
    byte[] arrayOfByte2;
    if (this.forEncryption)
      throw new IllegalStateException("cipher initialised for decryption"); 
    int i = this.n >> 3;
    if (paramArrayOfbyte.length < i)
      throw new InvalidCipherTextException("Bad Padding: Ciphertext too short."); 
    int j = this.messDigest.getDigestSize();
    int k = this.k >> 3;
    int m = paramArrayOfbyte.length - i;
    if (m > 0) {
      byte[][] arrayOfByte = ByteUtils.split(paramArrayOfbyte, m);
      arrayOfByte1 = arrayOfByte[0];
      arrayOfByte2 = arrayOfByte[1];
    } else {
      arrayOfByte1 = new byte[0];
      arrayOfByte2 = paramArrayOfbyte;
    } 
    GF2Vector gF2Vector1 = GF2Vector.OS2VP(this.n, arrayOfByte2);
    GF2Vector[] arrayOfGF2Vector = McElieceCCA2Primitives.decryptionPrimitive((McElieceCCA2PrivateKeyParameters)this.key, gF2Vector1);
    byte[] arrayOfByte3 = arrayOfGF2Vector[0].getEncoded();
    GF2Vector gF2Vector2 = arrayOfGF2Vector[1];
    if (arrayOfByte3.length > k)
      arrayOfByte3 = ByteUtils.subArray(arrayOfByte3, 0, k); 
    byte[] arrayOfByte4 = Conversions.decode(this.n, this.t, gF2Vector2);
    byte[] arrayOfByte5 = ByteUtils.concatenate(arrayOfByte1, arrayOfByte4);
    arrayOfByte5 = ByteUtils.concatenate(arrayOfByte5, arrayOfByte3);
    int n = arrayOfByte5.length - j;
    byte[][] arrayOfByte6 = ByteUtils.split(arrayOfByte5, j);
    byte[] arrayOfByte7 = arrayOfByte6[0];
    byte[] arrayOfByte8 = arrayOfByte6[1];
    byte[] arrayOfByte9 = new byte[this.messDigest.getDigestSize()];
    this.messDigest.update(arrayOfByte8, 0, arrayOfByte8.length);
    this.messDigest.doFinal(arrayOfByte9, 0);
    for (int i1 = j - 1; i1 >= 0; i1--)
      arrayOfByte9[i1] = (byte)(arrayOfByte9[i1] ^ arrayOfByte7[i1]); 
    DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator((Digest)new SHA1Digest());
    digestRandomGenerator.addSeedMaterial(arrayOfByte9);
    byte[] arrayOfByte10 = new byte[n];
    digestRandomGenerator.nextBytes(arrayOfByte10);
    for (int i2 = n - 1; i2 >= 0; i2--)
      arrayOfByte10[i2] = (byte)(arrayOfByte10[i2] ^ arrayOfByte8[i2]); 
    if (arrayOfByte10.length < n)
      throw new InvalidCipherTextException("Bad Padding: invalid ciphertext"); 
    byte[][] arrayOfByte11 = ByteUtils.split(arrayOfByte10, n - PUBLIC_CONSTANT.length);
    byte[] arrayOfByte12 = arrayOfByte11[0];
    byte[] arrayOfByte13 = arrayOfByte11[1];
    if (!ByteUtils.equals(arrayOfByte13, PUBLIC_CONSTANT))
      throw new InvalidCipherTextException("Bad Padding: invalid ciphertext"); 
    return arrayOfByte12;
  }
}
