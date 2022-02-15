package org.bouncycastle.cms.jcajce;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;

public class JceAlgorithmIdentifierConverter {
  private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  private SecureRandom random;
  
  public JceAlgorithmIdentifierConverter setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    return this;
  }
  
  public JceAlgorithmIdentifierConverter setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    return this;
  }
  
  public AlgorithmParameters getAlgorithmParameters(AlgorithmIdentifier paramAlgorithmIdentifier) throws CMSException {
    ASN1Encodable aSN1Encodable = paramAlgorithmIdentifier.getParameters();
    if (aSN1Encodable == null)
      return null; 
    try {
      AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(paramAlgorithmIdentifier.getAlgorithm());
      CMSUtils.loadParameters(algorithmParameters, paramAlgorithmIdentifier.getParameters());
      return algorithmParameters;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CMSException("can't find parameters for algorithm", noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new CMSException("can't find provider for algorithm", noSuchProviderException);
    } 
  }
}
