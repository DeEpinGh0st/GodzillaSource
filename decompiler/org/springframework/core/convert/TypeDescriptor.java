package org.springframework.core.convert;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

































public class TypeDescriptor
  implements Serializable
{
  private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
  
  private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<>(32);
  
  private static final Class<?>[] CACHED_COMMON_TYPES = new Class[] { boolean.class, Boolean.class, byte.class, Byte.class, char.class, Character.class, double.class, Double.class, float.class, Float.class, int.class, Integer.class, long.class, Long.class, short.class, Short.class, String.class, Object.class };
  private final Class<?> type;
  private final ResolvableType resolvableType;
  private final AnnotatedElementAdapter annotatedElement;
  
  static {
    for (Class<?> preCachedClass : CACHED_COMMON_TYPES) {
      commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
    }
  }














  
  public TypeDescriptor(MethodParameter methodParameter) {
    this.resolvableType = ResolvableType.forMethodParameter(methodParameter);
    this.type = this.resolvableType.resolve(methodParameter.getNestedParameterType());
    this
      .annotatedElement = new AnnotatedElementAdapter((methodParameter.getParameterIndex() == -1) ? methodParameter.getMethodAnnotations() : methodParameter.getParameterAnnotations());
  }





  
  public TypeDescriptor(Field field) {
    this.resolvableType = ResolvableType.forField(field);
    this.type = this.resolvableType.resolve(field.getType());
    this.annotatedElement = new AnnotatedElementAdapter(field.getAnnotations());
  }






  
  public TypeDescriptor(Property property) {
    Assert.notNull(property, "Property must not be null");
    this.resolvableType = ResolvableType.forMethodParameter(property.getMethodParameter());
    this.type = this.resolvableType.resolve(property.getType());
    this.annotatedElement = new AnnotatedElementAdapter(property.getAnnotations());
  }










  
  public TypeDescriptor(ResolvableType resolvableType, @Nullable Class<?> type, @Nullable Annotation[] annotations) {
    this.resolvableType = resolvableType;
    this.type = (type != null) ? type : resolvableType.toClass();
    this.annotatedElement = new AnnotatedElementAdapter(annotations);
  }







  
  public Class<?> getObjectType() {
    return ClassUtils.resolvePrimitiveIfNecessary(getType());
  }








  
  public Class<?> getType() {
    return this.type;
  }




  
  public ResolvableType getResolvableType() {
    return this.resolvableType;
  }







  
  public Object getSource() {
    return this.resolvableType.getSource();
  }
















  
  public TypeDescriptor narrow(@Nullable Object value) {
    if (value == null) {
      return this;
    }
    ResolvableType narrowed = ResolvableType.forType(value.getClass(), getResolvableType());
    return new TypeDescriptor(narrowed, value.getClass(), getAnnotations());
  }








  
  @Nullable
  public TypeDescriptor upcast(@Nullable Class<?> superType) {
    if (superType == null) {
      return null;
    }
    Assert.isAssignable(superType, getType());
    return new TypeDescriptor(getResolvableType().as(superType), superType, getAnnotations());
  }



  
  public String getName() {
    return ClassUtils.getQualifiedName(getType());
  }



  
  public boolean isPrimitive() {
    return getType().isPrimitive();
  }




  
  public Annotation[] getAnnotations() {
    return this.annotatedElement.getAnnotations();
  }







  
  public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
    if (this.annotatedElement.isEmpty())
    {
      
      return false;
    }
    return AnnotatedElementUtils.isAnnotated(this.annotatedElement, annotationType);
  }






  
  @Nullable
  public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
    if (this.annotatedElement.isEmpty())
    {
      
      return null;
    }
    return (T)AnnotatedElementUtils.getMergedAnnotation(this.annotatedElement, annotationType);
  }














  
  public boolean isAssignableTo(TypeDescriptor typeDescriptor) {
    boolean typesAssignable = typeDescriptor.getObjectType().isAssignableFrom(getObjectType());
    if (!typesAssignable) {
      return false;
    }
    if (isArray() && typeDescriptor.isArray()) {
      return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
    }
    if (isCollection() && typeDescriptor.isCollection()) {
      return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
    }
    if (isMap() && typeDescriptor.isMap()) {
      return (isNestedAssignable(getMapKeyTypeDescriptor(), typeDescriptor.getMapKeyTypeDescriptor()) && 
        isNestedAssignable(getMapValueTypeDescriptor(), typeDescriptor.getMapValueTypeDescriptor()));
    }
    
    return true;
  }



  
  private boolean isNestedAssignable(@Nullable TypeDescriptor nestedTypeDescriptor, @Nullable TypeDescriptor otherNestedTypeDescriptor) {
    return (nestedTypeDescriptor == null || otherNestedTypeDescriptor == null || nestedTypeDescriptor
      .isAssignableTo(otherNestedTypeDescriptor));
  }



  
  public boolean isCollection() {
    return Collection.class.isAssignableFrom(getType());
  }



  
  public boolean isArray() {
    return getType().isArray();
  }









  
  @Nullable
  public TypeDescriptor getElementTypeDescriptor() {
    if (getResolvableType().isArray()) {
      return new TypeDescriptor(getResolvableType().getComponentType(), null, getAnnotations());
    }
    if (Stream.class.isAssignableFrom(getType())) {
      return getRelatedIfResolvable(this, getResolvableType().as(Stream.class).getGeneric(new int[] { 0 }));
    }
    return getRelatedIfResolvable(this, getResolvableType().asCollection().getGeneric(new int[] { 0 }));
  }

















  
  @Nullable
  public TypeDescriptor elementTypeDescriptor(Object element) {
    return narrow(element, getElementTypeDescriptor());
  }



  
  public boolean isMap() {
    return Map.class.isAssignableFrom(getType());
  }








  
  @Nullable
  public TypeDescriptor getMapKeyTypeDescriptor() {
    Assert.state(isMap(), "Not a [java.util.Map]");
    return getRelatedIfResolvable(this, getResolvableType().asMap().getGeneric(new int[] { 0 }));
  }

















  
  @Nullable
  public TypeDescriptor getMapKeyTypeDescriptor(Object mapKey) {
    return narrow(mapKey, getMapKeyTypeDescriptor());
  }









  
  @Nullable
  public TypeDescriptor getMapValueTypeDescriptor() {
    Assert.state(isMap(), "Not a [java.util.Map]");
    return getRelatedIfResolvable(this, getResolvableType().asMap().getGeneric(new int[] { 1 }));
  }

















  
  @Nullable
  public TypeDescriptor getMapValueTypeDescriptor(Object mapValue) {
    return narrow(mapValue, getMapValueTypeDescriptor());
  }
  
  @Nullable
  private TypeDescriptor narrow(@Nullable Object value, @Nullable TypeDescriptor typeDescriptor) {
    if (typeDescriptor != null) {
      return typeDescriptor.narrow(value);
    }
    if (value != null) {
      return narrow(value);
    }
    return null;
  }

  
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof TypeDescriptor)) {
      return false;
    }
    TypeDescriptor otherDesc = (TypeDescriptor)other;
    if (getType() != otherDesc.getType()) {
      return false;
    }
    if (!annotationsMatch(otherDesc)) {
      return false;
    }
    if (isCollection() || isArray()) {
      return ObjectUtils.nullSafeEquals(getElementTypeDescriptor(), otherDesc.getElementTypeDescriptor());
    }
    if (isMap()) {
      return (ObjectUtils.nullSafeEquals(getMapKeyTypeDescriptor(), otherDesc.getMapKeyTypeDescriptor()) && 
        ObjectUtils.nullSafeEquals(getMapValueTypeDescriptor(), otherDesc.getMapValueTypeDescriptor()));
    }
    
    return true;
  }

  
  private boolean annotationsMatch(TypeDescriptor otherDesc) {
    Annotation[] anns = getAnnotations();
    Annotation[] otherAnns = otherDesc.getAnnotations();
    if (anns == otherAnns) {
      return true;
    }
    if (anns.length != otherAnns.length) {
      return false;
    }
    if (anns.length > 0) {
      for (int i = 0; i < anns.length; i++) {
        if (!annotationEquals(anns[i], otherAnns[i])) {
          return false;
        }
      } 
    }
    return true;
  }

  
  private boolean annotationEquals(Annotation ann, Annotation otherAnn) {
    return (ann == otherAnn || (ann.getClass() == otherAnn.getClass() && ann.equals(otherAnn)));
  }

  
  public int hashCode() {
    return getType().hashCode();
  }

  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Annotation ann : getAnnotations()) {
      builder.append('@').append(ann.annotationType().getName()).append(' ');
    }
    builder.append(getResolvableType());
    return builder.toString();
  }










  
  @Nullable
  public static TypeDescriptor forObject(@Nullable Object source) {
    return (source != null) ? valueOf(source.getClass()) : null;
  }










  
  public static TypeDescriptor valueOf(@Nullable Class<?> type) {
    if (type == null) {
      type = Object.class;
    }
    TypeDescriptor desc = commonTypesCache.get(type);
    return (desc != null) ? desc : new TypeDescriptor(ResolvableType.forClass(type), null, null);
  }












  
  public static TypeDescriptor collection(Class<?> collectionType, @Nullable TypeDescriptor elementTypeDescriptor) {
    Assert.notNull(collectionType, "Collection type must not be null");
    if (!Collection.class.isAssignableFrom(collectionType)) {
      throw new IllegalArgumentException("Collection type must be a [java.util.Collection]");
    }
    ResolvableType element = (elementTypeDescriptor != null) ? elementTypeDescriptor.resolvableType : null;
    return new TypeDescriptor(ResolvableType.forClassWithGenerics(collectionType, new ResolvableType[] { element }), null, null);
  }
















  
  public static TypeDescriptor map(Class<?> mapType, @Nullable TypeDescriptor keyTypeDescriptor, @Nullable TypeDescriptor valueTypeDescriptor) {
    Assert.notNull(mapType, "Map type must not be null");
    if (!Map.class.isAssignableFrom(mapType)) {
      throw new IllegalArgumentException("Map type must be a [java.util.Map]");
    }
    ResolvableType key = (keyTypeDescriptor != null) ? keyTypeDescriptor.resolvableType : null;
    ResolvableType value = (valueTypeDescriptor != null) ? valueTypeDescriptor.resolvableType : null;
    return new TypeDescriptor(ResolvableType.forClassWithGenerics(mapType, new ResolvableType[] { key, value }), null, null);
  }










  
  @Nullable
  public static TypeDescriptor array(@Nullable TypeDescriptor elementTypeDescriptor) {
    if (elementTypeDescriptor == null) {
      return null;
    }
    return new TypeDescriptor(ResolvableType.forArrayComponent(elementTypeDescriptor.resolvableType), null, elementTypeDescriptor
        .getAnnotations());
  }






















  
  @Nullable
  public static TypeDescriptor nested(MethodParameter methodParameter, int nestingLevel) {
    if (methodParameter.getNestingLevel() != 1) {
      throw new IllegalArgumentException("MethodParameter nesting level must be 1: use the nestingLevel parameter to specify the desired nestingLevel for nested type traversal");
    }
    
    return nested(new TypeDescriptor(methodParameter), nestingLevel);
  }





















  
  @Nullable
  public static TypeDescriptor nested(Field field, int nestingLevel) {
    return nested(new TypeDescriptor(field), nestingLevel);
  }





















  
  @Nullable
  public static TypeDescriptor nested(Property property, int nestingLevel) {
    return nested(new TypeDescriptor(property), nestingLevel);
  }
  
  @Nullable
  private static TypeDescriptor nested(TypeDescriptor typeDescriptor, int nestingLevel) {
    ResolvableType nested = typeDescriptor.resolvableType;
    for (int i = 0; i < nestingLevel; i++) {
      if (Object.class != nested.getType())
      {


        
        nested = nested.getNested(2);
      }
    } 
    if (nested == ResolvableType.NONE) {
      return null;
    }
    return getRelatedIfResolvable(typeDescriptor, nested);
  }
  
  @Nullable
  private static TypeDescriptor getRelatedIfResolvable(TypeDescriptor source, ResolvableType type) {
    if (type.resolve() == null) {
      return null;
    }
    return new TypeDescriptor(type, null, source.getAnnotations());
  }



  
  private class AnnotatedElementAdapter
    implements AnnotatedElement, Serializable
  {
    @Nullable
    private final Annotation[] annotations;



    
    public AnnotatedElementAdapter(Annotation[] annotations) {
      this.annotations = annotations;
    }

    
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
      for (Annotation annotation : getAnnotations()) {
        if (annotation.annotationType() == annotationClass) {
          return true;
        }
      } 
      return false;
    }


    
    @Nullable
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      for (Annotation annotation : getAnnotations()) {
        if (annotation.annotationType() == annotationClass) {
          return (T)annotation;
        }
      } 
      return null;
    }

    
    public Annotation[] getAnnotations() {
      return (this.annotations != null) ? (Annotation[])this.annotations.clone() : TypeDescriptor.EMPTY_ANNOTATION_ARRAY;
    }

    
    public Annotation[] getDeclaredAnnotations() {
      return getAnnotations();
    }
    
    public boolean isEmpty() {
      return ObjectUtils.isEmpty((Object[])this.annotations);
    }

    
    public boolean equals(@Nullable Object other) {
      return (this == other || (other instanceof AnnotatedElementAdapter && 
        Arrays.equals((Object[])this.annotations, (Object[])((AnnotatedElementAdapter)other).annotations)));
    }

    
    public int hashCode() {
      return Arrays.hashCode((Object[])this.annotations);
    }

    
    public String toString() {
      return TypeDescriptor.this.toString();
    }
  }
}
