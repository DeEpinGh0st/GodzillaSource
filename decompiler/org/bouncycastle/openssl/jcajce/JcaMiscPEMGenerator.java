package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PEMEncryptor;

public class JcaMiscPEMGenerator extends MiscPEMGenerator {
  private Object obj;
  
  private String algorithm;
  
  private char[] password;
  
  private SecureRandom random;
  
  private Provider provider;
  
  public JcaMiscPEMGenerator(Object paramObject) throws IOException {
    super(convertObject(paramObject));
  }
  
  public JcaMiscPEMGenerator(Object paramObject, PEMEncryptor paramPEMEncryptor) throws IOException {
    super(convertObject(paramObject), paramPEMEncryptor);
  }
  
  private static Object convertObject(Object paramObject) throws IOException {
    if (paramObject instanceof X509Certificate)
      try {
        return new JcaX509CertificateHolder((X509Certificate)paramObject);
      } catch (CertificateEncodingException certificateEncodingException) {
        throw new IllegalArgumentException("Cannot encode object: " + certificateEncodingException.toString());
      }  
    if (paramObject instanceof X509CRL)
      try {
        return new JcaX509CRLHolder((X509CRL)paramObject);
      } catch (CRLException cRLException) {
        throw new IllegalArgumentException("Cannot encode object: " + cRLException.toString());
      }  
    return (paramObject instanceof KeyPair) ? convertObject(((KeyPair)paramObject).getPrivate()) : ((paramObject instanceof java.security.PrivateKey) ? PrivateKeyInfo.getInstance(((Key)paramObject).getEncoded()) : ((paramObject instanceof PublicKey) ? SubjectPublicKeyInfo.getInstance(((PublicKey)paramObject).getEncoded()) : paramObject));
  }
}
