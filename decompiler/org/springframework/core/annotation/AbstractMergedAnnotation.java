package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;



























abstract class AbstractMergedAnnotation<A extends Annotation>
  implements MergedAnnotation<A>
{
  @Nullable
  private volatile A synthesizedAnnotation;
  
  public boolean isDirectlyPresent() {
    return (isPresent() && getDistance() == 0);
  }

  
  public boolean isMetaPresent() {
    return (isPresent() && getDistance() > 0);
  }

  
  public boolean hasNonDefaultValue(String attributeName) {
    return !hasDefaultValue(attributeName);
  }

  
  public byte getByte(String attributeName) {
    return ((Byte)getRequiredAttributeValue(attributeName, Byte.class)).byteValue();
  }

  
  public byte[] getByteArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)byte[].class);
  }

  
  public boolean getBoolean(String attributeName) {
    return ((Boolean)getRequiredAttributeValue(attributeName, Boolean.class)).booleanValue();
  }

  
  public boolean[] getBooleanArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)boolean[].class);
  }

  
  public char getChar(String attributeName) {
    return ((Character)getRequiredAttributeValue(attributeName, Character.class)).charValue();
  }

  
  public char[] getCharArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)char[].class);
  }

  
  public short getShort(String attributeName) {
    return ((Short)getRequiredAttributeValue(attributeName, Short.class)).shortValue();
  }

  
  public short[] getShortArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)short[].class);
  }

  
  public int getInt(String attributeName) {
    return ((Integer)getRequiredAttributeValue(attributeName, Integer.class)).intValue();
  }

  
  public int[] getIntArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)int[].class);
  }

  
  public long getLong(String attributeName) {
    return ((Long)getRequiredAttributeValue(attributeName, Long.class)).longValue();
  }

  
  public long[] getLongArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)long[].class);
  }

  
  public double getDouble(String attributeName) {
    return ((Double)getRequiredAttributeValue(attributeName, Double.class)).doubleValue();
  }

  
  public double[] getDoubleArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)double[].class);
  }

  
  public float getFloat(String attributeName) {
    return ((Float)getRequiredAttributeValue(attributeName, Float.class)).floatValue();
  }

  
  public float[] getFloatArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)float[].class);
  }

  
  public String getString(String attributeName) {
    return getRequiredAttributeValue(attributeName, String.class);
  }

  
  public String[] getStringArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)String[].class);
  }

  
  public Class<?> getClass(String attributeName) {
    return getRequiredAttributeValue(attributeName, Class.class);
  }

  
  public Class<?>[] getClassArray(String attributeName) {
    return getRequiredAttributeValue(attributeName, (Class)Class[].class);
  }

  
  public <E extends Enum<E>> E getEnum(String attributeName, Class<E> type) {
    Assert.notNull(type, "Type must not be null");
    return (E)getRequiredAttributeValue(attributeName, type);
  }


  
  public <E extends Enum<E>> E[] getEnumArray(String attributeName, Class<E> type) {
    Assert.notNull(type, "Type must not be null");
    Class<?> arrayType = Array.newInstance(type, 0).getClass();
    return (E[])getRequiredAttributeValue(attributeName, arrayType);
  }

  
  public Optional<Object> getValue(String attributeName) {
    return getValue(attributeName, Object.class);
  }

  
  public <T> Optional<T> getValue(String attributeName, Class<T> type) {
    return Optional.ofNullable(getAttributeValue(attributeName, type));
  }

  
  public Optional<Object> getDefaultValue(String attributeName) {
    return getDefaultValue(attributeName, Object.class);
  }

  
  public MergedAnnotation<A> filterDefaultValues() {
    return filterAttributes(this::hasNonDefaultValue);
  }

  
  public AnnotationAttributes asAnnotationAttributes(MergedAnnotation.Adapt... adaptations) {
    return (AnnotationAttributes)asMap(mergedAnnotation -> new AnnotationAttributes(mergedAnnotation.getType()), adaptations);
  }



  
  public Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition) throws NoSuchElementException {
    return condition.test(this) ? Optional.<A>of(synthesize()) : Optional.<A>empty();
  }

  
  public A synthesize() {
    if (!isPresent()) {
      throw new NoSuchElementException("Unable to synthesize missing annotation");
    }
    A synthesized = this.synthesizedAnnotation;
    if (synthesized == null) {
      synthesized = createSynthesized();
      this.synthesizedAnnotation = synthesized;
    } 
    return synthesized;
  }
  
  private <T> T getRequiredAttributeValue(String attributeName, Class<T> type) {
    T value = getAttributeValue(attributeName, type);
    if (value == null) {
      throw new NoSuchElementException("No attribute named '" + attributeName + "' present in merged annotation " + 
          getType().getName());
    }
    return value;
  }
  
  @Nullable
  protected abstract <T> T getAttributeValue(String paramString, Class<T> paramClass);
  
  protected abstract A createSynthesized();
}
