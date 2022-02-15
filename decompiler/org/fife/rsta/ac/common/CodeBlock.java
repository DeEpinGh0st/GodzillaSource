package org.fife.rsta.ac.common;

import java.util.ArrayList;
import java.util.List;




























public class CodeBlock
{
  private int start;
  private int end;
  private CodeBlock parent;
  private List<CodeBlock> children;
  private List<VariableDeclaration> varDecs;
  
  public CodeBlock(int start) {
    this.start = start;
    this.end = Integer.MAX_VALUE;
  }







  
  public CodeBlock addChildCodeBlock(int start) {
    CodeBlock child = new CodeBlock(start);
    child.parent = this;
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(child);
    return child;
  }






  
  public void addVariable(VariableDeclaration varDec) {
    if (this.varDecs == null) {
      this.varDecs = new ArrayList<>();
    }
    this.varDecs.add(varDec);
  }







  
  public boolean contains(int offset) {
    return (offset >= this.start && offset < this.end);
  }








  
  public CodeBlock getChildCodeBlock(int index) {
    return this.children.get(index);
  }







  
  public int getChildCodeBlockCount() {
    return (this.children == null) ? 0 : this.children.size();
  }










  
  public CodeBlock getDeepestCodeBlockContaining(int offs) {
    if (!contains(offs)) {
      return null;
    }
    for (int i = 0; i < getChildCodeBlockCount(); i++) {
      CodeBlock child = getChildCodeBlock(i);
      if (child.contains(offs)) {
        return child.getDeepestCodeBlockContaining(offs);
      }
    } 
    return this;
  }








  
  public int getEndOffset() {
    return this.end;
  }






  
  public CodeBlock getParent() {
    return this.parent;
  }







  
  public int getStartOffset() {
    return this.start;
  }








  
  public VariableDeclaration getVariableDeclaration(int index) {
    return this.varDecs.get(index);
  }







  
  public int getVariableDeclarationCount() {
    return (this.varDecs == null) ? 0 : this.varDecs.size();
  }










  
  public List<VariableDeclaration> getVariableDeclarationsBefore(int offs) {
    List<VariableDeclaration> vars = new ArrayList<>();
    
    int varCount = getVariableDeclarationCount();
    for (int i = 0; i < varCount; ) {
      VariableDeclaration localVar = getVariableDeclaration(i);
      if (localVar.getOffset() < offs) {
        vars.add(localVar);

        
        i++;
      } 
    } 
    
    if (this.parent != null) {
      vars.addAll(this.parent.getVariableDeclarationsBefore(offs));
    }
    
    return vars;
  }








  
  public void setEndOffset(int end) {
    this.end = end;
  }
}
