package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;

class DigestInputBuffer extends ByteArrayOutputStream {
  void updateDigest(Digest paramDigest) {
    paramDigest.update(this.buf, 0, this.count);
  }
}
