package org.springframework.expression.spel.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodFilter;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;































public class ReflectiveMethodResolver
  implements MethodResolver
{
  private final boolean useDistance;
  @Nullable
  private Map<Class<?>, MethodFilter> filters;
  
  public ReflectiveMethodResolver() {
    this.useDistance = true;
  }










  
  public ReflectiveMethodResolver(boolean useDistance) {
    this.useDistance = useDistance;
  }







  
  public void registerMethodFilter(Class<?> type, @Nullable MethodFilter filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    if (filter != null) {
      this.filters.put(type, filter);
    } else {
      
      this.filters.remove(type);
    } 
  }












  
  @Nullable
  public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException {
    try {
      TypeConverter typeConverter = context.getTypeConverter();
      Class<?> type = (targetObject instanceof Class) ? (Class)targetObject : targetObject.getClass();
      ArrayList<Method> methods = new ArrayList<>(getMethods(type, targetObject));

      
      MethodFilter filter = (this.filters != null) ? this.filters.get(type) : null;
      if (filter != null) {
        List<Method> filtered = filter.filter(methods);
        methods = (filtered instanceof ArrayList) ? (ArrayList<Method>)filtered : new ArrayList<>(filtered);
      } 

      
      if (methods.size() > 1) {
        methods.sort((m1, m2) -> {
              int m1pl = m1.getParameterCount();
              
              int m2pl = m2.getParameterCount();
              
              return (m1pl == m2pl) ? ((!m1.isVarArgs() && m2.isVarArgs()) ? -1 : (

                
                (m1.isVarArgs() && !m2.isVarArgs()) ? 1 : 0)) : Integer.compare(m1pl, m2pl);
            });
      }








      
      for (int i = 0; i < methods.size(); i++) {
        methods.set(i, BridgeMethodResolver.findBridgedMethod(methods.get(i)));
      }

      
      Set<Method> methodsToIterate = new LinkedHashSet<>(methods);
      
      Method closeMatch = null;
      int closeMatchDistance = Integer.MAX_VALUE;
      Method matchRequiringConversion = null;
      boolean multipleOptions = false;
      
      for (Method method : methodsToIterate) {
        if (method.getName().equals(name)) {
          int paramCount = method.getParameterCount();
          List<TypeDescriptor> paramDescriptors = new ArrayList<>(paramCount);
          for (int j = 0; j < paramCount; j++) {
            paramDescriptors.add(new TypeDescriptor(new MethodParameter(method, j)));
          }
          ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
          if (method.isVarArgs() && argumentTypes.size() >= paramCount - 1) {
            
            matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
          }
          else if (paramCount == argumentTypes.size()) {
            
            matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
          } 
          if (matchInfo != null) {
            if (matchInfo.isExactMatch()) {
              return new ReflectiveMethodExecutor(method);
            }
            if (matchInfo.isCloseMatch()) {
              if (this.useDistance) {
                int matchDistance = ReflectionHelper.getTypeDifferenceWeight(paramDescriptors, argumentTypes);
                if (closeMatch == null || matchDistance < closeMatchDistance) {
                  
                  closeMatch = method;
                  closeMatchDistance = matchDistance;
                } 
                
                continue;
              } 
              if (closeMatch == null) {
                closeMatch = method;
              }
              continue;
            } 
            if (matchInfo.isMatchRequiringConversion()) {
              if (matchRequiringConversion != null) {
                multipleOptions = true;
              }
              matchRequiringConversion = method;
            } 
          } 
        } 
      } 
      if (closeMatch != null) {
        return new ReflectiveMethodExecutor(closeMatch);
      }
      if (matchRequiringConversion != null) {
        if (multipleOptions) {
          throw new SpelEvaluationException(SpelMessage.MULTIPLE_POSSIBLE_METHODS, new Object[] { name });
        }
        return new ReflectiveMethodExecutor(matchRequiringConversion);
      } 
      
      return null;
    
    }
    catch (EvaluationException ex) {
      throw new AccessException("Failed to resolve method", ex);
    } 
  }
  
  private Set<Method> getMethods(Class<?> type, Object targetObject) {
    if (targetObject instanceof Class) {
      Set<Method> set = new LinkedHashSet<>();
      
      Method[] arrayOfMethod = getMethods(type);
      for (Method method : arrayOfMethod) {
        if (Modifier.isStatic(method.getModifiers())) {
          set.add(method);
        }
      } 
      
      Collections.addAll(set, getMethods(Class.class));
      return set;
    } 
    if (Proxy.isProxyClass(type)) {
      Set<Method> set = new LinkedHashSet<>();
      
      for (Class<?> ifc : type.getInterfaces()) {
        Method[] arrayOfMethod = getMethods(ifc);
        for (Method method : arrayOfMethod) {
          if (isCandidateForInvocation(method, type)) {
            set.add(method);
          }
        } 
      } 
      return set;
    } 
    
    Set<Method> result = new LinkedHashSet<>();
    Method[] methods = getMethods(type);
    for (Method method : methods) {
      if (isCandidateForInvocation(method, type)) {
        result.add(method);
      }
    } 
    return result;
  }









  
  protected Method[] getMethods(Class<?> type) {
    return type.getMethods();
  }









  
  protected boolean isCandidateForInvocation(Method method, Class<?> targetClass) {
    return true;
  }
}
