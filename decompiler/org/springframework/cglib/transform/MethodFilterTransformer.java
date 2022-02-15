package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;













public class MethodFilterTransformer
  extends AbstractClassTransformer
{
  private MethodFilter filter;
  private ClassTransformer pass;
  private ClassVisitor direct;
  
  public MethodFilterTransformer(MethodFilter filter, ClassTransformer pass) {
    this.filter = filter;
    this.pass = pass;
    super.setTarget(pass);
  }




  
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    return (this.filter.accept(access, name, desc, signature, exceptions) ? this.pass : this.direct).visitMethod(access, name, desc, signature, exceptions);
  }
  
  public void setTarget(ClassVisitor target) {
    this.pass.setTarget(target);
    this.direct = target;
  }
}
