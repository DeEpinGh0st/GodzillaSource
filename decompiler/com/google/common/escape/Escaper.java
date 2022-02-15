package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;













































































@GwtCompatible
public abstract class Escaper
{
  private final Function<String, String> asFunction = new Function<String, String>()
    {
      public String apply(String from)
      {
        return Escaper.this.escape(from);
      }
    };

  
  public final Function<String, String> asFunction() {
    return this.asFunction;
  }
  
  public abstract String escape(String paramString);
}
