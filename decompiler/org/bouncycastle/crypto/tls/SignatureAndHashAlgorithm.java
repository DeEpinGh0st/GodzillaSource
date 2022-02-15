package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SignatureAndHashAlgorithm {
  protected short hash;
  
  protected short signature;
  
  public SignatureAndHashAlgorithm(short paramShort1, short paramShort2) {
    if (!TlsUtils.isValidUint8(paramShort1))
      throw new IllegalArgumentException("'hash' should be a uint8"); 
    if (!TlsUtils.isValidUint8(paramShort2))
      throw new IllegalArgumentException("'signature' should be a uint8"); 
    if (paramShort2 == 0)
      throw new IllegalArgumentException("'signature' MUST NOT be \"anonymous\""); 
    this.hash = paramShort1;
    this.signature = paramShort2;
  }
  
  public short getHash() {
    return this.hash;
  }
  
  public short getSignature() {
    return this.signature;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof SignatureAndHashAlgorithm))
      return false; 
    SignatureAndHashAlgorithm signatureAndHashAlgorithm = (SignatureAndHashAlgorithm)paramObject;
    return (signatureAndHashAlgorithm.getHash() == getHash() && signatureAndHashAlgorithm.getSignature() == getSignature());
  }
  
  public int hashCode() {
    return getHash() << 16 | getSignature();
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeUint8(getHash(), paramOutputStream);
    TlsUtils.writeUint8(getSignature(), paramOutputStream);
  }
  
  public static SignatureAndHashAlgorithm parse(InputStream paramInputStream) throws IOException {
    short s1 = TlsUtils.readUint8(paramInputStream);
    short s2 = TlsUtils.readUint8(paramInputStream);
    return new SignatureAndHashAlgorithm(s1, s2);
  }
}
