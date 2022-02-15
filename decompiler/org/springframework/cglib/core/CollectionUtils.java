package org.springframework.cglib.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
















public class CollectionUtils
{
  public static Map bucket(Collection c, Transformer t) {
    Map<Object, Object> buckets = new HashMap<Object, Object>();
    for (Iterator it = c.iterator(); it.hasNext(); ) {
      Object value = it.next();
      Object key = t.transform(value);
      List<Object> bucket = (List)buckets.get(key);
      if (bucket == null) {
        buckets.put(key, bucket = new LinkedList());
      }
      bucket.add(value);
    } 
    return buckets;
  }
  
  public static void reverse(Map source, Map target) {
    for (Iterator it = source.keySet().iterator(); it.hasNext(); ) {
      Object key = it.next();
      target.put(source.get(key), key);
    } 
  }
  
  public static Collection filter(Collection c, Predicate p) {
    Iterator it = c.iterator();
    while (it.hasNext()) {
      if (!p.evaluate(it.next())) {
        it.remove();
      }
    } 
    return c;
  }
  
  public static List transform(Collection c, Transformer t) {
    List<Object> result = new ArrayList(c.size());
    for (Iterator it = c.iterator(); it.hasNext();) {
      result.add(t.transform(it.next()));
    }
    return result;
  }
  
  public static Map getIndexMap(List list) {
    Map<Object, Object> indexes = new HashMap<Object, Object>();
    int index = 0;
    for (Iterator it = list.iterator(); it.hasNext();) {
      indexes.put(it.next(), new Integer(index++));
    }
    return indexes;
  }
}
