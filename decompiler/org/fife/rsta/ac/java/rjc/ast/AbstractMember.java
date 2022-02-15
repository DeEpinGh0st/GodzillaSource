package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lexer.Offset;

















abstract class AbstractMember
  extends AbstractASTNode
  implements Member
{
  private TypeDeclaration parentTypeDec;
  
  protected AbstractMember(String name, Offset start) {
    super(name, start);
  }

  
  protected AbstractMember(String name, Offset start, Offset end) {
    super(name, start, end);
  }


  
  public TypeDeclaration getParentTypeDeclaration() {
    return this.parentTypeDec;
  }





  
  public boolean isStatic() {
    Modifiers modifiers = getModifiers();
    return (modifiers != null && modifiers.isStatic());
  }





  
  public void setParentTypeDeclaration(TypeDeclaration dec) {
    if (dec == null) {
      throw new InternalError("Parent TypeDeclaration cannot be null");
    }
    this.parentTypeDec = dec;
  }
}
