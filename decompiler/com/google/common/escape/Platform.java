package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;






















@GwtCompatible(emulated = true)
final class Platform
{
  static char[] charBufferFromThreadLocal() {
    return DEST_TL.get();
  }





  
  private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>()
    {
      protected char[] initialValue()
      {
        return new char[1024];
      }
    };
}
