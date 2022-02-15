package javassist;

import java.util.List;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.SignatureAttribute;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.compiler.SymbolTable;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.DoubleConst;
import javassist.compiler.ast.IntConst;
import javassist.compiler.ast.StringL;












































public class CtField
  extends CtMember
{
  static final String javaLangString = "java.lang.String";
  protected FieldInfo fieldInfo;
  
  public CtField(CtClass type, String name, CtClass declaring) throws CannotCompileException {
    this(Descriptor.of(type), name, declaring);
  }



















  
  public CtField(CtField src, CtClass declaring) throws CannotCompileException {
    this(src.fieldInfo.getDescriptor(), src.fieldInfo.getName(), declaring);
    
    FieldInfo fi = this.fieldInfo;
    fi.setAccessFlags(src.fieldInfo.getAccessFlags());
    ConstPool cp = fi.getConstPool();
    List<AttributeInfo> attributes = src.fieldInfo.getAttributes();
    for (AttributeInfo ainfo : attributes) {
      fi.addAttribute(ainfo.copy(cp, null));
    }
  }

  
  private CtField(String typeDesc, String name, CtClass clazz) throws CannotCompileException {
    super(clazz);
    ClassFile cf = clazz.getClassFile2();
    if (cf == null) {
      throw new CannotCompileException("bad declaring class: " + clazz
          .getName());
    }
    this.fieldInfo = new FieldInfo(cf.getConstPool(), name, typeDesc);
  }
  
  CtField(FieldInfo fi, CtClass clazz) {
    super(clazz);
    this.fieldInfo = fi;
  }




  
  public String toString() {
    return getDeclaringClass().getName() + "." + getName() + ":" + this.fieldInfo
      .getDescriptor();
  }

  
  protected void extendToString(StringBuffer buffer) {
    buffer.append(' ');
    buffer.append(getName());
    buffer.append(' ');
    buffer.append(this.fieldInfo.getDescriptor());
  }
  
  protected ASTree getInitAST() {
    return null;
  }
  
  Initializer getInit() {
    ASTree tree = getInitAST();
    if (tree == null)
      return null; 
    return Initializer.byExpr(tree);
  }
















  
  public static CtField make(String src, CtClass declaring) throws CannotCompileException {
    Javac compiler = new Javac(declaring);
    try {
      CtMember obj = compiler.compile(src);
      if (obj instanceof CtField) {
        return (CtField)obj;
      }
    } catch (CompileError e) {
      throw new CannotCompileException(e);
    } 
    
    throw new CannotCompileException("not a field");
  }



  
  public FieldInfo getFieldInfo() {
    this.declaringClass.checkModify();
    return this.fieldInfo;
  }


















  
  public FieldInfo getFieldInfo2() {
    return this.fieldInfo;
  }




  
  public CtClass getDeclaringClass() {
    return super.getDeclaringClass();
  }




  
  public String getName() {
    return this.fieldInfo.getName();
  }



  
  public void setName(String newName) {
    this.declaringClass.checkModify();
    this.fieldInfo.setName(newName);
  }






  
  public int getModifiers() {
    return AccessFlag.toModifier(this.fieldInfo.getAccessFlags());
  }






  
  public void setModifiers(int mod) {
    this.declaringClass.checkModify();
    this.fieldInfo.setAccessFlags(AccessFlag.of(mod));
  }








  
  public boolean hasAnnotation(String typeName) {
    FieldInfo fi = getFieldInfo2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)fi.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)fi.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType.hasAnnotationType(typeName, getDeclaringClass().getClassPool(), ainfo, ainfo2);
  }













  
  public Object getAnnotation(Class<?> clz) throws ClassNotFoundException {
    FieldInfo fi = getFieldInfo2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)fi.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)fi.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType.getAnnotationType(clz, getDeclaringClass().getClassPool(), ainfo, ainfo2);
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
    FieldInfo fi = getFieldInfo2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)fi.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)fi.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType.toAnnotationType(ignoreNotFound, getDeclaringClass().getClassPool(), ainfo, ainfo2);
  }
















  
  public String getSignature() {
    return this.fieldInfo.getDescriptor();
  }









  
  public String getGenericSignature() {
    SignatureAttribute sa = (SignatureAttribute)this.fieldInfo.getAttribute("Signature");
    return (sa == null) ? null : sa.getSignature();
  }











  
  public void setGenericSignature(String sig) {
    this.declaringClass.checkModify();
    this.fieldInfo.addAttribute((AttributeInfo)new SignatureAttribute(this.fieldInfo.getConstPool(), sig));
  }



  
  public CtClass getType() throws NotFoundException {
    return Descriptor.toCtClass(this.fieldInfo.getDescriptor(), this.declaringClass
        .getClassPool());
  }














  
  public void setType(CtClass clazz) {
    this.declaringClass.checkModify();
    this.fieldInfo.setDescriptor(Descriptor.of(clazz));
  }

















  
  public Object getConstantValue() {
    int value, index = this.fieldInfo.getConstantValue();
    if (index == 0) {
      return null;
    }
    ConstPool cp = this.fieldInfo.getConstPool();
    switch (cp.getTag(index)) {
      case 5:
        return Long.valueOf(cp.getLongInfo(index));
      case 4:
        return Float.valueOf(cp.getFloatInfo(index));
      case 6:
        return Double.valueOf(cp.getDoubleInfo(index));
      case 3:
        value = cp.getIntegerInfo(index);
        
        if ("Z".equals(this.fieldInfo.getDescriptor()))
          return Boolean.valueOf((value != 0)); 
        return Integer.valueOf(value);
      case 8:
        return cp.getStringInfo(index);
    } 
    throw new RuntimeException("bad tag: " + cp.getTag(index) + " at " + index);
  }














  
  public byte[] getAttribute(String name) {
    AttributeInfo ai = this.fieldInfo.getAttribute(name);
    if (ai == null)
      return null; 
    return ai.get();
  }











  
  public void setAttribute(String name, byte[] data) {
    this.declaringClass.checkModify();
    this.fieldInfo.addAttribute(new AttributeInfo(this.fieldInfo.getConstPool(), name, data));
  }



















  
  public static abstract class Initializer
  {
    public static Initializer constant(int i) {
      return new CtField.IntInitializer(i);
    }




    
    public static Initializer constant(boolean b) {
      return new CtField.IntInitializer(b ? 1 : 0);
    }




    
    public static Initializer constant(long l) {
      return new CtField.LongInitializer(l);
    }




    
    public static Initializer constant(float l) {
      return new CtField.FloatInitializer(l);
    }




    
    public static Initializer constant(double d) {
      return new CtField.DoubleInitializer(d);
    }




    
    public static Initializer constant(String s) {
      return new CtField.StringInitializer(s);
    }














    
    public static Initializer byParameter(int nth) {
      CtField.ParamInitializer i = new CtField.ParamInitializer();
      i.nthParam = nth;
      return i;
    }














    
    public static Initializer byNew(CtClass objectType) {
      CtField.NewInitializer i = new CtField.NewInitializer();
      i.objectType = objectType;
      i.stringParams = null;
      i.withConstructorParams = false;
      return i;
    }



















    
    public static Initializer byNew(CtClass objectType, String[] stringParams) {
      CtField.NewInitializer i = new CtField.NewInitializer();
      i.objectType = objectType;
      i.stringParams = stringParams;
      i.withConstructorParams = false;
      return i;
    }




















    
    public static Initializer byNewWithParams(CtClass objectType) {
      CtField.NewInitializer i = new CtField.NewInitializer();
      i.objectType = objectType;
      i.stringParams = null;
      i.withConstructorParams = true;
      return i;
    }






















    
    public static Initializer byNewWithParams(CtClass objectType, String[] stringParams) {
      CtField.NewInitializer i = new CtField.NewInitializer();
      i.objectType = objectType;
      i.stringParams = stringParams;
      i.withConstructorParams = true;
      return i;
    }




















    
    public static Initializer byCall(CtClass methodClass, String methodName) {
      CtField.MethodInitializer i = new CtField.MethodInitializer();
      i.objectType = methodClass;
      i.methodName = methodName;
      i.stringParams = null;
      i.withConstructorParams = false;
      return i;
    }

























    
    public static Initializer byCall(CtClass methodClass, String methodName, String[] stringParams) {
      CtField.MethodInitializer i = new CtField.MethodInitializer();
      i.objectType = methodClass;
      i.methodName = methodName;
      i.stringParams = stringParams;
      i.withConstructorParams = false;
      return i;
    }























    
    public static Initializer byCallWithParams(CtClass methodClass, String methodName) {
      CtField.MethodInitializer i = new CtField.MethodInitializer();
      i.objectType = methodClass;
      i.methodName = methodName;
      i.stringParams = null;
      i.withConstructorParams = true;
      return i;
    }



























    
    public static Initializer byCallWithParams(CtClass methodClass, String methodName, String[] stringParams) {
      CtField.MethodInitializer i = new CtField.MethodInitializer();
      i.objectType = methodClass;
      i.methodName = methodName;
      i.stringParams = stringParams;
      i.withConstructorParams = true;
      return i;
    }










    
    public static Initializer byNewArray(CtClass type, int size) throws NotFoundException {
      return new CtField.ArrayInitializer(type.getComponentType(), size);
    }









    
    public static Initializer byNewArray(CtClass type, int[] sizes) {
      return new CtField.MultiArrayInitializer(type, sizes);
    }





    
    public static Initializer byExpr(String source) {
      return new CtField.CodeInitializer(source);
    }
    
    static Initializer byExpr(ASTree source) {
      return new CtField.PtreeInitializer(source);
    }


    
    void check(String desc) throws CannotCompileException {}


    
    abstract int compile(CtClass param1CtClass, String param1String, Bytecode param1Bytecode, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException;


    
    abstract int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode param1Bytecode, Javac param1Javac) throws CannotCompileException;


    
    int getConstantValue(ConstPool cp, CtClass type) {
      return 0;
    }
  }

  
  static abstract class CodeInitializer0
    extends Initializer
  {
    abstract void compileExpr(Javac param1Javac) throws CompileError;
    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      try {
        code.addAload(0);
        compileExpr(drv);
        code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
        return code.getMaxStack();
      }
      catch (CompileError e) {
        throw new CannotCompileException(e);
      } 
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      try {
        compileExpr(drv);
        code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
        return code.getMaxStack();
      }
      catch (CompileError e) {
        throw new CannotCompileException(e);
      } 
    }
    
    int getConstantValue2(ConstPool cp, CtClass type, ASTree tree) {
      if (type.isPrimitive()) {
        if (tree instanceof IntConst) {
          long value = ((IntConst)tree).get();
          if (type == CtClass.doubleType)
            return cp.addDoubleInfo(value); 
          if (type == CtClass.floatType)
            return cp.addFloatInfo((float)value); 
          if (type == CtClass.longType)
            return cp.addLongInfo(value); 
          if (type != CtClass.voidType) {
            return cp.addIntegerInfo((int)value);
          }
        } else if (tree instanceof DoubleConst) {
          double value = ((DoubleConst)tree).get();
          if (type == CtClass.floatType)
            return cp.addFloatInfo((float)value); 
          if (type == CtClass.doubleType) {
            return cp.addDoubleInfo(value);
          }
        } 
      } else if (tree instanceof StringL && type
        .getName().equals("java.lang.String")) {
        return cp.addStringInfo(((StringL)tree).get());
      } 
      return 0;
    }
  }
  
  static class CodeInitializer
    extends CodeInitializer0 {
    CodeInitializer(String expr) {
      this.expression = expr;
    }
    private String expression;
    void compileExpr(Javac drv) throws CompileError {
      drv.compileExpr(this.expression);
    }

    
    int getConstantValue(ConstPool cp, CtClass type) {
      try {
        ASTree t = Javac.parseExpr(this.expression, new SymbolTable());
        return getConstantValue2(cp, type, t);
      }
      catch (CompileError e) {
        return 0;
      } 
    } }
  
  static class PtreeInitializer extends CodeInitializer0 {
    private ASTree expression;
    
    PtreeInitializer(ASTree expr) {
      this.expression = expr;
    }
    
    void compileExpr(Javac drv) throws CompileError {
      drv.compileExpr(this.expression);
    }

    
    int getConstantValue(ConstPool cp, CtClass type) {
      return getConstantValue2(cp, type, this.expression);
    }
  }




  
  static class ParamInitializer
    extends Initializer
  {
    int nthParam;




    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      if (parameters != null && this.nthParam < parameters.length) {
        code.addAload(0);
        int nth = nthParamToLocal(this.nthParam, parameters, false);
        int s = code.addLoad(nth, type) + 1;
        code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
        return s;
      } 
      return 0;
    }








    
    static int nthParamToLocal(int nth, CtClass[] params, boolean isStatic) {
      int k;
      CtClass longType = CtClass.longType;
      CtClass doubleType = CtClass.doubleType;
      
      if (isStatic) {
        k = 0;
      } else {
        k = 1;
      } 
      for (int i = 0; i < nth; i++) {
        CtClass type = params[i];
        if (type == longType || type == doubleType) {
          k += 2;
        } else {
          k++;
        } 
      } 
      return k;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      return 0;
    }
  }



  
  static class NewInitializer
    extends Initializer
  {
    CtClass objectType;


    
    String[] stringParams;


    
    boolean withConstructorParams;



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      int stacksize;
      code.addAload(0);
      code.addNew(this.objectType);
      code.add(89);
      code.addAload(0);
      
      if (this.stringParams == null) {
        stacksize = 4;
      } else {
        stacksize = compileStringParameter(code) + 4;
      } 
      if (this.withConstructorParams) {
        stacksize += CtNewWrappedMethod.compileParameterList(code, parameters, 1);
      }
      
      code.addInvokespecial(this.objectType, "<init>", getDescriptor());
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return stacksize;
    }
    
    private String getDescriptor() {
      String desc3 = "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)V";

      
      if (this.stringParams == null) {
        if (this.withConstructorParams) {
          return "(Ljava/lang/Object;[Ljava/lang/Object;)V";
        }
        return "(Ljava/lang/Object;)V";
      } 
      if (this.withConstructorParams) {
        return "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)V";
      }
      return "(Ljava/lang/Object;[Ljava/lang/String;)V";
    }







    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      String desc;
      code.addNew(this.objectType);
      code.add(89);
      
      int stacksize = 2;
      if (this.stringParams == null) {
        desc = "()V";
      } else {
        desc = "([Ljava/lang/String;)V";
        stacksize += compileStringParameter(code);
      } 
      
      code.addInvokespecial(this.objectType, "<init>", desc);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return stacksize;
    }


    
    protected final int compileStringParameter(Bytecode code) throws CannotCompileException {
      int nparam = this.stringParams.length;
      code.addIconst(nparam);
      code.addAnewarray("java.lang.String");
      for (int j = 0; j < nparam; j++) {
        code.add(89);
        code.addIconst(j);
        code.addLdc(this.stringParams[j]);
        code.add(83);
      } 
      
      return 4;
    }
  }







  
  static class MethodInitializer
    extends NewInitializer
  {
    String methodName;







    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      int stacksize;
      code.addAload(0);
      code.addAload(0);
      
      if (this.stringParams == null) {
        stacksize = 2;
      } else {
        stacksize = compileStringParameter(code) + 2;
      } 
      if (this.withConstructorParams) {
        stacksize += CtNewWrappedMethod.compileParameterList(code, parameters, 1);
      }
      
      String typeDesc = Descriptor.of(type);
      String mDesc = getDescriptor() + typeDesc;
      code.addInvokestatic(this.objectType, this.methodName, mDesc);
      code.addPutfield(Bytecode.THIS, name, typeDesc);
      return stacksize;
    }
    
    private String getDescriptor() {
      String desc3 = "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)";

      
      if (this.stringParams == null) {
        if (this.withConstructorParams) {
          return "(Ljava/lang/Object;[Ljava/lang/Object;)";
        }
        return "(Ljava/lang/Object;)";
      } 
      if (this.withConstructorParams) {
        return "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)";
      }
      return "(Ljava/lang/Object;[Ljava/lang/String;)";
    }







    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      String desc;
      int stacksize = 1;
      if (this.stringParams == null) {
        desc = "()";
      } else {
        desc = "([Ljava/lang/String;)";
        stacksize += compileStringParameter(code);
      } 
      
      String typeDesc = Descriptor.of(type);
      code.addInvokestatic(this.objectType, this.methodName, desc + typeDesc);
      code.addPutstatic(Bytecode.THIS, name, typeDesc);
      return stacksize;
    } }
  
  static class IntInitializer extends Initializer {
    int value;
    
    IntInitializer(int v) {
      this.value = v;
    }
    
    void check(String desc) throws CannotCompileException {
      char c = desc.charAt(0);
      if (c != 'I' && c != 'S' && c != 'B' && c != 'C' && c != 'Z') {
        throw new CannotCompileException("type mismatch");
      }
    }



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      code.addAload(0);
      code.addIconst(this.value);
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return 2;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      code.addIconst(this.value);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return 1;
    }

    
    int getConstantValue(ConstPool cp, CtClass type) {
      return cp.addIntegerInfo(this.value);
    }
  }
  
  static class LongInitializer extends Initializer { long value;
    
    LongInitializer(long v) {
      this.value = v;
    }
    
    void check(String desc) throws CannotCompileException {
      if (!desc.equals("J")) {
        throw new CannotCompileException("type mismatch");
      }
    }



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      code.addAload(0);
      code.addLdc2w(this.value);
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return 3;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      code.addLdc2w(this.value);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return 2;
    }

    
    int getConstantValue(ConstPool cp, CtClass type) {
      if (type == CtClass.longType)
        return cp.addLongInfo(this.value); 
      return 0;
    } }
  
  static class FloatInitializer extends Initializer {
    float value;
    
    FloatInitializer(float v) {
      this.value = v;
    }
    
    void check(String desc) throws CannotCompileException {
      if (!desc.equals("F")) {
        throw new CannotCompileException("type mismatch");
      }
    }



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      code.addAload(0);
      code.addFconst(this.value);
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return 3;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      code.addFconst(this.value);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return 2;
    }

    
    int getConstantValue(ConstPool cp, CtClass type) {
      if (type == CtClass.floatType)
        return cp.addFloatInfo(this.value); 
      return 0;
    }
  }
  
  static class DoubleInitializer extends Initializer { double value;
    
    DoubleInitializer(double v) {
      this.value = v;
    }
    
    void check(String desc) throws CannotCompileException {
      if (!desc.equals("D")) {
        throw new CannotCompileException("type mismatch");
      }
    }



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      code.addAload(0);
      code.addLdc2w(this.value);
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return 3;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      code.addLdc2w(this.value);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return 2;
    }

    
    int getConstantValue(ConstPool cp, CtClass type) {
      if (type == CtClass.doubleType)
        return cp.addDoubleInfo(this.value); 
      return 0;
    } }
  
  static class StringInitializer extends Initializer {
    String value;
    
    StringInitializer(String v) {
      this.value = v;
    }



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      code.addAload(0);
      code.addLdc(this.value);
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return 2;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      code.addLdc(this.value);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return 1;
    }

    
    int getConstantValue(ConstPool cp, CtClass type) {
      if (type.getName().equals("java.lang.String"))
        return cp.addStringInfo(this.value); 
      return 0;
    }
  }
  
  static class ArrayInitializer extends Initializer { CtClass type;
    int size;
    
    ArrayInitializer(CtClass t, int s) {
      this.type = t; this.size = s;
    }
    private void addNewarray(Bytecode code) {
      if (this.type.isPrimitive()) {
        code.addNewarray(((CtPrimitiveType)this.type).getArrayType(), this.size);
      } else {
        
        code.addAnewarray(this.type, this.size);
      } 
    }



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      code.addAload(0);
      addNewarray(code);
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return 2;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      addNewarray(code);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return 1;
    } }
  
  static class MultiArrayInitializer extends Initializer {
    CtClass type;
    int[] dim;
    
    MultiArrayInitializer(CtClass t, int[] d) {
      this.type = t; this.dim = d;
    }
    
    void check(String desc) throws CannotCompileException {
      if (desc.charAt(0) != '[') {
        throw new CannotCompileException("type mismatch");
      }
    }



    
    int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv) throws CannotCompileException {
      code.addAload(0);
      int s = code.addMultiNewarray(type, this.dim);
      code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
      return s + 1;
    }



    
    int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException {
      int s = code.addMultiNewarray(type, this.dim);
      code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
      return s;
    }
  }
}
