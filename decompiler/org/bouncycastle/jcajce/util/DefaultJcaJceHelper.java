package org.bouncycastle.jcajce.util;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

public class DefaultJcaJceHelper implements JcaJceHelper {
  public Cipher createCipher(String paramString) throws NoSuchAlgorithmException, NoSuchPaddingException {
    return Cipher.getInstance(paramString);
  }
  
  public Mac createMac(String paramString) throws NoSuchAlgorithmException {
    return Mac.getInstance(paramString);
  }
  
  public KeyAgreement createKeyAgreement(String paramString) throws NoSuchAlgorithmException {
    return KeyAgreement.getInstance(paramString);
  }
  
  public AlgorithmParameterGenerator createAlgorithmParameterGenerator(String paramString) throws NoSuchAlgorithmException {
    return AlgorithmParameterGenerator.getInstance(paramString);
  }
  
  public AlgorithmParameters createAlgorithmParameters(String paramString) throws NoSuchAlgorithmException {
    return AlgorithmParameters.getInstance(paramString);
  }
  
  public KeyGenerator createKeyGenerator(String paramString) throws NoSuchAlgorithmException {
    return KeyGenerator.getInstance(paramString);
  }
  
  public KeyFactory createKeyFactory(String paramString) throws NoSuchAlgorithmException {
    return KeyFactory.getInstance(paramString);
  }
  
  public SecretKeyFactory createSecretKeyFactory(String paramString) throws NoSuchAlgorithmException {
    return SecretKeyFactory.getInstance(paramString);
  }
  
  public KeyPairGenerator createKeyPairGenerator(String paramString) throws NoSuchAlgorithmException {
    return KeyPairGenerator.getInstance(paramString);
  }
  
  public MessageDigest createDigest(String paramString) throws NoSuchAlgorithmException {
    return MessageDigest.getInstance(paramString);
  }
  
  public Signature createSignature(String paramString) throws NoSuchAlgorithmException {
    return Signature.getInstance(paramString);
  }
  
  public CertificateFactory createCertificateFactory(String paramString) throws CertificateException {
    return CertificateFactory.getInstance(paramString);
  }
  
  public SecureRandom createSecureRandom(String paramString) throws NoSuchAlgorithmException {
    return SecureRandom.getInstance(paramString);
  }
}
