package com.kichik.pecoff4j.asm;








public class JumpIfInstruction
  extends AbstractInstruction
{
  private int op;
  private int imm32;
  
  public JumpIfInstruction(int op, int imm32) {
    this.op = op;
    this.imm32 = imm32;
    this.code = toCode(15, new ModRM(op), imm32);
  }
  
  public String getOp() {
    switch (this.op) {
      case 133:
        return "jnz";
      case 141:
        return "jge";
    } 
    return "???";
  }

  
  public String toIntelAssembly() {
    return getOp() + "  " + toHexString(this.imm32, false) + " (" + 
      toHexString(this.offset + this.imm32 + size(), false) + ")";
  }
}
