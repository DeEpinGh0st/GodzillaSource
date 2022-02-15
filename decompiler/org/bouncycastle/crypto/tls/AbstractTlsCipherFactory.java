package org.bouncycastle.crypto.tls;

import java.io.IOException;

public class AbstractTlsCipherFactory implements TlsCipherFactory {
  public TlsCipher createCipher(TlsContext paramTlsContext, int paramInt1, int paramInt2) throws IOException {
    throw new TlsFatalAlert((short)80);
  }
}
