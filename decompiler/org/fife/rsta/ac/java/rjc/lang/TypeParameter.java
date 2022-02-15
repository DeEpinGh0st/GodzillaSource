package org.fife.rsta.ac.java.rjc.lang;

import java.util.ArrayList;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lexer.Token;




























public class TypeParameter
{
  private Token name;
  private List<Type> bounds;
  
  public TypeParameter(Token name) {
    this.name = name;
  }

  
  public void addBound(Type bound) {
    if (this.bounds == null) {
      this.bounds = new ArrayList<>(1);
    }
    this.bounds.add(bound);
  }

  
  public String getName() {
    return this.name.getLexeme();
  }
}
