package org.springframework.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.lang.Nullable;











































public class LinkedCaseInsensitiveMap<V>
  implements Map<String, V>, Serializable, Cloneable
{
  private final LinkedHashMap<String, V> targetMap;
  private final HashMap<String, String> caseInsensitiveKeys;
  private final Locale locale;
  @Nullable
  private volatile transient Set<String> keySet;
  @Nullable
  private volatile transient Collection<V> values;
  @Nullable
  private volatile transient Set<Map.Entry<String, V>> entrySet;
  
  public LinkedCaseInsensitiveMap() {
    this((Locale)null);
  }






  
  public LinkedCaseInsensitiveMap(@Nullable Locale locale) {
    this(12, locale);
  }










  
  public LinkedCaseInsensitiveMap(int expectedSize) {
    this(expectedSize, null);
  }











  
  public LinkedCaseInsensitiveMap(int expectedSize, @Nullable Locale locale) {
    this.targetMap = new LinkedHashMap<String, V>((int)(expectedSize / 0.75F), 0.75F)
      {
        public boolean containsKey(Object key)
        {
          return LinkedCaseInsensitiveMap.this.containsKey(key);
        }
        
        protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
          boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
          if (doRemove) {
            LinkedCaseInsensitiveMap.this.removeCaseInsensitiveKey(eldest.getKey());
          }
          return doRemove;
        }
      };
    this.caseInsensitiveKeys = CollectionUtils.newHashMap(expectedSize);
    this.locale = (locale != null) ? locale : Locale.getDefault();
  }




  
  private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
    this.targetMap = (LinkedHashMap<String, V>)other.targetMap.clone();
    this.caseInsensitiveKeys = (HashMap<String, String>)other.caseInsensitiveKeys.clone();
    this.locale = other.locale;
  }




  
  public int size() {
    return this.targetMap.size();
  }

  
  public boolean isEmpty() {
    return this.targetMap.isEmpty();
  }

  
  public boolean containsKey(Object key) {
    return (key instanceof String && this.caseInsensitiveKeys.containsKey(convertKey((String)key)));
  }

  
  public boolean containsValue(Object value) {
    return this.targetMap.containsValue(value);
  }

  
  @Nullable
  public V get(Object key) {
    if (key instanceof String) {
      String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String)key));
      if (caseInsensitiveKey != null) {
        return this.targetMap.get(caseInsensitiveKey);
      }
    } 
    return null;
  }

  
  @Nullable
  public V getOrDefault(Object key, V defaultValue) {
    if (key instanceof String) {
      String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String)key));
      if (caseInsensitiveKey != null) {
        return this.targetMap.get(caseInsensitiveKey);
      }
    } 
    return defaultValue;
  }

  
  @Nullable
  public V put(String key, @Nullable V value) {
    String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
    V oldKeyValue = null;
    if (oldKey != null && !oldKey.equals(key)) {
      oldKeyValue = this.targetMap.remove(oldKey);
    }
    V oldValue = this.targetMap.put(key, value);
    return (oldKeyValue != null) ? oldKeyValue : oldValue;
  }

  
  public void putAll(Map<? extends String, ? extends V> map) {
    if (map.isEmpty()) {
      return;
    }
    map.forEach(this::put);
  }

  
  @Nullable
  public V putIfAbsent(String key, @Nullable V value) {
    String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
    if (oldKey != null) {
      V oldKeyValue = this.targetMap.get(oldKey);
      if (oldKeyValue != null) {
        return oldKeyValue;
      }
      
      key = oldKey;
    } 
    
    return this.targetMap.putIfAbsent(key, value);
  }

  
  @Nullable
  public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
    String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
    if (oldKey != null) {
      V oldKeyValue = this.targetMap.get(oldKey);
      if (oldKeyValue != null) {
        return oldKeyValue;
      }
      
      key = oldKey;
    } 
    
    return this.targetMap.computeIfAbsent(key, mappingFunction);
  }

  
  @Nullable
  public V remove(Object key) {
    if (key instanceof String) {
      String caseInsensitiveKey = removeCaseInsensitiveKey((String)key);
      if (caseInsensitiveKey != null) {
        return this.targetMap.remove(caseInsensitiveKey);
      }
    } 
    return null;
  }

  
  public void clear() {
    this.caseInsensitiveKeys.clear();
    this.targetMap.clear();
  }

  
  public Set<String> keySet() {
    Set<String> keySet = this.keySet;
    if (keySet == null) {
      keySet = new KeySet(this.targetMap.keySet());
      this.keySet = keySet;
    } 
    return keySet;
  }

  
  public Collection<V> values() {
    Collection<V> values = this.values;
    if (values == null) {
      values = new Values(this.targetMap.values());
      this.values = values;
    } 
    return values;
  }

  
  public Set<Map.Entry<String, V>> entrySet() {
    Set<Map.Entry<String, V>> entrySet = this.entrySet;
    if (entrySet == null) {
      entrySet = new EntrySet(this.targetMap.entrySet());
      this.entrySet = entrySet;
    } 
    return entrySet;
  }

  
  public LinkedCaseInsensitiveMap<V> clone() {
    return new LinkedCaseInsensitiveMap(this);
  }

  
  public boolean equals(@Nullable Object other) {
    return (this == other || this.targetMap.equals(other));
  }

  
  public int hashCode() {
    return this.targetMap.hashCode();
  }

  
  public String toString() {
    return this.targetMap.toString();
  }










  
  public Locale getLocale() {
    return this.locale;
  }








  
  protected String convertKey(String key) {
    return key.toLowerCase(getLocale());
  }






  
  protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
    return false;
  }
  
  @Nullable
  private String removeCaseInsensitiveKey(String key) {
    return this.caseInsensitiveKeys.remove(convertKey(key));
  }
  
  private class KeySet
    extends AbstractSet<String>
  {
    private final Set<String> delegate;
    
    KeySet(Set<String> delegate) {
      this.delegate = delegate;
    }

    
    public int size() {
      return this.delegate.size();
    }

    
    public boolean contains(Object o) {
      return this.delegate.contains(o);
    }

    
    public Iterator<String> iterator() {
      return new LinkedCaseInsensitiveMap.KeySetIterator();
    }

    
    public boolean remove(Object o) {
      return (LinkedCaseInsensitiveMap.this.remove(o) != null);
    }

    
    public void clear() {
      LinkedCaseInsensitiveMap.this.clear();
    }

    
    public Spliterator<String> spliterator() {
      return this.delegate.spliterator();
    }

    
    public void forEach(Consumer<? super String> action) {
      this.delegate.forEach(action);
    }
  }
  
  private class Values
    extends AbstractCollection<V>
  {
    private final Collection<V> delegate;
    
    Values(Collection<V> delegate) {
      this.delegate = delegate;
    }

    
    public int size() {
      return this.delegate.size();
    }

    
    public boolean contains(Object o) {
      return this.delegate.contains(o);
    }

    
    public Iterator<V> iterator() {
      return new LinkedCaseInsensitiveMap.ValuesIterator();
    }

    
    public void clear() {
      LinkedCaseInsensitiveMap.this.clear();
    }

    
    public Spliterator<V> spliterator() {
      return this.delegate.spliterator();
    }

    
    public void forEach(Consumer<? super V> action) {
      this.delegate.forEach(action);
    }
  }
  
  private class EntrySet
    extends AbstractSet<Map.Entry<String, V>>
  {
    private final Set<Map.Entry<String, V>> delegate;
    
    public EntrySet(Set<Map.Entry<String, V>> delegate) {
      this.delegate = delegate;
    }

    
    public int size() {
      return this.delegate.size();
    }

    
    public boolean contains(Object o) {
      return this.delegate.contains(o);
    }

    
    public Iterator<Map.Entry<String, V>> iterator() {
      return new LinkedCaseInsensitiveMap.EntrySetIterator();
    }


    
    public boolean remove(Object o) {
      if (this.delegate.remove(o)) {
        LinkedCaseInsensitiveMap.this.removeCaseInsensitiveKey((String)((Map.Entry)o).getKey());
        return true;
      } 
      return false;
    }

    
    public void clear() {
      this.delegate.clear();
      LinkedCaseInsensitiveMap.this.caseInsensitiveKeys.clear();
    }

    
    public Spliterator<Map.Entry<String, V>> spliterator() {
      return this.delegate.spliterator();
    }

    
    public void forEach(Consumer<? super Map.Entry<String, V>> action) {
      this.delegate.forEach(action);
    }
  }






  
  private abstract class EntryIterator<T>
    implements Iterator<T>
  {
    private final Iterator<Map.Entry<String, V>> delegate = LinkedCaseInsensitiveMap.this.targetMap.entrySet().iterator();

    
    protected Map.Entry<String, V> nextEntry() {
      Map.Entry<String, V> entry = this.delegate.next();
      this.last = entry;
      return entry;
    }
    @Nullable
    private Map.Entry<String, V> last;
    public boolean hasNext() {
      return this.delegate.hasNext();
    }

    
    public void remove() {
      this.delegate.remove();
      if (this.last != null) {
        LinkedCaseInsensitiveMap.this.removeCaseInsensitiveKey(this.last.getKey());
        this.last = null;
      } 
    }
  }
  
  private class KeySetIterator
    extends EntryIterator<String> {
    private KeySetIterator() {}
    
    public String next() {
      return (String)nextEntry().getKey();
    }
  }
  
  private class ValuesIterator
    extends EntryIterator<V> {
    private ValuesIterator() {}
    
    public V next() {
      return (V)nextEntry().getValue();
    }
  }
  
  private class EntrySetIterator
    extends EntryIterator<Map.Entry<String, V>> {
    private EntrySetIterator() {}
    
    public Map.Entry<String, V> next() {
      return nextEntry();
    }
  }
}
