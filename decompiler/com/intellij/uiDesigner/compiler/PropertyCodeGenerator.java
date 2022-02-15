package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwIntrospectedProperty;
import org.objectweb.asm.commons.GeneratorAdapter;






public abstract class PropertyCodeGenerator
{
  public abstract void generatePushValue(GeneratorAdapter paramGeneratorAdapter, Object paramObject);
  
  public boolean generateCustomSetValue(LwComponent lwComponent, Class componentClass, LwIntrospectedProperty property, GeneratorAdapter generator, int componentLocal, String formClassName) {
    return false;
  }
  
  public void generateClassStart(AsmCodeGenerator.FormClassVisitor visitor, String name, ClassLoader loader) {}
  
  public void generateClassEnd(AsmCodeGenerator.FormClassVisitor visitor) {}
}
