package org.springframework.cglib.core;

import org.springframework.asm.Type;

public interface FieldTypeCustomizer extends KeyFactoryCustomizer {
  void customize(CodeEmitter paramCodeEmitter, int paramInt, Type paramType);
  
  Type getOutType(int paramInt, Type paramType);
}
