package com.kichik.pecoff4j.asm;








public class SHL
  extends AbstractInstruction
{
  private ModRM modrm;
  private byte imm8;
  
  public SHL(ModRM modrm, byte imm8) {
    this.modrm = modrm;
    this.imm8 = imm8;
    this.code = toCode(193, modrm, imm8);
  }

  
  public String toIntelAssembly() {
    return "shl  " + Register.to32(this.modrm.reg1) + ", " + 
      toHexString(this.imm8, false);
  }
}
