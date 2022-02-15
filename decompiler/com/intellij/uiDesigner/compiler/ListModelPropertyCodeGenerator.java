package com.intellij.uiDesigner.compiler;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;


















public class ListModelPropertyCodeGenerator
  extends PropertyCodeGenerator
{
  private final Type myListModelType;
  private static final Method ourInitMethod = Method.getMethod("void <init>()");
  private static final Method ourAddElementMethod = Method.getMethod("void addElement(java.lang.Object)");
  
  public ListModelPropertyCodeGenerator(Class aClass) {
    this.myListModelType = Type.getType(aClass);
  }
  
  public void generatePushValue(GeneratorAdapter generator, Object value) {
    String[] items = (String[])value;
    int listModelLocal = generator.newLocal(this.myListModelType);
    
    generator.newInstance(this.myListModelType);
    generator.dup();
    generator.invokeConstructor(this.myListModelType, ourInitMethod);
    generator.storeLocal(listModelLocal);
    
    for (int i = 0; i < items.length; i++) {
      generator.loadLocal(listModelLocal);
      generator.push(items[i]);
      generator.invokeVirtual(this.myListModelType, ourAddElementMethod);
    } 
    
    generator.loadLocal(listModelLocal);
  }
}
