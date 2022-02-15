package com.kichik.pecoff4j.asm;








public class CMP
  extends AbstractInstruction
{
  private ModRM modrm;
  private byte imm8;
  
  public CMP(ModRM modrm, byte imm8) {
    this.modrm = modrm;
    this.imm8 = imm8;
    this.code = toCode(59, modrm, imm8);
  }

  
  public String toIntelAssembly() {
    return "cmp  " + Register.to32(this.modrm.reg2) + ", [" + 
      Register.to32(this.modrm.reg1) + toHexString(this.imm8, true) + "]";
  }
}
