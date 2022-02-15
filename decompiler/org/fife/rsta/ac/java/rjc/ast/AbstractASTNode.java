package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Offset;


















abstract class AbstractASTNode
  implements ASTNode
{
  private String name;
  private Offset startOffs;
  private Offset endOffs;
  
  protected AbstractASTNode(String name, Offset start) {
    this(name, start, null);
  }

  
  protected AbstractASTNode(String name, Offset start, Offset end) {
    this.name = name;
    this.startOffs = start;
    this.endOffs = end;
  }





  
  public String getName() {
    return this.name;
  }





  
  public int getNameEndOffset() {
    return (this.endOffs != null) ? this.endOffs.getOffset() : Integer.MAX_VALUE;
  }





  
  public int getNameStartOffset() {
    return (this.startOffs != null) ? this.startOffs.getOffset() : 0;
  }

  
  public void setDeclarationEndOffset(Offset end) {
    this.endOffs = end;
  }







  
  protected void setDeclarationOffsets(Offset start, Offset end) {
    this.startOffs = start;
    this.endOffs = end;
  }








  
  public String toString() {
    return getName();
  }
}
