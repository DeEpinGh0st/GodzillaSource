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

public class McEliecePointchevalCipher implements MessageEncryptor {
  public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2.2";
  
  private Digest messDigest;
  
  private SecureRandom sr;
  
  private int n;
  
  private int k;
  
  private int t;
  
  McElieceCCA2KeyParameters key;
  
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
  
  public int getKeySize(McElieceCCA2KeyParameters paramMcElieceCCA2KeyParameters) throws IllegalArgumentException {
    if (paramMcElieceCCA2KeyParameters instanceof McElieceCCA2PublicKeyParameters)
      return ((McElieceCCA2PublicKeyParameters)paramMcElieceCCA2KeyParameters).getN(); 
    if (paramMcElieceCCA2KeyParameters instanceof McElieceCCA2PrivateKeyParameters)
      return ((McElieceCCA2PrivateKeyParameters)paramMcElieceCCA2KeyParameters).getN(); 
    throw new IllegalArgumentException("unsupported type");
  }
  
  protected int decryptOutputSize(int paramInt) {
    return 0;
  }
  
  protected int encryptOutputSize(int paramInt) {
    return 0;
  }
  
  private void initCipherEncrypt(McElieceCCA2PublicKeyParameters paramMcElieceCCA2PublicKeyParameters) {
    this.sr = (this.sr != null) ? this.sr : new SecureRandom();
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
    int i = this.k >> 3;
    byte[] arrayOfByte1 = new byte[i];
    this.sr.nextBytes(arrayOfByte1);
    GF2Vector gF2Vector1 = new GF2Vector(this.k, this.sr);
    byte[] arrayOfByte2 = gF2Vector1.getEncoded();
    byte[] arrayOfByte3 = ByteUtils.concatenate(paramArrayOfbyte, arrayOfByte1);
    this.messDigest.update(arrayOfByte3, 0, arrayOfByte3.length);
    byte[] arrayOfByte4 = new byte[this.messDigest.getDigestSize()];
    this.messDigest.doFinal(arrayOfByte4, 0);
    GF2Vector gF2Vector2 = Conversions.encode(this.n, this.t, arrayOfByte4);
    byte[] arrayOfByte5 = McElieceCCA2Primitives.encryptionPrimitive((McElieceCCA2PublicKeyParameters)this.key, gF2Vector1, gF2Vector2).getEncoded();
    DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator((Digest)new SHA1Digest());
    digestRandomGenerator.addSeedMaterial(arrayOfByte2);
    byte[] arrayOfByte6 = new byte[paramArrayOfbyte.length + i];
    digestRandomGenerator.nextBytes(arrayOfByte6);
    byte b;
    for (b = 0; b < paramArrayOfbyte.length; b++)
      arrayOfByte6[b] = (byte)(arrayOfByte6[b] ^ paramArrayOfbyte[b]); 
    for (b = 0; b < i; b++)
      arrayOfByte6[paramArrayOfbyte.length + b] = (byte)(arrayOfByte6[paramArrayOfbyte.length + b] ^ arrayOfByte1[b]); 
    return ByteUtils.concatenate(arrayOfByte5, arrayOfByte6);
  }
  
  public byte[] messageDecrypt(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    if (this.forEncryption)
      throw new IllegalStateException("cipher initialised for decryption"); 
    int i = this.n + 7 >> 3;
    int j = paramArrayOfbyte.length - i;
    byte[][] arrayOfByte1 = ByteUtils.split(paramArrayOfbyte, i);
    byte[] arrayOfByte2 = arrayOfByte1[0];
    byte[] arrayOfByte3 = arrayOfByte1[1];
    GF2Vector gF2Vector1 = GF2Vector.OS2VP(this.n, arrayOfByte2);
    GF2Vector[] arrayOfGF2Vector = McElieceCCA2Primitives.decryptionPrimitive((McElieceCCA2PrivateKeyParameters)this.key, gF2Vector1);
    byte[] arrayOfByte4 = arrayOfGF2Vector[0].getEncoded();
    GF2Vector gF2Vector2 = arrayOfGF2Vector[1];
    DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator((Digest)new SHA1Digest());
    digestRandomGenerator.addSeedMaterial(arrayOfByte4);
    byte[] arrayOfByte5 = new byte[j];
    digestRandomGenerator.nextBytes(arrayOfByte5);
    for (byte b = 0; b < j; b++)
      arrayOfByte5[b] = (byte)(arrayOfByte5[b] ^ arrayOfByte3[b]); 
    this.messDigest.update(arrayOfByte5, 0, arrayOfByte5.length);
    byte[] arrayOfByte6 = new byte[this.messDigest.getDigestSize()];
    this.messDigest.doFinal(arrayOfByte6, 0);
    gF2Vector1 = Conversions.encode(this.n, this.t, arrayOfByte6);
    if (!gF2Vector1.equals(gF2Vector2))
      throw new InvalidCipherTextException("Bad Padding: Invalid ciphertext."); 
    int k = this.k >> 3;
    byte[][] arrayOfByte7 = ByteUtils.split(arrayOfByte5, j - k);
    return arrayOfByte7[0];
  }
}
