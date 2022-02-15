package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.Constants;














public abstract class ClassTransformer
  extends ClassVisitor
{
  public ClassTransformer() {
    super(Constants.ASM_API);
  }
  public ClassTransformer(int opcode) {
    super(opcode);
  }
  
  public abstract void setTarget(ClassVisitor paramClassVisitor);
}
