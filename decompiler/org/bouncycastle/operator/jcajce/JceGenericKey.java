package org.bouncycastle.operator.jcajce;

import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;

public class JceGenericKey extends GenericKey {
  private static Object getRepresentation(Key paramKey) {
    byte[] arrayOfByte = paramKey.getEncoded();
    return (arrayOfByte != null) ? arrayOfByte : paramKey;
  }
  
  public JceGenericKey(AlgorithmIdentifier paramAlgorithmIdentifier, Key paramKey) {
    super(paramAlgorithmIdentifier, getRepresentation(paramKey));
  }
}
