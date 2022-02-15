package com.kichik.pecoff4j.asm;

public class SIB {
  public final int scale;
  public final int index;
  public final int base;
  
  public SIB(int value) {
    value &= 0xFF;
    this.scale = value >> 6;
    this.index = value >> 3 & 0x7;
    this.base = value & 0x7;
  }
  
  public byte encode() {
    return (byte)(this.scale << 6 | this.index << 3 | this.base);
  }
  
  public String toString(int imm32) {
    return Register.to32(this.index) + "*" + (this.scale * 2) + "+" + 
      Register.to32(this.base) + 
      AbstractInstruction.toHexString(imm32, true);
  }
}
