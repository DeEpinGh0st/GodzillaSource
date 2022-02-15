package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;





















@GwtIncompatible
final class FuturesGetChecked
{
  @CanIgnoreReturnValue
  static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws X {
    return getChecked(bestGetCheckedTypeValidator(), future, exceptionClass);
  }


  
  @CanIgnoreReturnValue
  @VisibleForTesting
  static <V, X extends Exception> V getChecked(GetCheckedTypeValidator validator, Future<V> future, Class<X> exceptionClass) throws X {
    validator.validateClass(exceptionClass);
    try {
      return future.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw newWithCause(exceptionClass, e);
    } catch (ExecutionException e) {
      wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
      throw (X)new AssertionError();
    } 
  }



  
  @CanIgnoreReturnValue
  static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws X {
    bestGetCheckedTypeValidator().validateClass(exceptionClass);
    try {
      return future.get(timeout, unit);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw newWithCause(exceptionClass, e);
    } catch (TimeoutException e) {
      throw newWithCause(exceptionClass, e);
    } catch (ExecutionException e) {
      wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
      throw (X)new AssertionError();
    } 
  }





  
  private static GetCheckedTypeValidator bestGetCheckedTypeValidator() {
    return GetCheckedTypeValidatorHolder.BEST_VALIDATOR;
  }
  
  @VisibleForTesting
  static GetCheckedTypeValidator weakSetValidator() {
    return GetCheckedTypeValidatorHolder.WeakSetValidator.INSTANCE;
  }

  
  @VisibleForTesting
  static GetCheckedTypeValidator classValueValidator() {
    return GetCheckedTypeValidatorHolder.ClassValueValidator.INSTANCE;
  }





  
  @VisibleForTesting
  static class GetCheckedTypeValidatorHolder
  {
    static final String CLASS_VALUE_VALIDATOR_NAME = GetCheckedTypeValidatorHolder.class
      .getName() + "$ClassValueValidator";
    
    static final FuturesGetChecked.GetCheckedTypeValidator BEST_VALIDATOR = getBestValidator();
    
    @IgnoreJRERequirement
    enum ClassValueValidator
      implements FuturesGetChecked.GetCheckedTypeValidator {
      INSTANCE;




      
      private static final ClassValue<Boolean> isValidClass = new ClassValue<Boolean>()
        {
          protected Boolean computeValue(Class<?> type)
          {
            FuturesGetChecked.checkExceptionClassValidity(type.asSubclass(Exception.class));
            return Boolean.valueOf(true);
          }
        }; static {
      
      }
      public void validateClass(Class<? extends Exception> exceptionClass) {
        isValidClass.get(exceptionClass);
      }
    }
    
    enum WeakSetValidator implements FuturesGetChecked.GetCheckedTypeValidator {
      INSTANCE;








      
      private static final Set<WeakReference<Class<? extends Exception>>> validClasses = new CopyOnWriteArraySet<>();
      static {
      
      }
      public void validateClass(Class<? extends Exception> exceptionClass) {
        for (WeakReference<Class<? extends Exception>> knownGood : validClasses) {
          if (exceptionClass.equals(knownGood.get())) {
            return;
          }
        } 
        
        FuturesGetChecked.checkExceptionClassValidity(exceptionClass);









        
        if (validClasses.size() > 1000) {
          validClasses.clear();
        }
        
        validClasses.add(new WeakReference<>(exceptionClass));
      }
    }




    
    static FuturesGetChecked.GetCheckedTypeValidator getBestValidator() {
      try {
        Class<?> theClass = Class.forName(CLASS_VALUE_VALIDATOR_NAME);
        return (FuturesGetChecked.GetCheckedTypeValidator)theClass.getEnumConstants()[0];
      } catch (Throwable t) {
        return FuturesGetChecked.weakSetValidator();
      } 
    }
  }


  
  private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable cause, Class<X> exceptionClass) throws X {
    if (cause instanceof Error) {
      throw (X)new ExecutionError((Error)cause);
    }
    if (cause instanceof RuntimeException) {
      throw (X)new UncheckedExecutionException(cause);
    }
    throw newWithCause(exceptionClass, cause);
  }
  @IgnoreJRERequirement enum ClassValueValidator implements GetCheckedTypeValidator {
    INSTANCE;
    private static final ClassValue<Boolean> isValidClass = new ClassValue<Boolean>() { protected Boolean computeValue(Class<?> type) { FuturesGetChecked.checkExceptionClassValidity(type.asSubclass(Exception.class)); return Boolean.valueOf(true); } }
    ; static {  } public void validateClass(Class<? extends Exception> exceptionClass) { isValidClass.get(exceptionClass); } } enum WeakSetValidator implements GetCheckedTypeValidator {
    INSTANCE; private static final Set<WeakReference<Class<? extends Exception>>> validClasses = new CopyOnWriteArraySet<>(); static {  } public void validateClass(Class<? extends Exception> exceptionClass) { for (WeakReference<Class<? extends Exception>> knownGood : validClasses) { if (exceptionClass.equals(knownGood.get()))
          return;  }
       FuturesGetChecked.checkExceptionClassValidity(exceptionClass); if (validClasses.size() > 1000)
        validClasses.clear();  validClasses.add(new WeakReference<>(exceptionClass)); }
  } private static boolean hasConstructorUsableByGetChecked(Class<? extends Exception> exceptionClass) { try {
      Exception unused = newWithCause((Class)exceptionClass, new Exception());
      return true;
    } catch (Exception e) {
      return false;
    }  }



  
  private static <X extends Exception> X newWithCause(Class<X> exceptionClass, Throwable cause) {
    List<Constructor<X>> constructors = (List)Arrays.asList(exceptionClass.getConstructors());
    for (Constructor<X> constructor : preferringStrings(constructors)) {
      Exception exception = newFromConstructor(constructor, cause);
      if (exception != null) {
        if (exception.getCause() == null) {
          exception.initCause(cause);
        }
        return (X)exception;
      } 
    } 
    throw new IllegalArgumentException("No appropriate constructor for exception of type " + exceptionClass + " in response to chained exception", cause);
  }





  
  private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> constructors) {
    return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
  }

  
  private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural()
    .onResultOf(new Function<Constructor<?>, Boolean>()
      {
        public Boolean apply(Constructor<?> input)
        {
          return Boolean.valueOf(Arrays.<Class<?>>asList(input.getParameterTypes()).contains(String.class));
        }
      }).reverse();
  
  private static <X> X newFromConstructor(Constructor<X> constructor, Throwable cause) {
    Class<?>[] paramTypes = constructor.getParameterTypes();
    Object[] params = new Object[paramTypes.length];
    for (int i = 0; i < paramTypes.length; i++) {
      Class<?> paramType = paramTypes[i];
      if (paramType.equals(String.class)) {
        params[i] = cause.toString();
      } else if (paramType.equals(Throwable.class)) {
        params[i] = cause;
      } else {
        return null;
      } 
    } 
    try {
      return constructor.newInstance(params);
    } catch (IllegalArgumentException|InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {


      
      return null;
    } 
  }
  
  @VisibleForTesting
  static boolean isCheckedException(Class<? extends Exception> type) {
    return !RuntimeException.class.isAssignableFrom(type);
  }
  
  @VisibleForTesting
  static void checkExceptionClassValidity(Class<? extends Exception> exceptionClass) {
    Preconditions.checkArgument(
        isCheckedException(exceptionClass), "Futures.getChecked exception type (%s) must not be a RuntimeException", exceptionClass);

    
    Preconditions.checkArgument(
        hasConstructorUsableByGetChecked(exceptionClass), "Futures.getChecked exception type (%s) must be an accessible class with an accessible constructor whose parameters (if any) must be of type String and/or Throwable", exceptionClass);
  }
  
  @VisibleForTesting
  static interface GetCheckedTypeValidator {
    void validateClass(Class<? extends Exception> param1Class);
  }
}
