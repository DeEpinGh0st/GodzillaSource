package org.bouncycastle.operator.bc;

import java.security.Key;
import org.bouncycastle.operator.GenericKey;

class OperatorUtils {
  static byte[] getKeyBytes(GenericKey paramGenericKey) {
    if (paramGenericKey.getRepresentation() instanceof Key)
      return ((Key)paramGenericKey.getRepresentation()).getEncoded(); 
    if (paramGenericKey.getRepresentation() instanceof byte[])
      return (byte[])paramGenericKey.getRepresentation(); 
    throw new IllegalArgumentException("unknown generic key type");
  }
}
