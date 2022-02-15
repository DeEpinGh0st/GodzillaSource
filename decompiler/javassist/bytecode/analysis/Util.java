package javassist.bytecode.analysis;

import javassist.bytecode.CodeIterator;
import javassist.bytecode.Opcode;



















public class Util
  implements Opcode
{
  public static int getJumpTarget(int pos, CodeIterator iter) {
    int opcode = iter.byteAt(pos);
    pos += (opcode == 201 || opcode == 200) ? iter.s32bitAt(pos + 1) : iter.s16bitAt(pos + 1);
    return pos;
  }
  
  public static boolean isJumpInstruction(int opcode) {
    return ((opcode >= 153 && opcode <= 168) || opcode == 198 || opcode == 199 || opcode == 201 || opcode == 200);
  }
  
  public static boolean isGoto(int opcode) {
    return (opcode == 167 || opcode == 200);
  }
  
  public static boolean isJsr(int opcode) {
    return (opcode == 168 || opcode == 201);
  }
  
  public static boolean isReturn(int opcode) {
    return (opcode >= 172 && opcode <= 177);
  }
}
