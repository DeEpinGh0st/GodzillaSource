package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;












































public class Selection
  extends SpelNodeImpl
{
  public static final int ALL = 0;
  public static final int FIRST = 1;
  public static final int LAST = 2;
  private final int variant;
  private final boolean nullSafe;
  
  public Selection(boolean nullSafe, int variant, int startPos, int endPos, SpelNodeImpl expression) {
    super(startPos, endPos, new SpelNodeImpl[] { expression });
    this.nullSafe = nullSafe;
    this.variant = variant;
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    return getValueRef(state).getValue();
  }

  
  protected ValueRef getValueRef(ExpressionState state) throws EvaluationException {
    TypedValue op = state.getActiveContextObject();
    Object operand = op.getValue();
    SpelNodeImpl selectionCriteria = this.children[0];
    
    if (operand instanceof Map) {
      Map<?, ?> mapdata = (Map<?, ?>)operand;
      
      Map<Object, Object> result = new HashMap<>();
      Object lastKey = null;
      
      for (Map.Entry<?, ?> entry : mapdata.entrySet()) {
        try {
          TypedValue kvPair = new TypedValue(entry);
          state.pushActiveContextObject(kvPair);
          state.enterScope();
          Object val = selectionCriteria.getValueInternal(state).getValue();
          if (val instanceof Boolean) {
            if (((Boolean)val).booleanValue()) {
              if (this.variant == 1) {
                result.put(entry.getKey(), entry.getValue());
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
              } 
              result.put(entry.getKey(), entry.getValue());
              lastKey = entry.getKey();
            } 
          } else {
            
            throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN, new Object[0]);
          }
        
        } finally {
          
          state.popActiveContextObject();
          state.exitScope();
        } 
      } 
      
      if ((this.variant == 1 || this.variant == 2) && result.isEmpty()) {
        return new ValueRef.TypedValueHolderValueRef(new TypedValue(null), this);
      }
      
      if (this.variant == 2) {
        Map<Object, Object> resultMap = new HashMap<>();
        Object lastValue = result.get(lastKey);
        resultMap.put(lastKey, lastValue);
        return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultMap), this);
      } 
      
      return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
    } 
    
    if (operand instanceof Iterable || ObjectUtils.isArray(operand)) {
      
      Iterable<?> data = (operand instanceof Iterable) ? (Iterable)operand : Arrays.asList(ObjectUtils.toObjectArray(operand));
      
      List<Object> result = new ArrayList();
      int index = 0;
      for (Object element : data) {
        try {
          state.pushActiveContextObject(new TypedValue(element));
          state.enterScope("index", Integer.valueOf(index));
          Object val = selectionCriteria.getValueInternal(state).getValue();
          if (val instanceof Boolean) {
            if (((Boolean)val).booleanValue()) {
              if (this.variant == 1) {
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(element), this);
              }
              result.add(element);
            } 
          } else {
            
            throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN, new Object[0]);
          } 
          
          index++;
        } finally {
          
          state.exitScope();
          state.popActiveContextObject();
        } 
      } 
      
      if ((this.variant == 1 || this.variant == 2) && result.isEmpty()) {
        return ValueRef.NullValueRef.INSTANCE;
      }
      
      if (this.variant == 2) {
        return new ValueRef.TypedValueHolderValueRef(new TypedValue(CollectionUtils.lastElement(result)), this);
      }
      
      if (operand instanceof Iterable) {
        return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
      }
      
      Class<?> elementType = null;
      TypeDescriptor typeDesc = op.getTypeDescriptor();
      if (typeDesc != null) {
        TypeDescriptor elementTypeDesc = typeDesc.getElementTypeDescriptor();
        if (elementTypeDesc != null) {
          elementType = ClassUtils.resolvePrimitiveIfNecessary(elementTypeDesc.getType());
        }
      } 
      Assert.state((elementType != null), "Unresolvable element type");
      
      Object resultArray = Array.newInstance(elementType, result.size());
      System.arraycopy(result.toArray(), 0, resultArray, 0, result.size());
      return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultArray), this);
    } 
    
    if (operand == null) {
      if (this.nullSafe) {
        return ValueRef.NullValueRef.INSTANCE;
      }
      throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, new Object[] { "null" });
    } 
    
    throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, new Object[] { operand
          .getClass().getName() });
  }

  
  public String toStringAST() {
    return prefix() + getChild(0).toStringAST() + "]";
  }
  
  private String prefix() {
    switch (this.variant) { case 0:
        return "?[";
      case 1: return "^[";
      case 2: return "$["; }
    
    return "";
  }
}
