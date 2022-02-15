package org.springframework.expression.spel.support;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompilablePropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

































public class ReflectivePropertyAccessor
  implements PropertyAccessor
{
  private static final Set<Class<?>> ANY_TYPES = Collections.emptySet();
  private static final Set<Class<?>> BOOLEAN_TYPES;
  private final boolean allowWrite;
  
  static {
    Set<Class<?>> booleanTypes = new HashSet<>(4);
    booleanTypes.add(Boolean.class);
    booleanTypes.add(boolean.class);
    BOOLEAN_TYPES = Collections.unmodifiableSet(booleanTypes);
  }



  
  private final Map<PropertyCacheKey, InvokerPair> readerCache = new ConcurrentHashMap<>(64);
  
  private final Map<PropertyCacheKey, Member> writerCache = new ConcurrentHashMap<>(64);
  
  private final Map<PropertyCacheKey, TypeDescriptor> typeDescriptorCache = new ConcurrentHashMap<>(64);
  
  private final Map<Class<?>, Method[]> sortedMethodsCache = (Map)new ConcurrentHashMap<>(64);


  
  @Nullable
  private volatile InvokerPair lastReadInvokerPair;



  
  public ReflectivePropertyAccessor() {
    this.allowWrite = true;
  }






  
  public ReflectivePropertyAccessor(boolean allowWrite) {
    this.allowWrite = allowWrite;
  }





  
  @Nullable
  public Class<?>[] getSpecificTargetClasses() {
    return null;
  }

  
  public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
    if (target == null) {
      return false;
    }
    
    Class<?> type = (target instanceof Class) ? (Class)target : target.getClass();
    if (type.isArray() && name.equals("length")) {
      return true;
    }
    
    PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
    if (this.readerCache.containsKey(cacheKey)) {
      return true;
    }
    
    Method method = findGetterForProperty(name, type, target);
    if (method != null) {

      
      Property property = new Property(type, method, null);
      TypeDescriptor typeDescriptor = new TypeDescriptor(property);
      method = ClassUtils.getInterfaceMethodIfPossible(method);
      this.readerCache.put(cacheKey, new InvokerPair(method, typeDescriptor));
      this.typeDescriptorCache.put(cacheKey, typeDescriptor);
      return true;
    } 
    
    Field field = findField(name, type, target);
    if (field != null) {
      TypeDescriptor typeDescriptor = new TypeDescriptor(field);
      this.readerCache.put(cacheKey, new InvokerPair(field, typeDescriptor));
      this.typeDescriptorCache.put(cacheKey, typeDescriptor);
      return true;
    } 

    
    return false;
  }

  
  public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
    Assert.state((target != null), "Target must not be null");
    Class<?> type = (target instanceof Class) ? (Class)target : target.getClass();
    
    if (type.isArray() && name.equals("length")) {
      if (target instanceof Class) {
        throw new AccessException("Cannot access length on array class itself");
      }
      return new TypedValue(Integer.valueOf(Array.getLength(target)));
    } 
    
    PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
    InvokerPair invoker = this.readerCache.get(cacheKey);
    this.lastReadInvokerPair = invoker;
    
    if (invoker == null || invoker.member instanceof Method) {
      Method method = (invoker != null) ? (Method)invoker.member : null;
      if (method == null) {
        method = findGetterForProperty(name, type, target);
        if (method != null) {

          
          Property property = new Property(type, method, null);
          TypeDescriptor typeDescriptor = new TypeDescriptor(property);
          method = ClassUtils.getInterfaceMethodIfPossible(method);
          invoker = new InvokerPair(method, typeDescriptor);
          this.lastReadInvokerPair = invoker;
          this.readerCache.put(cacheKey, invoker);
        } 
      } 
      if (method != null) {
        try {
          ReflectionUtils.makeAccessible(method);
          Object value = method.invoke(target, new Object[0]);
          return new TypedValue(value, invoker.typeDescriptor.narrow(value));
        }
        catch (Exception ex) {
          throw new AccessException("Unable to access property '" + name + "' through getter method", ex);
        } 
      }
    } 
    
    if (invoker == null || invoker.member instanceof Field) {
      Field field = (invoker == null) ? null : (Field)invoker.member;
      if (field == null) {
        field = findField(name, type, target);
        if (field != null) {
          invoker = new InvokerPair(field, new TypeDescriptor(field));
          this.lastReadInvokerPair = invoker;
          this.readerCache.put(cacheKey, invoker);
        } 
      } 
      if (field != null) {
        try {
          ReflectionUtils.makeAccessible(field);
          Object value = field.get(target);
          return new TypedValue(value, invoker.typeDescriptor.narrow(value));
        }
        catch (Exception ex) {
          throw new AccessException("Unable to access field '" + name + "'", ex);
        } 
      }
    } 
    
    throw new AccessException("Neither getter method nor field found for property '" + name + "'");
  }

  
  public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
    if (!this.allowWrite || target == null) {
      return false;
    }
    
    Class<?> type = (target instanceof Class) ? (Class)target : target.getClass();
    PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
    if (this.writerCache.containsKey(cacheKey)) {
      return true;
    }
    
    Method method = findSetterForProperty(name, type, target);
    if (method != null) {
      
      Property property = new Property(type, null, method);
      TypeDescriptor typeDescriptor = new TypeDescriptor(property);
      method = ClassUtils.getInterfaceMethodIfPossible(method);
      this.writerCache.put(cacheKey, method);
      this.typeDescriptorCache.put(cacheKey, typeDescriptor);
      return true;
    } 
    
    Field field = findField(name, type, target);
    if (field != null) {
      this.writerCache.put(cacheKey, field);
      this.typeDescriptorCache.put(cacheKey, new TypeDescriptor(field));
      return true;
    } 

    
    return false;
  }



  
  public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) throws AccessException {
    if (!this.allowWrite) {
      throw new AccessException("PropertyAccessor for property '" + name + "' on target [" + target + "] does not allow write operations");
    }

    
    Assert.state((target != null), "Target must not be null");
    Class<?> type = (target instanceof Class) ? (Class)target : target.getClass();
    
    Object possiblyConvertedNewValue = newValue;
    TypeDescriptor typeDescriptor = getTypeDescriptor(context, target, name);
    if (typeDescriptor != null) {
      try {
        possiblyConvertedNewValue = context.getTypeConverter().convertValue(newValue, 
            TypeDescriptor.forObject(newValue), typeDescriptor);
      }
      catch (EvaluationException evaluationException) {
        throw new AccessException("Type conversion failure", evaluationException);
      } 
    }
    
    PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
    Member cachedMember = this.writerCache.get(cacheKey);
    
    if (cachedMember == null || cachedMember instanceof Method) {
      Method method = (Method)cachedMember;
      if (method == null) {
        method = findSetterForProperty(name, type, target);
        if (method != null) {
          method = ClassUtils.getInterfaceMethodIfPossible(method);
          cachedMember = method;
          this.writerCache.put(cacheKey, cachedMember);
        } 
      } 
      if (method != null) {
        try {
          ReflectionUtils.makeAccessible(method);
          method.invoke(target, new Object[] { possiblyConvertedNewValue });
          
          return;
        } catch (Exception ex) {
          throw new AccessException("Unable to access property '" + name + "' through setter method", ex);
        } 
      }
    } 
    
    if (cachedMember == null || cachedMember instanceof Field) {
      Field field = (Field)cachedMember;
      if (field == null) {
        field = findField(name, type, target);
        if (field != null) {
          cachedMember = field;
          this.writerCache.put(cacheKey, cachedMember);
        } 
      } 
      if (field != null) {
        try {
          ReflectionUtils.makeAccessible(field);
          field.set(target, possiblyConvertedNewValue);
          
          return;
        } catch (Exception ex) {
          throw new AccessException("Unable to access field '" + name + "'", ex);
        } 
      }
    } 
    
    throw new AccessException("Neither setter method nor field found for property '" + name + "'");
  }




  
  @Deprecated
  @Nullable
  public Member getLastReadInvokerPair() {
    InvokerPair lastReadInvoker = this.lastReadInvokerPair;
    return (lastReadInvoker != null) ? lastReadInvoker.member : null;
  }

  
  @Nullable
  private TypeDescriptor getTypeDescriptor(EvaluationContext context, Object target, String name) {
    Class<?> type = (target instanceof Class) ? (Class)target : target.getClass();
    
    if (type.isArray() && name.equals("length")) {
      return TypeDescriptor.valueOf(int.class);
    }
    PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
    TypeDescriptor typeDescriptor = this.typeDescriptorCache.get(cacheKey);
    if (typeDescriptor == null) {
      
      try {
        if (canRead(context, target, name) || canWrite(context, target, name)) {
          typeDescriptor = this.typeDescriptorCache.get(cacheKey);
        }
      }
      catch (AccessException accessException) {}
    }

    
    return typeDescriptor;
  }
  
  @Nullable
  private Method findGetterForProperty(String propertyName, Class<?> clazz, Object target) {
    Method method = findGetterForProperty(propertyName, clazz, target instanceof Class);
    if (method == null && target instanceof Class) {
      method = findGetterForProperty(propertyName, target.getClass(), false);
    }
    return method;
  }
  
  @Nullable
  private Method findSetterForProperty(String propertyName, Class<?> clazz, Object target) {
    Method method = findSetterForProperty(propertyName, clazz, target instanceof Class);
    if (method == null && target instanceof Class) {
      method = findSetterForProperty(propertyName, target.getClass(), false);
    }
    return method;
  }



  
  @Nullable
  protected Method findGetterForProperty(String propertyName, Class<?> clazz, boolean mustBeStatic) {
    Method method = findMethodForProperty(getPropertyMethodSuffixes(propertyName), "get", clazz, mustBeStatic, 0, ANY_TYPES);
    
    if (method == null) {
      method = findMethodForProperty(getPropertyMethodSuffixes(propertyName), "is", clazz, mustBeStatic, 0, BOOLEAN_TYPES);
      
      if (method == null)
      {
        method = findMethodForProperty(new String[] { propertyName }, "", clazz, mustBeStatic, 0, ANY_TYPES);
      }
    } 
    
    return method;
  }



  
  @Nullable
  protected Method findSetterForProperty(String propertyName, Class<?> clazz, boolean mustBeStatic) {
    return findMethodForProperty(getPropertyMethodSuffixes(propertyName), "set", clazz, mustBeStatic, 1, ANY_TYPES);
  }



  
  @Nullable
  private Method findMethodForProperty(String[] methodSuffixes, String prefix, Class<?> clazz, boolean mustBeStatic, int numberOfParams, Set<Class<?>> requiredReturnTypes) {
    Method[] methods = getSortedMethods(clazz);
    for (String methodSuffix : methodSuffixes) {
      for (Method method : methods) {
        if (isCandidateForProperty(method, clazz) && method.getName().equals(prefix + methodSuffix) && method
          .getParameterCount() == numberOfParams && (!mustBeStatic || 
          Modifier.isStatic(method.getModifiers())) && (requiredReturnTypes
          .isEmpty() || requiredReturnTypes.contains(method.getReturnType()))) {
          return method;
        }
      } 
    } 
    return null;
  }



  
  private Method[] getSortedMethods(Class<?> clazz) {
    return this.sortedMethodsCache.computeIfAbsent(clazz, key -> {
          Method[] methods = key.getMethods();
          Arrays.sort(methods, ());
          return methods;
        });
  }









  
  protected boolean isCandidateForProperty(Method method, Class<?> targetClass) {
    return true;
  }






  
  protected String[] getPropertyMethodSuffixes(String propertyName) {
    String suffix = getPropertyMethodSuffix(propertyName);
    if (suffix.length() > 0 && Character.isUpperCase(suffix.charAt(0))) {
      return new String[] { suffix };
    }
    return new String[] { suffix, StringUtils.capitalize(suffix) };
  }




  
  protected String getPropertyMethodSuffix(String propertyName) {
    if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(1))) {
      return propertyName;
    }
    return StringUtils.capitalize(propertyName);
  }
  
  @Nullable
  private Field findField(String name, Class<?> clazz, Object target) {
    Field field = findField(name, clazz, target instanceof Class);
    if (field == null && target instanceof Class) {
      field = findField(name, target.getClass(), false);
    }
    return field;
  }



  
  @Nullable
  protected Field findField(String name, Class<?> clazz, boolean mustBeStatic) {
    Field[] fields = clazz.getFields();
    for (Field field : fields) {
      if (field.getName().equals(name) && (!mustBeStatic || Modifier.isStatic(field.getModifiers()))) {
        return field;
      }
    } 

    
    if (clazz.getSuperclass() != null) {
      Field field = findField(name, clazz.getSuperclass(), mustBeStatic);
      if (field != null) {
        return field;
      }
    } 
    for (Class<?> implementedInterface : clazz.getInterfaces()) {
      Field field = findField(name, implementedInterface, mustBeStatic);
      if (field != null) {
        return field;
      }
    } 
    return null;
  }












  
  public PropertyAccessor createOptimalAccessor(EvaluationContext context, @Nullable Object target, String name) {
    if (target == null) {
      return this;
    }
    Class<?> clazz = (target instanceof Class) ? (Class)target : target.getClass();
    if (clazz.isArray()) {
      return this;
    }
    
    PropertyCacheKey cacheKey = new PropertyCacheKey(clazz, name, target instanceof Class);
    InvokerPair invocationTarget = this.readerCache.get(cacheKey);
    
    if (invocationTarget == null || invocationTarget.member instanceof Method) {
      Method method = (invocationTarget != null) ? (Method)invocationTarget.member : null;
      if (method == null) {
        method = findGetterForProperty(name, clazz, target);
        if (method != null) {
          TypeDescriptor typeDescriptor = new TypeDescriptor(new MethodParameter(method, -1));
          method = ClassUtils.getInterfaceMethodIfPossible(method);
          invocationTarget = new InvokerPair(method, typeDescriptor);
          ReflectionUtils.makeAccessible(method);
          this.readerCache.put(cacheKey, invocationTarget);
        } 
      } 
      if (method != null) {
        return (PropertyAccessor)new OptimalPropertyAccessor(invocationTarget);
      }
    } 
    
    if (invocationTarget == null || invocationTarget.member instanceof Field) {
      Field field = (invocationTarget != null) ? (Field)invocationTarget.member : null;
      if (field == null) {
        field = findField(name, clazz, target instanceof Class);
        if (field != null) {
          invocationTarget = new InvokerPair(field, new TypeDescriptor(field));
          ReflectionUtils.makeAccessible(field);
          this.readerCache.put(cacheKey, invocationTarget);
        } 
      } 
      if (field != null) {
        return (PropertyAccessor)new OptimalPropertyAccessor(invocationTarget);
      }
    } 
    
    return this;
  }


  
  private static class InvokerPair
  {
    final Member member;

    
    final TypeDescriptor typeDescriptor;


    
    public InvokerPair(Member member, TypeDescriptor typeDescriptor) {
      this.member = member;
      this.typeDescriptor = typeDescriptor;
    }
  }

  
  private static final class PropertyCacheKey
    implements Comparable<PropertyCacheKey>
  {
    private final Class<?> clazz;
    
    private final String property;
    private boolean targetIsClass;
    
    public PropertyCacheKey(Class<?> clazz, String name, boolean targetIsClass) {
      this.clazz = clazz;
      this.property = name;
      this.targetIsClass = targetIsClass;
    }

    
    public boolean equals(@Nullable Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof PropertyCacheKey)) {
        return false;
      }
      PropertyCacheKey otherKey = (PropertyCacheKey)other;
      return (this.clazz == otherKey.clazz && this.property.equals(otherKey.property) && this.targetIsClass == otherKey.targetIsClass);
    }


    
    public int hashCode() {
      return this.clazz.hashCode() * 29 + this.property.hashCode();
    }

    
    public String toString() {
      return "PropertyCacheKey [clazz=" + this.clazz.getName() + ", property=" + this.property + ", targetIsClass=" + this.targetIsClass + "]";
    }


    
    public int compareTo(PropertyCacheKey other) {
      int result = this.clazz.getName().compareTo(other.clazz.getName());
      if (result == 0) {
        result = this.property.compareTo(other.property);
      }
      return result;
    }
  }




  
  public static class OptimalPropertyAccessor
    implements CompilablePropertyAccessor
  {
    public final Member member;



    
    private final TypeDescriptor typeDescriptor;




    
    OptimalPropertyAccessor(ReflectivePropertyAccessor.InvokerPair target) {
      this.member = target.member;
      this.typeDescriptor = target.typeDescriptor;
    }

    
    @Nullable
    public Class<?>[] getSpecificTargetClasses() {
      throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
    }

    
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
      if (target == null) {
        return false;
      }
      Class<?> type = (target instanceof Class) ? (Class)target : target.getClass();
      if (type.isArray()) {
        return false;
      }
      
      if (this.member instanceof Method) {
        Method method = (Method)this.member;
        String getterName = "get" + StringUtils.capitalize(name);
        if (getterName.equals(method.getName())) {
          return true;
        }
        getterName = "is" + StringUtils.capitalize(name);
        if (getterName.equals(method.getName())) {
          return true;
        }
      } 
      return this.member.getName().equals(name);
    }

    
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
      if (this.member instanceof Method) {
        Method method = (Method)this.member;
        try {
          ReflectionUtils.makeAccessible(method);
          Object value = method.invoke(target, new Object[0]);
          return new TypedValue(value, this.typeDescriptor.narrow(value));
        }
        catch (Exception ex) {
          throw new AccessException("Unable to access property '" + name + "' through getter method", ex);
        } 
      } 
      
      Field field = (Field)this.member;
      try {
        ReflectionUtils.makeAccessible(field);
        Object value = field.get(target);
        return new TypedValue(value, this.typeDescriptor.narrow(value));
      }
      catch (Exception ex) {
        throw new AccessException("Unable to access field '" + name + "'", ex);
      } 
    }


    
    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
      throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
    }

    
    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) {
      throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
    }

    
    public boolean isCompilable() {
      return (Modifier.isPublic(this.member.getModifiers()) && 
        Modifier.isPublic(this.member.getDeclaringClass().getModifiers()));
    }

    
    public Class<?> getPropertyType() {
      if (this.member instanceof Method) {
        return ((Method)this.member).getReturnType();
      }
      
      return ((Field)this.member).getType();
    }


    
    public void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf) {
      boolean isStatic = Modifier.isStatic(this.member.getModifiers());
      String descriptor = cf.lastDescriptor();
      String classDesc = this.member.getDeclaringClass().getName().replace('.', '/');
      
      if (!isStatic) {
        if (descriptor == null) {
          cf.loadTarget(mv);
        }
        if (descriptor == null || !classDesc.equals(descriptor.substring(1))) {
          mv.visitTypeInsn(192, classDesc);
        
        }
      }
      else if (descriptor != null) {

        
        mv.visitInsn(87);
      } 

      
      if (this.member instanceof Method) {
        Method method = (Method)this.member;
        boolean isInterface = method.getDeclaringClass().isInterface();
        int opcode = isStatic ? 184 : (isInterface ? 185 : 182);
        mv.visitMethodInsn(opcode, classDesc, method.getName(), 
            CodeFlow.createSignatureDescriptor(method), isInterface);
      } else {
        
        mv.visitFieldInsn(isStatic ? 178 : 180, classDesc, this.member.getName(), 
            CodeFlow.toJvmDescriptor(((Field)this.member).getType()));
      } 
    }
  }
}
