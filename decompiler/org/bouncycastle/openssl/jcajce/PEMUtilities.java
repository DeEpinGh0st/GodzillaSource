package org.bouncycastle.openssl.jcajce;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

class PEMUtilities {
  private static final Map KEYSIZES = new HashMap<Object, Object>();
  
  private static final Set PKCS5_SCHEME_1 = new HashSet();
  
  private static final Set PKCS5_SCHEME_2 = new HashSet();
  
  private static final Map PRFS = new HashMap<Object, Object>();
  
  private static final Map PRFS_SALT = new HashMap<Object, Object>();
  
  static int getKeySize(String paramString) {
    if (!KEYSIZES.containsKey(paramString))
      throw new IllegalStateException("no key size for algorithm: " + paramString); 
    return ((Integer)KEYSIZES.get(paramString)).intValue();
  }
  
  static int getSaltSize(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    if (!PRFS_SALT.containsKey(paramASN1ObjectIdentifier))
      throw new IllegalStateException("no salt size for algorithm: " + paramASN1ObjectIdentifier); 
    return ((Integer)PRFS_SALT.get(paramASN1ObjectIdentifier)).intValue();
  }
  
  static boolean isHmacSHA1(AlgorithmIdentifier paramAlgorithmIdentifier) {
    return (paramAlgorithmIdentifier == null || paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA1));
  }
  
  static boolean isPKCS5Scheme1(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return PKCS5_SCHEME_1.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isPKCS5Scheme2(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return PKCS5_SCHEME_2.contains(paramASN1ObjectIdentifier);
  }
  
  public static boolean isPKCS12(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return paramASN1ObjectIdentifier.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
  }
  
  public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper paramJcaJceHelper, String paramString, char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
    SecretKeyFactory secretKeyFactory = paramJcaJceHelper.createSecretKeyFactory("PBKDF2with8BIT");
    SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(paramArrayOfchar, paramArrayOfbyte, paramInt, getKeySize(paramString)));
    return new SecretKeySpec(secretKey.getEncoded(), paramString);
  }
  
  public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper paramJcaJceHelper, String paramString, char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt, AlgorithmIdentifier paramAlgorithmIdentifier) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
    String str = (String)PRFS.get(paramAlgorithmIdentifier.getAlgorithm());
    if (str == null)
      throw new NoSuchAlgorithmException("unknown PRF in PKCS#2: " + paramAlgorithmIdentifier.getAlgorithm()); 
    SecretKeyFactory secretKeyFactory = paramJcaJceHelper.createSecretKeyFactory(str);
    SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(paramArrayOfchar, paramArrayOfbyte, paramInt, getKeySize(paramString)));
    return new SecretKeySpec(secretKey.getEncoded(), paramString);
  }
  
  static byte[] crypt(boolean paramBoolean, JcaJceHelper paramJcaJceHelper, byte[] paramArrayOfbyte1, char[] paramArrayOfchar, String paramString, byte[] paramArrayOfbyte2) throws PEMException {
    RC2ParameterSpec rC2ParameterSpec;
    String str1;
    SecretKey secretKey;
    IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfbyte2);
    String str2 = "CBC";
    String str3 = "PKCS5Padding";
    if (paramString.endsWith("-CFB")) {
      str2 = "CFB";
      str3 = "NoPadding";
    } 
    if (paramString.endsWith("-ECB") || "DES-EDE".equals(paramString) || "DES-EDE3".equals(paramString)) {
      str2 = "ECB";
      ivParameterSpec = null;
    } 
    if (paramString.endsWith("-OFB")) {
      str2 = "OFB";
      str3 = "NoPadding";
    } 
    if (paramString.startsWith("DES-EDE")) {
      str1 = "DESede";
      boolean bool = !paramString.startsWith("DES-EDE3") ? true : false;
      secretKey = getKey(paramJcaJceHelper, paramArrayOfchar, str1, 24, paramArrayOfbyte2, bool);
    } else if (paramString.startsWith("DES-")) {
      str1 = "DES";
      secretKey = getKey(paramJcaJceHelper, paramArrayOfchar, str1, 8, paramArrayOfbyte2);
    } else if (paramString.startsWith("BF-")) {
      str1 = "Blowfish";
      secretKey = getKey(paramJcaJceHelper, paramArrayOfchar, str1, 16, paramArrayOfbyte2);
    } else if (paramString.startsWith("RC2-")) {
      str1 = "RC2";
      char c = '';
      if (paramString.startsWith("RC2-40-")) {
        c = '(';
      } else if (paramString.startsWith("RC2-64-")) {
        c = '@';
      } 
      secretKey = getKey(paramJcaJceHelper, paramArrayOfchar, str1, c / 8, paramArrayOfbyte2);
      if (ivParameterSpec == null) {
        rC2ParameterSpec = new RC2ParameterSpec(c);
      } else {
        rC2ParameterSpec = new RC2ParameterSpec(c, paramArrayOfbyte2);
      } 
    } else if (paramString.startsWith("AES-")) {
      char c;
      str1 = "AES";
      byte[] arrayOfByte = paramArrayOfbyte2;
      if (arrayOfByte.length > 8) {
        arrayOfByte = new byte[8];
        System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, 0, 8);
      } 
      if (paramString.startsWith("AES-128-")) {
        c = '';
      } else if (paramString.startsWith("AES-192-")) {
        c = 'À';
      } else if (paramString.startsWith("AES-256-")) {
        c = 'Ā';
      } else {
        throw new EncryptionException("unknown AES encryption with private key");
      } 
      secretKey = getKey(paramJcaJceHelper, paramArrayOfchar, "AES", c / 8, arrayOfByte);
    } else {
      throw new EncryptionException("unknown encryption with private key");
    } 
    String str4 = str1 + "/" + str2 + "/" + str3;
    try {
      Cipher cipher = paramJcaJceHelper.createCipher(str4);
      boolean bool = paramBoolean ? true : true;
      if (rC2ParameterSpec == null) {
        cipher.init(bool, secretKey);
      } else {
        cipher.init(bool, secretKey, rC2ParameterSpec);
      } 
      return cipher.doFinal(paramArrayOfbyte1);
    } catch (Exception exception) {
      throw new EncryptionException("exception using cipher - please check password and data.", exception);
    } 
  }
  
  private static SecretKey getKey(JcaJceHelper paramJcaJceHelper, char[] paramArrayOfchar, String paramString, int paramInt, byte[] paramArrayOfbyte) throws PEMException {
    return getKey(paramJcaJceHelper, paramArrayOfchar, paramString, paramInt, paramArrayOfbyte, false);
  }
  
  private static SecretKey getKey(JcaJceHelper paramJcaJceHelper, char[] paramArrayOfchar, String paramString, int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean) throws PEMException {
    try {
      PBEKeySpec pBEKeySpec = new PBEKeySpec(paramArrayOfchar, paramArrayOfbyte, 1, paramInt * 8);
      SecretKeyFactory secretKeyFactory = paramJcaJceHelper.createSecretKeyFactory("PBKDF-OpenSSL");
      byte[] arrayOfByte = secretKeyFactory.generateSecret(pBEKeySpec).getEncoded();
      if (paramBoolean && arrayOfByte.length >= 24)
        System.arraycopy(arrayOfByte, 0, arrayOfByte, 16, 8); 
      return new SecretKeySpec(arrayOfByte, paramString);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new PEMException("Unable to create OpenSSL PBDKF: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  static {
    PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
    PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
    PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
    PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
    PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
    PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
    PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
    PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
    PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
    PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
    PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
    KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
    KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf(128));
    KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf(192));
    KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf(256));
    KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId(), Integers.valueOf(128));
    KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
    KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
    KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
    KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
    KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
    PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "PBKDF2withHMACSHA1");
    PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "PBKDF2withHMACSHA256");
    PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "PBKDF2withHMACSHA512");
    PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "PBKDF2withHMACSHA224");
    PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "PBKDF2withHMACSHA384");
    PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, "PBKDF2withHMACSHA3-224");
    PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, "PBKDF2withHMACSHA3-256");
    PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, "PBKDF2withHMACSHA3-384");
    PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, "PBKDF2withHMACSHA3-512");
    PRFS.put(CryptoProObjectIdentifiers.gostR3411Hmac, "PBKDF2withHMACGOST3411");
    PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(20));
    PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(32));
    PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(64));
    PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(28));
    PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(48));
    PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(28));
    PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(32));
    PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(48));
    PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(64));
    PRFS_SALT.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(32));
  }
}
