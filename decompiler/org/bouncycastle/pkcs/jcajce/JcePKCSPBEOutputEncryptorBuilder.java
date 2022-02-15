package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class JcePKCSPBEOutputEncryptorBuilder {
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  private ASN1ObjectIdentifier algorithm;
  
  private ASN1ObjectIdentifier keyEncAlgorithm;
  
  private SecureRandom random;
  
  private SecretKeySizeProvider keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
  
  private int iterationCount = 1024;
  
  private AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  public JcePKCSPBEOutputEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    if (isPKCS12(paramASN1ObjectIdentifier)) {
      this.algorithm = paramASN1ObjectIdentifier;
      this.keyEncAlgorithm = paramASN1ObjectIdentifier;
    } else {
      this.algorithm = PKCSObjectIdentifiers.id_PBES2;
      this.keyEncAlgorithm = paramASN1ObjectIdentifier;
    } 
  }
  
  public JcePKCSPBEOutputEncryptorBuilder setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public JcePKCSPBEOutputEncryptorBuilder setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public JcePKCSPBEOutputEncryptorBuilder setPRF(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.prf = paramAlgorithmIdentifier;
    return this;
  }
  
  public JcePKCSPBEOutputEncryptorBuilder setKeySizeProvider(SecretKeySizeProvider paramSecretKeySizeProvider) {
    this.keySizeProvider = paramSecretKeySizeProvider;
    return this;
  }
  
  public JcePKCSPBEOutputEncryptorBuilder setIterationCount(int paramInt) {
    this.iterationCount = paramInt;
    return this;
  }
  
  public OutputEncryptor build(final char[] password) throws OperatorCreationException {
    if (this.random == null)
      this.random = new SecureRandom(); 
    try {
      final Cipher cipher;
      final AlgorithmIdentifier encryptionAlg;
      if (isPKCS12(this.algorithm)) {
        byte[] arrayOfByte = new byte[20];
        this.random.nextBytes(arrayOfByte);
        cipher = this.helper.createCipher(this.algorithm.getId());
        cipher.init(1, (Key)new PKCS12KeyWithParameters(password, arrayOfByte, this.iterationCount));
        algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)new PKCS12PBEParams(arrayOfByte, this.iterationCount));
      } else if (this.algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
        byte[] arrayOfByte = new byte[JceUtils.getSaltSize(this.prf.getAlgorithm())];
        this.random.nextBytes(arrayOfByte);
        SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory(JceUtils.getAlgorithm(this.prf.getAlgorithm()));
        SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(password, arrayOfByte, this.iterationCount, this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
        cipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
        cipher.init(1, secretKey, this.random);
        PBES2Parameters pBES2Parameters = new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(arrayOfByte, this.iterationCount, this.prf)), new EncryptionScheme(this.keyEncAlgorithm, (ASN1Encodable)ASN1Primitive.fromByteArray(cipher.getParameters().getEncoded())));
        algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)pBES2Parameters);
      } else {
        throw new OperatorCreationException("unrecognised algorithm");
      } 
      return new OutputEncryptor() {
          public AlgorithmIdentifier getAlgorithmIdentifier() {
            return encryptionAlg;
          }
          
          public OutputStream getOutputStream(OutputStream param1OutputStream) {
            return new CipherOutputStream(param1OutputStream, cipher);
          }
          
          public GenericKey getKey() {
            return JcePKCSPBEOutputEncryptorBuilder.this.isPKCS12(encryptionAlg.getAlgorithm()) ? new GenericKey(encryptionAlg, JcePKCSPBEOutputEncryptorBuilder.PKCS12PasswordToBytes(password)) : new GenericKey(encryptionAlg, JcePKCSPBEOutputEncryptorBuilder.PKCS5PasswordToBytes(password));
          }
        };
    } catch (Exception exception) {
      throw new OperatorCreationException("unable to create OutputEncryptor: " + exception.getMessage(), exception);
    } 
  }
  
  private boolean isPKCS12(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (paramASN1ObjectIdentifier.on(PKCSObjectIdentifiers.pkcs_12PbeIds) || paramASN1ObjectIdentifier.on(BCObjectIdentifiers.bc_pbe_sha1_pkcs12) || paramASN1ObjectIdentifier.on(BCObjectIdentifiers.bc_pbe_sha256_pkcs12));
  }
  
  private static byte[] PKCS5PasswordToBytes(char[] paramArrayOfchar) {
    if (paramArrayOfchar != null) {
      byte[] arrayOfByte = new byte[paramArrayOfchar.length];
      for (byte b = 0; b != arrayOfByte.length; b++)
        arrayOfByte[b] = (byte)paramArrayOfchar[b]; 
      return arrayOfByte;
    } 
    return new byte[0];
  }
  
  private static byte[] PKCS12PasswordToBytes(char[] paramArrayOfchar) {
    if (paramArrayOfchar != null && paramArrayOfchar.length > 0) {
      byte[] arrayOfByte = new byte[(paramArrayOfchar.length + 1) * 2];
      for (byte b = 0; b != paramArrayOfchar.length; b++) {
        arrayOfByte[b * 2] = (byte)(paramArrayOfchar[b] >>> 8);
        arrayOfByte[b * 2 + 1] = (byte)paramArrayOfchar[b];
      } 
      return arrayOfByte;
    } 
    return new byte[0];
  }
}
