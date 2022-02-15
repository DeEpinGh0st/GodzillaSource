package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.support.ReflectiveConstructorExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;








































public class ConstructorReference
  extends SpelNodeImpl
{
  private final boolean isArrayConstructor;
  @Nullable
  private SpelNodeImpl[] dimensions;
  @Nullable
  private volatile ConstructorExecutor cachedExecutor;
  
  public ConstructorReference(int startPos, int endPos, SpelNodeImpl... arguments) {
    super(startPos, endPos, arguments);
    this.isArrayConstructor = false;
  }




  
  public ConstructorReference(int startPos, int endPos, SpelNodeImpl[] dimensions, SpelNodeImpl... arguments) {
    super(startPos, endPos, arguments);
    this.isArrayConstructor = true;
    this.dimensions = dimensions;
  }





  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    if (this.isArrayConstructor) {
      return createArray(state);
    }
    
    return createNewInstance(state);
  }







  
  private TypedValue createNewInstance(ExpressionState state) throws EvaluationException {
    Object[] arguments = new Object[getChildCount() - 1];
    List<TypeDescriptor> argumentTypes = new ArrayList<>(getChildCount() - 1);
    for (int i = 0; i < arguments.length; i++) {
      TypedValue childValue = this.children[i + 1].getValueInternal(state);
      Object value = childValue.getValue();
      arguments[i] = value;
      argumentTypes.add(TypeDescriptor.forObject(value));
    } 
    
    ConstructorExecutor executorToUse = this.cachedExecutor;
    if (executorToUse != null) {
      try {
        return executorToUse.execute(state.getEvaluationContext(), arguments);
      }
      catch (AccessException ex) {









        
        if (ex.getCause() instanceof java.lang.reflect.InvocationTargetException) {
          
          Throwable rootCause = ex.getCause().getCause();
          if (rootCause instanceof RuntimeException) {
            throw (RuntimeException)rootCause;
          }
          
          String str = (String)this.children[0].getValueInternal(state).getValue();
          throw new SpelEvaluationException(getStartPosition(), rootCause, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, new Object[] { str, 
                
                FormatHelper.formatMethodForMessage("", argumentTypes) });
        } 


        
        this.cachedExecutor = null;
      } 
    }

    
    String typeName = (String)this.children[0].getValueInternal(state).getValue();
    Assert.state((typeName != null), "No type name");
    executorToUse = findExecutorForConstructor(typeName, argumentTypes, state);
    try {
      this.cachedExecutor = executorToUse;
      if (executorToUse instanceof ReflectiveConstructorExecutor) {
        this.exitTypeDescriptor = CodeFlow.toDescriptor(((ReflectiveConstructorExecutor)executorToUse)
            .getConstructor().getDeclaringClass());
      }
      
      return executorToUse.execute(state.getEvaluationContext(), arguments);
    }
    catch (AccessException ex) {
      throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, new Object[] { typeName, 
            
            FormatHelper.formatMethodForMessage("", argumentTypes) });
    } 
  }











  
  private ConstructorExecutor findExecutorForConstructor(String typeName, List<TypeDescriptor> argumentTypes, ExpressionState state) throws SpelEvaluationException {
    EvaluationContext evalContext = state.getEvaluationContext();
    List<ConstructorResolver> ctorResolvers = evalContext.getConstructorResolvers();
    for (ConstructorResolver ctorResolver : ctorResolvers) {
      try {
        ConstructorExecutor ce = ctorResolver.resolve(state.getEvaluationContext(), typeName, argumentTypes);
        if (ce != null) {
          return ce;
        }
      }
      catch (AccessException ex) {
        throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, new Object[] { typeName, 
              
              FormatHelper.formatMethodForMessage("", argumentTypes) });
      } 
    } 
    throw new SpelEvaluationException(getStartPosition(), SpelMessage.CONSTRUCTOR_NOT_FOUND, new Object[] { typeName, 
          FormatHelper.formatMethodForMessage("", argumentTypes) });
  }

  
  public String toStringAST() {
    StringBuilder sb = new StringBuilder("new ");
    int index = 0;
    sb.append(getChild(index++).toStringAST());
    sb.append('(');
    for (int i = index; i < getChildCount(); i++) {
      if (i > index) {
        sb.append(',');
      }
      sb.append(getChild(i).toStringAST());
    } 
    sb.append(')');
    return sb.toString();
  }






  
  private TypedValue createArray(ExpressionState state) throws EvaluationException {
    Class<?> componentType;
    Object intendedArrayType = getChild(0).getValue(state);
    if (!(intendedArrayType instanceof String)) {
      throw new SpelEvaluationException(getChild(0).getStartPosition(), SpelMessage.TYPE_NAME_EXPECTED_FOR_ARRAY_CONSTRUCTION, new Object[] {
            
            FormatHelper.formatClassNameForMessage((intendedArrayType != null) ? intendedArrayType
              .getClass() : null)
          });
    }
    String type = (String)intendedArrayType;
    
    TypeCode arrayTypeCode = TypeCode.forName(type);
    if (arrayTypeCode == TypeCode.OBJECT) {
      componentType = state.findType(type);
    } else {
      
      componentType = arrayTypeCode.getType();
    } 
    
    Object newArray = null;
    if (!hasInitializer()) {
      
      if (this.dimensions != null) {
        for (SpelNodeImpl dimension : this.dimensions) {
          if (dimension == null) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.MISSING_ARRAY_DIMENSION, new Object[0]);
          }
        } 
        TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
        if (this.dimensions.length == 1)
        {
          TypedValue o = this.dimensions[0].getTypedValue(state);
          int arraySize = ExpressionUtils.toInt(typeConverter, o);
          newArray = Array.newInstance(componentType, arraySize);
        }
        else
        {
          int[] dims = new int[this.dimensions.length];
          for (int d = 0; d < this.dimensions.length; d++) {
            TypedValue o = this.dimensions[d].getTypedValue(state);
            dims[d] = ExpressionUtils.toInt(typeConverter, o);
          } 
          newArray = Array.newInstance(componentType, dims);
        }
      
      } 
    } else {
      
      if (this.dimensions == null || this.dimensions.length > 1)
      {
        
        throw new SpelEvaluationException(getStartPosition(), SpelMessage.MULTIDIM_ARRAY_INITIALIZER_NOT_SUPPORTED, new Object[0]);
      }
      
      TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
      InlineList initializer = (InlineList)getChild(1);
      
      if (this.dimensions[0] != null) {
        TypedValue dValue = this.dimensions[0].getTypedValue(state);
        int i = ExpressionUtils.toInt(typeConverter, dValue);
        if (i != initializer.getChildCount()) {
          throw new SpelEvaluationException(getStartPosition(), SpelMessage.INITIALIZER_LENGTH_INCORRECT, new Object[0]);
        }
      } 
      
      int arraySize = initializer.getChildCount();
      newArray = Array.newInstance(componentType, arraySize);
      if (arrayTypeCode == TypeCode.OBJECT) {
        populateReferenceTypeArray(state, newArray, typeConverter, initializer, componentType);
      }
      else if (arrayTypeCode == TypeCode.BOOLEAN) {
        populateBooleanArray(state, newArray, typeConverter, initializer);
      }
      else if (arrayTypeCode == TypeCode.BYTE) {
        populateByteArray(state, newArray, typeConverter, initializer);
      }
      else if (arrayTypeCode == TypeCode.CHAR) {
        populateCharArray(state, newArray, typeConverter, initializer);
      }
      else if (arrayTypeCode == TypeCode.DOUBLE) {
        populateDoubleArray(state, newArray, typeConverter, initializer);
      }
      else if (arrayTypeCode == TypeCode.FLOAT) {
        populateFloatArray(state, newArray, typeConverter, initializer);
      }
      else if (arrayTypeCode == TypeCode.INT) {
        populateIntArray(state, newArray, typeConverter, initializer);
      }
      else if (arrayTypeCode == TypeCode.LONG) {
        populateLongArray(state, newArray, typeConverter, initializer);
      }
      else if (arrayTypeCode == TypeCode.SHORT) {
        populateShortArray(state, newArray, typeConverter, initializer);
      } else {
        
        throw new IllegalStateException(arrayTypeCode.name());
      } 
    } 
    return new TypedValue(newArray);
  }


  
  private void populateReferenceTypeArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer, Class<?> componentType) {
    TypeDescriptor toTypeDescriptor = TypeDescriptor.valueOf(componentType);
    Object[] newObjectArray = (Object[])newArray;
    for (int i = 0; i < newObjectArray.length; i++) {
      SpelNode elementNode = initializer.getChild(i);
      Object arrayEntry = elementNode.getValue(state);
      newObjectArray[i] = typeConverter.convertValue(arrayEntry, 
          TypeDescriptor.forObject(arrayEntry), toTypeDescriptor);
    } 
  }


  
  private void populateByteArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    byte[] newByteArray = (byte[])newArray;
    for (int i = 0; i < newByteArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newByteArray[i] = ExpressionUtils.toByte(typeConverter, typedValue);
    } 
  }


  
  private void populateFloatArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    float[] newFloatArray = (float[])newArray;
    for (int i = 0; i < newFloatArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newFloatArray[i] = ExpressionUtils.toFloat(typeConverter, typedValue);
    } 
  }


  
  private void populateDoubleArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    double[] newDoubleArray = (double[])newArray;
    for (int i = 0; i < newDoubleArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newDoubleArray[i] = ExpressionUtils.toDouble(typeConverter, typedValue);
    } 
  }


  
  private void populateShortArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    short[] newShortArray = (short[])newArray;
    for (int i = 0; i < newShortArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newShortArray[i] = ExpressionUtils.toShort(typeConverter, typedValue);
    } 
  }


  
  private void populateLongArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    long[] newLongArray = (long[])newArray;
    for (int i = 0; i < newLongArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newLongArray[i] = ExpressionUtils.toLong(typeConverter, typedValue);
    } 
  }


  
  private void populateCharArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    char[] newCharArray = (char[])newArray;
    for (int i = 0; i < newCharArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newCharArray[i] = ExpressionUtils.toChar(typeConverter, typedValue);
    } 
  }


  
  private void populateBooleanArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    boolean[] newBooleanArray = (boolean[])newArray;
    for (int i = 0; i < newBooleanArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newBooleanArray[i] = ExpressionUtils.toBoolean(typeConverter, typedValue);
    } 
  }


  
  private void populateIntArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
    int[] newIntArray = (int[])newArray;
    for (int i = 0; i < newIntArray.length; i++) {
      TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
      newIntArray[i] = ExpressionUtils.toInt(typeConverter, typedValue);
    } 
  }
  
  private boolean hasInitializer() {
    return (getChildCount() > 1);
  }

  
  public boolean isCompilable() {
    if (!(this.cachedExecutor instanceof ReflectiveConstructorExecutor) || this.exitTypeDescriptor == null)
    {
      return false;
    }
    
    if (getChildCount() > 1) {
      for (int c = 1, max = getChildCount(); c < max; c++) {
        if (!this.children[c].isCompilable()) {
          return false;
        }
      } 
    }
    
    ReflectiveConstructorExecutor executor = (ReflectiveConstructorExecutor)this.cachedExecutor;
    if (executor == null) {
      return false;
    }
    Constructor<?> constructor = executor.getConstructor();
    return (Modifier.isPublic(constructor.getModifiers()) && 
      Modifier.isPublic(constructor.getDeclaringClass().getModifiers()));
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    ReflectiveConstructorExecutor executor = (ReflectiveConstructorExecutor)this.cachedExecutor;
    Assert.state((executor != null), "No cached executor");
    
    Constructor<?> constructor = executor.getConstructor();
    String classDesc = constructor.getDeclaringClass().getName().replace('.', '/');
    mv.visitTypeInsn(187, classDesc);
    mv.visitInsn(89);

    
    SpelNodeImpl[] arguments = new SpelNodeImpl[this.children.length - 1];
    System.arraycopy(this.children, 1, arguments, 0, this.children.length - 1);
    generateCodeForArguments(mv, cf, constructor, arguments);
    mv.visitMethodInsn(183, classDesc, "<init>", CodeFlow.createSignatureDescriptor(constructor), false);
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
