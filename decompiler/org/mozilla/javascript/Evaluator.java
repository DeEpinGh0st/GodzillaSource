package org.mozilla.javascript;

import java.util.List;
import org.mozilla.javascript.ast.ScriptNode;

public interface Evaluator {
  Object compile(CompilerEnvirons paramCompilerEnvirons, ScriptNode paramScriptNode, String paramString, boolean paramBoolean);
  
  Function createFunctionObject(Context paramContext, Scriptable paramScriptable, Object paramObject1, Object paramObject2);
  
  Script createScriptObject(Object paramObject1, Object paramObject2);
  
  void captureStackInfo(RhinoException paramRhinoException);
  
  String getSourcePositionFromStack(Context paramContext, int[] paramArrayOfint);
  
  String getPatchedStack(RhinoException paramRhinoException, String paramString);
  
  List<String> getScriptStack(RhinoException paramRhinoException);
  
  void setEvalScriptFlag(Script paramScript);
}
