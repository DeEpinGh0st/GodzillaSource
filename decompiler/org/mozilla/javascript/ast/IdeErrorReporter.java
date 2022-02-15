package org.mozilla.javascript.ast;

import org.mozilla.javascript.ErrorReporter;

public interface IdeErrorReporter extends ErrorReporter {
  void warning(String paramString1, String paramString2, int paramInt1, int paramInt2);
  
  void error(String paramString1, String paramString2, int paramInt1, int paramInt2);
}
