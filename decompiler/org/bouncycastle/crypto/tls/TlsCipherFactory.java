package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsCipherFactory {
  TlsCipher createCipher(TlsContext paramTlsContext, int paramInt1, int paramInt2) throws IOException;
}
