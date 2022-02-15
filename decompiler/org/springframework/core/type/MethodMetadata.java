package org.springframework.core.type;

public interface MethodMetadata extends AnnotatedTypeMetadata {
  String getMethodName();
  
  String getDeclaringClassName();
  
  String getReturnTypeName();
  
  boolean isAbstract();
  
  boolean isStatic();
  
  boolean isFinal();
  
  boolean isOverridable();
}
