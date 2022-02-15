package org.springframework.core.type.classreading;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;







































@Deprecated
public class MethodMetadataReadingVisitor
  extends MethodVisitor
  implements MethodMetadata
{
  protected final String methodName;
  protected final int access;
  protected final String declaringClassName;
  protected final String returnTypeName;
  @Nullable
  protected final ClassLoader classLoader;
  protected final Set<MethodMetadata> methodMetadataSet;
  protected final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<>(4);
  
  protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap = new LinkedMultiValueMap(3);



  
  public MethodMetadataReadingVisitor(String methodName, int access, String declaringClassName, String returnTypeName, @Nullable ClassLoader classLoader, Set<MethodMetadata> methodMetadataSet) {
    super(17432576);
    this.methodName = methodName;
    this.access = access;
    this.declaringClassName = declaringClassName;
    this.returnTypeName = returnTypeName;
    this.classLoader = classLoader;
    this.methodMetadataSet = methodMetadataSet;
  }


  
  public MergedAnnotations getAnnotations() {
    throw new UnsupportedOperationException();
  }

  
  @Nullable
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    if (!visible) {
      return null;
    }
    this.methodMetadataSet.add(this);
    String className = Type.getType(desc).getClassName();
    return new AnnotationAttributesReadingVisitor(className, (MultiValueMap<String, AnnotationAttributes>)this.attributesMap, this.metaAnnotationMap, this.classLoader);
  }



  
  public String getMethodName() {
    return this.methodName;
  }

  
  public boolean isAbstract() {
    return ((this.access & 0x400) != 0);
  }

  
  public boolean isStatic() {
    return ((this.access & 0x8) != 0);
  }

  
  public boolean isFinal() {
    return ((this.access & 0x10) != 0);
  }

  
  public boolean isOverridable() {
    return (!isStatic() && !isFinal() && (this.access & 0x2) == 0);
  }

  
  public boolean isAnnotated(String annotationName) {
    return this.attributesMap.containsKey(annotationName);
  }

  
  @Nullable
  public AnnotationAttributes getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    AnnotationAttributes raw = AnnotationReadingVisitorUtils.getMergedAnnotationAttributes(this.attributesMap, this.metaAnnotationMap, annotationName);
    
    if (raw == null) {
      return null;
    }
    return AnnotationReadingVisitorUtils.convertClassValues("method '" + 
        getMethodName() + "'", this.classLoader, raw, classValuesAsString);
  }

  
  @Nullable
  public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    if (!this.attributesMap.containsKey(annotationName)) {
      return null;
    }
    LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
    List<AnnotationAttributes> attributesList = this.attributesMap.get(annotationName);
    if (attributesList != null) {
      String annotatedElement = "method '" + getMethodName() + '\'';
      for (AnnotationAttributes annotationAttributes : attributesList) {
        AnnotationAttributes convertedAttributes = AnnotationReadingVisitorUtils.convertClassValues(annotatedElement, this.classLoader, annotationAttributes, classValuesAsString);
        
        convertedAttributes.forEach(linkedMultiValueMap::add);
      } 
    } 
    return (MultiValueMap<String, Object>)linkedMultiValueMap;
  }

  
  public String getDeclaringClassName() {
    return this.declaringClassName;
  }

  
  public String getReturnTypeName() {
    return this.returnTypeName;
  }
}
