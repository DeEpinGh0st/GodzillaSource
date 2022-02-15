package org.bouncycastle.jcajce.util;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;

public class ProviderJcaJceHelper implements JcaJceHelper {
  protected final Provider provider;
  
  public ProviderJcaJceHelper(Provider paramProvider) {
    this.provider = paramProvider;
  }
  
  public Cipher createCipher(String paramString) throws NoSuchAlgorithmException, NoSuchPaddingException {
    return Cipher.getInstance(paramString, this.provider);
  }
  
  public Mac createMac(String paramString) throws NoSuchAlgorithmException {
    return Mac.getInstance(paramString, this.provider);
  }
  
  public KeyAgreement createKeyAgreement(String paramString) throws NoSuchAlgorithmException {
    return KeyAgreement.getInstance(paramString, this.provider);
  }
  
  public AlgorithmParameterGenerator createAlgorithmParameterGenerator(String paramString) throws NoSuchAlgorithmException {
    return AlgorithmParameterGenerator.getInstance(paramString, this.provider);
  }
  
  public AlgorithmParameters createAlgorithmParameters(String paramString) throws NoSuchAlgorithmException {
    return AlgorithmParameters.getInstance(paramString, this.provider);
  }
  
  public KeyGenerator createKeyGenerator(String paramString) throws NoSuchAlgorithmException {
    return KeyGenerator.getInstance(paramString, this.provider);
  }
  
  public KeyFactory createKeyFactory(String paramString) throws NoSuchAlgorithmException {
    return KeyFactory.getInstance(paramString, this.provider);
  }
  
  public SecretKeyFactory createSecretKeyFactory(String paramString) throws NoSuchAlgorithmException {
    return SecretKeyFactory.getInstance(paramString, this.provider);
  }
  
  public KeyPairGenerator createKeyPairGenerator(String paramString) throws NoSuchAlgorithmException {
    return KeyPairGenerator.getInstance(paramString, this.provider);
  }
  
  public MessageDigest createDigest(String paramString) throws NoSuchAlgorithmException {
    return MessageDigest.getInstance(paramString, this.provider);
  }
  
  public Signature createSignature(String paramString) throws NoSuchAlgorithmException {
    return Signature.getInstance(paramString, this.provider);
  }
  
  public CertificateFactory createCertificateFactory(String paramString) throws CertificateException {
    return CertificateFactory.getInstance(paramString, this.provider);
  }
  
  public SecureRandom createSecureRandom(String paramString) throws NoSuchAlgorithmException {
    return SecureRandom.getInstance(paramString, this.provider);
  }
}
