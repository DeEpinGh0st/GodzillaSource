package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;

public class BcDSAContentVerifierProviderBuilder extends BcContentVerifierProviderBuilder {
  private DigestAlgorithmIdentifierFinder digestAlgorithmFinder;
  
  public BcDSAContentVerifierProviderBuilder(DigestAlgorithmIdentifierFinder paramDigestAlgorithmIdentifierFinder) {
    this.digestAlgorithmFinder = paramDigestAlgorithmIdentifierFinder;
  }
  
  protected Signer createSigner(AlgorithmIdentifier paramAlgorithmIdentifier) throws OperatorCreationException {
    AlgorithmIdentifier algorithmIdentifier = this.digestAlgorithmFinder.find(paramAlgorithmIdentifier);
    ExtendedDigest extendedDigest = this.digestProvider.get(algorithmIdentifier);
    return (Signer)new DSADigestSigner((DSA)new DSASigner(), (Digest)extendedDigest);
  }
  
  protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    return PublicKeyFactory.createKey(paramSubjectPublicKeyInfo);
  }
}
