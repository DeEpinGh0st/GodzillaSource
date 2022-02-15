package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;

class DefaultEACHelper extends EACHelper {
  protected Signature createSignature(String paramString) throws NoSuchAlgorithmException {
    return Signature.getInstance(paramString);
  }
}
