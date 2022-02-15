package org.springframework.core.type.classreading;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;






























final class SimpleMethodMetadataReadingVisitor
  extends MethodVisitor
{
  @Nullable
  private final ClassLoader classLoader;
  private final String declaringClassName;
  private final int access;
  private final String methodName;
  private final String descriptor;
  private final List<MergedAnnotation<?>> annotations = new ArrayList<>(4);

  
  private final Consumer<SimpleMethodMetadata> consumer;

  
  @Nullable
  private Source source;

  
  SimpleMethodMetadataReadingVisitor(@Nullable ClassLoader classLoader, String declaringClassName, int access, String methodName, String descriptor, Consumer<SimpleMethodMetadata> consumer) {
    super(17432576);
    this.classLoader = classLoader;
    this.declaringClassName = declaringClassName;
    this.access = access;
    this.methodName = methodName;
    this.descriptor = descriptor;
    this.consumer = consumer;
  }


  
  @Nullable
  public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    return MergedAnnotationReadingVisitor.get(this.classLoader, getSource(), descriptor, visible, this.annotations::add);
  }


  
  public void visitEnd() {
    if (!this.annotations.isEmpty()) {
      String returnTypeName = Type.getReturnType(this.descriptor).getClassName();
      MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
      
      SimpleMethodMetadata metadata = new SimpleMethodMetadata(this.methodName, this.access, this.declaringClassName, returnTypeName, getSource(), annotations);
      this.consumer.accept(metadata);
    } 
  }
  
  private Object getSource() {
    Source source = this.source;
    if (source == null) {
      source = new Source(this.declaringClassName, this.methodName, this.descriptor);
      this.source = source;
    } 
    return source;
  }


  
  static final class Source
  {
    private final String declaringClassName;

    
    private final String methodName;
    
    private final String descriptor;
    
    @Nullable
    private String toStringValue;

    
    Source(String declaringClassName, String methodName, String descriptor) {
      this.declaringClassName = declaringClassName;
      this.methodName = methodName;
      this.descriptor = descriptor;
    }

    
    public int hashCode() {
      int result = 1;
      result = 31 * result + this.declaringClassName.hashCode();
      result = 31 * result + this.methodName.hashCode();
      result = 31 * result + this.descriptor.hashCode();
      return result;
    }

    
    public boolean equals(@Nullable Object other) {
      if (this == other) {
        return true;
      }
      if (other == null || getClass() != other.getClass()) {
        return false;
      }
      Source otherSource = (Source)other;
      return (this.declaringClassName.equals(otherSource.declaringClassName) && this.methodName
        .equals(otherSource.methodName) && this.descriptor.equals(otherSource.descriptor));
    }

    
    public String toString() {
      String value = this.toStringValue;
      if (value == null) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.declaringClassName);
        builder.append('.');
        builder.append(this.methodName);
        Type[] argumentTypes = Type.getArgumentTypes(this.descriptor);
        builder.append('(');
        for (int i = 0; i < argumentTypes.length; i++) {
          if (i != 0) {
            builder.append(',');
          }
          builder.append(argumentTypes[i].getClassName());
        } 
        builder.append(')');
        value = builder.toString();
        this.toStringValue = value;
      } 
      return value;
    }
  }
}
