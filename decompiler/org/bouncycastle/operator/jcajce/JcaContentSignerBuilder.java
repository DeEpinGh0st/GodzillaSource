package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaContentSignerBuilder {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private SecureRandom random;
  
  private String signatureAlgorithm;
  
  private AlgorithmIdentifier sigAlgId;
  
  public JcaContentSignerBuilder(String paramString) {
    this.signatureAlgorithm = paramString;
    this.sigAlgId = (new DefaultSignatureAlgorithmIdentifierFinder()).find(paramString);
  }
  
  public JcaContentSignerBuilder setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JcaContentSignerBuilder setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public JcaContentSignerBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public ContentSigner build(PrivateKey paramPrivateKey) throws OperatorCreationException {
    try {
      final Signature sig = this.helper.createSignature(this.sigAlgId);
      final AlgorithmIdentifier signatureAlgId = this.sigAlgId;
      if (this.random != null) {
        signature.initSign(paramPrivateKey, this.random);
      } else {
        signature.initSign(paramPrivateKey);
      } 
      return new ContentSigner() {
          private JcaContentSignerBuilder.SignatureOutputStream stream = new JcaContentSignerBuilder.SignatureOutputStream(sig);
          
          public AlgorithmIdentifier getAlgorithmIdentifier() {
            return signatureAlgId;
          }
          
          public OutputStream getOutputStream() {
            return this.stream;
          }
          
          public byte[] getSignature() {
            try {
              return this.stream.getSignature();
            } catch (SignatureException signatureException) {
              throw new RuntimeOperatorException("exception obtaining signature: " + signatureException.getMessage(), signatureException);
            } 
          }
        };
    } catch (GeneralSecurityException generalSecurityException) {
      throw new OperatorCreationException("cannot create signer: " + generalSecurityException.getMessage(), generalSecurityException);
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
    
    byte[] getSignature() throws SignatureException {
      return this.sig.sign();
    }
  }
}
