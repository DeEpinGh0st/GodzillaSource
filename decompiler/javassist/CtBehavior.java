package javassist;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.LocalVariableTypeAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.expr.ExprEditor;



























public abstract class CtBehavior
  extends CtMember
{
  protected MethodInfo methodInfo;
  
  protected CtBehavior(CtClass clazz, MethodInfo minfo) {
    super(clazz);
    this.methodInfo = minfo;
  }





  
  void copy(CtBehavior src, boolean isCons, ClassMap map) throws CannotCompileException {
    CtClass declaring = this.declaringClass;
    MethodInfo srcInfo = src.methodInfo;
    CtClass srcClass = src.getDeclaringClass();
    ConstPool cp = declaring.getClassFile2().getConstPool();
    
    map = new ClassMap(map);
    map.put(srcClass.getName(), declaring.getName());
    try {
      boolean patch = false;
      CtClass srcSuper = srcClass.getSuperclass();
      CtClass destSuper = declaring.getSuperclass();
      String destSuperName = null;
      if (srcSuper != null && destSuper != null) {
        String srcSuperName = srcSuper.getName();
        destSuperName = destSuper.getName();
        if (!srcSuperName.equals(destSuperName)) {
          if (srcSuperName.equals("java.lang.Object")) {
            patch = true;
          } else {
            map.putIfNone(srcSuperName, destSuperName);
          } 
        }
      } 
      this.methodInfo = new MethodInfo(cp, srcInfo.getName(), srcInfo, map);
      if (isCons && patch) {
        this.methodInfo.setSuperclass(destSuperName);
      }
    } catch (NotFoundException e) {
      throw new CannotCompileException(e);
    }
    catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
  }

  
  protected void extendToString(StringBuffer buffer) {
    buffer.append(' ');
    buffer.append(getName());
    buffer.append(' ');
    buffer.append(this.methodInfo.getDescriptor());
  }









  
  public abstract String getLongName();








  
  public MethodInfo getMethodInfo() {
    this.declaringClass.checkModify();
    return this.methodInfo;
  }


















  
  public MethodInfo getMethodInfo2() {
    return this.methodInfo;
  }







  
  public int getModifiers() {
    return AccessFlag.toModifier(this.methodInfo.getAccessFlags());
  }










  
  public void setModifiers(int mod) {
    this.declaringClass.checkModify();
    this.methodInfo.setAccessFlags(AccessFlag.of(mod));
  }









  
  public boolean hasAnnotation(String typeName) {
    MethodInfo mi = getMethodInfo2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)mi.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)mi.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType.hasAnnotationType(typeName, 
        getDeclaringClass().getClassPool(), ainfo, ainfo2);
  }













  
  public Object getAnnotation(Class<?> clz) throws ClassNotFoundException {
    MethodInfo mi = getMethodInfo2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)mi.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)mi.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType.getAnnotationType(clz, 
        getDeclaringClass().getClassPool(), ainfo, ainfo2);
  }









  
  public Object[] getAnnotations() throws ClassNotFoundException {
    return getAnnotations(false);
  }










  
  public Object[] getAvailableAnnotations() {
    try {
      return getAnnotations(true);
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("Unexpected exception", e);
    } 
  }


  
  private Object[] getAnnotations(boolean ignoreNotFound) throws ClassNotFoundException {
    MethodInfo mi = getMethodInfo2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)mi.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)mi.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType.toAnnotationType(ignoreNotFound, 
        getDeclaringClass().getClassPool(), ainfo, ainfo2);
  }












  
  public Object[][] getParameterAnnotations() throws ClassNotFoundException {
    return getParameterAnnotations(false);
  }













  
  public Object[][] getAvailableParameterAnnotations() {
    try {
      return getParameterAnnotations(true);
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("Unexpected exception", e);
    } 
  }


  
  Object[][] getParameterAnnotations(boolean ignoreNotFound) throws ClassNotFoundException {
    MethodInfo mi = getMethodInfo2();
    
    ParameterAnnotationsAttribute ainfo = (ParameterAnnotationsAttribute)mi.getAttribute("RuntimeInvisibleParameterAnnotations");
    
    ParameterAnnotationsAttribute ainfo2 = (ParameterAnnotationsAttribute)mi.getAttribute("RuntimeVisibleParameterAnnotations");
    return CtClassType.toAnnotationType(ignoreNotFound, 
        getDeclaringClass().getClassPool(), ainfo, ainfo2, mi);
  }




  
  public CtClass[] getParameterTypes() throws NotFoundException {
    return Descriptor.getParameterTypes(this.methodInfo.getDescriptor(), this.declaringClass
        .getClassPool());
  }



  
  CtClass getReturnType0() throws NotFoundException {
    return Descriptor.getReturnType(this.methodInfo.getDescriptor(), this.declaringClass
        .getClassPool());
  }


















  
  public String getSignature() {
    return this.methodInfo.getDescriptor();
  }









  
  public String getGenericSignature() {
    SignatureAttribute sa = (SignatureAttribute)this.methodInfo.getAttribute("Signature");
    return (sa == null) ? null : sa.getSignature();
  }











  
  public void setGenericSignature(String sig) {
    this.declaringClass.checkModify();
    this.methodInfo.addAttribute((AttributeInfo)new SignatureAttribute(this.methodInfo.getConstPool(), sig));
  }





  
  public CtClass[] getExceptionTypes() throws NotFoundException {
    String[] exceptions;
    ExceptionsAttribute ea = this.methodInfo.getExceptionsAttribute();
    if (ea == null) {
      exceptions = null;
    } else {
      exceptions = ea.getExceptions();
    } 
    return this.declaringClass.getClassPool().get(exceptions);
  }



  
  public void setExceptionTypes(CtClass[] types) throws NotFoundException {
    this.declaringClass.checkModify();
    if (types == null || types.length == 0) {
      this.methodInfo.removeExceptionsAttribute();
      
      return;
    } 
    String[] names = new String[types.length];
    for (int i = 0; i < types.length; i++) {
      names[i] = types[i].getName();
    }
    ExceptionsAttribute ea = this.methodInfo.getExceptionsAttribute();
    if (ea == null) {
      ea = new ExceptionsAttribute(this.methodInfo.getConstPool());
      this.methodInfo.setExceptionsAttribute(ea);
    } 
    
    ea.setExceptions(names);
  }






  
  public abstract boolean isEmpty();





  
  public void setBody(String src) throws CannotCompileException {
    setBody(src, (String)null, (String)null);
  }















  
  public void setBody(String src, String delegateObj, String delegateMethod) throws CannotCompileException {
    CtClass cc = this.declaringClass;
    cc.checkModify();
    try {
      Javac jv = new Javac(cc);
      if (delegateMethod != null) {
        jv.recordProceed(delegateObj, delegateMethod);
      }
      Bytecode b = jv.compileBody(this, src);
      this.methodInfo.setCodeAttribute(b.toCodeAttribute());
      this.methodInfo.setAccessFlags(this.methodInfo.getAccessFlags() & 0xFFFFFBFF);
      
      this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
      this.declaringClass.rebuildClassFile();
    }
    catch (CompileError e) {
      throw new CannotCompileException(e);
    } catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
  }




  
  static void setBody0(CtClass srcClass, MethodInfo srcInfo, CtClass destClass, MethodInfo destInfo, ClassMap map) throws CannotCompileException {
    destClass.checkModify();
    
    map = new ClassMap(map);
    map.put(srcClass.getName(), destClass.getName());
    try {
      CodeAttribute cattr = srcInfo.getCodeAttribute();
      if (cattr != null) {
        ConstPool cp = destInfo.getConstPool();
        CodeAttribute ca = (CodeAttribute)cattr.copy(cp, map);
        destInfo.setCodeAttribute(ca);
      }
    
    }
    catch (javassist.bytecode.CodeAttribute.RuntimeCopyException e) {

      
      throw new CannotCompileException(e);
    } 
    
    destInfo.setAccessFlags(destInfo.getAccessFlags() & 0xFFFFFBFF);
    
    destClass.rebuildClassFile();
  }













  
  public byte[] getAttribute(String name) {
    AttributeInfo ai = this.methodInfo.getAttribute(name);
    if (ai == null)
      return null; 
    return ai.get();
  }












  
  public void setAttribute(String name, byte[] data) {
    this.declaringClass.checkModify();
    this.methodInfo.addAttribute(new AttributeInfo(this.methodInfo.getConstPool(), name, data));
  }


















  
  public void useCflow(String name) throws CannotCompileException {
    String fname;
    CtClass cc = this.declaringClass;
    cc.checkModify();
    ClassPool pool = cc.getClassPool();
    
    int i = 0;
    while (true) {
      fname = "_cflow$" + i++;
      try {
        cc.getDeclaredField(fname);
      }
      catch (NotFoundException e) {
        break;
      } 
    } 
    
    pool.recordCflow(name, this.declaringClass.getName(), fname);
    try {
      CtClass type = pool.get("javassist.runtime.Cflow");
      CtField field = new CtField(type, fname, cc);
      field.setModifiers(9);
      cc.addField(field, CtField.Initializer.byNew(type));
      insertBefore(fname + ".enter();", false);
      String src = fname + ".exit();";
      insertAfter(src, true);
    }
    catch (NotFoundException e) {
      throw new CannotCompileException(e);
    } 
  }

















  
  public void addLocalVariable(String name, CtClass type) throws CannotCompileException {
    this.declaringClass.checkModify();
    ConstPool cp = this.methodInfo.getConstPool();
    CodeAttribute ca = this.methodInfo.getCodeAttribute();
    if (ca == null) {
      throw new CannotCompileException("no method body");
    }
    LocalVariableAttribute va = (LocalVariableAttribute)ca.getAttribute("LocalVariableTable");
    
    if (va == null) {
      va = new LocalVariableAttribute(cp);
      ca.getAttributes().add(va);
    } 
    
    int maxLocals = ca.getMaxLocals();
    String desc = Descriptor.of(type);
    va.addEntry(0, ca.getCodeLength(), cp
        .addUtf8Info(name), cp.addUtf8Info(desc), maxLocals);
    ca.setMaxLocals(maxLocals + Descriptor.dataSize(desc));
  }





  
  public void insertParameter(CtClass type) throws CannotCompileException {
    this.declaringClass.checkModify();
    String desc = this.methodInfo.getDescriptor();
    String desc2 = Descriptor.insertParameter(type, desc);
    try {
      addParameter2(Modifier.isStatic(getModifiers()) ? 0 : 1, type, desc);
    }
    catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
    
    this.methodInfo.setDescriptor(desc2);
  }





  
  public void addParameter(CtClass type) throws CannotCompileException {
    this.declaringClass.checkModify();
    String desc = this.methodInfo.getDescriptor();
    String desc2 = Descriptor.appendParameter(type, desc);
    int offset = Modifier.isStatic(getModifiers()) ? 0 : 1;
    try {
      addParameter2(offset + Descriptor.paramSize(desc), type, desc);
    }
    catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
    
    this.methodInfo.setDescriptor(desc2);
  }


  
  private void addParameter2(int where, CtClass type, String desc) throws BadBytecode {
    CodeAttribute ca = this.methodInfo.getCodeAttribute();
    if (ca != null) {
      int size = 1;
      char typeDesc = 'L';
      int classInfo = 0;
      if (type.isPrimitive()) {
        CtPrimitiveType cpt = (CtPrimitiveType)type;
        size = cpt.getDataSize();
        typeDesc = cpt.getDescriptor();
      } else {
        
        classInfo = this.methodInfo.getConstPool().addClassInfo(type);
      } 
      ca.insertLocalVar(where, size);
      
      LocalVariableAttribute va = (LocalVariableAttribute)ca.getAttribute("LocalVariableTable");
      if (va != null) {
        va.shiftIndex(where, size);
      }
      
      LocalVariableTypeAttribute lvta = (LocalVariableTypeAttribute)ca.getAttribute("LocalVariableTypeTable");
      if (lvta != null) {
        lvta.shiftIndex(where, size);
      }
      StackMapTable smt = (StackMapTable)ca.getAttribute("StackMapTable");
      if (smt != null) {
        smt.insertLocal(where, StackMapTable.typeTagOf(typeDesc), classInfo);
      }
      StackMap sm = (StackMap)ca.getAttribute("StackMap");
      if (sm != null) {
        sm.insertLocal(where, StackMapTable.typeTagOf(typeDesc), classInfo);
      }
    } 
  }






  
  public void instrument(CodeConverter converter) throws CannotCompileException {
    this.declaringClass.checkModify();
    ConstPool cp = this.methodInfo.getConstPool();
    converter.doit(getDeclaringClass(), this.methodInfo, cp);
  }

















  
  public void instrument(ExprEditor editor) throws CannotCompileException {
    if (this.declaringClass.isFrozen()) {
      this.declaringClass.checkModify();
    }
    if (editor.doit(this.declaringClass, this.methodInfo)) {
      this.declaringClass.checkModify();
    }
  }
















  
  public void insertBefore(String src) throws CannotCompileException {
    insertBefore(src, true);
  }


  
  private void insertBefore(String src, boolean rebuild) throws CannotCompileException {
    CtClass cc = this.declaringClass;
    cc.checkModify();
    CodeAttribute ca = this.methodInfo.getCodeAttribute();
    if (ca == null) {
      throw new CannotCompileException("no method body");
    }
    CodeIterator iterator = ca.iterator();
    Javac jv = new Javac(cc);
    try {
      int nvars = jv.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      jv.recordParamNames(ca, nvars);
      jv.recordLocalVariables(ca, 0);
      jv.recordReturnType(getReturnType0(), false);
      jv.compileStmnt(src);
      Bytecode b = jv.getBytecode();
      int stack = b.getMaxStack();
      int locals = b.getMaxLocals();
      
      if (stack > ca.getMaxStack()) {
        ca.setMaxStack(stack);
      }
      if (locals > ca.getMaxLocals()) {
        ca.setMaxLocals(locals);
      }
      int pos = iterator.insertEx(b.get());
      iterator.insert(b.getExceptionTable(), pos);
      if (rebuild) {
        this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
      }
    } catch (NotFoundException e) {
      throw new CannotCompileException(e);
    }
    catch (CompileError e) {
      throw new CannotCompileException(e);
    }
    catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
  }










  
  public void insertAfter(String src) throws CannotCompileException {
    insertAfter(src, false, false);
  }














  
  public void insertAfter(String src, boolean asFinally) throws CannotCompileException {
    insertAfter(src, asFinally, false);
  }

























  
  public void insertAfter(String src, boolean asFinally, boolean redundant) throws CannotCompileException {
    CtClass cc = this.declaringClass;
    cc.checkModify();
    ConstPool pool = this.methodInfo.getConstPool();
    CodeAttribute ca = this.methodInfo.getCodeAttribute();
    if (ca == null) {
      throw new CannotCompileException("no method body");
    }
    CodeIterator iterator = ca.iterator();
    int retAddr = ca.getMaxLocals();
    Bytecode b = new Bytecode(pool, 0, retAddr + 1);
    b.setStackDepth(ca.getMaxStack() + 1);
    Javac jv = new Javac(b, cc);
    try {
      int nvars = jv.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      jv.recordParamNames(ca, nvars);
      CtClass rtype = getReturnType0();
      int varNo = jv.recordReturnType(rtype, true);
      jv.recordLocalVariables(ca, 0);

      
      int handlerLen = insertAfterHandler(asFinally, b, rtype, varNo, jv, src);
      
      int handlerPos = iterator.getCodeLength();
      if (asFinally) {
        ca.getExceptionTable().add(getStartPosOfBody(ca), handlerPos, handlerPos, 0);
      }
      int adviceLen = 0;
      int advicePos = 0;
      boolean noReturn = true;
      while (iterator.hasNext()) {
        int pos = iterator.next();
        if (pos >= handlerPos) {
          break;
        }
        int c = iterator.byteAt(pos);
        if (c == 176 || c == 172 || c == 174 || c == 173 || c == 175 || c == 177) {

          
          if (redundant) {
            Bytecode bcode; Javac jvc; int retVarNo; iterator.setMark2(handlerPos);


            
            if (noReturn) {
              noReturn = false;
              bcode = b;
              jvc = jv;
              retVarNo = varNo;
            } else {
              
              bcode = new Bytecode(pool, 0, retAddr + 1);
              bcode.setStackDepth(ca.getMaxStack() + 1);
              jvc = new Javac(bcode, cc);
              int nvars2 = jvc.recordParams(getParameterTypes(), 
                  Modifier.isStatic(getModifiers()));
              jvc.recordParamNames(ca, nvars2);
              retVarNo = jvc.recordReturnType(rtype, true);
              jvc.recordLocalVariables(ca, 0);
            } 
            
            int adviceLen2 = insertAfterAdvice(bcode, jvc, src, pool, rtype, retVarNo);
            int offset = iterator.append(bcode.get());
            iterator.append(bcode.getExceptionTable(), offset);
            int advicePos2 = iterator.getCodeLength() - adviceLen2;
            insertGoto(iterator, advicePos2, pos);
            handlerPos = iterator.getMark2();
            continue;
          } 
          if (noReturn) {
            
            adviceLen = insertAfterAdvice(b, jv, src, pool, rtype, varNo);
            handlerPos = iterator.append(b.get());
            iterator.append(b.getExceptionTable(), handlerPos);
            advicePos = iterator.getCodeLength() - adviceLen;
            handlerLen = advicePos - handlerPos;
            noReturn = false;
          } 
          
          insertGoto(iterator, advicePos, pos);
          advicePos = iterator.getCodeLength() - adviceLen;
          handlerPos = advicePos - handlerLen;
        } 
      } 

      
      if (noReturn) {
        handlerPos = iterator.append(b.get());
        iterator.append(b.getExceptionTable(), handlerPos);
      } 
      
      ca.setMaxStack(b.getMaxStack());
      ca.setMaxLocals(b.getMaxLocals());
      this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
    }
    catch (NotFoundException e) {
      throw new CannotCompileException(e);
    }
    catch (CompileError e) {
      throw new CannotCompileException(e);
    }
    catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
  }



  
  private int insertAfterAdvice(Bytecode code, Javac jv, String src, ConstPool cp, CtClass rtype, int varNo) throws CompileError {
    int pc = code.currentPc();
    if (rtype == CtClass.voidType) {
      code.addOpcode(1);
      code.addAstore(varNo);
      jv.compileStmnt(src);
      code.addOpcode(177);
      if (code.getMaxLocals() < 1) {
        code.setMaxLocals(1);
      }
    } else {
      code.addStore(varNo, rtype);
      jv.compileStmnt(src);
      code.addLoad(varNo, rtype);
      if (rtype.isPrimitive()) {
        code.addOpcode(((CtPrimitiveType)rtype).getReturnOp());
      } else {
        code.addOpcode(176);
      } 
    } 
    return code.currentPc() - pc;
  }





  
  private void insertGoto(CodeIterator iterator, int subr, int pos) throws BadBytecode {
    iterator.setMark(subr);
    
    iterator.writeByte(0, pos);
    boolean wide = (subr + 2 - pos > 32767);
    int len = wide ? 4 : 2;
    CodeIterator.Gap gap = iterator.insertGapAt(pos, len, false);
    pos = gap.position + gap.length - len;
    int offset = iterator.getMark() - pos;
    if (wide) {
      iterator.writeByte(200, pos);
      iterator.write32bit(offset, pos + 1);
    }
    else if (offset <= 32767) {
      iterator.writeByte(167, pos);
      iterator.write16bit(offset, pos + 1);
    } else {
      
      if (gap.length < 4) {
        CodeIterator.Gap gap2 = iterator.insertGapAt(gap.position, 2, false);
        pos = gap2.position + gap2.length + gap.length - 4;
      } 
      
      iterator.writeByte(200, pos);
      iterator.write32bit(iterator.getMark() - pos, pos + 1);
    } 
  }






  
  private int insertAfterHandler(boolean asFinally, Bytecode b, CtClass rtype, int returnVarNo, Javac javac, String src) throws CompileError {
    if (!asFinally) {
      return 0;
    }
    int var = b.getMaxLocals();
    b.incMaxLocals(1);
    int pc = b.currentPc();
    b.addAstore(var);
    if (rtype.isPrimitive()) {
      char c = ((CtPrimitiveType)rtype).getDescriptor();
      if (c == 'D') {
        b.addDconst(0.0D);
        b.addDstore(returnVarNo);
      }
      else if (c == 'F') {
        b.addFconst(0.0F);
        b.addFstore(returnVarNo);
      }
      else if (c == 'J') {
        b.addLconst(0L);
        b.addLstore(returnVarNo);
      }
      else if (c == 'V') {
        b.addOpcode(1);
        b.addAstore(returnVarNo);
      } else {
        
        b.addIconst(0);
        b.addIstore(returnVarNo);
      } 
    } else {
      
      b.addOpcode(1);
      b.addAstore(returnVarNo);
    } 
    
    javac.compileStmnt(src);
    b.addAload(var);
    b.addOpcode(191);
    return b.currentPc() - pc;
  }






























































  
  public void addCatch(String src, CtClass exceptionType) throws CannotCompileException {
    addCatch(src, exceptionType, "$e");
  }















  
  public void addCatch(String src, CtClass exceptionType, String exceptionName) throws CannotCompileException {
    CtClass cc = this.declaringClass;
    cc.checkModify();
    ConstPool cp = this.methodInfo.getConstPool();
    CodeAttribute ca = this.methodInfo.getCodeAttribute();
    CodeIterator iterator = ca.iterator();
    Bytecode b = new Bytecode(cp, ca.getMaxStack(), ca.getMaxLocals());
    b.setStackDepth(1);
    Javac jv = new Javac(b, cc);
    try {
      jv.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      int var = jv.recordVariable(exceptionType, exceptionName);
      b.addAstore(var);
      jv.compileStmnt(src);
      
      int stack = b.getMaxStack();
      int locals = b.getMaxLocals();
      
      if (stack > ca.getMaxStack()) {
        ca.setMaxStack(stack);
      }
      if (locals > ca.getMaxLocals()) {
        ca.setMaxLocals(locals);
      }
      int len = iterator.getCodeLength();
      int pos = iterator.append(b.get());
      ca.getExceptionTable().add(getStartPosOfBody(ca), len, len, cp
          .addClassInfo(exceptionType));
      iterator.append(b.getExceptionTable(), pos);
      this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
    }
    catch (NotFoundException e) {
      throw new CannotCompileException(e);
    }
    catch (CompileError e) {
      throw new CannotCompileException(e);
    } catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
  }


  
  int getStartPosOfBody(CodeAttribute ca) throws CannotCompileException {
    return 0;
  }



















  
  public int insertAt(int lineNum, String src) throws CannotCompileException {
    return insertAt(lineNum, true, src);
  }
























  
  public int insertAt(int lineNum, boolean modify, String src) throws CannotCompileException {
    CodeAttribute ca = this.methodInfo.getCodeAttribute();
    if (ca == null) {
      throw new CannotCompileException("no method body");
    }
    
    LineNumberAttribute ainfo = (LineNumberAttribute)ca.getAttribute("LineNumberTable");
    if (ainfo == null) {
      throw new CannotCompileException("no line number info");
    }
    LineNumberAttribute.Pc pc = ainfo.toNearPc(lineNum);
    lineNum = pc.line;
    int index = pc.index;
    if (!modify) {
      return lineNum;
    }
    CtClass cc = this.declaringClass;
    cc.checkModify();
    CodeIterator iterator = ca.iterator();
    Javac jv = new Javac(cc);
    try {
      jv.recordLocalVariables(ca, index);
      jv.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      jv.setMaxLocals(ca.getMaxLocals());
      jv.compileStmnt(src);
      Bytecode b = jv.getBytecode();
      int locals = b.getMaxLocals();
      int stack = b.getMaxStack();
      ca.setMaxLocals(locals);



      
      if (stack > ca.getMaxStack()) {
        ca.setMaxStack(stack);
      }
      index = iterator.insertAt(index, b.get());
      iterator.insert(b.getExceptionTable(), index);
      this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
      return lineNum;
    }
    catch (NotFoundException e) {
      throw new CannotCompileException(e);
    }
    catch (CompileError e) {
      throw new CannotCompileException(e);
    }
    catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
  }
}
