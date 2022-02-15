package org.bouncycastle.crypto.tls;

import java.io.OutputStream;

public interface TlsCompression {
  OutputStream compress(OutputStream paramOutputStream);
  
  OutputStream decompress(OutputStream paramOutputStream);
}
