package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwRootContainer;

public interface NestedFormLoader {
  LwRootContainer loadForm(String paramString) throws Exception;
  
  String getClassToBindName(LwRootContainer paramLwRootContainer);
}
