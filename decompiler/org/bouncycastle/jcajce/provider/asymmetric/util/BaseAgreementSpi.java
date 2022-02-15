package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

public abstract class BaseAgreementSpi extends KeyAgreementSpi {
  private static final Map<String, ASN1ObjectIdentifier> defaultOids = new HashMap<String, ASN1ObjectIdentifier>();
  
  private static final Map<String, Integer> keySizes = new HashMap<String, Integer>();
  
  private static final Map<String, String> nameTable = new HashMap<String, String>();
  
  private static final Hashtable oids = new Hashtable<Object, Object>();
  
  private static final Hashtable des = new Hashtable<Object, Object>();
  
  private final String kaAlgorithm;
  
  private final DerivationFunction kdf;
  
  protected byte[] ukmParameters;
  
  public BaseAgreementSpi(String paramString, DerivationFunction paramDerivationFunction) {
    this.kaAlgorithm = paramString;
    this.kdf = paramDerivationFunction;
  }
  
  protected static String getAlgorithm(String paramString) {
    if (paramString.indexOf('[') > 0)
      return paramString.substring(0, paramString.indexOf('[')); 
    if (paramString.startsWith(NISTObjectIdentifiers.aes.getId()))
      return "AES"; 
    if (paramString.startsWith(GNUObjectIdentifiers.Serpent.getId()))
      return "Serpent"; 
    String str = nameTable.get(Strings.toUpperCase(paramString));
    return (str != null) ? str : paramString;
  }
  
  protected static int getKeySize(String paramString) {
    if (paramString.indexOf('[') > 0)
      return Integer.parseInt(paramString.substring(paramString.indexOf('[') + 1, paramString.indexOf(']'))); 
    String str = Strings.toUpperCase(paramString);
    return !keySizes.containsKey(str) ? -1 : ((Integer)keySizes.get(str)).intValue();
  }
  
  protected static byte[] trimZeroes(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte[0] != 0)
      return paramArrayOfbyte; 
    byte b;
    for (b = 0; b < paramArrayOfbyte.length && paramArrayOfbyte[b] == 0; b++);
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length - b];
    System.arraycopy(paramArrayOfbyte, b, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  protected byte[] engineGenerateSecret() throws IllegalStateException {
    if (this.kdf != null)
      throw new UnsupportedOperationException("KDF can only be used when algorithm is known"); 
    return calcSecret();
  }
  
  protected int engineGenerateSecret(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, ShortBufferException {
    byte[] arrayOfByte = engineGenerateSecret();
    if (paramArrayOfbyte.length - paramInt < arrayOfByte.length)
      throw new ShortBufferException(this.kaAlgorithm + " key agreement: need " + arrayOfByte.length + " bytes"); 
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected SecretKey engineGenerateSecret(String paramString) throws NoSuchAlgorithmException {
    byte[] arrayOfByte = calcSecret();
    String str1 = Strings.toUpperCase(paramString);
    String str2 = paramString;
    if (oids.containsKey(str1))
      str2 = ((ASN1ObjectIdentifier)oids.get(str1)).getId(); 
    int i = getKeySize(str2);
    if (this.kdf != null) {
      if (i < 0)
        throw new NoSuchAlgorithmException("unknown algorithm encountered: " + str2); 
      byte[] arrayOfByte1 = new byte[i / 8];
      if (this.kdf instanceof org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        try {
          aSN1ObjectIdentifier = new ASN1ObjectIdentifier(str2);
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new NoSuchAlgorithmException("no OID for algorithm: " + str2);
        } 
        DHKDFParameters dHKDFParameters = new DHKDFParameters(aSN1ObjectIdentifier, i, arrayOfByte, this.ukmParameters);
        this.kdf.init((DerivationParameters)dHKDFParameters);
      } else {
        KDFParameters kDFParameters = new KDFParameters(arrayOfByte, this.ukmParameters);
        this.kdf.init((DerivationParameters)kDFParameters);
      } 
      this.kdf.generateBytes(arrayOfByte1, 0, arrayOfByte1.length);
      arrayOfByte = arrayOfByte1;
    } else if (i > 0) {
      byte[] arrayOfByte1 = new byte[i / 8];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, arrayOfByte1.length);
      arrayOfByte = arrayOfByte1;
    } 
    String str3 = getAlgorithm(paramString);
    if (des.containsKey(str3))
      DESParameters.setOddParity(arrayOfByte); 
    return new SecretKeySpec(arrayOfByte, str3);
  }
  
  protected abstract byte[] calcSecret();
  
  static {
    Integer integer1 = Integers.valueOf(64);
    Integer integer2 = Integers.valueOf(128);
    Integer integer3 = Integers.valueOf(192);
    Integer integer4 = Integers.valueOf(256);
    keySizes.put("DES", integer1);
    keySizes.put("DESEDE", integer3);
    keySizes.put("BLOWFISH", integer2);
    keySizes.put("AES", integer4);
    keySizes.put(NISTObjectIdentifiers.id_aes128_ECB.getId(), integer2);
    keySizes.put(NISTObjectIdentifiers.id_aes192_ECB.getId(), integer3);
    keySizes.put(NISTObjectIdentifiers.id_aes256_ECB.getId(), integer4);
    keySizes.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), integer2);
    keySizes.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), integer3);
    keySizes.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), integer4);
    keySizes.put(NISTObjectIdentifiers.id_aes128_CFB.getId(), integer2);
    keySizes.put(NISTObjectIdentifiers.id_aes192_CFB.getId(), integer3);
    keySizes.put(NISTObjectIdentifiers.id_aes256_CFB.getId(), integer4);
    keySizes.put(NISTObjectIdentifiers.id_aes128_OFB.getId(), integer2);
    keySizes.put(NISTObjectIdentifiers.id_aes192_OFB.getId(), integer3);
    keySizes.put(NISTObjectIdentifiers.id_aes256_OFB.getId(), integer4);
    keySizes.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), integer2);
    keySizes.put(NISTObjectIdentifiers.id_aes192_wrap.getId(), integer3);
    keySizes.put(NISTObjectIdentifiers.id_aes256_wrap.getId(), integer4);
    keySizes.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), integer2);
    keySizes.put(NISTObjectIdentifiers.id_aes192_CCM.getId(), integer3);
    keySizes.put(NISTObjectIdentifiers.id_aes256_CCM.getId(), integer4);
    keySizes.put(NISTObjectIdentifiers.id_aes128_GCM.getId(), integer2);
    keySizes.put(NISTObjectIdentifiers.id_aes192_GCM.getId(), integer3);
    keySizes.put(NISTObjectIdentifiers.id_aes256_GCM.getId(), integer4);
    keySizes.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), integer2);
    keySizes.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), integer3);
    keySizes.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), integer4);
    keySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), integer2);
    keySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), integer3);
    keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), integer3);
    keySizes.put(OIWObjectIdentifiers.desCBC.getId(), integer1);
    keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), integer4);
    keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap.getId(), integer4);
    keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId(), integer4);
    keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), Integers.valueOf(160));
    keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), integer4);
    keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), Integers.valueOf(384));
    keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), Integers.valueOf(512));
    defaultOids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
    defaultOids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
    defaultOids.put("CAMELLIA", NTTObjectIdentifiers.id_camellia256_cbc);
    defaultOids.put("SEED", KISAObjectIdentifiers.id_seedCBC);
    defaultOids.put("DES", OIWObjectIdentifiers.desCBC);
    nameTable.put(MiscObjectIdentifiers.cast5CBC.getId(), "CAST5");
    nameTable.put(MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC.getId(), "IDEA");
    nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_ECB.getId(), "Blowfish");
    nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC.getId(), "Blowfish");
    nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CFB.getId(), "Blowfish");
    nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_OFB.getId(), "Blowfish");
    nameTable.put(OIWObjectIdentifiers.desECB.getId(), "DES");
    nameTable.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
    nameTable.put(OIWObjectIdentifiers.desCFB.getId(), "DES");
    nameTable.put(OIWObjectIdentifiers.desOFB.getId(), "DES");
    nameTable.put(OIWObjectIdentifiers.desEDE.getId(), "DESede");
    nameTable.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DESede");
    nameTable.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DESede");
    nameTable.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap.getId(), "RC2");
    nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), "HmacSHA1");
    nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA224.getId(), "HmacSHA224");
    nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), "HmacSHA256");
    nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), "HmacSHA384");
    nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), "HmacSHA512");
    nameTable.put(NTTObjectIdentifiers.id_camellia128_cbc.getId(), "Camellia");
    nameTable.put(NTTObjectIdentifiers.id_camellia192_cbc.getId(), "Camellia");
    nameTable.put(NTTObjectIdentifiers.id_camellia256_cbc.getId(), "Camellia");
    nameTable.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), "Camellia");
    nameTable.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), "Camellia");
    nameTable.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), "Camellia");
    nameTable.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), "SEED");
    nameTable.put(KISAObjectIdentifiers.id_seedCBC.getId(), "SEED");
    nameTable.put(KISAObjectIdentifiers.id_seedMAC.getId(), "SEED");
    nameTable.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), "GOST28147");
    nameTable.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), "AES");
    nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
    nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
    oids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
    oids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
    oids.put("DES", OIWObjectIdentifiers.desCBC);
    des.put("DES", "DES");
    des.put("DESEDE", "DES");
    des.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
    des.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DES");
    des.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DES");
  }
}
