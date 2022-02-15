package javassist.bytecode.stackmap;

import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ByteArray;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;
















































































public class MapMaker
  extends Tracer
{
  public static StackMapTable make(ClassPool classes, MethodInfo minfo) throws BadBytecode {
    TypedBlock[] blocks;
    CodeAttribute ca = minfo.getCodeAttribute();
    if (ca == null) {
      return null;
    }
    
    try {
      blocks = TypedBlock.makeBlocks(minfo, ca, true);
    }
    catch (JsrBytecode e) {
      return null;
    } 
    
    if (blocks == null) {
      return null;
    }
    MapMaker mm = new MapMaker(classes, minfo, ca);
    try {
      mm.make(blocks, ca.getCode());
    }
    catch (BadBytecode bb) {
      throw new BadBytecode(minfo, bb);
    } 
    
    return mm.toStackMap(blocks);
  }






  
  public static StackMap make2(ClassPool classes, MethodInfo minfo) throws BadBytecode {
    TypedBlock[] blocks;
    CodeAttribute ca = minfo.getCodeAttribute();
    if (ca == null) {
      return null;
    }
    
    try {
      blocks = TypedBlock.makeBlocks(minfo, ca, true);
    }
    catch (JsrBytecode e) {
      return null;
    } 
    
    if (blocks == null) {
      return null;
    }
    MapMaker mm = new MapMaker(classes, minfo, ca);
    try {
      mm.make(blocks, ca.getCode());
    }
    catch (BadBytecode bb) {
      throw new BadBytecode(minfo, bb);
    } 
    return mm.toStackMap2(minfo.getConstPool(), blocks);
  }
  
  public MapMaker(ClassPool classes, MethodInfo minfo, CodeAttribute ca) {
    super(classes, minfo.getConstPool(), ca
        .getMaxStack(), ca.getMaxLocals(), 
        TypedBlock.getRetType(minfo.getDescriptor()));
  }
  protected MapMaker(MapMaker old) {
    super(old);
  }




  
  void make(TypedBlock[] blocks, byte[] code) throws BadBytecode {
    make(code, blocks[0]);
    findDeadCatchers(code, blocks);
    try {
      fixTypes(code, blocks);
    } catch (NotFoundException e) {
      throw new BadBytecode("failed to resolve types", e);
    } 
  }




  
  private void make(byte[] code, TypedBlock tb) throws BadBytecode {
    copyTypeData(tb.stackTop, tb.stackTypes, this.stackTypes);
    this.stackTop = tb.stackTop;
    copyTypeData(tb.localsTypes.length, tb.localsTypes, this.localsTypes);
    
    traceException(code, tb.toCatch);
    
    int pos = tb.position;
    int end = pos + tb.length;
    while (pos < end) {
      pos += doOpcode(pos, code);
      traceException(code, tb.toCatch);
    } 
    
    if (tb.exit != null) {
      for (int i = 0; i < tb.exit.length; i++) {
        TypedBlock e = (TypedBlock)tb.exit[i];
        if (e.alreadySet()) {
          mergeMap(e, true);
        } else {
          recordStackMap(e);
          MapMaker maker = new MapMaker(this);
          maker.make(code, e);
        } 
      } 
    }
  }


  
  private void traceException(byte[] code, BasicBlock.Catch handler) throws BadBytecode {
    while (handler != null) {
      TypedBlock tb = (TypedBlock)handler.body;
      if (tb.alreadySet()) {
        mergeMap(tb, false);
        if (tb.stackTop < 1) {
          throw new BadBytecode("bad catch clause: " + handler.typeIndex);
        }
        tb.stackTypes[0] = merge(toExceptionType(handler.typeIndex), tb.stackTypes[0]);
      }
      else {
        
        recordStackMap(tb, handler.typeIndex);
        MapMaker maker = new MapMaker(this);
        maker.make(code, tb);
      } 
      
      handler = handler.next;
    } 
  }
  
  private void mergeMap(TypedBlock dest, boolean mergeStack) throws BadBytecode {
    int n = this.localsTypes.length; int i;
    for (i = 0; i < n; i++) {
      dest.localsTypes[i] = merge(validateTypeData(this.localsTypes, n, i), dest.localsTypes[i]);
    }
    
    if (mergeStack) {
      n = this.stackTop;
      for (i = 0; i < n; i++)
        dest.stackTypes[i] = merge(this.stackTypes[i], dest.stackTypes[i]); 
    } 
  }
  
  private TypeData merge(TypeData src, TypeData target) throws BadBytecode {
    if (src == target)
      return target; 
    if (target instanceof TypeData.ClassName || target instanceof TypeData.BasicType)
    {
      return target; } 
    if (target instanceof TypeData.AbsTypeVar) {
      ((TypeData.AbsTypeVar)target).merge(src);
      return target;
    } 
    
    throw new RuntimeException("fatal: this should never happen");
  }


  
  private void recordStackMap(TypedBlock target) throws BadBytecode {
    TypeData[] tStackTypes = TypeData.make(this.stackTypes.length);
    int st = this.stackTop;
    recordTypeData(st, this.stackTypes, tStackTypes);
    recordStackMap0(target, st, tStackTypes);
  }


  
  private void recordStackMap(TypedBlock target, int exceptionType) throws BadBytecode {
    TypeData[] tStackTypes = TypeData.make(this.stackTypes.length);
    tStackTypes[0] = toExceptionType(exceptionType).join();
    recordStackMap0(target, 1, tStackTypes);
  }
  
  private TypeData.ClassName toExceptionType(int exceptionType) {
    String type;
    if (exceptionType == 0) {
      type = "java.lang.Throwable";
    } else {
      type = this.cpool.getClassInfo(exceptionType);
    } 
    return new TypeData.ClassName(type);
  }


  
  private void recordStackMap0(TypedBlock target, int st, TypeData[] tStackTypes) throws BadBytecode {
    int n = this.localsTypes.length;
    TypeData[] tLocalsTypes = TypeData.make(n);
    int k = recordTypeData(n, this.localsTypes, tLocalsTypes);
    target.setStackMap(st, tStackTypes, k, tLocalsTypes);
  }
  
  protected static int recordTypeData(int n, TypeData[] srcTypes, TypeData[] destTypes) {
    int k = -1;
    for (int i = 0; i < n; i++) {
      TypeData t = validateTypeData(srcTypes, n, i);
      destTypes[i] = t.join();
      if (t != TOP) {
        k = i + 1;
      }
    } 
    return k + 1;
  }
  
  protected static void copyTypeData(int n, TypeData[] srcTypes, TypeData[] destTypes) {
    for (int i = 0; i < n; i++)
      destTypes[i] = srcTypes[i]; 
  }
  
  private static TypeData validateTypeData(TypeData[] data, int length, int index) {
    TypeData td = data[index];
    if (td.is2WordType() && index + 1 < length && 
      data[index + 1] != TOP) {
      return TOP;
    }
    return td;
  }








  
  private void findDeadCatchers(byte[] code, TypedBlock[] blocks) throws BadBytecode {
    int len = blocks.length;
    for (int i = 0; i < len; i++) {
      TypedBlock block = blocks[i];
      if (!block.alreadySet()) {
        fixDeadcode(code, block);
        BasicBlock.Catch handler = block.toCatch;
        if (handler != null) {
          TypedBlock tb = (TypedBlock)handler.body;
          if (!tb.alreadySet()) {

            
            recordStackMap(tb, handler.typeIndex);
            fixDeadcode(code, tb);
            tb.incoming = 1;
          } 
        } 
      } 
    } 
  }

  
  private void fixDeadcode(byte[] code, TypedBlock block) throws BadBytecode {
    int pos = block.position;
    int len = block.length - 3;
    if (len < 0) {
      
      if (len == -1) {
        code[pos] = 0;
      }
      code[pos + block.length - 1] = -65;
      block.incoming = 1;
      recordStackMap(block, 0);

      
      return;
    } 
    
    block.incoming = 0;
    
    for (int k = 0; k < len; k++) {
      code[pos + k] = 0;
    }
    code[pos + len] = -89;
    ByteArray.write16bit(-len, code, pos + len + 1);
  }









  
  private void fixTypes(byte[] code, TypedBlock[] blocks) throws NotFoundException, BadBytecode {
    List<TypeData> preOrder = new ArrayList<>();
    int len = blocks.length;
    int index = 0;
    for (int i = 0; i < len; i++) {
      TypedBlock block = blocks[i];
      if (block.alreadySet()) {
        int n = block.localsTypes.length; int j;
        for (j = 0; j < n; j++) {
          index = block.localsTypes[j].dfs(preOrder, index, this.classPool);
        }
        n = block.stackTop;
        for (j = 0; j < n; j++) {
          index = block.stackTypes[j].dfs(preOrder, index, this.classPool);
        }
      } 
    } 
  }

  
  public StackMapTable toStackMap(TypedBlock[] blocks) {
    StackMapTable.Writer writer = new StackMapTable.Writer(32);
    int n = blocks.length;
    TypedBlock prev = blocks[0];
    int offsetDelta = prev.length;
    if (prev.incoming > 0) {
      writer.sameFrame(0);
      offsetDelta--;
    } 
    
    for (int i = 1; i < n; i++) {
      TypedBlock bb = blocks[i];
      if (isTarget(bb, blocks[i - 1])) {
        bb.resetNumLocals();
        int diffL = stackMapDiff(prev.numLocals, prev.localsTypes, bb.numLocals, bb.localsTypes);
        
        toStackMapBody(writer, bb, diffL, offsetDelta, prev);
        offsetDelta = bb.length - 1;
        prev = bb;
      }
      else if (bb.incoming == 0) {
        
        writer.sameFrame(offsetDelta);
        offsetDelta = bb.length - 1;
      } else {
        
        offsetDelta += bb.length;
      } 
    } 
    return writer.toStackMapTable(this.cpool);
  }



  
  private boolean isTarget(TypedBlock cur, TypedBlock prev) {
    int in = cur.incoming;
    if (in > 1)
      return true; 
    if (in < 1) {
      return false;
    }
    return prev.stop;
  }




  
  private void toStackMapBody(StackMapTable.Writer writer, TypedBlock bb, int diffL, int offsetDelta, TypedBlock prev) {
    int stackTop = bb.stackTop;
    if (stackTop == 0) {
      if (diffL == 0) {
        writer.sameFrame(offsetDelta);
        return;
      } 
      if (0 > diffL && diffL >= -3) {
        writer.chopFrame(offsetDelta, -diffL);
        return;
      } 
      if (0 < diffL && diffL <= 3) {
        int[] data = new int[diffL];
        int[] tags = fillStackMap(bb.numLocals - prev.numLocals, prev.numLocals, data, bb.localsTypes);

        
        writer.appendFrame(offsetDelta, tags, data);
        return;
      } 
    } else {
      if (stackTop == 1 && diffL == 0) {
        TypeData td = bb.stackTypes[0];
        writer.sameLocals(offsetDelta, td.getTypeTag(), td.getTypeData(this.cpool));
        return;
      } 
      if (stackTop == 2 && diffL == 0) {
        TypeData td = bb.stackTypes[0];
        if (td.is2WordType()) {
          
          writer.sameLocals(offsetDelta, td.getTypeTag(), td.getTypeData(this.cpool));
          return;
        } 
      } 
    } 
    int[] sdata = new int[stackTop];
    int[] stags = fillStackMap(stackTop, 0, sdata, bb.stackTypes);
    int[] ldata = new int[bb.numLocals];
    int[] ltags = fillStackMap(bb.numLocals, 0, ldata, bb.localsTypes);
    writer.fullFrame(offsetDelta, ltags, ldata, stags, sdata);
  }
  
  private int[] fillStackMap(int num, int offset, int[] data, TypeData[] types) {
    int realNum = diffSize(types, offset, offset + num);
    ConstPool cp = this.cpool;
    int[] tags = new int[realNum];
    int j = 0;
    for (int i = 0; i < num; i++) {
      TypeData td = types[offset + i];
      tags[j] = td.getTypeTag();
      data[j] = td.getTypeData(cp);
      if (td.is2WordType()) {
        i++;
      }
      j++;
    } 
    
    return tags;
  }


  
  private static int stackMapDiff(int oldTdLen, TypeData[] oldTd, int newTdLen, TypeData[] newTd) {
    int len, diff = newTdLen - oldTdLen;
    
    if (diff > 0) {
      len = oldTdLen;
    } else {
      len = newTdLen;
    } 
    if (stackMapEq(oldTd, newTd, len)) {
      if (diff > 0) {
        return diffSize(newTd, len, newTdLen);
      }
      return -diffSize(oldTd, len, oldTdLen);
    }  return -100;
  }
  
  private static boolean stackMapEq(TypeData[] oldTd, TypeData[] newTd, int len) {
    for (int i = 0; i < len; i++) {
      if (!oldTd[i].eq(newTd[i])) {
        return false;
      }
    } 
    return true;
  }
  
  private static int diffSize(TypeData[] types, int offset, int len) {
    int num = 0;
    while (offset < len) {
      TypeData td = types[offset++];
      num++;
      if (td.is2WordType()) {
        offset++;
      }
    } 
    return num;
  }


  
  public StackMap toStackMap2(ConstPool cp, TypedBlock[] blocks) {
    StackMap.Writer writer = new StackMap.Writer();
    int n = blocks.length;
    boolean[] effective = new boolean[n];
    TypedBlock prev = blocks[0];

    
    effective[0] = (prev.incoming > 0);
    
    int num = effective[0] ? 1 : 0; int i;
    for (i = 1; i < n; i++) {
      TypedBlock bb = blocks[i];
      effective[i] = isTarget(bb, blocks[i - 1]); if (isTarget(bb, blocks[i - 1])) {
        bb.resetNumLocals();
        prev = bb;
        num++;
      } 
    } 
    
    if (num == 0) {
      return null;
    }
    writer.write16bit(num);
    for (i = 0; i < n; i++) {
      if (effective[i])
        writeStackFrame(writer, cp, (blocks[i]).position, blocks[i]); 
    } 
    return writer.toStackMap(cp);
  }
  
  private void writeStackFrame(StackMap.Writer writer, ConstPool cp, int offset, TypedBlock tb) {
    writer.write16bit(offset);
    writeVerifyTypeInfo(writer, cp, tb.localsTypes, tb.numLocals);
    writeVerifyTypeInfo(writer, cp, tb.stackTypes, tb.stackTop);
  }
  
  private void writeVerifyTypeInfo(StackMap.Writer writer, ConstPool cp, TypeData[] types, int num) {
    int numDWord = 0; int i;
    for (i = 0; i < num; i++) {
      TypeData td = types[i];
      if (td != null && td.is2WordType()) {
        numDWord++;
        i++;
      } 
    } 
    
    writer.write16bit(num - numDWord);
    for (i = 0; i < num; i++) {
      TypeData td = types[i];
      writer.writeVerifyTypeInfo(td.getTypeTag(), td.getTypeData(cp));
      if (td.is2WordType())
        i++; 
    } 
  }
}
