package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;


















public class GridLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private static final Method myInitConstraintsMethod = Method.getMethod("void <init> (int,int,int,int,int,int,int,int,java.awt.Dimension,java.awt.Dimension,java.awt.Dimension)");
  private static final Method myInitConstraintsIndentMethod = Method.getMethod("void <init> (int,int,int,int,int,int,int,int,java.awt.Dimension,java.awt.Dimension,java.awt.Dimension,int)");
  private static final Method myInitConstraintsIndentParentMethod = Method.getMethod("void <init> (int,int,int,int,int,int,int,int,java.awt.Dimension,java.awt.Dimension,java.awt.Dimension,int,boolean)");
  private static final Method ourGridLayoutManagerConstructor = Method.getMethod("void <init> (int,int,java.awt.Insets,int,int,boolean,boolean)");
  private static final Type myGridLayoutManagerType = Type.getType(GridLayoutManager.class);
  private static final Type myGridConstraintsType = Type.getType(GridConstraints.class);
  
  public static GridLayoutCodeGenerator INSTANCE = new GridLayoutCodeGenerator();
  
  public void generateContainerLayout(LwContainer lwContainer, GeneratorAdapter generator, int componentLocal) {
    if (lwContainer.isGrid()) {
      
      generator.loadLocal(componentLocal);

      
      GridLayoutManager layout = (GridLayoutManager)lwContainer.getLayout();
      
      generator.newInstance(myGridLayoutManagerType);
      generator.dup();
      generator.push(layout.getRowCount());
      generator.push(layout.getColumnCount());
      AsmCodeGenerator.pushPropValue(generator, "java.awt.Insets", layout.getMargin());
      generator.push(layout.getHGap());
      generator.push(layout.getVGap());
      generator.push(layout.isSameSizeHorizontally());
      generator.push(layout.isSameSizeVertically());
      generator.invokeConstructor(myGridLayoutManagerType, ourGridLayoutManagerConstructor);
      
      generator.invokeVirtual(ourContainerType, ourSetLayoutMethod);
    } 
  }



  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    addNewGridConstraints(generator, lwComponent);
    generator.invokeVirtual(ourContainerType, ourAddMethod);
  }
  
  private static void addNewGridConstraints(GeneratorAdapter generator, LwComponent lwComponent) {
    GridConstraints constraints = lwComponent.getConstraints();
    
    generator.newInstance(myGridConstraintsType);
    generator.dup();
    generator.push(constraints.getRow());
    generator.push(constraints.getColumn());
    generator.push(constraints.getRowSpan());
    generator.push(constraints.getColSpan());
    generator.push(constraints.getAnchor());
    generator.push(constraints.getFill());
    generator.push(constraints.getHSizePolicy());
    generator.push(constraints.getVSizePolicy());
    newDimensionOrNull(generator, constraints.myMinimumSize);
    newDimensionOrNull(generator, constraints.myPreferredSize);
    newDimensionOrNull(generator, constraints.myMaximumSize);
    
    if (constraints.isUseParentLayout()) {
      generator.push(constraints.getIndent());
      generator.push(constraints.isUseParentLayout());
      generator.invokeConstructor(myGridConstraintsType, myInitConstraintsIndentParentMethod);
    }
    else if (constraints.getIndent() != 0) {
      generator.push(constraints.getIndent());
      generator.invokeConstructor(myGridConstraintsType, myInitConstraintsIndentMethod);
    } else {
      
      generator.invokeConstructor(myGridConstraintsType, myInitConstraintsMethod);
    } 
  }
}
