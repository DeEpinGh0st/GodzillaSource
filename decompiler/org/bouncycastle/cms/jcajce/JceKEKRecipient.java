package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.Provider;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEKRecipient;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public abstract class JceKEKRecipient implements KEKRecipient {
  private SecretKey recipientKey;
  
  protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  protected EnvelopedDataHelper contentHelper = this.helper;
  
  protected boolean validateKeySize = false;
  
  public JceKEKRecipient(SecretKey paramSecretKey) {
    this.recipientKey = paramSecretKey;
  }
  
  public JceKEKRecipient setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKEKRecipient setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKEKRecipient setContentProvider(Provider paramProvider) {
    this.contentHelper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    return this;
  }
  
  public JceKEKRecipient setContentProvider(String paramString) {
    this.contentHelper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    return this;
  }
  
  public JceKEKRecipient setKeySizeValidation(boolean paramBoolean) {
    this.validateKeySize = paramBoolean;
    return this;
  }
  
  protected Key extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) throws CMSException {
    SymmetricKeyUnwrapper symmetricKeyUnwrapper = this.helper.createSymmetricUnwrapper(paramAlgorithmIdentifier1, this.recipientKey);
    try {
      Key key = this.helper.getJceKey(paramAlgorithmIdentifier2.getAlgorithm(), symmetricKeyUnwrapper.generateUnwrappedKey(paramAlgorithmIdentifier2, paramArrayOfbyte));
      if (this.validateKeySize)
        this.helper.keySizeCheck(paramAlgorithmIdentifier2, key); 
      return key;
    } catch (OperatorException operatorException) {
      throw new CMSException("exception unwrapping key: " + operatorException.getMessage(), operatorException);
    } 
  }
}
