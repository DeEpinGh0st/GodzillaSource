package org.mozilla.javascript;

public interface ErrorReporter {
  void warning(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2);
  
  void error(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2);
  
  EvaluatorException runtimeError(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2);
}
