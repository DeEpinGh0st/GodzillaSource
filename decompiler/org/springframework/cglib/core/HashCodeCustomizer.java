package org.springframework.cglib.core;

import org.springframework.asm.Type;

public interface HashCodeCustomizer extends KeyFactoryCustomizer {
  boolean customize(CodeEmitter paramCodeEmitter, Type paramType);
}
