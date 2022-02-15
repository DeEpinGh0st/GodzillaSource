package org.springframework.core.env;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;





























public interface PropertySources
  extends Iterable<PropertySource<?>>
{
  default Stream<PropertySource<?>> stream() {
    return StreamSupport.stream(spliterator(), false);
  }
  
  boolean contains(String paramString);
  
  @Nullable
  PropertySource<?> get(String paramString);
}
