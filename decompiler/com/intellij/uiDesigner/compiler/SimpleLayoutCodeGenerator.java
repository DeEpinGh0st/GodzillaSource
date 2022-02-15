package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;




















public class SimpleLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private final Type myLayoutType;
  private static Method ourConstructor = Method.getMethod("void <init>(int,int)");
  
  public SimpleLayoutCodeGenerator(Type layoutType) {
    this.myLayoutType = layoutType;
  }
  
  public void generateContainerLayout(LwContainer lwContainer, GeneratorAdapter generator, int componentLocal) {
    generator.loadLocal(componentLocal);
    
    generator.newInstance(this.myLayoutType);
    generator.dup();
    generator.push(Utils.getHGap(lwContainer.getLayout()));
    generator.push(Utils.getVGap(lwContainer.getLayout()));
    
    generator.invokeConstructor(this.myLayoutType, ourConstructor);
    
    generator.invokeVirtual(ourContainerType, ourSetLayoutMethod);
  }



  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    generator.push((String)lwComponent.getCustomLayoutConstraints());
    generator.invokeVirtual(ourContainerType, ourAddMethod);
  }
}
