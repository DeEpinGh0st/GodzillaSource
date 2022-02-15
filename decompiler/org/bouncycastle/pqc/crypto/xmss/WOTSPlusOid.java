package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class WOTSPlusOid implements XMSSOid {
  private static final Map<String, WOTSPlusOid> oidLookupTable;
  
  private final int oid;
  
  private final String stringRepresentation;
  
  private WOTSPlusOid(int paramInt, String paramString) {
    this.oid = paramInt;
    this.stringRepresentation = paramString;
  }
  
  protected static WOTSPlusOid lookup(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (paramString == null)
      throw new NullPointerException("algorithmName == null"); 
    return oidLookupTable.get(createKey(paramString, paramInt1, paramInt2, paramInt3));
  }
  
  private static String createKey(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (paramString == null)
      throw new NullPointerException("algorithmName == null"); 
    return paramString + "-" + paramInt1 + "-" + paramInt2 + "-" + paramInt3;
  }
  
  public int getOid() {
    return this.oid;
  }
  
  public String toString() {
    return this.stringRepresentation;
  }
  
  static {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(createKey("SHA-256", 32, 16, 67), new WOTSPlusOid(16777217, "WOTSP_SHA2-256_W16"));
    hashMap.put(createKey("SHA-512", 64, 16, 131), new WOTSPlusOid(33554434, "WOTSP_SHA2-512_W16"));
    hashMap.put(createKey("SHAKE128", 32, 16, 67), new WOTSPlusOid(50331651, "WOTSP_SHAKE128_W16"));
    hashMap.put(createKey("SHAKE256", 64, 16, 131), new WOTSPlusOid(67108868, "WOTSP_SHAKE256_W16"));
    oidLookupTable = Collections.unmodifiableMap(hashMap);
  }
}
