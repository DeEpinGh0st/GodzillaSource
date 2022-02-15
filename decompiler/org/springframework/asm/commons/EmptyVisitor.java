package org.springframework.asm.commons;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;

public class EmptyVisitor implements ClassVisitor, FieldVisitor, MethodVisitor, AnnotationVisitor {
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {}
  
  public void visitSource(String paramString1, String paramString2) {}
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3) {}
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    return this;
  }
  
  public void visitAttribute(Attribute paramAttribute) {}
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt) {}
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    return this;
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    return this;
  }
  
  public void visitEnd() {}
  
  public AnnotationVisitor visitAnnotationDefault() {
    return this;
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean) {
    return this;
  }
  
  public void visitCode() {}
  
  public void visitInsn(int paramInt) {}
  
  public void visitIntInsn(int paramInt1, int paramInt2) {}
  
  public void visitVarInsn(int paramInt1, int paramInt2) {}
  
  public void visitTypeInsn(int paramInt, String paramString) {}
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {}
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {}
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {}
  
  public void visitLabel(Label paramLabel) {}
  
  public void visitLdcInsn(Object paramObject) {}
  
  public void visitIincInsn(int paramInt1, int paramInt2) {}
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label[] paramArrayOfLabel) {}
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfint, Label[] paramArrayOfLabel) {}
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {}
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString) {}
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt) {}
  
  public void visitLineNumber(int paramInt, Label paramLabel) {}
  
  public void visitMaxs(int paramInt1, int paramInt2) {}
  
  public void visit(String paramString, Object paramObject) {}
  
  public void visitEnum(String paramString1, String paramString2, String paramString3) {}
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2) {
    return this;
  }
  
  public AnnotationVisitor visitArray(String paramString) {
    return this;
  }
}
