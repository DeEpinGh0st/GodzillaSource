package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.BufferingOutputStream;

public class BufferingContentSigner implements ContentSigner {
  private final ContentSigner contentSigner;
  
  private final OutputStream output;
  
  public BufferingContentSigner(ContentSigner paramContentSigner) {
    this.contentSigner = paramContentSigner;
    this.output = (OutputStream)new BufferingOutputStream(paramContentSigner.getOutputStream());
  }
  
  public BufferingContentSigner(ContentSigner paramContentSigner, int paramInt) {
    this.contentSigner = paramContentSigner;
    this.output = (OutputStream)new BufferingOutputStream(paramContentSigner.getOutputStream(), paramInt);
  }
  
  public AlgorithmIdentifier getAlgorithmIdentifier() {
    return this.contentSigner.getAlgorithmIdentifier();
  }
  
  public OutputStream getOutputStream() {
    return this.output;
  }
  
  public byte[] getSignature() {
    return this.contentSigner.getSignature();
  }
}
