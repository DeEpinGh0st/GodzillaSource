package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.operator.OperatorCreationException;

public class BcDSAContentSignerBuilder extends BcContentSignerBuilder {
  public BcDSAContentSignerBuilder(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    super(paramAlgorithmIdentifier1, paramAlgorithmIdentifier2);
  }
  
  protected Signer createSigner(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) throws OperatorCreationException {
    ExtendedDigest extendedDigest = this.digestProvider.get(paramAlgorithmIdentifier2);
    return (Signer)new DSADigestSigner((DSA)new DSASigner(), (Digest)extendedDigest);
  }
}
