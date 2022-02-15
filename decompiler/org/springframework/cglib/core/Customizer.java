package org.springframework.cglib.core;

import org.springframework.asm.Type;

public interface Customizer extends KeyFactoryCustomizer {
  void customize(CodeEmitter paramCodeEmitter, Type paramType);
}
