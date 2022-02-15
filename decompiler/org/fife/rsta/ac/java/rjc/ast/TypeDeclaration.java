package org.fife.rsta.ac.java.rjc.ast;

import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;

public interface TypeDeclaration extends ASTNode, TypeDeclarationContainer {
  boolean getBodyContainsOffset(int paramInt);
  
  int getBodyEndOffset();
  
  int getBodyStartOffset();
  
  TypeDeclaration getChildType(int paramInt);
  
  TypeDeclaration getChildTypeAtOffset(int paramInt);
  
  int getChildTypeCount();
  
  String getDocComment();
  
  Iterator<Field> getFieldIterator();
  
  Member getMember(int paramInt);
  
  int getMemberCount();
  
  Iterator<Member> getMemberIterator();
  
  Iterator<Method> getMethodIterator();
  
  List<Method> getMethodsByName(String paramString);
  
  Modifiers getModifiers();
  
  String getName();
  
  String getName(boolean paramBoolean);
  
  Package getPackage();
  
  TypeDeclaration getParentType();
  
  String getTypeString();
  
  boolean isDeprecated();
  
  boolean isStatic();
  
  void setDocComment(String paramString);
  
  void setParentType(TypeDeclaration paramTypeDeclaration);
}
