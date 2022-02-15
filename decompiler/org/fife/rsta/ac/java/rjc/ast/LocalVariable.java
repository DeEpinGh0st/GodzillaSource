package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;


















public class LocalVariable
  extends AbstractASTNode
{
  private boolean isFinal;
  private Type type;
  
  public LocalVariable(Scanner s, boolean isFinal, Type type, int offs, String name) {
    super(name, s.createOffset(offs), s.createOffset(offs + name.length()));
    this.isFinal = isFinal;
    this.type = type;
  }

  
  public Type getType() {
    return this.type;
  }

  
  public boolean isFinal() {
    return this.isFinal;
  }
}
