package org.springframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;




































public class LocalVariableTableParameterNameDiscoverer
  implements ParameterNameDiscoverer
{
  private static final Log logger = LogFactory.getLog(LocalVariableTableParameterNameDiscoverer.class);

  
  private static final Map<Executable, String[]> NO_DEBUG_INFO_MAP = (Map)Collections.emptyMap();

  
  private final Map<Class<?>, Map<Executable, String[]>> parameterNamesCache = new ConcurrentHashMap<>(32);


  
  @Nullable
  public String[] getParameterNames(Method method) {
    Method originalMethod = BridgeMethodResolver.findBridgedMethod(method);
    return doGetParameterNames(originalMethod);
  }

  
  @Nullable
  public String[] getParameterNames(Constructor<?> ctor) {
    return doGetParameterNames(ctor);
  }
  
  @Nullable
  private String[] doGetParameterNames(Executable executable) {
    Class<?> declaringClass = executable.getDeclaringClass();
    Map<Executable, String[]> map = this.parameterNamesCache.computeIfAbsent(declaringClass, this::inspectClass);
    return (map != NO_DEBUG_INFO_MAP) ? map.get(executable) : null;
  }





  
  private Map<Executable, String[]> inspectClass(Class<?> clazz) {
    InputStream is = clazz.getResourceAsStream(ClassUtils.getClassFileName(clazz));
    if (is == null) {

      
      if (logger.isDebugEnabled()) {
        logger.debug("Cannot find '.class' file for class [" + clazz + "] - unable to determine constructor/method parameter names");
      }
      
      return NO_DEBUG_INFO_MAP;
    } 
    try {
      ClassReader classReader = new ClassReader(is);
      Map<Executable, String[]> map = (Map)new ConcurrentHashMap<>(32);
      classReader.accept(new ParameterNameDiscoveringVisitor(clazz, map), 0);
      return map;
    }
    catch (IOException ex) {
      if (logger.isDebugEnabled()) {
        logger.debug("Exception thrown while reading '.class' file for class [" + clazz + "] - unable to determine constructor/method parameter names", ex);
      
      }
    }
    catch (IllegalArgumentException ex) {
      if (logger.isDebugEnabled()) {
        logger.debug("ASM ClassReader failed to parse class file [" + clazz + "], probably due to a new Java class file version that isn't supported yet - unable to determine constructor/method parameter names", ex);
      }
    } finally {

      
      try {
        
        is.close();
      }
      catch (IOException iOException) {}
    } 

    
    return NO_DEBUG_INFO_MAP;
  }


  
  private static class ParameterNameDiscoveringVisitor
    extends ClassVisitor
  {
    private static final String STATIC_CLASS_INIT = "<clinit>";

    
    private final Class<?> clazz;
    
    private final Map<Executable, String[]> executableMap;

    
    public ParameterNameDiscoveringVisitor(Class<?> clazz, Map<Executable, String[]> executableMap) {
      super(17432576);
      this.clazz = clazz;
      this.executableMap = executableMap;
    }


    
    @Nullable
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      if (!isSyntheticOrBridged(access) && !"<clinit>".equals(name)) {
        return new LocalVariableTableParameterNameDiscoverer.LocalVariableTableVisitor(this.clazz, this.executableMap, name, desc, isStatic(access));
      }
      return null;
    }
    
    private static boolean isSyntheticOrBridged(int access) {
      return ((access & 0x1000 | access & 0x40) > 0);
    }
    
    private static boolean isStatic(int access) {
      return ((access & 0x8) > 0);
    }
  }


  
  private static class LocalVariableTableVisitor
    extends MethodVisitor
  {
    private static final String CONSTRUCTOR = "<init>";

    
    private final Class<?> clazz;
    
    private final Map<Executable, String[]> executableMap;
    
    private final String name;
    
    private final Type[] args;
    
    private final String[] parameterNames;
    
    private final boolean isStatic;
    
    private boolean hasLvtInfo = false;
    
    private final int[] lvtSlotIndex;

    
    public LocalVariableTableVisitor(Class<?> clazz, Map<Executable, String[]> map, String name, String desc, boolean isStatic) {
      super(17432576);
      this.clazz = clazz;
      this.executableMap = map;
      this.name = name;
      this.args = Type.getArgumentTypes(desc);
      this.parameterNames = new String[this.args.length];
      this.isStatic = isStatic;
      this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
    }

    
    public void visitLocalVariable(String name, String description, String signature, Label start, Label end, int index) {
      this.hasLvtInfo = true;
      for (int i = 0; i < this.lvtSlotIndex.length; i++) {
        if (this.lvtSlotIndex[i] == index) {
          this.parameterNames[i] = name;
        }
      } 
    }

    
    public void visitEnd() {
      if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0))
      {


        
        this.executableMap.put(resolveExecutable(), this.parameterNames);
      }
    }
    
    private Executable resolveExecutable() {
      ClassLoader loader = this.clazz.getClassLoader();
      Class<?>[] argTypes = new Class[this.args.length];
      for (int i = 0; i < this.args.length; i++) {
        argTypes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
      }
      try {
        if ("<init>".equals(this.name)) {
          return this.clazz.getDeclaredConstructor(argTypes);
        }
        return this.clazz.getDeclaredMethod(this.name, argTypes);
      }
      catch (NoSuchMethodException ex) {
        throw new IllegalStateException("Method [" + this.name + "] was discovered in the .class file but cannot be resolved in the class object", ex);
      } 
    }

    
    private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
      int[] lvtIndex = new int[paramTypes.length];
      int nextIndex = isStatic ? 0 : 1;
      for (int i = 0; i < paramTypes.length; i++) {
        lvtIndex[i] = nextIndex;
        if (isWideType(paramTypes[i])) {
          nextIndex += 2;
        } else {
          
          nextIndex++;
        } 
      } 
      return lvtIndex;
    }

    
    private static boolean isWideType(Type aType) {
      return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
    }
  }
}
