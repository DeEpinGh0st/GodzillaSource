package org.fife.rsta.ac.java.classreader;

import java.util.Stack;
import org.fife.rsta.ac.java.classreader.attributes.Code;



























public class Frame
{
  private Stack<String> operandStack;
  private LocalVarInfo[] localVars;
  
  public Frame(Code code) {
    this.operandStack = new Stack<>();
    
    this.localVars = new LocalVarInfo[code.getMaxLocals()];
    int i = 0;
    MethodInfo mi = code.getMethodInfo();

    
    if (!mi.isStatic()) {
      this.localVars[i++] = new LocalVarInfo("this", true);
    }


    
    String[] paramTypes = mi.getParameterTypes();
    for (int param_i = 0; param_i < paramTypes.length; param_i++) {
      String type = paramTypes[param_i];
      if (type.indexOf('.') > -1) {
        type = type.substring(type.lastIndexOf('.') + 1);
      }
      String name = "localVar_" + type + "_" + param_i;
      this.localVars[i] = new LocalVarInfo(name, true);
      i++;
      if ("long".equals(type) || "double".equals(type)) {
        i++;
      }
    } 


    
    System.out.println("NOTE: " + (this.localVars.length - i) + " unknown localVars slots");
  }


  
  public LocalVarInfo getLocalVar(int index, String defaultType) {
    LocalVarInfo var = this.localVars[index];
    if (var == null) {
      String name = "localVar_" + defaultType + "_" + index;
      var = new LocalVarInfo(name, false);
      this.localVars[index] = var;
    } else {
      
      var.alreadyDeclared = true;
    } 
    return var;
  }

  
  public String pop() {
    return this.operandStack.pop();
  }

  
  public void push(String value) {
    this.operandStack.push(value);
  }

  
  public static class LocalVarInfo
  {
    private String value;
    private boolean alreadyDeclared;
    
    public LocalVarInfo(String value, boolean alreadyDeclared) {
      this.value = value;
      this.alreadyDeclared = alreadyDeclared;
    }
    
    public String getValue() {
      return this.value;
    }
    
    public boolean isAlreadyDeclared() {
      return this.alreadyDeclared;
    }
  }
}
