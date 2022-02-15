package org.springframework.cglib.core.internal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cglib.core.Customizer;
import org.springframework.cglib.core.KeyFactoryCustomizer;

public class CustomizerRegistry {
  private Map<Class, List<KeyFactoryCustomizer>> customizers = (Map)new HashMap<Class<?>, List<KeyFactoryCustomizer>>(); private final Class[] customizerTypes;
  
  public CustomizerRegistry(Class[] customizerTypes) {
    this.customizerTypes = customizerTypes;
  }
  
  public void add(KeyFactoryCustomizer customizer) {
    Class<? extends KeyFactoryCustomizer> klass = (Class)customizer.getClass();
    for (Class type : this.customizerTypes) {
      if (type.isAssignableFrom(klass)) {
        List<KeyFactoryCustomizer> list = this.customizers.get(type);
        if (list == null) {
          this.customizers.put(type, list = new ArrayList<KeyFactoryCustomizer>());
        }
        list.add(customizer);
      } 
    } 
  }
  
  public <T> List<T> get(Class<T> klass) {
    List<KeyFactoryCustomizer> list = this.customizers.get(klass);
    if (list == null) {
      return Collections.emptyList();
    }
    return (List)list;
  }




  
  @Deprecated
  public static CustomizerRegistry singleton(Customizer customizer) {
    CustomizerRegistry registry = new CustomizerRegistry(new Class[] { Customizer.class });
    registry.add((KeyFactoryCustomizer)customizer);
    return registry;
  }
}
