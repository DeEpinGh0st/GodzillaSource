package org.springframework.core.type.classreading;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
































@Deprecated
public class AnnotationMetadataReadingVisitor
  extends ClassMetadataReadingVisitor
  implements AnnotationMetadata
{
  @Nullable
  protected final ClassLoader classLoader;
  protected final Set<String> annotationSet = new LinkedHashSet<>(4);
  
  protected final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<>(4);





  
  protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap = new LinkedMultiValueMap(3);
  
  protected final Set<MethodMetadata> methodMetadataSet = new LinkedHashSet<>(4);

  
  public AnnotationMetadataReadingVisitor(@Nullable ClassLoader classLoader) {
    this.classLoader = classLoader;
  }


  
  public MergedAnnotations getAnnotations() {
    throw new UnsupportedOperationException();
  }



  
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    if ((access & 0x40) != 0) {
      return super.visitMethod(access, name, desc, signature, exceptions);
    }
    return new MethodMetadataReadingVisitor(name, access, getClassName(), 
        Type.getReturnType(desc).getClassName(), this.classLoader, this.methodMetadataSet);
  }

  
  @Nullable
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    if (!visible) {
      return null;
    }
    String className = Type.getType(desc).getClassName();
    if (AnnotationUtils.isInJavaLangAnnotationPackage(className)) {
      return null;
    }
    this.annotationSet.add(className);
    return new AnnotationAttributesReadingVisitor(className, (MultiValueMap<String, AnnotationAttributes>)this.attributesMap, this.metaAnnotationMap, this.classLoader);
  }



  
  public Set<String> getAnnotationTypes() {
    return this.annotationSet;
  }

  
  public Set<String> getMetaAnnotationTypes(String annotationName) {
    Set<String> metaAnnotationTypes = this.metaAnnotationMap.get(annotationName);
    return (metaAnnotationTypes != null) ? metaAnnotationTypes : Collections.<String>emptySet();
  }

  
  public boolean hasMetaAnnotation(String metaAnnotationType) {
    if (AnnotationUtils.isInJavaLangAnnotationPackage(metaAnnotationType)) {
      return false;
    }
    Collection<Set<String>> allMetaTypes = this.metaAnnotationMap.values();
    for (Set<String> metaTypes : allMetaTypes) {
      if (metaTypes.contains(metaAnnotationType)) {
        return true;
      }
    } 
    return false;
  }

  
  public boolean isAnnotated(String annotationName) {
    return (!AnnotationUtils.isInJavaLangAnnotationPackage(annotationName) && this.attributesMap
      .containsKey(annotationName));
  }

  
  public boolean hasAnnotation(String annotationName) {
    return getAnnotationTypes().contains(annotationName);
  }

  
  @Nullable
  public AnnotationAttributes getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    AnnotationAttributes raw = AnnotationReadingVisitorUtils.getMergedAnnotationAttributes(this.attributesMap, this.metaAnnotationMap, annotationName);
    
    if (raw == null) {
      return null;
    }
    return AnnotationReadingVisitorUtils.convertClassValues("class '" + 
        getClassName() + "'", this.classLoader, raw, classValuesAsString);
  }

  
  @Nullable
  public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
    List<AnnotationAttributes> attributes = this.attributesMap.get(annotationName);
    if (attributes == null) {
      return null;
    }
    String annotatedElement = "class '" + getClassName() + "'";
    for (AnnotationAttributes raw : attributes) {
      for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)AnnotationReadingVisitorUtils.convertClassValues(annotatedElement, this.classLoader, raw, classValuesAsString)
        .entrySet()) {
        linkedMultiValueMap.add(entry.getKey(), entry.getValue());
      }
    } 
    return (MultiValueMap<String, Object>)linkedMultiValueMap;
  }

  
  public boolean hasAnnotatedMethods(String annotationName) {
    for (MethodMetadata methodMetadata : this.methodMetadataSet) {
      if (methodMetadata.isAnnotated(annotationName)) {
        return true;
      }
    } 
    return false;
  }

  
  public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
    Set<MethodMetadata> annotatedMethods = new LinkedHashSet<>(4);
    for (MethodMetadata methodMetadata : this.methodMetadataSet) {
      if (methodMetadata.isAnnotated(annotationName)) {
        annotatedMethods.add(methodMetadata);
      }
    } 
    return annotatedMethods;
  }
}
