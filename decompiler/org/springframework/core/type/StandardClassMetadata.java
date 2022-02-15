package org.springframework.core.type;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;































public class StandardClassMetadata
  implements ClassMetadata
{
  private final Class<?> introspectedClass;
  
  @Deprecated
  public StandardClassMetadata(Class<?> introspectedClass) {
    Assert.notNull(introspectedClass, "Class must not be null");
    this.introspectedClass = introspectedClass;
  }



  
  public final Class<?> getIntrospectedClass() {
    return this.introspectedClass;
  }


  
  public String getClassName() {
    return this.introspectedClass.getName();
  }

  
  public boolean isInterface() {
    return this.introspectedClass.isInterface();
  }

  
  public boolean isAnnotation() {
    return this.introspectedClass.isAnnotation();
  }

  
  public boolean isAbstract() {
    return Modifier.isAbstract(this.introspectedClass.getModifiers());
  }

  
  public boolean isFinal() {
    return Modifier.isFinal(this.introspectedClass.getModifiers());
  }

  
  public boolean isIndependent() {
    return (!hasEnclosingClass() || (this.introspectedClass
      .getDeclaringClass() != null && 
      Modifier.isStatic(this.introspectedClass.getModifiers())));
  }

  
  @Nullable
  public String getEnclosingClassName() {
    Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
    return (enclosingClass != null) ? enclosingClass.getName() : null;
  }

  
  @Nullable
  public String getSuperClassName() {
    Class<?> superClass = this.introspectedClass.getSuperclass();
    return (superClass != null) ? superClass.getName() : null;
  }

  
  public String[] getInterfaceNames() {
    Class<?>[] ifcs = this.introspectedClass.getInterfaces();
    String[] ifcNames = new String[ifcs.length];
    for (int i = 0; i < ifcs.length; i++) {
      ifcNames[i] = ifcs[i].getName();
    }
    return ifcNames;
  }

  
  public String[] getMemberClassNames() {
    LinkedHashSet<String> memberClassNames = new LinkedHashSet<>(4);
    for (Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
      memberClassNames.add(nestedClass.getName());
    }
    return StringUtils.toStringArray(memberClassNames);
  }

  
  public boolean equals(@Nullable Object obj) {
    return (this == obj || (obj instanceof StandardClassMetadata && 
      getIntrospectedClass().equals(((StandardClassMetadata)obj).getIntrospectedClass())));
  }

  
  public int hashCode() {
    return getIntrospectedClass().hashCode();
  }

  
  public String toString() {
    return getClassName();
  }
}
