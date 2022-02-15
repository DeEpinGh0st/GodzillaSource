package org.bouncycastle.crypto.tls;

public class CertChainType {
  public static final short individual_certs = 0;
  
  public static final short pkipath = 1;
  
  public static boolean isValid(short paramShort) {
    return (paramShort >= 0 && paramShort <= 1);
  }
}
