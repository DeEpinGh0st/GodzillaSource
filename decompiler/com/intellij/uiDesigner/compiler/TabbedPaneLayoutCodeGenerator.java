package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.IComponent;
import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwTabbedPane;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
















public class TabbedPaneLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private final Type myTabbedPaneType = Type.getType(JTabbedPane.class);
  private final Method myAddTabMethod = Method.getMethod("void addTab(java.lang.String,javax.swing.Icon,java.awt.Component,java.lang.String)");
  private final Method mySetDisabledIconAtMethod = Method.getMethod("void setDisabledIconAt(int,javax.swing.Icon)");
  private final Method mySetEnabledAtMethod = Method.getMethod("void setEnabledAt(int,boolean)");



  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    LwTabbedPane.Constraints tabConstraints = (LwTabbedPane.Constraints)lwComponent.getCustomLayoutConstraints();
    if (tabConstraints == null) {
      throw new IllegalArgumentException("tab constraints cannot be null: " + lwComponent.getId());
    }
    AsmCodeGenerator.pushPropValue(generator, String.class.getName(), tabConstraints.myTitle);
    if (tabConstraints.myIcon == null) {
      generator.push((String)null);
    } else {
      
      AsmCodeGenerator.pushPropValue(generator, Icon.class.getName(), tabConstraints.myIcon);
    } 
    generator.loadLocal(componentLocal);
    if (tabConstraints.myToolTip == null) {
      generator.push((String)null);
    } else {
      
      AsmCodeGenerator.pushPropValue(generator, String.class.getName(), tabConstraints.myToolTip);
    } 
    generator.invokeVirtual(this.myTabbedPaneType, this.myAddTabMethod);
    
    int index = lwComponent.getParent().indexOfComponent((IComponent)lwComponent);
    if (tabConstraints.myDisabledIcon != null) {
      generator.loadLocal(parentLocal);
      generator.push(index);
      AsmCodeGenerator.pushPropValue(generator, Icon.class.getName(), tabConstraints.myDisabledIcon);
      generator.invokeVirtual(this.myTabbedPaneType, this.mySetDisabledIconAtMethod);
    } 
    if (!tabConstraints.myEnabled) {
      generator.loadLocal(parentLocal);
      generator.push(index);
      generator.push(tabConstraints.myEnabled);
      generator.invokeVirtual(this.myTabbedPaneType, this.mySetEnabledAtMethod);
    } 
  }
}
