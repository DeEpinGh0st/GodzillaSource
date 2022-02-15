package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.cglib.core.Predicate;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.RejectModifierPredicate;

















class MixinEverythingEmitter
  extends MixinEmitter
{
  public MixinEverythingEmitter(ClassVisitor v, String className, Class[] classes) {
    super(v, className, classes, null);
  }
  
  protected Class[] getInterfaces(Class[] classes) {
    List list = new ArrayList();
    for (int i = 0; i < classes.length; i++) {
      ReflectUtils.addAllInterfaces(classes[i], list);
    }
    return (Class[])list.toArray((Object[])new Class[list.size()]);
  }
  
  protected Method[] getMethods(Class type) {
    List methods = new ArrayList(Arrays.asList((Object[])type.getMethods()));
    CollectionUtils.filter(methods, (Predicate)new RejectModifierPredicate(24));
    return (Method[])methods.toArray((Object[])new Method[methods.size()]);
  }
}
