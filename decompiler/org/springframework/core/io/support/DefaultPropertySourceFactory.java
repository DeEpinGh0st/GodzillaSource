package org.springframework.core.io.support;

import java.io.IOException;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;



























public class DefaultPropertySourceFactory
  implements PropertySourceFactory
{
  public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
    return (name != null) ? (PropertySource<?>)new ResourcePropertySource(name, resource) : (PropertySource<?>)new ResourcePropertySource(resource);
  }
}
