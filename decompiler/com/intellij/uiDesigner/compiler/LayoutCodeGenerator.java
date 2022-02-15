package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;
import java.awt.Container;
import java.awt.Dimension;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;




















public abstract class LayoutCodeGenerator
{
  protected static final Method ourSetLayoutMethod = Method.getMethod("void setLayout(java.awt.LayoutManager)");
  protected static final Type ourContainerType = Type.getType(Container.class);
  protected static final Method ourAddMethod = Method.getMethod("void add(java.awt.Component,java.lang.Object)");
  protected static final Method ourAddNoConstraintMethod = Method.getMethod("java.awt.Component add(java.awt.Component)");


  
  public void generateContainerLayout(LwContainer lwContainer, GeneratorAdapter generator, int componentLocal) {}


  
  protected static void newDimensionOrNull(GeneratorAdapter generator, Dimension dimension) {
    if (dimension.width == -1 && dimension.height == -1) {
      generator.visitInsn(1);
    } else {
      
      AsmCodeGenerator.pushPropValue(generator, "java.awt.Dimension", dimension);
    } 
  }
  
  public String mapComponentClass(String componentClassName) {
    return componentClassName;
  }
  
  public abstract void generateComponentLayout(LwComponent paramLwComponent, GeneratorAdapter paramGeneratorAdapter, int paramInt1, int paramInt2);
}
