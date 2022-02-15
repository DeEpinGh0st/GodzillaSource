package com.kichik.pecoff4j.asm;

public interface Instruction {
  int size();
  
  byte[] toCode();
  
  String toIntelAssembly();
}
