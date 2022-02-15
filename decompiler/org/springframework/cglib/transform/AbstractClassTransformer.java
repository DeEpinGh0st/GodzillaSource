package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.Constants;














public abstract class AbstractClassTransformer
  extends ClassTransformer
{
  protected AbstractClassTransformer() {
    super(Constants.ASM_API);
  }
  
  public void setTarget(ClassVisitor target) {
    this.cv = target;
  }
}
