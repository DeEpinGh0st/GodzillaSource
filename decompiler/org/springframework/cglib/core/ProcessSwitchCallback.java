package org.springframework.cglib.core;

import org.springframework.asm.Label;

public interface ProcessSwitchCallback {
  void processCase(int paramInt, Label paramLabel) throws Exception;
  
  void processDefault() throws Exception;
}
