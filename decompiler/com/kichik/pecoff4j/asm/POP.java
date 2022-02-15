package com.kichik.pecoff4j.asm;








public class POP
  extends AbstractInstruction
{
  private int register;
  
  public POP(int register) {
    this.register = register;
    this.code = toCode(0x58 | register);
  }

  
  public String toIntelAssembly() {
    return "pop  " + Register.to32(this.register);
  }
}
