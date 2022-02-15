package org.springframework.asm;

public class MethodAdapter implements MethodVisitor {
  protected MethodVisitor mv;
  
  public MethodAdapter(MethodVisitor paramMethodVisitor) {
    this.mv = paramMethodVisitor;
  }
  
  public AnnotationVisitor visitAnnotationDefault() {
    return this.mv.visitAnnotationDefault();
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    return this.mv.visitAnnotation(paramString, paramBoolean);
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean) {
    return this.mv.visitParameterAnnotation(paramInt, paramString, paramBoolean);
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    this.mv.visitAttribute(paramAttribute);
  }
  
  public void visitCode() {
    this.mv.visitCode();
  }
  
  public void visitInsn(int paramInt) {
    this.mv.visitInsn(paramInt);
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    this.mv.visitIntInsn(paramInt1, paramInt2);
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    this.mv.visitVarInsn(paramInt1, paramInt2);
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    this.mv.visitTypeInsn(paramInt, paramString);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    this.mv.visitJumpInsn(paramInt, paramLabel);
  }
  
  public void visitLabel(Label paramLabel) {
    this.mv.visitLabel(paramLabel);
  }
  
  public void visitLdcInsn(Object paramObject) {
    this.mv.visitLdcInsn(paramObject);
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) {
    this.mv.visitIincInsn(paramInt1, paramInt2);
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label[] paramArrayOfLabel) {
    this.mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramArrayOfLabel);
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfint, Label[] paramArrayOfLabel) {
    this.mv.visitLookupSwitchInsn(paramLabel, paramArrayOfint, paramArrayOfLabel);
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {
    this.mv.visitMultiANewArrayInsn(paramString, paramInt);
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString) {
    this.mv.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt) {
    this.mv.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel) {
    this.mv.visitLineNumber(paramInt, paramLabel);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2) {
    this.mv.visitMaxs(paramInt1, paramInt2);
  }
  
  public void visitEnd() {
    this.mv.visitEnd();
  }
}
