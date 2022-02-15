package org.springframework.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;















































public class OrderComparator
  implements Comparator<Object>
{
  public static final OrderComparator INSTANCE = new OrderComparator();







  
  public Comparator<Object> withSourceProvider(OrderSourceProvider sourceProvider) {
    return (o1, o2) -> doCompare(o1, o2, sourceProvider);
  }

  
  public int compare(@Nullable Object o1, @Nullable Object o2) {
    return doCompare(o1, o2, null);
  }
  
  private int doCompare(@Nullable Object o1, @Nullable Object o2, @Nullable OrderSourceProvider sourceProvider) {
    boolean p1 = o1 instanceof PriorityOrdered;
    boolean p2 = o2 instanceof PriorityOrdered;
    if (p1 && !p2) {
      return -1;
    }
    if (p2 && !p1) {
      return 1;
    }
    
    int i1 = getOrder(o1, sourceProvider);
    int i2 = getOrder(o2, sourceProvider);
    return Integer.compare(i1, i2);
  }







  
  private int getOrder(@Nullable Object obj, @Nullable OrderSourceProvider sourceProvider) {
    Integer order = null;
    if (obj != null && sourceProvider != null) {
      Object orderSource = sourceProvider.getOrderSource(obj);
      if (orderSource != null) {
        if (orderSource.getClass().isArray()) {
          for (Object source : ObjectUtils.toObjectArray(orderSource)) {
            order = findOrder(source);
            if (order != null) {
              break;
            }
          } 
        } else {
          
          order = findOrder(orderSource);
        } 
      }
    } 
    return (order != null) ? order.intValue() : getOrder(obj);
  }







  
  protected int getOrder(@Nullable Object obj) {
    if (obj != null) {
      Integer order = findOrder(obj);
      if (order != null) {
        return order.intValue();
      }
    } 
    return Integer.MAX_VALUE;
  }







  
  @Nullable
  protected Integer findOrder(Object obj) {
    return (obj instanceof Ordered) ? Integer.valueOf(((Ordered)obj).getOrder()) : null;
  }











  
  @Nullable
  public Integer getPriority(Object obj) {
    return null;
  }








  
  public static void sort(List<?> list) {
    if (list.size() > 1) {
      list.sort(INSTANCE);
    }
  }







  
  public static void sort(Object[] array) {
    if (array.length > 1) {
      Arrays.sort(array, INSTANCE);
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
  
  @FunctionalInterface
  public static interface OrderSourceProvider {
    @Nullable
    Object getOrderSource(Object param1Object);
  }
}
