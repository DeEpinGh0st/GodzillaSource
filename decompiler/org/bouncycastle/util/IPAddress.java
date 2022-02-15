package org.bouncycastle.util;

public class IPAddress {
  public static boolean isValid(String paramString) {
    return (isValidIPv4(paramString) || isValidIPv6(paramString));
  }
  
  public static boolean isValidWithNetMask(String paramString) {
    return (isValidIPv4WithNetmask(paramString) || isValidIPv6WithNetmask(paramString));
  }
  
  public static boolean isValidIPv4(String paramString) {
    if (paramString.length() == 0)
      return false; 
    byte b = 0;
    String str = paramString + ".";
    int j = 0;
    int i;
    while (j < str.length() && (i = str.indexOf('.', j)) > j) {
      int k;
      if (b == 4)
        return false; 
      try {
        k = Integer.parseInt(str.substring(j, i));
      } catch (NumberFormatException numberFormatException) {
        return false;
      } 
      if (k < 0 || k > 255)
        return false; 
      j = i + 1;
      b++;
    } 
    return (b == 4);
  }
  
  public static boolean isValidIPv4WithNetmask(String paramString) {
    int i = paramString.indexOf("/");
    String str = paramString.substring(i + 1);
    return (i > 0 && isValidIPv4(paramString.substring(0, i)) && (isValidIPv4(str) || isMaskValue(str, 32)));
  }
  
  public static boolean isValidIPv6WithNetmask(String paramString) {
    int i = paramString.indexOf("/");
    String str = paramString.substring(i + 1);
    return (i > 0 && isValidIPv6(paramString.substring(0, i)) && (isValidIPv6(str) || isMaskValue(str, 128)));
  }
  
  private static boolean isMaskValue(String paramString, int paramInt) {
    try {
      int i = Integer.parseInt(paramString);
      return (i >= 0 && i <= paramInt);
    } catch (NumberFormatException numberFormatException) {
      return false;
    } 
  }
  
  public static boolean isValidIPv6(String paramString) {
    if (paramString.length() == 0)
      return false; 
    byte b = 0;
    String str = paramString + ":";
    boolean bool = false;
    int j = 0;
    int i;
    while (j < str.length() && (i = str.indexOf(':', j)) >= j) {
      if (b == 8)
        return false; 
      if (j != i) {
        String str1 = str.substring(j, i);
        if (i == str.length() - 1 && str1.indexOf('.') > 0) {
          if (!isValidIPv4(str1))
            return false; 
          b++;
        } else {
          int k;
          try {
            k = Integer.parseInt(str.substring(j, i), 16);
          } catch (NumberFormatException numberFormatException) {
            return false;
          } 
          if (k < 0 || k > 65535)
            return false; 
        } 
      } else {
        if (i != 1 && i != str.length() - 1 && bool)
          return false; 
        bool = true;
      } 
      j = i + 1;
      b++;
    } 
    return (b == 8 || bool);
  }
}
