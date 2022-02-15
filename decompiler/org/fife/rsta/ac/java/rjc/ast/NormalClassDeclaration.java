package org.fife.rsta.ac.java.rjc.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lang.TypeParameter;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;

























public class NormalClassDeclaration
  extends AbstractTypeDeclarationNode
{
  private List<TypeParameter> typeParams;
  private Type extendedType;
  private List<Type> implementedList;
  
  public NormalClassDeclaration(Scanner s, int offs, String className) {
    super(className, s.createOffset(offs), s.createOffset(offs + className.length()));
    this.implementedList = new ArrayList<>(0);


    
    this.extendedType = new Type("java.lang.Object");
  }

  
  public void addImplemented(Type implemented) {
    this.implementedList.add(implemented);
  }

  
  public Type getExtendedType() {
    return this.extendedType;
  }

  
  public int getImplementedCount() {
    return this.implementedList.size();
  }

  
  public Iterator<Type> getImplementedIterator() {
    return this.implementedList.iterator();
  }








  
  public Method getMethodContainingOffset(int offs) {
    for (Iterator<Method> i = getMethodIterator(); i.hasNext(); ) {
      Method method = i.next();
      if (method.getBodyContainsOffset(offs)) {
        return method;
      }
    } 
    return null;
  }

  
  public List<TypeParameter> getTypeParameters() {
    return this.typeParams;
  }


  
  public String getTypeString() {
    return "class";
  }











  
  private boolean isTypeCompatible(Type type, String typeName) {
    String typeName2 = type.getName(false);


    
    int lt = typeName2.indexOf('<');
    if (lt > -1) {
      String arrayDepth = null;
      int brackets = typeName2.indexOf('[', lt);
      if (brackets > -1) {
        arrayDepth = typeName2.substring(brackets);
      }
      typeName2 = typeName2.substring(lt);
      if (arrayDepth != null) {
        typeName2 = typeName2 + arrayDepth;
      }
    } 
    
    return typeName2.equalsIgnoreCase(typeName);
  }


  
  public void setExtendedType(Type type) {
    this.extendedType = type;
  }

  
  public void setTypeParameters(List<TypeParameter> typeParams) {
    this.typeParams = typeParams;
  }
}
