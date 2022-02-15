package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Insets;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;




















public class FormLayoutCodeGenerator
  extends LayoutCodeGenerator
{
  private static final Type ourFormLayoutType = Type.getType(FormLayout.class);
  private static final Type ourCellConstraintsType = Type.getType(CellConstraints.class);
  private static final Type ourCellAlignmentType = Type.getType(CellConstraints.Alignment.class);
  private static final Method ourFormLayoutConstructor = Method.getMethod("void <init>(java.lang.String,java.lang.String)");
  private static final Method ourCellConstraintsConstructor = Method.getMethod("void <init>(int,int,int,int,com.jgoodies.forms.layout.CellConstraints$Alignment,com.jgoodies.forms.layout.CellConstraints$Alignment,java.awt.Insets)");
  private static final Method ourSetRowGroupsMethod = Method.getMethod("void setRowGroups(int[][])");
  private static final Method ourSetColumnGroupsMethod = Method.getMethod("void setColumnGroups(int[][])");
  
  public static String[] HORZ_ALIGN_FIELDS = new String[] { "LEFT", "CENTER", "RIGHT", "FILL" };
  public static String[] VERT_ALIGN_FIELDS = new String[] { "TOP", "CENTER", "BOTTOM", "FILL" };
  
  public void generateContainerLayout(LwContainer lwContainer, GeneratorAdapter generator, int componentLocal) {
    FormLayout formLayout = (FormLayout)lwContainer.getLayout();
    
    generator.loadLocal(componentLocal);
    
    generator.newInstance(ourFormLayoutType);
    generator.dup();
    generator.push(FormLayoutUtils.getEncodedColumnSpecs(formLayout));
    generator.push(FormLayoutUtils.getEncodedRowSpecs(formLayout));
    
    generator.invokeConstructor(ourFormLayoutType, ourFormLayoutConstructor);
    
    generateGroups(generator, formLayout.getRowGroups(), ourSetRowGroupsMethod);
    generateGroups(generator, formLayout.getColumnGroups(), ourSetColumnGroupsMethod);
    
    generator.invokeVirtual(ourContainerType, ourSetLayoutMethod);
  }
  
  private static void generateGroups(GeneratorAdapter generator, int[][] groups, Method setGroupsMethod) {
    if (groups.length == 0)
      return;  int groupLocal = generator.newLocal(Type.getType("[I"));
    generator.dup();
    generator.push(groups.length);
    generator.newArray(Type.getType("[I"));
    for (int i = 0; i < groups.length; i++) {
      generator.dup();
      generator.push((groups[i]).length);
      generator.newArray(Type.INT_TYPE);
      generator.storeLocal(groupLocal);
      for (int j = 0; j < (groups[i]).length; j++) {
        generator.loadLocal(groupLocal);
        generator.push(j);
        generator.push(groups[i][j]);
        generator.visitInsn(79);
      } 
      generator.push(i);
      generator.loadLocal(groupLocal);
      generator.visitInsn(83);
    } 
    generator.invokeVirtual(ourFormLayoutType, setGroupsMethod);
  }
  
  public void generateComponentLayout(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal, int parentLocal) {
    generator.loadLocal(parentLocal);
    generator.loadLocal(componentLocal);
    addNewCellConstraints(generator, lwComponent);
    generator.invokeVirtual(ourContainerType, ourAddMethod);
  }
  
  private static void addNewCellConstraints(GeneratorAdapter generator, LwComponent lwComponent) {
    GridConstraints constraints = lwComponent.getConstraints();
    CellConstraints cc = (CellConstraints)lwComponent.getCustomLayoutConstraints();
    
    generator.newInstance(ourCellConstraintsType);
    generator.dup();
    generator.push(constraints.getColumn() + 1);
    generator.push(constraints.getRow() + 1);
    generator.push(constraints.getColSpan());
    generator.push(constraints.getRowSpan());
    
    if (cc.hAlign == CellConstraints.DEFAULT) {
      generator.getStatic(ourCellConstraintsType, "DEFAULT", ourCellAlignmentType);
    } else {
      
      int hAlign = Utils.alignFromConstraints(constraints, true);
      generator.getStatic(ourCellConstraintsType, HORZ_ALIGN_FIELDS[hAlign], ourCellAlignmentType);
    } 
    if (cc.vAlign == CellConstraints.DEFAULT) {
      generator.getStatic(ourCellConstraintsType, "DEFAULT", ourCellAlignmentType);
    } else {
      
      int vAlign = Utils.alignFromConstraints(constraints, false);
      generator.getStatic(ourCellConstraintsType, VERT_ALIGN_FIELDS[vAlign], ourCellAlignmentType);
    } 
    
    AsmCodeGenerator.pushPropValue(generator, Insets.class.getName(), cc.insets);
    
    generator.invokeConstructor(ourCellConstraintsType, ourCellConstraintsConstructor);
  }
}
