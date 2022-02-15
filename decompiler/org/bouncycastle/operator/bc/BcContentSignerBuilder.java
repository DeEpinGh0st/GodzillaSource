package org.bouncycastle.operator.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RuntimeOperatorException;

public abstract class BcContentSignerBuilder {
  private SecureRandom random;
  
  private AlgorithmIdentifier sigAlgId;
  
  private AlgorithmIdentifier digAlgId;
  
  protected BcDigestProvider digestProvider;
  
  public BcContentSignerBuilder(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    this.sigAlgId = paramAlgorithmIdentifier1;
    this.digAlgId = paramAlgorithmIdentifier2;
    this.digestProvider = BcDefaultDigestProvider.INSTANCE;
  }
  
  public BcContentSignerBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public ContentSigner build(AsymmetricKeyParameter paramAsymmetricKeyParameter) throws OperatorCreationException {
    final Signer sig = createSigner(this.sigAlgId, this.digAlgId);
    if (this.random != null) {
      signer.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)paramAsymmetricKeyParameter, this.random));
    } else {
      signer.init(true, (CipherParameters)paramAsymmetricKeyParameter);
    } 
    return new ContentSigner() {
        private BcSignerOutputStream stream = new BcSignerOutputStream(sig);
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
          return BcContentSignerBuilder.this.sigAlgId;
        }
        
        public OutputStream getOutputStream() {
          return this.stream;
        }
        
        public byte[] getSignature() {
          try {
            return this.stream.getSignature();
          } catch (CryptoException cryptoException) {
            throw new RuntimeOperatorException("exception obtaining signature: " + cryptoException.getMessage(), cryptoException);
          } 
        }
      };
  }
  
  protected abstract Signer createSigner(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) throws OperatorCreationException;
}
