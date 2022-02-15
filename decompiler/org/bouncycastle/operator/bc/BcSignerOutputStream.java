package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;

public class BcSignerOutputStream extends OutputStream {
  private Signer sig;
  
  BcSignerOutputStream(Signer paramSigner) {
    this.sig = paramSigner;
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.sig.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void write(byte[] paramArrayOfbyte) throws IOException {
    this.sig.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void write(int paramInt) throws IOException {
    this.sig.update((byte)paramInt);
  }
  
  byte[] getSignature() throws CryptoException {
    return this.sig.generateSignature();
  }
  
  boolean verify(byte[] paramArrayOfbyte) {
    return this.sig.verifySignature(paramArrayOfbyte);
  }
}
