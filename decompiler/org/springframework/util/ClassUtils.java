package org.springframework.util;

import java.beans.Introspector;
import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import org.springframework.lang.Nullable;





































public abstract class ClassUtils
{
  public static final String ARRAY_SUFFIX = "[]";
  private static final String INTERNAL_ARRAY_PREFIX = "[";
  private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
  private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];


  
  private static final char PACKAGE_SEPARATOR = '.';


  
  private static final char PATH_SEPARATOR = '/';


  
  private static final char NESTED_CLASS_SEPARATOR = '$';


  
  public static final String CGLIB_CLASS_SEPARATOR = "$$";

  
  public static final String CLASS_FILE_SUFFIX = ".class";

  
  private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(9);




  
  private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(9);




  
  private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);




  
  private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);




  
  private static final Set<Class<?>> javaLanguageInterfaces;



  
  private static final Map<Method, Method> interfaceMethodCache = new ConcurrentReferenceHashMap<>(256);

  
  static {
    primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
    primitiveWrapperTypeMap.put(Byte.class, byte.class);
    primitiveWrapperTypeMap.put(Character.class, char.class);
    primitiveWrapperTypeMap.put(Double.class, double.class);
    primitiveWrapperTypeMap.put(Float.class, float.class);
    primitiveWrapperTypeMap.put(Integer.class, int.class);
    primitiveWrapperTypeMap.put(Long.class, long.class);
    primitiveWrapperTypeMap.put(Short.class, short.class);
    primitiveWrapperTypeMap.put(Void.class, void.class);

    
    for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
      primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
      registerCommonClasses(new Class[] { entry.getKey() });
    } 
    
    Set<Class<?>> primitiveTypes = new HashSet<>(32);
    primitiveTypes.addAll(primitiveWrapperTypeMap.values());
    Collections.addAll(primitiveTypes, new Class[] { boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class });
    
    for (Class<?> primitiveType : primitiveTypes) {
      primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
    }
    
    registerCommonClasses(new Class[] { Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class });
    
    registerCommonClasses(new Class[] { Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class, Object.class, Object[].class });
    
    registerCommonClasses(new Class[] { Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class });
    
    registerCommonClasses(new Class[] { Enum.class, Iterable.class, Iterator.class, Enumeration.class, Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class });

    
    Class<?>[] javaLanguageInterfaceArray = new Class[] { Serializable.class, Externalizable.class, Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class };
    
    registerCommonClasses(javaLanguageInterfaceArray);
    javaLanguageInterfaces = new HashSet<>(Arrays.asList(javaLanguageInterfaceArray));
  }




  
  private static void registerCommonClasses(Class<?>... commonClasses) {
    for (Class<?> clazz : commonClasses) {
      commonClassCache.put(clazz.getName(), clazz);
    }
  }














  
  @Nullable
  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    }
    catch (Throwable throwable) {}

    
    if (cl == null) {
      
      cl = ClassUtils.class.getClassLoader();
      if (cl == null) {
        
        try {
          cl = ClassLoader.getSystemClassLoader();
        }
        catch (Throwable throwable) {}
      }
    } 

    
    return cl;
  }







  
  @Nullable
  public static ClassLoader overrideThreadContextClassLoader(@Nullable ClassLoader classLoaderToUse) {
    Thread currentThread = Thread.currentThread();
    ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
    if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
      currentThread.setContextClassLoader(classLoaderToUse);
      return threadContextClassLoader;
    } 
    
    return null;
  }
















  
  public static Class<?> forName(String name, @Nullable ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
    Assert.notNull(name, "Name must not be null");
    
    Class<?> clazz = resolvePrimitiveClassName(name);
    if (clazz == null) {
      clazz = commonClassCache.get(name);
    }
    if (clazz != null) {
      return clazz;
    }

    
    if (name.endsWith("[]")) {
      String elementClassName = name.substring(0, name.length() - "[]".length());
      Class<?> elementClass = forName(elementClassName, classLoader);
      return Array.newInstance(elementClass, 0).getClass();
    } 

    
    if (name.startsWith("[L") && name.endsWith(";")) {
      String elementName = name.substring("[L".length(), name.length() - 1);
      Class<?> elementClass = forName(elementName, classLoader);
      return Array.newInstance(elementClass, 0).getClass();
    } 

    
    if (name.startsWith("[")) {
      String elementName = name.substring("[".length());
      Class<?> elementClass = forName(elementName, classLoader);
      return Array.newInstance(elementClass, 0).getClass();
    } 
    
    ClassLoader clToUse = classLoader;
    if (clToUse == null) {
      clToUse = getDefaultClassLoader();
    }
    try {
      return Class.forName(name, false, clToUse);
    }
    catch (ClassNotFoundException ex) {
      int lastDotIndex = name.lastIndexOf('.');
      if (lastDotIndex != -1) {
        
        String nestedClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
        try {
          return Class.forName(nestedClassName, false, clToUse);
        }
        catch (ClassNotFoundException classNotFoundException) {}
      } 

      
      throw ex;
    } 
  }




















  
  public static Class<?> resolveClassName(String className, @Nullable ClassLoader classLoader) throws IllegalArgumentException {
    try {
      return forName(className, classLoader);
    }
    catch (IllegalAccessError err) {
      throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err
          .getMessage(), err);
    }
    catch (LinkageError err) {
      throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
    }
    catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException("Could not find class [" + className + "]", ex);
    } 
  }














  
  public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
    try {
      forName(className, classLoader);
      return true;
    }
    catch (IllegalAccessError err) {
      throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err
          .getMessage(), err);
    }
    catch (Throwable ex) {
      
      return false;
    } 
  }






  
  public static boolean isVisible(Class<?> clazz, @Nullable ClassLoader classLoader) {
    if (classLoader == null) {
      return true;
    }
    try {
      if (clazz.getClassLoader() == classLoader) {
        return true;
      }
    }
    catch (SecurityException securityException) {}



    
    return isLoadable(clazz, classLoader);
  }







  
  public static boolean isCacheSafe(Class<?> clazz, @Nullable ClassLoader classLoader) {
    Assert.notNull(clazz, "Class must not be null");
    try {
      ClassLoader target = clazz.getClassLoader();
      
      if (target == classLoader || target == null) {
        return true;
      }
      if (classLoader == null) {
        return false;
      }
      
      ClassLoader current = classLoader;
      while (current != null) {
        current = current.getParent();
        if (current == target) {
          return true;
        }
      } 
      
      while (target != null) {
        target = target.getParent();
        if (target == classLoader) {
          return false;
        }
      }
    
    } catch (SecurityException securityException) {}




    
    return (classLoader != null && isLoadable(clazz, classLoader));
  }






  
  private static boolean isLoadable(Class<?> clazz, ClassLoader classLoader) {
    try {
      return (clazz == classLoader.loadClass(clazz.getName()));
    
    }
    catch (ClassNotFoundException ex) {
      
      return false;
    } 
  }










  
  @Nullable
  public static Class<?> resolvePrimitiveClassName(@Nullable String name) {
    Class<?> result = null;

    
    if (name != null && name.length() <= 7)
    {
      result = primitiveTypeNameMap.get(name);
    }
    return result;
  }







  
  public static boolean isPrimitiveWrapper(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return primitiveWrapperTypeMap.containsKey(clazz);
  }









  
  public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
  }






  
  public static boolean isPrimitiveArray(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return (clazz.isArray() && clazz.getComponentType().isPrimitive());
  }






  
  public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
  }






  
  public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return (clazz.isPrimitive() && clazz != void.class) ? primitiveTypeToWrapperMap.get(clazz) : clazz;
  }









  
  public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
    Assert.notNull(lhsType, "Left-hand side type must not be null");
    Assert.notNull(rhsType, "Right-hand side type must not be null");
    if (lhsType.isAssignableFrom(rhsType)) {
      return true;
    }
    if (lhsType.isPrimitive()) {
      Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
      return (lhsType == resolvedPrimitive);
    } 
    
    Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
    return (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper));
  }









  
  public static boolean isAssignableValue(Class<?> type, @Nullable Object value) {
    Assert.notNull(type, "Type must not be null");
    return (value != null) ? isAssignable(type, value.getClass()) : (!type.isPrimitive());
  }





  
  public static String convertResourcePathToClassName(String resourcePath) {
    Assert.notNull(resourcePath, "Resource path must not be null");
    return resourcePath.replace('/', '.');
  }





  
  public static String convertClassNameToResourcePath(String className) {
    Assert.notNull(className, "Class name must not be null");
    return className.replace('.', '/');
  }
















  
  public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
    Assert.notNull(resourceName, "Resource name must not be null");
    if (!resourceName.startsWith("/")) {
      return classPackageAsResourcePath(clazz) + '/' + resourceName;
    }
    return classPackageAsResourcePath(clazz) + resourceName;
  }














  
  public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
    if (clazz == null) {
      return "";
    }
    String className = clazz.getName();
    int packageEndIndex = className.lastIndexOf('.');
    if (packageEndIndex == -1) {
      return "";
    }
    String packageName = className.substring(0, packageEndIndex);
    return packageName.replace('.', '/');
  }









  
  public static String classNamesToString(Class<?>... classes) {
    return classNamesToString(Arrays.asList(classes));
  }









  
  public static String classNamesToString(@Nullable Collection<Class<?>> classes) {
    if (CollectionUtils.isEmpty(classes)) {
      return "[]";
    }
    StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
    for (Class<?> clazz : classes) {
      stringJoiner.add(clazz.getName());
    }
    return stringJoiner.toString();
  }








  
  public static Class<?>[] toClassArray(@Nullable Collection<Class<?>> collection) {
    return !CollectionUtils.isEmpty(collection) ? (Class[])collection.<Class<?>[]>toArray((Class<?>[][])EMPTY_CLASS_ARRAY) : EMPTY_CLASS_ARRAY;
  }






  
  public static Class<?>[] getAllInterfaces(Object instance) {
    Assert.notNull(instance, "Instance must not be null");
    return getAllInterfacesForClass(instance.getClass());
  }







  
  public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
    return getAllInterfacesForClass(clazz, null);
  }









  
  public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, @Nullable ClassLoader classLoader) {
    return toClassArray(getAllInterfacesForClassAsSet(clazz, classLoader));
  }






  
  public static Set<Class<?>> getAllInterfacesAsSet(Object instance) {
    Assert.notNull(instance, "Instance must not be null");
    return getAllInterfacesForClassAsSet(instance.getClass());
  }







  
  public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
    return getAllInterfacesForClassAsSet(clazz, null);
  }









  
  public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, @Nullable ClassLoader classLoader) {
    Assert.notNull(clazz, "Class must not be null");
    if (clazz.isInterface() && isVisible(clazz, classLoader)) {
      return Collections.singleton(clazz);
    }
    Set<Class<?>> interfaces = new LinkedHashSet<>();
    Class<?> current = clazz;
    while (current != null) {
      Class<?>[] ifcs = current.getInterfaces();
      for (Class<?> ifc : ifcs) {
        if (isVisible(ifc, classLoader)) {
          interfaces.add(ifc);
        }
      } 
      current = current.getSuperclass();
    } 
    return interfaces;
  }












  
  public static Class<?> createCompositeInterface(Class<?>[] interfaces, @Nullable ClassLoader classLoader) {
    Assert.notEmpty((Object[])interfaces, "Interface array must not be empty");
    return Proxy.getProxyClass(classLoader, interfaces);
  }









  
  @Nullable
  public static Class<?> determineCommonAncestor(@Nullable Class<?> clazz1, @Nullable Class<?> clazz2) {
    if (clazz1 == null) {
      return clazz2;
    }
    if (clazz2 == null) {
      return clazz1;
    }
    if (clazz1.isAssignableFrom(clazz2)) {
      return clazz1;
    }
    if (clazz2.isAssignableFrom(clazz1)) {
      return clazz2;
    }
    Class<?> ancestor = clazz1;
    while (true) {
      ancestor = ancestor.getSuperclass();
      if (ancestor == null || Object.class == ancestor) {
        return null;
      }
      
      if (ancestor.isAssignableFrom(clazz2)) {
        return ancestor;
      }
    } 
  }







  
  public static boolean isJavaLanguageInterface(Class<?> ifc) {
    return javaLanguageInterfaces.contains(ifc);
  }







  
  public static boolean isInnerClass(Class<?> clazz) {
    return (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers()));
  }







  
  @Deprecated
  public static boolean isCglibProxy(Object object) {
    return isCglibProxyClass(object.getClass());
  }






  
  @Deprecated
  public static boolean isCglibProxyClass(@Nullable Class<?> clazz) {
    return (clazz != null && isCglibProxyClassName(clazz.getName()));
  }





  
  @Deprecated
  public static boolean isCglibProxyClassName(@Nullable String className) {
    return (className != null && className.contains("$$"));
  }







  
  public static Class<?> getUserClass(Object instance) {
    Assert.notNull(instance, "Instance must not be null");
    return getUserClass(instance.getClass());
  }






  
  public static Class<?> getUserClass(Class<?> clazz) {
    if (clazz.getName().contains("$$")) {
      Class<?> superclass = clazz.getSuperclass();
      if (superclass != null && superclass != Object.class) {
        return superclass;
      }
    } 
    return clazz;
  }







  
  @Nullable
  public static String getDescriptiveType(@Nullable Object value) {
    if (value == null) {
      return null;
    }
    Class<?> clazz = value.getClass();
    if (Proxy.isProxyClass(clazz)) {
      String prefix = clazz.getName() + " implementing ";
      StringJoiner result = new StringJoiner(",", prefix, "");
      for (Class<?> ifc : clazz.getInterfaces()) {
        result.add(ifc.getName());
      }
      return result.toString();
    } 
    
    return clazz.getTypeName();
  }






  
  public static boolean matchesTypeName(Class<?> clazz, @Nullable String typeName) {
    return (typeName != null && (typeName
      .equals(clazz.getTypeName()) || typeName.equals(clazz.getSimpleName())));
  }






  
  public static String getShortName(String className) {
    Assert.hasLength(className, "Class name must not be empty");
    int lastDotIndex = className.lastIndexOf('.');
    int nameEndIndex = className.indexOf("$$");
    if (nameEndIndex == -1) {
      nameEndIndex = className.length();
    }
    String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
    shortName = shortName.replace('$', '.');
    return shortName;
  }





  
  public static String getShortName(Class<?> clazz) {
    return getShortName(getQualifiedName(clazz));
  }







  
  public static String getShortNameAsProperty(Class<?> clazz) {
    String shortName = getShortName(clazz);
    int dotIndex = shortName.lastIndexOf('.');
    shortName = (dotIndex != -1) ? shortName.substring(dotIndex + 1) : shortName;
    return Introspector.decapitalize(shortName);
  }






  
  public static String getClassFileName(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    String className = clazz.getName();
    int lastDotIndex = className.lastIndexOf('.');
    return className.substring(lastDotIndex + 1) + ".class";
  }







  
  public static String getPackageName(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return getPackageName(clazz.getName());
  }







  
  public static String getPackageName(String fqClassName) {
    Assert.notNull(fqClassName, "Class name must not be null");
    int lastDotIndex = fqClassName.lastIndexOf('.');
    return (lastDotIndex != -1) ? fqClassName.substring(0, lastDotIndex) : "";
  }






  
  public static String getQualifiedName(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return clazz.getTypeName();
  }






  
  public static String getQualifiedMethodName(Method method) {
    return getQualifiedMethodName(method, null);
  }









  
  public static String getQualifiedMethodName(Method method, @Nullable Class<?> clazz) {
    Assert.notNull(method, "Method must not be null");
    return ((clazz != null) ? clazz : method.getDeclaringClass()).getName() + '.' + method.getName();
  }








  
  public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
    return (getConstructorIfAvailable(clazz, paramTypes) != null);
  }









  
  @Nullable
  public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
    Assert.notNull(clazz, "Class must not be null");
    try {
      return clazz.getConstructor(paramTypes);
    }
    catch (NoSuchMethodException ex) {
      return null;
    } 
  }







  
  public static boolean hasMethod(Class<?> clazz, Method method) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.notNull(method, "Method must not be null");
    if (clazz == method.getDeclaringClass()) {
      return true;
    }
    String methodName = method.getName();
    Class<?>[] paramTypes = method.getParameterTypes();
    return (getMethodOrNull(clazz, methodName, paramTypes) != null);
  }









  
  public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
    return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
  }














  
  public static Method getMethod(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.notNull(methodName, "Method name must not be null");
    if (paramTypes != null) {
      try {
        return clazz.getMethod(methodName, paramTypes);
      }
      catch (NoSuchMethodException ex) {
        throw new IllegalStateException("Expected method not found: " + ex);
      } 
    }
    
    Set<Method> candidates = findMethodCandidatesByName(clazz, methodName);
    if (candidates.size() == 1) {
      return candidates.iterator().next();
    }
    if (candidates.isEmpty()) {
      throw new IllegalStateException("Expected method not found: " + clazz.getName() + '.' + methodName);
    }
    
    throw new IllegalStateException("No unique method found: " + clazz.getName() + '.' + methodName);
  }















  
  @Nullable
  public static Method getMethodIfAvailable(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.notNull(methodName, "Method name must not be null");
    if (paramTypes != null) {
      return getMethodOrNull(clazz, methodName, paramTypes);
    }
    
    Set<Method> candidates = findMethodCandidatesByName(clazz, methodName);
    if (candidates.size() == 1) {
      return candidates.iterator().next();
    }
    return null;
  }








  
  public static int getMethodCountForName(Class<?> clazz, String methodName) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.notNull(methodName, "Method name must not be null");
    int count = 0;
    Method[] declaredMethods = clazz.getDeclaredMethods();
    for (Method method : declaredMethods) {
      if (methodName.equals(method.getName())) {
        count++;
      }
    } 
    Class<?>[] ifcs = clazz.getInterfaces();
    for (Class<?> ifc : ifcs) {
      count += getMethodCountForName(ifc, methodName);
    }
    if (clazz.getSuperclass() != null) {
      count += getMethodCountForName(clazz.getSuperclass(), methodName);
    }
    return count;
  }








  
  public static boolean hasAtLeastOneMethodWithName(Class<?> clazz, String methodName) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.notNull(methodName, "Method name must not be null");
    Method[] declaredMethods = clazz.getDeclaredMethods();
    for (Method method : declaredMethods) {
      if (method.getName().equals(methodName)) {
        return true;
      }
    } 
    Class<?>[] ifcs = clazz.getInterfaces();
    for (Class<?> ifc : ifcs) {
      if (hasAtLeastOneMethodWithName(ifc, methodName)) {
        return true;
      }
    } 
    return (clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName));
  }





















  
  public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
    if (targetClass != null && targetClass != method.getDeclaringClass() && isOverridable(method, targetClass)) {
      try {
        if (Modifier.isPublic(method.getModifiers())) {
          try {
            return targetClass.getMethod(method.getName(), method.getParameterTypes());
          }
          catch (NoSuchMethodException ex) {
            return method;
          } 
        }

        
        Method specificMethod = ReflectionUtils.findMethod(targetClass, method.getName(), method.getParameterTypes());
        return (specificMethod != null) ? specificMethod : method;
      
      }
      catch (SecurityException securityException) {}
    }

    
    return method;
  }









  
  public static Method getInterfaceMethodIfPossible(Method method) {
    if (!Modifier.isPublic(method.getModifiers()) || method.getDeclaringClass().isInterface()) {
      return method;
    }
    return interfaceMethodCache.computeIfAbsent(method, key -> {
          Class<?> current = key.getDeclaringClass();
          
          while (current != null && current != Object.class) {
            Class<?>[] ifcs = current.getInterfaces();
            for (Class<?> ifc : ifcs) {
              try {
                return ifc.getMethod(key.getName(), key.getParameterTypes());
              } catch (NoSuchMethodException noSuchMethodException) {}
            } 
            current = current.getSuperclass();
          } 
          return key;
        });
  }













  
  public static boolean isUserLevelMethod(Method method) {
    Assert.notNull(method, "Method must not be null");
    return (method.isBridge() || (!method.isSynthetic() && !isGroovyObjectMethod(method)));
  }
  
  private static boolean isGroovyObjectMethod(Method method) {
    return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
  }





  
  private static boolean isOverridable(Method method, @Nullable Class<?> targetClass) {
    if (Modifier.isPrivate(method.getModifiers())) {
      return false;
    }
    if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
      return true;
    }
    return (targetClass == null || 
      getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass)));
  }








  
  @Nullable
  public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args) {
    Assert.notNull(clazz, "Class must not be null");
    Assert.notNull(methodName, "Method name must not be null");
    try {
      Method method = clazz.getMethod(methodName, args);
      return Modifier.isStatic(method.getModifiers()) ? method : null;
    }
    catch (NoSuchMethodException ex) {
      return null;
    } 
  }

  
  @Nullable
  private static Method getMethodOrNull(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
    try {
      return clazz.getMethod(methodName, paramTypes);
    }
    catch (NoSuchMethodException ex) {
      return null;
    } 
  }
  
  private static Set<Method> findMethodCandidatesByName(Class<?> clazz, String methodName) {
    Set<Method> candidates = new HashSet<>(1);
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      if (methodName.equals(method.getName())) {
        candidates.add(method);
      }
    } 
    return candidates;
  }
}
