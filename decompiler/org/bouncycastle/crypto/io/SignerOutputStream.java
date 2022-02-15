package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Signer;

public class SignerOutputStream extends OutputStream {
  protected Signer signer;
  
  public SignerOutputStream(Signer paramSigner) {
    this.signer = paramSigner;
  }
  
  public void write(int paramInt) throws IOException {
    this.signer.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.signer.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public Signer getSigner() {
    return this.signer;
  }
}
