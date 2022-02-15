package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

































public final class GenericTypeResolver
{
  private static final Map<Class<?>, Map<TypeVariable, Type>> typeVariableCache = (Map<Class<?>, Map<TypeVariable, Type>>)new ConcurrentReferenceHashMap();












  
  @Deprecated
  public static Class<?> resolveParameterType(MethodParameter methodParameter, Class<?> implementationClass) {
    Assert.notNull(methodParameter, "MethodParameter must not be null");
    Assert.notNull(implementationClass, "Class must not be null");
    methodParameter.setContainingClass(implementationClass);
    return methodParameter.getParameterType();
  }







  
  public static Class<?> resolveReturnType(Method method, Class<?> clazz) {
    Assert.notNull(method, "Method must not be null");
    Assert.notNull(clazz, "Class must not be null");
    return ResolvableType.forMethodReturnType(method, clazz).resolve(method.getReturnType());
  }









  
  @Nullable
  public static Class<?> resolveReturnTypeArgument(Method method, Class<?> genericIfc) {
    Assert.notNull(method, "Method must not be null");
    ResolvableType resolvableType = ResolvableType.forMethodReturnType(method).as(genericIfc);
    if (!resolvableType.hasGenerics() || resolvableType.getType() instanceof java.lang.reflect.WildcardType) {
      return null;
    }
    return getSingleGeneric(resolvableType);
  }








  
  @Nullable
  public static Class<?> resolveTypeArgument(Class<?> clazz, Class<?> genericIfc) {
    ResolvableType resolvableType = ResolvableType.forClass(clazz).as(genericIfc);
    if (!resolvableType.hasGenerics()) {
      return null;
    }
    return getSingleGeneric(resolvableType);
  }
  
  @Nullable
  private static Class<?> getSingleGeneric(ResolvableType resolvableType) {
    Assert.isTrue(((resolvableType.getGenerics()).length == 1), () -> "Expected 1 type argument on generic interface [" + resolvableType + "] but found " + (resolvableType.getGenerics()).length);

    
    return resolvableType.getGeneric(new int[0]).resolve();
  }










  
  @Nullable
  public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
    ResolvableType type = ResolvableType.forClass(clazz).as(genericIfc);
    if (!type.hasGenerics() || type.isEntirelyUnresolvable()) {
      return null;
    }
    return type.resolveGenerics(Object.class);
  }









  
  public static Type resolveType(Type genericType, @Nullable Class<?> contextClass) {
    if (contextClass != null) {
      if (genericType instanceof TypeVariable) {
        ResolvableType resolvedTypeVariable = resolveVariable((TypeVariable)genericType, 
            ResolvableType.forClass(contextClass));
        if (resolvedTypeVariable != ResolvableType.NONE) {
          Class<?> resolved = resolvedTypeVariable.resolve();
          if (resolved != null) {
            return resolved;
          }
        }
      
      } else if (genericType instanceof ParameterizedType) {
        ResolvableType resolvedType = ResolvableType.forType(genericType);
        if (resolvedType.hasUnresolvableGenerics()) {
          ParameterizedType parameterizedType = (ParameterizedType)genericType;
          Class<?>[] generics = new Class[(parameterizedType.getActualTypeArguments()).length];
          Type[] typeArguments = parameterizedType.getActualTypeArguments();
          ResolvableType contextType = ResolvableType.forClass(contextClass);
          for (int i = 0; i < typeArguments.length; i++) {
            Type typeArgument = typeArguments[i];
            if (typeArgument instanceof TypeVariable) {
              ResolvableType resolvedTypeArgument = resolveVariable((TypeVariable)typeArgument, contextType);
              
              if (resolvedTypeArgument != ResolvableType.NONE) {
                generics[i] = resolvedTypeArgument.resolve();
              } else {
                
                generics[i] = ResolvableType.forType(typeArgument).resolve();
              } 
            } else {
              
              generics[i] = ResolvableType.forType(typeArgument).resolve();
            } 
          } 
          Class<?> rawClass = resolvedType.getRawClass();
          if (rawClass != null) {
            return ResolvableType.forClassWithGenerics(rawClass, generics).getType();
          }
        } 
      } 
    }
    return genericType;
  }

  
  private static ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
    if (contextType.hasGenerics()) {
      ResolvableType resolvedType = ResolvableType.forType(typeVariable, contextType);
      if (resolvedType.resolve() != null) {
        return resolvedType;
      }
    } 
    
    ResolvableType superType = contextType.getSuperType();
    if (superType != ResolvableType.NONE) {
      ResolvableType resolvedType = resolveVariable(typeVariable, superType);
      if (resolvedType.resolve() != null) {
        return resolvedType;
      }
    } 
    for (ResolvableType ifc : contextType.getInterfaces()) {
      ResolvableType resolvedType = resolveVariable(typeVariable, ifc);
      if (resolvedType.resolve() != null) {
        return resolvedType;
      }
    } 
    return ResolvableType.NONE;
  }








  
  public static Class<?> resolveType(Type genericType, Map<TypeVariable, Type> map) {
    return ResolvableType.forType(genericType, new TypeVariableMapVariableResolver(map)).toClass();
  }







  
  public static Map<TypeVariable, Type> getTypeVariableMap(Class<?> clazz) {
    Map<TypeVariable, Type> typeVariableMap = typeVariableCache.get(clazz);
    if (typeVariableMap == null) {
      typeVariableMap = new HashMap<>();
      buildTypeVariableMap(ResolvableType.forClass(clazz), typeVariableMap);
      typeVariableCache.put(clazz, Collections.unmodifiableMap(typeVariableMap));
    } 
    return typeVariableMap;
  }

  
  private static void buildTypeVariableMap(ResolvableType type, Map<TypeVariable, Type> typeVariableMap) {
    if (type != ResolvableType.NONE) {
      Class<?> resolved = type.resolve();
      if (resolved != null && type.getType() instanceof ParameterizedType) {
        TypeVariable[] arrayOfTypeVariable = (TypeVariable[])resolved.getTypeParameters();
        for (int i = 0; i < arrayOfTypeVariable.length; i++) {
          ResolvableType generic = type.getGeneric(new int[] { i });
          while (generic.getType() instanceof TypeVariable) {
            generic = generic.resolveType();
          }
          if (generic != ResolvableType.NONE) {
            typeVariableMap.put(arrayOfTypeVariable[i], generic.getType());
          }
        } 
      } 
      buildTypeVariableMap(type.getSuperType(), typeVariableMap);
      for (ResolvableType interfaceType : type.getInterfaces()) {
        buildTypeVariableMap(interfaceType, typeVariableMap);
      }
      if (resolved != null && resolved.isMemberClass()) {
        buildTypeVariableMap(ResolvableType.forClass(resolved.getEnclosingClass()), typeVariableMap);
      }
    } 
  }

  
  private static class TypeVariableMapVariableResolver
    implements ResolvableType.VariableResolver
  {
    private final Map<TypeVariable, Type> typeVariableMap;
    
    public TypeVariableMapVariableResolver(Map<TypeVariable, Type> typeVariableMap) {
      this.typeVariableMap = typeVariableMap;
    }

    
    @Nullable
    public ResolvableType resolveVariable(TypeVariable<?> variable) {
      Type type = this.typeVariableMap.get(variable);
      return (type != null) ? ResolvableType.forType(type) : null;
    }

    
    public Object getSource() {
      return this.typeVariableMap;
    }
  }
}
