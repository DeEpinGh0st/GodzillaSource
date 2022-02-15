package com.google.common.base;

import com.google.common.annotations.GwtCompatible;





















@GwtCompatible
abstract class CommonPattern
{
  public abstract CommonMatcher matcher(CharSequence paramCharSequence);
  
  public abstract String pattern();
  
  public abstract int flags();
  
  public abstract String toString();
  
  public static CommonPattern compile(String pattern) {
    return Platform.compilePattern(pattern);
  }
  
  public static boolean isPcreLike() {
    return Platform.patternCompilerIsPcreLike();
  }
}
