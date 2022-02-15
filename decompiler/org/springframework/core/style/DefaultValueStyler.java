package org.springframework.core.style;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
































public class DefaultValueStyler
  implements ValueStyler
{
  private static final String EMPTY = "[[empty]]";
  private static final String NULL = "[null]";
  private static final String COLLECTION = "collection";
  private static final String SET = "set";
  private static final String LIST = "list";
  private static final String MAP = "map";
  private static final String EMPTY_MAP = "map[[empty]]";
  private static final String ARRAY = "array";
  
  public String style(@Nullable Object value) {
    if (value == null) {
      return "[null]";
    }
    if (value instanceof String) {
      return "'" + value + "'";
    }
    if (value instanceof Class) {
      return ClassUtils.getShortName((Class)value);
    }
    if (value instanceof Method) {
      Method method = (Method)value;
      return method.getName() + "@" + ClassUtils.getShortName(method.getDeclaringClass());
    } 
    if (value instanceof Map) {
      return style((Map<?, ?>)value);
    }
    if (value instanceof Map.Entry) {
      return style((Map.Entry<?, ?>)value);
    }
    if (value instanceof Collection) {
      return style((Collection)value);
    }
    if (value.getClass().isArray()) {
      return styleArray(ObjectUtils.toObjectArray(value));
    }
    
    return String.valueOf(value);
  }

  
  private <K, V> String style(Map<K, V> value) {
    if (value.isEmpty()) {
      return "map[[empty]]";
    }
    
    StringJoiner result = new StringJoiner(", ", "[", "]");
    for (Map.Entry<K, V> entry : value.entrySet()) {
      result.add(style(entry));
    }
    return "map" + result;
  }
  
  private String style(Map.Entry<?, ?> value) {
    return style(value.getKey()) + " -> " + style(value.getValue());
  }
  
  private String style(Collection<?> value) {
    String collectionType = getCollectionTypeString(value);
    
    if (value.isEmpty()) {
      return collectionType + "[[empty]]";
    }
    
    StringJoiner result = new StringJoiner(", ", "[", "]");
    for (Object o : value) {
      result.add(style(o));
    }
    return collectionType + result;
  }
  
  private String getCollectionTypeString(Collection<?> value) {
    if (value instanceof java.util.List) {
      return "list";
    }
    if (value instanceof java.util.Set) {
      return "set";
    }
    
    return "collection";
  }

  
  private String styleArray(Object[] array) {
    if (array.length == 0) {
      return "array<" + ClassUtils.getShortName(array.getClass().getComponentType()) + '>' + "[[empty]]";
    }
    
    StringJoiner result = new StringJoiner(", ", "[", "]");
    for (Object o : array) {
      result.add(style(o));
    }
    return "array<" + ClassUtils.getShortName(array.getClass().getComponentType()) + '>' + result;
  }
}
