package org.bouncycastle.pqc.jcajce.interfaces;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;

public interface StateAwareSignature {
  void initVerify(PublicKey paramPublicKey) throws InvalidKeyException;
  
  void initVerify(Certificate paramCertificate) throws InvalidKeyException;
  
  void initSign(PrivateKey paramPrivateKey) throws InvalidKeyException;
  
  void initSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException;
  
  byte[] sign() throws SignatureException;
  
  int sign(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException;
  
  boolean verify(byte[] paramArrayOfbyte) throws SignatureException;
  
  boolean verify(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException;
  
  void update(byte paramByte) throws SignatureException;
  
  void update(byte[] paramArrayOfbyte) throws SignatureException;
  
  void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException;
  
  void update(ByteBuffer paramByteBuffer) throws SignatureException;
  
  String getAlgorithm();
  
  PrivateKey getUpdatedPrivateKey();
}
