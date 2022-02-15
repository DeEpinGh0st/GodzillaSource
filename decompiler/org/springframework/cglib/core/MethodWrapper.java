package org.springframework.cglib.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;













public class MethodWrapper
{
  private static final MethodWrapperKey KEY_FACTORY = (MethodWrapperKey)KeyFactory.create(MethodWrapperKey.class);








  
  public static Object create(Method method) {
    return KEY_FACTORY.newInstance(method.getName(), 
        ReflectUtils.getNames(method.getParameterTypes()), method
        .getReturnType().getName());
  }
  
  public static Set createSet(Collection methods) {
    Set<Object> set = new HashSet();
    for (Iterator<Method> it = methods.iterator(); it.hasNext();) {
      set.add(create(it.next()));
    }
    return set;
  }
  
  public static interface MethodWrapperKey {
    Object newInstance(String param1String1, String[] param1ArrayOfString, String param1String2);
  }
}
