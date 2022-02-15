package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
















































































@Beta
public abstract class TypeToken<T>
  extends TypeCapture<T>
  implements Serializable
{
  private final Type runtimeType;
  private transient TypeResolver invariantTypeResolver;
  private transient TypeResolver covariantTypeResolver;
  private static final long serialVersionUID = 3637540370352322684L;
  
  protected TypeToken() {
    this.runtimeType = capture();
    Preconditions.checkState(!(this.runtimeType instanceof TypeVariable), "Cannot construct a TypeToken for a type variable.\nYou probably meant to call new TypeToken<%s>(getClass()) that can resolve the type variable for you.\nIf you do need to create a TypeToken of a type variable, please use TypeToken.of() instead.", this.runtimeType);
  }


























  
  protected TypeToken(Class<?> declaringClass) {
    Type captured = capture();
    if (captured instanceof Class) {
      this.runtimeType = captured;
    } else {
      this.runtimeType = TypeResolver.covariantly(declaringClass).resolveType(captured);
    } 
  }
  
  private TypeToken(Type type) {
    this.runtimeType = (Type)Preconditions.checkNotNull(type);
  }

  
  public static <T> TypeToken<T> of(Class<T> type) {
    return new SimpleTypeToken<>(type);
  }

  
  public static TypeToken<?> of(Type type) {
    return new SimpleTypeToken(type);
  }
















  
  public final Class<? super T> getRawType() {
    Class<?> rawType = (Class)getRawTypes().iterator().next();
    
    Class<? super T> result = (Class)rawType;
    return result;
  }

  
  public final Type getType() {
    return this.runtimeType;
  }




















  
  public final <X> TypeToken<T> where(TypeParameter<X> typeParam, TypeToken<X> typeArg) {
    TypeResolver resolver = (new TypeResolver()).where(
        (Map<TypeResolver.TypeVariableKey, ? extends Type>)ImmutableMap.of(new TypeResolver.TypeVariableKey(typeParam.typeVariable), typeArg.runtimeType));

    
    return new SimpleTypeToken<>(resolver.resolveType(this.runtimeType));
  }


















  
  public final <X> TypeToken<T> where(TypeParameter<X> typeParam, Class<X> typeArg) {
    return where(typeParam, of(typeArg));
  }









  
  public final TypeToken<?> resolveType(Type type) {
    Preconditions.checkNotNull(type);

    
    return of(getInvariantTypeResolver().resolveType(type));
  }
  
  private TypeToken<?> resolveSupertype(Type type) {
    TypeToken<?> supertype = of(getCovariantTypeResolver().resolveType(type));
    
    supertype.covariantTypeResolver = this.covariantTypeResolver;
    supertype.invariantTypeResolver = this.invariantTypeResolver;
    return supertype;
  }












  
  final TypeToken<? super T> getGenericSuperclass() {
    if (this.runtimeType instanceof TypeVariable)
    {
      return boundAsSuperclass(((TypeVariable)this.runtimeType).getBounds()[0]);
    }
    if (this.runtimeType instanceof WildcardType)
    {
      return boundAsSuperclass(((WildcardType)this.runtimeType).getUpperBounds()[0]);
    }
    Type superclass = getRawType().getGenericSuperclass();
    if (superclass == null) {
      return null;
    }
    
    TypeToken<? super T> superToken = (TypeToken)resolveSupertype(superclass);
    return superToken;
  }
  
  private TypeToken<? super T> boundAsSuperclass(Type bound) {
    TypeToken<?> token = of(bound);
    if (token.getRawType().isInterface()) {
      return null;
    }
    
    TypeToken<? super T> superclass = (TypeToken)token;
    return superclass;
  }












  
  final ImmutableList<TypeToken<? super T>> getGenericInterfaces() {
    if (this.runtimeType instanceof TypeVariable) {
      return boundsAsInterfaces(((TypeVariable)this.runtimeType).getBounds());
    }
    if (this.runtimeType instanceof WildcardType) {
      return boundsAsInterfaces(((WildcardType)this.runtimeType).getUpperBounds());
    }
    ImmutableList.Builder<TypeToken<? super T>> builder = ImmutableList.builder();
    for (Type interfaceType : getRawType().getGenericInterfaces()) {

      
      TypeToken<? super T> resolvedInterface = (TypeToken)resolveSupertype(interfaceType);
      builder.add(resolvedInterface);
    } 
    return builder.build();
  }
  
  private ImmutableList<TypeToken<? super T>> boundsAsInterfaces(Type[] bounds) {
    ImmutableList.Builder<TypeToken<? super T>> builder = ImmutableList.builder();
    for (Type bound : bounds) {
      
      TypeToken<? super T> boundType = (TypeToken)of(bound);
      if (boundType.getRawType().isInterface()) {
        builder.add(boundType);
      }
    } 
    return builder.build();
  }











  
  public final TypeSet getTypes() {
    return new TypeSet();
  }





  
  public final TypeToken<? super T> getSupertype(Class<? super T> superclass) {
    Preconditions.checkArgument(
        someRawTypeIsSubclassOf(superclass), "%s is not a super class of %s", superclass, this);


    
    if (this.runtimeType instanceof TypeVariable) {
      return getSupertypeFromUpperBounds(superclass, ((TypeVariable)this.runtimeType).getBounds());
    }
    if (this.runtimeType instanceof WildcardType) {
      return getSupertypeFromUpperBounds(superclass, ((WildcardType)this.runtimeType).getUpperBounds());
    }
    if (superclass.isArray()) {
      return getArraySupertype(superclass);
    }

    
    TypeToken<? super T> supertype = (TypeToken)resolveSupertype((toGenericType((Class)superclass)).runtimeType);
    return supertype;
  }





  
  public final TypeToken<? extends T> getSubtype(Class<?> subclass) {
    Preconditions.checkArgument(!(this.runtimeType instanceof TypeVariable), "Cannot get subtype of type variable <%s>", this);
    
    if (this.runtimeType instanceof WildcardType) {
      return getSubtypeFromLowerBounds(subclass, ((WildcardType)this.runtimeType).getLowerBounds());
    }
    
    if (isArray()) {
      return getArraySubtype(subclass);
    }
    
    Preconditions.checkArgument(
        getRawType().isAssignableFrom(subclass), "%s isn't a subclass of %s", subclass, this);
    Type resolvedTypeArgs = resolveTypeArgsForSubclass(subclass);
    
    TypeToken<? extends T> subtype = (TypeToken)of(resolvedTypeArgs);
    Preconditions.checkArgument(subtype
        .isSubtypeOf(this), "%s does not appear to be a subtype of %s", subtype, this);
    return subtype;
  }








  
  public final boolean isSupertypeOf(TypeToken<?> type) {
    return type.isSubtypeOf(getType());
  }








  
  public final boolean isSupertypeOf(Type type) {
    return of(type).isSubtypeOf(getType());
  }








  
  public final boolean isSubtypeOf(TypeToken<?> type) {
    return isSubtypeOf(type.getType());
  }








  
  public final boolean isSubtypeOf(Type supertype) {
    Preconditions.checkNotNull(supertype);
    if (supertype instanceof WildcardType)
    {

      
      return any(((WildcardType)supertype).getLowerBounds()).isSupertypeOf(this.runtimeType);
    }

    
    if (this.runtimeType instanceof WildcardType)
    {
      return any(((WildcardType)this.runtimeType).getUpperBounds()).isSubtypeOf(supertype);
    }

    
    if (this.runtimeType instanceof TypeVariable) {
      return (this.runtimeType.equals(supertype) || 
        any(((TypeVariable)this.runtimeType).getBounds()).isSubtypeOf(supertype));
    }
    if (this.runtimeType instanceof GenericArrayType) {
      return of(supertype).isSupertypeOfArray((GenericArrayType)this.runtimeType);
    }
    
    if (supertype instanceof Class)
      return someRawTypeIsSubclassOf((Class)supertype); 
    if (supertype instanceof ParameterizedType)
      return isSubtypeOfParameterizedType((ParameterizedType)supertype); 
    if (supertype instanceof GenericArrayType) {
      return isSubtypeOfArrayType((GenericArrayType)supertype);
    }
    return false;
  }





  
  public final boolean isArray() {
    return (getComponentType() != null);
  }





  
  public final boolean isPrimitive() {
    return (this.runtimeType instanceof Class && ((Class)this.runtimeType).isPrimitive());
  }






  
  public final TypeToken<T> wrap() {
    if (isPrimitive()) {
      
      Class<T> type = (Class<T>)this.runtimeType;
      return of(Primitives.wrap(type));
    } 
    return this;
  }
  
  private boolean isWrapper() {
    return Primitives.allWrapperTypes().contains(this.runtimeType);
  }






  
  public final TypeToken<T> unwrap() {
    if (isWrapper()) {
      
      Class<T> type = (Class<T>)this.runtimeType;
      return of(Primitives.unwrap(type));
    } 
    return this;
  }




  
  public final TypeToken<?> getComponentType() {
    Type componentType = Types.getComponentType(this.runtimeType);
    if (componentType == null) {
      return null;
    }
    return of(componentType);
  }





  
  public final Invokable<T, Object> method(Method method) {
    Preconditions.checkArgument(
        someRawTypeIsSubclassOf(method.getDeclaringClass()), "%s not declared by %s", method, this);


    
    return new Invokable.MethodInvokable<T>(method)
      {
        Type getGenericReturnType() {
          return TypeToken.this.getCovariantTypeResolver().resolveType(super.getGenericReturnType());
        }

        
        Type[] getGenericParameterTypes() {
          return TypeToken.this.getInvariantTypeResolver().resolveTypesInPlace(super.getGenericParameterTypes());
        }

        
        Type[] getGenericExceptionTypes() {
          return TypeToken.this.getCovariantTypeResolver().resolveTypesInPlace(super.getGenericExceptionTypes());
        }

        
        public TypeToken<T> getOwnerType() {
          return TypeToken.this;
        }

        
        public String toString() {
          return getOwnerType() + "." + super.toString();
        }
      };
  }





  
  public final Invokable<T, T> constructor(Constructor<?> constructor) {
    Preconditions.checkArgument(
        (constructor.getDeclaringClass() == getRawType()), "%s not declared by %s", constructor, 

        
        getRawType());
    return new Invokable.ConstructorInvokable<T>(constructor)
      {
        Type getGenericReturnType() {
          return TypeToken.this.getCovariantTypeResolver().resolveType(super.getGenericReturnType());
        }

        
        Type[] getGenericParameterTypes() {
          return TypeToken.this.getInvariantTypeResolver().resolveTypesInPlace(super.getGenericParameterTypes());
        }

        
        Type[] getGenericExceptionTypes() {
          return TypeToken.this.getCovariantTypeResolver().resolveTypesInPlace(super.getGenericExceptionTypes());
        }

        
        public TypeToken<T> getOwnerType() {
          return TypeToken.this;
        }

        
        public String toString() {
          return getOwnerType() + "(" + Joiner.on(", ").join((Object[])getGenericParameterTypes()) + ")";
        }
      };
  }


  
  public class TypeSet
    extends ForwardingSet<TypeToken<? super T>>
    implements Serializable
  {
    private transient ImmutableSet<TypeToken<? super T>> types;

    
    private static final long serialVersionUID = 0L;


    
    public TypeSet interfaces() {
      return new TypeToken.InterfaceSet(this);
    }

    
    public TypeSet classes() {
      return new TypeToken.ClassSet();
    }

    
    protected Set<TypeToken<? super T>> delegate() {
      ImmutableSet<TypeToken<? super T>> filteredTypes = this.types;
      if (filteredTypes == null) {


        
        ImmutableList<TypeToken<? super T>> collectedTypes = (ImmutableList)TypeToken.TypeCollector.FOR_GENERIC_TYPE.collectTypes(TypeToken.this);
        return 

          
          (Set<TypeToken<? super T>>)(this.types = FluentIterable.from((Iterable)collectedTypes).filter(TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet());
      } 
      return (Set<TypeToken<? super T>>)filteredTypes;
    }





    
    public Set<Class<? super T>> rawTypes() {
      ImmutableList<Class<? super T>> collectedTypes = (ImmutableList)TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes((Iterable<? extends Class<?>>)TypeToken.this.getRawTypes());
      return (Set<Class<? super T>>)ImmutableSet.copyOf((Collection)collectedTypes);
    }
  }
  
  private final class InterfaceSet
    extends TypeSet
  {
    private final transient TypeToken<T>.TypeSet allTypes;
    private transient ImmutableSet<TypeToken<? super T>> interfaces;
    private static final long serialVersionUID = 0L;
    
    InterfaceSet(TypeToken<T>.TypeSet allTypes) {
      this.allTypes = allTypes;
    }

    
    protected Set<TypeToken<? super T>> delegate() {
      ImmutableSet<TypeToken<? super T>> result = this.interfaces;
      if (result == null) {
        return 
          (Set<TypeToken<? super T>>)(this.interfaces = FluentIterable.from((Iterable)this.allTypes).filter(TypeToken.TypeFilter.INTERFACE_ONLY).toSet());
      }
      return (Set<TypeToken<? super T>>)result;
    }


    
    public TypeToken<T>.TypeSet interfaces() {
      return this;
    }




    
    public Set<Class<? super T>> rawTypes() {
      ImmutableList<Class<? super T>> collectedTypes = (ImmutableList)TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes((Iterable<? extends Class<?>>)TypeToken.this.getRawTypes());
      return (Set<Class<? super T>>)FluentIterable.from((Iterable)collectedTypes)
        .filter(new Predicate<Class<?>>()
          {
            public boolean apply(Class<?> type)
            {
              return type.isInterface();
            }
          }).toSet();
    }

    
    public TypeToken<T>.TypeSet classes() {
      throw new UnsupportedOperationException("interfaces().classes() not supported.");
    }
    
    private Object readResolve() {
      return TypeToken.this.getTypes().interfaces();
    }
  }
  
  private final class ClassSet
    extends TypeSet {
    private transient ImmutableSet<TypeToken<? super T>> classes;
    private static final long serialVersionUID = 0L;
    
    private ClassSet() {}
    
    protected Set<TypeToken<? super T>> delegate() {
      ImmutableSet<TypeToken<? super T>> result = this.classes;
      if (result == null) {


        
        ImmutableList<TypeToken<? super T>> collectedTypes = TypeToken.TypeCollector.FOR_GENERIC_TYPE.classesOnly().collectTypes(TypeToken.this);
        return 

          
          (Set<TypeToken<? super T>>)(this.classes = FluentIterable.from((Iterable)collectedTypes).filter(TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet());
      } 
      return (Set<TypeToken<? super T>>)result;
    }


    
    public TypeToken<T>.TypeSet classes() {
      return this;
    }




    
    public Set<Class<? super T>> rawTypes() {
      ImmutableList<Class<? super T>> collectedTypes = TypeToken.TypeCollector.FOR_RAW_TYPE.classesOnly().collectTypes((Iterable<? extends Class<? super T>>)TypeToken.this.getRawTypes());
      return (Set<Class<? super T>>)ImmutableSet.copyOf((Collection)collectedTypes);
    }

    
    public TypeToken<T>.TypeSet interfaces() {
      throw new UnsupportedOperationException("classes().interfaces() not supported.");
    }
    
    private Object readResolve() {
      return TypeToken.this.getTypes().classes();
    }
  }
  
  private enum TypeFilter
    implements Predicate<TypeToken<?>>
  {
    IGNORE_TYPE_VARIABLE_OR_WILDCARD
    {
      public boolean apply(TypeToken<?> type) {
        return (!(type.runtimeType instanceof TypeVariable) && 
          !(type.runtimeType instanceof WildcardType));
      }
    },
    INTERFACE_ONLY
    {
      public boolean apply(TypeToken<?> type) {
        return type.getRawType().isInterface();
      }
    };
  }




  
  public boolean equals(Object o) {
    if (o instanceof TypeToken) {
      TypeToken<?> that = (TypeToken)o;
      return this.runtimeType.equals(that.runtimeType);
    } 
    return false;
  }

  
  public int hashCode() {
    return this.runtimeType.hashCode();
  }

  
  public String toString() {
    return Types.toString(this.runtimeType);
  }



  
  protected Object writeReplace() {
    return of((new TypeResolver()).resolveType(this.runtimeType));
  }




  
  @CanIgnoreReturnValue
  final TypeToken<T> rejectTypeVariables() {
    (new TypeVisitor()
      {
        void visitTypeVariable(TypeVariable<?> type) {
          throw new IllegalArgumentException(TypeToken.this
              .runtimeType + "contains a type variable and is not safe for the operation");
        }

        
        void visitWildcardType(WildcardType type) {
          visit(type.getLowerBounds());
          visit(type.getUpperBounds());
        }

        
        void visitParameterizedType(ParameterizedType type) {
          visit(type.getActualTypeArguments());
          visit(new Type[] { type.getOwnerType() });
        }

        
        void visitGenericArrayType(GenericArrayType type) {
          visit(new Type[] { type.getGenericComponentType() });
        }
      }).visit(new Type[] { this.runtimeType });
    return this;
  }
  
  private boolean someRawTypeIsSubclassOf(Class<?> superclass) {
    for (UnmodifiableIterator<Class<?>> unmodifiableIterator = getRawTypes().iterator(); unmodifiableIterator.hasNext(); ) { Class<?> rawType = unmodifiableIterator.next();
      if (superclass.isAssignableFrom(rawType)) {
        return true;
      } }
    
    return false;
  }
  
  private boolean isSubtypeOfParameterizedType(ParameterizedType supertype) {
    Class<?> matchedClass = of(supertype).getRawType();
    if (!someRawTypeIsSubclassOf(matchedClass)) {
      return false;
    }
    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])matchedClass.getTypeParameters();
    Type[] supertypeArgs = supertype.getActualTypeArguments();
    for (int i = 0; i < arrayOfTypeVariable.length; i++) {
      Type subtypeParam = getCovariantTypeResolver().resolveType(arrayOfTypeVariable[i]);



      
      if (!of(subtypeParam).is(supertypeArgs[i], arrayOfTypeVariable[i])) {
        return false;
      }
    } 


    
    return (Modifier.isStatic(((Class)supertype.getRawType()).getModifiers()) || supertype
      .getOwnerType() == null || 
      isOwnedBySubtypeOf(supertype.getOwnerType()));
  }
  
  private boolean isSubtypeOfArrayType(GenericArrayType supertype) {
    if (this.runtimeType instanceof Class) {
      Class<?> fromClass = (Class)this.runtimeType;
      if (!fromClass.isArray()) {
        return false;
      }
      return of(fromClass.getComponentType()).isSubtypeOf(supertype.getGenericComponentType());
    }  if (this.runtimeType instanceof GenericArrayType) {
      GenericArrayType fromArrayType = (GenericArrayType)this.runtimeType;
      return of(fromArrayType.getGenericComponentType())
        .isSubtypeOf(supertype.getGenericComponentType());
    } 
    return false;
  }

  
  private boolean isSupertypeOfArray(GenericArrayType subtype) {
    if (this.runtimeType instanceof Class) {
      Class<?> thisClass = (Class)this.runtimeType;
      if (!thisClass.isArray()) {
        return thisClass.isAssignableFrom(Object[].class);
      }
      return of(subtype.getGenericComponentType()).isSubtypeOf(thisClass.getComponentType());
    }  if (this.runtimeType instanceof GenericArrayType) {
      return of(subtype.getGenericComponentType())
        .isSubtypeOf(((GenericArrayType)this.runtimeType).getGenericComponentType());
    }
    return false;
  }



























  
  private boolean is(Type formalType, TypeVariable<?> declaration) {
    if (this.runtimeType.equals(formalType)) {
      return true;
    }
    if (formalType instanceof WildcardType) {
      WildcardType your = canonicalizeWildcardType(declaration, (WildcardType)formalType);




      
      return (every(your.getUpperBounds()).isSupertypeOf(this.runtimeType) && 
        every(your.getLowerBounds()).isSubtypeOf(this.runtimeType));
    } 
    return canonicalizeWildcardsInType(this.runtimeType).equals(canonicalizeWildcardsInType(formalType));
  }


















  
  private static Type canonicalizeTypeArg(TypeVariable<?> declaration, Type typeArg) {
    return (typeArg instanceof WildcardType) ? 
      canonicalizeWildcardType(declaration, (WildcardType)typeArg) : 
      canonicalizeWildcardsInType(typeArg);
  }
  
  private static Type canonicalizeWildcardsInType(Type type) {
    if (type instanceof ParameterizedType) {
      return canonicalizeWildcardsInParameterizedType((ParameterizedType)type);
    }
    if (type instanceof GenericArrayType) {
      return Types.newArrayType(
          canonicalizeWildcardsInType(((GenericArrayType)type).getGenericComponentType()));
    }
    return type;
  }




  
  private static WildcardType canonicalizeWildcardType(TypeVariable<?> declaration, WildcardType type) {
    Type[] declared = declaration.getBounds();
    List<Type> upperBounds = new ArrayList<>();
    for (Type bound : type.getUpperBounds()) {
      if (!any(declared).isSubtypeOf(bound)) {
        upperBounds.add(canonicalizeWildcardsInType(bound));
      }
    } 
    return new Types.WildcardTypeImpl(type.getLowerBounds(), upperBounds.<Type>toArray(new Type[0]));
  }

  
  private static ParameterizedType canonicalizeWildcardsInParameterizedType(ParameterizedType type) {
    Class<?> rawType = (Class)type.getRawType();
    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])rawType.getTypeParameters();
    Type[] typeArgs = type.getActualTypeArguments();
    for (int i = 0; i < typeArgs.length; i++) {
      typeArgs[i] = canonicalizeTypeArg(arrayOfTypeVariable[i], typeArgs[i]);
    }
    return Types.newParameterizedTypeWithOwner(type.getOwnerType(), rawType, typeArgs);
  }

  
  private static Bounds every(Type[] bounds) {
    return new Bounds(bounds, false);
  }

  
  private static Bounds any(Type[] bounds) {
    return new Bounds(bounds, true);
  }
  
  private static class Bounds {
    private final Type[] bounds;
    private final boolean target;
    
    Bounds(Type[] bounds, boolean target) {
      this.bounds = bounds;
      this.target = target;
    }
    
    boolean isSubtypeOf(Type supertype) {
      for (Type bound : this.bounds) {
        if (TypeToken.of(bound).isSubtypeOf(supertype) == this.target) {
          return this.target;
        }
      } 
      return !this.target;
    }
    
    boolean isSupertypeOf(Type subtype) {
      TypeToken<?> type = TypeToken.of(subtype);
      for (Type bound : this.bounds) {
        if (type.isSubtypeOf(bound) == this.target) {
          return this.target;
        }
      } 
      return !this.target;
    }
  }
  
  private ImmutableSet<Class<? super T>> getRawTypes() {
    final ImmutableSet.Builder<Class<?>> builder = ImmutableSet.builder();
    (new TypeVisitor()
      {
        void visitTypeVariable(TypeVariable<?> t) {
          visit(t.getBounds());
        }

        
        void visitWildcardType(WildcardType t) {
          visit(t.getUpperBounds());
        }

        
        void visitParameterizedType(ParameterizedType t) {
          builder.add(t.getRawType());
        }

        
        void visitClass(Class<?> t) {
          builder.add(t);
        }

        
        void visitGenericArrayType(GenericArrayType t) {
          builder.add(Types.getArrayClass(TypeToken.of(t.getGenericComponentType()).getRawType()));
        }
      }).visit(new Type[] { this.runtimeType });

    
    ImmutableSet<Class<? super T>> result = builder.build();
    return result;
  }
  
  private boolean isOwnedBySubtypeOf(Type supertype) {
    for (TypeToken<?> type : (Iterable<TypeToken<?>>)getTypes()) {
      Type ownerType = type.getOwnerTypeIfPresent();
      if (ownerType != null && of(ownerType).isSubtypeOf(supertype)) {
        return true;
      }
    } 
    return false;
  }




  
  private Type getOwnerTypeIfPresent() {
    if (this.runtimeType instanceof ParameterizedType)
      return ((ParameterizedType)this.runtimeType).getOwnerType(); 
    if (this.runtimeType instanceof Class) {
      return ((Class)this.runtimeType).getEnclosingClass();
    }
    return null;
  }








  
  @VisibleForTesting
  static <T> TypeToken<? extends T> toGenericType(Class<T> cls) {
    if (cls.isArray()) {
      
      Type arrayOfGenericType = Types.newArrayType(
          
          (toGenericType((Class)cls.getComponentType())).runtimeType);
      
      TypeToken<? extends T> result = (TypeToken)of(arrayOfGenericType);
      return result;
    } 
    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])cls.getTypeParameters();

    
    Type ownerType = (cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers())) ? (toGenericType((Class)cls.getEnclosingClass())).runtimeType : null;

    
    if (arrayOfTypeVariable.length > 0 || (ownerType != null && ownerType != cls.getEnclosingClass())) {


      
      TypeToken<? extends T> type = (TypeToken)of(Types.newParameterizedTypeWithOwner(ownerType, cls, (Type[])arrayOfTypeVariable));
      return type;
    } 
    return of(cls);
  }

  
  private TypeResolver getCovariantTypeResolver() {
    TypeResolver resolver = this.covariantTypeResolver;
    if (resolver == null) {
      resolver = this.covariantTypeResolver = TypeResolver.covariantly(this.runtimeType);
    }
    return resolver;
  }
  
  private TypeResolver getInvariantTypeResolver() {
    TypeResolver resolver = this.invariantTypeResolver;
    if (resolver == null) {
      resolver = this.invariantTypeResolver = TypeResolver.invariantly(this.runtimeType);
    }
    return resolver;
  }

  
  private TypeToken<? super T> getSupertypeFromUpperBounds(Class<? super T> supertype, Type[] upperBounds) {
    for (Type upperBound : upperBounds) {
      
      TypeToken<? super T> bound = (TypeToken)of(upperBound);
      if (bound.isSubtypeOf(supertype)) {
        
        TypeToken<? super T> result = bound.getSupertype(supertype);
        return result;
      } 
    } 
    throw new IllegalArgumentException(supertype + " isn't a super type of " + this);
  }
  
  private TypeToken<? extends T> getSubtypeFromLowerBounds(Class<?> subclass, Type[] lowerBounds) {
    Type[] arrayOfType = lowerBounds; int i = arrayOfType.length; byte b = 0; if (b < i) { Type lowerBound = arrayOfType[b];
      
      TypeToken<? extends T> bound = (TypeToken)of(lowerBound);
      
      return bound.getSubtype(subclass); }
    
    throw new IllegalArgumentException(subclass + " isn't a subclass of " + this);
  }




  
  private TypeToken<? super T> getArraySupertype(Class<? super T> supertype) {
    TypeToken<?> componentType = (TypeToken)Preconditions.checkNotNull(getComponentType(), "%s isn't a super type of %s", supertype, this);

    
    TypeToken<?> componentSupertype = componentType.getSupertype(supertype.getComponentType());



    
    TypeToken<? super T> result = (TypeToken)of(newArrayClassOrGenericArrayType(componentSupertype.runtimeType));
    return result;
  }

  
  private TypeToken<? extends T> getArraySubtype(Class<?> subclass) {
    TypeToken<?> componentSubtype = getComponentType().getSubtype(subclass.getComponentType());



    
    TypeToken<? extends T> result = (TypeToken)of(newArrayClassOrGenericArrayType(componentSubtype.runtimeType));
    return result;
  }




  
  private Type resolveTypeArgsForSubclass(Class<?> subclass) {
    if (this.runtimeType instanceof Class && ((subclass
      .getTypeParameters()).length == 0 || (
      getRawType().getTypeParameters()).length != 0))
    {
      return subclass;
    }






    
    TypeToken<?> genericSubtype = toGenericType(subclass);

    
    Type supertypeWithArgsFromSubtype = (genericSubtype.getSupertype(getRawType())).runtimeType;
    return (new TypeResolver())
      .where(supertypeWithArgsFromSubtype, this.runtimeType)
      .resolveType(genericSubtype.runtimeType);
  }




  
  private static Type newArrayClassOrGenericArrayType(Type componentType) {
    return Types.JavaVersion.JAVA7.newArrayType(componentType);
  }
  
  private static final class SimpleTypeToken<T> extends TypeToken<T> { private static final long serialVersionUID = 0L;
    
    SimpleTypeToken(Type type) {
      super(type);
    } }




  
  private static abstract class TypeCollector<K>
  {
    private TypeCollector() {}


    
    static final TypeCollector<TypeToken<?>> FOR_GENERIC_TYPE = new TypeCollector<TypeToken<?>>()
      {
        Class<?> getRawType(TypeToken<?> type)
        {
          return type.getRawType();
        }

        
        Iterable<? extends TypeToken<?>> getInterfaces(TypeToken<?> type) {
          return (Iterable<? extends TypeToken<?>>)type.getGenericInterfaces();
        }


        
        TypeToken<?> getSuperclass(TypeToken<?> type) {
          return type.getGenericSuperclass();
        }
      };
    
    static final TypeCollector<Class<?>> FOR_RAW_TYPE = new TypeCollector<Class<?>>()
      {
        Class<?> getRawType(Class<?> type)
        {
          return type;
        }

        
        Iterable<? extends Class<?>> getInterfaces(Class<?> type) {
          return Arrays.asList(type.getInterfaces());
        }


        
        Class<?> getSuperclass(Class<?> type) {
          return type.getSuperclass();
        }
      };

    
    final TypeCollector<K> classesOnly() {
      return new ForwardingTypeCollector<K>(this)
        {
          Iterable<? extends K> getInterfaces(K type) {
            return (Iterable<? extends K>)ImmutableSet.of();
          }

          
          ImmutableList<K> collectTypes(Iterable<? extends K> types) {
            ImmutableList.Builder<K> builder = ImmutableList.builder();
            for (K type : types) {
              if (!getRawType(type).isInterface()) {
                builder.add(type);
              }
            } 
            return super.collectTypes((Iterable<? extends K>)builder.build());
          }
        };
    }
    
    final ImmutableList<K> collectTypes(K type) {
      return collectTypes((Iterable<? extends K>)ImmutableList.of(type));
    }

    
    ImmutableList<K> collectTypes(Iterable<? extends K> types) {
      Map<K, Integer> map = Maps.newHashMap();
      for (K type : types) {
        collectTypes(type, map);
      }
      return sortKeysByValue(map, (Comparator<? super Integer>)Ordering.natural().reverse());
    }

    
    @CanIgnoreReturnValue
    private int collectTypes(K type, Map<? super K, Integer> map) {
      Integer existing = map.get(type);
      if (existing != null)
      {
        return existing.intValue();
      }
      
      int aboveMe = getRawType(type).isInterface() ? 1 : 0;
      for (K interfaceType : getInterfaces(type)) {
        aboveMe = Math.max(aboveMe, collectTypes(interfaceType, map));
      }
      K superclass = getSuperclass(type);
      if (superclass != null) {
        aboveMe = Math.max(aboveMe, collectTypes(superclass, map));
      }




      
      map.put(type, Integer.valueOf(aboveMe + 1));
      return aboveMe + 1;
    }

    
    private static <K, V> ImmutableList<K> sortKeysByValue(final Map<K, V> map, final Comparator<? super V> valueComparator) {
      Ordering<K> keyOrdering = new Ordering<K>()
        {
          public int compare(K left, K right)
          {
            return valueComparator.compare(map.get(left), map.get(right));
          }
        };
      return keyOrdering.immutableSortedCopy(map.keySet());
    }
    
    abstract Class<?> getRawType(K param1K);
    
    abstract Iterable<? extends K> getInterfaces(K param1K);
    
    abstract K getSuperclass(K param1K);
    
    private static class ForwardingTypeCollector<K>
      extends TypeCollector<K> {
      private final TypeToken.TypeCollector<K> delegate;
      
      ForwardingTypeCollector(TypeToken.TypeCollector<K> delegate) {
        this.delegate = delegate;
      }

      
      Class<?> getRawType(K type) {
        return this.delegate.getRawType(type);
      }

      
      Iterable<? extends K> getInterfaces(K type) {
        return this.delegate.getInterfaces(type);
      }

      
      K getSuperclass(K type) {
        return this.delegate.getSuperclass(type);
      }
    }
  }
}
