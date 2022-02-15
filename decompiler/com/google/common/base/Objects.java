package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Arrays;










































@GwtCompatible
public final class Objects
  extends ExtraObjectsMethodsForWeb
{
  public static boolean equal(Object a, Object b) {
    return (a == b || (a != null && a.equals(b)));
  }





















  
  public static int hashCode(Object... objects) {
    return Arrays.hashCode(objects);
  }
}
