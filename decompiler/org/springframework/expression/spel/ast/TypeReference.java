package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

























public class TypeReference
  extends SpelNodeImpl
{
  private final int dimensions;
  @Nullable
  private transient Class<?> type;
  
  public TypeReference(int startPos, int endPos, SpelNodeImpl qualifiedId) {
    this(startPos, endPos, qualifiedId, 0);
  }
  
  public TypeReference(int startPos, int endPos, SpelNodeImpl qualifiedId, int dims) {
    super(startPos, endPos, new SpelNodeImpl[] { qualifiedId });
    this.dimensions = dims;
  }



  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    String typeName = (String)this.children[0].getValueInternal(state).getValue();
    Assert.state((typeName != null), "No type name");
    if (!typeName.contains(".") && Character.isLowerCase(typeName.charAt(0))) {
      TypeCode tc = TypeCode.valueOf(typeName.toUpperCase());
      if (tc != TypeCode.OBJECT) {
        
        Class<?> clazz1 = makeArrayIfNecessary(tc.getType());
        this.exitTypeDescriptor = "Ljava/lang/Class";
        this.type = clazz1;
        return new TypedValue(clazz1);
      } 
    } 
    Class<?> clazz = state.findType(typeName);
    clazz = makeArrayIfNecessary(clazz);
    this.exitTypeDescriptor = "Ljava/lang/Class";
    this.type = clazz;
    return new TypedValue(clazz);
  }
  
  private Class<?> makeArrayIfNecessary(Class<?> clazz) {
    if (this.dimensions != 0) {
      for (int i = 0; i < this.dimensions; i++) {
        Object array = Array.newInstance(clazz, 0);
        clazz = array.getClass();
      } 
    }
    return clazz;
  }

  
  public String toStringAST() {
    StringBuilder sb = new StringBuilder("T(");
    sb.append(getChild(0).toStringAST());
    for (int d = 0; d < this.dimensions; d++) {
      sb.append("[]");
    }
    sb.append(')');
    return sb.toString();
  }

  
  public boolean isCompilable() {
    return (this.exitTypeDescriptor != null);
  }


  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    Assert.state((this.type != null), "No type available");
    if (this.type.isPrimitive()) {
      if (this.type == boolean.class) {
        mv.visitFieldInsn(178, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
      }
      else if (this.type == byte.class) {
        mv.visitFieldInsn(178, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
      }
      else if (this.type == char.class) {
        mv.visitFieldInsn(178, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
      }
      else if (this.type == double.class) {
        mv.visitFieldInsn(178, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
      }
      else if (this.type == float.class) {
        mv.visitFieldInsn(178, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
      }
      else if (this.type == int.class) {
        mv.visitFieldInsn(178, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
      }
      else if (this.type == long.class) {
        mv.visitFieldInsn(178, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
      }
      else if (this.type == short.class) {
        mv.visitFieldInsn(178, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
      } 
    } else {
      
      mv.visitLdcInsn(Type.getType(this.type));
    } 
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
