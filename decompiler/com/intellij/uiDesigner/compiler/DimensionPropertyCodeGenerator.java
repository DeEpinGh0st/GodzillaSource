package com.intellij.uiDesigner.compiler;

import java.awt.Dimension;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;




public class DimensionPropertyCodeGenerator
  extends PropertyCodeGenerator
{
  private static final Type myDimensionType = Type.getType(Dimension.class);
  private static final Method myInitMethod = Method.getMethod("void <init>(int,int)");
  
  public void generatePushValue(GeneratorAdapter generator, Object value) {
    Dimension dimension = (Dimension)value;
    generator.newInstance(myDimensionType);
    generator.dup();
    generator.push(dimension.width);
    generator.push(dimension.height);
    generator.invokeConstructor(myDimensionType, myInitMethod);
  }
}
