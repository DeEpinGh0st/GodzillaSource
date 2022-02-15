package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.Digest;

public class KDF1BytesGenerator extends BaseKDFBytesGenerator {
  public KDF1BytesGenerator(Digest paramDigest) {
    super(0, paramDigest);
  }
}
