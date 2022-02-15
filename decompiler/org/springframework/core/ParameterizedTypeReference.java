package org.springframework.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;







































public abstract class ParameterizedTypeReference<T>
{
  private final Type type;
  
  protected ParameterizedTypeReference() {
    Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(getClass());
    Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
    Assert.isInstanceOf(ParameterizedType.class, type, "Type must be a parameterized type");
    ParameterizedType parameterizedType = (ParameterizedType)type;
    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    Assert.isTrue((actualTypeArguments.length == 1), "Number of type arguments must be 1");
    this.type = actualTypeArguments[0];
  }
  
  private ParameterizedTypeReference(Type type) {
    this.type = type;
  }

  
  public Type getType() {
    return this.type;
  }

  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof ParameterizedTypeReference && this.type
      .equals(((ParameterizedTypeReference)other).type)));
  }

  
  public int hashCode() {
    return this.type.hashCode();
  }

  
  public String toString() {
    return "ParameterizedTypeReference<" + this.type + ">";
  }









  
  public static <T> ParameterizedTypeReference<T> forType(Type type) {
    return new ParameterizedTypeReference<T>(type) {  }
      ;
  }
  
  private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
    Class<?> parent = child.getSuperclass();
    if (Object.class == parent) {
      throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
    }
    if (ParameterizedTypeReference.class == parent) {
      return child;
    }
    
    return findParameterizedTypeReferenceSubclass(parent);
  }
}
