package org.springframework.asm.commons;

import org.springframework.asm.Label;

public interface TableSwitchGenerator {
  void generateCase(int paramInt, Label paramLabel);
  
  void generateDefault();
}
