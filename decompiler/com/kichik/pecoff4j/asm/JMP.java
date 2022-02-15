package com.kichik.pecoff4j.asm;








public class JMP
  extends AbstractInstruction
{
  private byte imm8;
  private int imm32;
  
  public JMP(byte imm8) {
    this.imm8 = imm8;
    this.code = toCode(235, imm8);
  }
  
  public JMP(int imm32) {
    this.imm32 = imm32;
    this.code = toCode(233, imm32);
  }

  
  public String toIntelAssembly() {
    switch (getOpCode()) {
      case 233:
        return "jmp  " + toHexString(this.imm32, false);
    } 
    return "jmp  " + toHexString(this.imm8, false);
  }
}
