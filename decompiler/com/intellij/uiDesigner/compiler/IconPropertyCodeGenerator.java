package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.IconDescriptor;
import javax.swing.ImageIcon;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;


















public class IconPropertyCodeGenerator
  extends PropertyCodeGenerator
{
  private static final Type ourImageIconType = Type.getType(ImageIcon.class);
  private static final Method ourInitMethod = Method.getMethod("void <init>(java.net.URL)");
  private static final Method ourGetResourceMethod = Method.getMethod("java.net.URL getResource(java.lang.String)");
  private static final Method ourGetClassMethod = new Method("getClass", "()Ljava/lang/Class;");
  private static final Type ourObjectType = Type.getType(Object.class);
  private static final Type ourClassType = Type.getType(Class.class);
  
  public void generatePushValue(GeneratorAdapter generator, Object value) {
    IconDescriptor descriptor = (IconDescriptor)value;
    generator.newInstance(ourImageIconType);
    generator.dup();
    
    generator.loadThis();
    generator.invokeVirtual(ourObjectType, ourGetClassMethod);
    generator.push("/" + descriptor.getIconPath());
    generator.invokeVirtual(ourClassType, ourGetResourceMethod);
    
    generator.invokeConstructor(ourImageIconType, ourInitMethod);
  }
}
