package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


































@Beta
public final class TypeResolver
{
  private final TypeTable typeTable;
  
  public TypeResolver() {
    this.typeTable = new TypeTable();
  }
  
  private TypeResolver(TypeTable typeTable) {
    this.typeTable = typeTable;
  }







  
  static TypeResolver covariantly(Type contextType) {
    return (new TypeResolver()).where((Map<TypeVariableKey, ? extends Type>)TypeMappingIntrospector.getTypeMappings(contextType));
  }











  
  static TypeResolver invariantly(Type contextType) {
    Type invariantContext = WildcardCapturer.INSTANCE.capture(contextType);
    return (new TypeResolver()).where((Map<TypeVariableKey, ? extends Type>)TypeMappingIntrospector.getTypeMappings(invariantContext));
  }



















  
  public TypeResolver where(Type formal, Type actual) {
    Map<TypeVariableKey, Type> mappings = Maps.newHashMap();
    populateTypeMappings(mappings, (Type)Preconditions.checkNotNull(formal), (Type)Preconditions.checkNotNull(actual));
    return where(mappings);
  }

  
  TypeResolver where(Map<TypeVariableKey, ? extends Type> mappings) {
    return new TypeResolver(this.typeTable.where(mappings));
  }

  
  private static void populateTypeMappings(final Map<TypeVariableKey, Type> mappings, Type from, final Type to) {
    if (from.equals(to)) {
      return;
    }
    (new TypeVisitor()
      {
        void visitTypeVariable(TypeVariable<?> typeVariable) {
          mappings.put(new TypeResolver.TypeVariableKey(typeVariable), to);
        }

        
        void visitWildcardType(WildcardType fromWildcardType) {
          if (!(to instanceof WildcardType)) {
            return;
          }
          WildcardType toWildcardType = (WildcardType)to;
          Type[] fromUpperBounds = fromWildcardType.getUpperBounds();
          Type[] toUpperBounds = toWildcardType.getUpperBounds();
          Type[] fromLowerBounds = fromWildcardType.getLowerBounds();
          Type[] toLowerBounds = toWildcardType.getLowerBounds();
          Preconditions.checkArgument((fromUpperBounds.length == toUpperBounds.length && fromLowerBounds.length == toLowerBounds.length), "Incompatible type: %s vs. %s", fromWildcardType, to);

          
          int i;

          
          for (i = 0; i < fromUpperBounds.length; i++) {
            TypeResolver.populateTypeMappings(mappings, fromUpperBounds[i], toUpperBounds[i]);
          }
          for (i = 0; i < fromLowerBounds.length; i++) {
            TypeResolver.populateTypeMappings(mappings, fromLowerBounds[i], toLowerBounds[i]);
          }
        }

        
        void visitParameterizedType(ParameterizedType fromParameterizedType) {
          if (to instanceof WildcardType) {
            return;
          }
          ParameterizedType toParameterizedType = (ParameterizedType)TypeResolver.expectArgument((Class)ParameterizedType.class, to);
          if (fromParameterizedType.getOwnerType() != null && toParameterizedType
            .getOwnerType() != null) {
            TypeResolver.populateTypeMappings(mappings, fromParameterizedType
                .getOwnerType(), toParameterizedType.getOwnerType());
          }
          Preconditions.checkArgument(fromParameterizedType
              .getRawType().equals(toParameterizedType.getRawType()), "Inconsistent raw type: %s vs. %s", fromParameterizedType, to);


          
          Type[] fromArgs = fromParameterizedType.getActualTypeArguments();
          Type[] toArgs = toParameterizedType.getActualTypeArguments();
          Preconditions.checkArgument((fromArgs.length == toArgs.length), "%s not compatible with %s", fromParameterizedType, toParameterizedType);



          
          for (int i = 0; i < fromArgs.length; i++) {
            TypeResolver.populateTypeMappings(mappings, fromArgs[i], toArgs[i]);
          }
        }

        
        void visitGenericArrayType(GenericArrayType fromArrayType) {
          if (to instanceof WildcardType) {
            return;
          }
          Type componentType = Types.getComponentType(to);
          Preconditions.checkArgument((componentType != null), "%s is not an array type.", to);
          TypeResolver.populateTypeMappings(mappings, fromArrayType.getGenericComponentType(), componentType);
        }

        
        void visitClass(Class<?> fromClass) {
          if (to instanceof WildcardType) {
            return;
          }


          
          throw new IllegalArgumentException("No type mapping from " + fromClass + " to " + to);
        }
      }).visit(new Type[] { from });
  }




  
  public Type resolveType(Type type) {
    Preconditions.checkNotNull(type);
    if (type instanceof TypeVariable)
      return this.typeTable.resolve((TypeVariable)type); 
    if (type instanceof ParameterizedType)
      return resolveParameterizedType((ParameterizedType)type); 
    if (type instanceof GenericArrayType)
      return resolveGenericArrayType((GenericArrayType)type); 
    if (type instanceof WildcardType) {
      return resolveWildcardType((WildcardType)type);
    }
    
    return type;
  }

  
  Type[] resolveTypesInPlace(Type[] types) {
    for (int i = 0; i < types.length; i++) {
      types[i] = resolveType(types[i]);
    }
    return types;
  }
  
  private Type[] resolveTypes(Type[] types) {
    Type[] result = new Type[types.length];
    for (int i = 0; i < types.length; i++) {
      result[i] = resolveType(types[i]);
    }
    return result;
  }
  
  private WildcardType resolveWildcardType(WildcardType type) {
    Type[] lowerBounds = type.getLowerBounds();
    Type[] upperBounds = type.getUpperBounds();
    return new Types.WildcardTypeImpl(resolveTypes(lowerBounds), resolveTypes(upperBounds));
  }
  
  private Type resolveGenericArrayType(GenericArrayType type) {
    Type componentType = type.getGenericComponentType();
    Type resolvedComponentType = resolveType(componentType);
    return Types.newArrayType(resolvedComponentType);
  }
  
  private ParameterizedType resolveParameterizedType(ParameterizedType type) {
    Type owner = type.getOwnerType();
    Type resolvedOwner = (owner == null) ? null : resolveType(owner);
    Type resolvedRawType = resolveType(type.getRawType());
    
    Type[] args = type.getActualTypeArguments();
    Type[] resolvedArgs = resolveTypes(args);
    return Types.newParameterizedTypeWithOwner(resolvedOwner, (Class)resolvedRawType, resolvedArgs);
  }

  
  private static <T> T expectArgument(Class<T> type, Object arg) {
    try {
      return type.cast(arg);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException(arg + " is not a " + type.getSimpleName());
    } 
  }
  
  private static class TypeTable
  {
    private final ImmutableMap<TypeResolver.TypeVariableKey, Type> map;
    
    TypeTable() {
      this.map = ImmutableMap.of();
    }
    
    private TypeTable(ImmutableMap<TypeResolver.TypeVariableKey, Type> map) {
      this.map = map;
    }

    
    final TypeTable where(Map<TypeResolver.TypeVariableKey, ? extends Type> mappings) {
      ImmutableMap.Builder<TypeResolver.TypeVariableKey, Type> builder = ImmutableMap.builder();
      builder.putAll((Map)this.map);
      for (Map.Entry<TypeResolver.TypeVariableKey, ? extends Type> mapping : mappings.entrySet()) {
        TypeResolver.TypeVariableKey variable = mapping.getKey();
        Type type = mapping.getValue();
        Preconditions.checkArgument(!variable.equalsType(type), "Type variable %s bound to itself", variable);
        builder.put(variable, type);
      } 
      return new TypeTable(builder.build());
    }
    
    final Type resolve(final TypeVariable<?> var) {
      final TypeTable unguarded = this;
      TypeTable guarded = new TypeTable()
        {
          public Type resolveInternal(TypeVariable<?> intermediateVar, TypeResolver.TypeTable forDependent)
          {
            if (intermediateVar.getGenericDeclaration().equals(var.getGenericDeclaration())) {
              return intermediateVar;
            }
            return unguarded.resolveInternal(intermediateVar, forDependent);
          }
        };
      return resolveInternal(var, guarded);
    }








    
    Type resolveInternal(TypeVariable<?> var, TypeTable forDependants) {
      Type type = (Type)this.map.get(new TypeResolver.TypeVariableKey(var));
      if (type == null) {
        Type[] bounds = var.getBounds();
        if (bounds.length == 0) {
          return var;
        }
        Type[] resolvedBounds = (new TypeResolver(forDependants)).resolveTypes(bounds);



























        
        if (Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY && 
          Arrays.equals((Object[])bounds, (Object[])resolvedBounds)) {
          return var;
        }
        return Types.newArtificialTypeVariable((GenericDeclaration)var
            .getGenericDeclaration(), var.getName(), resolvedBounds);
      } 
      
      return (new TypeResolver(forDependants)).resolveType(type);
    }
  }
  
  private static final class TypeMappingIntrospector
    extends TypeVisitor {
    private final Map<TypeResolver.TypeVariableKey, Type> mappings = Maps.newHashMap();




    
    static ImmutableMap<TypeResolver.TypeVariableKey, Type> getTypeMappings(Type contextType) {
      Preconditions.checkNotNull(contextType);
      TypeMappingIntrospector introspector = new TypeMappingIntrospector();
      introspector.visit(new Type[] { contextType });
      return ImmutableMap.copyOf(introspector.mappings);
    }

    
    void visitClass(Class<?> clazz) {
      visit(new Type[] { clazz.getGenericSuperclass() });
      visit(clazz.getGenericInterfaces());
    }

    
    void visitParameterizedType(ParameterizedType parameterizedType) {
      Class<?> rawClass = (Class)parameterizedType.getRawType();
      TypeVariable[] arrayOfTypeVariable = (TypeVariable[])rawClass.getTypeParameters();
      Type[] typeArgs = parameterizedType.getActualTypeArguments();
      Preconditions.checkState((arrayOfTypeVariable.length == typeArgs.length));
      for (int i = 0; i < arrayOfTypeVariable.length; i++) {
        map(new TypeResolver.TypeVariableKey(arrayOfTypeVariable[i]), typeArgs[i]);
      }
      visit(new Type[] { rawClass });
      visit(new Type[] { parameterizedType.getOwnerType() });
    }

    
    void visitTypeVariable(TypeVariable<?> t) {
      visit(t.getBounds());
    }

    
    void visitWildcardType(WildcardType t) {
      visit(t.getUpperBounds());
    }
    
    private void map(TypeResolver.TypeVariableKey var, Type arg) {
      if (this.mappings.containsKey(var)) {
        return;
      }





      
      for (Type t = arg; t != null; t = this.mappings.get(TypeResolver.TypeVariableKey.forLookup(t))) {
        if (var.equalsType(t)) {



          
          for (Type x = arg; x != null; x = this.mappings.remove(TypeResolver.TypeVariableKey.forLookup(x)));
          return;
        } 
      } 
      this.mappings.put(var, arg);
    }
  }







  
  private static class WildcardCapturer
  {
    static final WildcardCapturer INSTANCE = new WildcardCapturer();
    
    private final AtomicInteger id;
    
    private WildcardCapturer() {
      this(new AtomicInteger());
    }
    
    private WildcardCapturer(AtomicInteger id) {
      this.id = id;
    }
    
    final Type capture(Type type) {
      Preconditions.checkNotNull(type);
      if (type instanceof Class) {
        return type;
      }
      if (type instanceof TypeVariable) {
        return type;
      }
      if (type instanceof GenericArrayType) {
        GenericArrayType arrayType = (GenericArrayType)type;
        return Types.newArrayType(
            notForTypeVariable().capture(arrayType.getGenericComponentType()));
      } 
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)type;
        Class<?> rawType = (Class)parameterizedType.getRawType();
        TypeVariable[] arrayOfTypeVariable = (TypeVariable[])rawType.getTypeParameters();
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < typeArgs.length; i++) {
          typeArgs[i] = forTypeVariable(arrayOfTypeVariable[i]).capture(typeArgs[i]);
        }
        return Types.newParameterizedTypeWithOwner(
            notForTypeVariable().captureNullable(parameterizedType.getOwnerType()), rawType, typeArgs);
      } 

      
      if (type instanceof WildcardType) {
        WildcardType wildcardType = (WildcardType)type;
        Type[] lowerBounds = wildcardType.getLowerBounds();
        if (lowerBounds.length == 0) {
          return captureAsTypeVariable(wildcardType.getUpperBounds());
        }
        
        return type;
      } 
      
      throw new AssertionError("must have been one of the known types");
    }

    
    TypeVariable<?> captureAsTypeVariable(Type[] upperBounds) {
      String name = "capture#" + this.id.incrementAndGet() + "-of ? extends " + Joiner.on('&').join((Object[])upperBounds);
      return Types.newArtificialTypeVariable(WildcardCapturer.class, name, upperBounds);
    }
    
    private WildcardCapturer forTypeVariable(final TypeVariable<?> typeParam) {
      return new WildcardCapturer(this.id)
        {
          TypeVariable<?> captureAsTypeVariable(Type[] upperBounds) {
            Set<Type> combined = new LinkedHashSet<>(Arrays.asList(upperBounds));





            
            combined.addAll(Arrays.asList(typeParam.getBounds()));
            if (combined.size() > 1) {
              combined.remove(Object.class);
            }
            return super.captureAsTypeVariable(combined.<Type>toArray(new Type[0]));
          }
        };
    }
    
    private WildcardCapturer notForTypeVariable() {
      return new WildcardCapturer(this.id);
    }
    
    private Type captureNullable(Type type) {
      if (type == null) {
        return null;
      }
      return capture(type);
    }
  }






  
  static final class TypeVariableKey
  {
    private final TypeVariable<?> var;






    
    TypeVariableKey(TypeVariable<?> var) {
      this.var = (TypeVariable)Preconditions.checkNotNull(var);
    }

    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.var.getGenericDeclaration(), this.var.getName() });
    }

    
    public boolean equals(Object obj) {
      if (obj instanceof TypeVariableKey) {
        TypeVariableKey that = (TypeVariableKey)obj;
        return equalsTypeVariable(that.var);
      } 
      return false;
    }


    
    public String toString() {
      return this.var.toString();
    }

    
    static TypeVariableKey forLookup(Type t) {
      if (t instanceof TypeVariable) {
        return new TypeVariableKey((TypeVariable)t);
      }
      return null;
    }





    
    boolean equalsType(Type type) {
      if (type instanceof TypeVariable) {
        return equalsTypeVariable((TypeVariable)type);
      }
      return false;
    }

    
    private boolean equalsTypeVariable(TypeVariable<?> that) {
      return (this.var.getGenericDeclaration().equals(that.getGenericDeclaration()) && this.var
        .getName().equals(that.getName()));
    }
  }
}
