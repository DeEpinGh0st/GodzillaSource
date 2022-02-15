package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BasicTlsPSKIdentity implements TlsPSKIdentity {
  protected byte[] identity;
  
  protected byte[] psk;
  
  public BasicTlsPSKIdentity(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.identity = Arrays.clone(paramArrayOfbyte1);
    this.psk = Arrays.clone(paramArrayOfbyte2);
  }
  
  public BasicTlsPSKIdentity(String paramString, byte[] paramArrayOfbyte) {
    this.identity = Strings.toUTF8ByteArray(paramString);
    this.psk = Arrays.clone(paramArrayOfbyte);
  }
  
  public void skipIdentityHint() {}
  
  public void notifyIdentityHint(byte[] paramArrayOfbyte) {}
  
  public byte[] getPSKIdentity() {
    return this.identity;
  }
  
  public byte[] getPSK() {
    return this.psk;
  }
}
