package org.springframework.expression.spel.ast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.StringJoiner;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectionHelper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

































public class FunctionReference
  extends SpelNodeImpl
{
  private final String name;
  @Nullable
  private volatile Method method;
  
  public FunctionReference(String functionName, int startPos, int endPos, SpelNodeImpl... arguments) {
    super(startPos, endPos, arguments);
    this.name = functionName;
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    TypedValue value = state.lookupVariable(this.name);
    if (value == TypedValue.NULL) {
      throw new SpelEvaluationException(getStartPosition(), SpelMessage.FUNCTION_NOT_DEFINED, new Object[] { this.name });
    }
    if (!(value.getValue() instanceof Method))
    {
      throw new SpelEvaluationException(SpelMessage.FUNCTION_REFERENCE_CANNOT_BE_INVOKED, new Object[] { this.name, value
            .getClass() });
    }
    
    try {
      return executeFunctionJLRMethod(state, (Method)value.getValue());
    }
    catch (SpelEvaluationException ex) {
      ex.setPosition(getStartPosition());
      throw ex;
    } 
  }







  
  private TypedValue executeFunctionJLRMethod(ExpressionState state, Method method) throws EvaluationException {
    Object[] functionArgs = getArguments(state);
    
    if (!method.isVarArgs()) {
      int declaredParamCount = method.getParameterCount();
      if (declaredParamCount != functionArgs.length)
        throw new SpelEvaluationException(SpelMessage.INCORRECT_NUMBER_OF_ARGUMENTS_TO_FUNCTION, new Object[] {
              Integer.valueOf(functionArgs.length), Integer.valueOf(declaredParamCount)
            }); 
    } 
    if (!Modifier.isStatic(method.getModifiers())) {
      throw new SpelEvaluationException(getStartPosition(), SpelMessage.FUNCTION_MUST_BE_STATIC, new Object[] {
            ClassUtils.getQualifiedMethodName(method), this.name
          });
    }
    
    TypeConverter converter = state.getEvaluationContext().getTypeConverter();
    boolean argumentConversionOccurred = ReflectionHelper.convertAllArguments(converter, functionArgs, method);
    if (method.isVarArgs()) {
      functionArgs = ReflectionHelper.setupArgumentsForVarargsInvocation(method
          .getParameterTypes(), functionArgs);
    }
    boolean compilable = false;
    
    try {
      ReflectionUtils.makeAccessible(method);
      Object result = method.invoke(method.getClass(), functionArgs);
      compilable = !argumentConversionOccurred;
      return new TypedValue(result, (new TypeDescriptor(new MethodParameter(method, -1))).narrow(result));
    }
    catch (Exception ex) {
      throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_FUNCTION_CALL, new Object[] { this.name, ex
            .getMessage() });
    } finally {
      
      if (compilable) {
        this.exitTypeDescriptor = CodeFlow.toDescriptor(method.getReturnType());
        this.method = method;
      } else {
        
        this.exitTypeDescriptor = null;
        this.method = null;
      } 
    } 
  }

  
  public String toStringAST() {
    StringJoiner sj = new StringJoiner(",", "(", ")");
    for (int i = 0; i < getChildCount(); i++) {
      sj.add(getChild(i).toStringAST());
    }
    return '#' + this.name + sj.toString();
  }





  
  private Object[] getArguments(ExpressionState state) throws EvaluationException {
    Object[] arguments = new Object[getChildCount()];
    for (int i = 0; i < arguments.length; i++) {
      arguments[i] = this.children[i].getValueInternal(state).getValue();
    }
    return arguments;
  }

  
  public boolean isCompilable() {
    Method method = this.method;
    if (method == null) {
      return false;
    }
    int methodModifiers = method.getModifiers();
    if (!Modifier.isStatic(methodModifiers) || !Modifier.isPublic(methodModifiers) || 
      !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
      return false;
    }
    for (SpelNodeImpl child : this.children) {
      if (!child.isCompilable()) {
        return false;
      }
    } 
    return true;
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    Method method = this.method;
    Assert.state((method != null), "No method handle");
    String classDesc = method.getDeclaringClass().getName().replace('.', '/');
    generateCodeForArguments(mv, cf, method, this.children);
    mv.visitMethodInsn(184, classDesc, method.getName(), 
        CodeFlow.createSignatureDescriptor(method), false);
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
