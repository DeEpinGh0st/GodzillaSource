package org.springframework.cglib.core;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;






































public class LocalVariablesSorter
  extends MethodVisitor
{
  protected final int firstLocal;
  private final State state;
  
  private static class State
  {
    private State() {}
    
    int[] mapping = new int[40];



    
    int nextLocal;
  }



  
  public LocalVariablesSorter(int access, String desc, MethodVisitor mv) {
    super(Constants.ASM_API, mv);
    this.state = new State();
    Type[] args = Type.getArgumentTypes(desc);
    this.state.nextLocal = ((0x8 & access) != 0) ? 0 : 1;
    for (int i = 0; i < args.length; i++) {
      this.state.nextLocal += args[i].getSize();
    }
    this.firstLocal = this.state.nextLocal;
  }
  
  public LocalVariablesSorter(LocalVariablesSorter lvs) {
    super(Constants.ASM_API, lvs.mv);
    this.state = lvs.state;
    this.firstLocal = lvs.firstLocal;
  }
  
  public void visitVarInsn(int opcode, int var) {
    int size;
    switch (opcode) {
      case 22:
      case 24:
      case 55:
      case 57:
        size = 2;
        break;
      default:
        size = 1; break;
    } 
    this.mv.visitVarInsn(opcode, remap(var, size));
  }
  
  public void visitIincInsn(int var, int increment) {
    this.mv.visitIincInsn(remap(var, 1), increment);
  }
  
  public void visitMaxs(int maxStack, int maxLocals) {
    this.mv.visitMaxs(maxStack, this.state.nextLocal);
  }







  
  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
    this.mv.visitLocalVariable(name, desc, signature, start, end, remap(index));
  }


  
  protected int newLocal(int size) {
    int var = this.state.nextLocal;
    this.state.nextLocal += size;
    return var;
  }
  
  private int remap(int var, int size) {
    if (var < this.firstLocal) {
      return var;
    }
    int key = 2 * var + size - 1;
    int length = this.state.mapping.length;
    if (key >= length) {
      int[] newMapping = new int[Math.max(2 * length, key + 1)];
      System.arraycopy(this.state.mapping, 0, newMapping, 0, length);
      this.state.mapping = newMapping;
    } 
    int value = this.state.mapping[key];
    if (value == 0) {
      value = this.state.nextLocal + 1;
      this.state.mapping[key] = value;
      this.state.nextLocal += size;
    } 
    return value - 1;
  }
  
  private int remap(int var) {
    if (var < this.firstLocal) {
      return var;
    }
    int key = 2 * var;
    int value = (key < this.state.mapping.length) ? this.state.mapping[key] : 0;
    if (value == 0) {
      value = (key + 1 < this.state.mapping.length) ? this.state.mapping[key + 1] : 0;
    }
    if (value == 0) {
      throw new IllegalStateException("Unknown local variable " + var);
    }
    return value - 1;
  }
}
