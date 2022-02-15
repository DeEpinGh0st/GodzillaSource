package org.fife.rsta.ac.java.rjc.ast;

import java.util.ArrayList;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Offset;





























public class CodeBlock
  extends AbstractMember
{
  public static final String NAME = "{...}";
  private CodeBlock parent;
  private List<CodeBlock> children;
  private List<LocalVariable> localVars;
  private boolean isStatic;
  
  public CodeBlock(boolean isStatic, Offset startOffs) {
    super("{...}", startOffs);
    this.isStatic = isStatic;
  }

  
  public void add(CodeBlock child) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(child);
    child.setParent(this);
  }

  
  public void addLocalVariable(LocalVariable localVar) {
    if (this.localVars == null) {
      this.localVars = new ArrayList<>();
    }
    this.localVars.add(localVar);
  }



  
  public boolean containsOffset(int offs) {
    return (getNameEndOffset() >= offs && getNameStartOffset() <= offs);
  }

  
  public CodeBlock getChildBlock(int index) {
    return this.children.get(index);
  }

  
  public int getChildBlockCount() {
    return (this.children == null) ? 0 : this.children.size();
  }










  
  public CodeBlock getDeepestCodeBlockContaining(int offs) {
    if (!containsOffset(offs)) {
      return null;
    }
    for (int i = 0; i < getChildBlockCount(); i++) {
      CodeBlock child = getChildBlock(i);
      if (child.containsOffset(offs)) {
        return child.getDeepestCodeBlockContaining(offs);
      }
    } 
    return this;
  }



  
  public String getDocComment() {
    return null;
  }

  
  public LocalVariable getLocalVar(int index) {
    return this.localVars.get(index);
  }

  
  public int getLocalVarCount() {
    return (this.localVars == null) ? 0 : this.localVars.size();
  }










  
  public List<LocalVariable> getLocalVarsBefore(int offs) {
    List<LocalVariable> vars = new ArrayList<>();
    
    if (this.localVars != null) {
      for (int i = 0; i < getLocalVarCount(); ) {
        LocalVariable localVar = getLocalVar(i);
        if (localVar.getNameStartOffset() < offs) {
          vars.add(localVar);

          
          i++;
        } 
      } 
    }
    
    if (this.parent != null) {
      vars.addAll(this.parent.getLocalVarsBefore(offs));
    }
    
    return vars;
  }



  
  public Modifiers getModifiers() {
    Modifiers modifiers = new Modifiers();
    if (this.isStatic) {
      modifiers.addModifier(65574);
    }
    return modifiers;
  }

  
  public CodeBlock getParent() {
    return this.parent;
  }







  
  public Type getType() {
    return null;
  }


  
  public boolean isDeprecated() {
    return false;
  }







  
  public boolean isStatic() {
    return this.isStatic;
  }

  
  public void setParent(CodeBlock parent) {
    this.parent = parent;
  }
}
