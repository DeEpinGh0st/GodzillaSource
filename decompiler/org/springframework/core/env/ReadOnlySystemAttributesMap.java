package org.springframework.core.env;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
































abstract class ReadOnlySystemAttributesMap
  implements Map<String, String>
{
  public boolean containsKey(Object key) {
    return (get(key) != null);
  }







  
  @Nullable
  public String get(Object key) {
    if (!(key instanceof String)) {
      throw new IllegalArgumentException("Type of key [" + key
          .getClass().getName() + "] must be java.lang.String");
    }
    return getSystemAttribute((String)key);
  }

  
  public boolean isEmpty() {
    return false;
  }











  
  public int size() {
    throw new UnsupportedOperationException();
  }

  
  public String put(String key, String value) {
    throw new UnsupportedOperationException();
  }

  
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }

  
  public String remove(Object key) {
    throw new UnsupportedOperationException();
  }

  
  public void clear() {
    throw new UnsupportedOperationException();
  }

  
  public Set<String> keySet() {
    return Collections.emptySet();
  }

  
  public void putAll(Map<? extends String, ? extends String> map) {
    throw new UnsupportedOperationException();
  }

  
  public Collection<String> values() {
    return Collections.emptySet();
  }

  
  public Set<Map.Entry<String, String>> entrySet() {
    return Collections.emptySet();
  }
  
  @Nullable
  protected abstract String getSystemAttribute(String paramString);
}
