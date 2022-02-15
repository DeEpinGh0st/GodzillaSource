package org.fife.rsta.ac.js.ast;

import java.util.HashMap;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;



















public class VariableResolver
{
  private HashMap<String, JavaScriptVariableDeclaration> localVariables = new HashMap<>();

  
  private HashMap<String, JavaScriptVariableDeclaration> preProcessedVariables = new HashMap<>();



  
  private HashMap<String, JavaScriptVariableDeclaration> systemVariables = new HashMap<>();

  
  private HashMap<String, JavaScriptFunctionDeclaration> localFunctions = new HashMap<>();
  
  private HashMap<String, JavaScriptFunctionDeclaration> preProcessedFunctions = new HashMap<>();







  
  public void addLocalVariable(JavaScriptVariableDeclaration declaration) {
    this.localVariables.put(declaration.getName(), declaration);
  }






  
  public void addPreProcessingVariable(JavaScriptVariableDeclaration declaration) {
    this.preProcessedVariables.put(declaration.getName(), declaration);
  }






  
  public void addSystemVariable(JavaScriptVariableDeclaration declaration) {
    this.systemVariables.put(declaration.getName(), declaration);
  }






  
  public void removePreProcessingVariable(String name) {
    this.preProcessedVariables.remove(name);
  }






  
  public void removeSystemVariable(String name) {
    this.systemVariables.remove(name);
  }








  
  public JavaScriptVariableDeclaration findDeclaration(String name, int dot) {
    JavaScriptVariableDeclaration findDeclaration = findDeclaration(this.localVariables, name, dot);

    
    findDeclaration = (findDeclaration == null) ? findDeclaration(this.preProcessedVariables, name, dot) : findDeclaration;

    
    return (findDeclaration == null) ? findDeclaration(this.systemVariables, name, dot) : findDeclaration;
  }

  
  public JavaScriptVariableDeclaration findDeclaration(String name, int dot, boolean local, boolean preProcessed, boolean system) {
    JavaScriptVariableDeclaration findDeclaration = local ? findDeclaration(this.localVariables, name, dot) : null;
    
    findDeclaration = (findDeclaration == null) ? (preProcessed ? findDeclaration(this.preProcessedVariables, name, dot) : null) : findDeclaration;
    
    return (findDeclaration == null) ? (system ? findDeclaration(this.systemVariables, name, dot) : null) : findDeclaration;
  }









  
  public JavaScriptVariableDeclaration findNonLocalDeclaration(String name, int dot) {
    JavaScriptVariableDeclaration findDeclaration = findDeclaration(this.preProcessedVariables, name, dot);
    
    return (findDeclaration == null) ? findDeclaration(this.systemVariables, name, dot) : findDeclaration;
  }











  
  private JavaScriptVariableDeclaration findDeclaration(HashMap<String, JavaScriptVariableDeclaration> variables, String name, int dot) {
    JavaScriptVariableDeclaration dec = variables.get(name);
    
    if (dec != null && (
      dec.getCodeBlock() == null || dec.getCodeBlock().contains(dot))) {
      int decOffs = dec.getOffset();
      if (dot <= decOffs) {
        return dec;
      }
    } 
    
    return null;
  }









  
  public TypeDeclaration getTypeDeclarationForVariable(String name, int dot) {
    JavaScriptVariableDeclaration dec = findDeclaration(name, dot);
    return (dec != null) ? dec.getTypeDeclaration() : null;
  }




  
  public void resetLocalVariables() {
    this.localVariables.clear();
    this.localFunctions.clear();
  }

  
  public void resetPreProcessingVariables(boolean clear) {
    if (clear) {
      this.preProcessedVariables.clear();
      this.preProcessedFunctions.clear();
    } else {
      
      for (JavaScriptVariableDeclaration dec : this.preProcessedVariables.values()) {
        dec.resetVariableToOriginalType();
      }
    } 
  }

  
  public void resetSystemVariables() {
    this.systemVariables.clear();
  }











  
  public TypeDeclaration resolveType(String varName, int dot) {
    return getTypeDeclarationForVariable(varName, dot);
  }


  
  public void addLocalFunction(JavaScriptFunctionDeclaration func) {
    this.localFunctions.put(func.getName(), func);
  }

  
  public JavaScriptFunctionDeclaration findFunctionDeclaration(String name) {
    JavaScriptFunctionDeclaration dec = this.localFunctions.get(name);
    if (dec == null) {
      dec = this.preProcessedFunctions.get(name);
    }
    return dec;
  }

  
  public JavaScriptFunctionDeclaration findFunctionDeclaration(String name, boolean local, boolean preProcessed) {
    JavaScriptFunctionDeclaration dec = local ? this.localFunctions.get(name) : null;
    if (dec == null) {
      dec = preProcessed ? this.preProcessedFunctions.get(name) : null;
    }
    return dec;
  }
  
  public JavaScriptFunctionDeclaration findFunctionDeclarationByFunctionName(String name, boolean local, boolean preprocessed) {
    JavaScriptFunctionDeclaration func = local ? findFirstFunction(name, this.localFunctions) : null;
    if (func == null) {
      func = preprocessed ? findFirstFunction(name, this.preProcessedFunctions) : null;
    }
    return func;
  }

  
  private JavaScriptFunctionDeclaration findFirstFunction(String name, HashMap<String, JavaScriptFunctionDeclaration> functions) {
    for (JavaScriptFunctionDeclaration func : functions.values()) {
      if (name.equals(func.getFunctionName())) {
        return func;
      }
    } 
    return null;
  }






  
  public void addPreProcessingFunction(JavaScriptFunctionDeclaration func) {
    this.preProcessedFunctions.put(func.getName(), func);
  }
}
