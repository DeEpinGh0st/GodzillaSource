package org.mozilla.javascript;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.ScriptNode;














class CodeGenerator
  extends Icode
{
  private static final int MIN_LABEL_TABLE_SIZE = 32;
  private static final int MIN_FIXUP_TABLE_SIZE = 40;
  private CompilerEnvirons compilerEnv;
  private boolean itsInFunctionFlag;
  private boolean itsInTryFlag;
  private InterpreterData itsData;
  private ScriptNode scriptOrFn;
  private int iCodeTop;
  private int stackDepth;
  private int lineNumber;
  private int doubleTableTop;
  private ObjToIntMap strings = new ObjToIntMap(20);
  
  private int localTop;
  
  private int[] labelTable;
  private int labelTableTop;
  private long[] fixupTable;
  private int fixupTableTop;
  private ObjArray literalIds = new ObjArray();


  
  private int exceptionTableTop;

  
  private static final int ECF_TAIL = 1;


  
  public InterpreterData compile(CompilerEnvirons compilerEnv, ScriptNode tree, String encodedSource, boolean returnFunction) {
    this.compilerEnv = compilerEnv;





    
    (new NodeTransformer()).transform(tree);





    
    if (returnFunction) {
      this.scriptOrFn = (ScriptNode)tree.getFunctionNode(0);
    } else {
      this.scriptOrFn = tree;
    } 
    this.itsData = new InterpreterData(compilerEnv.getLanguageVersion(), this.scriptOrFn.getSourceName(), encodedSource, ((AstRoot)tree).isInStrictMode());


    
    this.itsData.topLevel = true;
    
    if (returnFunction) {
      generateFunctionICode();
    } else {
      generateICodeFromTree((Node)this.scriptOrFn);
    } 
    return this.itsData;
  }

  
  private void generateFunctionICode() {
    this.itsInFunctionFlag = true;
    
    FunctionNode theFunction = (FunctionNode)this.scriptOrFn;
    
    this.itsData.itsFunctionType = theFunction.getFunctionType();
    this.itsData.itsNeedsActivation = theFunction.requiresActivation();
    if (theFunction.getFunctionName() != null) {
      this.itsData.itsName = theFunction.getName();
    }
    if (theFunction.isGenerator()) {
      addIcode(-62);
      addUint16(theFunction.getBaseLineno() & 0xFFFF);
    } 
    
    generateICodeFromTree(theFunction.getLastChild());
  }

  
  private void generateICodeFromTree(Node tree) {
    generateNestedFunctions();
    
    generateRegExpLiterals();
    
    visitStatement(tree, 0);
    fixLabelGotos();
    
    if (this.itsData.itsFunctionType == 0) {
      addToken(64);
    }
    
    if (this.itsData.itsICode.length != this.iCodeTop) {

      
      byte[] tmp = new byte[this.iCodeTop];
      System.arraycopy(this.itsData.itsICode, 0, tmp, 0, this.iCodeTop);
      this.itsData.itsICode = tmp;
    } 
    if (this.strings.size() == 0) {
      this.itsData.itsStringTable = null;
    } else {
      this.itsData.itsStringTable = new String[this.strings.size()];
      ObjToIntMap.Iterator iter = this.strings.newIterator();
      iter.start(); for (; !iter.done(); iter.next()) {
        String str = (String)iter.getKey();
        int index = iter.getValue();
        if (this.itsData.itsStringTable[index] != null) Kit.codeBug(); 
        this.itsData.itsStringTable[index] = str;
      } 
    } 
    if (this.doubleTableTop == 0) {
      this.itsData.itsDoubleTable = null;
    } else if (this.itsData.itsDoubleTable.length != this.doubleTableTop) {
      double[] tmp = new double[this.doubleTableTop];
      System.arraycopy(this.itsData.itsDoubleTable, 0, tmp, 0, this.doubleTableTop);
      
      this.itsData.itsDoubleTable = tmp;
    } 
    if (this.exceptionTableTop != 0 && this.itsData.itsExceptionTable.length != this.exceptionTableTop) {

      
      int[] tmp = new int[this.exceptionTableTop];
      System.arraycopy(this.itsData.itsExceptionTable, 0, tmp, 0, this.exceptionTableTop);
      
      this.itsData.itsExceptionTable = tmp;
    } 
    
    this.itsData.itsMaxVars = this.scriptOrFn.getParamAndVarCount();

    
    this.itsData.itsMaxFrameArray = this.itsData.itsMaxVars + this.itsData.itsMaxLocals + this.itsData.itsMaxStack;


    
    this.itsData.argNames = this.scriptOrFn.getParamAndVarNames();
    this.itsData.argIsConst = this.scriptOrFn.getParamAndVarConst();
    this.itsData.argCount = this.scriptOrFn.getParamCount();
    
    this.itsData.encodedSourceStart = this.scriptOrFn.getEncodedSourceStart();
    this.itsData.encodedSourceEnd = this.scriptOrFn.getEncodedSourceEnd();
    
    if (this.literalIds.size() != 0) {
      this.itsData.literalIds = this.literalIds.toArray();
    }
  }



  
  private void generateNestedFunctions() {
    int functionCount = this.scriptOrFn.getFunctionCount();
    if (functionCount == 0)
      return; 
    InterpreterData[] array = new InterpreterData[functionCount];
    for (int i = 0; i != functionCount; i++) {
      FunctionNode fn = this.scriptOrFn.getFunctionNode(i);
      CodeGenerator gen = new CodeGenerator();
      gen.compilerEnv = this.compilerEnv;
      gen.scriptOrFn = (ScriptNode)fn;
      gen.itsData = new InterpreterData(this.itsData);
      gen.generateFunctionICode();
      array[i] = gen.itsData;
    } 
    this.itsData.itsNestedFunctions = array;
  }

  
  private void generateRegExpLiterals() {
    int N = this.scriptOrFn.getRegexpCount();
    if (N == 0)
      return; 
    Context cx = Context.getContext();
    RegExpProxy rep = ScriptRuntime.checkRegExpProxy(cx);
    Object[] array = new Object[N];
    for (int i = 0; i != N; i++) {
      String string = this.scriptOrFn.getRegexpString(i);
      String flags = this.scriptOrFn.getRegexpFlags(i);
      array[i] = rep.compileRegExp(cx, string, flags);
    } 
    this.itsData.itsRegExpLiterals = array;
  }

  
  private void updateLineNumber(Node node) {
    int lineno = node.getLineno();
    if (lineno != this.lineNumber && lineno >= 0) {
      if (this.itsData.firstLinePC < 0) {
        this.itsData.firstLinePC = lineno;
      }
      this.lineNumber = lineno;
      addIcode(-26);
      addUint16(lineno & 0xFFFF);
    } 
  }

  
  private RuntimeException badTree(Node node) {
    throw new RuntimeException(node.toString()); } private void visitStatement(Node node, int initialStackDepth) { int fnIndex, local; Jump caseNode; Node target; int finallyRegister; Jump tryNode; int localIndex, fnType, exceptionObjectLocal, scopeIndex, scopeLocal;
    String name;
    int tryStart;
    boolean savedFlag;
    Node catchTarget, finallyTarget;
    int type = node.getType();
    Node child = node.getFirstChild();
    switch (type) {

      
      case 109:
        fnIndex = node.getExistingIntProp(1);
        fnType = this.scriptOrFn.getFunctionNode(fnIndex).getFunctionType();






        
        if (fnType == 3) {
          addIndexOp(-20, fnIndex);
        }
        else if (fnType != 1) {
          throw Kit.codeBug();
        } 





        
        if (!this.itsInFunctionFlag) {
          addIndexOp(-19, fnIndex);
          stackChange(1);
          addIcode(-5);
          stackChange(-1);
        } 
        break;

      
      case 123:
      case 128:
      case 129:
      case 130:
      case 132:
        updateLineNumber(node);
      
      case 136:
        while (child != null) {
          visitStatement(child, initialStackDepth);
          child = child.getNext();
        } 
        break;
      
      case 2:
        visitExpression(child, 0);
        addToken(2);
        stackChange(-1);
        break;
      
      case 3:
        addToken(3);
        break;

      
      case 141:
        local = allocLocal();
        node.putIntProp(2, local);
        updateLineNumber(node);
        while (child != null) {
          visitStatement(child, initialStackDepth);
          child = child.getNext();
        } 
        addIndexOp(-56, local);
        releaseLocal(local);
        break;

      
      case 160:
        addIcode(-64);
        break;
      
      case 114:
        updateLineNumber(node);


        
        visitExpression(child, 0);
        caseNode = (Jump)child.getNext();
        for (; caseNode != null; 
          caseNode = (Jump)caseNode.getNext()) {
          
          if (caseNode.getType() != 115)
            throw badTree(caseNode); 
          Node test = caseNode.getFirstChild();
          addIcode(-1);
          stackChange(1);
          visitExpression(test, 0);
          addToken(46);
          stackChange(-1);

          
          addGoto(caseNode.target, -6);
          stackChange(-1);
        } 
        addIcode(-4);
        stackChange(-1);
        break;

      
      case 131:
        markTargetLabel(node);
        break;

      
      case 6:
      case 7:
        target = ((Jump)node).target;
        visitExpression(child, 0);
        addGoto(target, type);
        stackChange(-1);
        break;


      
      case 5:
        target = ((Jump)node).target;
        addGoto(target, type);
        break;


      
      case 135:
        target = ((Jump)node).target;
        addGoto(target, -23);
        break;



      
      case 125:
        stackChange(1);
        finallyRegister = getLocalBlockRef(node);
        addIndexOp(-24, finallyRegister);
        stackChange(-1);
        while (child != null) {
          visitStatement(child, initialStackDepth);
          child = child.getNext();
        } 
        addIndexOp(-25, finallyRegister);
        break;

      
      case 133:
      case 134:
        updateLineNumber(node);
        visitExpression(child, 0);
        addIcode((type == 133) ? -4 : -5);
        stackChange(-1);
        break;

      
      case 81:
        tryNode = (Jump)node;
        exceptionObjectLocal = getLocalBlockRef((Node)tryNode);
        scopeLocal = allocLocal();
        
        addIndexOp(-13, scopeLocal);
        
        tryStart = this.iCodeTop;
        savedFlag = this.itsInTryFlag;
        this.itsInTryFlag = true;
        while (child != null) {
          visitStatement(child, initialStackDepth);
          child = child.getNext();
        } 
        this.itsInTryFlag = savedFlag;
        
        catchTarget = tryNode.target;
        if (catchTarget != null) {
          int catchStartPC = this.labelTable[getTargetLabel(catchTarget)];
          
          addExceptionHandler(tryStart, catchStartPC, catchStartPC, false, exceptionObjectLocal, scopeLocal);
        } 

        
        finallyTarget = tryNode.getFinally();
        if (finallyTarget != null) {
          int finallyStartPC = this.labelTable[getTargetLabel(finallyTarget)];
          
          addExceptionHandler(tryStart, finallyStartPC, finallyStartPC, true, exceptionObjectLocal, scopeLocal);
        } 


        
        addIndexOp(-56, scopeLocal);
        releaseLocal(scopeLocal);
        break;


      
      case 57:
        localIndex = getLocalBlockRef(node);
        scopeIndex = node.getExistingIntProp(14);
        name = child.getString();
        child = child.getNext();
        visitExpression(child, 0);
        addStringPrefix(name);
        addIndexPrefix(localIndex);
        addToken(57);
        addUint8((scopeIndex != 0) ? 1 : 0);
        stackChange(-1);
        break;

      
      case 50:
        updateLineNumber(node);
        visitExpression(child, 0);
        addToken(50);
        addUint16(this.lineNumber & 0xFFFF);
        stackChange(-1);
        break;
      
      case 51:
        updateLineNumber(node);
        addIndexOp(51, getLocalBlockRef(node));
        break;
      
      case 4:
        updateLineNumber(node);
        if (node.getIntProp(20, 0) != 0) {
          
          addIcode(-63);
          addUint16(this.lineNumber & 0xFFFF); break;
        }  if (child != null) {
          visitExpression(child, 1);
          addToken(4);
          stackChange(-1); break;
        } 
        addIcode(-22);
        break;

      
      case 64:
        updateLineNumber(node);
        addToken(64);
        break;
      
      case 58:
      case 59:
      case 60:
        visitExpression(child, 0);
        addIndexOp(type, getLocalBlockRef(node));
        stackChange(-1);
        break;
      
      case -62:
        break;
      
      default:
        throw badTree(node);
    } 
    
    if (this.stackDepth != initialStackDepth)
      throw Kit.codeBug();  } private void visitExpression(Node node, int contextFlags) { int fnIndex, localIndex; Node lastChild; int argCount, afterSecondJumpStart; Node ifThen; boolean isName; FunctionNode fn; int callType, jump; Node ifElse; String property, name; int i;
    double num;
    int index, memberTypeFlags, queryPC;
    Node enterWith;
    int elseJumpStart, childCount;
    Node with;
    int afterElseJumpStart, inum, type = node.getType();
    Node child = node.getFirstChild();
    int savedStackDepth = this.stackDepth;
    switch (type)
    
    { 
      case 109:
        fnIndex = node.getExistingIntProp(1);
        fn = this.scriptOrFn.getFunctionNode(fnIndex);
        
        if (fn.getFunctionType() != 2) {
          throw Kit.codeBug();
        }
        addIndexOp(-19, fnIndex);
        stackChange(1);
        break;


      
      case 54:
        localIndex = getLocalBlockRef(node);
        addIndexOp(54, localIndex);
        stackChange(1);
        break;


      
      case 89:
        lastChild = node.getLastChild();
        while (child != lastChild) {
          visitExpression(child, 0);
          addIcode(-4);
          stackChange(-1);
          child = child.getNext();
        } 
        
        visitExpression(child, contextFlags & 0x1);
        break;



      
      case 138:
        stackChange(1);
        break;

      
      case 30:
      case 38:
      case 70:
        if (type == 30) {
          visitExpression(child, 0);
        } else {
          generateCallFunAndThis(child);
        } 
        argCount = 0;
        while ((child = child.getNext()) != null) {
          visitExpression(child, 0);
          argCount++;
        } 
        callType = node.getIntProp(10, 0);
        
        if (type != 70 && callType != 0) {
          
          addIndexOp(-21, argCount);
          addUint8(callType);
          addUint8((type == 30) ? 1 : 0);
          addUint16(this.lineNumber & 0xFFFF);
        
        }
        else {
          
          if (type == 38 && (contextFlags & 0x1) != 0 && !this.compilerEnv.isGenerateDebugInfo() && !this.itsInTryFlag)
          {
            
            type = -55;
          }
          addIndexOp(type, argCount);
        } 
        
        if (type == 30) {
          
          stackChange(-argCount);
        }
        else {
          
          stackChange(-1 - argCount);
        } 
        if (argCount > this.itsData.itsMaxCalleeArgs) {
          this.itsData.itsMaxCalleeArgs = argCount;
        }
        break;


      
      case 104:
      case 105:
        visitExpression(child, 0);
        addIcode(-1);
        stackChange(1);
        afterSecondJumpStart = this.iCodeTop;
        jump = (type == 105) ? 7 : 6;
        addGotoOp(jump);
        stackChange(-1);
        addIcode(-4);
        stackChange(-1);
        child = child.getNext();
        
        visitExpression(child, contextFlags & 0x1);
        resolveForwardGoto(afterSecondJumpStart);
        break;


      
      case 102:
        ifThen = child.getNext();
        ifElse = ifThen.getNext();
        visitExpression(child, 0);
        elseJumpStart = this.iCodeTop;
        addGotoOp(7);
        stackChange(-1);
        
        visitExpression(ifThen, contextFlags & 0x1);
        afterElseJumpStart = this.iCodeTop;
        addGotoOp(5);
        resolveForwardGoto(elseJumpStart);
        this.stackDepth = savedStackDepth;
        
        visitExpression(ifElse, contextFlags & 0x1);
        resolveForwardGoto(afterElseJumpStart);
        break;

      
      case 33:
      case 34:
        visitExpression(child, 0);
        child = child.getNext();
        addStringOp(type, child.getString());
        break;
      
      case 31:
        isName = (child.getType() == 49);
        visitExpression(child, 0);
        child = child.getNext();
        visitExpression(child, 0);
        if (isName) {
          
          addIcode(0);
        } else {
          addToken(31);
        } 
        stackChange(-1);
        break;
      
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 36:
      case 46:
      case 47:
      case 52:
      case 53:
        visitExpression(child, 0);
        child = child.getNext();
        visitExpression(child, 0);
        addToken(type);
        stackChange(-1);
        break;
      
      case 26:
      case 27:
      case 28:
      case 29:
      case 32:
      case 126:
        visitExpression(child, 0);
        if (type == 126) {
          addIcode(-4);
          addIcode(-50); break;
        } 
        addToken(type);
        break;

      
      case 67:
      case 69:
        visitExpression(child, 0);
        addToken(type);
        break;

      
      case 35:
      case 139:
        visitExpression(child, 0);
        child = child.getNext();
        property = child.getString();
        child = child.getNext();
        if (type == 139) {
          addIcode(-1);
          stackChange(1);
          addStringOp(33, property);
          
          stackChange(-1);
        } 
        visitExpression(child, 0);
        addStringOp(35, property);
        stackChange(-1);
        break;

      
      case 37:
      case 140:
        visitExpression(child, 0);
        child = child.getNext();
        visitExpression(child, 0);
        child = child.getNext();
        if (type == 140) {
          addIcode(-2);
          stackChange(2);
          addToken(36);
          stackChange(-1);
          
          stackChange(-1);
        } 
        visitExpression(child, 0);
        addToken(37);
        stackChange(-2);
        break;
      
      case 68:
      case 142:
        visitExpression(child, 0);
        child = child.getNext();
        if (type == 142) {
          addIcode(-1);
          stackChange(1);
          addToken(67);
          
          stackChange(-1);
        } 
        visitExpression(child, 0);
        addToken(68);
        stackChange(-1);
        break;

      
      case 8:
      case 73:
        name = child.getString();
        visitExpression(child, 0);
        child = child.getNext();
        visitExpression(child, 0);
        addStringOp(type, name);
        stackChange(-1);
        break;


      
      case 155:
        name = child.getString();
        visitExpression(child, 0);
        child = child.getNext();
        visitExpression(child, 0);
        addStringOp(-59, name);
        stackChange(-1);
        break;


      
      case 137:
        i = -1;

        
        if (this.itsInFunctionFlag && !this.itsData.itsNeedsActivation)
          i = this.scriptOrFn.getIndexForNameNode(node); 
        if (i == -1) {
          addStringOp(-14, node.getString());
          stackChange(1); break;
        } 
        addVarOp(55, i);
        stackChange(1);
        addToken(32);
        break;


      
      case 39:
      case 41:
      case 49:
        addStringOp(type, node.getString());
        stackChange(1);
        break;
      
      case 106:
      case 107:
        visitIncDec(node, child);
        break;

      
      case 40:
        num = node.getDouble();
        inum = (int)num;
        if (inum == num) {
          if (inum == 0) {
            addIcode(-51);
            
            if (1.0D / num < 0.0D) {
              addToken(29);
            }
          } else if (inum == 1) {
            addIcode(-52);
          } else if ((short)inum == inum) {
            addIcode(-27);
            
            addUint16(inum & 0xFFFF);
          } else {
            addIcode(-28);
            addInt(inum);
          } 
        } else {
          int j = getDoubleIndex(num);
          addIndexOp(40, j);
        } 
        stackChange(1);
        break;


      
      case 55:
        if (this.itsData.itsNeedsActivation) Kit.codeBug(); 
        index = this.scriptOrFn.getIndexForNameNode(node);
        addVarOp(55, index);
        stackChange(1);
        break;


      
      case 56:
        if (this.itsData.itsNeedsActivation) Kit.codeBug(); 
        index = this.scriptOrFn.getIndexForNameNode(child);
        child = child.getNext();
        visitExpression(child, 0);
        addVarOp(56, index);
        break;


      
      case 156:
        if (this.itsData.itsNeedsActivation) Kit.codeBug(); 
        index = this.scriptOrFn.getIndexForNameNode(child);
        child = child.getNext();
        visitExpression(child, 0);
        addVarOp(156, index);
        break;

      
      case 42:
      case 43:
      case 44:
      case 45:
      case 63:
        addToken(type);
        stackChange(1);
        break;
      
      case 61:
      case 62:
        addIndexOp(type, getLocalBlockRef(node));
        stackChange(1);
        break;

      
      case 48:
        index = node.getExistingIntProp(4);
        addIndexOp(48, index);
        stackChange(1);
        break;

      
      case 65:
      case 66:
        visitLiteral(node, child);
        break;
      
      case 157:
        visitArrayComprehension(node, child, child.getNext());
        break;
      
      case 71:
        visitExpression(child, 0);
        addStringOp(type, (String)node.getProp(17));
        break;

      
      case 77:
      case 78:
      case 79:
      case 80:
        memberTypeFlags = node.getIntProp(16, 0);
        
        childCount = 0;
        while (true)
        { visitExpression(child, 0);
          childCount++;
          child = child.getNext();
          if (child == null)
          { addIndexOp(type, memberTypeFlags);
            stackChange(1 - childCount);















































            
            if (savedStackDepth + 1 != this.stackDepth)
              Kit.codeBug();  return; }  } case 146: updateLineNumber(node); visitExpression(child, 0); addIcode(-53); stackChange(-1); queryPC = this.iCodeTop; visitExpression(child.getNext(), 0); addBackwardGoto(-54, queryPC); break;case 74: case 75: case 76: visitExpression(child, 0); addToken(type); break;case 72: if (child != null) { visitExpression(child, 0); } else { addIcode(-50); stackChange(1); }  addToken(72); addUint16(node.getLineno() & 0xFFFF); break;case 159: enterWith = node.getFirstChild(); with = enterWith.getNext(); visitExpression(enterWith.getFirstChild(), 0); addToken(2); stackChange(-1); visitExpression(with.getFirstChild(), 0); addToken(3); break;default: throw badTree(node); }  if (savedStackDepth + 1 != this.stackDepth) Kit.codeBug();
     }

  
  private void generateCallFunAndThis(Node left) {
    String name;
    Node target, id;
    int type = left.getType();
    switch (type) {
      case 39:
        name = left.getString();
        
        addStringOp(-15, name);
        stackChange(2);
        return;
      
      case 33:
      case 36:
        target = left.getFirstChild();
        visitExpression(target, 0);
        id = target.getNext();
        if (type == 33) {
          String property = id.getString();
          
          addStringOp(-16, property);
          stackChange(1);
        } else {
          visitExpression(id, 0);
          
          addIcode(-17);
        } 
        return;
    } 

    
    visitExpression(left, 0);
    
    addIcode(-18);
    stackChange(1);
  }
  private void visitIncDec(Node node, Node child) {
    int i;
    String name;
    Node object, ref;
    String property;
    Node index;
    int incrDecrMask = node.getExistingIntProp(13);
    int childType = child.getType();
    switch (childType) {
      case 55:
        if (this.itsData.itsNeedsActivation) Kit.codeBug(); 
        i = this.scriptOrFn.getIndexForNameNode(child);
        addVarOp(-7, i);
        addUint8(incrDecrMask);
        stackChange(1);
        return;
      
      case 39:
        name = child.getString();
        addStringOp(-8, name);
        addUint8(incrDecrMask);
        stackChange(1);
        return;
      
      case 33:
        object = child.getFirstChild();
        visitExpression(object, 0);
        property = object.getNext().getString();
        addStringOp(-9, property);
        addUint8(incrDecrMask);
        return;
      
      case 36:
        object = child.getFirstChild();
        visitExpression(object, 0);
        index = object.getNext();
        visitExpression(index, 0);
        addIcode(-10);
        addUint8(incrDecrMask);
        stackChange(-1);
        return;
      
      case 67:
        ref = child.getFirstChild();
        visitExpression(ref, 0);
        addIcode(-11);
        addUint8(incrDecrMask);
        return;
    } 
    
    throw badTree(node);
  }



  
  private void visitLiteral(Node node, Node child) {
    int count, type = node.getType();
    
    Object[] propertyIds = null;
    if (type == 65) {
      count = 0;
      for (Node n = child; n != null; n = n.getNext()) {
        count++;
      }
    } else if (type == 66) {
      propertyIds = (Object[])node.getProp(12);
      count = propertyIds.length;
    } else {
      throw badTree(node);
    } 
    addIndexOp(-29, count);
    stackChange(2);
    while (child != null) {
      int childType = child.getType();
      if (childType == 151) {
        visitExpression(child.getFirstChild(), 0);
        addIcode(-57);
      } else if (childType == 152) {
        visitExpression(child.getFirstChild(), 0);
        addIcode(-58);
      } else {
        visitExpression(child, 0);
        addIcode(-30);
      } 
      stackChange(-1);
      child = child.getNext();
    } 
    if (type == 65) {
      int[] skipIndexes = (int[])node.getProp(11);
      if (skipIndexes == null) {
        addToken(65);
      } else {
        int index = this.literalIds.size();
        this.literalIds.add(skipIndexes);
        addIndexOp(-31, index);
      } 
    } else {
      int index = this.literalIds.size();
      this.literalIds.add(propertyIds);
      addIndexOp(66, index);
    } 
    stackChange(-1);
  }






  
  private void visitArrayComprehension(Node node, Node initStmt, Node expr) {
    visitStatement(initStmt, this.stackDepth);
    visitExpression(expr, 0);
  }

  
  private int getLocalBlockRef(Node node) {
    Node localBlock = (Node)node.getProp(3);
    return localBlock.getExistingIntProp(2);
  }

  
  private int getTargetLabel(Node target) {
    int label = target.labelId();
    if (label != -1) {
      return label;
    }
    label = this.labelTableTop;
    if (this.labelTable == null || label == this.labelTable.length) {
      if (this.labelTable == null) {
        this.labelTable = new int[32];
      } else {
        int[] tmp = new int[this.labelTable.length * 2];
        System.arraycopy(this.labelTable, 0, tmp, 0, label);
        this.labelTable = tmp;
      } 
    }
    this.labelTableTop = label + 1;
    this.labelTable[label] = -1;
    
    target.labelId(label);
    return label;
  }

  
  private void markTargetLabel(Node target) {
    int label = getTargetLabel(target);
    if (this.labelTable[label] != -1)
    {
      Kit.codeBug();
    }
    this.labelTable[label] = this.iCodeTop;
  }

  
  private void addGoto(Node target, int gotoOp) {
    int label = getTargetLabel(target);
    if (label >= this.labelTableTop) Kit.codeBug(); 
    int targetPC = this.labelTable[label];
    
    if (targetPC != -1) {
      addBackwardGoto(gotoOp, targetPC);
    } else {
      int gotoPC = this.iCodeTop;
      addGotoOp(gotoOp);
      int top = this.fixupTableTop;
      if (this.fixupTable == null || top == this.fixupTable.length) {
        if (this.fixupTable == null) {
          this.fixupTable = new long[40];
        } else {
          long[] tmp = new long[this.fixupTable.length * 2];
          System.arraycopy(this.fixupTable, 0, tmp, 0, top);
          this.fixupTable = tmp;
        } 
      }
      this.fixupTableTop = top + 1;
      this.fixupTable[top] = label << 32L | gotoPC;
    } 
  }

  
  private void fixLabelGotos() {
    for (int i = 0; i < this.fixupTableTop; i++) {
      long fixup = this.fixupTable[i];
      int label = (int)(fixup >> 32L);
      int jumpSource = (int)fixup;
      int pc = this.labelTable[label];
      if (pc == -1)
      {
        throw Kit.codeBug();
      }
      resolveGoto(jumpSource, pc);
    } 
    this.fixupTableTop = 0;
  }

  
  private void addBackwardGoto(int gotoOp, int jumpPC) {
    int fromPC = this.iCodeTop;
    
    if (fromPC <= jumpPC) throw Kit.codeBug(); 
    addGotoOp(gotoOp);
    resolveGoto(fromPC, jumpPC);
  }


  
  private void resolveForwardGoto(int fromPC) {
    if (this.iCodeTop < fromPC + 3) throw Kit.codeBug(); 
    resolveGoto(fromPC, this.iCodeTop);
  }

  
  private void resolveGoto(int fromPC, int jumpPC) {
    int offset = jumpPC - fromPC;
    
    if (0 <= offset && offset <= 2) throw Kit.codeBug(); 
    int offsetSite = fromPC + 1;
    if (offset != (short)offset) {
      if (this.itsData.longJumps == null) {
        this.itsData.longJumps = new UintMap();
      }
      this.itsData.longJumps.put(offsetSite, jumpPC);
      offset = 0;
    } 
    byte[] array = this.itsData.itsICode;
    array[offsetSite] = (byte)(offset >> 8);
    array[offsetSite + 1] = (byte)offset;
  }

  
  private void addToken(int token) {
    if (!Icode.validTokenCode(token)) throw Kit.codeBug(); 
    addUint8(token);
  }

  
  private void addIcode(int icode) {
    if (!Icode.validIcode(icode)) throw Kit.codeBug();
    
    addUint8(icode & 0xFF);
  }

  
  private void addUint8(int value) {
    if ((value & 0xFFFFFF00) != 0) throw Kit.codeBug(); 
    byte[] array = this.itsData.itsICode;
    int top = this.iCodeTop;
    if (top == array.length) {
      array = increaseICodeCapacity(1);
    }
    array[top] = (byte)value;
    this.iCodeTop = top + 1;
  }

  
  private void addUint16(int value) {
    if ((value & 0xFFFF0000) != 0) throw Kit.codeBug(); 
    byte[] array = this.itsData.itsICode;
    int top = this.iCodeTop;
    if (top + 2 > array.length) {
      array = increaseICodeCapacity(2);
    }
    array[top] = (byte)(value >>> 8);
    array[top + 1] = (byte)value;
    this.iCodeTop = top + 2;
  }

  
  private void addInt(int i) {
    byte[] array = this.itsData.itsICode;
    int top = this.iCodeTop;
    if (top + 4 > array.length) {
      array = increaseICodeCapacity(4);
    }
    array[top] = (byte)(i >>> 24);
    array[top + 1] = (byte)(i >>> 16);
    array[top + 2] = (byte)(i >>> 8);
    array[top + 3] = (byte)i;
    this.iCodeTop = top + 4;
  }

  
  private int getDoubleIndex(double num) {
    int index = this.doubleTableTop;
    if (index == 0) {
      this.itsData.itsDoubleTable = new double[64];
    } else if (this.itsData.itsDoubleTable.length == index) {
      double[] na = new double[index * 2];
      System.arraycopy(this.itsData.itsDoubleTable, 0, na, 0, index);
      this.itsData.itsDoubleTable = na;
    } 
    this.itsData.itsDoubleTable[index] = num;
    this.doubleTableTop = index + 1;
    return index;
  }

  
  private void addGotoOp(int gotoOp) {
    byte[] array = this.itsData.itsICode;
    int top = this.iCodeTop;
    if (top + 3 > array.length) {
      array = increaseICodeCapacity(3);
    }
    array[top] = (byte)gotoOp;
    
    this.iCodeTop = top + 1 + 2;
  }

  
  private void addVarOp(int op, int varIndex) {
    switch (op) {
      case 156:
        if (varIndex < 128) {
          addIcode(-61);
          addUint8(varIndex);
          return;
        } 
        addIndexOp(-60, varIndex);
        return;
      case 55:
      case 56:
        if (varIndex < 128) {
          addIcode((op == 55) ? -48 : -49);
          addUint8(varIndex);
          return;
        } 
      
      case -7:
        addIndexOp(op, varIndex);
        return;
    } 
    throw Kit.codeBug();
  }

  
  private void addStringOp(int op, String str) {
    addStringPrefix(str);
    if (Icode.validIcode(op)) {
      addIcode(op);
    } else {
      addToken(op);
    } 
  }

  
  private void addIndexOp(int op, int index) {
    addIndexPrefix(index);
    if (Icode.validIcode(op)) {
      addIcode(op);
    } else {
      addToken(op);
    } 
  }

  
  private void addStringPrefix(String str) {
    int index = this.strings.get(str, -1);
    if (index == -1) {
      index = this.strings.size();
      this.strings.put(str, index);
    } 
    if (index < 4) {
      addIcode(-41 - index);
    } else if (index <= 255) {
      addIcode(-45);
      addUint8(index);
    } else if (index <= 65535) {
      addIcode(-46);
      addUint16(index);
    } else {
      addIcode(-47);
      addInt(index);
    } 
  }

  
  private void addIndexPrefix(int index) {
    if (index < 0) Kit.codeBug(); 
    if (index < 6) {
      addIcode(-32 - index);
    } else if (index <= 255) {
      addIcode(-38);
      addUint8(index);
    } else if (index <= 65535) {
      addIcode(-39);
      addUint16(index);
    } else {
      addIcode(-40);
      addInt(index);
    } 
  }



  
  private void addExceptionHandler(int icodeStart, int icodeEnd, int handlerStart, boolean isFinally, int exceptionObjectLocal, int scopeLocal) {
    int top = this.exceptionTableTop;
    int[] table = this.itsData.itsExceptionTable;
    if (table == null) {
      if (top != 0) Kit.codeBug(); 
      table = new int[12];
      this.itsData.itsExceptionTable = table;
    } else if (table.length == top) {
      table = new int[table.length * 2];
      System.arraycopy(this.itsData.itsExceptionTable, 0, table, 0, top);
      this.itsData.itsExceptionTable = table;
    } 
    table[top + 0] = icodeStart;
    table[top + 1] = icodeEnd;
    table[top + 2] = handlerStart;
    table[top + 3] = isFinally ? 1 : 0;
    table[top + 4] = exceptionObjectLocal;
    table[top + 5] = scopeLocal;
    
    this.exceptionTableTop = top + 6;
  }

  
  private byte[] increaseICodeCapacity(int extraSize) {
    int capacity = this.itsData.itsICode.length;
    int top = this.iCodeTop;
    if (top + extraSize <= capacity) throw Kit.codeBug(); 
    capacity *= 2;
    if (top + extraSize > capacity) {
      capacity = top + extraSize;
    }
    byte[] array = new byte[capacity];
    System.arraycopy(this.itsData.itsICode, 0, array, 0, top);
    this.itsData.itsICode = array;
    return array;
  }

  
  private void stackChange(int change) {
    if (change <= 0) {
      this.stackDepth += change;
    } else {
      int newDepth = this.stackDepth + change;
      if (newDepth > this.itsData.itsMaxStack) {
        this.itsData.itsMaxStack = newDepth;
      }
      this.stackDepth = newDepth;
    } 
  }

  
  private int allocLocal() {
    int localSlot = this.localTop;
    this.localTop++;
    if (this.localTop > this.itsData.itsMaxLocals) {
      this.itsData.itsMaxLocals = this.localTop;
    }
    return localSlot;
  }

  
  private void releaseLocal(int localSlot) {
    this.localTop--;
    if (localSlot != this.localTop) Kit.codeBug(); 
  }
}
