package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwComponent;
import javax.swing.JSplitPane;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;



















public class SplitPaneLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private final Type mySplitPaneType = Type.getType(JSplitPane.class);
  private final Method mySetLeftMethod = Method.getMethod("void setLeftComponent(java.awt.Component)");
  private final Method mySetRightMethod = Method.getMethod("void setRightComponent(java.awt.Component)");



  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    if ("left".equals(lwComponent.getCustomLayoutConstraints())) {
      generator.invokeVirtual(this.mySplitPaneType, this.mySetLeftMethod);
    } else {
      
      generator.invokeVirtual(this.mySplitPaneType, this.mySetRightMethod);
    } 
  }
}
