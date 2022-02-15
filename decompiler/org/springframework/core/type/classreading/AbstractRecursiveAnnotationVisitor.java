package org.springframework.core.type.classreading;

import java.lang.reflect.Field;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
































@Deprecated
abstract class AbstractRecursiveAnnotationVisitor
  extends AnnotationVisitor
{
  protected final Log logger = LogFactory.getLog(getClass());
  
  protected final AnnotationAttributes attributes;
  
  @Nullable
  protected final ClassLoader classLoader;

  
  public AbstractRecursiveAnnotationVisitor(@Nullable ClassLoader classLoader, AnnotationAttributes attributes) {
    super(17432576);
    this.classLoader = classLoader;
    this.attributes = attributes;
  }


  
  public void visit(String attributeName, Object attributeValue) {
    this.attributes.put(attributeName, attributeValue);
  }

  
  public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
    String annotationType = Type.getType(asmTypeDescriptor).getClassName();
    AnnotationAttributes nestedAttributes = new AnnotationAttributes(annotationType, this.classLoader);
    this.attributes.put(attributeName, nestedAttributes);
    return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
  }

  
  public AnnotationVisitor visitArray(String attributeName) {
    return new RecursiveAnnotationArrayVisitor(attributeName, this.attributes, this.classLoader);
  }

  
  public void visitEnum(String attributeName, String asmTypeDescriptor, String attributeValue) {
    Object newValue = getEnumValue(asmTypeDescriptor, attributeValue);
    visit(attributeName, newValue);
  }
  
  protected Object getEnumValue(String asmTypeDescriptor, String attributeValue) {
    Object valueToUse = attributeValue;
    try {
      Class<?> enumType = ClassUtils.forName(Type.getType(asmTypeDescriptor).getClassName(), this.classLoader);
      Field enumConstant = ReflectionUtils.findField(enumType, attributeValue);
      if (enumConstant != null) {
        ReflectionUtils.makeAccessible(enumConstant);
        valueToUse = enumConstant.get(null);
      }
    
    } catch (ClassNotFoundException|NoClassDefFoundError ex) {
      this.logger.debug("Failed to classload enum type while reading annotation metadata", ex);
    }
    catch (IllegalAccessException|java.security.AccessControlException ex) {
      this.logger.debug("Could not access enum value while reading annotation metadata", ex);
    } 
    return valueToUse;
  }
}
