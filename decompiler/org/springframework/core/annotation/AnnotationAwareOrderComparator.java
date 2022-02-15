package org.springframework.core.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.OrderComparator;
import org.springframework.lang.Nullable;







































public class AnnotationAwareOrderComparator
  extends OrderComparator
{
  public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();








  
  @Nullable
  protected Integer findOrder(Object obj) {
    Integer order = super.findOrder(obj);
    if (order != null) {
      return order;
    }
    return findOrderFromAnnotation(obj);
  }
  
  @Nullable
  private Integer findOrderFromAnnotation(Object obj) {
    AnnotatedElement element = (obj instanceof AnnotatedElement) ? (AnnotatedElement)obj : obj.getClass();
    MergedAnnotations annotations = MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
    Integer order = OrderUtils.getOrderFromAnnotations(element, annotations);
    if (order == null && obj instanceof DecoratingProxy) {
      return findOrderFromAnnotation(((DecoratingProxy)obj).getDecoratedClass());
    }
    return order;
  }







  
  @Nullable
  public Integer getPriority(Object obj) {
    if (obj instanceof Class) {
      return OrderUtils.getPriority((Class)obj);
    }
    Integer priority = OrderUtils.getPriority(obj.getClass());
    if (priority == null && obj instanceof DecoratingProxy) {
      return getPriority(((DecoratingProxy)obj).getDecoratedClass());
    }
    return priority;
  }








  
  public static void sort(List<?> list) {
    if (list.size() > 1) {
      list.sort((Comparator<?>)INSTANCE);
    }
  }







  
  public static void sort(Object[] array) {
    if (array.length > 1) {
      Arrays.sort(array, (Comparator<? super Object>)INSTANCE);
    }
  }








  
  public static void sortIfNecessary(Object value) {
    if (value instanceof Object[]) {
      sort((Object[])value);
    }
    else if (value instanceof List) {
      sort((List)value);
    } 
  }
}
