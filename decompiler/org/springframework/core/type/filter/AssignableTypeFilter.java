package org.springframework.core.type.filter;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;





























public class AssignableTypeFilter
  extends AbstractTypeHierarchyTraversingFilter
{
  private final Class<?> targetType;
  
  public AssignableTypeFilter(Class<?> targetType) {
    super(true, true);
    this.targetType = targetType;
  }




  
  public final Class<?> getTargetType() {
    return this.targetType;
  }

  
  protected boolean matchClassName(String className) {
    return this.targetType.getName().equals(className);
  }

  
  @Nullable
  protected Boolean matchSuperClass(String superClassName) {
    return matchTargetType(superClassName);
  }

  
  @Nullable
  protected Boolean matchInterface(String interfaceName) {
    return matchTargetType(interfaceName);
  }
  
  @Nullable
  protected Boolean matchTargetType(String typeName) {
    if (this.targetType.getName().equals(typeName)) {
      return Boolean.valueOf(true);
    }
    if (Object.class.getName().equals(typeName)) {
      return Boolean.valueOf(false);
    }
    if (typeName.startsWith("java")) {
      try {
        Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
        return Boolean.valueOf(this.targetType.isAssignableFrom(clazz));
      }
      catch (Throwable throwable) {}
    }

    
    return null;
  }
}
