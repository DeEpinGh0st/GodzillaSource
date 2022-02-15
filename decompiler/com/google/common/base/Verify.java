package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;


























































































@GwtCompatible
public final class Verify
{
  public static void verify(boolean expression) {
    if (!expression) {
      throw new VerifyException();
    }
  }


















  
  public static void verify(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs));
    }
  }








  
  public static void verify(boolean expression, String errorMessageTemplate, char p1) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1) }));
    }
  }








  
  public static void verify(boolean expression, String errorMessageTemplate, int p1) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1) }));
    }
  }








  
  public static void verify(boolean expression, String errorMessageTemplate, long p1) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, Object p1) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1 }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, char p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Character.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, char p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Character.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, char p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Character.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, Object p1, char p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Character.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, int p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Integer.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, int p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Integer.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, int p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Integer.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, Object p1, int p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Integer.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, long p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Long.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, long p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Long.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, long p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Long.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, Object p1, long p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Long.valueOf(p2) }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, Object p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), p2 }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, Object p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), p2 }));
    }
  }









  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, Object p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), p2 }));
    }
  }












  
  public static void verify(boolean expression, String errorMessageTemplate, Object p1, Object p2) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2 }));
    }
  }













  
  public static void verify(boolean expression, String errorMessageTemplate, Object p1, Object p2, Object p3) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3 }));
    }
  }














  
  public static void verify(boolean expression, String errorMessageTemplate, Object p1, Object p2, Object p3, Object p4) {
    if (!expression) {
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3, p4 }));
    }
  }








  
  @CanIgnoreReturnValue
  public static <T> T verifyNotNull(T reference) {
    return verifyNotNull(reference, "expected a non-null reference", new Object[0]);
  }


















  
  @CanIgnoreReturnValue
  public static <T> T verifyNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
    verify((reference != null), errorMessageTemplate, errorMessageArgs);
    return reference;
  }
}
