package org.bouncycastle.jce.provider;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;

public interface BrokenPBE {
  public static final int MD5 = 0;
  
  public static final int SHA1 = 1;
  
  public static final int RIPEMD160 = 2;
  
  public static final int PKCS5S1 = 0;
  
  public static final int PKCS5S2 = 1;
  
  public static final int PKCS12 = 2;
  
  public static final int OLD_PKCS12 = 3;
  
  public static class Util {
    private static void setOddParity(byte[] param1ArrayOfbyte) {
      for (byte b = 0; b < param1ArrayOfbyte.length; b++) {
        byte b1 = param1ArrayOfbyte[b];
        param1ArrayOfbyte[b] = (byte)(b1 & 0xFE | b1 >> 1 ^ b1 >> 2 ^ b1 >> 3 ^ b1 >> 4 ^ b1 >> 5 ^ b1 >> 6 ^ b1 >> 7 ^ 0x1);
      } 
    }
    
    private static PBEParametersGenerator makePBEGenerator(int param1Int1, int param1Int2) {
      if (param1Int1 == 0) {
        switch (param1Int2) {
          case 0:
            return (PBEParametersGenerator)new PKCS5S1ParametersGenerator((Digest)new MD5Digest());
          case 1:
            return (PBEParametersGenerator)new PKCS5S1ParametersGenerator((Digest)new SHA1Digest());
        } 
        throw new IllegalStateException("PKCS5 scheme 1 only supports only MD5 and SHA1.");
      } 
      if (param1Int1 == 1) {
        PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator();
      } else {
        if (param1Int1 == 3) {
          switch (param1Int2) {
            case 0:
              return new OldPKCS12ParametersGenerator((Digest)new MD5Digest());
            case 1:
              return new OldPKCS12ParametersGenerator((Digest)new SHA1Digest());
            case 2:
              return new OldPKCS12ParametersGenerator((Digest)new RIPEMD160Digest());
          } 
          throw new IllegalStateException("unknown digest scheme for PBE encryption.");
        } 
        switch (param1Int2) {
          case 0:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator((Digest)new MD5Digest());
          case 1:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator((Digest)new SHA1Digest());
          case 2:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator((Digest)new RIPEMD160Digest());
        } 
        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
      } 
      return (PBEParametersGenerator)SYNTHETIC_LOCAL_VARIABLE_2;
    }
    
    static CipherParameters makePBEParameters(BCPBEKey param1BCPBEKey, AlgorithmParameterSpec param1AlgorithmParameterSpec, int param1Int1, int param1Int2, String param1String, int param1Int3, int param1Int4) {
      CipherParameters cipherParameters;
      if (param1AlgorithmParameterSpec == null || !(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key."); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1Int1, param1Int2);
      byte[] arrayOfByte = param1BCPBEKey.getEncoded();
      pBEParametersGenerator.init(arrayOfByte, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
      if (param1Int4 != 0) {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1Int3, param1Int4);
      } else {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1Int3);
      } 
      if (param1String.startsWith("DES"))
        if (cipherParameters instanceof ParametersWithIV) {
          KeyParameter keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
          setOddParity(keyParameter.getKey());
        } else {
          KeyParameter keyParameter = (KeyParameter)cipherParameters;
          setOddParity(keyParameter.getKey());
        }  
      for (byte b = 0; b != arrayOfByte.length; b++)
        arrayOfByte[b] = 0; 
      return cipherParameters;
    }
    
    static CipherParameters makePBEMacParameters(BCPBEKey param1BCPBEKey, AlgorithmParameterSpec param1AlgorithmParameterSpec, int param1Int1, int param1Int2, int param1Int3) {
      if (param1AlgorithmParameterSpec == null || !(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key."); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1Int1, param1Int2);
      byte[] arrayOfByte = param1BCPBEKey.getEncoded();
      pBEParametersGenerator.init(arrayOfByte, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
      CipherParameters cipherParameters = pBEParametersGenerator.generateDerivedMacParameters(param1Int3);
      for (byte b = 0; b != arrayOfByte.length; b++)
        arrayOfByte[b] = 0; 
      return cipherParameters;
    }
  }
}
