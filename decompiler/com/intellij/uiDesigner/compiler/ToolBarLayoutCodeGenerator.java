package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwComponent;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;


















public class ToolBarLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private static final Method ourAddMethod = Method.getMethod("java.awt.Component add(java.awt.Component)");



  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    generator.invokeVirtual(ourContainerType, ourAddMethod);
  }
}
