package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.core.Spacer;
import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;














public class GridBagLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private static Type ourGridBagLayoutType = Type.getType(GridBagLayout.class);
  private static Type ourGridBagConstraintsType = Type.getType(GridBagConstraints.class);
  private static Method ourDefaultConstructor = Method.getMethod("void <init> ()");
  
  private static Type myPanelType = Type.getType(JPanel.class);
  
  public String mapComponentClass(String componentClassName) {
    if (componentClassName.equals(Spacer.class.getName())) {
      return JPanel.class.getName();
    }
    return super.mapComponentClass(componentClassName);
  }
  
  public void generateContainerLayout(LwContainer lwContainer, GeneratorAdapter generator, int componentLocal) {
    generator.loadLocal(componentLocal);
    
    generator.newInstance(ourGridBagLayoutType);
    generator.dup();
    generator.invokeConstructor(ourGridBagLayoutType, ourDefaultConstructor);
    
    generator.invokeVirtual(ourContainerType, ourSetLayoutMethod);
  }
  
  private void generateFillerPanel(GeneratorAdapter generator, int parentLocal, GridBagConverter.Result result) {
    int panelLocal = generator.newLocal(myPanelType);
    
    generator.newInstance(myPanelType);
    generator.dup();
    generator.invokeConstructor(myPanelType, ourDefaultConstructor);
    generator.storeLocal(panelLocal);
    
    generateConversionResult(generator, result, panelLocal, parentLocal);
  }




  
  public void generateComponentLayout(LwComponent component, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    GridBagConstraints gbc;
    if (component.getCustomLayoutConstraints() instanceof GridBagConstraints) {
      gbc = (GridBagConstraints)component.getCustomLayoutConstraints();
    } else {
      
      gbc = new GridBagConstraints();
    } 
    
    GridBagConverter.constraintsToGridBag(component.getConstraints(), gbc);
    
    generateGridBagConstraints(generator, gbc, componentLocal, parentLocal);
  }

  
  private static void generateConversionResult(GeneratorAdapter generator, GridBagConverter.Result result, int componentLocal, int parentLocal) {
    checkSetSize(generator, componentLocal, "setMinimumSize", result.minimumSize);
    checkSetSize(generator, componentLocal, "setPreferredSize", result.preferredSize);
    checkSetSize(generator, componentLocal, "setMaximumSize", result.maximumSize);
    
    generateGridBagConstraints(generator, result.constraints, componentLocal, parentLocal);
  }

  
  private static void generateGridBagConstraints(GeneratorAdapter generator, GridBagConstraints constraints, int componentLocal, int parentLocal) {
    int gbcLocal = generator.newLocal(ourGridBagConstraintsType);
    
    generator.newInstance(ourGridBagConstraintsType);
    generator.dup();
    generator.invokeConstructor(ourGridBagConstraintsType, ourDefaultConstructor);
    generator.storeLocal(gbcLocal);
    
    GridBagConstraints defaults = new GridBagConstraints();
    if (defaults.gridx != constraints.gridx) {
      setIntField(generator, gbcLocal, "gridx", constraints.gridx);
    }
    if (defaults.gridy != constraints.gridy) {
      setIntField(generator, gbcLocal, "gridy", constraints.gridy);
    }
    if (defaults.gridwidth != constraints.gridwidth) {
      setIntField(generator, gbcLocal, "gridwidth", constraints.gridwidth);
    }
    if (defaults.gridheight != constraints.gridheight) {
      setIntField(generator, gbcLocal, "gridheight", constraints.gridheight);
    }
    if (defaults.weightx != constraints.weightx) {
      setDoubleField(generator, gbcLocal, "weightx", constraints.weightx);
    }
    if (defaults.weighty != constraints.weighty) {
      setDoubleField(generator, gbcLocal, "weighty", constraints.weighty);
    }
    if (defaults.anchor != constraints.anchor) {
      setIntField(generator, gbcLocal, "anchor", constraints.anchor);
    }
    if (defaults.fill != constraints.fill) {
      setIntField(generator, gbcLocal, "fill", constraints.fill);
    }
    if (defaults.ipadx != constraints.ipadx) {
      setIntField(generator, gbcLocal, "ipadx", constraints.ipadx);
    }
    if (defaults.ipady != constraints.ipady) {
      setIntField(generator, gbcLocal, "ipady", constraints.ipady);
    }
    if (!defaults.insets.equals(constraints.insets)) {
      generator.loadLocal(gbcLocal);
      AsmCodeGenerator.pushPropValue(generator, Insets.class.getName(), constraints.insets);
      generator.putField(ourGridBagConstraintsType, "insets", Type.getType(Insets.class));
    } 
    
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    generator.loadLocal(gbcLocal);
    
    generator.invokeVirtual(ourContainerType, ourAddMethod);
  }
  
  private static void checkSetSize(GeneratorAdapter generator, int componentLocal, String methodName, Dimension dimension) {
    if (dimension != null) {
      generator.loadLocal(componentLocal);
      AsmCodeGenerator.pushPropValue(generator, "java.awt.Dimension", dimension);
      generator.invokeVirtual(Type.getType(Component.class), new Method(methodName, Type.VOID_TYPE, new Type[] { Type.getType(Dimension.class) }));
    } 
  }

  
  private static void setIntField(GeneratorAdapter generator, int local, String fieldName, int value) {
    generator.loadLocal(local);
    generator.push(value);
    generator.putField(ourGridBagConstraintsType, fieldName, Type.INT_TYPE);
  }
  
  private static void setDoubleField(GeneratorAdapter generator, int local, String fieldName, double value) {
    generator.loadLocal(local);
    generator.push(value);
    generator.putField(ourGridBagConstraintsType, fieldName, Type.DOUBLE_TYPE);
  }
}
