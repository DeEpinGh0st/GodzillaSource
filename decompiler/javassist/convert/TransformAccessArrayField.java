package javassist.convert;

import javassist.CannotCompileException;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;






















public final class TransformAccessArrayField
  extends Transformer
{
  private final String methodClassname;
  private final CodeConverter.ArrayAccessReplacementMethodNames names;
  private Frame[] frames;
  private int offset;
  
  public TransformAccessArrayField(Transformer next, String methodClassname, CodeConverter.ArrayAccessReplacementMethodNames names) throws NotFoundException {
    super(next);
    this.methodClassname = methodClassname;
    this.names = names;
  }












  
  public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo) throws CannotCompileException {
    CodeIterator iterator = minfo.getCodeAttribute().iterator();
    while (iterator.hasNext()) {
      try {
        int pos = iterator.next();
        int c = iterator.byteAt(pos);
        
        if (c == 50) {
          initFrames(clazz, minfo);
        }
        if (c == 50 || c == 51 || c == 52 || c == 49 || c == 48 || c == 46 || c == 47 || c == 53) {

          
          pos = replace(cp, iterator, pos, c, getLoadReplacementSignature(c)); continue;
        }  if (c == 83 || c == 84 || c == 85 || c == 82 || c == 81 || c == 79 || c == 80 || c == 86)
        {
          
          pos = replace(cp, iterator, pos, c, getStoreReplacementSignature(c));
        }
      }
      catch (Exception e) {
        throw new CannotCompileException(e);
      } 
    } 
  }

  
  public void clean() {
    this.frames = null;
    this.offset = -1;
  }



  
  public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp) throws BadBytecode {
    return pos;
  }
  
  private Frame getFrame(int pos) throws BadBytecode {
    return this.frames[pos - this.offset];
  }
  
  private void initFrames(CtClass clazz, MethodInfo minfo) throws BadBytecode {
    if (this.frames == null) {
      this.frames = (new Analyzer()).analyze(clazz, minfo);
      this.offset = 0;
    } 
  }
  
  private int updatePos(int pos, int increment) {
    if (this.offset > -1) {
      this.offset += increment;
    }
    return pos + increment;
  }
  
  private String getTopType(int pos) throws BadBytecode {
    Frame frame = getFrame(pos);
    if (frame == null) {
      return null;
    }
    CtClass clazz = frame.peek().getCtClass();
    return (clazz != null) ? Descriptor.toJvmName(clazz) : null;
  }

  
  private int replace(ConstPool cp, CodeIterator iterator, int pos, int opcode, String signature) throws BadBytecode {
    String castType = null;
    String methodName = getMethodName(opcode);
    if (methodName != null) {
      
      if (opcode == 50) {
        castType = getTopType(iterator.lookAhead());


        
        if (castType == null)
          return pos; 
        if ("java/lang/Object".equals(castType)) {
          castType = null;
        }
      } 

      
      iterator.writeByte(0, pos);
      
      CodeIterator.Gap gap = iterator.insertGapAt(pos, (castType != null) ? 5 : 2, false);
      pos = gap.position;
      int mi = cp.addClassInfo(this.methodClassname);
      int methodref = cp.addMethodrefInfo(mi, methodName, signature);
      iterator.writeByte(184, pos);
      iterator.write16bit(methodref, pos + 1);
      
      if (castType != null) {
        int index = cp.addClassInfo(castType);
        iterator.writeByte(192, pos + 3);
        iterator.write16bit(index, pos + 4);
      } 
      
      pos = updatePos(pos, gap.length);
    } 
    
    return pos;
  }
  
  private String getMethodName(int opcode) {
    String methodName = null;
    switch (opcode) {
      case 50:
        methodName = this.names.objectRead();
        break;
      case 51:
        methodName = this.names.byteOrBooleanRead();
        break;
      case 52:
        methodName = this.names.charRead();
        break;
      case 49:
        methodName = this.names.doubleRead();
        break;
      case 48:
        methodName = this.names.floatRead();
        break;
      case 46:
        methodName = this.names.intRead();
        break;
      case 53:
        methodName = this.names.shortRead();
        break;
      case 47:
        methodName = this.names.longRead();
        break;
      case 83:
        methodName = this.names.objectWrite();
        break;
      case 84:
        methodName = this.names.byteOrBooleanWrite();
        break;
      case 85:
        methodName = this.names.charWrite();
        break;
      case 82:
        methodName = this.names.doubleWrite();
        break;
      case 81:
        methodName = this.names.floatWrite();
        break;
      case 79:
        methodName = this.names.intWrite();
        break;
      case 86:
        methodName = this.names.shortWrite();
        break;
      case 80:
        methodName = this.names.longWrite();
        break;
    } 
    
    if (methodName.equals("")) {
      methodName = null;
    }
    return methodName;
  }
  
  private String getLoadReplacementSignature(int opcode) throws BadBytecode {
    switch (opcode) {
      case 50:
        return "(Ljava/lang/Object;I)Ljava/lang/Object;";
      case 51:
        return "(Ljava/lang/Object;I)B";
      case 52:
        return "(Ljava/lang/Object;I)C";
      case 49:
        return "(Ljava/lang/Object;I)D";
      case 48:
        return "(Ljava/lang/Object;I)F";
      case 46:
        return "(Ljava/lang/Object;I)I";
      case 53:
        return "(Ljava/lang/Object;I)S";
      case 47:
        return "(Ljava/lang/Object;I)J";
    } 
    
    throw new BadBytecode(opcode);
  }
  
  private String getStoreReplacementSignature(int opcode) throws BadBytecode {
    switch (opcode) {
      case 83:
        return "(Ljava/lang/Object;ILjava/lang/Object;)V";
      case 84:
        return "(Ljava/lang/Object;IB)V";
      case 85:
        return "(Ljava/lang/Object;IC)V";
      case 82:
        return "(Ljava/lang/Object;ID)V";
      case 81:
        return "(Ljava/lang/Object;IF)V";
      case 79:
        return "(Ljava/lang/Object;II)V";
      case 86:
        return "(Ljava/lang/Object;IS)V";
      case 80:
        return "(Ljava/lang/Object;IJ)V";
    } 
    
    throw new BadBytecode(opcode);
  }
}
