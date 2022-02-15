package org.springframework.cglib.proxy;

import java.util.List;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.Signature;

interface CallbackGenerator {
  void generate(ClassEmitter paramClassEmitter, Context paramContext, List paramList) throws Exception;
  
  void generateStatic(CodeEmitter paramCodeEmitter, Context paramContext, List paramList) throws Exception;
  
  public static interface Context {
    ClassLoader getClassLoader();
    
    CodeEmitter beginMethod(ClassEmitter param1ClassEmitter, MethodInfo param1MethodInfo);
    
    int getOriginalModifiers(MethodInfo param1MethodInfo);
    
    int getIndex(MethodInfo param1MethodInfo);
    
    void emitCallback(CodeEmitter param1CodeEmitter, int param1Int);
    
    Signature getImplSignature(MethodInfo param1MethodInfo);
    
    void emitLoadArgsAndInvoke(CodeEmitter param1CodeEmitter, MethodInfo param1MethodInfo);
  }
}
