package com.jgoodies.forms.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.AbstractButton;























































public final class FocusTraversalUtilsAccessor
{
  private static final String FOCUS_TRAVERSAL_UTILS_NAME = "com.jgoodies.jsdl.common.focus.FocusTraversalUtils";
  private static Method groupMethod = null;

  
  static {
    groupMethod = getGroupMethod();
  }












  
  public static void tryToBuildAFocusGroup(AbstractButton... buttons) {
    if (groupMethod == null) {
      return;
    }
    try {
      groupMethod.invoke(null, new Object[] { buttons });
    } catch (IllegalAccessException e) {
    
    } catch (InvocationTargetException e) {}
  }





  
  private static Method getGroupMethod() {
    try {
      Class<?> clazz = Class.forName("com.jgoodies.jsdl.common.focus.FocusTraversalUtils");
      return clazz.getMethod("group", new Class[] { AbstractButton[].class });
    } catch (ClassNotFoundException e) {
    
    } catch (SecurityException e) {
    
    } catch (NoSuchMethodException e) {}

    
    return null;
  }
}
