package org.springframework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import org.springframework.lang.Nullable;







































public abstract class CollectionUtils
{
  static final float DEFAULT_LOAD_FACTOR = 0.75F;
  
  public static boolean isEmpty(@Nullable Collection<?> collection) {
    return (collection == null || collection.isEmpty());
  }






  
  public static boolean isEmpty(@Nullable Map<?, ?> map) {
    return (map == null || map.isEmpty());
  }













  
  public static <K, V> HashMap<K, V> newHashMap(int expectedSize) {
    return new HashMap<>((int)(expectedSize / 0.75F), 0.75F);
  }













  
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int expectedSize) {
    return new LinkedHashMap<>((int)(expectedSize / 0.75F), 0.75F);
  }












  
  public static List<?> arrayToList(@Nullable Object source) {
    return Arrays.asList(ObjectUtils.toObjectArray(source));
  }






  
  public static <E> void mergeArrayIntoCollection(@Nullable Object array, Collection<E> collection) {
    Object[] arr = ObjectUtils.toObjectArray(array);
    for (Object elem : arr) {
      collection.add((E)elem);
    }
  }









  
  public static <K, V> void mergePropertiesIntoMap(@Nullable Properties props, Map<K, V> map) {
    if (props != null) {
      for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements(); ) {
        String key = (String)en.nextElement();
        Object value = props.get(key);
        if (value == null)
        {
          value = props.getProperty(key);
        }
        map.put((K)key, (V)value);
      } 
    }
  }







  
  public static boolean contains(@Nullable Iterator<?> iterator, Object element) {
    if (iterator != null) {
      while (iterator.hasNext()) {
        Object candidate = iterator.next();
        if (ObjectUtils.nullSafeEquals(candidate, element)) {
          return true;
        }
      } 
    }
    return false;
  }






  
  public static boolean contains(@Nullable Enumeration<?> enumeration, Object element) {
    if (enumeration != null) {
      while (enumeration.hasMoreElements()) {
        Object candidate = enumeration.nextElement();
        if (ObjectUtils.nullSafeEquals(candidate, element)) {
          return true;
        }
      } 
    }
    return false;
  }








  
  public static boolean containsInstance(@Nullable Collection<?> collection, Object element) {
    if (collection != null) {
      for (Object candidate : collection) {
        if (candidate == element) {
          return true;
        }
      } 
    }
    return false;
  }







  
  public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
    return (findFirstMatch(source, candidates) != null);
  }










  
  @Nullable
  public static <E> E findFirstMatch(Collection<?> source, Collection<E> candidates) {
    if (isEmpty(source) || isEmpty(candidates)) {
      return null;
    }
    for (E candidate : candidates) {
      if (source.contains(candidate)) {
        return candidate;
      }
    } 
    return null;
  }








  
  @Nullable
  public static <T> T findValueOfType(Collection<?> collection, @Nullable Class<T> type) {
    if (isEmpty(collection)) {
      return null;
    }
    T value = null;
    for (Object element : collection) {
      if (type == null || type.isInstance(element)) {
        if (value != null)
        {
          return null;
        }
        value = (T)element;
      } 
    } 
    return value;
  }









  
  @Nullable
  public static Object findValueOfType(Collection<?> collection, Class<?>[] types) {
    if (isEmpty(collection) || ObjectUtils.isEmpty((Object[])types)) {
      return null;
    }
    for (Class<?> type : types) {
      Object value = findValueOfType(collection, type);
      if (value != null) {
        return value;
      }
    } 
    return null;
  }






  
  public static boolean hasUniqueObject(Collection<?> collection) {
    if (isEmpty(collection)) {
      return false;
    }
    boolean hasCandidate = false;
    Object candidate = null;
    for (Object elem : collection) {
      if (!hasCandidate) {
        hasCandidate = true;
        candidate = elem; continue;
      } 
      if (candidate != elem) {
        return false;
      }
    } 
    return true;
  }






  
  @Nullable
  public static Class<?> findCommonElementType(Collection<?> collection) {
    if (isEmpty(collection)) {
      return null;
    }
    Class<?> candidate = null;
    for (Object val : collection) {
      if (val != null) {
        if (candidate == null) {
          candidate = val.getClass(); continue;
        } 
        if (candidate != val.getClass()) {
          return null;
        }
      } 
    } 
    return candidate;
  }










  
  @Nullable
  public static <T> T firstElement(@Nullable Set<T> set) {
    if (isEmpty(set)) {
      return null;
    }
    if (set instanceof SortedSet) {
      return ((SortedSet<T>)set).first();
    }
    
    Iterator<T> it = set.iterator();
    T first = null;
    if (it.hasNext()) {
      first = it.next();
    }
    return first;
  }






  
  @Nullable
  public static <T> T firstElement(@Nullable List<T> list) {
    if (isEmpty(list)) {
      return null;
    }
    return list.get(0);
  }










  
  @Nullable
  public static <T> T lastElement(@Nullable Set<T> set) {
    if (isEmpty(set)) {
      return null;
    }
    if (set instanceof SortedSet) {
      return ((SortedSet<T>)set).last();
    }

    
    Iterator<T> it = set.iterator();
    T last = null;
    while (it.hasNext()) {
      last = it.next();
    }
    return last;
  }






  
  @Nullable
  public static <T> T lastElement(@Nullable List<T> list) {
    if (isEmpty(list)) {
      return null;
    }
    return list.get(list.size() - 1);
  }





  
  public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
    ArrayList<A> elements = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      elements.add((A)enumeration.nextElement());
    }
    return elements.toArray(array);
  }





  
  public static <E> Iterator<E> toIterator(@Nullable Enumeration<E> enumeration) {
    return (enumeration != null) ? new EnumerationIterator<>(enumeration) : Collections.<E>emptyIterator();
  }






  
  public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> targetMap) {
    return new MultiValueMapAdapter<>(targetMap);
  }









  
  public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(MultiValueMap<? extends K, ? extends V> targetMap) {
    Assert.notNull(targetMap, "'targetMap' must not be null");
    Map<K, List<V>> result = newLinkedHashMap(targetMap.size());
    targetMap.forEach((key, value) -> {
          List<? extends V> values = Collections.unmodifiableList(value);
          result.put(key, values);
        });
    Map<K, List<V>> unmodifiableMap = Collections.unmodifiableMap(result);
    return toMultiValueMap(unmodifiableMap);
  }


  
  private static class EnumerationIterator<E>
    implements Iterator<E>
  {
    private final Enumeration<E> enumeration;

    
    public EnumerationIterator(Enumeration<E> enumeration) {
      this.enumeration = enumeration;
    }

    
    public boolean hasNext() {
      return this.enumeration.hasMoreElements();
    }

    
    public E next() {
      return this.enumeration.nextElement();
    }

    
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Not supported");
    }
  }
}
