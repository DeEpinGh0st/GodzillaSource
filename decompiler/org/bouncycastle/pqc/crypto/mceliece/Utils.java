package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;

class Utils {
  static Digest getDigest(String paramString) {
    if (paramString.equals("SHA-1"))
      return (Digest)new SHA1Digest(); 
    if (paramString.equals("SHA-224"))
      return (Digest)new SHA224Digest(); 
    if (paramString.equals("SHA-256"))
      return (Digest)new SHA256Digest(); 
    if (paramString.equals("SHA-384"))
      return (Digest)new SHA384Digest(); 
    if (paramString.equals("SHA-512"))
      return (Digest)new SHA512Digest(); 
    throw new IllegalArgumentException("unrecognised digest algorithm: " + paramString);
  }
}
