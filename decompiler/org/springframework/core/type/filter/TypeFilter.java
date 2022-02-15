package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

@FunctionalInterface
public interface TypeFilter {
  boolean match(MetadataReader paramMetadataReader, MetadataReaderFactory paramMetadataReaderFactory) throws IOException;
}
