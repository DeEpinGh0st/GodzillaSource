package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.bc.BcRSAAsymmetricKeyUnwrapper;

public abstract class BcKeyTransRecipient implements KeyTransRecipient {
  private AsymmetricKeyParameter recipientKey;
  
  public BcKeyTransRecipient(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    this.recipientKey = paramAsymmetricKeyParameter;
  }
  
  protected CipherParameters extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) throws CMSException {
    BcRSAAsymmetricKeyUnwrapper bcRSAAsymmetricKeyUnwrapper = new BcRSAAsymmetricKeyUnwrapper(paramAlgorithmIdentifier1, this.recipientKey);
    try {
      return CMSUtils.getBcKey(bcRSAAsymmetricKeyUnwrapper.generateUnwrappedKey(paramAlgorithmIdentifier2, paramArrayOfbyte));
    } catch (OperatorException operatorException) {
      throw new CMSException("exception unwrapping key: " + operatorException.getMessage(), operatorException);
    } 
  }
}
