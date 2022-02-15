package org.springframework.cglib.core;

import org.springframework.asm.ClassVisitor;

public interface ClassGenerator {
  void generateClass(ClassVisitor paramClassVisitor) throws Exception;
}
