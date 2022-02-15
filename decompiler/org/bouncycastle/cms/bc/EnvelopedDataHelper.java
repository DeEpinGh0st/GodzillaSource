package org.bouncycastle.cms.bc;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;

class EnvelopedDataHelper {
  protected static final Map BASE_CIPHER_NAMES = new HashMap<Object, Object>();
  
  protected static final Map MAC_ALG_NAMES = new HashMap<Object, Object>();
  
  private static final Map prfs = createTable();
  
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
  
  private static Map createTable() {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA1, new BcDigestProvider() {
          public ExtendedDigest get(AlgorithmIdentifier param1AlgorithmIdentifier) {
            return (ExtendedDigest)new SHA1Digest();
          }
        });
    hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA224, new BcDigestProvider() {
          public ExtendedDigest get(AlgorithmIdentifier param1AlgorithmIdentifier) {
            return (ExtendedDigest)new SHA224Digest();
          }
        });
    hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA256, new BcDigestProvider() {
          public ExtendedDigest get(AlgorithmIdentifier param1AlgorithmIdentifier) {
            return (ExtendedDigest)new SHA256Digest();
          }
        });
    hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA384, new BcDigestProvider() {
          public ExtendedDigest get(AlgorithmIdentifier param1AlgorithmIdentifier) {
            return (ExtendedDigest)new SHA384Digest();
          }
        });
    hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA512, new BcDigestProvider() {
          public ExtendedDigest get(AlgorithmIdentifier param1AlgorithmIdentifier) {
            return (ExtendedDigest)new SHA512Digest();
          }
        });
    return Collections.unmodifiableMap(hashMap);
  }
  
  String getBaseCipherName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = (String)BASE_CIPHER_NAMES.get(paramASN1ObjectIdentifier);
    return (str == null) ? paramASN1ObjectIdentifier.getId() : str;
  }
  
  static ExtendedDigest getPRF(AlgorithmIdentifier paramAlgorithmIdentifier) throws OperatorCreationException {
    return ((BcDigestProvider)prfs.get(paramAlgorithmIdentifier.getAlgorithm())).get(null);
  }
  
  static BufferedBlockCipher createCipher(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    CBCBlockCipher cBCBlockCipher;
    if (NISTObjectIdentifiers.id_aes128_CBC.equals(paramASN1ObjectIdentifier) || NISTObjectIdentifiers.id_aes192_CBC.equals(paramASN1ObjectIdentifier) || NISTObjectIdentifiers.id_aes256_CBC.equals(paramASN1ObjectIdentifier)) {
      cBCBlockCipher = new CBCBlockCipher((BlockCipher)new AESEngine());
    } else if (PKCSObjectIdentifiers.des_EDE3_CBC.equals(paramASN1ObjectIdentifier)) {
      cBCBlockCipher = new CBCBlockCipher((BlockCipher)new DESedeEngine());
    } else if (OIWObjectIdentifiers.desCBC.equals(paramASN1ObjectIdentifier)) {
      cBCBlockCipher = new CBCBlockCipher((BlockCipher)new DESEngine());
    } else if (PKCSObjectIdentifiers.RC2_CBC.equals(paramASN1ObjectIdentifier)) {
      cBCBlockCipher = new CBCBlockCipher((BlockCipher)new RC2Engine());
    } else if (MiscObjectIdentifiers.cast5CBC.equals(paramASN1ObjectIdentifier)) {
      cBCBlockCipher = new CBCBlockCipher((BlockCipher)new CAST5Engine());
    } else {
      throw new CMSException("cannot recognise cipher: " + paramASN1ObjectIdentifier);
    } 
    return (BufferedBlockCipher)new PaddedBufferedBlockCipher((BlockCipher)cBCBlockCipher, (BlockCipherPadding)new PKCS7Padding());
  }
  
  static Wrapper createRFC3211Wrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    if (NISTObjectIdentifiers.id_aes128_CBC.equals(paramASN1ObjectIdentifier) || NISTObjectIdentifiers.id_aes192_CBC.equals(paramASN1ObjectIdentifier) || NISTObjectIdentifiers.id_aes256_CBC.equals(paramASN1ObjectIdentifier))
      return (Wrapper)new RFC3211WrapEngine((BlockCipher)new AESEngine()); 
    if (PKCSObjectIdentifiers.des_EDE3_CBC.equals(paramASN1ObjectIdentifier))
      return (Wrapper)new RFC3211WrapEngine((BlockCipher)new DESedeEngine()); 
    if (OIWObjectIdentifiers.desCBC.equals(paramASN1ObjectIdentifier))
      return (Wrapper)new RFC3211WrapEngine((BlockCipher)new DESEngine()); 
    if (PKCSObjectIdentifiers.RC2_CBC.equals(paramASN1ObjectIdentifier))
      return (Wrapper)new RFC3211WrapEngine((BlockCipher)new RC2Engine()); 
    throw new CMSException("cannot recognise wrapper: " + paramASN1ObjectIdentifier);
  }
  
  static Object createContentCipher(boolean paramBoolean, CipherParameters paramCipherParameters, AlgorithmIdentifier paramAlgorithmIdentifier) throws CMSException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramAlgorithmIdentifier.getAlgorithm();
    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.rc4)) {
      RC4Engine rC4Engine = new RC4Engine();
      rC4Engine.init(paramBoolean, paramCipherParameters);
      return rC4Engine;
    } 
    BufferedBlockCipher bufferedBlockCipher = createCipher(paramAlgorithmIdentifier.getAlgorithm());
    ASN1Primitive aSN1Primitive = paramAlgorithmIdentifier.getParameters().toASN1Primitive();
    if (aSN1Primitive != null && !(aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Null)) {
      if (aSN1ObjectIdentifier.equals(CMSAlgorithm.DES_EDE3_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.IDEA_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.AES128_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.AES192_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.AES256_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.CAMELLIA128_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.CAMELLIA192_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.CAMELLIA256_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.SEED_CBC) || aSN1ObjectIdentifier.equals(OIWObjectIdentifiers.desCBC)) {
        bufferedBlockCipher.init(paramBoolean, (CipherParameters)new ParametersWithIV(paramCipherParameters, ASN1OctetString.getInstance(aSN1Primitive).getOctets()));
      } else if (aSN1ObjectIdentifier.equals(CMSAlgorithm.CAST5_CBC)) {
        CAST5CBCParameters cAST5CBCParameters = CAST5CBCParameters.getInstance(aSN1Primitive);
        bufferedBlockCipher.init(paramBoolean, (CipherParameters)new ParametersWithIV(paramCipherParameters, cAST5CBCParameters.getIV()));
      } else if (aSN1ObjectIdentifier.equals(CMSAlgorithm.RC2_CBC)) {
        RC2CBCParameter rC2CBCParameter = RC2CBCParameter.getInstance(aSN1Primitive);
        bufferedBlockCipher.init(paramBoolean, (CipherParameters)new ParametersWithIV((CipherParameters)new RC2Parameters(((KeyParameter)paramCipherParameters).getKey(), rc2Ekb[rC2CBCParameter.getRC2ParameterVersion().intValue()]), rC2CBCParameter.getIV()));
      } else {
        throw new CMSException("cannot match parameters");
      } 
    } else if (aSN1ObjectIdentifier.equals(CMSAlgorithm.DES_EDE3_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.IDEA_CBC) || aSN1ObjectIdentifier.equals(CMSAlgorithm.CAST5_CBC)) {
      bufferedBlockCipher.init(paramBoolean, (CipherParameters)new ParametersWithIV(paramCipherParameters, new byte[8]));
    } else {
      bufferedBlockCipher.init(paramBoolean, paramCipherParameters);
    } 
    return bufferedBlockCipher;
  }
  
  AlgorithmIdentifier generateAlgorithmIdentifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier, CipherParameters paramCipherParameters, SecureRandom paramSecureRandom) throws CMSException {
    if (paramASN1ObjectIdentifier.equals(CMSAlgorithm.AES128_CBC) || paramASN1ObjectIdentifier.equals(CMSAlgorithm.AES192_CBC) || paramASN1ObjectIdentifier.equals(CMSAlgorithm.AES256_CBC) || paramASN1ObjectIdentifier.equals(CMSAlgorithm.CAMELLIA128_CBC) || paramASN1ObjectIdentifier.equals(CMSAlgorithm.CAMELLIA192_CBC) || paramASN1ObjectIdentifier.equals(CMSAlgorithm.CAMELLIA256_CBC) || paramASN1ObjectIdentifier.equals(CMSAlgorithm.SEED_CBC)) {
      byte[] arrayOfByte = new byte[16];
      paramSecureRandom.nextBytes(arrayOfByte);
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)new DEROctetString(arrayOfByte));
    } 
    if (paramASN1ObjectIdentifier.equals(CMSAlgorithm.DES_EDE3_CBC) || paramASN1ObjectIdentifier.equals(CMSAlgorithm.IDEA_CBC) || paramASN1ObjectIdentifier.equals(OIWObjectIdentifiers.desCBC)) {
      byte[] arrayOfByte = new byte[8];
      paramSecureRandom.nextBytes(arrayOfByte);
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)new DEROctetString(arrayOfByte));
    } 
    if (paramASN1ObjectIdentifier.equals(CMSAlgorithm.CAST5_CBC)) {
      byte[] arrayOfByte = new byte[8];
      paramSecureRandom.nextBytes(arrayOfByte);
      CAST5CBCParameters cAST5CBCParameters = new CAST5CBCParameters(arrayOfByte, (((KeyParameter)paramCipherParameters).getKey()).length * 8);
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)cAST5CBCParameters);
    } 
    if (paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.rc4))
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)DERNull.INSTANCE); 
    if (paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.RC2_CBC)) {
      byte[] arrayOfByte = new byte[8];
      paramSecureRandom.nextBytes(arrayOfByte);
      RC2CBCParameter rC2CBCParameter = new RC2CBCParameter(rc2Table[128], arrayOfByte);
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)rC2CBCParameter);
    } 
    throw new CMSException("unable to match algorithm");
  }
  
  CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier, SecureRandom paramSecureRandom) throws CMSException {
    if (NISTObjectIdentifiers.id_aes128_CBC.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 128); 
    if (NISTObjectIdentifiers.id_aes192_CBC.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 192); 
    if (NISTObjectIdentifiers.id_aes256_CBC.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 256); 
    if (PKCSObjectIdentifiers.des_EDE3_CBC.equals(paramASN1ObjectIdentifier)) {
      DESedeKeyGenerator dESedeKeyGenerator = new DESedeKeyGenerator();
      dESedeKeyGenerator.init(new KeyGenerationParameters(paramSecureRandom, 192));
      return (CipherKeyGenerator)dESedeKeyGenerator;
    } 
    if (NTTObjectIdentifiers.id_camellia128_cbc.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 128); 
    if (NTTObjectIdentifiers.id_camellia192_cbc.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 192); 
    if (NTTObjectIdentifiers.id_camellia256_cbc.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 256); 
    if (KISAObjectIdentifiers.id_seedCBC.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 128); 
    if (CMSAlgorithm.CAST5_CBC.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 128); 
    if (OIWObjectIdentifiers.desCBC.equals(paramASN1ObjectIdentifier)) {
      DESKeyGenerator dESKeyGenerator = new DESKeyGenerator();
      dESKeyGenerator.init(new KeyGenerationParameters(paramSecureRandom, 64));
      return (CipherKeyGenerator)dESKeyGenerator;
    } 
    if (PKCSObjectIdentifiers.rc4.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 128); 
    if (PKCSObjectIdentifiers.RC2_CBC.equals(paramASN1ObjectIdentifier))
      return createCipherKeyGenerator(paramSecureRandom, 128); 
    throw new CMSException("cannot recognise cipher: " + paramASN1ObjectIdentifier);
  }
  
  private CipherKeyGenerator createCipherKeyGenerator(SecureRandom paramSecureRandom, int paramInt) {
    CipherKeyGenerator cipherKeyGenerator = new CipherKeyGenerator();
    cipherKeyGenerator.init(new KeyGenerationParameters(paramSecureRandom, paramInt));
    return cipherKeyGenerator;
  }
  
  static {
    BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
    BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
    MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
    MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
  }
}
