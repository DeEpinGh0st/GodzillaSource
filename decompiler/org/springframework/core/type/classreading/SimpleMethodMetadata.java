package org.springframework.core.type.classreading;

import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

































final class SimpleMethodMetadata
  implements MethodMetadata
{
  private final String methodName;
  private final int access;
  private final String declaringClassName;
  private final String returnTypeName;
  private final Object source;
  private final MergedAnnotations annotations;
  
  SimpleMethodMetadata(String methodName, int access, String declaringClassName, String returnTypeName, Object source, MergedAnnotations annotations) {
    this.methodName = methodName;
    this.access = access;
    this.declaringClassName = declaringClassName;
    this.returnTypeName = returnTypeName;
    this.source = source;
    this.annotations = annotations;
  }


  
  public String getMethodName() {
    return this.methodName;
  }

  
  public String getDeclaringClassName() {
    return this.declaringClassName;
  }

  
  public String getReturnTypeName() {
    return this.returnTypeName;
  }

  
  public boolean isAbstract() {
    return ((this.access & 0x400) != 0);
  }

  
  public boolean isStatic() {
    return ((this.access & 0x8) != 0);
  }

  
  public boolean isFinal() {
    return ((this.access & 0x10) != 0);
  }

  
  public boolean isOverridable() {
    return (!isStatic() && !isFinal() && !isPrivate());
  }
  
  private boolean isPrivate() {
    return ((this.access & 0x2) != 0);
  }

  
  public MergedAnnotations getAnnotations() {
    return this.annotations;
  }

  
  public boolean equals(@Nullable Object obj) {
    return (this == obj || (obj instanceof SimpleMethodMetadata && this.source
      .equals(((SimpleMethodMetadata)obj).source)));
  }

  
  public int hashCode() {
    return this.source.hashCode();
  }

  
  public String toString() {
    return this.source.toString();
  }
}
