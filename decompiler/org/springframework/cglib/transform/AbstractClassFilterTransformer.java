package org.springframework.cglib.transform;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;










public abstract class AbstractClassFilterTransformer
  extends AbstractClassTransformer
{
  private ClassTransformer pass;
  private ClassVisitor target;
  
  public void setTarget(ClassVisitor target) {
    super.setTarget(target);
    this.pass.setTarget(target);
  }
  
  protected AbstractClassFilterTransformer(ClassTransformer pass) {
    this.pass = pass;
  }



  
  protected abstract boolean accept(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);


  
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.target = accept(version, access, name, signature, superName, interfaces) ? this.pass : this.cv;
    this.target.visit(version, access, name, signature, superName, interfaces);
  }
  
  public void visitSource(String source, String debug) {
    this.target.visitSource(source, debug);
  }
  
  public void visitOuterClass(String owner, String name, String desc) {
    this.target.visitOuterClass(owner, name, desc);
  }
  
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return this.target.visitAnnotation(desc, visible);
  }
  
  public void visitAttribute(Attribute attr) {
    this.target.visitAttribute(attr);
  }
  
  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    this.target.visitInnerClass(name, outerName, innerName, access);
  }




  
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    return this.target.visitField(access, name, desc, signature, value);
  }




  
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    return this.target.visitMethod(access, name, desc, signature, exceptions);
  }
  
  public void visitEnd() {
    this.target.visitEnd();
    this.target = null;
  }
}
