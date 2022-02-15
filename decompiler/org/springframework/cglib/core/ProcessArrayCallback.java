package org.springframework.cglib.core;

import org.springframework.asm.Type;

public interface ProcessArrayCallback {
  void processElement(Type paramType);
}
