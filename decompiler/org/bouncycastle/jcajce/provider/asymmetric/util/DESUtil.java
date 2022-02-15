package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.util.Strings;

public class DESUtil {
  private static final Set<String> des = new HashSet<String>();
  
  public static boolean isDES(String paramString) {
    String str = Strings.toUpperCase(paramString);
    return des.contains(str);
  }
  
  public static void setOddParity(byte[] paramArrayOfbyte) {
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      byte b1 = paramArrayOfbyte[b];
      paramArrayOfbyte[b] = (byte)(b1 & 0xFE | (b1 >> 1 ^ b1 >> 2 ^ b1 >> 3 ^ b1 >> 4 ^ b1 >> 5 ^ b1 >> 6 ^ b1 >> 7 ^ 0x1) & 0x1);
    } 
  }
  
  static {
    des.add("DES");
    des.add("DESEDE");
    des.add(OIWObjectIdentifiers.desCBC.getId());
    des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
    des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
    des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
  }
}
