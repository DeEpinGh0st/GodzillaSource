package com.kichik.pecoff4j.asm;








public class ADD
  extends AbstractInstruction
{
  private ModRM modrm;
  private byte imm8;
  private int imm32;
  
  public ADD(ModRM modrm, byte imm8) {
    this.modrm = modrm;
    this.imm8 = imm8;
    this.code = toCode(131, modrm, imm8);
  }
  
  public ADD(int opcode, ModRM modrm, int imm32) {
    this.modrm = modrm;
    this.imm32 = imm32;
    this.code = toCode(opcode, modrm, imm32);
  }

  
  public String toIntelAssembly() {
    switch (getOpCode()) {
      case 3:
        return "add  " + this.modrm.toIntelAssembly(this.imm32);
    } 
    return "add  " + Register.to32(this.modrm.reg1) + ", " + 
      toHexString(this.imm8, false);
  }
}
