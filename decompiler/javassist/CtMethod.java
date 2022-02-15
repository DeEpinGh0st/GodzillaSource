package javassist;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;






























public final class CtMethod
  extends CtBehavior
{
  protected String cachedStringRep;
  
  CtMethod(MethodInfo minfo, CtClass declaring) {
    super(declaring, minfo);
    this.cachedStringRep = null;
  }












  
  public CtMethod(CtClass returnType, String mname, CtClass[] parameters, CtClass declaring) {
    this((MethodInfo)null, declaring);
    ConstPool cp = declaring.getClassFile2().getConstPool();
    String desc = Descriptor.ofMethod(returnType, parameters);
    this.methodInfo = new MethodInfo(cp, mname, desc);
    setModifiers(1025);
  }




















































  
  public CtMethod(CtMethod src, CtClass declaring, ClassMap map) throws CannotCompileException {
    this((MethodInfo)null, declaring);
    copy(src, false, map);
  }












  
  public static CtMethod make(String src, CtClass declaring) throws CannotCompileException {
    return CtNewMethod.make(src, declaring);
  }











  
  public static CtMethod make(MethodInfo minfo, CtClass declaring) throws CannotCompileException {
    if (declaring.getClassFile2().getConstPool() != minfo.getConstPool()) {
      throw new CannotCompileException("bad declaring class");
    }
    return new CtMethod(minfo, declaring);
  }






  
  public int hashCode() {
    return getStringRep().hashCode();
  }





  
  void nameReplaced() {
    this.cachedStringRep = null;
  }


  
  final String getStringRep() {
    if (this.cachedStringRep == null) {
      this
        .cachedStringRep = this.methodInfo.getName() + Descriptor.getParamDescriptor(this.methodInfo.getDescriptor());
    }
    return this.cachedStringRep;
  }





  
  public boolean equals(Object obj) {
    return (obj != null && obj instanceof CtMethod && ((CtMethod)obj)
      .getStringRep().equals(getStringRep()));
  }







  
  public String getLongName() {
    return getDeclaringClass().getName() + "." + 
      getName() + Descriptor.toString(getSignature());
  }




  
  public String getName() {
    return this.methodInfo.getName();
  }



  
  public void setName(String newname) {
    this.declaringClass.checkModify();
    this.methodInfo.setName(newname);
  }



  
  public CtClass getReturnType() throws NotFoundException {
    return getReturnType0();
  }





  
  public boolean isEmpty() {
    CodeAttribute ca = getMethodInfo2().getCodeAttribute();
    if (ca == null) {
      return ((getModifiers() & 0x400) != 0);
    }
    CodeIterator it = ca.iterator();
    try {
      return (it.hasNext() && it.byteAt(it.next()) == 177 && 
        !it.hasNext());
    }
    catch (BadBytecode badBytecode) {
      return false;
    } 
  }















  
  public void setBody(CtMethod src, ClassMap map) throws CannotCompileException {
    setBody0(src.declaringClass, src.methodInfo, this.declaringClass, this.methodInfo, map);
  }













  
  public void setWrappedBody(CtMethod mbody, ConstParameter constParam) throws CannotCompileException {
    CtClass params[], retType;
    this.declaringClass.checkModify();
    
    CtClass clazz = getDeclaringClass();

    
    try {
      params = getParameterTypes();
      retType = getReturnType();
    }
    catch (NotFoundException e) {
      throw new CannotCompileException(e);
    } 
    
    Bytecode code = CtNewWrappedMethod.makeBody(clazz, clazz
        .getClassFile2(), mbody, params, retType, constParam);


    
    CodeAttribute cattr = code.toCodeAttribute();
    this.methodInfo.setCodeAttribute(cattr);
    this.methodInfo.setAccessFlags(this.methodInfo.getAccessFlags() & 0xFFFFFBFF);
  }

















  
  public static class ConstParameter
  {
    public static ConstParameter integer(int i) {
      return new CtMethod.IntConstParameter(i);
    }





    
    public static ConstParameter integer(long i) {
      return new CtMethod.LongConstParameter(i);
    }





    
    public static ConstParameter string(String s) {
      return new CtMethod.StringConstParameter(s);
    }





    
    int compile(Bytecode code) throws CannotCompileException {
      return 0;
    }
    
    String descriptor() {
      return defaultDescriptor();
    }



    
    static String defaultDescriptor() {
      return "([Ljava/lang/Object;)Ljava/lang/Object;";
    }





    
    String constDescriptor() {
      return defaultConstDescriptor();
    }



    
    static String defaultConstDescriptor() {
      return "([Ljava/lang/Object;)V";
    }
  }
  
  static class IntConstParameter extends ConstParameter {
    int param;
    
    IntConstParameter(int i) {
      this.param = i;
    }

    
    int compile(Bytecode code) throws CannotCompileException {
      code.addIconst(this.param);
      return 1;
    }

    
    String descriptor() {
      return "([Ljava/lang/Object;I)Ljava/lang/Object;";
    }

    
    String constDescriptor() {
      return "([Ljava/lang/Object;I)V";
    }
  }
  
  static class LongConstParameter extends ConstParameter {
    long param;
    
    LongConstParameter(long l) {
      this.param = l;
    }

    
    int compile(Bytecode code) throws CannotCompileException {
      code.addLconst(this.param);
      return 2;
    }

    
    String descriptor() {
      return "([Ljava/lang/Object;J)Ljava/lang/Object;";
    }

    
    String constDescriptor() {
      return "([Ljava/lang/Object;J)V";
    }
  }
  
  static class StringConstParameter extends ConstParameter {
    String param;
    
    StringConstParameter(String s) {
      this.param = s;
    }

    
    int compile(Bytecode code) throws CannotCompileException {
      code.addLdc(this.param);
      return 1;
    }

    
    String descriptor() {
      return "([Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;";
    }

    
    String constDescriptor() {
      return "([Ljava/lang/Object;Ljava/lang/String;)V";
    }
  }
}
