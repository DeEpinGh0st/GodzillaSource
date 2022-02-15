package org.springframework.cglib.core;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.Attribute;
import org.springframework.asm.Type;





















public class ReflectUtils
{
  static {
    Method privateLookupIn, lookupDefineClass, classLoaderDefineClass;
  }
  
  private static final Map primitives = new HashMap<>(8);
  
  private static final Map transforms = new HashMap<>(8);
  
  private static final ClassLoader defaultLoader = ReflectUtils.class.getClassLoader();

  
  private static final Method privateLookupInMethod;
  
  private static final Method lookupDefineClassMethod;
  
  private static final Method classLoaderDefineClassMethod;
  
  private static final Throwable THROWABLE;
  
  private static final ProtectionDomain PROTECTION_DOMAIN;
  
  private static final List<Method> OBJECT_METHODS = new ArrayList<>();
  
  private static final String[] CGLIB_PACKAGES;

  
  static {
    Throwable throwable = null;
    try {
      privateLookupIn = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Object run() throws Exception {
              try {
                return MethodHandles.class.getMethod("privateLookupIn", new Class[] { Class.class, MethodHandles.Lookup.class });
              }
              catch (NoSuchMethodException ex) {
                return null;
              } 
            }
          });
      lookupDefineClass = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Object run() throws Exception {
              try {
                return MethodHandles.Lookup.class.getMethod("defineClass", new Class[] { byte[].class });
              }
              catch (NoSuchMethodException ex) {
                return null;
              } 
            }
          });
      classLoaderDefineClass = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Object run() throws Exception {
              return ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class, ProtectionDomain.class });
            }
          });
    
    }
    catch (Throwable t) {
      privateLookupIn = null;
      lookupDefineClass = null;
      classLoaderDefineClass = null;
      throwable = t;
    } 
    
    privateLookupInMethod = privateLookupIn;
    lookupDefineClassMethod = lookupDefineClass;
    classLoaderDefineClassMethod = classLoaderDefineClass;
    THROWABLE = throwable;
    PROTECTION_DOMAIN = getProtectionDomain(ReflectUtils.class);
    
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Method[] methods = Object.class.getDeclaredMethods();
            for (Method method : methods) {
              if (!"finalize".equals(method.getName()) && (method
                .getModifiers() & 0x18) <= 0)
              {
                
                ReflectUtils.OBJECT_METHODS.add(method); } 
            } 
            return null;
          }
        });


    
    CGLIB_PACKAGES = new String[] { "java.lang" };

    
    primitives.put("byte", byte.class);
    primitives.put("char", char.class);
    primitives.put("double", double.class);
    primitives.put("float", float.class);
    primitives.put("int", int.class);
    primitives.put("long", long.class);
    primitives.put("short", short.class);
    primitives.put("boolean", boolean.class);
    
    transforms.put("byte", "B");
    transforms.put("char", "C");
    transforms.put("double", "D");
    transforms.put("float", "F");
    transforms.put("int", "I");
    transforms.put("long", "J");
    transforms.put("short", "S");
    transforms.put("boolean", "Z");
  }
  
  public static ProtectionDomain getProtectionDomain(final Class source) {
    if (source == null) {
      return null;
    }
    return AccessController.<ProtectionDomain>doPrivileged(new PrivilegedAction<ProtectionDomain>() {
          public Object run() {
            return source.getProtectionDomain();
          }
        });
  }
  
  public static Type[] getExceptionTypes(Member member) {
    if (member instanceof Method) {
      return TypeUtils.getTypes(((Method)member).getExceptionTypes());
    }
    if (member instanceof Constructor) {
      return TypeUtils.getTypes(((Constructor)member).getExceptionTypes());
    }
    
    throw new IllegalArgumentException("Cannot get exception types of a field");
  }

  
  public static Signature getSignature(Member member) {
    if (member instanceof Method) {
      return new Signature(member.getName(), Type.getMethodDescriptor((Method)member));
    }
    if (member instanceof Constructor) {
      Type[] types = TypeUtils.getTypes(((Constructor)member).getParameterTypes());
      return new Signature("<init>", 
          Type.getMethodDescriptor(Type.VOID_TYPE, types));
    } 

    
    throw new IllegalArgumentException("Cannot get signature of a field");
  }

  
  public static Constructor findConstructor(String desc) {
    return findConstructor(desc, defaultLoader);
  }
  
  public static Constructor findConstructor(String desc, ClassLoader loader) {
    try {
      int lparen = desc.indexOf('(');
      String className = desc.substring(0, lparen).trim();
      return getClass(className, loader).getConstructor(parseTypes(desc, loader));
    }
    catch (ClassNotFoundException|NoSuchMethodException ex) {
      throw new CodeGenerationException(ex);
    } 
  }
  
  public static Method findMethod(String desc) {
    return findMethod(desc, defaultLoader);
  }
  
  public static Method findMethod(String desc, ClassLoader loader) {
    try {
      int lparen = desc.indexOf('(');
      int dot = desc.lastIndexOf('.', lparen);
      String className = desc.substring(0, dot).trim();
      String methodName = desc.substring(dot + 1, lparen).trim();
      return getClass(className, loader).getDeclaredMethod(methodName, parseTypes(desc, loader));
    }
    catch (ClassNotFoundException|NoSuchMethodException ex) {
      throw new CodeGenerationException(ex);
    } 
  }
  
  private static Class[] parseTypes(String desc, ClassLoader loader) throws ClassNotFoundException {
    int lparen = desc.indexOf('(');
    int rparen = desc.indexOf(')', lparen);
    List<String> params = new ArrayList();
    int start = lparen + 1;
    while (true) {
      int comma = desc.indexOf(',', start);
      if (comma < 0) {
        break;
      }
      params.add(desc.substring(start, comma).trim());
      start = comma + 1;
    } 
    if (start < rparen) {
      params.add(desc.substring(start, rparen).trim());
    }
    Class[] types = new Class[params.size()];
    for (int i = 0; i < types.length; i++) {
      types[i] = getClass(params.get(i), loader);
    }
    return types;
  }
  
  private static Class getClass(String className, ClassLoader loader) throws ClassNotFoundException {
    return getClass(className, loader, CGLIB_PACKAGES);
  }
  
  private static Class getClass(String className, ClassLoader loader, String[] packages) throws ClassNotFoundException {
    String save = className;
    int dimensions = 0;
    int index = 0;
    while ((index = className.indexOf("[]", index) + 1) > 0) {
      dimensions++;
    }
    StringBuilder brackets = new StringBuilder(className.length() - dimensions);
    for (int i = 0; i < dimensions; i++) {
      brackets.append('[');
    }
    className = className.substring(0, className.length() - 2 * dimensions);
    
    String prefix = (dimensions > 0) ? (brackets + "L") : "";
    String suffix = (dimensions > 0) ? ";" : "";
    try {
      return Class.forName(prefix + className + suffix, false, loader);
    }
    catch (ClassNotFoundException classNotFoundException) {
      
      for (int j = 0; j < packages.length; j++) {
        try {
          return Class.forName(prefix + packages[j] + '.' + className + suffix, false, loader);
        }
        catch (ClassNotFoundException classNotFoundException1) {}
      } 
      
      if (dimensions == 0) {
        Class c = (Class)primitives.get(className);
        if (c != null) {
          return c;
        }
      } else {
        
        String transform = (String)transforms.get(className);
        if (transform != null) {
          try {
            return Class.forName(brackets + transform, false, loader);
          }
          catch (ClassNotFoundException classNotFoundException1) {}
        }
      } 
      
      throw new ClassNotFoundException(save);
    } 
  }
  public static Object newInstance(Class type) {
    return newInstance(type, Constants.EMPTY_CLASS_ARRAY, null);
  }
  
  public static Object newInstance(Class type, Class[] parameterTypes, Object[] args) {
    return newInstance(getConstructor(type, parameterTypes), args);
  }

  
  public static Object newInstance(Constructor cstruct, Object[] args) {
    boolean flag = cstruct.isAccessible();
    try {
      if (!flag) {
        cstruct.setAccessible(true);
      }
      Object result = cstruct.newInstance(args);
      return result;
    }
    catch (InstantiationException e) {
      throw new CodeGenerationException(e);
    }
    catch (IllegalAccessException e) {
      throw new CodeGenerationException(e);
    }
    catch (InvocationTargetException e) {
      throw new CodeGenerationException(e.getTargetException());
    } finally {
      
      if (!flag) {
        cstruct.setAccessible(flag);
      }
    } 
  }
  
  public static Constructor getConstructor(Class type, Class[] parameterTypes) {
    try {
      Constructor constructor = type.getDeclaredConstructor(parameterTypes);
      if (System.getSecurityManager() != null) {
        AccessController.doPrivileged(() -> {
              constructor.setAccessible(true);
              
              return null;
            });
      } else {
        constructor.setAccessible(true);
      } 
      return constructor;
    }
    catch (NoSuchMethodException e) {
      throw new CodeGenerationException(e);
    } 
  }
  
  public static String[] getNames(Class[] classes) {
    if (classes == null)
      return null; 
    String[] names = new String[classes.length];
    for (int i = 0; i < names.length; i++) {
      names[i] = classes[i].getName();
    }
    return names;
  }
  
  public static Class[] getClasses(Object[] objects) {
    Class[] classes = new Class[objects.length];
    for (int i = 0; i < objects.length; i++) {
      classes[i] = objects[i].getClass();
    }
    return classes;
  }
  
  public static Method findNewInstance(Class iface) {
    Method m = findInterfaceMethod(iface);
    if (!m.getName().equals("newInstance")) {
      throw new IllegalArgumentException(iface + " missing newInstance method");
    }
    return m;
  }
  
  public static Method[] getPropertyMethods(PropertyDescriptor[] properties, boolean read, boolean write) {
    Set<Method> methods = new HashSet();
    for (int i = 0; i < properties.length; i++) {
      PropertyDescriptor pd = properties[i];
      if (read) {
        methods.add(pd.getReadMethod());
      }
      if (write) {
        methods.add(pd.getWriteMethod());
      }
    } 
    methods.remove(null);
    return methods.<Method>toArray(new Method[methods.size()]);
  }
  
  public static PropertyDescriptor[] getBeanProperties(Class type) {
    return getPropertiesHelper(type, true, true);
  }
  
  public static PropertyDescriptor[] getBeanGetters(Class type) {
    return getPropertiesHelper(type, true, false);
  }
  
  public static PropertyDescriptor[] getBeanSetters(Class type) {
    return getPropertiesHelper(type, false, true);
  }
  
  private static PropertyDescriptor[] getPropertiesHelper(Class<?> type, boolean read, boolean write) {
    try {
      BeanInfo info = Introspector.getBeanInfo(type, Object.class);
      PropertyDescriptor[] all = info.getPropertyDescriptors();
      if (read && write) {
        return all;
      }
      List<PropertyDescriptor> properties = new ArrayList(all.length);
      for (int i = 0; i < all.length; i++) {
        PropertyDescriptor pd = all[i];
        if ((read && pd.getReadMethod() != null) || (write && pd
          .getWriteMethod() != null)) {
          properties.add(pd);
        }
      } 
      return properties.<PropertyDescriptor>toArray(new PropertyDescriptor[properties.size()]);
    }
    catch (IntrospectionException e) {
      throw new CodeGenerationException(e);
    } 
  }



  
  public static Method findDeclaredMethod(Class type, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
    Class cl = type;
    while (cl != null) {
      try {
        return cl.getDeclaredMethod(methodName, parameterTypes);
      }
      catch (NoSuchMethodException e) {
        cl = cl.getSuperclass();
      } 
    } 
    throw new NoSuchMethodException(methodName);
  }
  
  public static List addAllMethods(Class<Object> type, List<Method> list) {
    if (type == Object.class) {
      list.addAll(OBJECT_METHODS);
    } else {
      
      list.addAll(Arrays.asList(type.getDeclaredMethods()));
    } 
    Class<? super Object> superclass = type.getSuperclass();
    if (superclass != null) {
      addAllMethods(superclass, list);
    }
    Class[] interfaces = type.getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      addAllMethods(interfaces[i], list);
    }
    
    return list;
  }
  
  public static List addAllInterfaces(Class type, List list) {
    Class superclass = type.getSuperclass();
    if (superclass != null) {
      list.addAll(Arrays.asList(type.getInterfaces()));
      addAllInterfaces(superclass, list);
    } 
    return list;
  }

  
  public static Method findInterfaceMethod(Class iface) {
    if (!iface.isInterface()) {
      throw new IllegalArgumentException(iface + " is not an interface");
    }
    Method[] methods = iface.getDeclaredMethods();
    if (methods.length != 1) {
      throw new IllegalArgumentException("expecting exactly 1 method in " + iface);
    }
    return methods[0];
  }

  
  public static Class defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
    return defineClass(className, b, loader, null, null);
  }


  
  public static Class defineClass(String className, byte[] b, ClassLoader loader, ProtectionDomain protectionDomain) throws Exception {
    return defineClass(className, b, loader, protectionDomain, null);
  }



  
  public static Class defineClass(String className, byte[] b, ClassLoader loader, ProtectionDomain protectionDomain, Class<?> contextClass) throws Exception {
    Class c = null;
    Throwable t = THROWABLE;

    
    if (contextClass != null && contextClass.getClassLoader() == loader && privateLookupInMethod != null && lookupDefineClassMethod != null) {
      
      try {
        
        MethodHandles.Lookup lookup = (MethodHandles.Lookup)privateLookupInMethod.invoke((Object)null, new Object[] { contextClass, MethodHandles.lookup() });
        c = (Class)lookupDefineClassMethod.invoke(lookup, new Object[] { b });
      }
      catch (InvocationTargetException ex) {
        Throwable target = ex.getTargetException();
        if (target.getClass() != LinkageError.class && target.getClass() != IllegalArgumentException.class) {
          throw new CodeGenerationException(target);
        }


        
        t = target;
      }
      catch (Throwable ex) {
        throw new CodeGenerationException(ex);
      } 
    }

    
    if (c == null) {
      if (protectionDomain == null) {
        protectionDomain = PROTECTION_DOMAIN;
      }

      
      try {
        Method publicDefineClass = loader.getClass().getMethod("publicDefineClass", new Class[] { String.class, byte[].class, ProtectionDomain.class });
        
        c = (Class)publicDefineClass.invoke(loader, new Object[] { className, b, protectionDomain });
      }
      catch (InvocationTargetException ex) {
        if (!(ex.getTargetException() instanceof UnsupportedOperationException)) {
          throw new CodeGenerationException(ex.getTargetException());
        }
        
        t = ex.getTargetException();
      }
      catch (Throwable ex) {
        
        t = ex;
      } 

      
      if (c == null && classLoaderDefineClassMethod != null) {
        Object[] args = { className, b, Integer.valueOf(0), Integer.valueOf(b.length), protectionDomain };
        try {
          if (!classLoaderDefineClassMethod.isAccessible()) {
            classLoaderDefineClassMethod.setAccessible(true);
          }
          c = (Class)classLoaderDefineClassMethod.invoke(loader, args);
        }
        catch (InvocationTargetException ex) {
          throw new CodeGenerationException(ex.getTargetException());
        }
        catch (Throwable ex) {

          
          if (!ex.getClass().getName().endsWith("InaccessibleObjectException")) {
            throw new CodeGenerationException(ex);
          }
          t = ex;
        } 
      } 
    } 

    
    if (c == null && contextClass != null && contextClass.getClassLoader() != loader && privateLookupInMethod != null && lookupDefineClassMethod != null) {
      
      try {
        
        MethodHandles.Lookup lookup = (MethodHandles.Lookup)privateLookupInMethod.invoke((Object)null, new Object[] { contextClass, MethodHandles.lookup() });
        c = (Class)lookupDefineClassMethod.invoke(lookup, new Object[] { b });
      }
      catch (InvocationTargetException ex) {
        throw new CodeGenerationException(ex.getTargetException());
      }
      catch (Throwable ex) {
        throw new CodeGenerationException(ex);
      } 
    }

    
    if (c == null) {
      throw new CodeGenerationException(t);
    }

    
    Class.forName(className, true, loader);
    return c;
  }

  
  public static int findPackageProtected(Class[] classes) {
    for (int i = 0; i < classes.length; i++) {
      if (!Modifier.isPublic(classes[i].getModifiers())) {
        return i;
      }
    } 
    return 0;
  }
  
  public static MethodInfo getMethodInfo(final Member member, final int modifiers) {
    final Signature sig = getSignature(member);
    return new MethodInfo() {
        private ClassInfo ci;
        
        public ClassInfo getClassInfo() {
          if (this.ci == null)
            this.ci = ReflectUtils.getClassInfo(member.getDeclaringClass()); 
          return this.ci;
        }
        
        public int getModifiers() {
          return modifiers;
        }
        
        public Signature getSignature() {
          return sig;
        }
        
        public Type[] getExceptionTypes() {
          return ReflectUtils.getExceptionTypes(member);
        }
        
        public Attribute getAttribute() {
          return null;
        }
      };
  }
  
  public static MethodInfo getMethodInfo(Member member) {
    return getMethodInfo(member, member.getModifiers());
  }
  
  public static ClassInfo getClassInfo(final Class clazz) {
    final Type type = Type.getType(clazz);
    final Type sc = (clazz.getSuperclass() == null) ? null : Type.getType(clazz.getSuperclass());
    return new ClassInfo() {
        public Type getType() {
          return type;
        }
        public Type getSuperType() {
          return sc;
        }
        public Type[] getInterfaces() {
          return TypeUtils.getTypes(clazz.getInterfaces());
        }
        public int getModifiers() {
          return clazz.getModifiers();
        }
      };
  }

  
  public static Method[] findMethods(String[] namesAndDescriptors, Method[] methods) {
    Map<Object, Object> map = new HashMap<>();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      map.put(method.getName() + Type.getMethodDescriptor(method), method);
    } 
    Method[] result = new Method[namesAndDescriptors.length / 2];
    for (int j = 0; j < result.length; j++) {
      result[j] = (Method)map.get(namesAndDescriptors[j * 2] + namesAndDescriptors[j * 2 + 1]);
      if (result[j] == null);
    } 

    
    return result;
  }
}
