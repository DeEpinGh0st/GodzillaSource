package org.bouncycastle.operator.jcajce;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.operator.GenericKey;

class OperatorUtils {
  static Key getJceKey(GenericKey paramGenericKey) {
    if (paramGenericKey.getRepresentation() instanceof Key)
      return (Key)paramGenericKey.getRepresentation(); 
    if (paramGenericKey.getRepresentation() instanceof byte[])
      return new SecretKeySpec((byte[])paramGenericKey.getRepresentation(), "ENC"); 
    throw new IllegalArgumentException("unknown generic key type");
  }
}
