package org.springframework.core.type.classreading;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.asm.ClassReader;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;





























final class SimpleMetadataReader
  implements MetadataReader
{
  private static final int PARSING_OPTIONS = 7;
  private final Resource resource;
  private final AnnotationMetadata annotationMetadata;
  
  SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
    SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(classLoader);
    getClassReader(resource).accept(visitor, 7);
    this.resource = resource;
    this.annotationMetadata = visitor.getMetadata();
  }
  
  private static ClassReader getClassReader(Resource resource) throws IOException {
    InputStream is = resource.getInputStream(); Throwable throwable = null;
    
    try { return new ClassReader(is); }
    
    catch (IllegalArgumentException ex)
    { throw new NestedIOException("ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: " + resource, ex); }
    catch (Throwable throwable1) { throwable = throwable1 = null; throw throwable1; }
    finally
    { if (is != null) if (throwable != null) { try { is.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  } else { is.close(); }
          }
  
  }
  
  public Resource getResource() {
    return this.resource;
  }

  
  public ClassMetadata getClassMetadata() {
    return (ClassMetadata)this.annotationMetadata;
  }

  
  public AnnotationMetadata getAnnotationMetadata() {
    return this.annotationMetadata;
  }
}
