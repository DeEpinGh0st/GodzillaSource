package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEKRecipient;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.bc.BcSymmetricKeyUnwrapper;

public abstract class BcKEKRecipient implements KEKRecipient {
  private SymmetricKeyUnwrapper unwrapper;
  
  public BcKEKRecipient(BcSymmetricKeyUnwrapper paramBcSymmetricKeyUnwrapper) {
    this.unwrapper = (SymmetricKeyUnwrapper)paramBcSymmetricKeyUnwrapper;
  }
  
  protected CipherParameters extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) throws CMSException {
    try {
      return CMSUtils.getBcKey(this.unwrapper.generateUnwrappedKey(paramAlgorithmIdentifier2, paramArrayOfbyte));
    } catch (OperatorException operatorException) {
      throw new CMSException("exception unwrapping key: " + operatorException.getMessage(), operatorException);
    } 
  }
}
