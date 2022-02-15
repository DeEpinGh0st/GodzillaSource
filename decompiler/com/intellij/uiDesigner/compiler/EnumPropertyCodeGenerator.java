package com.intellij.uiDesigner.compiler;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;


















public class EnumPropertyCodeGenerator
  extends PropertyCodeGenerator
{
  public void generatePushValue(GeneratorAdapter generator, Object value) {
    Type enumType = Type.getType(value.getClass());
    generator.getStatic(enumType, value.toString(), enumType);
  }
}
