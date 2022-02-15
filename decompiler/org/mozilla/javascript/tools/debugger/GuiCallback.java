package org.mozilla.javascript.tools.debugger;

public interface GuiCallback {
  void updateSourceText(Dim.SourceInfo paramSourceInfo);
  
  void enterInterrupt(Dim.StackFrame paramStackFrame, String paramString1, String paramString2);
  
  boolean isGuiEventThread();
  
  void dispatchNextGuiEvent() throws InterruptedException;
}
