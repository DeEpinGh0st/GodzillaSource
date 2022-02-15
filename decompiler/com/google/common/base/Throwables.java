package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;














































@GwtCompatible(emulated = true)
public final class Throwables
{
  @GwtIncompatible
  private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
  @GwtIncompatible
  @VisibleForTesting
  static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
  
  @GwtIncompatible
  public static <X extends Throwable> void throwIfInstanceOf(Throwable throwable, Class<X> declaredType) throws X {
    Preconditions.checkNotNull(throwable);
    if (declaredType.isInstance(throwable)) {
      throw (X)declaredType.cast(throwable);
    }
  }




















  
  @Deprecated
  @GwtIncompatible
  public static <X extends Throwable> void propagateIfInstanceOf(Throwable throwable, Class<X> declaredType) throws X {
    if (throwable != null) {
      throwIfInstanceOf(throwable, declaredType);
    }
  }



















  
  public static void throwIfUnchecked(Throwable throwable) {
    Preconditions.checkNotNull(throwable);
    if (throwable instanceof RuntimeException) {
      throw (RuntimeException)throwable;
    }
    if (throwable instanceof Error) {
      throw (Error)throwable;
    }
  }


















  
  @Deprecated
  @GwtIncompatible
  public static void propagateIfPossible(Throwable throwable) {
    if (throwable != null) {
      throwIfUnchecked(throwable);
    }
  }



















  
  @GwtIncompatible
  public static <X extends Throwable> void propagateIfPossible(Throwable throwable, Class<X> declaredType) throws X {
    propagateIfInstanceOf(throwable, declaredType);
    propagateIfPossible(throwable);
  }













  
  @GwtIncompatible
  public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(Throwable throwable, Class<X1> declaredType1, Class<X2> declaredType2) throws X1, X2 {
    Preconditions.checkNotNull(declaredType2);
    propagateIfInstanceOf(throwable, declaredType1);
    propagateIfPossible(throwable, declaredType2);
  }




























  
  @Deprecated
  @CanIgnoreReturnValue
  @GwtIncompatible
  public static RuntimeException propagate(Throwable throwable) {
    throwIfUnchecked(throwable);
    throw new RuntimeException(throwable);
  }












  
  public static Throwable getRootCause(Throwable throwable) {
    Throwable slowPointer = throwable;
    boolean advanceSlowPointer = false;
    
    Throwable cause;
    while ((cause = throwable.getCause()) != null) {
      throwable = cause;
      
      if (throwable == slowPointer) {
        throw new IllegalArgumentException("Loop in causal chain detected.", throwable);
      }
      if (advanceSlowPointer) {
        slowPointer = slowPointer.getCause();
      }
      advanceSlowPointer = !advanceSlowPointer;
    } 
    return throwable;
  }
















  
  @Beta
  public static List<Throwable> getCausalChain(Throwable throwable) {
    Preconditions.checkNotNull(throwable);
    List<Throwable> causes = new ArrayList<>(4);
    causes.add(throwable);


    
    Throwable slowPointer = throwable;
    boolean advanceSlowPointer = false;
    
    Throwable cause;
    while ((cause = throwable.getCause()) != null) {
      throwable = cause;
      causes.add(throwable);
      
      if (throwable == slowPointer) {
        throw new IllegalArgumentException("Loop in causal chain detected.", throwable);
      }
      if (advanceSlowPointer) {
        slowPointer = slowPointer.getCause();
      }
      advanceSlowPointer = !advanceSlowPointer;
    } 
    return Collections.unmodifiableList(causes);
  }














  
  @Beta
  @GwtIncompatible
  public static <X extends Throwable> X getCauseAs(Throwable throwable, Class<X> expectedCauseType) {
    try {
      return expectedCauseType.cast(throwable.getCause());
    } catch (ClassCastException e) {
      e.initCause(throwable);
      throw e;
    } 
  }






  
  @GwtIncompatible
  public static String getStackTraceAsString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }





























  
  @Beta
  @GwtIncompatible
  public static List<StackTraceElement> lazyStackTrace(Throwable throwable) {
    return lazyStackTraceIsLazy() ? 
      jlaStackTrace(throwable) : 
      Collections.<StackTraceElement>unmodifiableList(Arrays.asList(throwable.getStackTrace()));
  }






  
  @Beta
  @GwtIncompatible
  public static boolean lazyStackTraceIsLazy() {
    return (getStackTraceElementMethod != null && getStackTraceDepthMethod != null);
  }
  
  @GwtIncompatible
  private static List<StackTraceElement> jlaStackTrace(final Throwable t) {
    Preconditions.checkNotNull(t);





    
    return new AbstractList<StackTraceElement>()
      {
        public StackTraceElement get(int n) {
          return 
            (StackTraceElement)Throwables.invokeAccessibleNonThrowingMethod(Throwables.getStackTraceElementMethod, Throwables.jla, new Object[] { this.val$t, Integer.valueOf(n) });
        }

        
        public int size() {
          return ((Integer)Throwables.invokeAccessibleNonThrowingMethod(Throwables.getStackTraceDepthMethod, Throwables.jla, new Object[] { this.val$t })).intValue();
        }
      };
  }

  
  @GwtIncompatible
  private static Object invokeAccessibleNonThrowingMethod(Method method, Object receiver, Object... params) {
    try {
      return method.invoke(receiver, params);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw propagate(e.getCause());
    } 
  }










  
  @GwtIncompatible
  private static final Object jla = getJLA();




  
  @GwtIncompatible
  private static final Method getStackTraceElementMethod = (jla == null) ? null : 
    getGetMethod();




  
  @GwtIncompatible
  private static final Method getStackTraceDepthMethod = (jla == null) ? null : 
    getSizeMethod();








  
  @GwtIncompatible
  private static Object getJLA() {
    try {
      Class<?> sharedSecrets = Class.forName("sun.misc.SharedSecrets", false, null);
      Method langAccess = sharedSecrets.getMethod("getJavaLangAccess", new Class[0]);
      return langAccess.invoke(null, new Object[0]);
    } catch (ThreadDeath death) {
      throw death;
    } catch (Throwable t) {



      
      return null;
    } 
  }




  
  @GwtIncompatible
  private static Method getGetMethod() {
    return getJlaMethod("getStackTraceElement", new Class[] { Throwable.class, int.class });
  }









  
  @GwtIncompatible
  private static Method getSizeMethod() {
    try {
      Method getStackTraceDepth = getJlaMethod("getStackTraceDepth", new Class[] { Throwable.class });
      if (getStackTraceDepth == null) {
        return null;
      }
      getStackTraceDepth.invoke(getJLA(), new Object[] { new Throwable() });
      return getStackTraceDepth;
    } catch (UnsupportedOperationException|IllegalAccessException|InvocationTargetException e) {
      return null;
    } 
  }

  
  @GwtIncompatible
  private static Method getJlaMethod(String name, Class<?>... parameterTypes) throws ThreadDeath {
    try {
      return Class.forName("sun.misc.JavaLangAccess", false, null).getMethod(name, parameterTypes);
    } catch (ThreadDeath death) {
      throw death;
    } catch (Throwable t) {



      
      return null;
    } 
  }
}
