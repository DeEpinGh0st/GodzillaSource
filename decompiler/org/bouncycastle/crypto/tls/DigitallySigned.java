package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DigitallySigned {
  protected SignatureAndHashAlgorithm algorithm;
  
  protected byte[] signature;
  
  public DigitallySigned(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'signature' cannot be null"); 
    this.algorithm = paramSignatureAndHashAlgorithm;
    this.signature = paramArrayOfbyte;
  }
  
  public SignatureAndHashAlgorithm getAlgorithm() {
    return this.algorithm;
  }
  
  public byte[] getSignature() {
    return this.signature;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    if (this.algorithm != null)
      this.algorithm.encode(paramOutputStream); 
    TlsUtils.writeOpaque16(this.signature, paramOutputStream);
  }
  
  public static DigitallySigned parse(TlsContext paramTlsContext, InputStream paramInputStream) throws IOException {
    SignatureAndHashAlgorithm signatureAndHashAlgorithm = null;
    if (TlsUtils.isTLSv12(paramTlsContext))
      signatureAndHashAlgorithm = SignatureAndHashAlgorithm.parse(paramInputStream); 
    byte[] arrayOfByte = TlsUtils.readOpaque16(paramInputStream);
    return new DigitallySigned(signatureAndHashAlgorithm, arrayOfByte);
  }
}
