package org.bouncycastle.pkcs.bc;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Integers;

class PKCS12PBEUtils {
  private static Map keySizes = new HashMap<Object, Object>();
  
  private static Set noIvAlgs = new HashSet();
  
  private static Set desAlgs = new HashSet();
  
  static int getKeySize(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return ((Integer)keySizes.get(paramASN1ObjectIdentifier)).intValue();
  }
  
  static boolean hasNoIv(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return noIvAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isDesAlg(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return desAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static PaddedBufferedBlockCipher getEngine(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    RC2Engine rC2Engine;
    if (paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC) || paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC)) {
      DESedeEngine dESedeEngine = new DESedeEngine();
    } else if (paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC) || paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC)) {
      rC2Engine = new RC2Engine();
    } else {
      throw new IllegalStateException("unknown algorithm");
    } 
    return new PaddedBufferedBlockCipher((BlockCipher)new CBCBlockCipher((BlockCipher)rC2Engine), (BlockCipherPadding)new PKCS7Padding());
  }
  
  static MacCalculator createMacCalculator(final ASN1ObjectIdentifier digestAlgorithm, ExtendedDigest paramExtendedDigest, final PKCS12PBEParams pbeParams, final char[] password) {
    PKCS12ParametersGenerator pKCS12ParametersGenerator = new PKCS12ParametersGenerator((Digest)paramExtendedDigest);
    pKCS12ParametersGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(password), pbeParams.getIV(), pbeParams.getIterations().intValue());
    KeyParameter keyParameter = (KeyParameter)pKCS12ParametersGenerator.generateDerivedMacParameters(paramExtendedDigest.getDigestSize() * 8);
    final HMac hMac = new HMac((Digest)paramExtendedDigest);
    hMac.init((CipherParameters)keyParameter);
    return new MacCalculator() {
        public AlgorithmIdentifier getAlgorithmIdentifier() {
          return new AlgorithmIdentifier(digestAlgorithm, (ASN1Encodable)pbeParams);
        }
        
        public OutputStream getOutputStream() {
          return (OutputStream)new MacOutputStream((Mac)hMac);
        }
        
        public byte[] getMac() {
          byte[] arrayOfByte = new byte[hMac.getMacSize()];
          hMac.doFinal(arrayOfByte, 0);
          return arrayOfByte;
        }
        
        public GenericKey getKey() {
          return new GenericKey(getAlgorithmIdentifier(), PKCS12ParametersGenerator.PKCS12PasswordToBytes(password));
        }
      };
  }
  
  static CipherParameters createCipherParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ExtendedDigest paramExtendedDigest, int paramInt, PKCS12PBEParams paramPKCS12PBEParams, char[] paramArrayOfchar) {
    CipherParameters cipherParameters;
    PKCS12ParametersGenerator pKCS12ParametersGenerator = new PKCS12ParametersGenerator((Digest)paramExtendedDigest);
    pKCS12ParametersGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(paramArrayOfchar), paramPKCS12PBEParams.getIV(), paramPKCS12PBEParams.getIterations().intValue());
    if (hasNoIv(paramASN1ObjectIdentifier)) {
      cipherParameters = pKCS12ParametersGenerator.generateDerivedParameters(getKeySize(paramASN1ObjectIdentifier));
    } else {
      cipherParameters = pKCS12ParametersGenerator.generateDerivedParameters(getKeySize(paramASN1ObjectIdentifier), paramInt * 8);
      if (isDesAlg(paramASN1ObjectIdentifier))
        DESedeParameters.setOddParity(((KeyParameter)((ParametersWithIV)cipherParameters).getParameters()).getKey()); 
    } 
    return cipherParameters;
  }
  
  static {
    keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, Integers.valueOf(128));
    keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
    keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
    keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
    keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
    keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
    noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4);
    noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4);
    desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC);
    desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC);
  }
}
