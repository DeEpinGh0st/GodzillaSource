package org.springframework.expression.spel.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;











public class Indexer
  extends SpelNodeImpl
{
  @Nullable
  private String cachedReadName;
  @Nullable
  private Class<?> cachedReadTargetType;
  @Nullable
  private PropertyAccessor cachedReadAccessor;
  @Nullable
  private String cachedWriteName;
  @Nullable
  private Class<?> cachedWriteTargetType;
  @Nullable
  private PropertyAccessor cachedWriteAccessor;
  @Nullable
  private IndexedType indexedType;
  
  private enum IndexedType
  {
    ARRAY, LIST, MAP, STRING, OBJECT;
  }
































  
  public Indexer(int startPos, int endPos, SpelNodeImpl expr) {
    super(startPos, endPos, new SpelNodeImpl[] { expr });
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    return getValueRef(state).getValue();
  }

  
  public void setValue(ExpressionState state, @Nullable Object newValue) throws EvaluationException {
    getValueRef(state).setValue(newValue);
  }

  
  public boolean isWritable(ExpressionState expressionState) throws SpelEvaluationException {
    return true;
  }
  
  protected ValueRef getValueRef(ExpressionState state) throws EvaluationException {
    TypedValue indexValue;
    Object index;
    TypedValue context = state.getActiveContextObject();
    Object target = context.getValue();
    TypeDescriptor targetDescriptor = context.getTypeDescriptor();



    
    if (target instanceof Map && this.children[0] instanceof PropertyOrFieldReference) {
      PropertyOrFieldReference reference = (PropertyOrFieldReference)this.children[0];
      index = reference.getName();
      indexValue = new TypedValue(index);
    } else {

      
      try {
        
        state.pushActiveContextObject(state.getRootContextObject());
        indexValue = this.children[0].getValueInternal(state);
        index = indexValue.getValue();
        Assert.state((index != null), "No index");
      } finally {
        
        state.popActiveContextObject();
      } 
    } 

    
    if (target == null) {
      throw new SpelEvaluationException(getStartPosition(), SpelMessage.CANNOT_INDEX_INTO_NULL_VALUE, new Object[0]);
    }
    
    Assert.state((targetDescriptor != null), "No type descriptor");

    
    if (target instanceof Map) {
      Object key = index;
      if (targetDescriptor.getMapKeyTypeDescriptor() != null) {
        key = state.convertValue(key, targetDescriptor.getMapKeyTypeDescriptor());
      }
      this.indexedType = IndexedType.MAP;
      return new MapIndexingValueRef(state.getTypeConverter(), (Map)target, key, targetDescriptor);
    } 


    
    if (target.getClass().isArray() || target instanceof Collection || target instanceof String) {
      int idx = ((Integer)state.convertValue(index, TypeDescriptor.valueOf(Integer.class))).intValue();
      if (target.getClass().isArray()) {
        this.indexedType = IndexedType.ARRAY;
        return new ArrayIndexingValueRef(state.getTypeConverter(), target, idx, targetDescriptor);
      } 
      if (target instanceof Collection) {
        if (target instanceof List) {
          this.indexedType = IndexedType.LIST;
        }
        return new CollectionIndexingValueRef((Collection)target, idx, targetDescriptor, state
            .getTypeConverter(), state.getConfiguration().isAutoGrowCollections(), state
            .getConfiguration().getMaximumAutoGrowSize());
      } 
      
      this.indexedType = IndexedType.STRING;
      return new StringIndexingLValue((String)target, idx, targetDescriptor);
    } 



    
    TypeDescriptor valueType = indexValue.getTypeDescriptor();
    if (valueType != null && String.class == valueType.getType()) {
      this.indexedType = IndexedType.OBJECT;
      return new PropertyIndexingValueRef(target, (String)index, state
          .getEvaluationContext(), targetDescriptor);
    } 
    
    throw new SpelEvaluationException(
        getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { targetDescriptor });
  }

  
  public boolean isCompilable() {
    if (this.indexedType == IndexedType.ARRAY) {
      return (this.exitTypeDescriptor != null);
    }
    if (this.indexedType == IndexedType.LIST) {
      return this.children[0].isCompilable();
    }
    if (this.indexedType == IndexedType.MAP) {
      return (this.children[0] instanceof PropertyOrFieldReference || this.children[0].isCompilable());
    }
    if (this.indexedType == IndexedType.OBJECT)
    {
      return (this.cachedReadAccessor != null && this.cachedReadAccessor instanceof ReflectivePropertyAccessor.OptimalPropertyAccessor && 
        
        getChild(0) instanceof StringLiteral);
    }
    return false;
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    String descriptor = cf.lastDescriptor();
    if (descriptor == null)
    {
      cf.loadTarget(mv);
    }
    
    if (this.indexedType == IndexedType.ARRAY) {
      int insn;
      if ("D".equals(this.exitTypeDescriptor)) {
        mv.visitTypeInsn(192, "[D");
        insn = 49;
      }
      else if ("F".equals(this.exitTypeDescriptor)) {
        mv.visitTypeInsn(192, "[F");
        insn = 48;
      }
      else if ("J".equals(this.exitTypeDescriptor)) {
        mv.visitTypeInsn(192, "[J");
        insn = 47;
      }
      else if ("I".equals(this.exitTypeDescriptor)) {
        mv.visitTypeInsn(192, "[I");
        insn = 46;
      }
      else if ("S".equals(this.exitTypeDescriptor)) {
        mv.visitTypeInsn(192, "[S");
        insn = 53;
      }
      else if ("B".equals(this.exitTypeDescriptor)) {
        mv.visitTypeInsn(192, "[B");
        insn = 51;
      }
      else if ("C".equals(this.exitTypeDescriptor)) {
        mv.visitTypeInsn(192, "[C");
        insn = 52;
      } else {
        
        mv.visitTypeInsn(192, "[" + this.exitTypeDescriptor + (
            CodeFlow.isPrimitiveArray(this.exitTypeDescriptor) ? "" : ";"));
        
        insn = 50;
      } 
      SpelNodeImpl index = this.children[0];
      cf.enterCompilationScope();
      index.generateCode(mv, cf);
      cf.exitCompilationScope();
      mv.visitInsn(insn);
    
    }
    else if (this.indexedType == IndexedType.LIST) {
      mv.visitTypeInsn(192, "java/util/List");
      cf.enterCompilationScope();
      this.children[0].generateCode(mv, cf);
      cf.exitCompilationScope();
      mv.visitMethodInsn(185, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
    
    }
    else if (this.indexedType == IndexedType.MAP) {
      mv.visitTypeInsn(192, "java/util/Map");

      
      if (this.children[0] instanceof PropertyOrFieldReference) {
        PropertyOrFieldReference reference = (PropertyOrFieldReference)this.children[0];
        String mapKeyName = reference.getName();
        mv.visitLdcInsn(mapKeyName);
      } else {
        
        cf.enterCompilationScope();
        this.children[0].generateCode(mv, cf);
        cf.exitCompilationScope();
      } 
      mv.visitMethodInsn(185, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);

    
    }
    else if (this.indexedType == IndexedType.OBJECT) {
      ReflectivePropertyAccessor.OptimalPropertyAccessor accessor = (ReflectivePropertyAccessor.OptimalPropertyAccessor)this.cachedReadAccessor;
      
      Assert.state((accessor != null), "No cached read accessor");
      Member member = accessor.member;
      boolean isStatic = Modifier.isStatic(member.getModifiers());
      String classDesc = member.getDeclaringClass().getName().replace('.', '/');
      
      if (!isStatic) {
        if (descriptor == null) {
          cf.loadTarget(mv);
        }
        if (descriptor == null || !classDesc.equals(descriptor.substring(1))) {
          mv.visitTypeInsn(192, classDesc);
        }
      } 
      
      if (member instanceof Method) {
        mv.visitMethodInsn(isStatic ? 184 : 182, classDesc, member.getName(), 
            CodeFlow.createSignatureDescriptor((Method)member), false);
      } else {
        
        mv.visitFieldInsn(isStatic ? 178 : 180, classDesc, member.getName(), 
            CodeFlow.toJvmDescriptor(((Field)member).getType()));
      } 
    } 
    
    cf.pushDescriptor(this.exitTypeDescriptor);
  }

  
  public String toStringAST() {
    StringJoiner sj = new StringJoiner(",", "[", "]");
    for (int i = 0; i < getChildCount(); i++) {
      sj.add(getChild(i).toStringAST());
    }
    return sj.toString();
  }



  
  private void setArrayElement(TypeConverter converter, Object ctx, int idx, @Nullable Object newValue, Class<?> arrayComponentType) throws EvaluationException {
    if (arrayComponentType == boolean.class) {
      boolean[] array = (boolean[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Boolean)convertValue(converter, newValue, (Class)Boolean.class)).booleanValue();
    }
    else if (arrayComponentType == byte.class) {
      byte[] array = (byte[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Byte)convertValue(converter, newValue, (Class)Byte.class)).byteValue();
    }
    else if (arrayComponentType == char.class) {
      char[] array = (char[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Character)convertValue(converter, newValue, (Class)Character.class)).charValue();
    }
    else if (arrayComponentType == double.class) {
      double[] array = (double[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Double)convertValue(converter, newValue, (Class)Double.class)).doubleValue();
    }
    else if (arrayComponentType == float.class) {
      float[] array = (float[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Float)convertValue(converter, newValue, (Class)Float.class)).floatValue();
    }
    else if (arrayComponentType == int.class) {
      int[] array = (int[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Integer)convertValue(converter, newValue, (Class)Integer.class)).intValue();
    }
    else if (arrayComponentType == long.class) {
      long[] array = (long[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Long)convertValue(converter, newValue, (Class)Long.class)).longValue();
    }
    else if (arrayComponentType == short.class) {
      short[] array = (short[])ctx;
      checkAccess(array.length, idx);
      array[idx] = ((Short)convertValue(converter, newValue, (Class)Short.class)).shortValue();
    } else {
      
      Object[] array = (Object[])ctx;
      checkAccess(array.length, idx);
      array[idx] = convertValue(converter, newValue, arrayComponentType);
    } 
  }
  
  private Object accessArrayElement(Object ctx, int idx) throws SpelEvaluationException {
    Class<?> arrayComponentType = ctx.getClass().getComponentType();
    if (arrayComponentType == boolean.class) {
      boolean[] arrayOfBoolean = (boolean[])ctx;
      checkAccess(arrayOfBoolean.length, idx);
      this.exitTypeDescriptor = "Z";
      return Boolean.valueOf(arrayOfBoolean[idx]);
    } 
    if (arrayComponentType == byte.class) {
      byte[] arrayOfByte = (byte[])ctx;
      checkAccess(arrayOfByte.length, idx);
      this.exitTypeDescriptor = "B";
      return Byte.valueOf(arrayOfByte[idx]);
    } 
    if (arrayComponentType == char.class) {
      char[] arrayOfChar = (char[])ctx;
      checkAccess(arrayOfChar.length, idx);
      this.exitTypeDescriptor = "C";
      return Character.valueOf(arrayOfChar[idx]);
    } 
    if (arrayComponentType == double.class) {
      double[] arrayOfDouble = (double[])ctx;
      checkAccess(arrayOfDouble.length, idx);
      this.exitTypeDescriptor = "D";
      return Double.valueOf(arrayOfDouble[idx]);
    } 
    if (arrayComponentType == float.class) {
      float[] arrayOfFloat = (float[])ctx;
      checkAccess(arrayOfFloat.length, idx);
      this.exitTypeDescriptor = "F";
      return Float.valueOf(arrayOfFloat[idx]);
    } 
    if (arrayComponentType == int.class) {
      int[] arrayOfInt = (int[])ctx;
      checkAccess(arrayOfInt.length, idx);
      this.exitTypeDescriptor = "I";
      return Integer.valueOf(arrayOfInt[idx]);
    } 
    if (arrayComponentType == long.class) {
      long[] arrayOfLong = (long[])ctx;
      checkAccess(arrayOfLong.length, idx);
      this.exitTypeDescriptor = "J";
      return Long.valueOf(arrayOfLong[idx]);
    } 
    if (arrayComponentType == short.class) {
      short[] arrayOfShort = (short[])ctx;
      checkAccess(arrayOfShort.length, idx);
      this.exitTypeDescriptor = "S";
      return Short.valueOf(arrayOfShort[idx]);
    } 
    
    Object[] array = (Object[])ctx;
    checkAccess(array.length, idx);
    Object retValue = array[idx];
    this.exitTypeDescriptor = CodeFlow.toDescriptor(arrayComponentType);
    return retValue;
  }

  
  private void checkAccess(int arrayLength, int index) throws SpelEvaluationException {
    if (index >= arrayLength) {
      throw new SpelEvaluationException(getStartPosition(), SpelMessage.ARRAY_INDEX_OUT_OF_BOUNDS, new Object[] {
            Integer.valueOf(arrayLength), Integer.valueOf(index)
          });
    }
  }
  
  private <T> T convertValue(TypeConverter converter, @Nullable Object value, Class<T> targetType) {
    T result = (T)converter.convertValue(value, 
        TypeDescriptor.forObject(value), TypeDescriptor.valueOf(targetType));
    if (result == null) {
      throw new IllegalStateException("Null conversion result for index [" + value + "]");
    }
    return result;
  }

  
  private class ArrayIndexingValueRef
    implements ValueRef
  {
    private final TypeConverter typeConverter;
    
    private final Object array;
    
    private final int index;
    private final TypeDescriptor typeDescriptor;
    
    ArrayIndexingValueRef(TypeConverter typeConverter, Object array, int index, TypeDescriptor typeDescriptor) {
      this.typeConverter = typeConverter;
      this.array = array;
      this.index = index;
      this.typeDescriptor = typeDescriptor;
    }

    
    public TypedValue getValue() {
      Object arrayElement = Indexer.this.accessArrayElement(this.array, this.index);
      return new TypedValue(arrayElement, this.typeDescriptor.elementTypeDescriptor(arrayElement));
    }

    
    public void setValue(@Nullable Object newValue) {
      TypeDescriptor elementType = this.typeDescriptor.getElementTypeDescriptor();
      Assert.state((elementType != null), "No element type");
      Indexer.this.setArrayElement(this.typeConverter, this.array, this.index, newValue, elementType.getType());
    }

    
    public boolean isWritable() {
      return true;
    }
  }


  
  private class MapIndexingValueRef
    implements ValueRef
  {
    private final TypeConverter typeConverter;
    
    private final Map map;
    
    @Nullable
    private final Object key;
    
    private final TypeDescriptor mapEntryDescriptor;

    
    public MapIndexingValueRef(TypeConverter typeConverter, @Nullable Map map, Object key, TypeDescriptor mapEntryDescriptor) {
      this.typeConverter = typeConverter;
      this.map = map;
      this.key = key;
      this.mapEntryDescriptor = mapEntryDescriptor;
    }

    
    public TypedValue getValue() {
      Object value = this.map.get(this.key);
      Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor(Object.class);
      return new TypedValue(value, this.mapEntryDescriptor.getMapValueTypeDescriptor(value));
    }

    
    public void setValue(@Nullable Object newValue) {
      if (this.mapEntryDescriptor.getMapValueTypeDescriptor() != null) {
        newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.mapEntryDescriptor
            .getMapValueTypeDescriptor());
      }
      this.map.put(this.key, newValue);
    }

    
    public boolean isWritable() {
      return true;
    }
  }

  
  private class PropertyIndexingValueRef
    implements ValueRef
  {
    private final Object targetObject;
    
    private final String name;
    
    private final EvaluationContext evaluationContext;
    
    private final TypeDescriptor targetObjectTypeDescriptor;

    
    public PropertyIndexingValueRef(Object targetObject, String value, EvaluationContext evaluationContext, TypeDescriptor targetObjectTypeDescriptor) {
      this.targetObject = targetObject;
      this.name = value;
      this.evaluationContext = evaluationContext;
      this.targetObjectTypeDescriptor = targetObjectTypeDescriptor;
    }

    
    public TypedValue getValue() {
      Class<?> targetObjectRuntimeClass = Indexer.this.getObjectClass(this.targetObject);
      try {
        if (Indexer.this.cachedReadName != null && Indexer.this.cachedReadName.equals(this.name) && Indexer.this
          .cachedReadTargetType != null && Indexer.this
          .cachedReadTargetType.equals(targetObjectRuntimeClass)) {
          
          PropertyAccessor accessor = Indexer.this.cachedReadAccessor;
          Assert.state((accessor != null), "No cached read accessor");
          return accessor.read(this.evaluationContext, this.targetObject, this.name);
        } 
        List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(targetObjectRuntimeClass, this.evaluationContext
            .getPropertyAccessors());
        for (PropertyAccessor accessor : accessorsToTry) {
          if (accessor.canRead(this.evaluationContext, this.targetObject, this.name)) {
            if (accessor instanceof ReflectivePropertyAccessor) {
              accessor = ((ReflectivePropertyAccessor)accessor).createOptimalAccessor(this.evaluationContext, this.targetObject, this.name);
            }
            
            Indexer.this.cachedReadAccessor = accessor;
            Indexer.this.cachedReadName = this.name;
            Indexer.this.cachedReadTargetType = targetObjectRuntimeClass;
            if (accessor instanceof ReflectivePropertyAccessor.OptimalPropertyAccessor) {
              ReflectivePropertyAccessor.OptimalPropertyAccessor optimalAccessor = (ReflectivePropertyAccessor.OptimalPropertyAccessor)accessor;
              
              Member member = optimalAccessor.member;
              Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor((member instanceof Method) ? ((Method)member)
                  .getReturnType() : ((Field)member).getType());
            } 
            return accessor.read(this.evaluationContext, this.targetObject, this.name);
          }
        
        } 
      } catch (AccessException ex) {
        throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.targetObjectTypeDescriptor
              .toString() });
      } 
      throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.targetObjectTypeDescriptor
            .toString() });
    }

    
    public void setValue(@Nullable Object newValue) {
      Class<?> contextObjectClass = Indexer.this.getObjectClass(this.targetObject);
      try {
        if (Indexer.this.cachedWriteName != null && Indexer.this.cachedWriteName.equals(this.name) && Indexer.this
          .cachedWriteTargetType != null && Indexer.this
          .cachedWriteTargetType.equals(contextObjectClass)) {
          
          PropertyAccessor accessor = Indexer.this.cachedWriteAccessor;
          Assert.state((accessor != null), "No cached write accessor");
          accessor.write(this.evaluationContext, this.targetObject, this.name, newValue);
          return;
        } 
        List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(contextObjectClass, this.evaluationContext
            .getPropertyAccessors());
        for (PropertyAccessor accessor : accessorsToTry) {
          if (accessor.canWrite(this.evaluationContext, this.targetObject, this.name)) {
            Indexer.this.cachedWriteName = this.name;
            Indexer.this.cachedWriteTargetType = contextObjectClass;
            Indexer.this.cachedWriteAccessor = accessor;
            accessor.write(this.evaluationContext, this.targetObject, this.name, newValue);
            
            return;
          } 
        } 
      } catch (AccessException ex) {
        throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, new Object[] { this.name, ex
              .getMessage() });
      } 
    }

    
    public boolean isWritable() {
      return true;
    }
  }


  
  private class CollectionIndexingValueRef
    implements ValueRef
  {
    private final Collection collection;
    
    private final int index;
    
    private final TypeDescriptor collectionEntryDescriptor;
    
    private final TypeConverter typeConverter;
    
    private final boolean growCollection;
    
    private final int maximumSize;

    
    public CollectionIndexingValueRef(Collection collection, int index, TypeDescriptor collectionEntryDescriptor, TypeConverter typeConverter, boolean growCollection, int maximumSize) {
      this.collection = collection;
      this.index = index;
      this.collectionEntryDescriptor = collectionEntryDescriptor;
      this.typeConverter = typeConverter;
      this.growCollection = growCollection;
      this.maximumSize = maximumSize;
    }

    
    public TypedValue getValue() {
      growCollectionIfNecessary();
      if (this.collection instanceof List) {
        Object o = ((List)this.collection).get(this.index);
        Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor(Object.class);
        return new TypedValue(o, this.collectionEntryDescriptor.elementTypeDescriptor(o));
      } 
      int pos = 0;
      for (Object o : this.collection) {
        if (pos == this.index) {
          return new TypedValue(o, this.collectionEntryDescriptor.elementTypeDescriptor(o));
        }
        pos++;
      } 
      throw new IllegalStateException("Failed to find indexed element " + this.index + ": " + this.collection);
    }

    
    public void setValue(@Nullable Object newValue) {
      growCollectionIfNecessary();
      if (this.collection instanceof List) {
        List<Object> list = (List)this.collection;
        if (this.collectionEntryDescriptor.getElementTypeDescriptor() != null) {
          newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.collectionEntryDescriptor
              .getElementTypeDescriptor());
        }
        list.set(this.index, newValue);
      } else {
        
        throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.collectionEntryDescriptor
              .toString() });
      } 
    }
    
    private void growCollectionIfNecessary() {
      if (this.index >= this.collection.size()) {
        if (!this.growCollection)
          throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.COLLECTION_INDEX_OUT_OF_BOUNDS, new Object[] {
                Integer.valueOf(this.collection.size()), Integer.valueOf(this.index)
              }); 
        if (this.index >= this.maximumSize) {
          throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
        }
        if (this.collectionEntryDescriptor.getElementTypeDescriptor() == null) {
          throw new SpelEvaluationException(Indexer.this
              .getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION_UNKNOWN_ELEMENT_TYPE, new Object[0]);
        }
        TypeDescriptor elementType = this.collectionEntryDescriptor.getElementTypeDescriptor();
        try {
          Constructor<?> ctor = getDefaultConstructor(elementType.getType());
          int newElements = this.index - this.collection.size();
          while (newElements >= 0)
          {
            this.collection.add((ctor != null) ? (T)ctor.newInstance(new Object[0]) : null);
            newElements--;
          }
        
        } catch (Throwable ex) {
          throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
        } 
      } 
    }
    
    @Nullable
    private Constructor<?> getDefaultConstructor(Class<?> type) {
      try {
        return ReflectionUtils.accessibleConstructor(type, new Class[0]);
      }
      catch (Throwable ex) {
        return null;
      } 
    }

    
    public boolean isWritable() {
      return true;
    }
  }

  
  private class StringIndexingLValue
    implements ValueRef
  {
    private final String target;
    
    private final int index;
    private final TypeDescriptor typeDescriptor;
    
    public StringIndexingLValue(String target, int index, TypeDescriptor typeDescriptor) {
      this.target = target;
      this.index = index;
      this.typeDescriptor = typeDescriptor;
    }

    
    public TypedValue getValue() {
      if (this.index >= this.target.length())
        throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.STRING_INDEX_OUT_OF_BOUNDS, new Object[] {
              Integer.valueOf(this.target.length()), Integer.valueOf(this.index)
            }); 
      return new TypedValue(String.valueOf(this.target.charAt(this.index)));
    }

    
    public void setValue(@Nullable Object newValue) {
      throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.typeDescriptor
            .toString() });
    }

    
    public boolean isWritable() {
      return true;
    }
  }
}
