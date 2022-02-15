package org.springframework.asm;


















































































































































































class Frame
{
  static final int SAME_FRAME = 0;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
  static final int RESERVED = 128;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
  static final int CHOP_FRAME = 248;
  static final int SAME_FRAME_EXTENDED = 251;
  static final int APPEND_FRAME = 252;
  static final int FULL_FRAME = 255;
  static final int ITEM_TOP = 0;
  static final int ITEM_INTEGER = 1;
  static final int ITEM_FLOAT = 2;
  static final int ITEM_DOUBLE = 3;
  static final int ITEM_LONG = 4;
  static final int ITEM_NULL = 5;
  static final int ITEM_UNINITIALIZED_THIS = 6;
  static final int ITEM_OBJECT = 7;
  static final int ITEM_UNINITIALIZED = 8;
  private static final int ITEM_ASM_BOOLEAN = 9;
  private static final int ITEM_ASM_BYTE = 10;
  private static final int ITEM_ASM_CHAR = 11;
  private static final int ITEM_ASM_SHORT = 12;
  private static final int DIM_SIZE = 6;
  private static final int KIND_SIZE = 4;
  private static final int FLAGS_SIZE = 2;
  private static final int VALUE_SIZE = 20;
  private static final int DIM_SHIFT = 26;
  private static final int KIND_SHIFT = 22;
  private static final int FLAGS_SHIFT = 20;
  private static final int DIM_MASK = -67108864;
  private static final int KIND_MASK = 62914560;
  private static final int VALUE_MASK = 1048575;
  private static final int ARRAY_OF = 67108864;
  private static final int ELEMENT_OF = -67108864;
  private static final int CONSTANT_KIND = 4194304;
  private static final int REFERENCE_KIND = 8388608;
  private static final int UNINITIALIZED_KIND = 12582912;
  private static final int LOCAL_KIND = 16777216;
  private static final int STACK_KIND = 20971520;
  private static final int TOP_IF_LONG_OR_DOUBLE_FLAG = 1048576;
  private static final int TOP = 4194304;
  private static final int BOOLEAN = 4194313;
  private static final int BYTE = 4194314;
  private static final int CHAR = 4194315;
  private static final int SHORT = 4194316;
  private static final int INTEGER = 4194305;
  private static final int FLOAT = 4194306;
  private static final int LONG = 4194308;
  private static final int DOUBLE = 4194307;
  private static final int NULL = 4194309;
  private static final int UNINITIALIZED_THIS = 4194310;
  Label owner;
  private int[] inputLocals;
  private int[] inputStack;
  private int[] outputLocals;
  private int[] outputStack;
  private short outputStackStart;
  private short outputStackTop;
  private int initializationCount;
  private int[] initializations;
  
  Frame(Label owner) {
    this.owner = owner;
  }








  
  final void copyFrom(Frame frame) {
    this.inputLocals = frame.inputLocals;
    this.inputStack = frame.inputStack;
    this.outputStackStart = 0;
    this.outputLocals = frame.outputLocals;
    this.outputStack = frame.outputStack;
    this.outputStackTop = frame.outputStackTop;
    this.initializationCount = frame.initializationCount;
    this.initializations = frame.initializations;
  }















  
  static int getAbstractTypeFromApiFormat(SymbolTable symbolTable, Object type) {
    if (type instanceof Integer)
      return 0x400000 | ((Integer)type).intValue(); 
    if (type instanceof String) {
      String descriptor = Type.getObjectType((String)type).getDescriptor();
      return getAbstractTypeFromDescriptor(symbolTable, descriptor, 0);
    } 
    return 0xC00000 | symbolTable
      .addUninitializedType("", ((Label)type).bytecodeOffset);
  }










  
  static int getAbstractTypeFromInternalName(SymbolTable symbolTable, String internalName) {
    return 0x800000 | symbolTable.addType(internalName);
  }







  
  private static int getAbstractTypeFromDescriptor(SymbolTable symbolTable, String buffer, int offset) {
    String internalName;
    int elementDescriptorOffset;
    int typeValue;
    switch (buffer.charAt(offset)) {
      case 'V':
        return 0;
      case 'B':
      case 'C':
      case 'I':
      case 'S':
      case 'Z':
        return 4194305;
      case 'F':
        return 4194306;
      case 'J':
        return 4194308;
      case 'D':
        return 4194307;
      case 'L':
        internalName = buffer.substring(offset + 1, buffer.length() - 1);
        return 0x800000 | symbolTable.addType(internalName);
      case '[':
        elementDescriptorOffset = offset + 1;
        while (buffer.charAt(elementDescriptorOffset) == '[') {
          elementDescriptorOffset++;
        }
        
        switch (buffer.charAt(elementDescriptorOffset)) {
          case 'Z':
            typeValue = 4194313;




























            
            return elementDescriptorOffset - offset << 26 | typeValue;case 'C': typeValue = 4194315; return elementDescriptorOffset - offset << 26 | typeValue;case 'B': typeValue = 4194314; return elementDescriptorOffset - offset << 26 | typeValue;case 'S': typeValue = 4194316; return elementDescriptorOffset - offset << 26 | typeValue;case 'I': typeValue = 4194305; return elementDescriptorOffset - offset << 26 | typeValue;case 'F': typeValue = 4194306; return elementDescriptorOffset - offset << 26 | typeValue;case 'J': typeValue = 4194308; return elementDescriptorOffset - offset << 26 | typeValue;case 'D': typeValue = 4194307; return elementDescriptorOffset - offset << 26 | typeValue;case 'L': internalName = buffer.substring(elementDescriptorOffset + 1, buffer.length() - 1); typeValue = 0x800000 | symbolTable.addType(internalName); return elementDescriptorOffset - offset << 26 | typeValue;
        }  throw new IllegalArgumentException();
    }  throw new IllegalArgumentException();
  }



















  
  final void setInputFrameFromDescriptor(SymbolTable symbolTable, int access, String descriptor, int maxLocals) {
    this.inputLocals = new int[maxLocals];
    this.inputStack = new int[0];
    int inputLocalIndex = 0;
    if ((access & 0x8) == 0) {
      if ((access & 0x40000) == 0) {
        this.inputLocals[inputLocalIndex++] = 0x800000 | symbolTable
          .addType(symbolTable.getClassName());
      } else {
        this.inputLocals[inputLocalIndex++] = 4194310;
      } 
    }
    for (Type argumentType : Type.getArgumentTypes(descriptor)) {
      
      int abstractType = getAbstractTypeFromDescriptor(symbolTable, argumentType.getDescriptor(), 0);
      this.inputLocals[inputLocalIndex++] = abstractType;
      if (abstractType == 4194308 || abstractType == 4194307) {
        this.inputLocals[inputLocalIndex++] = 4194304;
      }
    } 
    while (inputLocalIndex < maxLocals) {
      this.inputLocals[inputLocalIndex++] = 4194304;
    }
  }
















  
  final void setInputFrameFromApiFormat(SymbolTable symbolTable, int numLocal, Object[] local, int numStack, Object[] stack) {
    int inputLocalIndex = 0;
    for (int i = 0; i < numLocal; i++) {
      this.inputLocals[inputLocalIndex++] = getAbstractTypeFromApiFormat(symbolTable, local[i]);
      if (local[i] == Opcodes.LONG || local[i] == Opcodes.DOUBLE) {
        this.inputLocals[inputLocalIndex++] = 4194304;
      }
    } 
    while (inputLocalIndex < this.inputLocals.length) {
      this.inputLocals[inputLocalIndex++] = 4194304;
    }
    int numStackTop = 0;
    for (int j = 0; j < numStack; j++) {
      if (stack[j] == Opcodes.LONG || stack[j] == Opcodes.DOUBLE) {
        numStackTop++;
      }
    } 
    this.inputStack = new int[numStack + numStackTop];
    int inputStackIndex = 0;
    for (int k = 0; k < numStack; k++) {
      this.inputStack[inputStackIndex++] = getAbstractTypeFromApiFormat(symbolTable, stack[k]);
      if (stack[k] == Opcodes.LONG || stack[k] == Opcodes.DOUBLE) {
        this.inputStack[inputStackIndex++] = 4194304;
      }
    } 
    this.outputStackTop = 0;
    this.initializationCount = 0;
  }
  
  final int getInputStackSize() {
    return this.inputStack.length;
  }










  
  private int getLocal(int localIndex) {
    if (this.outputLocals == null || localIndex >= this.outputLocals.length)
    {
      
      return 0x1000000 | localIndex;
    }
    int abstractType = this.outputLocals[localIndex];
    if (abstractType == 0)
    {
      
      abstractType = this.outputLocals[localIndex] = 0x1000000 | localIndex;
    }
    return abstractType;
  }








  
  private void setLocal(int localIndex, int abstractType) {
    if (this.outputLocals == null) {
      this.outputLocals = new int[10];
    }
    int outputLocalsLength = this.outputLocals.length;
    if (localIndex >= outputLocalsLength) {
      int[] newOutputLocals = new int[Math.max(localIndex + 1, 2 * outputLocalsLength)];
      System.arraycopy(this.outputLocals, 0, newOutputLocals, 0, outputLocalsLength);
      this.outputLocals = newOutputLocals;
    } 
    
    this.outputLocals[localIndex] = abstractType;
  }






  
  private void push(int abstractType) {
    if (this.outputStack == null) {
      this.outputStack = new int[10];
    }
    int outputStackLength = this.outputStack.length;
    if (this.outputStackTop >= outputStackLength) {
      int[] newOutputStack = new int[Math.max(this.outputStackTop + 1, 2 * outputStackLength)];
      System.arraycopy(this.outputStack, 0, newOutputStack, 0, outputStackLength);
      this.outputStack = newOutputStack;
    } 
    
    this.outputStackTop = (short)(this.outputStackTop + 1); this.outputStack[this.outputStackTop] = abstractType;

    
    short outputStackSize = (short)(this.outputStackStart + this.outputStackTop);
    if (outputStackSize > this.owner.outputStackMax) {
      this.owner.outputStackMax = outputStackSize;
    }
  }







  
  private void push(SymbolTable symbolTable, String descriptor) {
    int typeDescriptorOffset = (descriptor.charAt(0) == '(') ? Type.getReturnTypeOffset(descriptor) : 0;
    int abstractType = getAbstractTypeFromDescriptor(symbolTable, descriptor, typeDescriptorOffset);
    if (abstractType != 0) {
      push(abstractType);
      if (abstractType == 4194308 || abstractType == 4194307) {
        push(4194304);
      }
    } 
  }





  
  private int pop() {
    if (this.outputStackTop > 0) {
      return this.outputStack[this.outputStackTop = (short)(this.outputStackTop - 1)];
    }
    
    return 0x1400000 | -(this.outputStackStart = (short)(this.outputStackStart - 1));
  }






  
  private void pop(int elements) {
    if (this.outputStackTop >= elements) {
      this.outputStackTop = (short)(this.outputStackTop - elements);
    }
    else {
      
      this.outputStackStart = (short)(this.outputStackStart - elements - this.outputStackTop);
      this.outputStackTop = 0;
    } 
  }





  
  private void pop(String descriptor) {
    char firstDescriptorChar = descriptor.charAt(0);
    if (firstDescriptorChar == '(') {
      pop((Type.getArgumentsAndReturnSizes(descriptor) >> 2) - 1);
    } else if (firstDescriptorChar == 'J' || firstDescriptorChar == 'D') {
      pop(2);
    } else {
      pop(1);
    } 
  }











  
  private void addInitializedType(int abstractType) {
    if (this.initializations == null) {
      this.initializations = new int[2];
    }
    int initializationsLength = this.initializations.length;
    if (this.initializationCount >= initializationsLength) {
      
      int[] newInitializations = new int[Math.max(this.initializationCount + 1, 2 * initializationsLength)];
      System.arraycopy(this.initializations, 0, newInitializations, 0, initializationsLength);
      this.initializations = newInitializations;
    } 
    
    this.initializations[this.initializationCount++] = abstractType;
  }









  
  private int getInitializedType(SymbolTable symbolTable, int abstractType) {
    if (abstractType == 4194310 || (abstractType & 0xFFC00000) == 12582912)
    {
      for (int i = 0; i < this.initializationCount; i++) {
        int initializedType = this.initializations[i];
        int dim = initializedType & 0xFC000000;
        int kind = initializedType & 0x3C00000;
        int value = initializedType & 0xFFFFF;
        if (kind == 16777216) {
          initializedType = dim + this.inputLocals[value];
        } else if (kind == 20971520) {
          initializedType = dim + this.inputStack[this.inputStack.length - value];
        } 
        if (abstractType == initializedType) {
          if (abstractType == 4194310) {
            return 0x800000 | symbolTable.addType(symbolTable.getClassName());
          }
          return 0x800000 | symbolTable
            .addType((symbolTable.getType(abstractType & 0xFFFFF)).value);
        } 
      } 
    }
    
    return abstractType;
  }












  
  void execute(int opcode, int arg, Symbol argSymbol, SymbolTable symbolTable) {
    int abstractType1;
    int abstractType2;
    int abstractType3;
    int abstractType4;
    String arrayElementType;
    String castType;
    switch (opcode) {
      case 0:
      case 116:
      case 117:
      case 118:
      case 119:
      case 145:
      case 146:
      case 147:
      case 167:
      case 177:
        return;
      case 1:
        push(4194309);
      
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 16:
      case 17:
      case 21:
        push(4194305);
      
      case 9:
      case 10:
      case 22:
        push(4194308);
        push(4194304);
      
      case 11:
      case 12:
      case 13:
      case 23:
        push(4194306);
      
      case 14:
      case 15:
      case 24:
        push(4194307);
        push(4194304);
      
      case 18:
        switch (argSymbol.tag) {
          case 3:
            push(4194305);
          
          case 5:
            push(4194308);
            push(4194304);
          
          case 4:
            push(4194306);
          
          case 6:
            push(4194307);
            push(4194304);
          
          case 7:
            push(0x800000 | symbolTable.addType("java/lang/Class"));
          
          case 8:
            push(0x800000 | symbolTable.addType("java/lang/String"));
          
          case 16:
            push(0x800000 | symbolTable.addType("java/lang/invoke/MethodType"));
          
          case 15:
            push(0x800000 | symbolTable.addType("java/lang/invoke/MethodHandle"));
          
          case 17:
            push(symbolTable, argSymbol.value);
        } 
        
        throw new AssertionError();

      
      case 25:
        push(getLocal(arg));
      
      case 47:
      case 143:
        pop(2);
        push(4194308);
        push(4194304);
      
      case 49:
      case 138:
        pop(2);
        push(4194307);
        push(4194304);
      
      case 50:
        pop(1);
        abstractType1 = pop();
        push((abstractType1 == 4194309) ? abstractType1 : (-67108864 + abstractType1));
      
      case 54:
      case 56:
      case 58:
        abstractType1 = pop();
        setLocal(arg, abstractType1);
        if (arg > 0) {
          int previousLocalType = getLocal(arg - 1);
          if (previousLocalType == 4194308 || previousLocalType == 4194307) {
            setLocal(arg - 1, 4194304);
          } else if ((previousLocalType & 0x3C00000) == 16777216 || (previousLocalType & 0x3C00000) == 20971520) {


            
            setLocal(arg - 1, previousLocalType | 0x100000);
          } 
        } 
      
      case 55:
      case 57:
        pop(1);
        abstractType1 = pop();
        setLocal(arg, abstractType1);
        setLocal(arg + 1, 4194304);
        if (arg > 0) {
          int previousLocalType = getLocal(arg - 1);
          if (previousLocalType == 4194308 || previousLocalType == 4194307) {
            setLocal(arg - 1, 4194304);
          } else if ((previousLocalType & 0x3C00000) == 16777216 || (previousLocalType & 0x3C00000) == 20971520) {


            
            setLocal(arg - 1, previousLocalType | 0x100000);
          } 
        } 
      
      case 79:
      case 81:
      case 83:
      case 84:
      case 85:
      case 86:
        pop(3);
      
      case 80:
      case 82:
        pop(4);
      
      case 87:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 170:
      case 171:
      case 172:
      case 174:
      case 176:
      case 191:
      case 194:
      case 195:
      case 198:
      case 199:
        pop(1);
      
      case 88:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 173:
      case 175:
        pop(2);
      
      case 89:
        abstractType1 = pop();
        push(abstractType1);
        push(abstractType1);
      
      case 90:
        abstractType1 = pop();
        abstractType2 = pop();
        push(abstractType1);
        push(abstractType2);
        push(abstractType1);
      
      case 91:
        abstractType1 = pop();
        abstractType2 = pop();
        abstractType3 = pop();
        push(abstractType1);
        push(abstractType3);
        push(abstractType2);
        push(abstractType1);
      
      case 92:
        abstractType1 = pop();
        abstractType2 = pop();
        push(abstractType2);
        push(abstractType1);
        push(abstractType2);
        push(abstractType1);
      
      case 93:
        abstractType1 = pop();
        abstractType2 = pop();
        abstractType3 = pop();
        push(abstractType2);
        push(abstractType1);
        push(abstractType3);
        push(abstractType2);
        push(abstractType1);
      
      case 94:
        abstractType1 = pop();
        abstractType2 = pop();
        abstractType3 = pop();
        abstractType4 = pop();
        push(abstractType2);
        push(abstractType1);
        push(abstractType4);
        push(abstractType3);
        push(abstractType2);
        push(abstractType1);
      
      case 95:
        abstractType1 = pop();
        abstractType2 = pop();
        push(abstractType1);
        push(abstractType2);
      
      case 46:
      case 51:
      case 52:
      case 53:
      case 96:
      case 100:
      case 104:
      case 108:
      case 112:
      case 120:
      case 122:
      case 124:
      case 126:
      case 128:
      case 130:
      case 136:
      case 142:
      case 149:
      case 150:
        pop(2);
        push(4194305);
      
      case 97:
      case 101:
      case 105:
      case 109:
      case 113:
      case 127:
      case 129:
      case 131:
        pop(4);
        push(4194308);
        push(4194304);
      
      case 48:
      case 98:
      case 102:
      case 106:
      case 110:
      case 114:
      case 137:
      case 144:
        pop(2);
        push(4194306);
      
      case 99:
      case 103:
      case 107:
      case 111:
      case 115:
        pop(4);
        push(4194307);
        push(4194304);
      
      case 121:
      case 123:
      case 125:
        pop(3);
        push(4194308);
        push(4194304);
      
      case 132:
        setLocal(arg, 4194305);
      
      case 133:
      case 140:
        pop(1);
        push(4194308);
        push(4194304);
      
      case 134:
        pop(1);
        push(4194306);
      
      case 135:
      case 141:
        pop(1);
        push(4194307);
        push(4194304);
      
      case 139:
      case 190:
      case 193:
        pop(1);
        push(4194305);
      
      case 148:
      case 151:
      case 152:
        pop(4);
        push(4194305);
      
      case 168:
      case 169:
        throw new IllegalArgumentException("JSR/RET are not supported with computeFrames option");
      case 178:
        push(symbolTable, argSymbol.value);
      
      case 179:
        pop(argSymbol.value);
      
      case 180:
        pop(1);
        push(symbolTable, argSymbol.value);
      
      case 181:
        pop(argSymbol.value);
        pop();
      
      case 182:
      case 183:
      case 184:
      case 185:
        pop(argSymbol.value);
        if (opcode != 184) {
          abstractType1 = pop();
          if (opcode == 183 && argSymbol.name.charAt(0) == '<') {
            addInitializedType(abstractType1);
          }
        } 
        push(symbolTable, argSymbol.value);
      
      case 186:
        pop(argSymbol.value);
        push(symbolTable, argSymbol.value);
      
      case 187:
        push(0xC00000 | symbolTable.addUninitializedType(argSymbol.value, arg));
      
      case 188:
        pop();
        switch (arg) {
          case 4:
            push(71303177);
          
          case 5:
            push(71303179);
          
          case 8:
            push(71303178);
          
          case 9:
            push(71303180);
          
          case 10:
            push(71303169);
          
          case 6:
            push(71303170);
          
          case 7:
            push(71303171);
          
          case 11:
            push(71303172);
        } 
        
        throw new IllegalArgumentException();

      
      case 189:
        arrayElementType = argSymbol.value;
        pop();
        if (arrayElementType.charAt(0) == '[') {
          push(symbolTable, '[' + arrayElementType);
        } else {
          push(0x4800000 | symbolTable.addType(arrayElementType));
        } 
      
      case 192:
        castType = argSymbol.value;
        pop();
        if (castType.charAt(0) == '[') {
          push(symbolTable, castType);
        } else {
          push(0x800000 | symbolTable.addType(castType));
        } 
      
      case 197:
        pop(arg);
        push(symbolTable, argSymbol.value);
    } 
    
    throw new IllegalArgumentException();
  }













  
  private int getConcreteOutputType(int abstractOutputType, int numStack) {
    int dim = abstractOutputType & 0xFC000000;
    int kind = abstractOutputType & 0x3C00000;
    if (kind == 16777216) {


      
      int concreteOutputType = dim + this.inputLocals[abstractOutputType & 0xFFFFF];
      if ((abstractOutputType & 0x100000) != 0 && (concreteOutputType == 4194308 || concreteOutputType == 4194307))
      {
        concreteOutputType = 4194304;
      }
      return concreteOutputType;
    }  if (kind == 20971520) {


      
      int concreteOutputType = dim + this.inputStack[numStack - (abstractOutputType & 0xFFFFF)];
      if ((abstractOutputType & 0x100000) != 0 && (concreteOutputType == 4194308 || concreteOutputType == 4194307))
      {
        concreteOutputType = 4194304;
      }
      return concreteOutputType;
    } 
    return abstractOutputType;
  }














  
  final boolean merge(SymbolTable symbolTable, Frame dstFrame, int catchTypeIndex) {
    boolean frameChanged = false;



    
    int numLocal = this.inputLocals.length;
    int numStack = this.inputStack.length;
    if (dstFrame.inputLocals == null) {
      dstFrame.inputLocals = new int[numLocal];
      frameChanged = true;
    }  int i;
    for (i = 0; i < numLocal; i++) {
      int concreteOutputType;
      if (this.outputLocals != null && i < this.outputLocals.length) {
        int abstractOutputType = this.outputLocals[i];
        if (abstractOutputType == 0) {

          
          concreteOutputType = this.inputLocals[i];
        } else {
          concreteOutputType = getConcreteOutputType(abstractOutputType, numStack);
        }
      
      } else {
        
        concreteOutputType = this.inputLocals[i];
      } 


      
      if (this.initializations != null) {
        concreteOutputType = getInitializedType(symbolTable, concreteOutputType);
      }
      frameChanged |= merge(symbolTable, concreteOutputType, dstFrame.inputLocals, i);
    } 





    
    if (catchTypeIndex > 0) {
      for (i = 0; i < numLocal; i++) {
        frameChanged |= merge(symbolTable, this.inputLocals[i], dstFrame.inputLocals, i);
      }
      if (dstFrame.inputStack == null) {
        dstFrame.inputStack = new int[1];
        frameChanged = true;
      } 
      frameChanged |= merge(symbolTable, catchTypeIndex, dstFrame.inputStack, 0);
      return frameChanged;
    } 



    
    int numInputStack = this.inputStack.length + this.outputStackStart;
    if (dstFrame.inputStack == null) {
      dstFrame.inputStack = new int[numInputStack + this.outputStackTop];
      frameChanged = true;
    } 
    
    int j;
    
    for (j = 0; j < numInputStack; j++) {
      int concreteOutputType = this.inputStack[j];
      if (this.initializations != null) {
        concreteOutputType = getInitializedType(symbolTable, concreteOutputType);
      }
      frameChanged |= merge(symbolTable, concreteOutputType, dstFrame.inputStack, j);
    } 

    
    for (j = 0; j < this.outputStackTop; j++) {
      int abstractOutputType = this.outputStack[j];
      int concreteOutputType = getConcreteOutputType(abstractOutputType, numStack);
      if (this.initializations != null) {
        concreteOutputType = getInitializedType(symbolTable, concreteOutputType);
      }
      frameChanged |= 
        merge(symbolTable, concreteOutputType, dstFrame.inputStack, numInputStack + j);
    } 
    return frameChanged;
  }


















  
  private static boolean merge(SymbolTable symbolTable, int sourceType, int[] dstTypes, int dstIndex) {
    int mergedType, dstType = dstTypes[dstIndex];
    if (dstType == sourceType)
    {
      return false;
    }
    int srcType = sourceType;
    if ((sourceType & 0x3FFFFFF) == 4194309) {
      if (dstType == 4194309) {
        return false;
      }
      srcType = 4194309;
    } 
    if (dstType == 0) {
      
      dstTypes[dstIndex] = srcType;
      return true;
    } 
    
    if ((dstType & 0xFC000000) != 0 || (dstType & 0x3C00000) == 8388608) {
      
      if (srcType == 4194309)
      {
        return false; } 
      if ((srcType & 0xFFC00000) == (dstType & 0xFFC00000)) {
        
        if ((dstType & 0x3C00000) == 8388608) {




          
          mergedType = srcType & 0xFC000000 | 0x800000 | symbolTable.addMergedType(srcType & 0xFFFFF, dstType & 0xFFFFF);
        }
        else {
          
          int mergedDim = -67108864 + (srcType & 0xFC000000);
          mergedType = mergedDim | 0x800000 | symbolTable.addType("java/lang/Object");
        } 
      } else if ((srcType & 0xFC000000) != 0 || (srcType & 0x3C00000) == 8388608) {



        
        int srcDim = srcType & 0xFC000000;
        if (srcDim != 0 && (srcType & 0x3C00000) != 8388608) {
          srcDim = -67108864 + srcDim;
        }
        int dstDim = dstType & 0xFC000000;
        if (dstDim != 0 && (dstType & 0x3C00000) != 8388608) {
          dstDim = -67108864 + dstDim;
        }
        
        mergedType = Math.min(srcDim, dstDim) | 0x800000 | symbolTable.addType("java/lang/Object");
      } else {
        
        mergedType = 4194304;
      } 
    } else if (dstType == 4194309) {

      
      mergedType = ((srcType & 0xFC000000) != 0 || (srcType & 0x3C00000) == 8388608) ? srcType : 4194304;
    }
    else {
      
      mergedType = 4194304;
    } 
    if (mergedType != dstType) {
      dstTypes[dstIndex] = mergedType;
      return true;
    } 
    return false;
  }














  
  final void accept(MethodWriter methodWriter) {
    int[] localTypes = this.inputLocals;
    int numLocal = 0;
    int numTrailingTop = 0;
    int i = 0;
    while (i < localTypes.length) {
      int localType = localTypes[i];
      i += (localType == 4194308 || localType == 4194307) ? 2 : 1;
      if (localType == 4194304) {
        numTrailingTop++; continue;
      } 
      numLocal += numTrailingTop + 1;
      numTrailingTop = 0;
    } 

    
    int[] stackTypes = this.inputStack;
    int numStack = 0;
    i = 0;
    while (i < stackTypes.length) {
      int stackType = stackTypes[i];
      i += (stackType == 4194308 || stackType == 4194307) ? 2 : 1;
      numStack++;
    } 
    
    int frameIndex = methodWriter.visitFrameStart(this.owner.bytecodeOffset, numLocal, numStack);
    i = 0;
    while (numLocal-- > 0) {
      int localType = localTypes[i];
      i += (localType == 4194308 || localType == 4194307) ? 2 : 1;
      methodWriter.visitAbstractType(frameIndex++, localType);
    } 
    i = 0;
    while (numStack-- > 0) {
      int stackType = stackTypes[i];
      i += (stackType == 4194308 || stackType == 4194307) ? 2 : 1;
      methodWriter.visitAbstractType(frameIndex++, stackType);
    } 
    methodWriter.visitFrameEnd();
  }












  
  static void putAbstractType(SymbolTable symbolTable, int abstractType, ByteVector output) {
    int arrayDimensions = (abstractType & 0xFC000000) >> 26;
    if (arrayDimensions == 0) {
      int typeValue = abstractType & 0xFFFFF;
      switch (abstractType & 0x3C00000) {
        case 4194304:
          output.putByte(typeValue);
          return;
        case 8388608:
          output
            .putByte(7)
            .putShort((symbolTable.addConstantClass((symbolTable.getType(typeValue)).value)).index);
          return;
        case 12582912:
          output.putByte(8).putShort((int)(symbolTable.getType(typeValue)).data);
          return;
      } 
      throw new AssertionError();
    } 

    
    StringBuilder typeDescriptor = new StringBuilder(32);
    while (arrayDimensions-- > 0) {
      typeDescriptor.append('[');
    }
    if ((abstractType & 0x3C00000) == 8388608) {
      typeDescriptor
        .append('L')
        .append((symbolTable.getType(abstractType & 0xFFFFF)).value)
        .append(';');
    } else {
      switch (abstractType & 0xFFFFF) {
        case 9:
          typeDescriptor.append('Z');
          break;
        case 10:
          typeDescriptor.append('B');
          break;
        case 11:
          typeDescriptor.append('C');
          break;
        case 12:
          typeDescriptor.append('S');
          break;
        case 1:
          typeDescriptor.append('I');
          break;
        case 2:
          typeDescriptor.append('F');
          break;
        case 4:
          typeDescriptor.append('J');
          break;
        case 3:
          typeDescriptor.append('D');
          break;
        default:
          throw new AssertionError();
      } 
    } 
    output
      .putByte(7)
      .putShort((symbolTable.addConstantClass(typeDescriptor.toString())).index);
  }
}
