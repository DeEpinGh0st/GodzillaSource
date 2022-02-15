package org.fife.rsta.ac.java;

import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;




















class FieldData
  implements MemberCompletion.Data
{
  private Field field;
  
  public FieldData(Field field) {
    this.field = field;
  }







  
  public String getEnclosingClassName(boolean fullyQualified) {
    TypeDeclaration td = this.field.getParentTypeDeclaration();
    if (td == null) {
      (new Exception("No parent type declaration for: " + getSignature()))
        .printStackTrace();
      return "";
    } 
    return td.getName(fullyQualified);
  }







  
  public String getIcon() {
    String key;
    Modifiers mod = this.field.getModifiers();
    if (mod == null) {
      key = "fieldDefaultIcon";
    }
    else if (mod.isPrivate()) {
      key = "fieldPrivateIcon";
    }
    else if (mod.isProtected()) {
      key = "fieldProtectedIcon";
    }
    else if (mod.isPublic()) {
      key = "fieldPublicIcon";
    } else {
      
      key = "fieldDefaultIcon";
    } 
    
    return key;
  }






  
  public String getSignature() {
    return this.field.getName();
  }





  
  public String getSummary() {
    String docComment = this.field.getDocComment();
    return (docComment != null) ? docComment : this.field.toString();
  }





  
  public String getType() {
    return this.field.getType().toString();
  }


  
  public boolean isAbstract() {
    return this.field.getModifiers().isAbstract();
  }







  
  public boolean isConstructor() {
    return false;
  }





  
  public boolean isDeprecated() {
    return this.field.isDeprecated();
  }


  
  public boolean isFinal() {
    return this.field.getModifiers().isFinal();
  }


  
  public boolean isStatic() {
    return this.field.getModifiers().isStatic();
  }
}
