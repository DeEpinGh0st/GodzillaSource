package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

class DefaultEACHelper implements EACHelper {
  public KeyFactory createKeyFactory(String paramString) throws NoSuchAlgorithmException {
    return KeyFactory.getInstance(paramString);
  }
}
