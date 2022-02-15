package org.springframework.cglib.core;

import org.springframework.asm.Label;

public interface ObjectSwitchCallback {
  void processCase(Object paramObject, Label paramLabel) throws Exception;
  
  void processDefault() throws Exception;
}
