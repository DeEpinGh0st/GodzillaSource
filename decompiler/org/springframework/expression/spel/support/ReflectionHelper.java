package org.springframework.expression.spel.support;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;







































public abstract class ReflectionHelper
{
  @Nullable
  static ArgumentsMatchInfo compareArguments(List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {
    Assert.isTrue((expectedArgTypes.size() == suppliedArgTypes.size()), "Expected argument types and supplied argument types should be arrays of same length");

    
    ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;
    for (int i = 0; i < expectedArgTypes.size() && match != null; i++) {
      TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
      TypeDescriptor expectedArg = expectedArgTypes.get(i);
      
      if (suppliedArg == null) {
        if (expectedArg.isPrimitive()) {
          match = null;
        }
      }
      else if (!expectedArg.equals(suppliedArg)) {
        if (suppliedArg.isAssignableTo(expectedArg)) {
          if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
            match = ArgumentsMatchKind.CLOSE;
          }
        }
        else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
          match = ArgumentsMatchKind.REQUIRES_CONVERSION;
        } else {
          
          match = null;
        } 
      } 
    } 
    return (match != null) ? new ArgumentsMatchInfo(match) : null;
  }



  
  public static int getTypeDifferenceWeight(List<TypeDescriptor> paramTypes, List<TypeDescriptor> argTypes) {
    int result = 0;
    for (int i = 0; i < paramTypes.size(); i++) {
      TypeDescriptor paramType = paramTypes.get(i);
      TypeDescriptor argType = (i < argTypes.size()) ? argTypes.get(i) : null;
      if (argType == null) {
        if (paramType.isPrimitive()) {
          return Integer.MAX_VALUE;
        }
      } else {
        
        Class<?> paramTypeClazz = paramType.getType();
        if (!ClassUtils.isAssignable(paramTypeClazz, argType.getType())) {
          return Integer.MAX_VALUE;
        }
        if (paramTypeClazz.isPrimitive()) {
          paramTypeClazz = Object.class;
        }
        Class<?> superClass = argType.getType().getSuperclass();
        while (superClass != null) {
          if (paramTypeClazz.equals(superClass)) {
            result += 2;
            superClass = null; continue;
          } 
          if (ClassUtils.isAssignable(paramTypeClazz, superClass)) {
            result += 2;
            superClass = superClass.getSuperclass();
            continue;
          } 
          superClass = null;
        } 
        
        if (paramTypeClazz.isInterface()) {
          result++;
        }
      } 
    } 
    return result;
  }













  
  @Nullable
  static ArgumentsMatchInfo compareArgumentsVarargs(List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {
    Assert.isTrue(!CollectionUtils.isEmpty(expectedArgTypes), "Expected arguments must at least include one array (the varargs parameter)");
    
    Assert.isTrue(((TypeDescriptor)expectedArgTypes.get(expectedArgTypes.size() - 1)).isArray(), "Final expected argument should be array type (the varargs parameter)");

    
    ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;



    
    int argCountUpToVarargs = expectedArgTypes.size() - 1;
    for (int i = 0; i < argCountUpToVarargs && match != null; i++) {
      TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
      TypeDescriptor expectedArg = expectedArgTypes.get(i);
      if (suppliedArg == null) {
        if (expectedArg.isPrimitive()) {
          match = null;
        
        }
      }
      else if (!expectedArg.equals(suppliedArg)) {
        if (suppliedArg.isAssignableTo(expectedArg)) {
          if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
            match = ArgumentsMatchKind.CLOSE;
          }
        }
        else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
          match = ArgumentsMatchKind.REQUIRES_CONVERSION;
        } else {
          
          match = null;
        } 
      } 
    } 


    
    if (match == null) {
      return null;
    }
    
    if (suppliedArgTypes.size() != expectedArgTypes.size() || 
      !((TypeDescriptor)expectedArgTypes.get(expectedArgTypes.size() - 1)).equals(suppliedArgTypes
        .get(suppliedArgTypes.size() - 1))) {





      
      TypeDescriptor varargsDesc = expectedArgTypes.get(expectedArgTypes.size() - 1);
      TypeDescriptor elementDesc = varargsDesc.getElementTypeDescriptor();
      Assert.state((elementDesc != null), "No element type");
      Class<?> varargsParamType = elementDesc.getType();

      
      for (int j = expectedArgTypes.size() - 1; j < suppliedArgTypes.size(); j++) {
        TypeDescriptor suppliedArg = suppliedArgTypes.get(j);
        if (suppliedArg == null) {
          if (varargsParamType.isPrimitive()) {
            match = null;
          
          }
        }
        else if (varargsParamType != suppliedArg.getType()) {
          if (ClassUtils.isAssignable(varargsParamType, suppliedArg.getType())) {
            if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
              match = ArgumentsMatchKind.CLOSE;
            }
          }
          else if (typeConverter.canConvert(suppliedArg, TypeDescriptor.valueOf(varargsParamType))) {
            match = ArgumentsMatchKind.REQUIRES_CONVERSION;
          } else {
            
            match = null;
          } 
        } 
      } 
    } 

    
    return (match != null) ? new ArgumentsMatchInfo(match) : null;
  }

















  
  public static boolean convertAllArguments(TypeConverter converter, Object[] arguments, Method method) throws SpelEvaluationException {
    Integer varargsPosition = method.isVarArgs() ? Integer.valueOf(method.getParameterCount() - 1) : null;
    return convertArguments(converter, arguments, method, varargsPosition);
  }












  
  static boolean convertArguments(TypeConverter converter, Object[] arguments, Executable executable, @Nullable Integer varargsPosition) throws EvaluationException {
    int i;
    boolean conversionOccurred = false;
    if (varargsPosition == null) {
      for (int j = 0; j < arguments.length; j++) {
        TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forExecutable(executable, j));
        Object argument = arguments[j];
        arguments[j] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
        i = conversionOccurred | ((argument != arguments[j]) ? 1 : 0);
      }
    
    } else {
      
      for (int j = 0; j < varargsPosition.intValue(); j++) {
        TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forExecutable(executable, j));
        Object argument = arguments[j];
        arguments[j] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
        i |= (argument != arguments[j]) ? 1 : 0;
      } 
      MethodParameter methodParam = MethodParameter.forExecutable(executable, varargsPosition.intValue());
      if (varargsPosition.intValue() == arguments.length - 1) {

        
        TypeDescriptor targetType = new TypeDescriptor(methodParam);
        Object argument = arguments[varargsPosition.intValue()];
        TypeDescriptor sourceType = TypeDescriptor.forObject(argument);
        arguments[varargsPosition.intValue()] = converter.convertValue(argument, sourceType, targetType);



        
        if (argument != arguments[varargsPosition.intValue()] && 
          !isFirstEntryInArray(argument, arguments[varargsPosition.intValue()])) {
          i = 1;
        }
      }
      else {
        
        TypeDescriptor targetType = (new TypeDescriptor(methodParam)).getElementTypeDescriptor();
        Assert.state((targetType != null), "No element type");
        for (int k = varargsPosition.intValue(); k < arguments.length; k++) {
          Object argument = arguments[k];
          arguments[k] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
          i |= (argument != arguments[k]) ? 1 : 0;
        } 
      } 
    } 
    return i;
  }






  
  private static boolean isFirstEntryInArray(Object value, @Nullable Object possibleArray) {
    if (possibleArray == null) {
      return false;
    }
    Class<?> type = possibleArray.getClass();
    if (!type.isArray() || Array.getLength(possibleArray) == 0 || 
      !ClassUtils.isAssignableValue(type.getComponentType(), value)) {
      return false;
    }
    Object arrayValue = Array.get(possibleArray, 0);
    return type.getComponentType().isPrimitive() ? arrayValue.equals(value) : ((arrayValue == value));
  }










  
  public static Object[] setupArgumentsForVarargsInvocation(Class<?>[] requiredParameterTypes, Object... args) {
    int parameterCount = requiredParameterTypes.length;
    int argumentCount = args.length;

    
    if (parameterCount != args.length || requiredParameterTypes[parameterCount - 1] != ((args[argumentCount - 1] != null) ? args[argumentCount - 1]
      
      .getClass() : null)) {
      
      int arraySize = 0;
      if (argumentCount >= parameterCount) {
        arraySize = argumentCount - parameterCount - 1;
      }

      
      Object[] newArgs = new Object[parameterCount];
      System.arraycopy(args, 0, newArgs, 0, newArgs.length - 1);


      
      Class<?> componentType = requiredParameterTypes[parameterCount - 1].getComponentType();
      Object repackagedArgs = Array.newInstance(componentType, arraySize);
      for (int i = 0; i < arraySize; i++) {
        Array.set(repackagedArgs, i, args[parameterCount - 1 + i]);
      }
      newArgs[newArgs.length - 1] = repackagedArgs;
      return newArgs;
    } 
    return args;
  }





  
  enum ArgumentsMatchKind
  {
    EXACT,

    
    CLOSE,

    
    REQUIRES_CONVERSION;
  }




  
  static class ArgumentsMatchInfo
  {
    private final ReflectionHelper.ArgumentsMatchKind kind;




    
    ArgumentsMatchInfo(ReflectionHelper.ArgumentsMatchKind kind) {
      this.kind = kind;
    }
    
    public boolean isExactMatch() {
      return (this.kind == ReflectionHelper.ArgumentsMatchKind.EXACT);
    }
    
    public boolean isCloseMatch() {
      return (this.kind == ReflectionHelper.ArgumentsMatchKind.CLOSE);
    }
    
    public boolean isMatchRequiringConversion() {
      return (this.kind == ReflectionHelper.ArgumentsMatchKind.REQUIRES_CONVERSION);
    }

    
    public String toString() {
      return "ArgumentMatchInfo: " + this.kind;
    }
  }
}
