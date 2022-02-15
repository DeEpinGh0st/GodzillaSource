package org.springframework.core.convert.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;















































final class ObjectToObjectConverter
  implements ConditionalGenericConverter
{
  private static final Map<Class<?>, Member> conversionMemberCache = (Map<Class<?>, Member>)new ConcurrentReferenceHashMap(32);



  
  public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
    return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
  }

  
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    return (sourceType.getType() != targetType.getType() && 
      hasConversionMethodOrConstructor(targetType.getType(), sourceType.getType()));
  }

  
  @Nullable
  public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source == null) {
      return null;
    }
    Class<?> sourceClass = sourceType.getType();
    Class<?> targetClass = targetType.getType();
    Member member = getValidatedMember(targetClass, sourceClass);
    
    try {
      if (member instanceof Method) {
        Method method = (Method)member;
        ReflectionUtils.makeAccessible(method);
        if (!Modifier.isStatic(method.getModifiers())) {
          return method.invoke(source, new Object[0]);
        }
        
        return method.invoke(null, new Object[] { source });
      } 
      
      if (member instanceof Constructor) {
        Constructor<?> ctor = (Constructor)member;
        ReflectionUtils.makeAccessible(ctor);
        return ctor.newInstance(new Object[] { source });
      }
    
    } catch (InvocationTargetException ex) {
      throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
    }
    catch (Throwable ex) {
      throw new ConversionFailedException(sourceType, targetType, source, ex);
    } 



    
    throw new IllegalStateException(String.format("No to%3$s() method exists on %1$s, and no static valueOf/of/from(%1$s) method or %3$s(%1$s) constructor exists on %2$s.", new Object[] { sourceClass
            
            .getName(), targetClass.getName(), targetClass.getSimpleName() }));
  }


  
  static boolean hasConversionMethodOrConstructor(Class<?> targetClass, Class<?> sourceClass) {
    return (getValidatedMember(targetClass, sourceClass) != null);
  }
  
  @Nullable
  private static Member getValidatedMember(Class<?> targetClass, Class<?> sourceClass) {
    Member<?> member = conversionMemberCache.get(targetClass);
    if (isApplicable(member, sourceClass)) {
      return member;
    }
    
    member = determineToMethod(targetClass, sourceClass);
    if (member == null) {
      member = determineFactoryMethod(targetClass, sourceClass);
      if (member == null) {
        member = determineFactoryConstructor(targetClass, sourceClass);
        if (member == null) {
          return null;
        }
      } 
    } 
    
    conversionMemberCache.put(targetClass, member);
    return member;
  }
  
  private static boolean isApplicable(Member member, Class<?> sourceClass) {
    if (member instanceof Method) {
      Method method = (Method)member;
      return !Modifier.isStatic(method.getModifiers()) ? 
        ClassUtils.isAssignable(method.getDeclaringClass(), sourceClass) : (
        (method.getParameterTypes()[0] == sourceClass));
    } 
    if (member instanceof Constructor) {
      Constructor<?> ctor = (Constructor)member;
      return (ctor.getParameterTypes()[0] == sourceClass);
    } 
    
    return false;
  }

  
  @Nullable
  private static Method determineToMethod(Class<?> targetClass, Class<?> sourceClass) {
    if (String.class == targetClass || String.class == sourceClass)
    {
      return null;
    }
    
    Method method = ClassUtils.getMethodIfAvailable(sourceClass, "to" + targetClass.getSimpleName(), new Class[0]);
    return (method != null && !Modifier.isStatic(method.getModifiers()) && 
      ClassUtils.isAssignable(targetClass, method.getReturnType())) ? method : null;
  }
  
  @Nullable
  private static Method determineFactoryMethod(Class<?> targetClass, Class<?> sourceClass) {
    if (String.class == targetClass)
    {
      return null;
    }
    
    Method method = ClassUtils.getStaticMethod(targetClass, "valueOf", new Class[] { sourceClass });
    if (method == null) {
      method = ClassUtils.getStaticMethod(targetClass, "of", new Class[] { sourceClass });
      if (method == null) {
        method = ClassUtils.getStaticMethod(targetClass, "from", new Class[] { sourceClass });
      }
    } 
    return method;
  }
  
  @Nullable
  private static Constructor<?> determineFactoryConstructor(Class<?> targetClass, Class<?> sourceClass) {
    return ClassUtils.getConstructorIfAvailable(targetClass, new Class[] { sourceClass });
  }
}
