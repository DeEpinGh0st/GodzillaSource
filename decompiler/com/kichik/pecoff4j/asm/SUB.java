package com.kichik.pecoff4j.asm;








public class SUB
  extends AbstractInstruction
{
  private ModRM modrm;
  private int imm32;
  
  public SUB(ModRM modrm, int imm32) {
    this.modrm = modrm;
    this.imm32 = imm32;
    this.code = toCode(129, modrm, imm32);
  }

  
  public String toIntelAssembly() {
    return "sub  " + Register.to32(this.modrm.reg1) + ", " + 
      toHexString(this.imm32, false);
  }
}
