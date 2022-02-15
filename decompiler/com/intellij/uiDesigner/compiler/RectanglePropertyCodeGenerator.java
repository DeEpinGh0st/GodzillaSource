package com.intellij.uiDesigner.compiler;

import java.awt.Rectangle;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;


















public class RectanglePropertyCodeGenerator
  extends PropertyCodeGenerator
{
  private static Type myRectangleType = Type.getType(Rectangle.class);
  private static Method myInitMethod = Method.getMethod("void <init>(int,int,int,int)");
  
  public void generatePushValue(GeneratorAdapter generator, Object value) {
    Rectangle rc = (Rectangle)value;
    generator.newInstance(myRectangleType);
    generator.dup();
    generator.push(rc.x);
    generator.push(rc.y);
    generator.push(rc.width);
    generator.push(rc.height);
    generator.invokeConstructor(myRectangleType, myInitMethod);
  }
}
