package org.bouncycastle.pkcs.jcajce;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class JcaPKCS10CertificationRequest extends PKCS10CertificationRequest {
  private static Hashtable keyAlgorithms = new Hashtable<Object, Object>();
  
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  public JcaPKCS10CertificationRequest(CertificationRequest paramCertificationRequest) {
    super(paramCertificationRequest);
  }
  
  public JcaPKCS10CertificationRequest(byte[] paramArrayOfbyte) throws IOException {
    super(paramArrayOfbyte);
  }
  
  public JcaPKCS10CertificationRequest(PKCS10CertificationRequest paramPKCS10CertificationRequest) {
    super(paramPKCS10CertificationRequest.toASN1Structure());
  }
  
  public JcaPKCS10CertificationRequest setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public JcaPKCS10CertificationRequest setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public PublicKey getPublicKey() throws InvalidKeyException, NoSuchAlgorithmException {
    try {
      KeyFactory keyFactory;
      SubjectPublicKeyInfo subjectPublicKeyInfo = getSubjectPublicKeyInfo();
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded());
      try {
        keyFactory = this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId());
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        if (keyAlgorithms.get(subjectPublicKeyInfo.getAlgorithm().getAlgorithm()) != null) {
          String str = (String)keyAlgorithms.get(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
          keyFactory = this.helper.createKeyFactory(str);
        } else {
          throw noSuchAlgorithmException;
        } 
      } 
      return keyFactory.generatePublic(x509EncodedKeySpec);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new InvalidKeyException("error decoding public key");
    } catch (IOException iOException) {
      throw new InvalidKeyException("error extracting key encoding");
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new NoSuchAlgorithmException("cannot find provider: " + noSuchProviderException.getMessage());
    } 
  }
  
  static {
    keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
    keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
  }
}
