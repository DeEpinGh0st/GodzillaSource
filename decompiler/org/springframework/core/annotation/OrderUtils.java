package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;





























public abstract class OrderUtils
{
  private static final Object NOT_ANNOTATED = new Object();

  
  private static final String JAVAX_PRIORITY_ANNOTATION = "javax.annotation.Priority";
  
  private static final Map<AnnotatedElement, Object> orderCache = (Map<AnnotatedElement, Object>)new ConcurrentReferenceHashMap(64);










  
  public static int getOrder(Class<?> type, int defaultOrder) {
    Integer order = getOrder(type);
    return (order != null) ? order.intValue() : defaultOrder;
  }








  
  @Nullable
  public static Integer getOrder(Class<?> type, @Nullable Integer defaultOrder) {
    Integer order = getOrder(type);
    return (order != null) ? order : defaultOrder;
  }







  
  @Nullable
  public static Integer getOrder(Class<?> type) {
    return getOrder(type);
  }







  
  @Nullable
  public static Integer getOrder(AnnotatedElement element) {
    return getOrderFromAnnotations(element, MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY));
  }








  
  @Nullable
  static Integer getOrderFromAnnotations(AnnotatedElement element, MergedAnnotations annotations) {
    if (!(element instanceof Class)) {
      return findOrder(annotations);
    }
    Object cached = orderCache.get(element);
    if (cached != null) {
      return (cached instanceof Integer) ? (Integer)cached : null;
    }
    Integer result = findOrder(annotations);
    orderCache.put(element, (result != null) ? result : NOT_ANNOTATED);
    return result;
  }
  
  @Nullable
  private static Integer findOrder(MergedAnnotations annotations) {
    MergedAnnotation<Order> orderAnnotation = annotations.get(Order.class);
    if (orderAnnotation.isPresent()) {
      return Integer.valueOf(orderAnnotation.getInt("value"));
    }
    MergedAnnotation<?> priorityAnnotation = annotations.get("javax.annotation.Priority");
    if (priorityAnnotation.isPresent()) {
      return Integer.valueOf(priorityAnnotation.getInt("value"));
    }
    return null;
  }






  
  @Nullable
  public static Integer getPriority(Class<?> type) {
    return MergedAnnotations.from(type, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).<Annotation>get("javax.annotation.Priority")
      .getValue("value", Integer.class).orElse(null);
  }
}
