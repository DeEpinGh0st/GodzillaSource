package org.bouncycastle.crypto.tls;

public class AlertLevel {
  public static final short warning = 1;
  
  public static final short fatal = 2;
  
  public static String getName(short paramShort) {
    switch (paramShort) {
      case 1:
        return "warning";
      case 2:
        return "fatal";
    } 
    return "UNKNOWN";
  }
  
  public static String getText(short paramShort) {
    return getName(paramShort) + "(" + paramShort + ")";
  }
}
