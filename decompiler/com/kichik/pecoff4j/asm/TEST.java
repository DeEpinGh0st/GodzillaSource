package com.kichik.pecoff4j.asm;








public class TEST
  extends AbstractInstruction
{
  private ModRM modrm;
  
  public TEST(ModRM modrm) {
    this.modrm = modrm;
    this.code = toCode(133, modrm);
  }

  
  public String toIntelAssembly() {
    return "test " + Register.to32(this.modrm.reg1) + ", " + 
      Register.to32(this.modrm.reg2);
  }
}
