package javassist.compiler;

import javassist.bytecode.Bytecode;
import javassist.compiler.ast.ASTList;

public interface ProceedHandler {
  void doit(JvstCodeGen paramJvstCodeGen, Bytecode paramBytecode, ASTList paramASTList) throws CompileError;
  
  void setReturnType(JvstTypeChecker paramJvstTypeChecker, ASTList paramASTList) throws CompileError;
}
