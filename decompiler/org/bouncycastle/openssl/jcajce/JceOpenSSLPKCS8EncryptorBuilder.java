package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
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
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceOpenSSLPKCS8EncryptorBuilder {
  public static final String AES_128_CBC = NISTObjectIdentifiers.id_aes128_CBC.getId();
  
  public static final String AES_192_CBC = NISTObjectIdentifiers.id_aes192_CBC.getId();
  
  public static final String AES_256_CBC = NISTObjectIdentifiers.id_aes256_CBC.getId();
  
  public static final String DES3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC.getId();
  
  public static final String PBE_SHA1_RC4_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId();
  
  public static final String PBE_SHA1_RC4_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4.getId();
  
  public static final String PBE_SHA1_3DES = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC.getId();
  
  public static final String PBE_SHA1_2DES = PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC.getId();
  
  public static final String PBE_SHA1_RC2_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC.getId();
  
  public static final String PBE_SHA1_RC2_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC.getId();
  
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  private AlgorithmParameters params;
  
  private ASN1ObjectIdentifier algOID;
  
  byte[] salt;
  
  int iterationCount;
  
  private Cipher cipher;
  
  private SecureRandom random;
  
  private AlgorithmParameterGenerator paramGen;
  
  private char[] password;
  
  private SecretKey key;
  
  private AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  public JceOpenSSLPKCS8EncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.algOID = paramASN1ObjectIdentifier;
    this.iterationCount = 2048;
  }
  
  public JceOpenSSLPKCS8EncryptorBuilder setRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public JceOpenSSLPKCS8EncryptorBuilder setPasssword(char[] paramArrayOfchar) {
    this.password = paramArrayOfchar;
    return this;
  }
  
  public JceOpenSSLPKCS8EncryptorBuilder setPRF(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.prf = paramAlgorithmIdentifier;
    return this;
  }
  
  public JceOpenSSLPKCS8EncryptorBuilder setIterationCount(int paramInt) {
    this.iterationCount = paramInt;
    return this;
  }
  
  public JceOpenSSLPKCS8EncryptorBuilder setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public JceOpenSSLPKCS8EncryptorBuilder setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public OutputEncryptor build() throws OperatorCreationException {
    final AlgorithmIdentifier algID;
    if (this.random == null)
      this.random = new SecureRandom(); 
    try {
      this.cipher = this.helper.createCipher(this.algOID.getId());
      if (PEMUtilities.isPKCS5Scheme2(this.algOID))
        this.paramGen = this.helper.createAlgorithmParameterGenerator(this.algOID.getId()); 
    } catch (GeneralSecurityException generalSecurityException) {
      throw new OperatorCreationException(this.algOID + " not available: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
    if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
      this.salt = new byte[PEMUtilities.getSaltSize(this.prf.getAlgorithm())];
      this.random.nextBytes(this.salt);
      this.params = this.paramGen.generateParameters();
      try {
        EncryptionScheme encryptionScheme = new EncryptionScheme(this.algOID, (ASN1Encodable)ASN1Primitive.fromByteArray(this.params.getEncoded()));
        KeyDerivationFunc keyDerivationFunc = new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(this.salt, this.iterationCount, this.prf));
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add((ASN1Encodable)keyDerivationFunc);
        aSN1EncodableVector.add((ASN1Encodable)encryptionScheme);
        algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, (ASN1Encodable)PBES2Parameters.getInstance(new DERSequence(aSN1EncodableVector)));
      } catch (IOException iOException) {
        throw new OperatorCreationException(iOException.getMessage(), iOException);
      } 
      try {
        if (PEMUtilities.isHmacSHA1(this.prf)) {
          this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount);
        } else {
          this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount, this.prf);
        } 
        this.cipher.init(1, this.key, this.params);
      } catch (GeneralSecurityException generalSecurityException) {
        throw new OperatorCreationException(generalSecurityException.getMessage(), generalSecurityException);
      } 
    } else if (PEMUtilities.isPKCS12(this.algOID)) {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      this.salt = new byte[20];
      this.random.nextBytes(this.salt);
      aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.salt));
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.iterationCount));
      algorithmIdentifier = new AlgorithmIdentifier(this.algOID, (ASN1Encodable)PKCS12PBEParams.getInstance(new DERSequence(aSN1EncodableVector)));
      try {
        this.cipher.init(1, (Key)new PKCS12KeyWithParameters(this.password, this.salt, this.iterationCount));
      } catch (GeneralSecurityException generalSecurityException) {
        throw new OperatorCreationException(generalSecurityException.getMessage(), generalSecurityException);
      } 
    } else {
      throw new OperatorCreationException("unknown algorithm: " + this.algOID, null);
    } 
    return new OutputEncryptor() {
        public AlgorithmIdentifier getAlgorithmIdentifier() {
          return algID;
        }
        
        public OutputStream getOutputStream(OutputStream param1OutputStream) {
          return new CipherOutputStream(param1OutputStream, JceOpenSSLPKCS8EncryptorBuilder.this.cipher);
        }
        
        public GenericKey getKey() {
          return (GenericKey)new JceGenericKey(algID, JceOpenSSLPKCS8EncryptorBuilder.this.key);
        }
      };
  }
}
