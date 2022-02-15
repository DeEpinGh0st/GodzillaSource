package org.springframework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
































public abstract class ReflectionUtils
{
  public static final MethodFilter USER_DECLARED_METHODS;
  public static final FieldFilter COPYABLE_FIELDS;
  private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";
  
  static {
    USER_DECLARED_METHODS = (method -> 
      (!method.isBridge() && !method.isSynthetic()));



    
    COPYABLE_FIELDS = (field -> 
      (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())));
  }






  
  private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
  
  private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
  
  private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
  
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];





  
  private static final Map<Class<?>, Method[]> declaredMethodsCache = (Map)new ConcurrentReferenceHashMap<>(256);



  
  private static final Map<Class<?>, Field[]> declaredFieldsCache = (Map)new ConcurrentReferenceHashMap<>(256);













  
  public static void handleReflectionException(Exception ex) {
    if (ex instanceof NoSuchMethodException) {
      throw new IllegalStateException("Method not found: " + ex.getMessage());
    }
    if (ex instanceof IllegalAccessException) {
      throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
    }
    if (ex instanceof InvocationTargetException) {
      handleInvocationTargetException((InvocationTargetException)ex);
    }
    if (ex instanceof RuntimeException) {
      throw (RuntimeException)ex;
    }
    throw new UndeclaredThrowableException(ex);
  }







  
  public static void handleInvocationTargetException(InvocationTargetException ex) {
    rethrowRuntimeException(ex.getTargetException());
  }











  
  public static void rethrowRuntimeException(Throwable ex) {
    if (ex instanceof RuntimeException) {
      throw (RuntimeException)ex;
    }
    if (ex instanceof Error) {
      throw (Error)ex;
    }
    throw new UndeclaredThrowableException(ex);
  }











  
  public static void rethrowException(Throwable ex) throws Exception {
    if (ex instanceof Exception) {
      throw (Exception)ex;
    }
    if (ex instanceof Error) {
      throw (Error)ex;
    }
    throw new UndeclaredThrowableException(ex);
  }













  
  public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
    Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
    makeAccessible(ctor);
    return ctor;
  }









  
  public static void makeAccessible(Constructor<?> ctor) {
    if ((!Modifier.isPublic(ctor.getModifiers()) || 
      !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
      ctor.setAccessible(true);
    }
  }











  
  @Nullable
  public static Method findMethod(Class<?> clazz, String name) {
    return findMethod(clazz, name, EMPTY_CLASS_ARRAY);
  }










  
  @Nullable
  public static Method findMethod(Class<?> clazz, String name, @Nullable Class<?>... paramTypes) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.notNull(name, "Method name must not be null");
    Class<?> searchType = clazz;
    while (searchType != null) {
      
      Method[] methods = searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType, false);
      for (Method method : methods) {
        if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
          return method;
        }
      } 
      searchType = searchType.getSuperclass();
    } 
    return null;
  }
  
  private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
    return (paramTypes.length == method.getParameterCount() && 
      Arrays.equals((Object[])paramTypes, (Object[])method.getParameterTypes()));
  }









  
  @Nullable
  public static Object invokeMethod(Method method, @Nullable Object target) {
    return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
  }










  
  @Nullable
  public static Object invokeMethod(Method method, @Nullable Object target, @Nullable Object... args) {
    try {
      return method.invoke(target, args);
    }
    catch (Exception ex) {
      handleReflectionException(ex);
      
      throw new IllegalStateException("Should never get here");
    } 
  }








  
  public static boolean declaresException(Method method, Class<?> exceptionType) {
    Assert.notNull(method, "Method must not be null");
    Class<?>[] declaredExceptions = method.getExceptionTypes();
    for (Class<?> declaredException : declaredExceptions) {
      if (declaredException.isAssignableFrom(exceptionType)) {
        return true;
      }
    } 
    return false;
  }










  
  public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
    Method[] methods = getDeclaredMethods(clazz, false);
    for (Method method : methods) {
      try {
        mc.doWith(method);
      }
      catch (IllegalAccessException ex) {
        throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
      } 
    } 
  }










  
  public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
    doWithMethods(clazz, mc, null);
  }











  
  public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) {
    Method[] methods = getDeclaredMethods(clazz, false);
    for (Method method : methods) {
      if (mf == null || mf.matches(method))
        
        try {
          
          mc.doWith(method);
        }
        catch (IllegalAccessException ex) {
          throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
        }  
    } 
    if (clazz.getSuperclass() != null && (mf != USER_DECLARED_METHODS || clazz.getSuperclass() != Object.class)) {
      doWithMethods(clazz.getSuperclass(), mc, mf);
    }
    else if (clazz.isInterface()) {
      for (Class<?> superIfc : clazz.getInterfaces()) {
        doWithMethods(superIfc, mc, mf);
      }
    } 
  }






  
  public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
    List<Method> methods = new ArrayList<>(20);
    doWithMethods(leafClass, methods::add);
    return methods.<Method>toArray(EMPTY_METHOD_ARRAY);
  }







  
  public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
    return getUniqueDeclaredMethods(leafClass, null);
  }









  
  public static Method[] getUniqueDeclaredMethods(Class<?> leafClass, @Nullable MethodFilter mf) {
    List<Method> methods = new ArrayList<>(20);
    doWithMethods(leafClass, method -> { boolean knownSignature = false; Method methodBeingOverriddenWithCovariantReturnType = null; for (Method existingMethod : methods) { if (method.getName().equals(existingMethod.getName()) && method.getParameterCount() == existingMethod.getParameterCount() && Arrays.equals((Object[])method.getParameterTypes(), (Object[])existingMethod.getParameterTypes())) { if (existingMethod.getReturnType() != method.getReturnType() && existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) { methodBeingOverriddenWithCovariantReturnType = existingMethod; break; }  knownSignature = true; break; }  }  if (methodBeingOverriddenWithCovariantReturnType != null) methods.remove(methodBeingOverriddenWithCovariantReturnType);  if (!knownSignature && !isCglibRenamedMethod(method)) methods.add(method);  }mf);























    
    return methods.<Method>toArray(EMPTY_METHOD_ARRAY);
  }












  
  public static Method[] getDeclaredMethods(Class<?> clazz) {
    return getDeclaredMethods(clazz, true);
  }
  
  private static Method[] getDeclaredMethods(Class<?> clazz, boolean defensive) {
    Assert.notNull(clazz, "Class must not be null");
    Method[] result = declaredMethodsCache.get(clazz);
    if (result == null) {
      try {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
        if (defaultMethods != null) {
          result = new Method[declaredMethods.length + defaultMethods.size()];
          System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
          int index = declaredMethods.length;
          for (Method defaultMethod : defaultMethods) {
            result[index] = defaultMethod;
            index++;
          } 
        } else {
          
          result = declaredMethods;
        } 
        declaredMethodsCache.put(clazz, (result.length == 0) ? EMPTY_METHOD_ARRAY : result);
      }
      catch (Throwable ex) {
        throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz
            .getClassLoader() + "]", ex);
      } 
    }
    return (result.length == 0 || !defensive) ? result : (Method[])result.clone();
  }
  
  @Nullable
  private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
    List<Method> result = null;
    for (Class<?> ifc : clazz.getInterfaces()) {
      for (Method ifcMethod : ifc.getMethods()) {
        if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
          if (result == null) {
            result = new ArrayList<>();
          }
          result.add(ifcMethod);
        } 
      } 
    } 
    return result;
  }




  
  public static boolean isEqualsMethod(@Nullable Method method) {
    if (method == null) {
      return false;
    }
    if (method.getParameterCount() != 1) {
      return false;
    }
    if (!method.getName().equals("equals")) {
      return false;
    }
    return (method.getParameterTypes()[0] == Object.class);
  }




  
  public static boolean isHashCodeMethod(@Nullable Method method) {
    return (method != null && method.getParameterCount() == 0 && method.getName().equals("hashCode"));
  }




  
  public static boolean isToStringMethod(@Nullable Method method) {
    return (method != null && method.getParameterCount() == 0 && method.getName().equals("toString"));
  }



  
  public static boolean isObjectMethod(@Nullable Method method) {
    return (method != null && (method.getDeclaringClass() == Object.class || 
      isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method)));
  }





  
  public static boolean isCglibRenamedMethod(Method renamedMethod) {
    String name = renamedMethod.getName();
    if (name.startsWith("CGLIB$")) {
      int i = name.length() - 1;
      while (i >= 0 && Character.isDigit(name.charAt(i))) {
        i--;
      }
      return (i > "CGLIB$".length() && i < name.length() - 1 && name.charAt(i) == '$');
    } 
    return false;
  }









  
  public static void makeAccessible(Method method) {
    if ((!Modifier.isPublic(method.getModifiers()) || 
      !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
      method.setAccessible(true);
    }
  }










  
  @Nullable
  public static Field findField(Class<?> clazz, String name) {
    return findField(clazz, name, null);
  }









  
  @Nullable
  public static Field findField(Class<?> clazz, @Nullable String name, @Nullable Class<?> type) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.isTrue((name != null || type != null), "Either name or type of the field must be specified");
    Class<?> searchType = clazz;
    while (Object.class != searchType && searchType != null) {
      Field[] fields = getDeclaredFields(searchType);
      for (Field field : fields) {
        if ((name == null || name.equals(field.getName())) && (type == null || type
          .equals(field.getType()))) {
          return field;
        }
      } 
      searchType = searchType.getSuperclass();
    } 
    return null;
  }












  
  public static void setField(Field field, @Nullable Object target, @Nullable Object value) {
    try {
      field.set(target, value);
    }
    catch (IllegalAccessException ex) {
      handleReflectionException(ex);
    } 
  }











  
  @Nullable
  public static Object getField(Field field, @Nullable Object target) {
    try {
      return field.get(target);
    }
    catch (IllegalAccessException ex) {
      handleReflectionException(ex);
      
      throw new IllegalStateException("Should never get here");
    } 
  }







  
  public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
    for (Field field : getDeclaredFields(clazz)) {
      try {
        fc.doWith(field);
      }
      catch (IllegalAccessException ex) {
        throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
      } 
    } 
  }







  
  public static void doWithFields(Class<?> clazz, FieldCallback fc) {
    doWithFields(clazz, fc, null);
  }









  
  public static void doWithFields(Class<?> clazz, FieldCallback fc, @Nullable FieldFilter ff) {
    Class<?> targetClass = clazz;
    do {
      Field[] fields = getDeclaredFields(targetClass);
      for (Field field : fields) {
        if (ff == null || ff.matches(field))
          
          try {
            
            fc.doWith(field);
          }
          catch (IllegalAccessException ex) {
            throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
          }  
      } 
      targetClass = targetClass.getSuperclass();
    }
    while (targetClass != null && targetClass != Object.class);
  }








  
  private static Field[] getDeclaredFields(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    Field[] result = declaredFieldsCache.get(clazz);
    if (result == null) {
      try {
        result = clazz.getDeclaredFields();
        declaredFieldsCache.put(clazz, (result.length == 0) ? EMPTY_FIELD_ARRAY : result);
      }
      catch (Throwable ex) {
        throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz
            .getClassLoader() + "]", ex);
      } 
    }
    return result;
  }






  
  public static void shallowCopyFieldState(Object src, Object dest) {
    Assert.notNull(src, "Source for field copy cannot be null");
    Assert.notNull(dest, "Destination for field copy cannot be null");
    if (!src.getClass().isAssignableFrom(dest.getClass())) {
      throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src
          .getClass().getName() + "]");
    }
    doWithFields(src.getClass(), field -> { makeAccessible(field); Object srcValue = field.get(src); field.set(dest, srcValue); }COPYABLE_FIELDS);
  }








  
  public static boolean isPublicStaticFinal(Field field) {
    int modifiers = field.getModifiers();
    return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
  }









  
  public static void makeAccessible(Field field) {
    if ((!Modifier.isPublic(field.getModifiers()) || 
      !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || 
      Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
      field.setAccessible(true);
    }
  }







  
  public static void clearCache() {
    declaredMethodsCache.clear();
    declaredFieldsCache.clear();
  }








  
  @FunctionalInterface
  public static interface MethodCallback
  {
    void doWith(Method param1Method) throws IllegalArgumentException, IllegalAccessException;
  }








  
  @FunctionalInterface
  public static interface MethodFilter
  {
    boolean matches(Method param1Method);








    
    default MethodFilter and(MethodFilter next) {
      Assert.notNull(next, "Next MethodFilter must not be null");
      return method -> (matches(method) && next.matches(method));
    }
  }








  
  @FunctionalInterface
  public static interface FieldCallback
  {
    void doWith(Field param1Field) throws IllegalArgumentException, IllegalAccessException;
  }








  
  @FunctionalInterface
  public static interface FieldFilter
  {
    boolean matches(Field param1Field);








    
    default FieldFilter and(FieldFilter next) {
      Assert.notNull(next, "Next FieldFilter must not be null");
      return field -> (matches(field) && next.matches(field));
    }
  }
}
