package org.bouncycastle.cms.jcajce;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;

public class EnvelopedDataHelper {
  protected static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
  
  protected static final Map BASE_CIPHER_NAMES = new HashMap<Object, Object>();
  
  protected static final Map CIPHER_ALG_NAMES = new HashMap<Object, Object>();
  
  protected static final Map MAC_ALG_NAMES = new HashMap<Object, Object>();
  
  private static final Map PBKDF2_ALG_NAMES = new HashMap<Object, Object>();
  
  private static final short[] rc2Table = new short[] { 
      189, 86, 234, 242, 162, 241, 172, 42, 176, 147, 
      209, 156, 27, 51, 253, 208, 48, 4, 182, 220, 
      125, 223, 50, 75, 247, 203, 69, 155, 49, 187, 
      33, 90, 65, 159, 225, 217, 74, 77, 158, 218, 
      160, 104, 44, 195, 39, 95, 128, 54, 62, 238, 
      251, 149, 26, 254, 206, 168, 52, 169, 19, 240, 
      166, 63, 216, 12, 120, 36, 175, 35, 82, 193, 
      103, 23, 245, 102, 144, 231, 232, 7, 184, 96, 
      72, 230, 30, 83, 243, 146, 164, 114, 140, 8, 
      21, 110, 134, 0, 132, 250, 244, 127, 138, 66, 
      25, 246, 219, 205, 20, 141, 80, 18, 186, 60, 
      6, 78, 236, 179, 53, 17, 161, 136, 142, 43, 
      148, 153, 183, 113, 116, 211, 228, 191, 58, 222, 
      150, 14, 188, 10, 237, 119, 252, 55, 107, 3, 
      121, 137, 98, 198, 215, 192, 210, 124, 106, 139, 
      34, 163, 91, 5, 93, 2, 117, 213, 97, 227, 
      24, 143, 85, 81, 173, 31, 11, 94, 133, 229, 
      194, 87, 99, 202, 61, 108, 180, 197, 204, 112, 
      178, 145, 89, 13, 71, 32, 200, 79, 88, 224, 
      1, 226, 22, 56, 196, 111, 59, 15, 101, 70, 
      190, 126, 45, 123, 130, 249, 64, 181, 29, 115, 
      248, 235, 38, 199, 135, 151, 37, 84, 177, 40, 
      170, 152, 157, 165, 100, 109, 122, 212, 16, 129, 
      68, 239, 73, 214, 174, 46, 221, 118, 92, 47, 
      167, 28, 201, 9, 105, 154, 131, 207, 41, 57, 
      185, 233, 76, 255, 67, 171 };
  
  private static final short[] rc2Ekb = new short[] { 
      93, 190, 155, 139, 17, 153, 110, 77, 89, 243, 
      133, 166, 63, 183, 131, 197, 228, 115, 107, 58, 
      104, 90, 192, 71, 160, 100, 52, 12, 241, 208, 
      82, 165, 185, 30, 150, 67, 65, 216, 212, 44, 
      219, 248, 7, 119, 42, 202, 235, 239, 16, 28, 
      22, 13, 56, 114, 47, 137, 193, 249, 128, 196, 
      109, 174, 48, 61, 206, 32, 99, 254, 230, 26, 
      199, 184, 80, 232, 36, 23, 252, 37, 111, 187, 
      106, 163, 68, 83, 217, 162, 1, 171, 188, 182, 
      31, 152, 238, 154, 167, 45, 79, 158, 142, 172, 
      224, 198, 73, 70, 41, 244, 148, 138, 175, 225, 
      91, 195, 179, 123, 87, 209, 124, 156, 237, 135, 
      64, 140, 226, 203, 147, 20, 201, 97, 46, 229, 
      204, 246, 94, 168, 92, 214, 117, 141, 98, 149, 
      88, 105, 118, 161, 74, 181, 85, 9, 120, 51, 
      130, 215, 221, 121, 245, 27, 11, 222, 38, 33, 
      40, 116, 4, 151, 86, 223, 60, 240, 55, 57, 
      220, 255, 6, 164, 234, 66, 8, 218, 180, 113, 
      176, 207, 18, 122, 78, 250, 108, 29, 132, 0, 
      200, 127, 145, 69, 170, 43, 194, 177, 143, 213, 
      186, 242, 173, 25, 178, 103, 54, 247, 15, 10, 
      146, 125, 227, 157, 233, 144, 62, 35, 39, 102, 
      19, 236, 129, 21, 189, 34, 191, 159, 126, 169, 
      81, 75, 76, 251, 2, 211, 112, 134, 49, 231, 
      59, 5, 3, 84, 96, 72, 101, 24, 210, 205, 
      95, 50, 136, 14, 53, 253 };
  
  private JcaJceExtHelper helper;
  
  EnvelopedDataHelper(JcaJceExtHelper paramJcaJceExtHelper) {
    this.helper = paramJcaJceExtHelper;
  }
  
  String getBaseCipherName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
    return (str == null) ? paramASN1ObjectIdentifier.getId() : str;
  }
  
  Key getJceKey(GenericKey paramGenericKey) {
    if (paramGenericKey.getRepresentation() instanceof Key)
      return (Key)paramGenericKey.getRepresentation(); 
    if (paramGenericKey.getRepresentation() instanceof byte[])
      return new SecretKeySpec((byte[])paramGenericKey.getRepresentation(), "ENC"); 
    throw new IllegalArgumentException("unknown generic key type");
  }
  
  public Key getJceKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, GenericKey paramGenericKey) {
    if (paramGenericKey.getRepresentation() instanceof Key)
      return (Key)paramGenericKey.getRepresentation(); 
    if (paramGenericKey.getRepresentation() instanceof byte[])
      return new SecretKeySpec((byte[])paramGenericKey.getRepresentation(), getBaseCipherName(paramASN1ObjectIdentifier)); 
    throw new IllegalArgumentException("unknown generic key type");
  }
  
  public void keySizeCheck(AlgorithmIdentifier paramAlgorithmIdentifier, Key paramKey) throws CMSException {
    int i = KEY_SIZE_PROVIDER.getKeySize(paramAlgorithmIdentifier);
    if (i > 0) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = paramKey.getEncoded();
      } catch (Exception exception) {}
      if (arrayOfByte != null && arrayOfByte.length * 8 != i)
        throw new CMSException("Expected key size for algorithm OID not found in recipient."); 
    } 
  }
  
  Cipher createCipher(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    try {
      String str = (String)CIPHER_ALG_NAMES.get(paramASN1ObjectIdentifier);
      if (str != null)
        try {
          return this.helper.createCipher(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
      return this.helper.createCipher(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  Mac createMac(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    try {
      String str = (String)MAC_ALG_NAMES.get(paramASN1ObjectIdentifier);
      if (str != null)
        try {
          return this.helper.createMac(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
      return this.helper.createMac(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot create mac: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  Cipher createRFC3211Wrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
    if (str == null)
      throw new CMSException("no name for " + paramASN1ObjectIdentifier); 
    str = str + "RFC3211Wrap";
    try {
      return this.helper.createCipher(str);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  KeyAgreement createKeyAgreement(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    try {
      String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
      if (str != null)
        try {
          return this.helper.createKeyAgreement(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
      return this.helper.createKeyAgreement(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot create key agreement: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  AlgorithmParameterGenerator createAlgorithmParameterGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws GeneralSecurityException {
    String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
    if (str != null)
      try {
        return this.helper.createAlgorithmParameterGenerator(str);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
    return this.helper.createAlgorithmParameterGenerator(paramASN1ObjectIdentifier.getId());
  }
  
  public Cipher createContentCipher(final Key sKey, final AlgorithmIdentifier encryptionAlgID) throws CMSException {
    return (Cipher)execute(new JCECallback() {
          public Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            Cipher cipher = EnvelopedDataHelper.this.createCipher(encryptionAlgID.getAlgorithm());
            ASN1Encodable aSN1Encodable = encryptionAlgID.getParameters();
            String str = encryptionAlgID.getAlgorithm().getId();
            if (aSN1Encodable != null && !(aSN1Encodable instanceof org.bouncycastle.asn1.ASN1Null)) {
              try {
                AlgorithmParameters algorithmParameters = EnvelopedDataHelper.this.createAlgorithmParameters(encryptionAlgID.getAlgorithm());
                CMSUtils.loadParameters(algorithmParameters, aSN1Encodable);
                cipher.init(2, sKey, algorithmParameters);
              } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                if (str.equals(CMSAlgorithm.DES_CBC.getId()) || str.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) || str.equals("1.3.6.1.4.1.188.7.1.1.2") || str.equals(CMSEnvelopedDataGenerator.AES128_CBC) || str.equals(CMSEnvelopedDataGenerator.AES192_CBC) || str.equals(CMSEnvelopedDataGenerator.AES256_CBC)) {
                  cipher.init(2, sKey, new IvParameterSpec(ASN1OctetString.getInstance(aSN1Encodable).getOctets()));
                } else {
                  throw noSuchAlgorithmException;
                } 
              } 
            } else if (str.equals(CMSAlgorithm.DES_CBC.getId()) || str.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) || str.equals("1.3.6.1.4.1.188.7.1.1.2") || str.equals("1.2.840.113533.7.66.10")) {
              cipher.init(2, sKey, new IvParameterSpec(new byte[8]));
            } else {
              cipher.init(2, sKey);
            } 
            return cipher;
          }
        });
  }
  
  Mac createContentMac(final Key sKey, final AlgorithmIdentifier macAlgId) throws CMSException {
    return (Mac)execute(new JCECallback() {
          public Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            Mac mac = EnvelopedDataHelper.this.createMac(macAlgId.getAlgorithm());
            ASN1Encodable aSN1Encodable = macAlgId.getParameters();
            String str = macAlgId.getAlgorithm().getId();
            if (aSN1Encodable != null && !(aSN1Encodable instanceof org.bouncycastle.asn1.ASN1Null)) {
              try {
                AlgorithmParameters algorithmParameters = EnvelopedDataHelper.this.createAlgorithmParameters(macAlgId.getAlgorithm());
                CMSUtils.loadParameters(algorithmParameters, aSN1Encodable);
                mac.init(sKey, algorithmParameters.getParameterSpec(AlgorithmParameterSpec.class));
              } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                throw noSuchAlgorithmException;
              } 
            } else {
              mac.init(sKey);
            } 
            return mac;
          }
        });
  }
  
  AlgorithmParameters createAlgorithmParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
    String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
    if (str != null)
      try {
        return this.helper.createAlgorithmParameters(str);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
    return this.helper.createAlgorithmParameters(paramASN1ObjectIdentifier.getId());
  }
  
  KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    try {
      String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
      if (str != null)
        try {
          return this.helper.createKeyPairGenerator(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
      return this.helper.createKeyPairGenerator(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot create key pair generator: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  public KeyGenerator createKeyGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    try {
      String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
      if (str != null)
        try {
          return this.helper.createKeyGenerator(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
      return this.helper.createKeyGenerator(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot create key generator: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  AlgorithmParameters generateParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier, SecretKey paramSecretKey, SecureRandom paramSecureRandom) throws CMSException {
    try {
      AlgorithmParameterGenerator algorithmParameterGenerator = createAlgorithmParameterGenerator(paramASN1ObjectIdentifier);
      if (paramASN1ObjectIdentifier.equals(CMSAlgorithm.RC2_CBC)) {
        byte[] arrayOfByte = new byte[8];
        paramSecureRandom.nextBytes(arrayOfByte);
        try {
          algorithmParameterGenerator.init(new RC2ParameterSpec((paramSecretKey.getEncoded()).length * 8, arrayOfByte), paramSecureRandom);
        } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
          throw new CMSException("parameters generation error: " + invalidAlgorithmParameterException, invalidAlgorithmParameterException);
        } 
      } 
      return algorithmParameterGenerator.generateParameters();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return null;
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("exception creating algorithm parameter generator: " + generalSecurityException, generalSecurityException);
    } 
  }
  
  AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier, AlgorithmParameters paramAlgorithmParameters) throws CMSException {
    DERNull dERNull;
    if (paramAlgorithmParameters != null) {
      ASN1Encodable aSN1Encodable = CMSUtils.extractParameters(paramAlgorithmParameters);
    } else {
      dERNull = DERNull.INSTANCE;
    } 
    return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)dERNull);
  }
  
  static Object execute(JCECallback paramJCECallback) throws CMSException {
    try {
      return paramJCECallback.doInJCE();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CMSException("can't find algorithm.", noSuchAlgorithmException);
    } catch (InvalidKeyException invalidKeyException) {
      throw new CMSException("key invalid in message.", invalidKeyException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new CMSException("can't find provider.", noSuchProviderException);
    } catch (NoSuchPaddingException noSuchPaddingException) {
      throw new CMSException("required padding not supported.", noSuchPaddingException);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new CMSException("algorithm parameters invalid.", invalidAlgorithmParameterException);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new CMSException("MAC algorithm parameter spec invalid.", invalidParameterSpecException);
    } 
  }
  
  public KeyFactory createKeyFactory(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    try {
      String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
      if (str != null)
        try {
          return this.helper.createKeyFactory(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
      return this.helper.createKeyFactory(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot create key factory: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey) {
    return this.helper.createAsymmetricUnwrapper(paramAlgorithmIdentifier, paramPrivateKey);
  }
  
  public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return this.helper.createAsymmetricUnwrapper(paramAlgorithmIdentifier, paramPrivateKey, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, SecretKey paramSecretKey) {
    return this.helper.createSymmetricUnwrapper(paramAlgorithmIdentifier, paramSecretKey);
  }
  
  public AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier, AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    if (paramAlgorithmParameterSpec instanceof IvParameterSpec)
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)new DEROctetString(((IvParameterSpec)paramAlgorithmParameterSpec).getIV())); 
    if (paramAlgorithmParameterSpec instanceof RC2ParameterSpec) {
      RC2ParameterSpec rC2ParameterSpec = (RC2ParameterSpec)paramAlgorithmParameterSpec;
      int i = ((RC2ParameterSpec)paramAlgorithmParameterSpec).getEffectiveKeyBits();
      if (i != -1) {
        int j;
        if (i < 256) {
          j = rc2Table[i];
        } else {
          j = i;
        } 
        return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)new RC2CBCParameter(j, rC2ParameterSpec.getIV()));
      } 
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)new RC2CBCParameter(rC2ParameterSpec.getIV()));
    } 
    throw new IllegalStateException("unknown parameter spec: " + paramAlgorithmParameterSpec);
  }
  
  SecretKeyFactory createSecretKeyFactory(String paramString) throws NoSuchProviderException, NoSuchAlgorithmException {
    return this.helper.createSecretKeyFactory(paramString);
  }
  
  byte[] calculateDerivedKey(int paramInt1, char[] paramArrayOfchar, AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt2) throws CMSException {
    PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(paramAlgorithmIdentifier.getParameters());
    try {
      SecretKeyFactory secretKeyFactory;
      if (paramInt1 == 0) {
        secretKeyFactory = this.helper.createSecretKeyFactory("PBKDF2with8BIT");
      } else {
        secretKeyFactory = this.helper.createSecretKeyFactory((String)PBKDF2_ALG_NAMES.get(pBKDF2Params.getPrf()));
      } 
      SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(paramArrayOfchar, pBKDF2Params.getSalt(), pBKDF2Params.getIterationCount().intValue(), paramInt2));
      return secretKey.getEncoded();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("Unable to calculate derived key from password: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  static {
    BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_CBC, "DES");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED");
    BASE_CIPHER_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
    BASE_CIPHER_NAMES.put(CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_CBC, "DES/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AES/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AES/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AES/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED/CBC/PKCS5Padding");
    CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
    MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
    PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA1.getAlgorithmID(), "PBKDF2WITHHMACSHA1");
    PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA224.getAlgorithmID(), "PBKDF2WITHHMACSHA224");
    PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA256.getAlgorithmID(), "PBKDF2WITHHMACSHA256");
    PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA384.getAlgorithmID(), "PBKDF2WITHHMACSHA384");
    PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA512.getAlgorithmID(), "PBKDF2WITHHMACSHA512");
  }
  
  static interface JCECallback {
    Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
  }
}
