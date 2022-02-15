package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKobaraImaiCipher;
import org.bouncycastle.pqc.jcajce.provider.util.AsymmetricHybridCipher;

public class McElieceKobaraImaiCipherSpi extends AsymmetricHybridCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
  private Digest digest;
  
  private McElieceKobaraImaiCipher cipher;
  
  private ByteArrayOutputStream buf = new ByteArrayOutputStream();
  
  public McElieceKobaraImaiCipherSpi() {
    this.buf = new ByteArrayOutputStream();
  }
  
  protected McElieceKobaraImaiCipherSpi(Digest paramDigest, McElieceKobaraImaiCipher paramMcElieceKobaraImaiCipher) {
    this.digest = paramDigest;
    this.cipher = paramMcElieceKobaraImaiCipher;
    this.buf = new ByteArrayOutputStream();
  }
  
  public byte[] update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.buf.write(paramArrayOfbyte, paramInt1, paramInt2);
    return new byte[0];
  }
  
  public byte[] doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws BadPaddingException {
    update(paramArrayOfbyte, paramInt1, paramInt2);
    if (this.opMode == 1)
      return this.cipher.messageEncrypt(pad()); 
    if (this.opMode == 2)
      try {
        byte[] arrayOfByte = this.buf.toByteArray();
        this.buf.reset();
        return unpad(this.cipher.messageDecrypt(arrayOfByte));
      } catch (InvalidCipherTextException invalidCipherTextException) {
        throw new BadPaddingException(invalidCipherTextException.getMessage());
      }  
    throw new IllegalStateException("unknown mode in doFinal");
  }
  
  protected int encryptOutputSize(int paramInt) {
    return 0;
  }
  
  protected int decryptOutputSize(int paramInt) {
    return 0;
  }
  
  protected void initCipherEncrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.buf.reset();
    AsymmetricKeyParameter asymmetricKeyParameter = McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)paramKey);
    ParametersWithRandom parametersWithRandom = new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, paramSecureRandom);
    this.digest.reset();
    this.cipher.init(true, (CipherParameters)parametersWithRandom);
  }
  
  protected void initCipherDecrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.buf.reset();
    AsymmetricKeyParameter asymmetricKeyParameter = McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)paramKey);
    this.digest.reset();
    this.cipher.init(false, (CipherParameters)asymmetricKeyParameter);
  }
  
  public String getName() {
    return "McElieceKobaraImaiCipher";
  }
  
  public int getKeySize(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof PublicKey) {
      McElieceCCA2KeyParameters mcElieceCCA2KeyParameters = (McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)paramKey);
      return this.cipher.getKeySize(mcElieceCCA2KeyParameters);
    } 
    if (paramKey instanceof PrivateKey) {
      McElieceCCA2KeyParameters mcElieceCCA2KeyParameters = (McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)paramKey);
      return this.cipher.getKeySize(mcElieceCCA2KeyParameters);
    } 
    throw new InvalidKeyException();
  }
  
  private byte[] pad() {
    this.buf.write(1);
    byte[] arrayOfByte = this.buf.toByteArray();
    this.buf.reset();
    return arrayOfByte;
  }
  
  private byte[] unpad(byte[] paramArrayOfbyte) throws BadPaddingException {
    int i;
    for (i = paramArrayOfbyte.length - 1; i >= 0 && paramArrayOfbyte[i] == 0; i--);
    if (paramArrayOfbyte[i] != 1)
      throw new BadPaddingException("invalid ciphertext"); 
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public static class McElieceKobaraImai extends McElieceKobaraImaiCipherSpi {
    public McElieceKobaraImai() {
      super(DigestFactory.createSHA1(), new McElieceKobaraImaiCipher());
    }
  }
  
  public static class McElieceKobaraImai224 extends McElieceKobaraImaiCipherSpi {
    public McElieceKobaraImai224() {
      super(DigestFactory.createSHA224(), new McElieceKobaraImaiCipher());
    }
  }
  
  public static class McElieceKobaraImai256 extends McElieceKobaraImaiCipherSpi {
    public McElieceKobaraImai256() {
      super(DigestFactory.createSHA256(), new McElieceKobaraImaiCipher());
    }
  }
  
  public static class McElieceKobaraImai384 extends McElieceKobaraImaiCipherSpi {
    public McElieceKobaraImai384() {
      super(DigestFactory.createSHA384(), new McElieceKobaraImaiCipher());
    }
  }
  
  public static class McElieceKobaraImai512 extends McElieceKobaraImaiCipherSpi {
    public McElieceKobaraImai512() {
      super(DigestFactory.createSHA512(), new McElieceKobaraImaiCipher());
    }
  }
}
