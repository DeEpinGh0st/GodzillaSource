package com.kichik.pecoff4j.asm;








public class PUSH
  extends AbstractInstruction
{
  private int register;
  private byte imm8;
  private int imm32;
  
  public PUSH(int register) {
    this.register = register;
    this.code = toCode(0x50 | register);
  }
  
  public PUSH(byte imm8) {
    this.imm8 = imm8;
    this.code = toCode(106, imm8);
  }
  
  public PUSH(int opcode, int imm32) {
    this.imm32 = imm32;
    this.code = toCode(opcode, imm32);
  }

  
  public String toIntelAssembly() {
    switch (getOpCode()) {
      case 106:
        return "push " + toHexString(this.imm8, false);
      case 104:
        return "push " + toHexString(this.imm32, false);
    } 
    return "push " + Register.to32(this.register);
  }
}
