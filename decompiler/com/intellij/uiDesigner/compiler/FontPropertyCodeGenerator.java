package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.FontDescriptor;
import com.intellij.uiDesigner.lw.IComponent;
import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwIntrospectedProperty;
import java.awt.Font;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;


















public class FontPropertyCodeGenerator
  extends PropertyCodeGenerator
{
  private static final Type ourFontType = Type.getType(Font.class);
  private static final Type ourUIManagerType = Type.getType("Ljavax/swing/UIManager;");
  private static final Type ourObjectType = Type.getType(Object.class);
  private static final Type ourStringType = Type.getType(String.class);
  
  private static final Method ourInitMethod = Method.getMethod("void <init>(java.lang.String,int,int)");
  private static final Method ourUIManagerGetFontMethod = new Method("getFont", ourFontType, new Type[] { ourObjectType });
  private static final Method ourGetNameMethod = new Method("getName", ourStringType, new Type[0]);
  private static final Method ourGetSizeMethod = new Method("getSize", Type.INT_TYPE, new Type[0]);
  private static final Method ourGetStyleMethod = new Method("getStyle", Type.INT_TYPE, new Type[0]);




  
  public boolean generateCustomSetValue(LwComponent lwComponent, Class componentClass, LwIntrospectedProperty property, GeneratorAdapter generator, int componentLocal, String formClassName) {
    FontDescriptor descriptor = (FontDescriptor)property.getPropertyValue((IComponent)lwComponent);
    if (descriptor.isFixedFont() && !descriptor.isFullyDefinedFont()) {
      generator.loadLocal(componentLocal);
      generatePushFont(generator, componentLocal, lwComponent, descriptor, property.getReadMethodName());
      
      Method setFontMethod = new Method(property.getWriteMethodName(), Type.VOID_TYPE, new Type[] { ourFontType });
      Type componentType = AsmCodeGenerator.typeFromClassName(lwComponent.getComponentClassName());
      generator.invokeVirtual(componentType, setFontMethod);
      return true;
    } 
    return false;
  }

  
  public static void generatePushFont(GeneratorAdapter generator, int componentLocal, LwComponent lwComponent, FontDescriptor descriptor, String readMethodName) {
    int fontLocal = generator.newLocal(ourFontType);
    
    generator.loadLocal(componentLocal);
    Type componentType = AsmCodeGenerator.typeFromClassName(lwComponent.getComponentClassName());
    Method getFontMethod = new Method(readMethodName, ourFontType, new Type[0]);
    generator.invokeVirtual(componentType, getFontMethod);
    generator.storeLocal(fontLocal);
    
    generator.newInstance(ourFontType);
    generator.dup();
    if (descriptor.getFontName() != null) {
      generator.push(descriptor.getFontName());
    } else {
      
      generator.loadLocal(fontLocal);
      generator.invokeVirtual(ourFontType, ourGetNameMethod);
    } 
    
    if (descriptor.getFontStyle() >= 0) {
      generator.push(descriptor.getFontStyle());
    } else {
      
      generator.loadLocal(fontLocal);
      generator.invokeVirtual(ourFontType, ourGetStyleMethod);
    } 
    
    if (descriptor.getFontSize() >= 0) {
      generator.push(descriptor.getFontSize());
    } else {
      
      generator.loadLocal(fontLocal);
      generator.invokeVirtual(ourFontType, ourGetSizeMethod);
    } 
    generator.invokeConstructor(ourFontType, ourInitMethod);
  }
  
  public void generatePushValue(GeneratorAdapter generator, Object value) {
    FontDescriptor descriptor = (FontDescriptor)value;
    if (descriptor.isFixedFont()) {
      if (!descriptor.isFullyDefinedFont()) throw new IllegalStateException("Unexpected font state"); 
      generator.newInstance(ourFontType);
      generator.dup();
      generator.push(descriptor.getFontName());
      generator.push(descriptor.getFontStyle());
      generator.push(descriptor.getFontSize());
      generator.invokeConstructor(ourFontType, ourInitMethod);
    }
    else if (descriptor.getSwingFont() != null) {
      generator.push(descriptor.getSwingFont());
      generator.invokeStatic(ourUIManagerType, ourUIManagerGetFontMethod);
    } else {
      
      throw new IllegalStateException("Unknown font type");
    } 
  }
}
