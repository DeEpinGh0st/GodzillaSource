package com.kichik.pecoff4j.asm;








public class CALL
  extends AbstractInstruction
{
  private int imm32;
  
  public CALL(ModRM modrm, int imm32) {
    this.imm32 = imm32;
    this.code = toCode(255, modrm, imm32);
  }
  
  public CALL(int opcode, int imm32) {
    this.imm32 = imm32;
    this.code = toCode(opcode, imm32);
  }

  
  public String toIntelAssembly() {
    switch (getOpCode()) {
      case 232:
        return "call " + toHexString(this.imm32, false) + " (" + 
          toHexString(this.offset + this.imm32 + size(), false) + ")";
    } 
    return "call " + toHexString(this.imm32, false);
  }
}
