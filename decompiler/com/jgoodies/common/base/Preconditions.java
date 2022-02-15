package com.jgoodies.common.base;

































































public final class Preconditions
{
  public static void checkArgument(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }




















  
  public static void checkArgument(boolean expression, String messageFormat, Object... messageArgs) {
    if (!expression) {
      throw new IllegalArgumentException(format(messageFormat, messageArgs));
    }
  }






















  
  public static <T> T checkNotNull(T reference, String message) {
    if (reference == null) {
      throw new NullPointerException(message);
    }
    return reference;
  }























  
  public static <T> T checkNotNull(T reference, String messageFormat, Object... messageArgs) {
    if (reference == null) {
      throw new NullPointerException(format(messageFormat, messageArgs));
    }
    return reference;
  }




















  
  public static void checkState(boolean expression, String message) {
    if (!expression) {
      throw new IllegalStateException(message);
    }
  }























  
  public static void checkState(boolean expression, String messageFormat, Object... messageArgs) {
    if (!expression) {
      throw new IllegalStateException(format(messageFormat, messageArgs));
    }
  }






















  
  public static String checkNotBlank(String str, String message) {
    checkNotNull(str, message);
    checkArgument(Strings.isNotBlank(str), message);
    return str;
  }


























  
  public static String checkNotBlank(String str, String messageFormat, Object... messageArgs) {
    checkNotNull(str, messageFormat, messageArgs);
    checkArgument(Strings.isNotBlank(str), messageFormat, messageArgs);
    return str;
  }



  
  static String format(String messageFormat, Object... messageArgs) {
    return String.format(messageFormat, messageArgs);
  }
}
