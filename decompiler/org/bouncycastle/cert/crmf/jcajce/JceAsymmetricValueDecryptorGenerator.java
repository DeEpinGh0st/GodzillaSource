package org.bouncycastle.cert.crmf.jcajce;

import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.ValueDecryptorGenerator;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;

public class JceAsymmetricValueDecryptorGenerator implements ValueDecryptorGenerator {
  private PrivateKey recipientKey;
  
  private CRMFHelper helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private Provider provider = null;
  
  private String providerName = null;
  
  public JceAsymmetricValueDecryptorGenerator(PrivateKey paramPrivateKey) {
    this.recipientKey = paramPrivateKey;
  }
  
  public JceAsymmetricValueDecryptorGenerator setProvider(Provider paramProvider) {
    this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    this.provider = paramProvider;
    this.providerName = null;
    return this;
  }
  
  public JceAsymmetricValueDecryptorGenerator setProvider(String paramString) {
    this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    this.provider = null;
    this.providerName = paramString;
    return this;
  }
  
  private Key extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) throws CRMFException {
    try {
      JceAsymmetricKeyUnwrapper jceAsymmetricKeyUnwrapper = new JceAsymmetricKeyUnwrapper(paramAlgorithmIdentifier1, this.recipientKey);
      if (this.provider != null)
        jceAsymmetricKeyUnwrapper.setProvider(this.provider); 
      if (this.providerName != null)
        jceAsymmetricKeyUnwrapper.setProvider(this.providerName); 
      return new SecretKeySpec((byte[])jceAsymmetricKeyUnwrapper.generateUnwrappedKey(paramAlgorithmIdentifier2, paramArrayOfbyte).getRepresentation(), paramAlgorithmIdentifier2.getAlgorithm().getId());
    } catch (OperatorException operatorException) {
      throw new CRMFException("key invalid in message: " + operatorException.getMessage(), operatorException);
    } 
  }
  
  public InputDecryptor getValueDecryptor(AlgorithmIdentifier paramAlgorithmIdentifier1, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] paramArrayOfbyte) throws CRMFException {
    Key key = extractSecretKey(paramAlgorithmIdentifier1, contentEncryptionAlgorithm, paramArrayOfbyte);
    final Cipher dataCipher = this.helper.createContentCipher(key, contentEncryptionAlgorithm);
    return new InputDecryptor() {
        public AlgorithmIdentifier getAlgorithmIdentifier() {
          return contentEncryptionAlgorithm;
        }
        
        public InputStream getInputStream(InputStream param1InputStream) {
          return new CipherInputStream(param1InputStream, dataCipher);
        }
      };
  }
}
