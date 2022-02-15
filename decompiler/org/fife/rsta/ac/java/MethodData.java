package org.fife.rsta.ac.java;

import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;




















class MethodData
  implements MemberCompletion.Data
{
  private Method method;
  
  public MethodData(Method method) {
    this.method = method;
  }







  
  public String getEnclosingClassName(boolean fullyQualified) {
    TypeDeclaration td = this.method.getParentTypeDeclaration();
    if (td == null) {
      (new Exception("No parent type declaration for: " + getSignature()))
        .printStackTrace();
      return "";
    } 
    return td.getName(fullyQualified);
  }




  
  public String getIcon() {
    String key;
    Modifiers mod = this.method.getModifiers();
    if (mod == null) {
      key = "methodDefaultIcon";
    }
    else if (mod.isPrivate()) {
      key = "methodPrivateIcon";
    }
    else if (mod.isProtected()) {
      key = "methodProtectedIcon";
    }
    else if (mod.isPublic()) {
      key = "methodPublicIcon";
    } else {
      
      key = "methodDefaultIcon";
    } 
    
    return key;
  }



  
  public String getSignature() {
    return this.method.getNameAndParameters();
  }


  
  public String getSummary() {
    String docComment = this.method.getDocComment();
    return (docComment != null) ? docComment : this.method.toString();
  }


  
  public String getType() {
    Type type = this.method.getType();
    return (type == null) ? "void" : type.toString();
  }


  
  public boolean isAbstract() {
    return this.method.getModifiers().isAbstract();
  }


  
  public boolean isConstructor() {
    return this.method.isConstructor();
  }


  
  public boolean isDeprecated() {
    return this.method.isDeprecated();
  }


  
  public boolean isFinal() {
    return this.method.getModifiers().isFinal();
  }


  
  public boolean isStatic() {
    return this.method.getModifiers().isStatic();
  }
}
