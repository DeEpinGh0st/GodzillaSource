package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;
import java.awt.FlowLayout;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;



















public class FlowLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private static Type ourFlowLayoutType = Type.getType(FlowLayout.class);
  private static Method ourConstructor = Method.getMethod("void <init>(int,int,int)");
  
  public void generateContainerLayout(LwContainer lwContainer, GeneratorAdapter generator, int componentLocal) {
    generator.loadLocal(componentLocal);
    
    FlowLayout flowLayout = (FlowLayout)lwContainer.getLayout();
    generator.newInstance(ourFlowLayoutType);
    generator.dup();
    generator.push(flowLayout.getAlignment());
    generator.push(flowLayout.getHgap());
    generator.push(flowLayout.getVgap());
    generator.invokeConstructor(ourFlowLayoutType, ourConstructor);
    
    generator.invokeVirtual(ourContainerType, ourSetLayoutMethod);
  }


  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    generator.invokeVirtual(ourContainerType, ourAddNoConstraintMethod);
  }
}
