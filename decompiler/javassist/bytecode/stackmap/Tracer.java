package javassist.bytecode.stackmap;

import javassist.ClassPool;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ByteArray;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;






















public abstract class Tracer
  implements TypeTag
{
  protected ClassPool classPool;
  protected ConstPool cpool;
  protected String returnType;
  protected int stackTop;
  protected TypeData[] stackTypes;
  protected TypeData[] localsTypes;
  
  public Tracer(ClassPool classes, ConstPool cp, int maxStack, int maxLocals, String retType) {
    this.classPool = classes;
    this.cpool = cp;
    this.returnType = retType;
    this.stackTop = 0;
    this.stackTypes = TypeData.make(maxStack);
    this.localsTypes = TypeData.make(maxLocals);
  }
  
  public Tracer(Tracer t) {
    this.classPool = t.classPool;
    this.cpool = t.cpool;
    this.returnType = t.returnType;
    this.stackTop = t.stackTop;
    this.stackTypes = TypeData.make(t.stackTypes.length);
    this.localsTypes = TypeData.make(t.localsTypes.length);
  }










  
  protected int doOpcode(int pos, byte[] code) throws BadBytecode {
    try {
      int op = code[pos] & 0xFF;
      if (op < 54)
        return doOpcode0_53(pos, code, op); 
      if (op < 96)
        return doOpcode54_95(pos, code, op); 
      if (op < 148)
        return doOpcode96_147(pos, code, op); 
      return doOpcode148_201(pos, code, op);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      throw new BadBytecode("inconsistent stack height " + e.getMessage(), e);
    } 
  }




  
  protected void visitBranch(int pos, byte[] code, int offset) throws BadBytecode {}



  
  protected void visitGoto(int pos, byte[] code, int offset) throws BadBytecode {}



  
  protected void visitReturn(int pos, byte[] code) throws BadBytecode {}



  
  protected void visitThrow(int pos, byte[] code) throws BadBytecode {}



  
  protected void visitTableSwitch(int pos, byte[] code, int n, int offsetPos, int defaultOffset) throws BadBytecode {}



  
  protected void visitLookupSwitch(int pos, byte[] code, int n, int pairsPos, int defaultOffset) throws BadBytecode {}



  
  protected void visitJSR(int pos, byte[] code) throws BadBytecode {}



  
  protected void visitRET(int pos, byte[] code) throws BadBytecode {}



  
  private int doOpcode0_53(int pos, byte[] code, int op) throws BadBytecode {
    int reg, s;
    TypeData data, stackTypes[] = this.stackTypes;
    switch (op)
    
    { 











































































































      
      case 0:
        return 1;case 1: stackTypes[this.stackTop++] = new TypeData.NullType();case 2: case 3: case 4: case 5: case 6: case 7: case 8: stackTypes[this.stackTop++] = INTEGER;case 9: case 10: stackTypes[this.stackTop++] = LONG; stackTypes[this.stackTop++] = TOP;case 11: case 12: case 13: stackTypes[this.stackTop++] = FLOAT;case 14: case 15: stackTypes[this.stackTop++] = DOUBLE; stackTypes[this.stackTop++] = TOP;case 16: case 17: stackTypes[this.stackTop++] = INTEGER; return (op == 17) ? 3 : 2;case 18: doLDC(code[pos + 1] & 0xFF); return 2;case 19: case 20: doLDC(ByteArray.readU16bit(code, pos + 1)); return 3;case 21: return doXLOAD(INTEGER, code, pos);case 22: return doXLOAD(LONG, code, pos);case 23: return doXLOAD(FLOAT, code, pos);case 24: return doXLOAD(DOUBLE, code, pos);case 25: return doALOAD(code[pos + 1] & 0xFF);case 26: case 27: case 28: case 29: stackTypes[this.stackTop++] = INTEGER;case 30: case 31: case 32: case 33: stackTypes[this.stackTop++] = LONG; stackTypes[this.stackTop++] = TOP;case 34: case 35: case 36: case 37: stackTypes[this.stackTop++] = FLOAT;case 38: case 39: case 40: case 41: stackTypes[this.stackTop++] = DOUBLE; stackTypes[this.stackTop++] = TOP;case 42: case 43: case 44: case 45: reg = op - 42; stackTypes[this.stackTop++] = this.localsTypes[reg];case 46: stackTypes[--this.stackTop - 1] = INTEGER;case 47: stackTypes[this.stackTop - 2] = LONG; stackTypes[this.stackTop - 1] = TOP;
      case 48: stackTypes[--this.stackTop - 1] = FLOAT;
      case 49: stackTypes[this.stackTop - 2] = DOUBLE; stackTypes[this.stackTop - 1] = TOP;
      case 50: s = --this.stackTop - 1; data = stackTypes[s]; stackTypes[s] = TypeData.ArrayElement.make(data);
      case 51: case 52: case 53: stackTypes[--this.stackTop - 1] = INTEGER; }  throw new RuntimeException("fatal"); } private void doLDC(int index) { TypeData[] stackTypes = this.stackTypes;
    int tag = this.cpool.getTag(index);
    if (tag == 8) {
      stackTypes[this.stackTop++] = new TypeData.ClassName("java.lang.String");
    } else if (tag == 3) {
      stackTypes[this.stackTop++] = INTEGER;
    } else if (tag == 4) {
      stackTypes[this.stackTop++] = FLOAT;
    } else if (tag == 5) {
      stackTypes[this.stackTop++] = LONG;
      stackTypes[this.stackTop++] = TOP;
    }
    else if (tag == 6) {
      stackTypes[this.stackTop++] = DOUBLE;
      stackTypes[this.stackTop++] = TOP;
    }
    else if (tag == 7) {
      stackTypes[this.stackTop++] = new TypeData.ClassName("java.lang.Class");
    } else if (tag == 17) {
      String desc = this.cpool.getDynamicType(index);
      pushMemberType(desc);
    } else {
      
      throw new RuntimeException("bad LDC: " + tag);
    }  }
  
  private int doXLOAD(TypeData type, byte[] code, int pos) {
    int localVar = code[pos + 1] & 0xFF;
    return doXLOAD(localVar, type);
  }
  
  private int doXLOAD(int localVar, TypeData type) {
    this.stackTypes[this.stackTop++] = type;
    if (type.is2WordType()) {
      this.stackTypes[this.stackTop++] = TOP;
    }
    return 2;
  }
  
  private int doALOAD(int localVar) {
    this.stackTypes[this.stackTop++] = this.localsTypes[localVar];
    return 2; } private int doOpcode54_95(int pos, byte[] code, int op) throws BadBytecode { int var; int i; int len;
    int sp;
    int j;
    TypeData t;
    switch (op) {
      case 54:
        return doXSTORE(pos, code, INTEGER);
      case 55:
        return doXSTORE(pos, code, LONG);
      case 56:
        return doXSTORE(pos, code, FLOAT);
      case 57:
        return doXSTORE(pos, code, DOUBLE);
      case 58:
        return doASTORE(code[pos + 1] & 0xFF);
      case 59:
      case 60:
      case 61:
      case 62:
        var = op - 59;
        this.localsTypes[var] = INTEGER;
        this.stackTop--;




























































































        
        return 1;case 63: case 64: case 65: case 66: var = op - 63; this.localsTypes[var] = LONG; this.localsTypes[var + 1] = TOP; this.stackTop -= 2; return 1;case 67: case 68: case 69: case 70: var = op - 67; this.localsTypes[var] = FLOAT; this.stackTop--; return 1;case 71: case 72: case 73: case 74: var = op - 71; this.localsTypes[var] = DOUBLE; this.localsTypes[var + 1] = TOP; this.stackTop -= 2; return 1;case 75: case 76: case 77: case 78: var = op - 75; doASTORE(var); return 1;case 79: case 80: case 81: case 82: this.stackTop -= (op == 80 || op == 82) ? 4 : 3; return 1;case 83: TypeData.ArrayElement.aastore(this.stackTypes[this.stackTop - 3], this.stackTypes[this.stackTop - 1], this.classPool); this.stackTop -= 3; return 1;case 84: case 85: case 86: this.stackTop -= 3; return 1;case 87: this.stackTop--; return 1;case 88: this.stackTop -= 2; return 1;case 89: i = this.stackTop; this.stackTypes[i] = this.stackTypes[i - 1]; this.stackTop = i + 1; return 1;case 90: case 91: len = op - 90 + 2; doDUP_XX(1, len); j = this.stackTop; this.stackTypes[j - len] = this.stackTypes[j]; this.stackTop = j + 1; return 1;case 92: doDUP_XX(2, 2); this.stackTop += 2; return 1;case 93: case 94: len = op - 93 + 3; doDUP_XX(2, len); j = this.stackTop; this.stackTypes[j - len] = this.stackTypes[j]; this.stackTypes[j - len + 1] = this.stackTypes[j + 1]; this.stackTop = j + 2; return 1;case 95: sp = this.stackTop - 1; t = this.stackTypes[sp]; this.stackTypes[sp] = this.stackTypes[sp - 1]; this.stackTypes[sp - 1] = t; return 1;
    } 
    throw new RuntimeException("fatal"); }
   private int doXSTORE(int pos, byte[] code, TypeData type) {
    int index = code[pos + 1] & 0xFF;
    return doXSTORE(index, type);
  }
  
  private int doXSTORE(int index, TypeData type) {
    this.stackTop--;
    this.localsTypes[index] = type;
    if (type.is2WordType()) {
      this.stackTop--;
      this.localsTypes[index + 1] = TOP;
    } 
    
    return 2;
  }
  
  private int doASTORE(int index) {
    this.stackTop--;
    
    this.localsTypes[index] = this.stackTypes[this.stackTop];
    return 2;
  }
  
  private void doDUP_XX(int delta, int len) {
    TypeData[] types = this.stackTypes;
    int sp = this.stackTop - 1;
    int end = sp - len;
    while (sp > end) {
      types[sp + delta] = types[sp];
      sp--;
    } 
  }
  
  private int doOpcode96_147(int pos, byte[] code, int op) {
    if (op <= 131) {
      this.stackTop += Opcode.STACK_GROW[op];
      return 1;
    } 
    
    switch (op) {
      
      case 132:
        return 3;
      case 133:
        this.stackTypes[this.stackTop - 1] = LONG;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      
      case 134:
        this.stackTypes[this.stackTop - 1] = FLOAT;
      
      case 135:
        this.stackTypes[this.stackTop - 1] = DOUBLE;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      
      case 136:
        this.stackTypes[--this.stackTop - 1] = INTEGER;
      
      case 137:
        this.stackTypes[--this.stackTop - 1] = FLOAT;
      
      case 138:
        this.stackTypes[this.stackTop - 2] = DOUBLE;
      
      case 139:
        this.stackTypes[this.stackTop - 1] = INTEGER;
      
      case 140:
        this.stackTypes[this.stackTop - 1] = LONG;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      
      case 141:
        this.stackTypes[this.stackTop - 1] = DOUBLE;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      
      case 142:
        this.stackTypes[--this.stackTop - 1] = INTEGER;
      
      case 143:
        this.stackTypes[this.stackTop - 2] = LONG;
      
      case 144:
        this.stackTypes[--this.stackTop - 1] = FLOAT;





      
      case 145:
      case 146:
      case 147:
        return 1;
    }  throw new RuntimeException("fatal"); } private int doOpcode148_201(int pos, byte[] code, int op) throws BadBytecode { int pos2; int i; int low; int n; String type;
    int high;
    int j;
    switch (op) {
      case 148:
        this.stackTypes[this.stackTop - 4] = INTEGER;
        this.stackTop -= 3;
        break;
      case 149:
      case 150:
        this.stackTypes[--this.stackTop - 1] = INTEGER;
        break;
      case 151:
      case 152:
        this.stackTypes[this.stackTop - 4] = INTEGER;
        this.stackTop -= 3;
        break;
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        this.stackTop--;
        visitBranch(pos, code, ByteArray.readS16bit(code, pos + 1));
        return 3;
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
        this.stackTop -= 2;
        visitBranch(pos, code, ByteArray.readS16bit(code, pos + 1));
        return 3;
      case 167:
        visitGoto(pos, code, ByteArray.readS16bit(code, pos + 1));
        return 3;
      case 168:
        visitJSR(pos, code);
        return 3;
      case 169:
        visitRET(pos, code);
        return 2;
      case 170:
        this.stackTop--;
        pos2 = (pos & 0xFFFFFFFC) + 8;
        low = ByteArray.read32bit(code, pos2);
        high = ByteArray.read32bit(code, pos2 + 4);
        j = high - low + 1;
        visitTableSwitch(pos, code, j, pos2 + 8, ByteArray.read32bit(code, pos2 - 4));
        return j * 4 + 16 - (pos & 0x3);
      case 171:
        this.stackTop--;
        pos2 = (pos & 0xFFFFFFFC) + 8;
        n = ByteArray.read32bit(code, pos2);
        visitLookupSwitch(pos, code, n, pos2 + 4, ByteArray.read32bit(code, pos2 - 4));
        return n * 8 + 12 - (pos & 0x3);
      case 172:
        this.stackTop--;
        visitReturn(pos, code);
        break;
      case 173:
        this.stackTop -= 2;
        visitReturn(pos, code);
        break;
      case 174:
        this.stackTop--;
        visitReturn(pos, code);
        break;
      case 175:
        this.stackTop -= 2;
        visitReturn(pos, code);
        break;
      case 176:
        this.stackTypes[--this.stackTop].setType(this.returnType, this.classPool);
        visitReturn(pos, code);
        break;
      case 177:
        visitReturn(pos, code);
        break;
      case 178:
        return doGetField(pos, code, false);
      case 179:
        return doPutField(pos, code, false);
      case 180:
        return doGetField(pos, code, true);
      case 181:
        return doPutField(pos, code, true);
      case 182:
      case 183:
        return doInvokeMethod(pos, code, true);
      case 184:
        return doInvokeMethod(pos, code, false);
      case 185:
        return doInvokeIntfMethod(pos, code);
      case 186:
        return doInvokeDynamic(pos, code);
      case 187:
        i = ByteArray.readU16bit(code, pos + 1);
        this.stackTypes[this.stackTop++] = new TypeData.UninitData(pos, this.cpool
            .getClassInfo(i));
        return 3;
      case 188:
        return doNEWARRAY(pos, code);
      case 189:
        i = ByteArray.readU16bit(code, pos + 1);
        type = this.cpool.getClassInfo(i).replace('.', '/');
        if (type.charAt(0) == '[') {
          type = "[" + type;
        } else {
          type = "[L" + type + ";";
        } 
        this.stackTypes[this.stackTop - 1] = new TypeData.ClassName(type);
        
        return 3;
      case 190:
        this.stackTypes[this.stackTop - 1].setType("[Ljava.lang.Object;", this.classPool);
        this.stackTypes[this.stackTop - 1] = INTEGER;
        break;
      case 191:
        this.stackTypes[--this.stackTop].setType("java.lang.Throwable", this.classPool);
        visitThrow(pos, code);
        break;
      
      case 192:
        i = ByteArray.readU16bit(code, pos + 1);
        type = this.cpool.getClassInfo(i);
        if (type.charAt(0) == '[') {
          type = type.replace('.', '/');
        }
        this.stackTypes[this.stackTop - 1] = new TypeData.ClassName(type);
        return 3;
      
      case 193:
        this.stackTypes[this.stackTop - 1] = INTEGER;
        return 3;
      case 194:
      case 195:
        this.stackTop--;
        break;
      
      case 196:
        return doWIDE(pos, code);
      case 197:
        return doMultiANewArray(pos, code);
      case 198:
      case 199:
        this.stackTop--;
        visitBranch(pos, code, ByteArray.readS16bit(code, pos + 1));
        return 3;
      case 200:
        visitGoto(pos, code, ByteArray.read32bit(code, pos + 1));
        return 5;
      case 201:
        visitJSR(pos, code);
        return 5;
    } 
    return 1; }

  
  private int doWIDE(int pos, byte[] code) throws BadBytecode {
    int index, op = code[pos + 1] & 0xFF;
    switch (op) {
      case 21:
        doWIDE_XLOAD(pos, code, INTEGER);







































        
        return 4;case 22: doWIDE_XLOAD(pos, code, LONG); return 4;case 23: doWIDE_XLOAD(pos, code, FLOAT); return 4;case 24: doWIDE_XLOAD(pos, code, DOUBLE); return 4;case 25: index = ByteArray.readU16bit(code, pos + 2); doALOAD(index); return 4;case 54: doWIDE_STORE(pos, code, INTEGER); return 4;case 55: doWIDE_STORE(pos, code, LONG); return 4;case 56: doWIDE_STORE(pos, code, FLOAT); return 4;case 57: doWIDE_STORE(pos, code, DOUBLE); return 4;case 58: index = ByteArray.readU16bit(code, pos + 2); doASTORE(index); return 4;case 132: return 6;case 169: visitRET(pos, code); return 4;
    } 
    throw new RuntimeException("bad WIDE instruction: " + op);
  } private void doWIDE_XLOAD(int pos, byte[] code, TypeData type) {
    int index = ByteArray.readU16bit(code, pos + 2);
    doXLOAD(index, type);
  }
  
  private void doWIDE_STORE(int pos, byte[] code, TypeData type) {
    int index = ByteArray.readU16bit(code, pos + 2);
    doXSTORE(index, type);
  }
  
  private int doPutField(int pos, byte[] code, boolean notStatic) throws BadBytecode {
    int index = ByteArray.readU16bit(code, pos + 1);
    String desc = this.cpool.getFieldrefType(index);
    this.stackTop -= Descriptor.dataSize(desc);
    char c = desc.charAt(0);
    if (c == 'L') {
      this.stackTypes[this.stackTop].setType(getFieldClassName(desc, 0), this.classPool);
    } else if (c == '[') {
      this.stackTypes[this.stackTop].setType(desc, this.classPool);
    } 
    setFieldTarget(notStatic, index);
    return 3;
  }
  
  private int doGetField(int pos, byte[] code, boolean notStatic) throws BadBytecode {
    int index = ByteArray.readU16bit(code, pos + 1);
    setFieldTarget(notStatic, index);
    String desc = this.cpool.getFieldrefType(index);
    pushMemberType(desc);
    return 3;
  }
  
  private void setFieldTarget(boolean notStatic, int index) throws BadBytecode {
    if (notStatic) {
      String className = this.cpool.getFieldrefClassName(index);
      this.stackTypes[--this.stackTop].setType(className, this.classPool);
    } 
  }
  private int doNEWARRAY(int pos, byte[] code) {
    String type;
    int s = this.stackTop - 1;
    
    switch (code[pos + 1] & 0xFF) {
      case 4:
        type = "[Z";

























        
        this.stackTypes[s] = new TypeData.ClassName(type);
        return 2;case 5: type = "[C"; this.stackTypes[s] = new TypeData.ClassName(type); return 2;case 6: type = "[F"; this.stackTypes[s] = new TypeData.ClassName(type); return 2;case 7: type = "[D"; this.stackTypes[s] = new TypeData.ClassName(type); return 2;case 8: type = "[B"; this.stackTypes[s] = new TypeData.ClassName(type); return 2;case 9: type = "[S"; this.stackTypes[s] = new TypeData.ClassName(type); return 2;case 10: type = "[I"; this.stackTypes[s] = new TypeData.ClassName(type); return 2;case 11: type = "[J"; this.stackTypes[s] = new TypeData.ClassName(type); return 2;
    } 
    throw new RuntimeException("bad newarray");
  } private int doMultiANewArray(int pos, byte[] code) {
    int i = ByteArray.readU16bit(code, pos + 1);
    int dim = code[pos + 3] & 0xFF;
    this.stackTop -= dim - 1;
    
    String type = this.cpool.getClassInfo(i).replace('.', '/');
    this.stackTypes[this.stackTop - 1] = new TypeData.ClassName(type);
    return 4;
  }
  
  private int doInvokeMethod(int pos, byte[] code, boolean notStatic) throws BadBytecode {
    int i = ByteArray.readU16bit(code, pos + 1);
    String desc = this.cpool.getMethodrefType(i);
    checkParamTypes(desc, 1);
    if (notStatic) {
      String className = this.cpool.getMethodrefClassName(i);
      TypeData target = this.stackTypes[--this.stackTop];
      if (target instanceof TypeData.UninitTypeVar && target.isUninit()) {
        constructorCalled(target, ((TypeData.UninitTypeVar)target).offset());
      } else if (target instanceof TypeData.UninitData) {
        constructorCalled(target, ((TypeData.UninitData)target).offset());
      } 
      target.setType(className, this.classPool);
    } 
    
    pushMemberType(desc);
    return 3;
  }





  
  private void constructorCalled(TypeData target, int offset) {
    target.constructorCalled(offset); int i;
    for (i = 0; i < this.stackTop; i++) {
      this.stackTypes[i].constructorCalled(offset);
    }
    for (i = 0; i < this.localsTypes.length; i++)
      this.localsTypes[i].constructorCalled(offset); 
  }
  
  private int doInvokeIntfMethod(int pos, byte[] code) throws BadBytecode {
    int i = ByteArray.readU16bit(code, pos + 1);
    String desc = this.cpool.getInterfaceMethodrefType(i);
    checkParamTypes(desc, 1);
    String className = this.cpool.getInterfaceMethodrefClassName(i);
    this.stackTypes[--this.stackTop].setType(className, this.classPool);
    pushMemberType(desc);
    return 5;
  }
  
  private int doInvokeDynamic(int pos, byte[] code) throws BadBytecode {
    int i = ByteArray.readU16bit(code, pos + 1);
    String desc = this.cpool.getInvokeDynamicType(i);
    checkParamTypes(desc, 1);






    
    pushMemberType(desc);
    return 5;
  }
  
  private void pushMemberType(String descriptor) {
    int top = 0;
    if (descriptor.charAt(0) == '(') {
      top = descriptor.indexOf(')') + 1;
      if (top < 1) {
        throw new IndexOutOfBoundsException("bad descriptor: " + descriptor);
      }
    } 
    
    TypeData[] types = this.stackTypes;
    int index = this.stackTop;
    switch (descriptor.charAt(top)) {
      case '[':
        types[index] = new TypeData.ClassName(descriptor.substring(top));
        break;
      case 'L':
        types[index] = new TypeData.ClassName(getFieldClassName(descriptor, top));
        break;
      case 'J':
        types[index] = LONG;
        types[index + 1] = TOP;
        this.stackTop += 2;
        return;
      case 'F':
        types[index] = FLOAT;
        break;
      case 'D':
        types[index] = DOUBLE;
        types[index + 1] = TOP;
        this.stackTop += 2;
        return;
      case 'V':
        return;
      default:
        types[index] = INTEGER;
        break;
    } 
    
    this.stackTop++;
  }
  
  private static String getFieldClassName(String desc, int index) {
    return desc.substring(index + 1, desc.length() - 1).replace('/', '.');
  }
  
  private void checkParamTypes(String desc, int i) throws BadBytecode {
    char c = desc.charAt(i);
    if (c == ')') {
      return;
    }
    int k = i;
    boolean array = false;
    while (c == '[') {
      array = true;
      c = desc.charAt(++k);
    } 
    
    if (c == 'L') {
      k = desc.indexOf(';', k) + 1;
      if (k <= 0) {
        throw new IndexOutOfBoundsException("bad descriptor");
      }
    } else {
      k++;
    } 
    checkParamTypes(desc, k);
    if (!array && (c == 'J' || c == 'D')) {
      this.stackTop -= 2;
    } else {
      this.stackTop--;
    } 
    if (array) {
      this.stackTypes[this.stackTop].setType(desc.substring(i, k), this.classPool);
    } else if (c == 'L') {
      this.stackTypes[this.stackTop].setType(desc.substring(i + 1, k - 1).replace('/', '.'), this.classPool);
    } 
  }
}
