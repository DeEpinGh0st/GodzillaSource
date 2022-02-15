package org.springframework.asm;

public class ClassAdapter implements ClassVisitor {
  protected ClassVisitor cv;
  
  public ClassAdapter(ClassVisitor paramClassVisitor) {
    this.cv = paramClassVisitor;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.cv.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public void visitSource(String paramString1, String paramString2) {
    this.cv.visitSource(paramString1, paramString2);
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3) {
    this.cv.visitOuterClass(paramString1, paramString2, paramString3);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    return this.cv.visitAnnotation(paramString, paramBoolean);
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    this.cv.visitAttribute(paramAttribute);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt) {
    this.cv.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    return this.cv.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    return this.cv.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public void visitEnd() {
    this.cv.visitEnd();
  }
}
