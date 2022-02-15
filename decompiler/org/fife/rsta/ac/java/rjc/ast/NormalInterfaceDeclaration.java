package org.fife.rsta.ac.java.rjc.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
























public class NormalInterfaceDeclaration
  extends AbstractTypeDeclarationNode
{
  private List<Type> extendedList;
  
  public NormalInterfaceDeclaration(Scanner s, int offs, String name) {
    super(name, s.createOffset(offs), s.createOffset(offs + name.length()));
    this.extendedList = new ArrayList<>(1);
  }

  
  public void addExtended(Type extended) {
    this.extendedList.add(extended);
  }

  
  public int getExtendedCount() {
    return this.extendedList.size();
  }

  
  public Iterator<Type> getExtendedIterator() {
    return this.extendedList.iterator();
  }


  
  public String getTypeString() {
    return "interface";
  }
}
