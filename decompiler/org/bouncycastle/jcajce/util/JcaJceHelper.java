package org.bouncycastle.jcajce.util;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

public interface JcaJceHelper {
  Cipher createCipher(String paramString) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
  
  Mac createMac(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  KeyAgreement createKeyAgreement(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  AlgorithmParameterGenerator createAlgorithmParameterGenerator(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  AlgorithmParameters createAlgorithmParameters(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  KeyGenerator createKeyGenerator(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  KeyFactory createKeyFactory(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  SecretKeyFactory createSecretKeyFactory(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  KeyPairGenerator createKeyPairGenerator(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  MessageDigest createDigest(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  Signature createSignature(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
  
  CertificateFactory createCertificateFactory(String paramString) throws NoSuchProviderException, CertificateException;
  
  SecureRandom createSecureRandom(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException;
}
