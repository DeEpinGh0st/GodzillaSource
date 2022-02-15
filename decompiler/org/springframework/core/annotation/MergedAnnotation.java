package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;
























































































































































































































































































































































































































public interface MergedAnnotation<A extends Annotation>
{
  public static final String VALUE = "value";
  
  Class<A> getType();
  
  boolean isPresent();
  
  boolean isDirectlyPresent();
  
  boolean isMetaPresent();
  
  int getDistance();
  
  int getAggregateIndex();
  
  @Nullable
  Object getSource();
  
  @Nullable
  MergedAnnotation<?> getMetaSource();
  
  MergedAnnotation<?> getRoot();
  
  List<Class<? extends Annotation>> getMetaTypes();
  
  boolean hasNonDefaultValue(String paramString);
  
  boolean hasDefaultValue(String paramString) throws NoSuchElementException;
  
  byte getByte(String paramString) throws NoSuchElementException;
  
  byte[] getByteArray(String paramString) throws NoSuchElementException;
  
  boolean getBoolean(String paramString) throws NoSuchElementException;
  
  boolean[] getBooleanArray(String paramString) throws NoSuchElementException;
  
  char getChar(String paramString) throws NoSuchElementException;
  
  char[] getCharArray(String paramString) throws NoSuchElementException;
  
  short getShort(String paramString) throws NoSuchElementException;
  
  short[] getShortArray(String paramString) throws NoSuchElementException;
  
  int getInt(String paramString) throws NoSuchElementException;
  
  int[] getIntArray(String paramString) throws NoSuchElementException;
  
  long getLong(String paramString) throws NoSuchElementException;
  
  long[] getLongArray(String paramString) throws NoSuchElementException;
  
  double getDouble(String paramString) throws NoSuchElementException;
  
  double[] getDoubleArray(String paramString) throws NoSuchElementException;
  
  float getFloat(String paramString) throws NoSuchElementException;
  
  float[] getFloatArray(String paramString) throws NoSuchElementException;
  
  String getString(String paramString) throws NoSuchElementException;
  
  String[] getStringArray(String paramString) throws NoSuchElementException;
  
  Class<?> getClass(String paramString) throws NoSuchElementException;
  
  Class<?>[] getClassArray(String paramString) throws NoSuchElementException;
  
  <E extends Enum<E>> E getEnum(String paramString, Class<E> paramClass) throws NoSuchElementException;
  
  <E extends Enum<E>> E[] getEnumArray(String paramString, Class<E> paramClass) throws NoSuchElementException;
  
  <T extends Annotation> MergedAnnotation<T> getAnnotation(String paramString, Class<T> paramClass) throws NoSuchElementException;
  
  <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String paramString, Class<T> paramClass) throws NoSuchElementException;
  
  Optional<Object> getValue(String paramString);
  
  <T> Optional<T> getValue(String paramString, Class<T> paramClass);
  
  Optional<Object> getDefaultValue(String paramString);
  
  <T> Optional<T> getDefaultValue(String paramString, Class<T> paramClass);
  
  MergedAnnotation<A> filterDefaultValues();
  
  MergedAnnotation<A> filterAttributes(Predicate<String> paramPredicate);
  
  MergedAnnotation<A> withNonMergedAttributes();
  
  AnnotationAttributes asAnnotationAttributes(Adapt... paramVarArgs);
  
  Map<String, Object> asMap(Adapt... paramVarArgs);
  
  <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> paramFunction, Adapt... paramVarArgs);
  
  A synthesize() throws NoSuchElementException;
  
  Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> paramPredicate) throws NoSuchElementException;
  
  static <A extends Annotation> MergedAnnotation<A> missing() {
    return MissingMergedAnnotation.getInstance();
  }






  
  static <A extends Annotation> MergedAnnotation<A> from(A annotation) {
    return from(null, annotation);
  }









  
  static <A extends Annotation> MergedAnnotation<A> from(@Nullable Object source, A annotation) {
    return TypeMappedAnnotation.from(source, annotation);
  }







  
  static <A extends Annotation> MergedAnnotation<A> of(Class<A> annotationType) {
    return of(null, annotationType, null);
  }











  
  static <A extends Annotation> MergedAnnotation<A> of(Class<A> annotationType, @Nullable Map<String, ?> attributes) {
    return of(null, annotationType, attributes);
  }













  
  static <A extends Annotation> MergedAnnotation<A> of(@Nullable AnnotatedElement source, Class<A> annotationType, @Nullable Map<String, ?> attributes) {
    return of(null, source, annotationType, attributes);
  }















  
  static <A extends Annotation> MergedAnnotation<A> of(@Nullable ClassLoader classLoader, @Nullable Object source, Class<A> annotationType, @Nullable Map<String, ?> attributes) {
    return TypeMappedAnnotation.of(classLoader, source, annotationType, attributes);
  }









  
  public enum Adapt
  {
    CLASS_TO_STRING,




    
    ANNOTATION_TO_MAP;
    
    protected final boolean isIn(Adapt... adaptations) {
      for (Adapt candidate : adaptations) {
        if (candidate == this) {
          return true;
        }
      } 
      return false;
    }













    
    private static <T> void addIfTrue(Set<T> result, T value, boolean test) {
      if (test)
        result.add(value); 
    }
  }
}
