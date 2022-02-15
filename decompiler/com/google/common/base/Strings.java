package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.logging.Level;
import java.util.logging.Logger;
































@GwtCompatible
public final class Strings
{
  public static String nullToEmpty(String string) {
    return Platform.nullToEmpty(string);
  }






  
  public static String emptyToNull(String string) {
    return Platform.emptyToNull(string);
  }











  
  public static boolean isNullOrEmpty(String string) {
    return Platform.stringIsNullOrEmpty(string);
  }


















  
  public static String padStart(String string, int minLength, char padChar) {
    Preconditions.checkNotNull(string);
    if (string.length() >= minLength) {
      return string;
    }
    StringBuilder sb = new StringBuilder(minLength);
    for (int i = string.length(); i < minLength; i++) {
      sb.append(padChar);
    }
    sb.append(string);
    return sb.toString();
  }


















  
  public static String padEnd(String string, int minLength, char padChar) {
    Preconditions.checkNotNull(string);
    if (string.length() >= minLength) {
      return string;
    }
    StringBuilder sb = new StringBuilder(minLength);
    sb.append(string);
    for (int i = string.length(); i < minLength; i++) {
      sb.append(padChar);
    }
    return sb.toString();
  }










  
  public static String repeat(String string, int count) {
    Preconditions.checkNotNull(string);
    
    if (count <= 1) {
      Preconditions.checkArgument((count >= 0), "invalid count: %s", count);
      return (count == 0) ? "" : string;
    } 

    
    int len = string.length();
    long longSize = len * count;
    int size = (int)longSize;
    if (size != longSize) {
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
    }
    
    char[] array = new char[size];
    string.getChars(0, len, array, 0);
    int n;
    for (n = len; n < size - n; n <<= 1) {
      System.arraycopy(array, 0, array, n, n);
    }
    System.arraycopy(array, 0, array, n, size - n);
    return new String(array);
  }







  
  public static String commonPrefix(CharSequence a, CharSequence b) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);
    
    int maxPrefixLength = Math.min(a.length(), b.length());
    int p = 0;
    while (p < maxPrefixLength && a.charAt(p) == b.charAt(p)) {
      p++;
    }
    if (validSurrogatePairAt(a, p - 1) || validSurrogatePairAt(b, p - 1)) {
      p--;
    }
    return a.subSequence(0, p).toString();
  }







  
  public static String commonSuffix(CharSequence a, CharSequence b) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);
    
    int maxSuffixLength = Math.min(a.length(), b.length());
    int s = 0;
    while (s < maxSuffixLength && a.charAt(a.length() - s - 1) == b.charAt(b.length() - s - 1)) {
      s++;
    }
    if (validSurrogatePairAt(a, a.length() - s - 1) || 
      validSurrogatePairAt(b, b.length() - s - 1)) {
      s--;
    }
    return a.subSequence(a.length() - s, a.length()).toString();
  }




  
  @VisibleForTesting
  static boolean validSurrogatePairAt(CharSequence string, int index) {
    return (index >= 0 && index <= string
      .length() - 2 && 
      Character.isHighSurrogate(string.charAt(index)) && 
      Character.isLowSurrogate(string.charAt(index + 1)));
  }


































  
  public static String lenientFormat(String template, Object... args) {
    template = String.valueOf(template);
    
    if (args == null) {
      args = new Object[] { "(Object[])null" };
    } else {
      for (int j = 0; j < args.length; j++) {
        args[j] = lenientToString(args[j]);
      }
    } 

    
    StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
    int templateStart = 0;
    int i = 0;
    while (i < args.length) {
      int placeholderStart = template.indexOf("%s", templateStart);
      if (placeholderStart == -1) {
        break;
      }
      builder.append(template, templateStart, placeholderStart);
      builder.append(args[i++]);
      templateStart = placeholderStart + 2;
    } 
    builder.append(template, templateStart, template.length());

    
    if (i < args.length) {
      builder.append(" [");
      builder.append(args[i++]);
      while (i < args.length) {
        builder.append(", ");
        builder.append(args[i++]);
      } 
      builder.append(']');
    } 
    
    return builder.toString();
  }
  
  private static String lenientToString(Object o) {
    try {
      return String.valueOf(o);
    } catch (Exception e) {

      
      String objectToString = o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
      
      Logger.getLogger("com.google.common.base.Strings")
        .log(Level.WARNING, "Exception during lenientFormat for " + objectToString, e);
      return "<" + objectToString + " threw " + e.getClass().getName() + ">";
    } 
  }
}
