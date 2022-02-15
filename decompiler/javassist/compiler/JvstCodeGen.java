package javassist.compiler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.Descriptor;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.CastExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.Symbol;


















public class JvstCodeGen
  extends MemberCodeGen
{
  String paramArrayName = null;
  String paramListName = null;
  CtClass[] paramTypeList = null;
  private int paramVarBase = 0;
  private boolean useParam0 = false;
  private String param0Type = null;
  public static final String sigName = "$sig";
  public static final String dollarTypeName = "$type";
  public static final String clazzName = "$class";
  private CtClass dollarType = null;
  CtClass returnType = null;
  String returnCastName = null;
  private String returnVarName = null;
  
  public static final String wrapperCastName = "$w";
  String proceedName = null;
  public static final String cflowName = "$cflow";
  ProceedHandler procHandler = null;
  
  public JvstCodeGen(Bytecode b, CtClass cc, ClassPool cp) {
    super(b, cc, cp);
    setTypeChecker(new JvstTypeChecker(cc, cp, this));
  }


  
  private int indexOfParam1() {
    return this.paramVarBase + (this.useParam0 ? 1 : 0);
  }





  
  public void setProceedHandler(ProceedHandler h, String name) {
    this.proceedName = name;
    this.procHandler = h;
  }



  
  public void addNullIfVoid() {
    if (this.exprType == 344) {
      this.bytecode.addOpcode(1);
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } 
  }




  
  public void atMember(Member mem) throws CompileError {
    String name = mem.get();
    if (name.equals(this.paramArrayName)) {
      compileParameterList(this.bytecode, this.paramTypeList, indexOfParam1());
      this.exprType = 307;
      this.arrayDim = 1;
      this.className = "java/lang/Object";
    }
    else if (name.equals("$sig")) {
      this.bytecode.addLdc(Descriptor.ofMethod(this.returnType, this.paramTypeList));
      this.bytecode.addInvokestatic("javassist/runtime/Desc", "getParams", "(Ljava/lang/String;)[Ljava/lang/Class;");
      
      this.exprType = 307;
      this.arrayDim = 1;
      this.className = "java/lang/Class";
    }
    else if (name.equals("$type")) {
      if (this.dollarType == null) {
        throw new CompileError("$type is not available");
      }
      this.bytecode.addLdc(Descriptor.of(this.dollarType));
      callGetType("getType");
    }
    else if (name.equals("$class")) {
      if (this.param0Type == null) {
        throw new CompileError("$class is not available");
      }
      this.bytecode.addLdc(this.param0Type);
      callGetType("getClazz");
    } else {
      
      super.atMember(mem);
    } 
  }
  private void callGetType(String method) {
    this.bytecode.addInvokestatic("javassist/runtime/Desc", method, "(Ljava/lang/String;)Ljava/lang/Class;");
    
    this.exprType = 307;
    this.arrayDim = 0;
    this.className = "java/lang/Class";
  }



  
  protected void atFieldAssign(Expr expr, int op, ASTree left, ASTree right, boolean doDup) throws CompileError {
    if (left instanceof Member && ((Member)left)
      .get().equals(this.paramArrayName)) {
      if (op != 61) {
        throw new CompileError("bad operator for " + this.paramArrayName);
      }
      right.accept(this);
      if (this.arrayDim != 1 || this.exprType != 307) {
        throw new CompileError("invalid type for " + this.paramArrayName);
      }
      atAssignParamList(this.paramTypeList, this.bytecode);
      if (!doDup) {
        this.bytecode.addOpcode(87);
      }
    } else {
      super.atFieldAssign(expr, op, left, right, doDup);
    } 
  }

  
  protected void atAssignParamList(CtClass[] params, Bytecode code) throws CompileError {
    if (params == null) {
      return;
    }
    int varNo = indexOfParam1();
    int n = params.length;
    for (int i = 0; i < n; i++) {
      code.addOpcode(89);
      code.addIconst(i);
      code.addOpcode(50);
      compileUnwrapValue(params[i], code);
      code.addStore(varNo, params[i]);
      varNo += is2word(this.exprType, this.arrayDim) ? 2 : 1;
    } 
  }

  
  public void atCastExpr(CastExpr expr) throws CompileError {
    ASTList classname = expr.getClassName();
    if (classname != null && expr.getArrayDim() == 0) {
      ASTree p = classname.head();
      if (p instanceof Symbol && classname.tail() == null) {
        String typename = ((Symbol)p).get();
        if (typename.equals(this.returnCastName)) {
          atCastToRtype(expr);
          return;
        } 
        if (typename.equals("$w")) {
          atCastToWrapper(expr);
          
          return;
        } 
      } 
    } 
    super.atCastExpr(expr);
  }




  
  protected void atCastToRtype(CastExpr expr) throws CompileError {
    expr.getOprand().accept(this);
    if (this.exprType == 344 || isRefType(this.exprType) || this.arrayDim > 0) {
      compileUnwrapValue(this.returnType, this.bytecode);
    } else if (this.returnType instanceof CtPrimitiveType) {
      CtPrimitiveType pt = (CtPrimitiveType)this.returnType;
      int destType = MemberResolver.descToType(pt.getDescriptor());
      atNumCastExpr(this.exprType, destType);
      this.exprType = destType;
      this.arrayDim = 0;
      this.className = null;
    } else {
      
      throw new CompileError("invalid cast");
    } 
  }
  protected void atCastToWrapper(CastExpr expr) throws CompileError {
    expr.getOprand().accept(this);
    if (isRefType(this.exprType) || this.arrayDim > 0) {
      return;
    }
    CtClass clazz = this.resolver.lookupClass(this.exprType, this.arrayDim, this.className);
    if (clazz instanceof CtPrimitiveType) {
      CtPrimitiveType pt = (CtPrimitiveType)clazz;
      String wrapper = pt.getWrapperName();
      this.bytecode.addNew(wrapper);
      this.bytecode.addOpcode(89);
      if (pt.getDataSize() > 1) {
        this.bytecode.addOpcode(94);
      } else {
        this.bytecode.addOpcode(93);
      } 
      this.bytecode.addOpcode(88);
      this.bytecode.addInvokespecial(wrapper, "<init>", "(" + pt
          .getDescriptor() + ")V");
      
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } 
  }




  
  public void atCallExpr(CallExpr expr) throws CompileError {
    ASTree method = expr.oprand1();
    if (method instanceof Member) {
      String name = ((Member)method).get();
      if (this.procHandler != null && name.equals(this.proceedName)) {
        this.procHandler.doit(this, this.bytecode, (ASTList)expr.oprand2());
        return;
      } 
      if (name.equals("$cflow")) {
        atCflow((ASTList)expr.oprand2());
        
        return;
      } 
    } 
    super.atCallExpr(expr);
  }


  
  protected void atCflow(ASTList cname) throws CompileError {
    StringBuffer sbuf = new StringBuffer();
    if (cname == null || cname.tail() != null) {
      throw new CompileError("bad $cflow");
    }
    makeCflowName(sbuf, cname.head());
    String name = sbuf.toString();
    Object[] names = this.resolver.getClassPool().lookupCflow(name);
    if (names == null) {
      throw new CompileError("no such $cflow: " + name);
    }
    this.bytecode.addGetstatic((String)names[0], (String)names[1], "Ljavassist/runtime/Cflow;");
    
    this.bytecode.addInvokevirtual("javassist.runtime.Cflow", "value", "()I");
    
    this.exprType = 324;
    this.arrayDim = 0;
    this.className = null;
  }







  
  private static void makeCflowName(StringBuffer sbuf, ASTree name) throws CompileError {
    if (name instanceof Symbol) {
      sbuf.append(((Symbol)name).get());
      return;
    } 
    if (name instanceof Expr) {
      Expr expr = (Expr)name;
      if (expr.getOperator() == 46) {
        makeCflowName(sbuf, expr.oprand1());
        sbuf.append('.');
        makeCflowName(sbuf, expr.oprand2());
        
        return;
      } 
    } 
    throw new CompileError("bad $cflow");
  }



  
  public boolean isParamListName(ASTList args) {
    if (this.paramTypeList != null && args != null && args
      .tail() == null) {
      ASTree left = args.head();
      return (left instanceof Member && ((Member)left)
        .get().equals(this.paramListName));
    } 
    return false;
  }










  
  public int getMethodArgsLength(ASTList args) {
    String pname = this.paramListName;
    int n = 0;
    while (args != null) {
      ASTree a = args.head();
      if (a instanceof Member && ((Member)a).get().equals(pname)) {
        if (this.paramTypeList != null) {
          n += this.paramTypeList.length;
        }
      } else {
        n++;
      } 
      args = args.tail();
    } 
    
    return n;
  }


  
  public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError {
    CtClass[] params = this.paramTypeList;
    String pname = this.paramListName;
    int i = 0;
    while (args != null) {
      ASTree a = args.head();
      if (a instanceof Member && ((Member)a).get().equals(pname)) {
        if (params != null) {
          int n = params.length;
          int regno = indexOfParam1();
          for (int k = 0; k < n; k++) {
            CtClass p = params[k];
            regno += this.bytecode.addLoad(regno, p);
            setType(p);
            types[i] = this.exprType;
            dims[i] = this.arrayDim;
            cnames[i] = this.className;
            i++;
          } 
        } 
      } else {
        
        a.accept(this);
        types[i] = this.exprType;
        dims[i] = this.arrayDim;
        cnames[i] = this.className;
        i++;
      } 
      
      args = args.tail();
    } 
  }






























  
  void compileInvokeSpecial(ASTree target, int methodIndex, String descriptor, ASTList args) throws CompileError {
    target.accept(this);
    int nargs = getMethodArgsLength(args);
    atMethodArgs(args, new int[nargs], new int[nargs], new String[nargs]);
    
    this.bytecode.addInvokespecial(methodIndex, descriptor);
    setReturnType(descriptor, false, false);
    addNullIfVoid();
  }




  
  protected void atReturnStmnt(Stmnt st) throws CompileError {
    ASTree result = st.getLeft();
    if (result != null && this.returnType == CtClass.voidType) {
      compileExpr(result);
      if (is2word(this.exprType, this.arrayDim)) {
        this.bytecode.addOpcode(88);
      } else if (this.exprType != 344) {
        this.bytecode.addOpcode(87);
      } 
      result = null;
    } 
    
    atReturnStmnt2(result);
  }












  
  public int recordReturnType(CtClass type, String castName, String resultName, SymbolTable tbl) throws CompileError {
    this.returnType = type;
    this.returnCastName = castName;
    this.returnVarName = resultName;
    if (resultName == null)
      return -1; 
    int varNo = getMaxLocals();
    int locals = varNo + recordVar(type, resultName, varNo, tbl);
    setMaxLocals(locals);
    return varNo;
  }



  
  public void recordType(CtClass t) {
    this.dollarType = t;
  }









  
  public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName, SymbolTable tbl) throws CompileError {
    return recordParams(params, isStatic, prefix, paramVarName, paramsName, !isStatic, 0, 
        getThisName(), tbl);
  }


























  
  public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName, boolean use0, int paramBase, String target, SymbolTable tbl) throws CompileError {
    this.paramTypeList = params;
    this.paramArrayName = paramVarName;
    this.paramListName = paramsName;
    this.paramVarBase = paramBase;
    this.useParam0 = use0;
    
    if (target != null) {
      this.param0Type = MemberResolver.jvmToJavaName(target);
    }
    this.inStaticMethod = isStatic;
    int varNo = paramBase;
    if (use0) {
      String varName = prefix + "0";
      
      Declarator decl = new Declarator(307, MemberResolver.javaToJvmName(target), 0, varNo++, new Symbol(varName));
      
      tbl.append(varName, decl);
    } 
    
    for (int i = 0; i < params.length; i++) {
      varNo += recordVar(params[i], prefix + (i + 1), varNo, tbl);
    }
    if (getMaxLocals() < varNo) {
      setMaxLocals(varNo);
    }
    return varNo;
  }








  
  public int recordVariable(CtClass type, String varName, SymbolTable tbl) throws CompileError {
    if (varName == null)
      return -1; 
    int varNo = getMaxLocals();
    int locals = varNo + recordVar(type, varName, varNo, tbl);
    setMaxLocals(locals);
    return varNo;
  }


  
  private int recordVar(CtClass cc, String varName, int varNo, SymbolTable tbl) throws CompileError {
    if (cc == CtClass.voidType) {
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } else {
      
      setType(cc);
    } 
    Declarator decl = new Declarator(this.exprType, this.className, this.arrayDim, varNo, new Symbol(varName));

    
    tbl.append(varName, decl);
    return is2word(this.exprType, this.arrayDim) ? 2 : 1;
  }










  
  public void recordVariable(String typeDesc, String varName, int varNo, SymbolTable tbl) throws CompileError {
    int dim = 0; char c;
    while ((c = typeDesc.charAt(dim)) == '[') {
      dim++;
    }
    int type = MemberResolver.descToType(c);
    String cname = null;
    if (type == 307) {
      if (dim == 0) {
        cname = typeDesc.substring(1, typeDesc.length() - 1);
      } else {
        cname = typeDesc.substring(dim + 1, typeDesc.length() - 1);
      } 
    }
    Declarator decl = new Declarator(type, cname, dim, varNo, new Symbol(varName));
    
    tbl.append(varName, decl);
  }










  
  public static int compileParameterList(Bytecode code, CtClass[] params, int regno) {
    if (params == null) {
      code.addIconst(0);
      code.addAnewarray("java.lang.Object");
      return 1;
    } 
    CtClass[] args = new CtClass[1];
    int n = params.length;
    code.addIconst(n);
    code.addAnewarray("java.lang.Object");
    for (int i = 0; i < n; i++) {
      code.addOpcode(89);
      code.addIconst(i);
      if (params[i].isPrimitive()) {
        CtPrimitiveType pt = (CtPrimitiveType)params[i];
        String wrapper = pt.getWrapperName();
        code.addNew(wrapper);
        code.addOpcode(89);
        int s = code.addLoad(regno, (CtClass)pt);
        regno += s;
        args[0] = (CtClass)pt;
        code.addInvokespecial(wrapper, "<init>", 
            Descriptor.ofMethod(CtClass.voidType, args));
      }
      else {
        
        code.addAload(regno);
        regno++;
      } 
      
      code.addOpcode(83);
    } 
    
    return 8;
  }


  
  protected void compileUnwrapValue(CtClass type, Bytecode code) throws CompileError {
    if (type == CtClass.voidType) {
      addNullIfVoid();
      
      return;
    } 
    if (this.exprType == 344) {
      throw new CompileError("invalid type for " + this.returnCastName);
    }
    if (type instanceof CtPrimitiveType) {
      CtPrimitiveType pt = (CtPrimitiveType)type;
      
      String wrapper = pt.getWrapperName();
      code.addCheckcast(wrapper);
      code.addInvokevirtual(wrapper, pt.getGetMethodName(), pt
          .getGetMethodDescriptor());
      setType(type);
    } else {
      
      code.addCheckcast(type);
      setType(type);
    } 
  }



  
  public void setType(CtClass type) throws CompileError {
    setType(type, 0);
  }
  
  private void setType(CtClass type, int dim) throws CompileError {
    if (type.isPrimitive()) {
      CtPrimitiveType pt = (CtPrimitiveType)type;
      this.exprType = MemberResolver.descToType(pt.getDescriptor());
      this.arrayDim = dim;
      this.className = null;
    }
    else if (type.isArray()) {
      try {
        setType(type.getComponentType(), dim + 1);
      }
      catch (NotFoundException e) {
        throw new CompileError("undefined type: " + type.getName());
      } 
    } else {
      this.exprType = 307;
      this.arrayDim = dim;
      this.className = MemberResolver.javaToJvmName(type.getName());
    } 
  }


  
  public void doNumCast(CtClass type) throws CompileError {
    if (this.arrayDim == 0 && !isRefType(this.exprType))
      if (type instanceof CtPrimitiveType) {
        CtPrimitiveType pt = (CtPrimitiveType)type;
        atNumCastExpr(this.exprType, 
            MemberResolver.descToType(pt.getDescriptor()));
      } else {
        
        throw new CompileError("type mismatch");
      }  
  }
}
