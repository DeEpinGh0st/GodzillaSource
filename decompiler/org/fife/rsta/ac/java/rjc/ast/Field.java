package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;











public class Field
  extends AbstractMember
{
  private Modifiers modifiers;
  private Type type;
  private boolean deprecated;
  private String docComment;
  
  public Field(Scanner s, Modifiers modifiers, Type type, Token t) {
    super(t.getLexeme(), s.createOffset(t.getOffset()));
    setDeclarationEndOffset(s.createOffset(t.getOffset() + t.getLength()));
    if (modifiers == null) {
      modifiers = new Modifiers();
    }
    this.modifiers = modifiers;
    this.type = type;
  }


  
  public String getDocComment() {
    return this.docComment;
  }


  
  public Modifiers getModifiers() {
    return this.modifiers;
  }


  
  public Type getType() {
    return this.type;
  }


  
  public boolean isDeprecated() {
    return this.deprecated;
  }

  
  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }

  
  public void setDocComment(String comment) {
    this.docComment = comment;
  }
}
