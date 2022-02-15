package org.bouncycastle.mozilla.jcajce;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.mozilla.SignedPublicKeyAndChallenge;

public class JcaSignedPublicKeyAndChallenge extends SignedPublicKeyAndChallenge {
  JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  private JcaSignedPublicKeyAndChallenge(SignedPublicKeyAndChallenge paramSignedPublicKeyAndChallenge, JcaJceHelper paramJcaJceHelper) {
    super(paramSignedPublicKeyAndChallenge);
    this.helper = paramJcaJceHelper;
  }
  
  public JcaSignedPublicKeyAndChallenge(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  public JcaSignedPublicKeyAndChallenge setProvider(String paramString) {
    return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, (JcaJceHelper)new NamedJcaJceHelper(paramString));
  }
  
  public JcaSignedPublicKeyAndChallenge setProvider(Provider paramProvider) {
    return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, (JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
  }
  
  public PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
    try {
      SubjectPublicKeyInfo subjectPublicKeyInfo = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded());
      AlgorithmIdentifier algorithmIdentifier = subjectPublicKeyInfo.getAlgorithm();
      KeyFactory keyFactory = this.helper.createKeyFactory(algorithmIdentifier.getAlgorithm().getId());
      return keyFactory.generatePublic(x509EncodedKeySpec);
    } catch (Exception exception) {
      throw new InvalidKeyException("error encoding public key");
    } 
  }
}
