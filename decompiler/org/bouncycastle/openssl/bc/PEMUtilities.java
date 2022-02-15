package org.bouncycastle.openssl.bc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

class PEMUtilities {
  private static final Map KEYSIZES = new HashMap<Object, Object>();
  
  private static final Set PKCS5_SCHEME_1 = new HashSet();
  
  private static final Set PKCS5_SCHEME_2 = new HashSet();
  
  static int getKeySize(String paramString) {
    if (!KEYSIZES.containsKey(paramString))
      throw new IllegalStateException("no key size for algorithm: " + paramString); 
    return ((Integer)KEYSIZES.get(paramString)).intValue();
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
  
  public static KeyParameter generateSecretKeyForPKCS5Scheme2(String paramString, char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt) {
    PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator((Digest)new SHA1Digest());
    pKCS5S2ParametersGenerator.init(PBEParametersGenerator.PKCS5PasswordToBytes(paramArrayOfchar), paramArrayOfbyte, paramInt);
    return (KeyParameter)pKCS5S2ParametersGenerator.generateDerivedParameters(getKeySize(paramString));
  }
  
  static byte[] crypt(boolean paramBoolean, byte[] paramArrayOfbyte1, char[] paramArrayOfchar, String paramString, byte[] paramArrayOfbyte2) throws PEMException {
    AESEngine aESEngine;
    CBCBlockCipher cBCBlockCipher;
    OFBBlockCipher oFBBlockCipher;
    KeyParameter keyParameter;
    byte[] arrayOfByte = paramArrayOfbyte2;
    String str = "CBC";
    PKCS7Padding pKCS7Padding = new PKCS7Padding();
    if (paramString.endsWith("-CFB")) {
      str = "CFB";
      pKCS7Padding = null;
    } 
    if (paramString.endsWith("-ECB") || "DES-EDE".equals(paramString) || "DES-EDE3".equals(paramString)) {
      str = "ECB";
      arrayOfByte = null;
    } 
    if (paramString.endsWith("-OFB")) {
      str = "OFB";
      pKCS7Padding = null;
    } 
    if (paramString.startsWith("DES-EDE")) {
      boolean bool = !paramString.startsWith("DES-EDE3") ? true : false;
      keyParameter = getKey(paramArrayOfchar, 24, paramArrayOfbyte2, bool);
      DESedeEngine dESedeEngine = new DESedeEngine();
    } else if (paramString.startsWith("DES-")) {
      keyParameter = getKey(paramArrayOfchar, 8, paramArrayOfbyte2);
      DESEngine dESEngine = new DESEngine();
    } else if (paramString.startsWith("BF-")) {
      keyParameter = getKey(paramArrayOfchar, 16, paramArrayOfbyte2);
      BlowfishEngine blowfishEngine = new BlowfishEngine();
    } else if (paramString.startsWith("RC2-")) {
      char c = '';
      if (paramString.startsWith("RC2-40-")) {
        c = '(';
      } else if (paramString.startsWith("RC2-64-")) {
        c = '@';
      } 
      RC2Parameters rC2Parameters = new RC2Parameters(getKey(paramArrayOfchar, c / 8, paramArrayOfbyte2).getKey(), c);
      RC2Engine rC2Engine = new RC2Engine();
    } else if (paramString.startsWith("AES-")) {
      char c;
      byte[] arrayOfByte1 = paramArrayOfbyte2;
      if (arrayOfByte1.length > 8) {
        arrayOfByte1 = new byte[8];
        System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte1, 0, 8);
      } 
      if (paramString.startsWith("AES-128-")) {
        c = '';
      } else if (paramString.startsWith("AES-192-")) {
        c = 'À';
      } else if (paramString.startsWith("AES-256-")) {
        c = 'Ā';
      } else {
        throw new EncryptionException("unknown AES encryption with private key: " + paramString);
      } 
      keyParameter = getKey(paramArrayOfchar, c / 8, arrayOfByte1);
      aESEngine = new AESEngine();
    } else {
      throw new EncryptionException("unknown encryption with private key: " + paramString);
    } 
    if (str.equals("CBC")) {
      cBCBlockCipher = new CBCBlockCipher((BlockCipher)aESEngine);
    } else {
      CFBBlockCipher cFBBlockCipher;
      if (str.equals("CFB")) {
        cFBBlockCipher = new CFBBlockCipher((BlockCipher)cBCBlockCipher, cBCBlockCipher.getBlockSize() * 8);
      } else if (str.equals("OFB")) {
        oFBBlockCipher = new OFBBlockCipher((BlockCipher)cFBBlockCipher, cFBBlockCipher.getBlockSize() * 8);
      } 
    } 
    try {
      PaddedBufferedBlockCipher paddedBufferedBlockCipher;
      if (pKCS7Padding == null) {
        BufferedBlockCipher bufferedBlockCipher = new BufferedBlockCipher((BlockCipher)oFBBlockCipher);
      } else {
        paddedBufferedBlockCipher = new PaddedBufferedBlockCipher((BlockCipher)oFBBlockCipher, (BlockCipherPadding)pKCS7Padding);
      } 
      if (arrayOfByte == null) {
        paddedBufferedBlockCipher.init(paramBoolean, (CipherParameters)keyParameter);
      } else {
        paddedBufferedBlockCipher.init(paramBoolean, (CipherParameters)new ParametersWithIV((CipherParameters)keyParameter, arrayOfByte));
      } 
      byte[] arrayOfByte1 = new byte[paddedBufferedBlockCipher.getOutputSize(paramArrayOfbyte1.length)];
      int i = paddedBufferedBlockCipher.processBytes(paramArrayOfbyte1, 0, paramArrayOfbyte1.length, arrayOfByte1, 0);
      i += paddedBufferedBlockCipher.doFinal(arrayOfByte1, i);
      if (i == arrayOfByte1.length)
        return arrayOfByte1; 
      byte[] arrayOfByte2 = new byte[i];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
      return arrayOfByte2;
    } catch (Exception exception) {
      throw new EncryptionException("exception using cipher - please check password and data.", exception);
    } 
  }
  
  private static KeyParameter getKey(char[] paramArrayOfchar, int paramInt, byte[] paramArrayOfbyte) throws PEMException {
    return getKey(paramArrayOfchar, paramInt, paramArrayOfbyte, false);
  }
  
  private static KeyParameter getKey(char[] paramArrayOfchar, int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean) throws PEMException {
    OpenSSLPBEParametersGenerator openSSLPBEParametersGenerator = new OpenSSLPBEParametersGenerator();
    openSSLPBEParametersGenerator.init(PBEParametersGenerator.PKCS5PasswordToBytes(paramArrayOfchar), paramArrayOfbyte, 1);
    KeyParameter keyParameter = (KeyParameter)openSSLPBEParametersGenerator.generateDerivedParameters(paramInt * 8);
    if (paramBoolean && (keyParameter.getKey()).length == 24) {
      byte[] arrayOfByte = keyParameter.getKey();
      System.arraycopy(arrayOfByte, 0, arrayOfByte, 16, 8);
      return new KeyParameter(arrayOfByte);
    } 
    return keyParameter;
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
  }
}
