package org.fife.rsta.ac.js.completion;

public interface JSCompletion extends JSCompletionUI {
  String getLookupName();
  
  String getType(boolean paramBoolean);
  
  String getEnclosingClassName(boolean paramBoolean);
}
