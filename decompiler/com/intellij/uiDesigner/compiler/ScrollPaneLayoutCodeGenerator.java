package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwComponent;
import javax.swing.JScrollPane;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;



















public class ScrollPaneLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private final Type myScrollPaneType = Type.getType(JScrollPane.class);
  private final Method mySetViewportViewMethod = Method.getMethod("void setViewportView(java.awt.Component)");



  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    generator.invokeVirtual(this.myScrollPaneType, this.mySetViewportViewMethod);
  }
}
