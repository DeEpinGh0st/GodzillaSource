package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;

@Beta
@GwtIncompatible
public interface LineProcessor<T> {
  @CanIgnoreReturnValue
  boolean processLine(String paramString) throws IOException;
  
  T getResult();
}
