package org.springframework.core.io.support;

import java.io.IOException;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

public interface PropertySourceFactory {
  PropertySource<?> createPropertySource(@Nullable String paramString, EncodedResource paramEncodedResource) throws IOException;
}
