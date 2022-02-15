package javassist.bytecode;

import java.io.PrintStream;
import javassist.CtMethod;





















public class InstructionPrinter
  implements Opcode
{
  private static final String[] opcodes = Mnemonic.OPCODE;

  
  private final PrintStream stream;

  
  public InstructionPrinter(PrintStream stream) {
    this.stream = stream;
  }



  
  public static void print(CtMethod method, PrintStream stream) {
    (new InstructionPrinter(stream)).print(method);
  }



  
  public void print(CtMethod method) {
    MethodInfo info = method.getMethodInfo2();
    ConstPool pool = info.getConstPool();
    CodeAttribute code = info.getCodeAttribute();
    if (code == null) {
      return;
    }
    CodeIterator iterator = code.iterator();
    while (iterator.hasNext()) {
      int pos;
      try {
        pos = iterator.next();
      } catch (BadBytecode e) {
        throw new RuntimeException(e);
      } 
      
      this.stream.println(pos + ": " + instructionString(iterator, pos, pool));
    } 
  }




  
  public static String instructionString(CodeIterator iter, int pos, ConstPool pool) {
    int opcode = iter.byteAt(pos);
    
    if (opcode > opcodes.length || opcode < 0) {
      throw new IllegalArgumentException("Invalid opcode, opcode: " + opcode + " pos: " + pos);
    }
    String opstring = opcodes[opcode];
    switch (opcode) {
      case 16:
        return opstring + " " + iter.byteAt(pos + 1);
      case 17:
        return opstring + " " + iter.s16bitAt(pos + 1);
      case 18:
        return opstring + " " + ldc(pool, iter.byteAt(pos + 1));
      case 19:
      case 20:
        return opstring + " " + ldc(pool, iter.u16bitAt(pos + 1));
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
        return opstring + " " + iter.byteAt(pos + 1);
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 198:
      case 199:
        return opstring + " " + (iter.s16bitAt(pos + 1) + pos);
      case 132:
        return opstring + " " + iter.byteAt(pos + 1) + ", " + iter.signedByteAt(pos + 2);
      case 167:
      case 168:
        return opstring + " " + (iter.s16bitAt(pos + 1) + pos);
      case 169:
        return opstring + " " + iter.byteAt(pos + 1);
      case 170:
        return tableSwitch(iter, pos);
      case 171:
        return lookupSwitch(iter, pos);
      case 178:
      case 179:
      case 180:
      case 181:
        return opstring + " " + fieldInfo(pool, iter.u16bitAt(pos + 1));
      case 182:
      case 183:
      case 184:
        return opstring + " " + methodInfo(pool, iter.u16bitAt(pos + 1));
      case 185:
        return opstring + " " + interfaceMethodInfo(pool, iter.u16bitAt(pos + 1));
      case 186:
        return opstring + " " + iter.u16bitAt(pos + 1);
      case 187:
        return opstring + " " + classInfo(pool, iter.u16bitAt(pos + 1));
      case 188:
        return opstring + " " + arrayInfo(iter.byteAt(pos + 1));
      case 189:
      case 192:
        return opstring + " " + classInfo(pool, iter.u16bitAt(pos + 1));
      case 196:
        return wide(iter, pos);
      case 197:
        return opstring + " " + classInfo(pool, iter.u16bitAt(pos + 1));
      case 200:
      case 201:
        return opstring + " " + (iter.s32bitAt(pos + 1) + pos);
    } 
    return opstring;
  }


  
  private static String wide(CodeIterator iter, int pos) {
    int opcode = iter.byteAt(pos + 1);
    int index = iter.u16bitAt(pos + 2);
    switch (opcode) {
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 132:
      case 169:
        return opcodes[opcode] + " " + index;
    } 
    throw new RuntimeException("Invalid WIDE operand");
  }


  
  private static String arrayInfo(int type) {
    switch (type) {
      case 4:
        return "boolean";
      case 5:
        return "char";
      case 8:
        return "byte";
      case 9:
        return "short";
      case 10:
        return "int";
      case 11:
        return "long";
      case 6:
        return "float";
      case 7:
        return "double";
    } 
    throw new RuntimeException("Invalid array type");
  }


  
  private static String classInfo(ConstPool pool, int index) {
    return "#" + index + " = Class " + pool.getClassInfo(index);
  }

  
  private static String interfaceMethodInfo(ConstPool pool, int index) {
    return "#" + index + " = Method " + pool
      .getInterfaceMethodrefClassName(index) + "." + pool
      .getInterfaceMethodrefName(index) + "(" + pool
      .getInterfaceMethodrefType(index) + ")";
  }
  
  private static String methodInfo(ConstPool pool, int index) {
    return "#" + index + " = Method " + pool
      .getMethodrefClassName(index) + "." + pool
      .getMethodrefName(index) + "(" + pool
      .getMethodrefType(index) + ")";
  }

  
  private static String fieldInfo(ConstPool pool, int index) {
    return "#" + index + " = Field " + pool
      .getFieldrefClassName(index) + "." + pool
      .getFieldrefName(index) + "(" + pool
      .getFieldrefType(index) + ")";
  }

  
  private static String lookupSwitch(CodeIterator iter, int pos) {
    StringBuffer buffer = new StringBuffer("lookupswitch {\n");
    int index = (pos & 0xFFFFFFFC) + 4;
    
    buffer.append("\t\tdefault: ").append(pos + iter.s32bitAt(index)).append("\n");
    index += 4; int npairs = iter.s32bitAt(index);
    index += 4; int end = npairs * 8 + index;
    
    for (; index < end; index += 8) {
      int match = iter.s32bitAt(index);
      int target = iter.s32bitAt(index + 4) + pos;
      buffer.append("\t\t").append(match).append(": ").append(target).append("\n");
    } 
    
    buffer.setCharAt(buffer.length() - 1, '}');
    return buffer.toString();
  }

  
  private static String tableSwitch(CodeIterator iter, int pos) {
    StringBuffer buffer = new StringBuffer("tableswitch {\n");
    int index = (pos & 0xFFFFFFFC) + 4;
    
    buffer.append("\t\tdefault: ").append(pos + iter.s32bitAt(index)).append("\n");
    index += 4; int low = iter.s32bitAt(index);
    index += 4; int high = iter.s32bitAt(index);
    index += 4; int end = (high - low + 1) * 4 + index;

    
    for (int key = low; index < end; index += 4, key++) {
      int target = iter.s32bitAt(index) + pos;
      buffer.append("\t\t").append(key).append(": ").append(target).append("\n");
    } 
    
    buffer.setCharAt(buffer.length() - 1, '}');
    return buffer.toString();
  }

  
  private static String ldc(ConstPool pool, int index) {
    int tag = pool.getTag(index);
    switch (tag) {
      case 8:
        return "#" + index + " = \"" + pool.getStringInfo(index) + "\"";
      case 3:
        return "#" + index + " = int " + pool.getIntegerInfo(index);
      case 4:
        return "#" + index + " = float " + pool.getFloatInfo(index);
      case 5:
        return "#" + index + " = long " + pool.getLongInfo(index);
      case 6:
        return "#" + index + " = double " + pool.getDoubleInfo(index);
      case 7:
        return classInfo(pool, index);
    } 
    throw new RuntimeException("bad LDC: " + tag);
  }
}
