package com.intellij.uiDesigner.compiler;

import java.awt.Insets;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;







public class InsetsPropertyCodeGenerator
  extends PropertyCodeGenerator
{
  private final Type myInsetsType = Type.getType(Insets.class);
  
  public void generatePushValue(GeneratorAdapter generator, Object value) {
    Insets insets = (Insets)value;
    generator.newInstance(this.myInsetsType);
    generator.dup();
    generator.push(insets.top);
    generator.push(insets.left);
    generator.push(insets.bottom);
    generator.push(insets.right);
    generator.invokeConstructor(this.myInsetsType, Method.getMethod("void <init>(int,int,int,int)"));
  }
}
