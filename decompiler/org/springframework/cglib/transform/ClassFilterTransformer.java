package org.springframework.cglib.transform;
















public class ClassFilterTransformer
  extends AbstractClassFilterTransformer
{
  private ClassFilter filter;
  
  public ClassFilterTransformer(ClassFilter filter, ClassTransformer pass) {
    super(pass);
    this.filter = filter;
  }
  
  protected boolean accept(int version, int access, String name, String signature, String superName, String[] interfaces) {
    return this.filter.accept(name.replace('/', '.'));
  }
}
