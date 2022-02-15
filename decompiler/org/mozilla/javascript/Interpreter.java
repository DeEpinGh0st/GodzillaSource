package org.mozilla.javascript;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.debug.DebugFrame;
































public final class Interpreter
  extends Icode
  implements Evaluator
{
  InterpreterData itsData;
  static final int EXCEPTION_TRY_START_SLOT = 0;
  static final int EXCEPTION_TRY_END_SLOT = 1;
  static final int EXCEPTION_HANDLER_SLOT = 2;
  static final int EXCEPTION_TYPE_SLOT = 3;
  static final int EXCEPTION_LOCAL_SLOT = 4;
  static final int EXCEPTION_SCOPE_SLOT = 5;
  static final int EXCEPTION_SLOT_SIZE = 6;
  
  private static class CallFrame
    implements Cloneable, Serializable
  {
    static final long serialVersionUID = -2843792508994958978L;
    CallFrame parentFrame;
    int frameIndex;
    boolean frozen;
    InterpretedFunction fnOrScript;
    InterpreterData idata;
    Object[] stack;
    int[] stackAttributes;
    double[] sDbl;
    CallFrame varSource;
    int localShift;
    int emptyStackTop;
    DebugFrame debuggerFrame;
    boolean useActivation;
    boolean isContinuationsTopFrame;
    Scriptable thisObj;
    Object result;
    double resultDbl;
    int pc;
    int pcPrevBranch;
    int pcSourceLineStart;
    Scriptable scope;
    int savedStackTop;
    int savedCallOp;
    Object throwable;
    
    private CallFrame() {}
    
    CallFrame cloneFrozen() {
      CallFrame copy;
      if (!this.frozen) Kit.codeBug();

      
      try {
        copy = (CallFrame)clone();
      } catch (CloneNotSupportedException ex) {
        throw new IllegalStateException();
      } 



      
      copy.stack = (Object[])this.stack.clone();
      copy.stackAttributes = (int[])this.stackAttributes.clone();
      copy.sDbl = (double[])this.sDbl.clone();
      
      copy.frozen = false;
      return copy;
    }
  }

  
  private static final class ContinuationJump
    implements Serializable
  {
    static final long serialVersionUID = 7687739156004308247L;
    Interpreter.CallFrame capturedFrame;
    Interpreter.CallFrame branchFrame;
    Object result;
    double resultDbl;
    
    ContinuationJump(NativeContinuation c, Interpreter.CallFrame current) {
      this.capturedFrame = (Interpreter.CallFrame)c.getImplementation();
      if (this.capturedFrame == null || current == null) {


        
        this.branchFrame = null;
      }
      else {
        
        Interpreter.CallFrame chain1 = this.capturedFrame;
        Interpreter.CallFrame chain2 = current;


        
        int diff = chain1.frameIndex - chain2.frameIndex;
        if (diff != 0) {
          if (diff < 0) {

            
            chain1 = current;
            chain2 = this.capturedFrame;
            diff = -diff;
          } 
          while (true) {
            chain1 = chain1.parentFrame;
            if (--diff == 0) {
              if (chain1.frameIndex != chain2.frameIndex) Kit.codeBug(); 
              break;
            } 
          } 
        } 
        while (chain1 != chain2 && chain1 != null) {
          chain1 = chain1.parentFrame;
          chain2 = chain2.parentFrame;
        } 
        
        this.branchFrame = chain1;
        if (this.branchFrame != null && !this.branchFrame.frozen)
          Kit.codeBug(); 
      } 
    }
  }
  
  private static CallFrame captureFrameForGenerator(CallFrame frame) {
    frame.frozen = true;
    CallFrame result = frame.cloneFrozen();
    frame.frozen = false;

    
    result.parentFrame = null;
    result.frameIndex = 0;
    
    return result;
  }



















  
  public Object compile(CompilerEnvirons compilerEnv, ScriptNode tree, String encodedSource, boolean returnFunction) {
    CodeGenerator cgen = new CodeGenerator();
    this.itsData = cgen.compile(compilerEnv, tree, encodedSource, returnFunction);
    return this.itsData;
  }

  
  public Script createScriptObject(Object bytecode, Object staticSecurityDomain) {
    if (bytecode != this.itsData)
    {
      Kit.codeBug();
    }
    return InterpretedFunction.createScript(this.itsData, staticSecurityDomain);
  }

  
  public void setEvalScriptFlag(Script script) {
    ((InterpretedFunction)script).idata.evalScriptFlag = true;
  }



  
  public Function createFunctionObject(Context cx, Scriptable scope, Object bytecode, Object staticSecurityDomain) {
    if (bytecode != this.itsData)
    {
      Kit.codeBug();
    }
    return InterpretedFunction.createFunction(cx, scope, this.itsData, staticSecurityDomain);
  }

  
  private static int getShort(byte[] iCode, int pc) {
    return iCode[pc] << 8 | iCode[pc + 1] & 0xFF;
  }
  
  private static int getIndex(byte[] iCode, int pc) {
    return (iCode[pc] & 0xFF) << 8 | iCode[pc + 1] & 0xFF;
  }
  
  private static int getInt(byte[] iCode, int pc) {
    return iCode[pc] << 24 | (iCode[pc + 1] & 0xFF) << 16 | (iCode[pc + 2] & 0xFF) << 8 | iCode[pc + 3] & 0xFF;
  }



  
  private static int getExceptionHandler(CallFrame frame, boolean onlyFinally) {
    int[] exceptionTable = frame.idata.itsExceptionTable;
    if (exceptionTable == null)
    {
      return -1;
    }



    
    int pc = frame.pc - 1;

    
    int best = -1, bestStart = 0, bestEnd = 0;
    for (int i = 0; i != exceptionTable.length; i += 6) {
      int start = exceptionTable[i + 0];
      int end = exceptionTable[i + 1];
      if (start > pc || pc >= end) {
        continue;
      }
      if (onlyFinally && exceptionTable[i + 3] != 1) {
        continue;
      }
      if (best >= 0) {


        
        if (bestEnd < end) {
          continue;
        }
        
        if (bestStart > start) Kit.codeBug(); 
        if (bestEnd == end) Kit.codeBug(); 
      } 
      best = i;
      bestStart = start;
      bestEnd = end; continue;
    } 
    return best;
  }









































































































  
  static void dumpICode(InterpreterData idata) {}









































































































  
  private static int bytecodeSpan(int bytecode) {
    switch (bytecode) {
      
      case -63:
      case -62:
      case 50:
      case 72:
        return 3;

      
      case -54:
      case -23:
      case -6:
      case 5:
      case 6:
      case 7:
        return 3;



      
      case -21:
        return 5;

      
      case 57:
        return 2;

      
      case -11:
      case -10:
      case -9:
      case -8:
      case -7:
        return 2;

      
      case -27:
        return 3;

      
      case -28:
        return 5;

      
      case -38:
        return 2;

      
      case -39:
        return 3;

      
      case -40:
        return 5;

      
      case -45:
        return 2;

      
      case -46:
        return 3;

      
      case -47:
        return 5;

      
      case -61:
      case -49:
      case -48:
        return 2;

      
      case -26:
        return 3;
    } 
    if (!validBytecode(bytecode)) throw Kit.codeBug(); 
    return 1;
  }

  
  static int[] getLineNumbers(InterpreterData data) {
    UintMap presentLines = new UintMap();
    
    byte[] iCode = data.itsICode;
    int iCodeLength = iCode.length; int pc;
    for (pc = 0; pc != iCodeLength; ) {
      int bytecode = iCode[pc];
      int span = bytecodeSpan(bytecode);
      if (bytecode == -26) {
        if (span != 3) Kit.codeBug(); 
        int line = getIndex(iCode, pc + 1);
        presentLines.put(line, 0);
      } 
      pc += span;
    } 
    
    return presentLines.getKeys();
  }
  
  public void captureStackInfo(RhinoException ex) {
    CallFrame[] array;
    Context cx = Context.getCurrentContext();
    if (cx == null || cx.lastInterpreterFrame == null) {
      
      ex.interpreterStackInfo = null;
      ex.interpreterLineData = null;
      
      return;
    } 
    
    if (cx.previousInterpreterInvocations == null || cx.previousInterpreterInvocations.size() == 0) {

      
      array = new CallFrame[1];
    } else {
      int previousCount = cx.previousInterpreterInvocations.size();
      if (cx.previousInterpreterInvocations.peek() == cx.lastInterpreterFrame)
      {




        
        previousCount--;
      }
      array = new CallFrame[previousCount + 1];
      cx.previousInterpreterInvocations.toArray((Object[])array);
    } 
    array[array.length - 1] = (CallFrame)cx.lastInterpreterFrame;
    
    int interpreterFrameCount = 0;
    for (int i = 0; i != array.length; i++) {
      interpreterFrameCount += 1 + (array[i]).frameIndex;
    }
    
    int[] linePC = new int[interpreterFrameCount];

    
    int linePCIndex = interpreterFrameCount;
    for (int j = array.length; j != 0; ) {
      j--;
      CallFrame frame = array[j];
      while (frame != null) {
        linePCIndex--;
        linePC[linePCIndex] = frame.pcSourceLineStart;
        frame = frame.parentFrame;
      } 
    } 
    if (linePCIndex != 0) Kit.codeBug();
    
    ex.interpreterStackInfo = array;
    ex.interpreterLineData = linePC;
  }

  
  public String getSourcePositionFromStack(Context cx, int[] linep) {
    CallFrame frame = (CallFrame)cx.lastInterpreterFrame;
    InterpreterData idata = frame.idata;
    if (frame.pcSourceLineStart >= 0) {
      linep[0] = getIndex(idata.itsICode, frame.pcSourceLineStart);
    } else {
      linep[0] = 0;
    } 
    return idata.itsSourceFile;
  }


  
  public String getPatchedStack(RhinoException ex, String nativeStackTrace) {
    String tag = "org.mozilla.javascript.Interpreter.interpretLoop";
    StringBuilder sb = new StringBuilder(nativeStackTrace.length() + 1000);
    String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
    
    CallFrame[] array = (CallFrame[])ex.interpreterStackInfo;
    int[] linePC = ex.interpreterLineData;
    int arrayIndex = array.length;
    int linePCIndex = linePC.length;
    int offset = 0;
    while (arrayIndex != 0) {
      arrayIndex--;
      int pos = nativeStackTrace.indexOf(tag, offset);
      if (pos < 0) {
        break;
      }

      
      pos += tag.length();
      
      for (; pos != nativeStackTrace.length(); pos++) {
        char c = nativeStackTrace.charAt(pos);
        if (c == '\n' || c == '\r') {
          break;
        }
      } 
      sb.append(nativeStackTrace.substring(offset, pos));
      offset = pos;
      
      CallFrame frame = array[arrayIndex];
      while (frame != null) {
        if (linePCIndex == 0) Kit.codeBug(); 
        linePCIndex--;
        InterpreterData idata = frame.idata;
        sb.append(lineSeparator);
        sb.append("\tat script");
        if (idata.itsName != null && idata.itsName.length() != 0) {
          sb.append('.');
          sb.append(idata.itsName);
        } 
        sb.append('(');
        sb.append(idata.itsSourceFile);
        int pc = linePC[linePCIndex];
        if (pc >= 0) {
          
          sb.append(':');
          sb.append(getIndex(idata.itsICode, pc));
        } 
        sb.append(')');
        frame = frame.parentFrame;
      } 
    } 
    sb.append(nativeStackTrace.substring(offset));
    
    return sb.toString();
  }
  
  public List<String> getScriptStack(RhinoException ex) {
    ScriptStackElement[][] stack = getScriptStackElements(ex);
    List<String> list = new ArrayList<String>(stack.length);
    String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
    
    for (ScriptStackElement[] group : stack) {
      StringBuilder sb = new StringBuilder();
      for (ScriptStackElement elem : group) {
        elem.renderJavaStyle(sb);
        sb.append(lineSeparator);
      } 
      list.add(sb.toString());
    } 
    return list;
  }

  
  public ScriptStackElement[][] getScriptStackElements(RhinoException ex) {
    if (ex.interpreterStackInfo == null) {
      return (ScriptStackElement[][])null;
    }
    
    List<ScriptStackElement[]> list = (List)new ArrayList<ScriptStackElement>();
    
    CallFrame[] array = (CallFrame[])ex.interpreterStackInfo;
    int[] linePC = ex.interpreterLineData;
    int arrayIndex = array.length;
    int linePCIndex = linePC.length;
    while (arrayIndex != 0) {
      arrayIndex--;
      CallFrame frame = array[arrayIndex];
      List<ScriptStackElement> group = new ArrayList<ScriptStackElement>();
      while (frame != null) {
        if (linePCIndex == 0) Kit.codeBug(); 
        linePCIndex--;
        InterpreterData idata = frame.idata;
        String fileName = idata.itsSourceFile;
        String functionName = null;
        int lineNumber = -1;
        int pc = linePC[linePCIndex];
        if (pc >= 0) {
          lineNumber = getIndex(idata.itsICode, pc);
        }
        if (idata.itsName != null && idata.itsName.length() != 0) {
          functionName = idata.itsName;
        }
        frame = frame.parentFrame;
        group.add(new ScriptStackElement(fileName, functionName, lineNumber));
      } 
      list.add(group.toArray(new ScriptStackElement[group.size()]));
    } 
    return list.<ScriptStackElement[]>toArray(new ScriptStackElement[list.size()][]);
  }

  
  static String getEncodedSource(InterpreterData idata) {
    if (idata.encodedSource == null) {
      return null;
    }
    return idata.encodedSource.substring(idata.encodedSourceStart, idata.encodedSourceEnd);
  }




  
  private static void initFunction(Context cx, Scriptable scope, InterpretedFunction parent, int index) {
    InterpretedFunction fn = InterpretedFunction.createFunction(cx, scope, parent, index);
    ScriptRuntime.initFunction(cx, scope, fn, fn.idata.itsFunctionType, parent.idata.evalScriptFlag);
  }




  
  static Object interpret(InterpretedFunction ifun, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (!ScriptRuntime.hasTopCall(cx)) Kit.codeBug();
    
    if (cx.interpreterSecurityDomain != ifun.securityDomain) {
      Object savedDomain = cx.interpreterSecurityDomain;
      cx.interpreterSecurityDomain = ifun.securityDomain;
      try {
        return ifun.securityController.callWithDomain(ifun.securityDomain, cx, ifun, scope, thisObj, args);
      } finally {
        
        cx.interpreterSecurityDomain = savedDomain;
      } 
    } 
    
    CallFrame frame = new CallFrame();
    initFrame(cx, scope, thisObj, args, (double[])null, 0, args.length, ifun, (CallFrame)null, frame);
    
    frame.isContinuationsTopFrame = cx.isContinuationsTopCall;
    cx.isContinuationsTopCall = false;
    
    return interpretLoop(cx, frame, (Object)null);
  }
  static class GeneratorState { int operation; Object value; RuntimeException returnedException;
    
    GeneratorState(int operation, Object value) {
      this.operation = operation;
      this.value = value;
    } }









  
  public static Object resumeGenerator(Context cx, Scriptable scope, int operation, Object savedState, Object value) {
    CallFrame frame = (CallFrame)savedState;
    GeneratorState generatorState = new GeneratorState(operation, value);
    if (operation == 2)
      try {
        return interpretLoop(cx, frame, generatorState);
      } catch (RuntimeException e) {
        
        if (e != value) {
          throw e;
        }
        return Undefined.instance;
      }  
    Object result = interpretLoop(cx, frame, generatorState);
    if (generatorState.returnedException != null)
      throw generatorState.returnedException; 
    return result;
  }

  
  public static Object restartContinuation(NativeContinuation c, Context cx, Scriptable scope, Object[] args) {
    Object arg;
    if (!ScriptRuntime.hasTopCall(cx)) {
      return ScriptRuntime.doTopCall(c, cx, scope, null, args);
    }

    
    if (args.length == 0) {
      arg = Undefined.instance;
    } else {
      arg = args[0];
    } 
    
    CallFrame capturedFrame = (CallFrame)c.getImplementation();
    if (capturedFrame == null)
    {
      return arg;
    }
    
    ContinuationJump cjump = new ContinuationJump(c, null);
    
    cjump.result = arg;
    return interpretLoop(cx, (CallFrame)null, cjump);
  }






  
  private static Object interpretLoop(Context cx, CallFrame frame, Object throwable) {
    Object DBL_MRK = UniqueTag.DOUBLE_MARK;
    Object undefined = Undefined.instance;
    
    boolean instructionCounting = (cx.instructionThreshold != 0);

    
    int INVOCATION_COST = 100;
    
    int EXCEPTION_COST = 100;
    
    String stringReg = null;
    int indexReg = -1;
    
    if (cx.lastInterpreterFrame != null) {

      
      if (cx.previousInterpreterInvocations == null) {
        cx.previousInterpreterInvocations = new ObjArray();
      }
      cx.previousInterpreterInvocations.push(cx.lastInterpreterFrame);
    } 







    
    GeneratorState generatorState = null;
    if (throwable != null) {
      if (throwable instanceof GeneratorState) {
        generatorState = (GeneratorState)throwable;

        
        enterFrame(cx, frame, ScriptRuntime.emptyArgs, true);
        throwable = null;
      } else if (!(throwable instanceof ContinuationJump)) {
        
        Kit.codeBug();
      } 
    }
    
    Object interpreterResult = null;
    double interpreterResultDbl = 0.0D;
    
    label595: while (true) {
      boolean bool;
      try {
        if (throwable != null)
        
        { 
          
          frame = processThrowable(cx, throwable, frame, indexReg, instructionCounting);
          
          throwable = frame.throwable;
          frame.throwable = null; }
        
        else if (generatorState == null && frame.frozen) { Kit.codeBug(); }



        
        Object[] stack = frame.stack;
        double[] sDbl = frame.sDbl;
        Object[] vars = frame.varSource.stack;
        double[] varDbls = frame.varSource.sDbl;
        int[] varAttributes = frame.varSource.stackAttributes;
        byte[] iCode = frame.idata.itsICode;
        String[] strings = frame.idata.itsStringTable;




        
        int stackTop = frame.savedStackTop;

        
        cx.lastInterpreterFrame = frame; while (true) {
          Object object9; int sourceLine; Object object8; boolean bool1; int k; boolean valBln; int j, offset; Object object7, o; int rIntValue; double lDbl, rDbl; Object object6, object5, rhs, object4; Ref ref1; Object object3; Ref ref; Object object2, value; Callable fun; Object object1; boolean afterFirstScope; Object lhs, val, obj, name, re; int m; double d; Scriptable scriptable1; Object object13; Ref ref2; Object id; Scriptable funThisObj; Function function; Throwable caughtException; int enumType; Object object12, data[], object11; boolean bool2; Object object10; int n;
          Scriptable calleeScope;
          Object[] outArgs;
          Scriptable lastCatchScope;
          int i, getterSetters[];
          Object x, object14;
          int op = iCode[frame.pc++];


          
          switch (op)
          { case -62:
              if (!frame.frozen) {

                
                frame.pc--;
                CallFrame generatorFrame = captureFrameForGenerator(frame);
                generatorFrame.frozen = true;
                NativeGenerator generator = new NativeGenerator(frame.scope, generatorFrame.fnOrScript, generatorFrame);
                
                frame.result = generator;
                continue label595;
              } 



            
            case 72:
              if (!frame.frozen) {
                return freezeGenerator(cx, frame, stackTop, generatorState);
              }
              object9 = thawGenerator(frame, stackTop, generatorState, op);
              if (object9 != Scriptable.NOT_FOUND) {
                throwable = object9;
                break;
              } 
              continue;


            
            case -63:
              frame.frozen = true;
              sourceLine = getIndex(iCode, frame.pc);
              generatorState.returnedException = new JavaScriptException(NativeIterator.getStopIterationObject(frame.scope), frame.idata.itsSourceFile, sourceLine);
              continue label595;


            
            case 50:
              object8 = stack[stackTop];
              if (object8 == DBL_MRK) object8 = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
              stackTop--;
              
              m = getIndex(iCode, frame.pc);
              throwable = new JavaScriptException(object8, frame.idata.itsSourceFile, m);
              break;


            
            case 51:
              indexReg += frame.localShift;
              throwable = stack[indexReg];
              break;
            
            case 14:
            case 15:
            case 16:
            case 17:
              stackTop = doCompare(frame, op, stack, sDbl, stackTop);
              continue;
            
            case 52:
            case 53:
              stackTop = doInOrInstanceof(cx, op, stack, sDbl, stackTop);
              continue;
            
            case 12:
            case 13:
              stackTop--;
              bool1 = doEquals(stack, sDbl, stackTop);
              k = bool1 ^ ((op == 13) ? 1 : 0);
              stack[stackTop] = ScriptRuntime.wrapBoolean(k);
              continue;
            
            case 46:
            case 47:
              stackTop--;
              valBln = doShallowEquals(stack, sDbl, stackTop);
              j = valBln ^ ((op == 47) ? 1 : 0);
              stack[stackTop] = ScriptRuntime.wrapBoolean(j);
              continue;
            
            case 7:
              if (stack_boolean(frame, stackTop--)) {
                frame.pc += 2;
                continue;
              } 
            
            case 6:
              if (!stack_boolean(frame, stackTop--)) {
                frame.pc += 2;
                continue;
              } 
            
            case -6:
              if (!stack_boolean(frame, stackTop--)) {
                frame.pc += 2;
                continue;
              } 
              stack[stackTop--] = null;























































































































































































































































































































































































































































































































































































































































































































































































































































            
            case 5:
              if (instructionCounting) {
                addInstructionCount(cx, frame, 2);
              }
              offset = getShort(iCode, frame.pc);
              if (offset != 0) {
                
                frame.pc += offset - 1;
              } else {
                frame.pc = frame.idata.longJumps.getExistingInt(frame.pc);
              } 
              
              if (instructionCounting)
                frame.pcPrevBranch = frame.pc;  continue;case -23: stackTop++; stack[stackTop] = DBL_MRK; sDbl[stackTop] = (frame.pc + 2);case -24: if (stackTop == frame.emptyStackTop + 1) { indexReg += frame.localShift; stack[indexReg] = stack[stackTop]; sDbl[indexReg] = sDbl[stackTop]; stackTop--; continue; }  if (stackTop != frame.emptyStackTop) Kit.codeBug();  continue;case -25: if (instructionCounting) addInstructionCount(cx, frame, 0);  indexReg += frame.localShift; object7 = stack[indexReg]; if (object7 != DBL_MRK) { throwable = object7; break; }  frame.pc = (int)sDbl[indexReg]; if (instructionCounting) frame.pcPrevBranch = frame.pc;  continue;case -4: stack[stackTop] = null; stackTop--; continue;case -5: frame.result = stack[stackTop]; frame.resultDbl = sDbl[stackTop]; stack[stackTop] = null; stackTop--; continue;case -1: stack[stackTop + 1] = stack[stackTop]; sDbl[stackTop + 1] = sDbl[stackTop]; stackTop++; continue;case -2: stack[stackTop + 1] = stack[stackTop - 1]; sDbl[stackTop + 1] = sDbl[stackTop - 1]; stack[stackTop + 2] = stack[stackTop]; sDbl[stackTop + 2] = sDbl[stackTop]; stackTop += 2; continue;case -3: o = stack[stackTop]; stack[stackTop] = stack[stackTop - 1]; stack[stackTop - 1] = o; d = sDbl[stackTop]; sDbl[stackTop] = sDbl[stackTop - 1]; sDbl[stackTop - 1] = d; continue;case 4: frame.result = stack[stackTop]; frame.resultDbl = sDbl[stackTop]; stackTop--; continue label595;case 64: break;case -22: frame.result = undefined; continue label595;case 27: rIntValue = stack_int32(frame, stackTop); stack[stackTop] = DBL_MRK; sDbl[stackTop] = (rIntValue ^ 0xFFFFFFFF); continue;case 9: case 10: case 11: case 18: case 19: stackTop = doBitOp(frame, op, stack, sDbl, stackTop); continue;case 20: lDbl = stack_double(frame, stackTop - 1); n = stack_int32(frame, stackTop) & 0x1F; stack[--stackTop] = DBL_MRK; sDbl[stackTop] = (ScriptRuntime.toUint32(lDbl) >>> n); continue;case 28: case 29: rDbl = stack_double(frame, stackTop); stack[stackTop] = DBL_MRK; if (op == 29) rDbl = -rDbl;  sDbl[stackTop] = rDbl; continue;case 21: stackTop--; doAdd(stack, sDbl, stackTop, cx); continue;case 22: case 23: case 24: case 25: stackTop = doArithmetic(frame, op, stack, sDbl, stackTop); continue;case 26: stack[stackTop] = ScriptRuntime.wrapBoolean(!stack_boolean(frame, stackTop)); continue;case 49: stack[++stackTop] = ScriptRuntime.bind(cx, frame.scope, stringReg); continue;case 8: case 73: object6 = stack[stackTop]; if (object6 == DBL_MRK) object6 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; scriptable1 = (Scriptable)stack[stackTop]; stack[stackTop] = (op == 8) ? ScriptRuntime.setName(scriptable1, object6, cx, frame.scope, stringReg) : ScriptRuntime.strictSetName(scriptable1, object6, cx, frame.scope, stringReg); continue;case -59: object6 = stack[stackTop]; if (object6 == DBL_MRK) object6 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; scriptable1 = (Scriptable)stack[stackTop]; stack[stackTop] = ScriptRuntime.setConst(scriptable1, object6, cx, stringReg); continue;case 0: case 31: stackTop = doDelName(cx, frame, op, stack, sDbl, stackTop); continue;case 34: object5 = stack[stackTop]; if (object5 == DBL_MRK) object5 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.getObjectPropNoWarn(object5, stringReg, cx, frame.scope); continue;case 33: object5 = stack[stackTop]; if (object5 == DBL_MRK) object5 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.getObjectProp(object5, stringReg, cx, frame.scope); continue;case 35: rhs = stack[stackTop]; if (rhs == DBL_MRK) rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; object13 = stack[stackTop]; if (object13 == DBL_MRK) object13 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.setObjectProp(object13, stringReg, rhs, cx, frame.scope); continue;case -9: object4 = stack[stackTop]; if (object4 == DBL_MRK) object4 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.propIncrDecr(object4, stringReg, cx, frame.scope, iCode[frame.pc]); frame.pc++; continue;case 36: stackTop = doGetElem(cx, frame, stack, sDbl, stackTop); continue;case 37: stackTop = doSetElem(cx, frame, stack, sDbl, stackTop); continue;case -10: stackTop = doElemIncDec(cx, frame, iCode, stack, sDbl, stackTop); continue;case 67: ref1 = (Ref)stack[stackTop]; stack[stackTop] = ScriptRuntime.refGet(ref1, cx); continue;case 68: object3 = stack[stackTop]; if (object3 == DBL_MRK) object3 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; ref2 = (Ref)stack[stackTop]; stack[stackTop] = ScriptRuntime.refSet(ref2, object3, cx, frame.scope); continue;case 69: ref = (Ref)stack[stackTop]; stack[stackTop] = ScriptRuntime.refDel(ref, cx); continue;case -11: ref = (Ref)stack[stackTop]; stack[stackTop] = ScriptRuntime.refIncrDecr(ref, cx, frame.scope, iCode[frame.pc]); frame.pc++; continue;case 54: stackTop++; indexReg += frame.localShift; stack[stackTop] = stack[indexReg]; sDbl[stackTop] = sDbl[indexReg]; continue;case -56: indexReg += frame.localShift; stack[indexReg] = null; continue;case -15: stackTop++; stack[stackTop] = ScriptRuntime.getNameFunctionAndThis(stringReg, cx, frame.scope); stackTop++; stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx); continue;case -16: object2 = stack[stackTop]; if (object2 == DBL_MRK) object2 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.getPropFunctionAndThis(object2, stringReg, cx, frame.scope); stackTop++; stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx); continue;case -17: object2 = stack[stackTop - 1]; if (object2 == DBL_MRK) object2 = ScriptRuntime.wrapNumber(sDbl[stackTop - 1]);  id = stack[stackTop]; if (id == DBL_MRK) id = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop - 1] = ScriptRuntime.getElemFunctionAndThis(object2, id, cx, frame.scope); stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx); continue;case -18: value = stack[stackTop]; if (value == DBL_MRK) value = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.getValueFunctionAndThis(value, cx); stackTop++; stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx); continue;case -21: if (instructionCounting) cx.instructionCount += 100;  stackTop = doCallSpecial(cx, frame, stack, sDbl, stackTop, iCode, indexReg); continue;case -55: case 38: case 70: if (instructionCounting) cx.instructionCount += 100;  stackTop -= 1 + indexReg; fun = (Callable)stack[stackTop]; funThisObj = (Scriptable)stack[stackTop + 1]; if (op == 70) { Object[] arrayOfObject = getArgsArray(stack, sDbl, stackTop + 2, indexReg); stack[stackTop] = ScriptRuntime.callRef(fun, funThisObj, arrayOfObject, cx); continue; }  calleeScope = frame.scope; if (frame.useActivation) calleeScope = ScriptableObject.getTopLevelScope(frame.scope);  if (fun instanceof InterpretedFunction) { InterpretedFunction ifun = (InterpretedFunction)fun; if (frame.fnOrScript.securityDomain == ifun.securityDomain) { CallFrame callParentFrame = frame; CallFrame calleeFrame = new CallFrame(); if (op == -55) { callParentFrame = frame.parentFrame; exitFrame(cx, frame, (Object)null); }  initFrame(cx, calleeScope, funThisObj, stack, sDbl, stackTop + 2, indexReg, ifun, callParentFrame, calleeFrame); if (op != -55) { frame.savedStackTop = stackTop; frame.savedCallOp = op; }  frame = calleeFrame; continue label595; }  }  if (fun instanceof NativeContinuation) { ContinuationJump continuationJump = new ContinuationJump((NativeContinuation)fun, frame); if (indexReg == 0) { continuationJump.result = undefined; } else { continuationJump.result = stack[stackTop + 2]; continuationJump.resultDbl = sDbl[stackTop + 2]; }  throwable = continuationJump; break; }  if (fun instanceof IdFunctionObject) { IdFunctionObject ifun = (IdFunctionObject)fun; if (NativeContinuation.isContinuationConstructor(ifun)) { frame.stack[stackTop] = captureContinuation(cx, frame.parentFrame, false); continue; }  if (BaseFunction.isApplyOrCall(ifun)) { Callable applyCallable = ScriptRuntime.getCallable(funThisObj); if (applyCallable instanceof InterpretedFunction) { InterpretedFunction iApplyCallable = (InterpretedFunction)applyCallable; if (frame.fnOrScript.securityDomain == iApplyCallable.securityDomain) { frame = initFrameForApplyOrCall(cx, frame, indexReg, stack, sDbl, stackTop, op, calleeScope, ifun, iApplyCallable); continue label595; }  }  }  }  if (fun instanceof ScriptRuntime.NoSuchMethodShim) { ScriptRuntime.NoSuchMethodShim noSuchMethodShim = (ScriptRuntime.NoSuchMethodShim)fun; Callable noSuchMethodMethod = noSuchMethodShim.noSuchMethodMethod; if (noSuchMethodMethod instanceof InterpretedFunction) { InterpretedFunction ifun = (InterpretedFunction)noSuchMethodMethod; if (frame.fnOrScript.securityDomain == ifun.securityDomain) { frame = initFrameForNoSuchMethod(cx, frame, indexReg, stack, sDbl, stackTop, op, funThisObj, calleeScope, noSuchMethodShim, ifun); continue label595; }  }  }  cx.lastInterpreterFrame = frame; frame.savedCallOp = op; frame.savedStackTop = stackTop; stack[stackTop] = fun.call(cx, calleeScope, funThisObj, getArgsArray(stack, sDbl, stackTop + 2, indexReg)); continue;case 30: if (instructionCounting) cx.instructionCount += 100;  stackTop -= indexReg; object1 = stack[stackTop]; if (object1 instanceof InterpretedFunction) { InterpretedFunction f = (InterpretedFunction)object1; if (frame.fnOrScript.securityDomain == f.securityDomain) { Scriptable newInstance = f.createObject(cx, frame.scope); CallFrame calleeFrame = new CallFrame(); initFrame(cx, frame.scope, newInstance, stack, sDbl, stackTop + 1, indexReg, f, frame, calleeFrame); stack[stackTop] = newInstance; frame.savedStackTop = stackTop; frame.savedCallOp = op; frame = calleeFrame; continue label595; }  }  if (!(object1 instanceof Function)) { if (object1 == DBL_MRK) object1 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  throw ScriptRuntime.notFunctionError(object1); }  function = (Function)object1; if (function instanceof IdFunctionObject) { IdFunctionObject ifun = (IdFunctionObject)function; if (NativeContinuation.isContinuationConstructor(ifun)) { frame.stack[stackTop] = captureContinuation(cx, frame.parentFrame, false); continue; }  }  outArgs = getArgsArray(stack, sDbl, stackTop + 1, indexReg); stack[stackTop] = function.construct(cx, frame.scope, outArgs); continue;case 32: object1 = stack[stackTop]; if (object1 == DBL_MRK) object1 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.typeof(object1); continue;case -14: stack[++stackTop] = ScriptRuntime.typeofName(frame.scope, stringReg); continue;case 41: stack[++stackTop] = stringReg; continue;case -27: stackTop++; stack[stackTop] = DBL_MRK; sDbl[stackTop] = getShort(iCode, frame.pc); frame.pc += 2; continue;case -28: stackTop++; stack[stackTop] = DBL_MRK; sDbl[stackTop] = getInt(iCode, frame.pc); frame.pc += 4; continue;case 40: stackTop++; stack[stackTop] = DBL_MRK; sDbl[stackTop] = frame.idata.itsDoubleTable[indexReg]; continue;case 39: stack[++stackTop] = ScriptRuntime.name(cx, frame.scope, stringReg); continue;case -8: stack[++stackTop] = ScriptRuntime.nameIncrDecr(frame.scope, stringReg, cx, iCode[frame.pc]); frame.pc++; continue;case -61: indexReg = iCode[frame.pc++];case 156: stackTop = doSetConstVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg); continue;case -49: indexReg = iCode[frame.pc++];case 56: stackTop = doSetVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg); continue;case -48: indexReg = iCode[frame.pc++];case 55: stackTop = doGetVar(frame, stack, sDbl, stackTop, vars, varDbls, indexReg); continue;case -7: stackTop = doVarIncDec(cx, frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg); continue;case -51: stackTop++; stack[stackTop] = DBL_MRK; sDbl[stackTop] = 0.0D; continue;case -52: stackTop++; stack[stackTop] = DBL_MRK; sDbl[stackTop] = 1.0D; continue;case 42: stack[++stackTop] = null; continue;case 43: stack[++stackTop] = frame.thisObj; continue;case 63: stack[++stackTop] = frame.fnOrScript; continue;case 44: stack[++stackTop] = Boolean.FALSE; continue;case 45: stack[++stackTop] = Boolean.TRUE; continue;case -50: stack[++stackTop] = undefined; continue;case 2: object1 = stack[stackTop]; if (object1 == DBL_MRK) object1 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; frame.scope = ScriptRuntime.enterWith(object1, cx, frame.scope); continue;case 3: frame.scope = ScriptRuntime.leaveWith(frame.scope); continue;case 57: stackTop--; indexReg += frame.localShift; afterFirstScope = (frame.idata.itsICode[frame.pc] != 0); caughtException = (Throwable)stack[stackTop + 1]; if (!afterFirstScope) { lastCatchScope = null; } else { lastCatchScope = (Scriptable)stack[indexReg]; }  stack[indexReg] = ScriptRuntime.newCatchScope(caughtException, lastCatchScope, stringReg, cx, frame.scope); frame.pc++; continue;case 58: case 59: case 60: lhs = stack[stackTop]; if (lhs == DBL_MRK) lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; indexReg += frame.localShift; enumType = (op == 58) ? 0 : ((op == 59) ? 1 : 2); stack[indexReg] = ScriptRuntime.enumInit(lhs, cx, frame.scope, enumType); continue;case 61: case 62: indexReg += frame.localShift; val = stack[indexReg]; stackTop++; stack[stackTop] = (op == 61) ? ScriptRuntime.enumNext(val) : ScriptRuntime.enumId(val, cx); continue;case 71: obj = stack[stackTop]; if (obj == DBL_MRK) obj = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.specialRef(obj, stringReg, cx, frame.scope); continue;case 77: stackTop = doRefMember(cx, stack, sDbl, stackTop, indexReg); continue;case 78: stackTop = doRefNsMember(cx, stack, sDbl, stackTop, indexReg); continue;case 79: name = stack[stackTop]; if (name == DBL_MRK) name = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.nameRef(name, cx, frame.scope, indexReg); continue;case 80: stackTop = doRefNsName(cx, frame, stack, sDbl, stackTop, indexReg); continue;case -12: indexReg += frame.localShift; frame.scope = (Scriptable)stack[indexReg]; continue;case -13: indexReg += frame.localShift; stack[indexReg] = frame.scope; continue;case -19: stack[++stackTop] = InterpretedFunction.createFunction(cx, frame.scope, frame.fnOrScript, indexReg); continue;case -20: initFunction(cx, frame.scope, frame.fnOrScript, indexReg); continue;case 48: re = frame.idata.itsRegExpLiterals[indexReg]; stack[++stackTop] = ScriptRuntime.wrapRegExp(cx, frame.scope, re); continue;case -29: stackTop++; stack[stackTop] = new int[indexReg]; stackTop++; stack[stackTop] = new Object[indexReg]; sDbl[stackTop] = 0.0D; continue;case -30: object12 = stack[stackTop]; if (object12 == DBL_MRK) object12 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; i = (int)sDbl[stackTop]; ((Object[])stack[stackTop])[i] = object12; sDbl[stackTop] = (i + 1); continue;case -57: object12 = stack[stackTop]; stackTop--; i = (int)sDbl[stackTop]; ((Object[])stack[stackTop])[i] = object12; ((int[])stack[stackTop - 1])[i] = -1; sDbl[stackTop] = (i + 1); continue;case -58: object12 = stack[stackTop]; stackTop--; i = (int)sDbl[stackTop]; ((Object[])stack[stackTop])[i] = object12; ((int[])stack[stackTop - 1])[i] = 1; sDbl[stackTop] = (i + 1); continue;case -31: case 65: case 66: data = (Object[])stack[stackTop]; stackTop--; getterSetters = (int[])stack[stackTop]; if (op == 66) { Object[] ids = (Object[])frame.idata.literalIds[indexReg]; object14 = ScriptRuntime.newObjectLiteral(ids, data, getterSetters, cx, frame.scope); } else { int[] skipIndexces = null; if (op == -31)
                  skipIndexces = (int[])frame.idata.literalIds[indexReg];  object14 = ScriptRuntime.newArrayLiteral(data, skipIndexces, cx, frame.scope); }  stack[stackTop] = object14; continue;case -53: object11 = stack[stackTop]; if (object11 == DBL_MRK)
                object11 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stackTop--; frame.scope = ScriptRuntime.enterDotQuery(object11, frame.scope); continue;case -54: bool2 = stack_boolean(frame, stackTop); x = ScriptRuntime.updateDotQuery(bool2, frame.scope); if (x != null) { stack[stackTop] = x; frame.scope = ScriptRuntime.leaveDotQuery(frame.scope); frame.pc += 2; continue; }  stackTop--;case 74: object10 = stack[stackTop]; if (object10 == DBL_MRK)
                object10 = ScriptRuntime.wrapNumber(sDbl[stackTop]);  stack[stackTop] = ScriptRuntime.setDefaultNamespace(object10, cx); continue;case 75: object10 = stack[stackTop]; if (object10 != DBL_MRK)
                stack[stackTop] = ScriptRuntime.escapeAttributeValue(object10, cx);  continue;case 76: object10 = stack[stackTop]; if (object10 != DBL_MRK)
                stack[stackTop] = ScriptRuntime.escapeTextValue(object10, cx);  continue;case -64: if (frame.debuggerFrame != null)
                frame.debuggerFrame.onDebuggerStatement(cx);  continue;case -26: frame.pcSourceLineStart = frame.pc; if (frame.debuggerFrame != null) { int line = getIndex(iCode, frame.pc); frame.debuggerFrame.onLineChange(cx, line); }  frame.pc += 2; continue;case -32: indexReg = 0; continue;case -33: indexReg = 1; continue;case -34: indexReg = 2; continue;case -35: indexReg = 3; continue;case -36: indexReg = 4; continue;case -37: indexReg = 5; continue;case -38: indexReg = 0xFF & iCode[frame.pc]; frame.pc++; continue;case -39: indexReg = getIndex(iCode, frame.pc); frame.pc += 2; continue;case -40: indexReg = getInt(iCode, frame.pc); frame.pc += 4; continue;case -41: stringReg = strings[0]; continue;case -42: stringReg = strings[1]; continue;case -43: stringReg = strings[2]; continue;case -44: stringReg = strings[3]; continue;case -45: stringReg = strings[0xFF & iCode[frame.pc]]; frame.pc++; continue;case -46: stringReg = strings[getIndex(iCode, frame.pc)]; frame.pc += 2; continue;case -47: stringReg = strings[getInt(iCode, frame.pc)]; frame.pc += 4; continue;default: dumpICode(frame.idata); throw new RuntimeException("Unknown icode : " + op + " @ pc : " + (frame.pc - 1)); }  exitFrame(cx, frame, (Object)null);
          interpreterResult = frame.result;
          interpreterResultDbl = frame.resultDbl;
          if (frame.parentFrame != null) {
            frame = frame.parentFrame;
            if (frame.frozen) {
              frame = frame.cloneFrozen();
            }
            setCallResult(frame, interpreterResult, interpreterResultDbl);
            
            interpreterResult = null;
            
            continue;
          } 
          break;
        } 
      } catch (Throwable ex) {
        if (throwable != null) {
          
          ex.printStackTrace(System.err);
          throw new IllegalStateException();
        } 
        throwable = ex;
      } 



      
      if (throwable == null) Kit.codeBug();

      
      int EX_CATCH_STATE = 2;
      int EX_FINALLY_STATE = 1;
      int EX_NO_JS_STATE = 0;

      
      ContinuationJump cjump = null;
      
      if (generatorState != null && generatorState.operation == 2 && throwable == generatorState.value) {


        
        bool = true;
      } else if (throwable instanceof JavaScriptException) {
        bool = true;
      } else if (throwable instanceof EcmaError) {
        
        bool = true;
      } else if (throwable instanceof EvaluatorException) {
        bool = true;
      } else if (throwable instanceof ContinuationPending) {
        bool = false;
      } else if (throwable instanceof RuntimeException) {
        bool = cx.hasFeature(13) ? true : true;
      
      }
      else if (throwable instanceof Error) {
        bool = cx.hasFeature(13) ? true : false;
      
      }
      else if (throwable instanceof ContinuationJump) {
        
        bool = true;
        cjump = (ContinuationJump)throwable;
      } else {
        bool = cx.hasFeature(13) ? true : true;
      } 


      
      if (instructionCounting) {
        try {
          addInstructionCount(cx, frame, 100);
        } catch (RuntimeException ex) {
          throwable = ex;
          bool = true;
        } catch (Error ex) {

          
          throwable = ex;
          cjump = null;
          bool = false;
        } 
      }
      if (frame.debuggerFrame != null && throwable instanceof RuntimeException) {


        
        RuntimeException rex = (RuntimeException)throwable;
        try {
          frame.debuggerFrame.onExceptionThrown(cx, rex);
        } catch (Throwable ex) {

          
          throwable = ex;
          cjump = null;
          bool = false;
        } 
      } 
      
      while (true) {
        if (bool) {
          boolean onlyFinally = (bool != 2);
          indexReg = getExceptionHandler(frame, onlyFinally);
          if (indexReg >= 0) {
            continue label595;
          }
        } 





        
        exitFrame(cx, frame, throwable);
        
        frame = frame.parentFrame;
        if (frame == null)
          break;  if (cjump != null && cjump.branchFrame == frame)
        {
          
          indexReg = -1;
        }
      } 


      
      if (cjump != null) {
        if (cjump.branchFrame != null)
        {
          Kit.codeBug();
        }
        if (cjump.capturedFrame != null) {
          
          indexReg = -1;
          
          continue;
        } 
        interpreterResult = cjump.result;
        interpreterResultDbl = cjump.resultDbl;
        throwable = null;
      } 

      
      break;
    } 

    
    if (cx.previousInterpreterInvocations != null && cx.previousInterpreterInvocations.size() != 0) {

      
      cx.lastInterpreterFrame = cx.previousInterpreterInvocations.pop();
    }
    else {
      
      cx.lastInterpreterFrame = null;
      
      cx.previousInterpreterInvocations = null;
    } 
    
    if (throwable != null) {
      if (throwable instanceof RuntimeException) {
        throw (RuntimeException)throwable;
      }
      
      throw (Error)throwable;
    } 

    
    return (interpreterResult != DBL_MRK) ? interpreterResult : ScriptRuntime.wrapNumber(interpreterResultDbl);
  }


  
  private static int doInOrInstanceof(Context cx, int op, Object[] stack, double[] sDbl, int stackTop) {
    boolean valBln;
    Object rhs = stack[stackTop];
    if (rhs == UniqueTag.DOUBLE_MARK) rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stackTop--;
    Object lhs = stack[stackTop];
    if (lhs == UniqueTag.DOUBLE_MARK) lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
    
    if (op == 52) {
      valBln = ScriptRuntime.in(lhs, rhs, cx);
    } else {
      valBln = ScriptRuntime.instanceOf(lhs, rhs, cx);
    } 
    stack[stackTop] = ScriptRuntime.wrapBoolean(valBln);
    return stackTop;
  }
  private static int doCompare(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
    boolean bool;
    double rDbl, lDbl;
    stackTop--;
    Object rhs = stack[stackTop + 1];
    Object lhs = stack[stackTop];





    
    if (rhs == UniqueTag.DOUBLE_MARK)
    { rDbl = sDbl[stackTop + 1];
      lDbl = stack_double(frame, stackTop); }
    else if (lhs == UniqueTag.DOUBLE_MARK)
    { rDbl = ScriptRuntime.toNumber(rhs);
      lDbl = sDbl[stackTop]; }
    else
    { boolean valBln;

















      
      switch (op)
      { case 17:
          valBln = ScriptRuntime.cmp_LE(rhs, lhs);













          
          stack[stackTop] = ScriptRuntime.wrapBoolean(valBln);
          return stackTop;case 15: valBln = ScriptRuntime.cmp_LE(lhs, rhs); stack[stackTop] = ScriptRuntime.wrapBoolean(valBln); return stackTop;case 16: valBln = ScriptRuntime.cmp_LT(rhs, lhs); stack[stackTop] = ScriptRuntime.wrapBoolean(valBln); return stackTop;case 14: valBln = ScriptRuntime.cmp_LT(lhs, rhs); stack[stackTop] = ScriptRuntime.wrapBoolean(valBln); return stackTop; }  throw Kit.codeBug(); }  switch (op) { case 17: bool = (lDbl >= rDbl) ? true : false; stack[stackTop] = ScriptRuntime.wrapBoolean(bool); return stackTop;case 15: bool = (lDbl <= rDbl) ? true : false; stack[stackTop] = ScriptRuntime.wrapBoolean(bool); return stackTop;case 16: bool = (lDbl > rDbl) ? true : false; stack[stackTop] = ScriptRuntime.wrapBoolean(bool); return stackTop;case 14: bool = (lDbl < rDbl) ? true : false; stack[stackTop] = ScriptRuntime.wrapBoolean(bool); return stackTop; }
    
    throw Kit.codeBug();
  }
  private static int doBitOp(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
    int lIntValue = stack_int32(frame, stackTop - 1);
    int rIntValue = stack_int32(frame, stackTop);
    stack[--stackTop] = UniqueTag.DOUBLE_MARK;
    switch (op) {
      case 11:
        lIntValue &= rIntValue;
        break;
      case 9:
        lIntValue |= rIntValue;
        break;
      case 10:
        lIntValue ^= rIntValue;
        break;
      case 18:
        lIntValue <<= rIntValue;
        break;
      case 19:
        lIntValue >>= rIntValue;
        break;
    } 
    sDbl[stackTop] = lIntValue;
    return stackTop;
  }

  
  private static int doDelName(Context cx, CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
    Object rhs = stack[stackTop];
    if (rhs == UniqueTag.DOUBLE_MARK) rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stackTop--;
    Object lhs = stack[stackTop];
    if (lhs == UniqueTag.DOUBLE_MARK) lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stack[stackTop] = ScriptRuntime.delete(lhs, rhs, cx, frame.scope, (op == 0));
    
    return stackTop;
  }
  
  private static int doGetElem(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop) {
    Object value;
    stackTop--;
    Object lhs = stack[stackTop];
    if (lhs == UniqueTag.DOUBLE_MARK) {
      lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
    }
    
    Object id = stack[stackTop + 1];
    if (id != UniqueTag.DOUBLE_MARK) {
      value = ScriptRuntime.getObjectElem(lhs, id, cx, frame.scope);
    } else {
      double d = sDbl[stackTop + 1];
      value = ScriptRuntime.getObjectIndex(lhs, d, cx, frame.scope);
    } 
    stack[stackTop] = value;
    return stackTop;
  }
  
  private static int doSetElem(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop) {
    Object value;
    stackTop -= 2;
    Object rhs = stack[stackTop + 2];
    if (rhs == UniqueTag.DOUBLE_MARK) {
      rhs = ScriptRuntime.wrapNumber(sDbl[stackTop + 2]);
    }
    Object lhs = stack[stackTop];
    if (lhs == UniqueTag.DOUBLE_MARK) {
      lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
    }
    
    Object id = stack[stackTop + 1];
    if (id != UniqueTag.DOUBLE_MARK) {
      value = ScriptRuntime.setObjectElem(lhs, id, rhs, cx, frame.scope);
    } else {
      double d = sDbl[stackTop + 1];
      value = ScriptRuntime.setObjectIndex(lhs, d, rhs, cx, frame.scope);
    } 
    stack[stackTop] = value;
    return stackTop;
  }

  
  private static int doElemIncDec(Context cx, CallFrame frame, byte[] iCode, Object[] stack, double[] sDbl, int stackTop) {
    Object rhs = stack[stackTop];
    if (rhs == UniqueTag.DOUBLE_MARK) rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stackTop--;
    Object lhs = stack[stackTop];
    if (lhs == UniqueTag.DOUBLE_MARK) lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stack[stackTop] = ScriptRuntime.elemIncrDecr(lhs, rhs, cx, frame.scope, iCode[frame.pc]);
    
    frame.pc++;
    return stackTop;
  }



  
  private static int doCallSpecial(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, byte[] iCode, int indexReg) {
    int callType = iCode[frame.pc] & 0xFF;
    boolean isNew = (iCode[frame.pc + 1] != 0);
    int sourceLine = getIndex(iCode, frame.pc + 2);

    
    if (isNew) {
      
      stackTop -= indexReg;
      
      Object function = stack[stackTop];
      if (function == UniqueTag.DOUBLE_MARK)
        function = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
      Object[] outArgs = getArgsArray(stack, sDbl, stackTop + 1, indexReg);
      
      stack[stackTop] = ScriptRuntime.newSpecial(cx, function, outArgs, frame.scope, callType);
    }
    else {
      
      stackTop -= 1 + indexReg;


      
      Scriptable functionThis = (Scriptable)stack[stackTop + 1];
      Callable function = (Callable)stack[stackTop];
      Object[] outArgs = getArgsArray(stack, sDbl, stackTop + 2, indexReg);
      
      stack[stackTop] = ScriptRuntime.callSpecial(cx, function, functionThis, outArgs, frame.scope, frame.thisObj, callType, frame.idata.itsSourceFile, sourceLine);
    } 


    
    frame.pc += 4;
    return stackTop;
  }



  
  private static int doSetConstVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
    if (!frame.useActivation) {
      if ((varAttributes[indexReg] & 0x1) == 0) {
        throw Context.reportRuntimeError1("msg.var.redecl", frame.idata.argNames[indexReg]);
      }
      
      if ((varAttributes[indexReg] & 0x8) != 0) {

        
        vars[indexReg] = stack[stackTop];
        varAttributes[indexReg] = varAttributes[indexReg] & 0xFFFFFFF7;
        varDbls[indexReg] = sDbl[stackTop];
      } 
    } else {
      Object val = stack[stackTop];
      if (val == UniqueTag.DOUBLE_MARK) val = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
      String stringReg = frame.idata.argNames[indexReg];
      if (frame.scope instanceof ConstProperties) {
        ConstProperties cp = (ConstProperties)frame.scope;
        cp.putConst(stringReg, frame.scope, val);
      } else {
        throw Kit.codeBug();
      } 
    }  return stackTop;
  }



  
  private static int doSetVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
    if (!frame.useActivation) {
      if ((varAttributes[indexReg] & 0x1) == 0) {
        vars[indexReg] = stack[stackTop];
        varDbls[indexReg] = sDbl[stackTop];
      } 
    } else {
      Object val = stack[stackTop];
      if (val == UniqueTag.DOUBLE_MARK) val = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
      String stringReg = frame.idata.argNames[indexReg];
      frame.scope.put(stringReg, frame.scope, val);
    } 
    return stackTop;
  }



  
  private static int doGetVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int indexReg) {
    stackTop++;
    if (!frame.useActivation) {
      stack[stackTop] = vars[indexReg];
      sDbl[stackTop] = varDbls[indexReg];
    } else {
      String stringReg = frame.idata.argNames[indexReg];
      stack[stackTop] = frame.scope.get(stringReg, frame.scope);
    } 
    return stackTop;
  }





  
  private static int doVarIncDec(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
    stackTop++;
    int incrDecrMask = frame.idata.itsICode[frame.pc];
    if (!frame.useActivation) {
      double d; Object varValue = vars[indexReg];
      
      if (varValue == UniqueTag.DOUBLE_MARK) {
        d = varDbls[indexReg];
      } else {
        d = ScriptRuntime.toNumber(varValue);
      } 
      double d2 = ((incrDecrMask & 0x1) == 0) ? (d + 1.0D) : (d - 1.0D);
      
      boolean post = ((incrDecrMask & 0x2) != 0);
      if ((varAttributes[indexReg] & 0x1) == 0) {
        if (varValue != UniqueTag.DOUBLE_MARK) {
          vars[indexReg] = UniqueTag.DOUBLE_MARK;
        }
        varDbls[indexReg] = d2;
        stack[stackTop] = UniqueTag.DOUBLE_MARK;
        sDbl[stackTop] = post ? d : d2;
      }
      else if (post && varValue != UniqueTag.DOUBLE_MARK) {
        stack[stackTop] = varValue;
      } else {
        stack[stackTop] = UniqueTag.DOUBLE_MARK;
        sDbl[stackTop] = post ? d : d2;
      } 
    } else {
      
      String varName = frame.idata.argNames[indexReg];
      stack[stackTop] = ScriptRuntime.nameIncrDecr(frame.scope, varName, cx, incrDecrMask);
    } 
    
    frame.pc++;
    return stackTop;
  }

  
  private static int doRefMember(Context cx, Object[] stack, double[] sDbl, int stackTop, int flags) {
    Object elem = stack[stackTop];
    if (elem == UniqueTag.DOUBLE_MARK) elem = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stackTop--;
    Object obj = stack[stackTop];
    if (obj == UniqueTag.DOUBLE_MARK) obj = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stack[stackTop] = ScriptRuntime.memberRef(obj, elem, cx, flags);
    return stackTop;
  }

  
  private static int doRefNsMember(Context cx, Object[] stack, double[] sDbl, int stackTop, int flags) {
    Object elem = stack[stackTop];
    if (elem == UniqueTag.DOUBLE_MARK) elem = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stackTop--;
    Object ns = stack[stackTop];
    if (ns == UniqueTag.DOUBLE_MARK) ns = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stackTop--;
    Object obj = stack[stackTop];
    if (obj == UniqueTag.DOUBLE_MARK) obj = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stack[stackTop] = ScriptRuntime.memberRef(obj, ns, elem, cx, flags);
    return stackTop;
  }


  
  private static int doRefNsName(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, int flags) {
    Object name = stack[stackTop];
    if (name == UniqueTag.DOUBLE_MARK) name = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stackTop--;
    Object ns = stack[stackTop];
    if (ns == UniqueTag.DOUBLE_MARK) ns = ScriptRuntime.wrapNumber(sDbl[stackTop]); 
    stack[stackTop] = ScriptRuntime.nameRef(ns, name, cx, frame.scope, flags);
    return stackTop;
  }








  
  private static CallFrame initFrameForNoSuchMethod(Context cx, CallFrame frame, int indexReg, Object[] stack, double[] sDbl, int stackTop, int op, Scriptable funThisObj, Scriptable calleeScope, ScriptRuntime.NoSuchMethodShim noSuchMethodShim, InterpretedFunction ifun) {
    Object[] argsArray = null;

    
    int shift = stackTop + 2;
    Object[] elements = new Object[indexReg];
    for (int i = 0; i < indexReg; i++, shift++) {
      Object val = stack[shift];
      if (val == UniqueTag.DOUBLE_MARK) {
        val = ScriptRuntime.wrapNumber(sDbl[shift]);
      }
      elements[i] = val;
    } 
    argsArray = new Object[2];
    argsArray[0] = noSuchMethodShim.methodName;
    argsArray[1] = cx.newArray(calleeScope, elements);

    
    CallFrame callParentFrame = frame;
    CallFrame calleeFrame = new CallFrame();
    if (op == -55) {
      callParentFrame = frame.parentFrame;
      exitFrame(cx, frame, (Object)null);
    } 

    
    initFrame(cx, calleeScope, funThisObj, argsArray, (double[])null, 0, 2, ifun, callParentFrame, calleeFrame);
    
    if (op != -55) {
      frame.savedStackTop = stackTop;
      frame.savedCallOp = op;
    } 
    return calleeFrame;
  }

  
  private static boolean doEquals(Object[] stack, double[] sDbl, int stackTop) {
    Object rhs = stack[stackTop + 1];
    Object lhs = stack[stackTop];
    if (rhs == UniqueTag.DOUBLE_MARK) {
      if (lhs == UniqueTag.DOUBLE_MARK) {
        return (sDbl[stackTop] == sDbl[stackTop + 1]);
      }
      return ScriptRuntime.eqNumber(sDbl[stackTop + 1], lhs);
    } 
    
    if (lhs == UniqueTag.DOUBLE_MARK) {
      return ScriptRuntime.eqNumber(sDbl[stackTop], rhs);
    }
    return ScriptRuntime.eq(lhs, rhs);
  }



  
  private static boolean doShallowEquals(Object[] stack, double[] sDbl, int stackTop) {
    double rdbl, ldbl;
    Object rhs = stack[stackTop + 1];
    Object lhs = stack[stackTop];
    Object DBL_MRK = UniqueTag.DOUBLE_MARK;
    
    if (rhs == DBL_MRK) {
      rdbl = sDbl[stackTop + 1];
      if (lhs == DBL_MRK) {
        ldbl = sDbl[stackTop];
      } else if (lhs instanceof Number) {
        ldbl = ((Number)lhs).doubleValue();
      } else {
        return false;
      } 
    } else if (lhs == DBL_MRK) {
      ldbl = sDbl[stackTop];
      if (rhs instanceof Number) {
        rdbl = ((Number)rhs).doubleValue();
      } else {
        return false;
      } 
    } else {
      return ScriptRuntime.shallowEq(lhs, rhs);
    } 
    return (ldbl == rdbl);
  }






  
  private static CallFrame processThrowable(Context cx, Object throwable, CallFrame frame, int indexReg, boolean instructionCounting) {
    if (indexReg >= 0) {


      
      if (frame.frozen)
      {
        frame = frame.cloneFrozen();
      }
      
      int[] table = frame.idata.itsExceptionTable;
      
      frame.pc = table[indexReg + 2];
      if (instructionCounting) {
        frame.pcPrevBranch = frame.pc;
      }
      
      frame.savedStackTop = frame.emptyStackTop;
      int scopeLocal = frame.localShift + table[indexReg + 5];

      
      int exLocal = frame.localShift + table[indexReg + 4];

      
      frame.scope = (Scriptable)frame.stack[scopeLocal];
      frame.stack[exLocal] = throwable;
      
      throwable = null;
    } else {
      
      ContinuationJump cjump = (ContinuationJump)throwable;

      
      throwable = null;
      
      if (cjump.branchFrame != frame) Kit.codeBug();



      
      if (cjump.capturedFrame == null) Kit.codeBug();


      
      int rewindCount = cjump.capturedFrame.frameIndex + 1;
      if (cjump.branchFrame != null) {
        rewindCount -= cjump.branchFrame.frameIndex;
      }
      
      int enterCount = 0;
      CallFrame[] enterFrames = null;
      
      CallFrame x = cjump.capturedFrame;
      for (int i = 0; i != rewindCount; i++) {
        if (!x.frozen) Kit.codeBug(); 
        if (isFrameEnterExitRequired(x)) {
          if (enterFrames == null)
          {

            
            enterFrames = new CallFrame[rewindCount - i];
          }
          
          enterFrames[enterCount] = x;
          enterCount++;
        } 
        x = x.parentFrame;
      } 
      
      while (enterCount != 0) {


        
        enterCount--;
        x = enterFrames[enterCount];
        enterFrame(cx, x, ScriptRuntime.emptyArgs, true);
      } 




      
      frame = cjump.capturedFrame.cloneFrozen();
      setCallResult(frame, cjump.result, cjump.resultDbl);
    } 
    
    frame.throwable = throwable;
    return frame;
  }



  
  private static Object freezeGenerator(Context cx, CallFrame frame, int stackTop, GeneratorState generatorState) {
    if (generatorState.operation == 2)
    {
      throw ScriptRuntime.typeError0("msg.yield.closing");
    }
    
    frame.frozen = true;
    frame.result = frame.stack[stackTop];
    frame.resultDbl = frame.sDbl[stackTop];
    frame.savedStackTop = stackTop;
    frame.pc--;
    ScriptRuntime.exitActivationFunction(cx);
    return (frame.result != UniqueTag.DOUBLE_MARK) ? frame.result : ScriptRuntime.wrapNumber(frame.resultDbl);
  }





  
  private static Object thawGenerator(CallFrame frame, int stackTop, GeneratorState generatorState, int op) {
    frame.frozen = false;
    int sourceLine = getIndex(frame.idata.itsICode, frame.pc);
    frame.pc += 2;
    if (generatorState.operation == 1)
    {
      
      return new JavaScriptException(generatorState.value, frame.idata.itsSourceFile, sourceLine);
    }

    
    if (generatorState.operation == 2) {
      return generatorState.value;
    }
    if (generatorState.operation != 0)
      throw Kit.codeBug(); 
    if (op == 72)
      frame.stack[stackTop] = generatorState.value; 
    return Scriptable.NOT_FOUND;
  }




  
  private static CallFrame initFrameForApplyOrCall(Context cx, CallFrame frame, int indexReg, Object[] stack, double[] sDbl, int stackTop, int op, Scriptable calleeScope, IdFunctionObject ifun, InterpretedFunction iApplyCallable) {
    Scriptable applyThis;
    if (indexReg != 0) {
      Object obj = stack[stackTop + 2];
      if (obj == UniqueTag.DOUBLE_MARK)
        obj = ScriptRuntime.wrapNumber(sDbl[stackTop + 2]); 
      applyThis = ScriptRuntime.toObjectOrNull(cx, obj, frame.scope);
    } else {
      
      applyThis = null;
    } 
    if (applyThis == null)
    {
      applyThis = ScriptRuntime.getTopCallScope(cx);
    }
    if (op == -55) {
      exitFrame(cx, frame, (Object)null);
      frame = frame.parentFrame;
    } else {
      
      frame.savedStackTop = stackTop;
      frame.savedCallOp = op;
    } 
    CallFrame calleeFrame = new CallFrame();
    if (BaseFunction.isApply(ifun)) {
      Object[] callArgs = (indexReg < 2) ? ScriptRuntime.emptyArgs : ScriptRuntime.getApplyArguments(cx, stack[stackTop + 3]);
      
      initFrame(cx, calleeScope, applyThis, callArgs, (double[])null, 0, callArgs.length, iApplyCallable, frame, calleeFrame);
    
    }
    else {
      
      for (int i = 1; i < indexReg; i++) {
        stack[stackTop + 1 + i] = stack[stackTop + 2 + i];
        sDbl[stackTop + 1 + i] = sDbl[stackTop + 2 + i];
      } 
      int argCount = (indexReg < 2) ? 0 : (indexReg - 1);
      initFrame(cx, calleeScope, applyThis, stack, sDbl, stackTop + 2, argCount, iApplyCallable, frame, calleeFrame);
    } 

    
    frame = calleeFrame;
    return frame;
  }

  
  private static void initFrame(Context cx, Scriptable callerScope, Scriptable thisObj, Object[] args, double[] argsDbl, int argShift, int argCount, InterpretedFunction fnOrScript, CallFrame parentFrame, CallFrame frame) {
    Scriptable scope;
    Object[] stack;
    int[] stackAttributes;
    double[] sDbl;
    boolean stackReuse;
    InterpreterData idata = fnOrScript.idata;
    
    boolean useActivation = idata.itsNeedsActivation;
    DebugFrame debuggerFrame = null;
    if (cx.debugger != null) {
      debuggerFrame = cx.debugger.getFrame(cx, idata);
      if (debuggerFrame != null) {
        useActivation = true;
      }
    } 
    
    if (useActivation) {

      
      if (argsDbl != null) {
        args = getArgsArray(args, argsDbl, argShift, argCount);
      }
      argShift = 0;
      argsDbl = null;
    } 

    
    if (idata.itsFunctionType != 0) {
      scope = fnOrScript.getParentScope();
      
      if (useActivation) {
        scope = ScriptRuntime.createFunctionActivation(fnOrScript, scope, args);
      }
    } else {
      
      scope = callerScope;
      ScriptRuntime.initScript(fnOrScript, thisObj, cx, scope, fnOrScript.idata.evalScriptFlag);
    } 

    
    if (idata.itsNestedFunctions != null) {
      if (idata.itsFunctionType != 0 && !idata.itsNeedsActivation)
        Kit.codeBug(); 
      for (int k = 0; k < idata.itsNestedFunctions.length; k++) {
        InterpreterData fdata = idata.itsNestedFunctions[k];
        if (fdata.itsFunctionType == 1) {
          initFunction(cx, scope, fnOrScript, k);
        }
      } 
    } 


    
    int emptyStackTop = idata.itsMaxVars + idata.itsMaxLocals - 1;
    int maxFrameArray = idata.itsMaxFrameArray;
    if (maxFrameArray != emptyStackTop + idata.itsMaxStack + 1) {
      Kit.codeBug();
    }



    
    if (frame.stack != null && maxFrameArray <= frame.stack.length) {
      
      stackReuse = true;
      stack = frame.stack;
      stackAttributes = frame.stackAttributes;
      sDbl = frame.sDbl;
    } else {
      stackReuse = false;
      stack = new Object[maxFrameArray];
      stackAttributes = new int[maxFrameArray];
      sDbl = new double[maxFrameArray];
    } 
    
    int varCount = idata.getParamAndVarCount();
    for (int i = 0; i < varCount; i++) {
      if (idata.getParamOrVarConst(i))
        stackAttributes[i] = 13; 
    } 
    int definedArgs = idata.argCount;
    if (definedArgs > argCount) definedArgs = argCount;


    
    frame.parentFrame = parentFrame;
    frame.frameIndex = (parentFrame == null) ? 0 : (parentFrame.frameIndex + 1);
    
    if (frame.frameIndex > cx.getMaximumInterpreterStackDepth())
    {
      throw Context.reportRuntimeError("Exceeded maximum stack depth");
    }
    frame.frozen = false;
    
    frame.fnOrScript = fnOrScript;
    frame.idata = idata;
    
    frame.stack = stack;
    frame.stackAttributes = stackAttributes;
    frame.sDbl = sDbl;
    frame.varSource = frame;
    frame.localShift = idata.itsMaxVars;
    frame.emptyStackTop = emptyStackTop;
    
    frame.debuggerFrame = debuggerFrame;
    frame.useActivation = useActivation;
    
    frame.thisObj = thisObj;


    
    frame.result = Undefined.instance;
    frame.pc = 0;
    frame.pcPrevBranch = 0;
    frame.pcSourceLineStart = idata.firstLinePC;
    frame.scope = scope;
    
    frame.savedStackTop = emptyStackTop;
    frame.savedCallOp = 0;
    
    System.arraycopy(args, argShift, stack, 0, definedArgs);
    if (argsDbl != null)
      System.arraycopy(argsDbl, argShift, sDbl, 0, definedArgs); 
    int j;
    for (j = definedArgs; j != idata.itsMaxVars; j++) {
      stack[j] = Undefined.instance;
    }
    if (stackReuse)
    {
      
      for (j = emptyStackTop + 1; j != stack.length; j++) {
        stack[j] = null;
      }
    }
    
    enterFrame(cx, frame, args, false);
  }

  
  private static boolean isFrameEnterExitRequired(CallFrame frame) {
    return (frame.debuggerFrame != null || frame.idata.itsNeedsActivation);
  }


  
  private static void enterFrame(Context cx, CallFrame frame, Object[] args, boolean continuationRestart) {
    boolean usesActivation = frame.idata.itsNeedsActivation;
    boolean isDebugged = (frame.debuggerFrame != null);
    if (usesActivation || isDebugged) {
      Scriptable scope = frame.scope;
      if (scope == null) {
        Kit.codeBug();
      } else if (continuationRestart) {








        
        while (scope instanceof NativeWith) {
          scope = scope.getParentScope();
          if (scope == null || (frame.parentFrame != null && frame.parentFrame.scope == scope)) {




            
            Kit.codeBug();


            
            break;
          } 
        } 
      } 

      
      if (isDebugged) {
        frame.debuggerFrame.onEnter(cx, scope, frame.thisObj, args);
      }


      
      if (usesActivation) {
        ScriptRuntime.enterActivationFunction(cx, scope);
      }
    } 
  }


  
  private static void exitFrame(Context cx, CallFrame frame, Object throwable) {
    if (frame.idata.itsNeedsActivation) {
      ScriptRuntime.exitActivationFunction(cx);
    }
    
    if (frame.debuggerFrame != null) {
      try {
        if (throwable instanceof Throwable) {
          frame.debuggerFrame.onExit(cx, true, throwable);
        } else {
          Object result;
          ContinuationJump cjump = (ContinuationJump)throwable;
          if (cjump == null) {
            result = frame.result;
          } else {
            result = cjump.result;
          } 
          if (result == UniqueTag.DOUBLE_MARK) {
            double resultDbl;
            if (cjump == null) {
              resultDbl = frame.resultDbl;
            } else {
              resultDbl = cjump.resultDbl;
            } 
            result = ScriptRuntime.wrapNumber(resultDbl);
          } 
          frame.debuggerFrame.onExit(cx, false, result);
        } 
      } catch (Throwable ex) {
        System.err.println("RHINO USAGE WARNING: onExit terminated with exception");
        
        ex.printStackTrace(System.err);
      } 
    }
  }



  
  private static void setCallResult(CallFrame frame, Object callResult, double callResultDbl) {
    if (frame.savedCallOp == 38) {
      frame.stack[frame.savedStackTop] = callResult;
      frame.sDbl[frame.savedStackTop] = callResultDbl;
    } else if (frame.savedCallOp == 30) {


      
      if (callResult instanceof Scriptable) {
        frame.stack[frame.savedStackTop] = callResult;
      }
    } else {
      Kit.codeBug();
    } 
    frame.savedCallOp = 0;
  }
  
  public static NativeContinuation captureContinuation(Context cx) {
    if (cx.lastInterpreterFrame == null || !(cx.lastInterpreterFrame instanceof CallFrame))
    {
      
      throw new IllegalStateException("Interpreter frames not found");
    }
    return captureContinuation(cx, (CallFrame)cx.lastInterpreterFrame, true);
  }


  
  private static NativeContinuation captureContinuation(Context cx, CallFrame frame, boolean requireContinuationsTopFrame) {
    NativeContinuation c = new NativeContinuation();
    ScriptRuntime.setObjectProtoAndParent(c, ScriptRuntime.getTopCallScope(cx));


    
    CallFrame x = frame;
    CallFrame outermost = frame;
    while (x != null && !x.frozen) {
      x.frozen = true;
      
      for (int i = x.savedStackTop + 1; i != x.stack.length; i++) {
        
        x.stack[i] = null;
        x.stackAttributes[i] = 0;
      } 
      if (x.savedCallOp == 38)
      
      { x.stack[x.savedStackTop] = null; }
      
      else if (x.savedCallOp != 30) { Kit.codeBug(); }



      
      outermost = x;
      x = x.parentFrame;
    } 
    
    if (requireContinuationsTopFrame) {
      while (outermost.parentFrame != null) {
        outermost = outermost.parentFrame;
      }
      if (!outermost.isContinuationsTopFrame) {
        throw new IllegalStateException("Cannot capture continuation from JavaScript code not called directly by executeScriptWithContinuations or callFunctionWithContinuations");
      }
    } 



    
    c.initImplementation(frame);
    return c;
  }

  
  private static int stack_int32(CallFrame frame, int i) {
    Object x = frame.stack[i];
    if (x == UniqueTag.DOUBLE_MARK) {
      return ScriptRuntime.toInt32(frame.sDbl[i]);
    }
    return ScriptRuntime.toInt32(x);
  }


  
  private static double stack_double(CallFrame frame, int i) {
    Object x = frame.stack[i];
    if (x != UniqueTag.DOUBLE_MARK) {
      return ScriptRuntime.toNumber(x);
    }
    return frame.sDbl[i];
  }


  
  private static boolean stack_boolean(CallFrame frame, int i) {
    Object x = frame.stack[i];
    if (x == Boolean.TRUE)
      return true; 
    if (x == Boolean.FALSE)
      return false; 
    if (x == UniqueTag.DOUBLE_MARK) {
      double d = frame.sDbl[i];
      return (d == d && d != 0.0D);
    }  if (x == null || x == Undefined.instance)
      return false; 
    if (x instanceof Number) {
      double d = ((Number)x).doubleValue();
      return (d == d && d != 0.0D);
    }  if (x instanceof Boolean) {
      return ((Boolean)x).booleanValue();
    }
    return ScriptRuntime.toBoolean(x);
  }

  
  private static void doAdd(Object[] stack, double[] sDbl, int stackTop, Context cx) {
    double d;
    boolean leftRightOrder;
    Object rhs = stack[stackTop + 1];
    Object lhs = stack[stackTop];

    
    if (rhs == UniqueTag.DOUBLE_MARK) {
      d = sDbl[stackTop + 1];
      if (lhs == UniqueTag.DOUBLE_MARK) {
        sDbl[stackTop] = sDbl[stackTop] + d;
        return;
      } 
      leftRightOrder = true;
    }
    else if (lhs == UniqueTag.DOUBLE_MARK) {
      d = sDbl[stackTop];
      lhs = rhs;
      leftRightOrder = false;
    } else {
      
      if (lhs instanceof Scriptable || rhs instanceof Scriptable) {
        stack[stackTop] = ScriptRuntime.add(lhs, rhs, cx);
      } else if (lhs instanceof CharSequence || rhs instanceof CharSequence) {
        CharSequence lstr = ScriptRuntime.toCharSequence(lhs);
        CharSequence rstr = ScriptRuntime.toCharSequence(rhs);
        stack[stackTop] = new ConsString(lstr, rstr);
      } else {
        double lDbl = (lhs instanceof Number) ? ((Number)lhs).doubleValue() : ScriptRuntime.toNumber(lhs);
        
        double rDbl = (rhs instanceof Number) ? ((Number)rhs).doubleValue() : ScriptRuntime.toNumber(rhs);
        
        stack[stackTop] = UniqueTag.DOUBLE_MARK;
        sDbl[stackTop] = lDbl + rDbl;
      } 
      
      return;
    } 
    
    if (lhs instanceof Scriptable) {
      rhs = ScriptRuntime.wrapNumber(d);
      if (!leftRightOrder) {
        Object tmp = lhs;
        lhs = rhs;
        rhs = tmp;
      } 
      stack[stackTop] = ScriptRuntime.add(lhs, rhs, cx);
    } else if (lhs instanceof CharSequence) {
      CharSequence lstr = (CharSequence)lhs;
      CharSequence rstr = ScriptRuntime.toCharSequence(Double.valueOf(d));
      if (leftRightOrder) {
        stack[stackTop] = new ConsString(lstr, rstr);
      } else {
        stack[stackTop] = new ConsString(rstr, lstr);
      } 
    } else {
      double lDbl = (lhs instanceof Number) ? ((Number)lhs).doubleValue() : ScriptRuntime.toNumber(lhs);
      
      stack[stackTop] = UniqueTag.DOUBLE_MARK;
      sDbl[stackTop] = lDbl + d;
    } 
  }

  
  private static int doArithmetic(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
    double rDbl = stack_double(frame, stackTop);
    stackTop--;
    double lDbl = stack_double(frame, stackTop);
    stack[stackTop] = UniqueTag.DOUBLE_MARK;
    switch (op) {
      case 22:
        lDbl -= rDbl;
        break;
      case 23:
        lDbl *= rDbl;
        break;
      case 24:
        lDbl /= rDbl;
        break;
      case 25:
        lDbl %= rDbl;
        break;
    } 
    sDbl[stackTop] = lDbl;
    return stackTop;
  }


  
  private static Object[] getArgsArray(Object[] stack, double[] sDbl, int shift, int count) {
    if (count == 0) {
      return ScriptRuntime.emptyArgs;
    }
    Object[] args = new Object[count];
    for (int i = 0; i != count; i++, shift++) {
      Object val = stack[shift];
      if (val == UniqueTag.DOUBLE_MARK) {
        val = ScriptRuntime.wrapNumber(sDbl[shift]);
      }
      args[i] = val;
    } 
    return args;
  }


  
  private static void addInstructionCount(Context cx, CallFrame frame, int extra) {
    cx.instructionCount += frame.pc - frame.pcPrevBranch + extra;
    if (cx.instructionCount > cx.instructionThreshold) {
      cx.observeInstructionCount(cx.instructionCount);
      cx.instructionCount = 0;
    } 
  }
}
