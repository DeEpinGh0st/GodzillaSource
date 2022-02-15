package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;

















public class ImportDeclaration
  extends AbstractASTNode
{
  private boolean isStatic;
  
  public ImportDeclaration(Scanner s, int offs, String info, boolean isStatic) {
    super(info, s.createOffset(offs), s.createOffset(offs + info.length()));
    setStatic(isStatic);
  }

  
  public boolean isStatic() {
    return this.isStatic;
  }

  
  public boolean isWildcard() {
    return getName().endsWith(".*");
  }

  
  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }
}
