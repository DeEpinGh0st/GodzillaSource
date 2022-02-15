package org.bouncycastle.openssl.jcajce;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;

public class JcaPEMKeyConverter {
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  private static final Map algorithms = new HashMap<Object, Object>();
  
  public JcaPEMKeyConverter setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public JcaPEMKeyConverter setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public KeyPair getKeyPair(PEMKeyPair paramPEMKeyPair) throws PEMException {
    try {
      KeyFactory keyFactory = getKeyFactory(paramPEMKeyPair.getPrivateKeyInfo().getPrivateKeyAlgorithm());
      return new KeyPair(keyFactory.generatePublic(new X509EncodedKeySpec(paramPEMKeyPair.getPublicKeyInfo().getEncoded())), keyFactory.generatePrivate(new PKCS8EncodedKeySpec(paramPEMKeyPair.getPrivateKeyInfo().getEncoded())));
    } catch (Exception exception) {
      throw new PEMException("unable to convert key pair: " + exception.getMessage(), exception);
    } 
  }
  
  public PublicKey getPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws PEMException {
    try {
      KeyFactory keyFactory = getKeyFactory(paramSubjectPublicKeyInfo.getAlgorithm());
      return keyFactory.generatePublic(new X509EncodedKeySpec(paramSubjectPublicKeyInfo.getEncoded()));
    } catch (Exception exception) {
      throw new PEMException("unable to convert key pair: " + exception.getMessage(), exception);
    } 
  }
  
  public PrivateKey getPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws PEMException {
    try {
      KeyFactory keyFactory = getKeyFactory(paramPrivateKeyInfo.getPrivateKeyAlgorithm());
      return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(paramPrivateKeyInfo.getEncoded()));
    } catch (Exception exception) {
      throw new PEMException("unable to convert key pair: " + exception.getMessage(), exception);
    } 
  }
  
  private KeyFactory getKeyFactory(AlgorithmIdentifier paramAlgorithmIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramAlgorithmIdentifier.getAlgorithm();
    String str = (String)algorithms.get(aSN1ObjectIdentifier);
    if (str == null)
      str = aSN1ObjectIdentifier.getId(); 
    try {
      return this.helper.createKeyFactory(str);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      if (str.equals("ECDSA"))
        return this.helper.createKeyFactory("EC"); 
      throw noSuchAlgorithmException;
    } 
  }
  
  static {
    algorithms.put(X9ObjectIdentifiers.id_ecPublicKey, "ECDSA");
    algorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
    algorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
  }
}
