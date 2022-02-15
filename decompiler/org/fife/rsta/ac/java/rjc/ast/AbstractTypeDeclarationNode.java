package org.fife.rsta.ac.java.rjc.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lexer.Offset;















public abstract class AbstractTypeDeclarationNode
  extends AbstractASTNode
  implements TypeDeclaration
{
  private Package pkg;
  private Modifiers modifiers;
  private TypeDeclaration parentType;
  private List<TypeDeclaration> childTypes;
  private Offset bodyStartOffs;
  private Offset bodyEndOffs;
  private boolean deprecated;
  private String docComment;
  private List<Member> memberList;
  
  public AbstractTypeDeclarationNode(String name, Offset start) {
    super(name, start);
    init();
  }

  
  public AbstractTypeDeclarationNode(String name, Offset start, Offset end) {
    super(name, start, end);
    init();
  }

  
  public void addMember(Member member) {
    member.setParentTypeDeclaration(this);
    this.memberList.add(member);
  }


  
  public void addTypeDeclaration(TypeDeclaration type) {
    if (this.childTypes == null) {
      this.childTypes = new ArrayList<>(1);
    }
    type.setParentType(this);
    this.childTypes.add(type);
  }


  
  public boolean getBodyContainsOffset(int offs) {
    return (offs >= getBodyStartOffset() && offs < getBodyEndOffset());
  }


  
  public int getBodyEndOffset() {
    return (this.bodyEndOffs != null) ? this.bodyEndOffs.getOffset() : Integer.MAX_VALUE;
  }


  
  public int getBodyStartOffset() {
    return (this.bodyStartOffs == null) ? 0 : this.bodyStartOffs.getOffset();
  }


  
  public TypeDeclaration getChildType(int index) {
    return this.childTypes.get(index);
  }






  
  public TypeDeclaration getChildTypeAtOffset(int offs) {
    TypeDeclaration typeDec = null;
    
    for (int i = 0; i < getChildTypeCount(); i++) {
      TypeDeclaration td = getChildType(i);
      if (td.getBodyContainsOffset(offs)) {
        typeDec = td;
        
        break;
      } 
    } 
    return typeDec;
  }



  
  public int getChildTypeCount() {
    return (this.childTypes == null) ? 0 : this.childTypes.size();
  }


  
  public String getDocComment() {
    return this.docComment;
  }





  
  public Iterator<Field> getFieldIterator() {
    List<Field> fields = new ArrayList<>();
    for (Iterator<Member> i = getMemberIterator(); i.hasNext(); ) {
      Member member = i.next();
      if (member instanceof Field) {
        fields.add((Field)member);
      }
    } 
    return fields.iterator();
  }


  
  public Member getMember(int index) {
    return this.memberList.get(index);
  }


  
  public int getMemberCount() {
    return this.memberList.size();
  }





  
  public Iterator<Member> getMemberIterator() {
    return this.memberList.iterator();
  }





  
  public Iterator<Method> getMethodIterator() {
    List<Method> methods = new ArrayList<>();
    for (Iterator<Member> i = getMemberIterator(); i.hasNext(); ) {
      Member member = i.next();
      if (member instanceof Method) {
        methods.add((Method)member);
      }
    } 
    return methods.iterator();
  }





  
  public List<Method> getMethodsByName(String name) {
    List<Method> methods = new ArrayList<>();
    for (Iterator<Member> i = getMemberIterator(); i.hasNext(); ) {
      Member member = i.next();
      if (member instanceof Method && name.equals(member.getName())) {
        methods.add((Method)member);
      }
    } 
    return methods;
  }


  
  public Modifiers getModifiers() {
    return this.modifiers;
  }





  
  public String getName(boolean fullyQualified) {
    String name = getName();
    if (fullyQualified) {
      Package pkg = getPackage();
      if (pkg != null) {
        name = pkg.getName() + "." + name;
      }
    } 
    return name;
  }





  
  public Package getPackage() {
    return this.pkg;
  }





  
  public TypeDeclaration getParentType() {
    return this.parentType;
  }

  
  private void init() {
    this.memberList = new ArrayList<>();
  }


  
  public boolean isDeprecated() {
    return this.deprecated;
  }





  
  public boolean isStatic() {
    return (this.modifiers == null) ? false : this.modifiers.isStatic();
  }

  
  public void setBodyEndOffset(Offset end) {
    this.bodyEndOffs = end;
  }

  
  public void setBodyStartOffset(Offset start) {
    this.bodyStartOffs = start;
  }

  
  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }


  
  public void setDocComment(String comment) {
    this.docComment = comment;
  }

  
  public void setModifiers(Modifiers modifiers) {
    this.modifiers = modifiers;
  }








  
  public void setPackage(Package pkg) {
    this.pkg = pkg;
  }





  
  public void setParentType(TypeDeclaration parentType) {
    this.parentType = parentType;
  }


  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (this.modifiers != null) {
      sb.append(this.modifiers.toString()).append(' ');
    }
    sb.append(getTypeString()).append(' ');
    sb.append(getName());
    return sb.toString();
  }
}
