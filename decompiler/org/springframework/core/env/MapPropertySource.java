package org.springframework.core.env;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

































public class MapPropertySource
  extends EnumerablePropertySource<Map<String, Object>>
{
  public MapPropertySource(String name, Map<String, Object> source) {
    super(name, source);
  }


  
  @Nullable
  public Object getProperty(String name) {
    return this.source.get(name);
  }

  
  public boolean containsProperty(String name) {
    return this.source.containsKey(name);
  }

  
  public String[] getPropertyNames() {
    return StringUtils.toStringArray(this.source.keySet());
  }
}
