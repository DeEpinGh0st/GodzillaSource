package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;

public abstract class BcContentVerifierProviderBuilder {
  protected BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;
  
  public ContentVerifierProvider build(final X509CertificateHolder certHolder) throws OperatorCreationException {
    return new ContentVerifierProvider() {
        public boolean hasAssociatedCertificate() {
          return true;
        }
        
        public X509CertificateHolder getAssociatedCertificate() {
          return certHolder;
        }
        
        public ContentVerifier get(AlgorithmIdentifier param1AlgorithmIdentifier) throws OperatorCreationException {
          try {
            AsymmetricKeyParameter asymmetricKeyParameter = BcContentVerifierProviderBuilder.this.extractKeyParameters(certHolder.getSubjectPublicKeyInfo());
            BcSignerOutputStream bcSignerOutputStream = BcContentVerifierProviderBuilder.this.createSignatureStream(param1AlgorithmIdentifier, asymmetricKeyParameter);
            return new BcContentVerifierProviderBuilder.SigVerifier(param1AlgorithmIdentifier, bcSignerOutputStream);
          } catch (IOException iOException) {
            throw new OperatorCreationException("exception on setup: " + iOException, iOException);
          } 
        }
      };
  }
  
  public ContentVerifierProvider build(final AsymmetricKeyParameter publicKey) throws OperatorCreationException {
    return new ContentVerifierProvider() {
        public boolean hasAssociatedCertificate() {
          return false;
        }
        
        public X509CertificateHolder getAssociatedCertificate() {
          return null;
        }
        
        public ContentVerifier get(AlgorithmIdentifier param1AlgorithmIdentifier) throws OperatorCreationException {
          BcSignerOutputStream bcSignerOutputStream = BcContentVerifierProviderBuilder.this.createSignatureStream(param1AlgorithmIdentifier, publicKey);
          return new BcContentVerifierProviderBuilder.SigVerifier(param1AlgorithmIdentifier, bcSignerOutputStream);
        }
      };
  }
  
  private BcSignerOutputStream createSignatureStream(AlgorithmIdentifier paramAlgorithmIdentifier, AsymmetricKeyParameter paramAsymmetricKeyParameter) throws OperatorCreationException {
    Signer signer = createSigner(paramAlgorithmIdentifier);
    signer.init(false, (CipherParameters)paramAsymmetricKeyParameter);
    return new BcSignerOutputStream(signer);
  }
  
  protected abstract AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException;
  
  protected abstract Signer createSigner(AlgorithmIdentifier paramAlgorithmIdentifier) throws OperatorCreationException;
  
  private class SigVerifier implements ContentVerifier {
    private BcSignerOutputStream stream;
    
    private AlgorithmIdentifier algorithm;
    
    SigVerifier(AlgorithmIdentifier param1AlgorithmIdentifier, BcSignerOutputStream param1BcSignerOutputStream) {
      this.algorithm = param1AlgorithmIdentifier;
      this.stream = param1BcSignerOutputStream;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithm;
    }
    
    public OutputStream getOutputStream() {
      if (this.stream == null)
        throw new IllegalStateException("verifier not initialised"); 
      return this.stream;
    }
    
    public boolean verify(byte[] param1ArrayOfbyte) {
      return this.stream.verify(param1ArrayOfbyte);
    }
  }
}
