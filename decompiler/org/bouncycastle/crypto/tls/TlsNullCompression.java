package org.bouncycastle.crypto.tls;

import java.io.OutputStream;

public class TlsNullCompression implements TlsCompression {
  public OutputStream compress(OutputStream paramOutputStream) {
    return paramOutputStream;
  }
  
  public OutputStream decompress(OutputStream paramOutputStream) {
    return paramOutputStream;
  }
}
