package javassist.util.proxy;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.lang.reflect.Method;























public class RuntimeSupport
{
  public static MethodHandler default_interceptor = new DefaultMethodHandler();


  
  static class DefaultMethodHandler
    implements MethodHandler, Serializable
  {
    private static final long serialVersionUID = 1L;

    
    public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Exception {
      return proceed.invoke(self, args);
    }
  }










  
  public static void find2Methods(Class<?> clazz, String superMethod, String thisMethod, int index, String desc, Method[] methods) {
    methods[index + 1] = (thisMethod == null) ? null : 
      findMethod(clazz, thisMethod, desc);
    methods[index] = findSuperClassMethod(clazz, superMethod, desc);
  }













  
  @Deprecated
  public static void find2Methods(Object self, String superMethod, String thisMethod, int index, String desc, Method[] methods) {
    methods[index + 1] = (thisMethod == null) ? null : 
      findMethod(self, thisMethod, desc);
    methods[index] = findSuperMethod(self, superMethod, desc);
  }









  
  @Deprecated
  public static Method findMethod(Object self, String name, String desc) {
    Method m = findMethod2(self.getClass(), name, desc);
    if (m == null) {
      error(self.getClass(), name, desc);
    }
    return m;
  }






  
  public static Method findMethod(Class<?> clazz, String name, String desc) {
    Method m = findMethod2(clazz, name, desc);
    if (m == null) {
      error(clazz, name, desc);
    }
    return m;
  }







  
  public static Method findSuperMethod(Object self, String name, String desc) {
    Class<?> clazz = self.getClass();
    return findSuperClassMethod(clazz, name, desc);
  }






  
  public static Method findSuperClassMethod(Class<?> clazz, String name, String desc) {
    Method m = findSuperMethod2(clazz.getSuperclass(), name, desc);
    if (m == null) {
      m = searchInterfaces(clazz, name, desc);
    }
    if (m == null) {
      error(clazz, name, desc);
    }
    return m;
  }
  
  private static void error(Class<?> clazz, String name, String desc) {
    throw new RuntimeException("not found " + name + ":" + desc + " in " + clazz
        .getName());
  }
  
  private static Method findSuperMethod2(Class<?> clazz, String name, String desc) {
    Method m = findMethod2(clazz, name, desc);
    if (m != null) {
      return m;
    }
    Class<?> superClass = clazz.getSuperclass();
    if (superClass != null) {
      m = findSuperMethod2(superClass, name, desc);
      if (m != null) {
        return m;
      }
    } 
    return searchInterfaces(clazz, name, desc);
  }
  
  private static Method searchInterfaces(Class<?> clazz, String name, String desc) {
    Method m = null;
    Class<?>[] interfaces = clazz.getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      m = findSuperMethod2(interfaces[i], name, desc);
      if (m != null) {
        return m;
      }
    } 
    return m;
  }
  
  private static Method findMethod2(Class<?> clazz, String name, String desc) {
    Method[] methods = SecurityActions.getDeclaredMethods(clazz);
    int n = methods.length;
    for (int i = 0; i < n; i++) {
      if (methods[i].getName().equals(name) && 
        makeDescriptor(methods[i]).equals(desc))
        return methods[i]; 
    } 
    return null;
  }



  
  public static String makeDescriptor(Method m) {
    Class<?>[] params = m.getParameterTypes();
    return makeDescriptor(params, m.getReturnType());
  }






  
  public static String makeDescriptor(Class<?>[] params, Class<?> retType) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('(');
    for (int i = 0; i < params.length; i++) {
      makeDesc(sbuf, params[i]);
    }
    sbuf.append(')');
    if (retType != null) {
      makeDesc(sbuf, retType);
    }
    return sbuf.toString();
  }






  
  public static String makeDescriptor(String params, Class<?> retType) {
    StringBuffer sbuf = new StringBuffer(params);
    makeDesc(sbuf, retType);
    return sbuf.toString();
  }
  
  private static void makeDesc(StringBuffer sbuf, Class<?> type) {
    if (type.isArray()) {
      sbuf.append('[');
      makeDesc(sbuf, type.getComponentType());
    }
    else if (type.isPrimitive()) {
      if (type == void.class) {
        sbuf.append('V');
      } else if (type == int.class) {
        sbuf.append('I');
      } else if (type == byte.class) {
        sbuf.append('B');
      } else if (type == long.class) {
        sbuf.append('J');
      } else if (type == double.class) {
        sbuf.append('D');
      } else if (type == float.class) {
        sbuf.append('F');
      } else if (type == char.class) {
        sbuf.append('C');
      } else if (type == short.class) {
        sbuf.append('S');
      } else if (type == boolean.class) {
        sbuf.append('Z');
      } else {
        throw new RuntimeException("bad type: " + type.getName());
      } 
    } else {
      sbuf.append('L').append(type.getName().replace('.', '/'))
        .append(';');
    } 
  }








  
  public static SerializedProxy makeSerializedProxy(Object proxy) throws InvalidClassException {
    Class<?> clazz = proxy.getClass();
    
    MethodHandler methodHandler = null;
    if (proxy instanceof ProxyObject) {
      methodHandler = ((ProxyObject)proxy).getHandler();
    } else if (proxy instanceof Proxy) {
      methodHandler = ProxyFactory.getHandler((Proxy)proxy);
    } 
    return new SerializedProxy(clazz, ProxyFactory.getFilterSignature(clazz), methodHandler);
  }
}
