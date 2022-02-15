package org.springframework.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

































public class LinkedMultiValueMap<K, V>
  extends MultiValueMapAdapter<K, V>
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 3801124242820219131L;
  
  public LinkedMultiValueMap() {
    super(new LinkedHashMap<>());
  }








  
  public LinkedMultiValueMap(int expectedSize) {
    super(CollectionUtils.newLinkedHashMap(expectedSize));
  }








  
  public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
    super(new LinkedHashMap<>(otherMap));
  }










  
  public LinkedMultiValueMap<K, V> deepCopy() {
    LinkedMultiValueMap<K, V> copy = new LinkedMultiValueMap(size());
    forEach((key, values) -> copy.put(key, new ArrayList(values)));
    return copy;
  }












  
  public LinkedMultiValueMap<K, V> clone() {
    return new LinkedMultiValueMap(this);
  }
}
