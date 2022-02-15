package org.springframework.core.type.classreading;

import java.io.IOException;
import org.springframework.core.io.Resource;

public interface MetadataReaderFactory {
  MetadataReader getMetadataReader(String paramString) throws IOException;
  
  MetadataReader getMetadataReader(Resource paramResource) throws IOException;
}
