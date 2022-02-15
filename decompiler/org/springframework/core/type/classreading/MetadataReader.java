package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

public interface MetadataReader {
  Resource getResource();
  
  ClassMetadata getClassMetadata();
  
  AnnotationMetadata getAnnotationMetadata();
}
