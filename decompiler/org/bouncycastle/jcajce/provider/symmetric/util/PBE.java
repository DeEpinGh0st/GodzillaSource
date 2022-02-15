package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

public interface PBE {
  public static final int MD5 = 0;
  
  public static final int SHA1 = 1;
  
  public static final int RIPEMD160 = 2;
  
  public static final int TIGER = 3;
  
  public static final int SHA256 = 4;
  
  public static final int MD2 = 5;
  
  public static final int GOST3411 = 6;
  
  public static final int SHA224 = 7;
  
  public static final int SHA384 = 8;
  
  public static final int SHA512 = 9;
  
  public static final int SHA3_224 = 10;
  
  public static final int SHA3_256 = 11;
  
  public static final int SHA3_384 = 12;
  
  public static final int SHA3_512 = 13;
  
  public static final int PKCS5S1 = 0;
  
  public static final int PKCS5S2 = 1;
  
  public static final int PKCS12 = 2;
  
  public static final int OPENSSL = 3;
  
  public static final int PKCS5S1_UTF8 = 4;
  
  public static final int PKCS5S2_UTF8 = 5;
  
  public static class Util {
    private static PBEParametersGenerator makePBEGenerator(int param1Int1, int param1Int2) {
      if (param1Int1 == 0 || param1Int1 == 4) {
        switch (param1Int2) {
          case 5:
            return (PBEParametersGenerator)new PKCS5S1ParametersGenerator((Digest)new MD2Digest());
          case 0:
            return (PBEParametersGenerator)new PKCS5S1ParametersGenerator(DigestFactory.createMD5());
          case 1:
            return (PBEParametersGenerator)new PKCS5S1ParametersGenerator(DigestFactory.createSHA1());
        } 
        throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
      } 
      if (param1Int1 == 1 || param1Int1 == 5) {
        switch (param1Int2) {
          case 5:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator((Digest)new MD2Digest());
          case 0:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createMD5());
          case 1:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA1());
          case 2:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator((Digest)new RIPEMD160Digest());
          case 3:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator((Digest)new TigerDigest());
          case 4:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA256());
          case 6:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator((Digest)new GOST3411Digest());
          case 7:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA224());
          case 8:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA384());
          case 9:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA512());
          case 10:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_224());
          case 11:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_256());
          case 12:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_384());
          case 13:
            return (PBEParametersGenerator)new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_512());
        } 
        throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
      } 
      if (param1Int1 == 2) {
        switch (param1Int2) {
          case 5:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator((Digest)new MD2Digest());
          case 0:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator(DigestFactory.createMD5());
          case 1:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator(DigestFactory.createSHA1());
          case 2:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator((Digest)new RIPEMD160Digest());
          case 3:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator((Digest)new TigerDigest());
          case 4:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator(DigestFactory.createSHA256());
          case 6:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator((Digest)new GOST3411Digest());
          case 7:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator(DigestFactory.createSHA224());
          case 8:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator(DigestFactory.createSHA384());
          case 9:
            return (PBEParametersGenerator)new PKCS12ParametersGenerator(DigestFactory.createSHA512());
        } 
        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
      } 
      return (PBEParametersGenerator)new OpenSSLPBEParametersGenerator();
    }
    
    public static CipherParameters makePBEParameters(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, int param1Int3, int param1Int4, AlgorithmParameterSpec param1AlgorithmParameterSpec, String param1String) throws InvalidAlgorithmParameterException {
      CipherParameters cipherParameters;
      if (param1AlgorithmParameterSpec == null || !(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new InvalidAlgorithmParameterException("Need a PBEParameter spec with a PBE key."); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1Int1, param1Int2);
      byte[] arrayOfByte = param1ArrayOfbyte;
      pBEParametersGenerator.init(arrayOfByte, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
      if (param1Int4 != 0) {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1Int3, param1Int4);
      } else {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1Int3);
      } 
      if (param1String.startsWith("DES"))
        if (cipherParameters instanceof ParametersWithIV) {
          KeyParameter keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
          DESParameters.setOddParity(keyParameter.getKey());
        } else {
          KeyParameter keyParameter = (KeyParameter)cipherParameters;
          DESParameters.setOddParity(keyParameter.getKey());
        }  
      return cipherParameters;
    }
    
    public static CipherParameters makePBEParameters(BCPBEKey param1BCPBEKey, AlgorithmParameterSpec param1AlgorithmParameterSpec, String param1String) {
      CipherParameters cipherParameters;
      if (param1AlgorithmParameterSpec == null || !(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key."); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1BCPBEKey.getType(), param1BCPBEKey.getDigest());
      byte[] arrayOfByte = param1BCPBEKey.getEncoded();
      if (param1BCPBEKey.shouldTryWrongPKCS12())
        arrayOfByte = new byte[2]; 
      pBEParametersGenerator.init(arrayOfByte, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
      if (param1BCPBEKey.getIvSize() != 0) {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1BCPBEKey.getKeySize(), param1BCPBEKey.getIvSize());
      } else {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1BCPBEKey.getKeySize());
      } 
      if (param1String.startsWith("DES"))
        if (cipherParameters instanceof ParametersWithIV) {
          KeyParameter keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
          DESParameters.setOddParity(keyParameter.getKey());
        } else {
          KeyParameter keyParameter = (KeyParameter)cipherParameters;
          DESParameters.setOddParity(keyParameter.getKey());
        }  
      return cipherParameters;
    }
    
    public static CipherParameters makePBEMacParameters(BCPBEKey param1BCPBEKey, AlgorithmParameterSpec param1AlgorithmParameterSpec) {
      if (param1AlgorithmParameterSpec == null || !(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key."); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1BCPBEKey.getType(), param1BCPBEKey.getDigest());
      byte[] arrayOfByte = param1BCPBEKey.getEncoded();
      pBEParametersGenerator.init(arrayOfByte, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
      return pBEParametersGenerator.generateDerivedMacParameters(param1BCPBEKey.getKeySize());
    }
    
    public static CipherParameters makePBEMacParameters(PBEKeySpec param1PBEKeySpec, int param1Int1, int param1Int2, int param1Int3) {
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1Int1, param1Int2);
      byte[] arrayOfByte = convertPassword(param1Int1, param1PBEKeySpec);
      pBEParametersGenerator.init(arrayOfByte, param1PBEKeySpec.getSalt(), param1PBEKeySpec.getIterationCount());
      CipherParameters cipherParameters = pBEParametersGenerator.generateDerivedMacParameters(param1Int3);
      for (byte b = 0; b != arrayOfByte.length; b++)
        arrayOfByte[b] = 0; 
      return cipherParameters;
    }
    
    public static CipherParameters makePBEParameters(PBEKeySpec param1PBEKeySpec, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      CipherParameters cipherParameters;
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1Int1, param1Int2);
      byte[] arrayOfByte = convertPassword(param1Int1, param1PBEKeySpec);
      pBEParametersGenerator.init(arrayOfByte, param1PBEKeySpec.getSalt(), param1PBEKeySpec.getIterationCount());
      if (param1Int4 != 0) {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1Int3, param1Int4);
      } else {
        cipherParameters = pBEParametersGenerator.generateDerivedParameters(param1Int3);
      } 
      for (byte b = 0; b != arrayOfByte.length; b++)
        arrayOfByte[b] = 0; 
      return cipherParameters;
    }
    
    public static CipherParameters makePBEMacParameters(SecretKey param1SecretKey, int param1Int1, int param1Int2, int param1Int3, PBEParameterSpec param1PBEParameterSpec) {
      PBEParametersGenerator pBEParametersGenerator = makePBEGenerator(param1Int1, param1Int2);
      byte[] arrayOfByte = param1SecretKey.getEncoded();
      pBEParametersGenerator.init(param1SecretKey.getEncoded(), param1PBEParameterSpec.getSalt(), param1PBEParameterSpec.getIterationCount());
      CipherParameters cipherParameters = pBEParametersGenerator.generateDerivedMacParameters(param1Int3);
      for (byte b = 0; b != arrayOfByte.length; b++)
        arrayOfByte[b] = 0; 
      return cipherParameters;
    }
    
    private static byte[] convertPassword(int param1Int, PBEKeySpec param1PBEKeySpec) {
      byte[] arrayOfByte;
      if (param1Int == 2) {
        arrayOfByte = PBEParametersGenerator.PKCS12PasswordToBytes(param1PBEKeySpec.getPassword());
      } else if (param1Int == 5 || param1Int == 4) {
        arrayOfByte = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(param1PBEKeySpec.getPassword());
      } else {
        arrayOfByte = PBEParametersGenerator.PKCS5PasswordToBytes(param1PBEKeySpec.getPassword());
      } 
      return arrayOfByte;
    }
  }
}
