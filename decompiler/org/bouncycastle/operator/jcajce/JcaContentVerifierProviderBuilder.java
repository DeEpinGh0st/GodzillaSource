package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaContentVerifierProviderBuilder {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  public JcaContentVerifierProviderBuilder setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JcaContentVerifierProviderBuilder setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public ContentVerifierProvider build(X509CertificateHolder paramX509CertificateHolder) throws OperatorCreationException, CertificateException {
    return build(this.helper.convertCertificate(paramX509CertificateHolder));
  }
  
  public ContentVerifierProvider build(final X509Certificate certificate) throws OperatorCreationException {
    final JcaX509CertificateHolder certHolder;
    try {
      jcaX509CertificateHolder = new JcaX509CertificateHolder(certificate);
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new OperatorCreationException("cannot process certificate: " + certificateEncodingException.getMessage(), certificateEncodingException);
    } 
    return new ContentVerifierProvider() {
        private JcaContentVerifierProviderBuilder.SignatureOutputStream stream;
        
        public boolean hasAssociatedCertificate() {
          return true;
        }
        
        public X509CertificateHolder getAssociatedCertificate() {
          return certHolder;
        }
        
        public ContentVerifier get(AlgorithmIdentifier param1AlgorithmIdentifier) throws OperatorCreationException {
          try {
            Signature signature1 = JcaContentVerifierProviderBuilder.this.helper.createSignature(param1AlgorithmIdentifier);
            signature1.initVerify(certificate.getPublicKey());
            this.stream = new JcaContentVerifierProviderBuilder.SignatureOutputStream(signature1);
          } catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException("exception on setup: " + generalSecurityException, generalSecurityException);
          } 
          Signature signature = JcaContentVerifierProviderBuilder.this.createRawSig(param1AlgorithmIdentifier, certificate.getPublicKey());
          return (signature != null) ? new JcaContentVerifierProviderBuilder.RawSigVerifier(param1AlgorithmIdentifier, this.stream, signature) : new JcaContentVerifierProviderBuilder.SigVerifier(param1AlgorithmIdentifier, this.stream);
        }
      };
  }
  
  public ContentVerifierProvider build(final PublicKey publicKey) throws OperatorCreationException {
    return new ContentVerifierProvider() {
        public boolean hasAssociatedCertificate() {
          return false;
        }
        
        public X509CertificateHolder getAssociatedCertificate() {
          return null;
        }
        
        public ContentVerifier get(AlgorithmIdentifier param1AlgorithmIdentifier) throws OperatorCreationException {
          JcaContentVerifierProviderBuilder.SignatureOutputStream signatureOutputStream = JcaContentVerifierProviderBuilder.this.createSignatureStream(param1AlgorithmIdentifier, publicKey);
          Signature signature = JcaContentVerifierProviderBuilder.this.createRawSig(param1AlgorithmIdentifier, publicKey);
          return (signature != null) ? new JcaContentVerifierProviderBuilder.RawSigVerifier(param1AlgorithmIdentifier, signatureOutputStream, signature) : new JcaContentVerifierProviderBuilder.SigVerifier(param1AlgorithmIdentifier, signatureOutputStream);
        }
      };
  }
  
  public ContentVerifierProvider build(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws OperatorCreationException {
    return build(this.helper.convertPublicKey(paramSubjectPublicKeyInfo));
  }
  
  private SignatureOutputStream createSignatureStream(AlgorithmIdentifier paramAlgorithmIdentifier, PublicKey paramPublicKey) throws OperatorCreationException {
    try {
      Signature signature = this.helper.createSignature(paramAlgorithmIdentifier);
      signature.initVerify(paramPublicKey);
      return new SignatureOutputStream(signature);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new OperatorCreationException("exception on setup: " + generalSecurityException, generalSecurityException);
    } 
  }
  
  private Signature createRawSig(AlgorithmIdentifier paramAlgorithmIdentifier, PublicKey paramPublicKey) {
    Signature signature;
    try {
      signature = this.helper.createRawSignature(paramAlgorithmIdentifier);
      if (signature != null)
        signature.initVerify(paramPublicKey); 
    } catch (Exception exception) {
      signature = null;
    } 
    return signature;
  }
  
  private class RawSigVerifier extends SigVerifier implements RawContentVerifier {
    private Signature rawSignature;
    
    RawSigVerifier(AlgorithmIdentifier param1AlgorithmIdentifier, JcaContentVerifierProviderBuilder.SignatureOutputStream param1SignatureOutputStream, Signature param1Signature) {
      super(param1AlgorithmIdentifier, param1SignatureOutputStream);
      this.rawSignature = param1Signature;
    }
    
    public boolean verify(byte[] param1ArrayOfbyte) {
      try {
        return super.verify(param1ArrayOfbyte);
      } finally {
        try {
          this.rawSignature.verify(param1ArrayOfbyte);
        } catch (Exception exception) {}
      } 
    }
    
    public boolean verify(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2) {
      try {
        this.rawSignature.update(param1ArrayOfbyte1);
        return this.rawSignature.verify(param1ArrayOfbyte2);
      } catch (SignatureException signatureException) {
        throw new RuntimeOperatorException("exception obtaining raw signature: " + signatureException.getMessage(), signatureException);
      } finally {
        try {
          this.stream.verify(param1ArrayOfbyte2);
        } catch (Exception exception) {}
      } 
    }
  }
  
  private class SigVerifier implements ContentVerifier {
    private AlgorithmIdentifier algorithm;
    
    protected JcaContentVerifierProviderBuilder.SignatureOutputStream stream;
    
    SigVerifier(AlgorithmIdentifier param1AlgorithmIdentifier, JcaContentVerifierProviderBuilder.SignatureOutputStream param1SignatureOutputStream) {
      this.algorithm = param1AlgorithmIdentifier;
      this.stream = param1SignatureOutputStream;
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
      try {
        return this.stream.verify(param1ArrayOfbyte);
      } catch (SignatureException signatureException) {
        throw new RuntimeOperatorException("exception obtaining signature: " + signatureException.getMessage(), signatureException);
      } 
    }
  }
  
  private class SignatureOutputStream extends OutputStream {
    private Signature sig;
    
    SignatureOutputStream(Signature param1Signature) {
      this.sig = param1Signature;
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      try {
        this.sig.update(param1ArrayOfbyte, param1Int1, param1Int2);
      } catch (SignatureException signatureException) {
        throw new OperatorStreamException("exception in content signer: " + signatureException.getMessage(), signatureException);
      } 
    }
    
    public void write(byte[] param1ArrayOfbyte) throws IOException {
      try {
        this.sig.update(param1ArrayOfbyte);
      } catch (SignatureException signatureException) {
        throw new OperatorStreamException("exception in content signer: " + signatureException.getMessage(), signatureException);
      } 
    }
    
    public void write(int param1Int) throws IOException {
      try {
        this.sig.update((byte)param1Int);
      } catch (SignatureException signatureException) {
        throw new OperatorStreamException("exception in content signer: " + signatureException.getMessage(), signatureException);
      } 
    }
    
    boolean verify(byte[] param1ArrayOfbyte) throws SignatureException {
      return this.sig.verify(param1ArrayOfbyte);
    }
  }
}
