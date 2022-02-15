package com.kichik.pecoff4j.asm;








public class JGE
  extends AbstractInstruction
{
  private byte imm8;
  
  public JGE(byte imm8) {
    this.imm8 = imm8;
    this.code = toCode(125, imm8);
  }

  
  public String toIntelAssembly() {
    return "jge  " + toHexString(this.imm8, true);
  }
}
