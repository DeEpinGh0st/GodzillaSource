package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;


























final class MissingMergedAnnotation<A extends Annotation>
  extends AbstractMergedAnnotation<A>
{
  private static final MissingMergedAnnotation<?> INSTANCE = new MissingMergedAnnotation();






  
  public Class<A> getType() {
    throw new NoSuchElementException("Unable to get type for missing annotation");
  }

  
  public boolean isPresent() {
    return false;
  }

  
  @Nullable
  public Object getSource() {
    return null;
  }

  
  @Nullable
  public MergedAnnotation<?> getMetaSource() {
    return null;
  }

  
  public MergedAnnotation<?> getRoot() {
    return this;
  }

  
  public List<Class<? extends Annotation>> getMetaTypes() {
    return Collections.emptyList();
  }

  
  public int getDistance() {
    return -1;
  }

  
  public int getAggregateIndex() {
    return -1;
  }

  
  public boolean hasNonDefaultValue(String attributeName) {
    throw new NoSuchElementException("Unable to check non-default value for missing annotation");
  }


  
  public boolean hasDefaultValue(String attributeName) {
    throw new NoSuchElementException("Unable to check default value for missing annotation");
  }


  
  public <T> Optional<T> getValue(String attributeName, Class<T> type) {
    return Optional.empty();
  }

  
  public <T> Optional<T> getDefaultValue(@Nullable String attributeName, Class<T> type) {
    return Optional.empty();
  }

  
  public MergedAnnotation<A> filterAttributes(Predicate<String> predicate) {
    return this;
  }

  
  public MergedAnnotation<A> withNonMergedAttributes() {
    return this;
  }

  
  public AnnotationAttributes asAnnotationAttributes(MergedAnnotation.Adapt... adaptations) {
    return new AnnotationAttributes();
  }

  
  public Map<String, Object> asMap(MergedAnnotation.Adapt... adaptations) {
    return Collections.emptyMap();
  }

  
  public <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory, MergedAnnotation.Adapt... adaptations) {
    return factory.apply(this);
  }

  
  public String toString() {
    return "(missing)";
  }



  
  public <T extends Annotation> MergedAnnotation<T> getAnnotation(String attributeName, Class<T> type) throws NoSuchElementException {
    throw new NoSuchElementException("Unable to get attribute value for missing annotation");
  }




  
  public <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String attributeName, Class<T> type) throws NoSuchElementException {
    throw new NoSuchElementException("Unable to get attribute value for missing annotation");
  }


  
  protected <T> T getAttributeValue(String attributeName, Class<T> type) {
    throw new NoSuchElementException("Unable to get attribute value for missing annotation");
  }


  
  protected A createSynthesized() {
    throw new NoSuchElementException("Unable to synthesize missing annotation");
  }


  
  static <A extends Annotation> MergedAnnotation<A> getInstance() {
    return (MergedAnnotation)INSTANCE;
  }
}
