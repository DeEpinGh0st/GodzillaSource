package javassist.bytecode.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;





















public class SubroutineScanner
  implements Opcode
{
  private Subroutine[] subroutines;
  Map<Integer, Subroutine> subTable = new HashMap<>();
  Set<Integer> done = new HashSet<>();

  
  public Subroutine[] scan(MethodInfo method) throws BadBytecode {
    CodeAttribute code = method.getCodeAttribute();
    CodeIterator iter = code.iterator();
    
    this.subroutines = new Subroutine[code.getCodeLength()];
    this.subTable.clear();
    this.done.clear();
    
    scan(0, iter, null);
    
    ExceptionTable exceptions = code.getExceptionTable();
    for (int i = 0; i < exceptions.size(); i++) {
      int handler = exceptions.handlerPc(i);

      
      scan(handler, iter, this.subroutines[exceptions.startPc(i)]);
    } 
    
    return this.subroutines;
  }
  
  private void scan(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode {
    boolean next;
    if (this.done.contains(Integer.valueOf(pos))) {
      return;
    }
    this.done.add(Integer.valueOf(pos));
    
    int old = iter.lookAhead();
    iter.move(pos);

    
    do {
      pos = iter.next();
      next = (scanOp(pos, iter, sub) && iter.hasNext());
    } while (next);
    
    iter.move(old);
  }
  
  private boolean scanOp(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode {
    this.subroutines[pos] = sub;
    
    int opcode = iter.byteAt(pos);
    
    if (opcode == 170) {
      scanTableSwitch(pos, iter, sub);
      
      return false;
    } 
    
    if (opcode == 171) {
      scanLookupSwitch(pos, iter, sub);
      
      return false;
    } 

    
    if (Util.isReturn(opcode) || opcode == 169 || opcode == 191) {
      return false;
    }
    if (Util.isJumpInstruction(opcode)) {
      int target = Util.getJumpTarget(pos, iter);
      if (opcode == 168 || opcode == 201) {
        Subroutine s = this.subTable.get(Integer.valueOf(target));
        if (s == null) {
          s = new Subroutine(target, pos);
          this.subTable.put(Integer.valueOf(target), s);
          scan(target, iter, s);
        } else {
          s.addCaller(pos);
        } 
      } else {
        scan(target, iter, sub);

        
        if (Util.isGoto(opcode)) {
          return false;
        }
      } 
    } 
    return true;
  }
  
  private void scanLookupSwitch(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode {
    int index = (pos & 0xFFFFFFFC) + 4;
    
    scan(pos + iter.s32bitAt(index), iter, sub);
    index += 4; int npairs = iter.s32bitAt(index);
    index += 4; int end = npairs * 8 + index;

    
    for (index += 4; index < end; index += 8) {
      int target = iter.s32bitAt(index) + pos;
      scan(target, iter, sub);
    } 
  }

  
  private void scanTableSwitch(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode {
    int index = (pos & 0xFFFFFFFC) + 4;
    
    scan(pos + iter.s32bitAt(index), iter, sub);
    index += 4; int low = iter.s32bitAt(index);
    index += 4; int high = iter.s32bitAt(index);
    index += 4; int end = (high - low + 1) * 4 + index;

    
    for (; index < end; index += 4) {
      int target = iter.s32bitAt(index) + pos;
      scan(target, iter, sub);
    } 
  }
}
