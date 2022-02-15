package org.springframework.expression.spel;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.PropertyAccessor;

public interface CompilablePropertyAccessor extends PropertyAccessor, Opcodes {
  boolean isCompilable();
  
  Class<?> getPropertyType();
  
  void generateCode(String paramString, MethodVisitor paramMethodVisitor, CodeFlow paramCodeFlow);
}
