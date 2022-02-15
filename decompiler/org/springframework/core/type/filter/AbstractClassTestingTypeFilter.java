package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;































public abstract class AbstractClassTestingTypeFilter
  implements TypeFilter
{
  public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
    return match(metadataReader.getClassMetadata());
  }
  
  protected abstract boolean match(ClassMetadata paramClassMetadata);
}
