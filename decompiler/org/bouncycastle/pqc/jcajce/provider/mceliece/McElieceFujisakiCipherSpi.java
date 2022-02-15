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
import org.bouncycastle.pqc.crypto.mceliece.McElieceFujisakiCipher;
import org.bouncycastle.pqc.jcajce.provider.util.AsymmetricHybridCipher;

public class McElieceFujisakiCipherSpi extends AsymmetricHybridCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
  private Digest digest;
  
  private McElieceFujisakiCipher cipher;
  
  private ByteArrayOutputStream buf;
  
  protected McElieceFujisakiCipherSpi(Digest paramDigest, McElieceFujisakiCipher paramMcElieceFujisakiCipher) {
    this.digest = paramDigest;
    this.cipher = paramMcElieceFujisakiCipher;
    this.buf = new ByteArrayOutputStream();
  }
  
  public byte[] update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.buf.write(paramArrayOfbyte, paramInt1, paramInt2);
    return new byte[0];
  }
  
  public byte[] doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws BadPaddingException {
    update(paramArrayOfbyte, paramInt1, paramInt2);
    byte[] arrayOfByte = this.buf.toByteArray();
    this.buf.reset();
    if (this.opMode == 1)
      return this.cipher.messageEncrypt(arrayOfByte); 
    if (this.opMode == 2)
      try {
        return this.cipher.messageDecrypt(arrayOfByte);
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
    AsymmetricKeyParameter asymmetricKeyParameter = McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)paramKey);
    ParametersWithRandom parametersWithRandom = new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, paramSecureRandom);
    this.digest.reset();
    this.cipher.init(true, (CipherParameters)parametersWithRandom);
  }
  
  protected void initCipherDecrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    AsymmetricKeyParameter asymmetricKeyParameter = McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)paramKey);
    this.digest.reset();
    this.cipher.init(false, (CipherParameters)asymmetricKeyParameter);
  }
  
  public String getName() {
    return "McElieceFujisakiCipher";
  }
  
  public int getKeySize(Key paramKey) throws InvalidKeyException {
    McElieceCCA2KeyParameters mcElieceCCA2KeyParameters;
    if (paramKey instanceof PublicKey) {
      mcElieceCCA2KeyParameters = (McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)paramKey);
    } else {
      mcElieceCCA2KeyParameters = (McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)paramKey);
    } 
    return this.cipher.getKeySize(mcElieceCCA2KeyParameters);
  }
  
  public static class McElieceFujisaki extends McElieceFujisakiCipherSpi {
    public McElieceFujisaki() {
      super(DigestFactory.createSHA1(), new McElieceFujisakiCipher());
    }
  }
}
