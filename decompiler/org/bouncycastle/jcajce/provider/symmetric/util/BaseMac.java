package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.jcajce.spec.SkeinParameterSpec;

public class BaseMac extends MacSpi implements PBE {
  private static final Class gcmSpecClass = ClassUtil.loadClass(BaseMac.class, "javax.crypto.spec.GCMParameterSpec");
  
  private Mac macEngine;
  
  private int scheme = 2;
  
  private int pbeHash = 1;
  
  private int keySize = 160;
  
  protected BaseMac(Mac paramMac) {
    this.macEngine = paramMac;
  }
  
  protected BaseMac(Mac paramMac, int paramInt1, int paramInt2, int paramInt3) {
    this.macEngine = paramMac;
    this.scheme = paramInt1;
    this.pbeHash = paramInt2;
    this.keySize = paramInt3;
  }
  
  protected void engineInit(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    KeyParameter keyParameter1;
    AEADParameters aEADParameters;
    KeyParameter keyParameter2;
    if (paramKey == null)
      throw new InvalidKeyException("key is null"); 
    if (paramKey instanceof org.bouncycastle.jcajce.PKCS12Key) {
      SecretKey secretKey;
      PBEParameterSpec pBEParameterSpec;
      try {
        secretKey = (SecretKey)paramKey;
      } catch (Exception exception) {
        throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
      } 
      try {
        pBEParameterSpec = (PBEParameterSpec)paramAlgorithmParameterSpec;
      } catch (Exception exception) {
        throw new InvalidAlgorithmParameterException("PKCS12 requires a PBEParameterSpec");
      } 
      if (secretKey instanceof PBEKey && pBEParameterSpec == null)
        pBEParameterSpec = new PBEParameterSpec(((PBEKey)secretKey).getSalt(), ((PBEKey)secretKey).getIterationCount()); 
      byte b = 1;
      char c = ' ';
      if (this.macEngine.getAlgorithmName().startsWith("GOST")) {
        b = 6;
        c = 'Ā';
      } else if (this.macEngine instanceof org.bouncycastle.crypto.macs.HMac && !this.macEngine.getAlgorithmName().startsWith("SHA-1")) {
        if (this.macEngine.getAlgorithmName().startsWith("SHA-224")) {
          b = 7;
          c = 'à';
        } else if (this.macEngine.getAlgorithmName().startsWith("SHA-256")) {
          b = 4;
          c = 'Ā';
        } else if (this.macEngine.getAlgorithmName().startsWith("SHA-384")) {
          b = 8;
          c = 'ƀ';
        } else if (this.macEngine.getAlgorithmName().startsWith("SHA-512")) {
          b = 9;
          c = 'Ȁ';
        } else if (this.macEngine.getAlgorithmName().startsWith("RIPEMD160")) {
          b = 2;
          c = ' ';
        } else {
          throw new InvalidAlgorithmParameterException("no PKCS12 mapping for HMAC: " + this.macEngine.getAlgorithmName());
        } 
      } 
      CipherParameters cipherParameters = PBE.Util.makePBEMacParameters(secretKey, 2, b, c, pBEParameterSpec);
    } else if (paramKey instanceof BCPBEKey) {
      BCPBEKey bCPBEKey = (BCPBEKey)paramKey;
      if (bCPBEKey.getParam() != null) {
        CipherParameters cipherParameters = bCPBEKey.getParam();
      } else if (paramAlgorithmParameterSpec instanceof PBEParameterSpec) {
        CipherParameters cipherParameters = PBE.Util.makePBEMacParameters(bCPBEKey, paramAlgorithmParameterSpec);
      } else {
        throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
      } 
    } else {
      if (paramAlgorithmParameterSpec instanceof PBEParameterSpec)
        throw new InvalidAlgorithmParameterException("inappropriate parameter type: " + paramAlgorithmParameterSpec.getClass().getName()); 
      keyParameter1 = new KeyParameter(paramKey.getEncoded());
    } 
    if (keyParameter1 instanceof ParametersWithIV) {
      keyParameter2 = (KeyParameter)((ParametersWithIV)keyParameter1).getParameters();
    } else {
      keyParameter2 = keyParameter1;
    } 
    if (paramAlgorithmParameterSpec instanceof AEADParameterSpec) {
      AEADParameterSpec aEADParameterSpec = (AEADParameterSpec)paramAlgorithmParameterSpec;
      aEADParameters = new AEADParameters(keyParameter2, aEADParameterSpec.getMacSizeInBits(), aEADParameterSpec.getNonce(), aEADParameterSpec.getAssociatedData());
    } else if (paramAlgorithmParameterSpec instanceof IvParameterSpec) {
      ParametersWithIV parametersWithIV = new ParametersWithIV((CipherParameters)keyParameter2, ((IvParameterSpec)paramAlgorithmParameterSpec).getIV());
    } else if (paramAlgorithmParameterSpec instanceof RC2ParameterSpec) {
      ParametersWithIV parametersWithIV = new ParametersWithIV((CipherParameters)new RC2Parameters(keyParameter2.getKey(), ((RC2ParameterSpec)paramAlgorithmParameterSpec).getEffectiveKeyBits()), ((RC2ParameterSpec)paramAlgorithmParameterSpec).getIV());
    } else if (paramAlgorithmParameterSpec instanceof SkeinParameterSpec) {
      SkeinParameters skeinParameters = (new SkeinParameters.Builder(copyMap(((SkeinParameterSpec)paramAlgorithmParameterSpec).getParameters()))).setKey(keyParameter2.getKey()).build();
    } else if (paramAlgorithmParameterSpec == null) {
      keyParameter1 = new KeyParameter(paramKey.getEncoded());
    } else if (gcmSpecClass != null && gcmSpecClass.isAssignableFrom(paramAlgorithmParameterSpec.getClass())) {
      try {
        Method method1 = gcmSpecClass.getDeclaredMethod("getTLen", new Class[0]);
        Method method2 = gcmSpecClass.getDeclaredMethod("getIV", new Class[0]);
        aEADParameters = new AEADParameters(keyParameter2, ((Integer)method1.invoke(paramAlgorithmParameterSpec, new Object[0])).intValue(), (byte[])method2.invoke(paramAlgorithmParameterSpec, new Object[0]));
      } catch (Exception exception) {
        throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
      } 
    } else if (!(paramAlgorithmParameterSpec instanceof PBEParameterSpec)) {
      throw new InvalidAlgorithmParameterException("unknown parameter type: " + paramAlgorithmParameterSpec.getClass().getName());
    } 
    try {
      this.macEngine.init((CipherParameters)aEADParameters);
    } catch (Exception exception) {
      throw new InvalidAlgorithmParameterException("cannot initialize MAC: " + exception.getMessage());
    } 
  }
  
  protected int engineGetMacLength() {
    return this.macEngine.getMacSize();
  }
  
  protected void engineReset() {
    this.macEngine.reset();
  }
  
  protected void engineUpdate(byte paramByte) {
    this.macEngine.update(paramByte);
  }
  
  protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.macEngine.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected byte[] engineDoFinal() {
    byte[] arrayOfByte = new byte[engineGetMacLength()];
    this.macEngine.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  private static Hashtable copyMap(Map paramMap) {
    Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
    for (Object object : paramMap.keySet())
      hashtable.put(object, paramMap.get(object)); 
    return hashtable;
  }
}
