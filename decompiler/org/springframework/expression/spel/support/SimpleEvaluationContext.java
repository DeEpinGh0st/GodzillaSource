package org.springframework.expression.spel.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;

























































public final class SimpleEvaluationContext
  implements EvaluationContext
{
  private static final TypeLocator typeNotFoundTypeLocator;
  private final TypedValue rootObject;
  private final List<PropertyAccessor> propertyAccessors;
  private final List<MethodResolver> methodResolvers;
  private final TypeConverter typeConverter;
  
  static {
    typeNotFoundTypeLocator = (typeName -> {
        throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, new Object[] { typeName });
      });
  }








  
  private final TypeComparator typeComparator = new StandardTypeComparator();
  
  private final OperatorOverloader operatorOverloader = new StandardOperatorOverloader();
  
  private final Map<String, Object> variables = new HashMap<>();



  
  private SimpleEvaluationContext(List<PropertyAccessor> accessors, List<MethodResolver> resolvers, @Nullable TypeConverter converter, @Nullable TypedValue rootObject) {
    this.propertyAccessors = accessors;
    this.methodResolvers = resolvers;
    this.typeConverter = (converter != null) ? converter : new StandardTypeConverter();
    this.rootObject = (rootObject != null) ? rootObject : TypedValue.NULL;
  }





  
  public TypedValue getRootObject() {
    return this.rootObject;
  }





  
  public List<PropertyAccessor> getPropertyAccessors() {
    return this.propertyAccessors;
  }





  
  public List<ConstructorResolver> getConstructorResolvers() {
    return Collections.emptyList();
  }





  
  public List<MethodResolver> getMethodResolvers() {
    return this.methodResolvers;
  }





  
  @Nullable
  public BeanResolver getBeanResolver() {
    return null;
  }






  
  public TypeLocator getTypeLocator() {
    return typeNotFoundTypeLocator;
  }







  
  public TypeConverter getTypeConverter() {
    return this.typeConverter;
  }




  
  public TypeComparator getTypeComparator() {
    return this.typeComparator;
  }




  
  public OperatorOverloader getOperatorOverloader() {
    return this.operatorOverloader;
  }

  
  public void setVariable(String name, @Nullable Object value) {
    this.variables.put(name, value);
  }

  
  @Nullable
  public Object lookupVariable(String name) {
    return this.variables.get(name);
  }










  
  public static Builder forPropertyAccessors(PropertyAccessor... accessors) {
    for (PropertyAccessor accessor : accessors) {
      if (accessor.getClass() == ReflectivePropertyAccessor.class) {
        throw new IllegalArgumentException("SimpleEvaluationContext is not designed for use with a plain ReflectivePropertyAccessor. Consider using DataBindingPropertyAccessor or a custom subclass.");
      }
    } 
    
    return new Builder(accessors);
  }






  
  public static Builder forReadOnlyDataBinding() {
    return new Builder(new PropertyAccessor[] { DataBindingPropertyAccessor.forReadOnlyAccess() });
  }






  
  public static Builder forReadWriteDataBinding() {
    return new Builder(new PropertyAccessor[] { DataBindingPropertyAccessor.forReadWriteAccess() });
  }



  
  public static class Builder
  {
    private final List<PropertyAccessor> accessors;

    
    private List<MethodResolver> resolvers = Collections.emptyList();
    
    @Nullable
    private TypeConverter typeConverter;
    
    @Nullable
    private TypedValue rootObject;
    
    public Builder(PropertyAccessor... accessors) {
      this.accessors = Arrays.asList(accessors);
    }







    
    public Builder withMethodResolvers(MethodResolver... resolvers) {
      for (MethodResolver resolver : resolvers) {
        if (resolver.getClass() == ReflectiveMethodResolver.class) {
          throw new IllegalArgumentException("SimpleEvaluationContext is not designed for use with a plain ReflectiveMethodResolver. Consider using DataBindingMethodResolver or a custom subclass.");
        }
      } 
      
      this.resolvers = Arrays.asList(resolvers);
      return this;
    }








    
    public Builder withInstanceMethods() {
      this.resolvers = Collections.singletonList(DataBindingMethodResolver.forInstanceMethodInvocation());
      return this;
    }








    
    public Builder withConversionService(ConversionService conversionService) {
      this.typeConverter = new StandardTypeConverter(conversionService);
      return this;
    }






    
    public Builder withTypeConverter(TypeConverter converter) {
      this.typeConverter = converter;
      return this;
    }






    
    public Builder withRootObject(Object rootObject) {
      this.rootObject = new TypedValue(rootObject);
      return this;
    }






    
    public Builder withTypedRootObject(Object rootObject, TypeDescriptor typeDescriptor) {
      this.rootObject = new TypedValue(rootObject, typeDescriptor);
      return this;
    }
    
    public SimpleEvaluationContext build() {
      return new SimpleEvaluationContext(this.accessors, this.resolvers, this.typeConverter, this.rootObject);
    }
  }
}
