package org.springframework.expression.spel.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelNode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

























public abstract class SpelNodeImpl
  implements SpelNode, Opcodes
{
  private static final SpelNodeImpl[] NO_CHILDREN = new SpelNodeImpl[0];

  
  private final int startPos;
  
  private final int endPos;
  
  protected SpelNodeImpl[] children = NO_CHILDREN;



  
  @Nullable
  private SpelNodeImpl parent;



  
  @Nullable
  protected volatile String exitTypeDescriptor;




  
  public SpelNodeImpl(int startPos, int endPos, SpelNodeImpl... operands) {
    this.startPos = startPos;
    this.endPos = endPos;
    if (!ObjectUtils.isEmpty((Object[])operands)) {
      this.children = operands;
      for (SpelNodeImpl operand : operands) {
        Assert.notNull(operand, "Operand must not be null");
        operand.parent = this;
      } 
    } 
  }




  
  protected boolean nextChildIs(Class<?>... classes) {
    if (this.parent != null) {
      SpelNodeImpl[] peers = this.parent.children;
      for (int i = 0, max = peers.length; i < max; i++) {
        if (this == peers[i]) {
          if (i + 1 >= max) {
            return false;
          }
          Class<?> peerClass = peers[i + 1].getClass();
          for (Class<?> desiredClass : classes) {
            if (peerClass == desiredClass) {
              return true;
            }
          } 
          return false;
        } 
      } 
    } 
    return false;
  }

  
  @Nullable
  public final Object getValue(ExpressionState expressionState) throws EvaluationException {
    return getValueInternal(expressionState).getValue();
  }

  
  public final TypedValue getTypedValue(ExpressionState expressionState) throws EvaluationException {
    return getValueInternal(expressionState);
  }


  
  public boolean isWritable(ExpressionState expressionState) throws EvaluationException {
    return false;
  }

  
  public void setValue(ExpressionState expressionState, @Nullable Object newValue) throws EvaluationException {
    throw new SpelEvaluationException(getStartPosition(), SpelMessage.SETVALUE_NOT_SUPPORTED, new Object[] { getClass() });
  }

  
  public SpelNode getChild(int index) {
    return this.children[index];
  }

  
  public int getChildCount() {
    return this.children.length;
  }

  
  @Nullable
  public Class<?> getObjectClass(@Nullable Object obj) {
    if (obj == null) {
      return null;
    }
    return (obj instanceof Class) ? (Class)obj : obj.getClass();
  }

  
  public int getStartPosition() {
    return this.startPos;
  }

  
  public int getEndPosition() {
    return this.endPos;
  }






  
  public boolean isCompilable() {
    return false;
  }







  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    throw new IllegalStateException(getClass().getName() + " has no generateCode(..) method");
  }
  
  @Nullable
  public String getExitDescriptor() {
    return this.exitTypeDescriptor;
  }
  
  @Nullable
  protected final <T> T getValue(ExpressionState state, Class<T> desiredReturnType) throws EvaluationException {
    return (T)ExpressionUtils.convertTypedValue(state.getEvaluationContext(), getValueInternal(state), desiredReturnType);
  }
  
  protected ValueRef getValueRef(ExpressionState state) throws EvaluationException {
    throw new SpelEvaluationException(getStartPosition(), SpelMessage.NOT_ASSIGNABLE, new Object[] { toStringAST() });
  }





  
  public abstract TypedValue getValueInternal(ExpressionState paramExpressionState) throws EvaluationException;





  
  protected static void generateCodeForArguments(MethodVisitor mv, CodeFlow cf, Member member, SpelNodeImpl[] arguments) {
    String[] paramDescriptors = null;
    boolean isVarargs = false;
    if (member instanceof Constructor) {
      Constructor<?> ctor = (Constructor)member;
      paramDescriptors = CodeFlow.toDescriptors(ctor.getParameterTypes());
      isVarargs = ctor.isVarArgs();
    } else {
      
      Method method = (Method)member;
      paramDescriptors = CodeFlow.toDescriptors(method.getParameterTypes());
      isVarargs = method.isVarArgs();
    } 
    if (isVarargs) {

      
      int p = 0;
      int childCount = arguments.length;

      
      for (p = 0; p < paramDescriptors.length - 1; p++) {
        generateCodeForArgument(mv, cf, arguments[p], paramDescriptors[p]);
      }
      
      SpelNodeImpl lastChild = (childCount == 0) ? null : arguments[childCount - 1];
      String arrayType = paramDescriptors[paramDescriptors.length - 1];

      
      if (lastChild != null && arrayType.equals(lastChild.getExitDescriptor())) {
        generateCodeForArgument(mv, cf, lastChild, paramDescriptors[p]);
      } else {
        
        arrayType = arrayType.substring(1);
        
        CodeFlow.insertNewArrayCode(mv, childCount - p, arrayType);
        
        int arrayindex = 0;
        while (p < childCount) {
          SpelNodeImpl child = arguments[p];
          mv.visitInsn(89);
          CodeFlow.insertOptimalLoad(mv, arrayindex++);
          generateCodeForArgument(mv, cf, child, arrayType);
          CodeFlow.insertArrayStore(mv, arrayType);
          p++;
        } 
      } 
    } else {
      
      for (int i = 0; i < paramDescriptors.length; i++) {
        generateCodeForArgument(mv, cf, arguments[i], paramDescriptors[i]);
      }
    } 
  }




  
  protected static void generateCodeForArgument(MethodVisitor mv, CodeFlow cf, SpelNodeImpl argument, String paramDesc) {
    cf.enterCompilationScope();
    argument.generateCode(mv, cf);
    String lastDesc = cf.lastDescriptor();
    Assert.state((lastDesc != null), "No last descriptor");
    boolean primitiveOnStack = CodeFlow.isPrimitive(lastDesc);
    
    if (primitiveOnStack && paramDesc.charAt(0) == 'L') {
      CodeFlow.insertBoxIfNecessary(mv, lastDesc.charAt(0));
    }
    else if (paramDesc.length() == 1 && !primitiveOnStack) {
      CodeFlow.insertUnboxInsns(mv, paramDesc.charAt(0), lastDesc);
    }
    else if (!paramDesc.equals(lastDesc)) {
      
      CodeFlow.insertCheckCast(mv, paramDesc);
    } 
    cf.exitCompilationScope();
  }
}
