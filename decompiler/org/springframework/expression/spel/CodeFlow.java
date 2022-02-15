package org.springframework.expression.spel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;




























































public class CodeFlow
  implements Opcodes
{
  private final String className;
  private final ClassWriter classWriter;
  private final Deque<List<String>> compilationScopes;
  @Nullable
  private List<FieldAdder> fieldAdders;
  @Nullable
  private List<ClinitAdder> clinitAdders;
  private int nextFieldId = 1;




  
  private int nextFreeVariableId = 1;






  
  public CodeFlow(String className, ClassWriter classWriter) {
    this.className = className;
    this.classWriter = classWriter;
    this.compilationScopes = new ArrayDeque<>();
    this.compilationScopes.add(new ArrayList<>());
  }






  
  public void loadTarget(MethodVisitor mv) {
    mv.visitVarInsn(25, 1);
  }






  
  public void loadEvaluationContext(MethodVisitor mv) {
    mv.visitVarInsn(25, 2);
  }




  
  public void pushDescriptor(@Nullable String descriptor) {
    if (descriptor != null) {
      ((List<String>)this.compilationScopes.element()).add(descriptor);
    }
  }





  
  public void enterCompilationScope() {
    this.compilationScopes.push(new ArrayList<>());
  }





  
  public void exitCompilationScope() {
    this.compilationScopes.pop();
  }



  
  @Nullable
  public String lastDescriptor() {
    return (String)CollectionUtils.lastElement(this.compilationScopes.peek());
  }





  
  public void unboxBooleanIfNecessary(MethodVisitor mv) {
    if ("Ljava/lang/Boolean".equals(lastDescriptor())) {
      mv.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false);
    }
  }





  
  public void finish() {
    if (this.fieldAdders != null) {
      for (FieldAdder fieldAdder : this.fieldAdders) {
        fieldAdder.generateField(this.classWriter, this);
      }
    }
    if (this.clinitAdders != null) {
      MethodVisitor mv = this.classWriter.visitMethod(9, "<clinit>", "()V", null, null);
      mv.visitCode();
      this.nextFreeVariableId = 0;
      for (ClinitAdder clinitAdder : this.clinitAdders) {
        clinitAdder.generateCode(mv, this);
      }
      mv.visitInsn(177);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
    } 
  }





  
  public void registerNewField(FieldAdder fieldAdder) {
    if (this.fieldAdders == null) {
      this.fieldAdders = new ArrayList<>();
    }
    this.fieldAdders.add(fieldAdder);
  }





  
  public void registerNewClinit(ClinitAdder clinitAdder) {
    if (this.clinitAdders == null) {
      this.clinitAdders = new ArrayList<>();
    }
    this.clinitAdders.add(clinitAdder);
  }
  
  public int nextFieldId() {
    return this.nextFieldId++;
  }
  
  public int nextFreeVariableId() {
    return this.nextFreeVariableId++;
  }
  
  public String getClassName() {
    return this.className;
  }








  
  public static void insertUnboxInsns(MethodVisitor mv, char ch, @Nullable String stackDescriptor) {
    if (stackDescriptor == null) {
      return;
    }
    switch (ch) {
      case 'Z':
        if (!stackDescriptor.equals("Ljava/lang/Boolean")) {
          mv.visitTypeInsn(192, "java/lang/Boolean");
        }
        mv.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false);
        return;
      case 'B':
        if (!stackDescriptor.equals("Ljava/lang/Byte")) {
          mv.visitTypeInsn(192, "java/lang/Byte");
        }
        mv.visitMethodInsn(182, "java/lang/Byte", "byteValue", "()B", false);
        return;
      case 'C':
        if (!stackDescriptor.equals("Ljava/lang/Character")) {
          mv.visitTypeInsn(192, "java/lang/Character");
        }
        mv.visitMethodInsn(182, "java/lang/Character", "charValue", "()C", false);
        return;
      case 'D':
        if (!stackDescriptor.equals("Ljava/lang/Double")) {
          mv.visitTypeInsn(192, "java/lang/Double");
        }
        mv.visitMethodInsn(182, "java/lang/Double", "doubleValue", "()D", false);
        return;
      case 'F':
        if (!stackDescriptor.equals("Ljava/lang/Float")) {
          mv.visitTypeInsn(192, "java/lang/Float");
        }
        mv.visitMethodInsn(182, "java/lang/Float", "floatValue", "()F", false);
        return;
      case 'I':
        if (!stackDescriptor.equals("Ljava/lang/Integer")) {
          mv.visitTypeInsn(192, "java/lang/Integer");
        }
        mv.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I", false);
        return;
      case 'J':
        if (!stackDescriptor.equals("Ljava/lang/Long")) {
          mv.visitTypeInsn(192, "java/lang/Long");
        }
        mv.visitMethodInsn(182, "java/lang/Long", "longValue", "()J", false);
        return;
      case 'S':
        if (!stackDescriptor.equals("Ljava/lang/Short")) {
          mv.visitTypeInsn(192, "java/lang/Short");
        }
        mv.visitMethodInsn(182, "java/lang/Short", "shortValue", "()S", false);
        return;
    } 
    throw new IllegalArgumentException("Unboxing should not be attempted for descriptor '" + ch + "'");
  }









  
  public static void insertUnboxNumberInsns(MethodVisitor mv, char targetDescriptor, @Nullable String stackDescriptor) {
    if (stackDescriptor == null) {
      return;
    }
    
    switch (targetDescriptor) {
      case 'D':
        if (stackDescriptor.equals("Ljava/lang/Object")) {
          mv.visitTypeInsn(192, "java/lang/Number");
        }
        mv.visitMethodInsn(182, "java/lang/Number", "doubleValue", "()D", false);
        return;
      case 'F':
        if (stackDescriptor.equals("Ljava/lang/Object")) {
          mv.visitTypeInsn(192, "java/lang/Number");
        }
        mv.visitMethodInsn(182, "java/lang/Number", "floatValue", "()F", false);
        return;
      case 'J':
        if (stackDescriptor.equals("Ljava/lang/Object")) {
          mv.visitTypeInsn(192, "java/lang/Number");
        }
        mv.visitMethodInsn(182, "java/lang/Number", "longValue", "()J", false);
        return;
      case 'I':
        if (stackDescriptor.equals("Ljava/lang/Object")) {
          mv.visitTypeInsn(192, "java/lang/Number");
        }
        mv.visitMethodInsn(182, "java/lang/Number", "intValue", "()I", false);
        return;
    } 
    
    throw new IllegalArgumentException("Unboxing should not be attempted for descriptor '" + targetDescriptor + "'");
  }







  
  public static void insertAnyNecessaryTypeConversionBytecodes(MethodVisitor mv, char targetDescriptor, String stackDescriptor) {
    if (isPrimitive(stackDescriptor)) {
      char stackTop = stackDescriptor.charAt(0);
      if (stackTop == 'I' || stackTop == 'B' || stackTop == 'S' || stackTop == 'C') {
        if (targetDescriptor == 'D') {
          mv.visitInsn(135);
        }
        else if (targetDescriptor == 'F') {
          mv.visitInsn(134);
        }
        else if (targetDescriptor == 'J') {
          mv.visitInsn(133);
        }
        else if (targetDescriptor != 'I') {


          
          throw new IllegalStateException("Cannot get from " + stackTop + " to " + targetDescriptor);
        }
      
      } else if (stackTop == 'J') {
        if (targetDescriptor == 'D') {
          mv.visitInsn(138);
        }
        else if (targetDescriptor == 'F') {
          mv.visitInsn(137);
        }
        else if (targetDescriptor != 'J') {

          
          if (targetDescriptor == 'I') {
            mv.visitInsn(136);
          } else {
            
            throw new IllegalStateException("Cannot get from " + stackTop + " to " + targetDescriptor);
          } 
        } 
      } else if (stackTop == 'F') {
        if (targetDescriptor == 'D') {
          mv.visitInsn(141);
        }
        else if (targetDescriptor != 'F') {

          
          if (targetDescriptor == 'J') {
            mv.visitInsn(140);
          }
          else if (targetDescriptor == 'I') {
            mv.visitInsn(139);
          } else {
            
            throw new IllegalStateException("Cannot get from " + stackTop + " to " + targetDescriptor);
          } 
        } 
      } else if (stackTop == 'D' && 
        targetDescriptor != 'D') {

        
        if (targetDescriptor == 'F') {
          mv.visitInsn(144);
        }
        else if (targetDescriptor == 'J') {
          mv.visitInsn(143);
        }
        else if (targetDescriptor == 'I') {
          mv.visitInsn(142);
        } else {
          
          throw new IllegalStateException("Cannot get from " + stackDescriptor + " to " + targetDescriptor);
        } 
      } 
    } 
  }










  
  public static String createSignatureDescriptor(Method method) {
    Class<?>[] params = method.getParameterTypes();
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    for (Class<?> param : params) {
      sb.append(toJvmDescriptor(param));
    }
    sb.append(')');
    sb.append(toJvmDescriptor(method.getReturnType()));
    return sb.toString();
  }









  
  public static String createSignatureDescriptor(Constructor<?> ctor) {
    Class<?>[] params = ctor.getParameterTypes();
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    for (Class<?> param : params) {
      sb.append(toJvmDescriptor(param));
    }
    sb.append(")V");
    return sb.toString();
  }








  
  public static String toJvmDescriptor(Class<?> clazz) {
    StringBuilder sb = new StringBuilder();
    if (clazz.isArray()) {
      while (clazz.isArray()) {
        sb.append('[');
        clazz = clazz.getComponentType();
      } 
    }
    if (clazz.isPrimitive()) {
      if (clazz == boolean.class) {
        sb.append('Z');
      }
      else if (clazz == byte.class) {
        sb.append('B');
      }
      else if (clazz == char.class) {
        sb.append('C');
      }
      else if (clazz == double.class) {
        sb.append('D');
      }
      else if (clazz == float.class) {
        sb.append('F');
      }
      else if (clazz == int.class) {
        sb.append('I');
      }
      else if (clazz == long.class) {
        sb.append('J');
      }
      else if (clazz == short.class) {
        sb.append('S');
      }
      else if (clazz == void.class) {
        sb.append('V');
      } 
    } else {
      
      sb.append('L');
      sb.append(clazz.getName().replace('.', '/'));
      sb.append(';');
    } 
    return sb.toString();
  }






  
  public static String toDescriptorFromObject(@Nullable Object value) {
    if (value == null) {
      return "Ljava/lang/Object";
    }
    
    return toDescriptor(value.getClass());
  }






  
  public static boolean isBooleanCompatible(@Nullable String descriptor) {
    return (descriptor != null && (descriptor.equals("Z") || descriptor.equals("Ljava/lang/Boolean")));
  }





  
  public static boolean isPrimitive(@Nullable String descriptor) {
    return (descriptor != null && descriptor.length() == 1);
  }





  
  public static boolean isPrimitiveArray(@Nullable String descriptor) {
    if (descriptor == null) {
      return false;
    }
    boolean primitive = true;
    for (int i = 0, max = descriptor.length(); i < max; ) {
      char ch = descriptor.charAt(i);
      if (ch == '[') {
        i++; continue;
      } 
      primitive = (ch != 'L');
    } 
    
    return primitive;
  }





  
  public static boolean areBoxingCompatible(String desc1, String desc2) {
    if (desc1.equals(desc2)) {
      return true;
    }
    if (desc1.length() == 1) {
      if (desc1.equals("Z")) {
        return desc2.equals("Ljava/lang/Boolean");
      }
      if (desc1.equals("D")) {
        return desc2.equals("Ljava/lang/Double");
      }
      if (desc1.equals("F")) {
        return desc2.equals("Ljava/lang/Float");
      }
      if (desc1.equals("I")) {
        return desc2.equals("Ljava/lang/Integer");
      }
      if (desc1.equals("J")) {
        return desc2.equals("Ljava/lang/Long");
      }
    }
    else if (desc2.length() == 1) {
      if (desc2.equals("Z")) {
        return desc1.equals("Ljava/lang/Boolean");
      }
      if (desc2.equals("D")) {
        return desc1.equals("Ljava/lang/Double");
      }
      if (desc2.equals("F")) {
        return desc1.equals("Ljava/lang/Float");
      }
      if (desc2.equals("I")) {
        return desc1.equals("Ljava/lang/Integer");
      }
      if (desc2.equals("J")) {
        return desc1.equals("Ljava/lang/Long");
      }
    } 
    return false;
  }







  
  public static boolean isPrimitiveOrUnboxableSupportedNumberOrBoolean(@Nullable String descriptor) {
    if (descriptor == null) {
      return false;
    }
    if (isPrimitiveOrUnboxableSupportedNumber(descriptor)) {
      return true;
    }
    return ("Z".equals(descriptor) || descriptor.equals("Ljava/lang/Boolean"));
  }







  
  public static boolean isPrimitiveOrUnboxableSupportedNumber(@Nullable String descriptor) {
    if (descriptor == null) {
      return false;
    }
    if (descriptor.length() == 1) {
      return "DFIJ".contains(descriptor);
    }
    if (descriptor.startsWith("Ljava/lang/")) {
      String name = descriptor.substring("Ljava/lang/".length());
      if (name.equals("Double") || name.equals("Float") || name.equals("Integer") || name.equals("Long")) {
        return true;
      }
    } 
    return false;
  }






  
  public static boolean isIntegerForNumericOp(Number number) {
    return (number instanceof Integer || number instanceof Short || number instanceof Byte);
  }





  
  public static char toPrimitiveTargetDesc(String descriptor) {
    if (descriptor.length() == 1) {
      return descriptor.charAt(0);
    }
    if (descriptor.equals("Ljava/lang/Boolean")) {
      return 'Z';
    }
    if (descriptor.equals("Ljava/lang/Byte")) {
      return 'B';
    }
    if (descriptor.equals("Ljava/lang/Character")) {
      return 'C';
    }
    if (descriptor.equals("Ljava/lang/Double")) {
      return 'D';
    }
    if (descriptor.equals("Ljava/lang/Float")) {
      return 'F';
    }
    if (descriptor.equals("Ljava/lang/Integer")) {
      return 'I';
    }
    if (descriptor.equals("Ljava/lang/Long")) {
      return 'J';
    }
    if (descriptor.equals("Ljava/lang/Short")) {
      return 'S';
    }
    
    throw new IllegalStateException("No primitive for '" + descriptor + "'");
  }






  
  public static void insertCheckCast(MethodVisitor mv, @Nullable String descriptor) {
    if (descriptor != null && descriptor.length() != 1) {
      if (descriptor.charAt(0) == '[') {
        if (isPrimitiveArray(descriptor)) {
          mv.visitTypeInsn(192, descriptor);
        } else {
          
          mv.visitTypeInsn(192, descriptor + ";");
        }
      
      }
      else if (!descriptor.equals("Ljava/lang/Object")) {
        
        mv.visitTypeInsn(192, descriptor.substring(1));
      } 
    }
  }







  
  public static void insertBoxIfNecessary(MethodVisitor mv, @Nullable String descriptor) {
    if (descriptor != null && descriptor.length() == 1) {
      insertBoxIfNecessary(mv, descriptor.charAt(0));
    }
  }






  
  public static void insertBoxIfNecessary(MethodVisitor mv, char ch) {
    switch (ch) {
      case 'Z':
        mv.visitMethodInsn(184, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
      
      case 'B':
        mv.visitMethodInsn(184, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
      
      case 'C':
        mv.visitMethodInsn(184, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
      
      case 'D':
        mv.visitMethodInsn(184, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
      
      case 'F':
        mv.visitMethodInsn(184, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
      
      case 'I':
        mv.visitMethodInsn(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
      
      case 'J':
        mv.visitMethodInsn(184, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
      
      case 'S':
        mv.visitMethodInsn(184, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
      
      case 'L':
      case 'V':
      case '[':
        return;
    } 
    
    throw new IllegalArgumentException("Boxing should not be attempted for descriptor '" + ch + "'");
  }







  
  public static String toDescriptor(Class<?> type) {
    String name = type.getName();
    if (type.isPrimitive()) {
      switch (name.length()) {
        case 3:
          return "I";
        case 4:
          if (name.equals("byte")) {
            return "B";
          }
          if (name.equals("char")) {
            return "C";
          }
          if (name.equals("long")) {
            return "J";
          }
          if (name.equals("void")) {
            return "V";
          }
          break;
        case 5:
          if (name.equals("float")) {
            return "F";
          }
          if (name.equals("short")) {
            return "S";
          }
          break;
        case 6:
          if (name.equals("double")) {
            return "D";
          }
          break;
        case 7:
          if (name.equals("boolean")) {
            return "Z";
          }
          break;
      } 
    
    } else {
      if (name.charAt(0) != '[') {
        return "L" + type.getName().replace('.', '/');
      }
      
      if (name.endsWith(";")) {
        return name.substring(0, name.length() - 1).replace('.', '/');
      }
      
      return name;
    } 

    
    return "";
  }






  
  public static String[] toParamDescriptors(Method method) {
    return toDescriptors(method.getParameterTypes());
  }






  
  public static String[] toParamDescriptors(Constructor<?> ctor) {
    return toDescriptors(ctor.getParameterTypes());
  }





  
  public static String[] toDescriptors(Class<?>[] types) {
    int typesCount = types.length;
    String[] descriptors = new String[typesCount];
    for (int p = 0; p < typesCount; p++) {
      descriptors[p] = toDescriptor(types[p]);
    }
    return descriptors;
  }





  
  public static void insertOptimalLoad(MethodVisitor mv, int value) {
    if (value < 6) {
      mv.visitInsn(3 + value);
    }
    else if (value < 127) {
      mv.visitIntInsn(16, value);
    }
    else if (value < 32767) {
      mv.visitIntInsn(17, value);
    } else {
      
      mv.visitLdcInsn(Integer.valueOf(value));
    } 
  }







  
  public static void insertArrayStore(MethodVisitor mv, String arrayElementType) {
    if (arrayElementType.length() == 1) {
      switch (arrayElementType.charAt(0)) {
        case 'I':
          mv.visitInsn(79);
          return;
        case 'J':
          mv.visitInsn(80);
          return;
        case 'F':
          mv.visitInsn(81);
          return;
        case 'D':
          mv.visitInsn(82);
          return;
        case 'B':
          mv.visitInsn(84);
          return;
        case 'C':
          mv.visitInsn(85);
          return;
        case 'S':
          mv.visitInsn(86);
          return;
        case 'Z':
          mv.visitInsn(84);
          return;
      } 
      throw new IllegalArgumentException("Unexpected arraytype " + arrayElementType
          .charAt(0));
    } 

    
    mv.visitInsn(83);
  }






  
  public static int arrayCodeFor(String arraytype) {
    switch (arraytype.charAt(0)) { case 'I':
        return 10;
      case 'J': return 11;
      case 'F': return 6;
      case 'D': return 7;
      case 'B': return 8;
      case 'C': return 5;
      case 'S': return 9;
      case 'Z': return 4; }
    
    throw new IllegalArgumentException("Unexpected arraytype " + arraytype.charAt(0));
  }




  
  public static boolean isReferenceTypeArray(String arraytype) {
    int length = arraytype.length();
    for (int i = 0; i < length; ) {
      char ch = arraytype.charAt(i);
      if (ch == '[') {
        i++; continue;
      } 
      return (ch == 'L');
    } 
    return false;
  }








  
  public static void insertNewArrayCode(MethodVisitor mv, int size, String arraytype) {
    insertOptimalLoad(mv, size);
    if (arraytype.length() == 1) {
      mv.visitIntInsn(188, arrayCodeFor(arraytype));
    
    }
    else if (arraytype.charAt(0) == '[') {

      
      if (isReferenceTypeArray(arraytype)) {
        mv.visitTypeInsn(189, arraytype + ";");
      } else {
        
        mv.visitTypeInsn(189, arraytype);
      } 
    } else {
      
      mv.visitTypeInsn(189, arraytype.substring(1));
    } 
  }












  
  public static void insertNumericUnboxOrPrimitiveTypeCoercion(MethodVisitor mv, @Nullable String stackDescriptor, char targetDescriptor) {
    if (!isPrimitive(stackDescriptor)) {
      insertUnboxNumberInsns(mv, targetDescriptor, stackDescriptor);
    } else {
      
      insertAnyNecessaryTypeConversionBytecodes(mv, targetDescriptor, stackDescriptor);
    } 
  }
  
  public static String toBoxedDescriptor(String primitiveDescriptor) {
    switch (primitiveDescriptor.charAt(0)) { case 'I':
        return "Ljava/lang/Integer";
      case 'J': return "Ljava/lang/Long";
      case 'F': return "Ljava/lang/Float";
      case 'D': return "Ljava/lang/Double";
      case 'B': return "Ljava/lang/Byte";
      case 'C': return "Ljava/lang/Character";
      case 'S': return "Ljava/lang/Short";
      case 'Z': return "Ljava/lang/Boolean"; }
    
    throw new IllegalArgumentException("Unexpected non primitive descriptor " + primitiveDescriptor);
  }
  
  @FunctionalInterface
  public static interface ClinitAdder {
    void generateCode(MethodVisitor param1MethodVisitor, CodeFlow param1CodeFlow);
  }
  
  @FunctionalInterface
  public static interface FieldAdder {
    void generateField(ClassWriter param1ClassWriter, CodeFlow param1CodeFlow);
  }
}
