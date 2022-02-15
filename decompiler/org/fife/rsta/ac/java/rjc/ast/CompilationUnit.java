package org.fife.rsta.ac.java.rjc.ast;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lang.Annotation;
import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.Token;
import org.fife.rsta.ac.java.rjc.notices.ParserNotice;

























public class CompilationUnit
  extends AbstractASTNode
  implements TypeDeclarationContainer
{
  private List<Annotation> annotations;
  private Package pkg;
  private List<ImportDeclaration> imports;
  private List<TypeDeclaration> typeDeclarations;
  private List<ParserNotice> notices;
  private static final Offset ZERO_OFFSET = new ZeroOffset();

  
  public CompilationUnit(String name) {
    super(name, ZERO_OFFSET);
    this.imports = new ArrayList<>(3);
    this.typeDeclarations = new ArrayList<>(1);
  }

  
  public void addImportDeclaration(ImportDeclaration dec) {
    this.imports.add(dec);
  }







  
  public void addParserNotice(Token t, String msg) {
    addParserNotice(new ParserNotice(t, msg));
  }

  
  public void addParserNotice(ParserNotice notice) {
    if (this.notices == null) {
      this.notices = new ArrayList<>();
      this.notices.add(notice);
    } 
  }


  
  public void addTypeDeclaration(TypeDeclaration typeDec) {
    this.typeDeclarations.add(typeDec);
  }

  
  public int getAnnotationCount() {
    return this.annotations.size();
  }

  
  public Iterator<Annotation> getAnnotationIterator() {
    return this.annotations.iterator();
  }












  
  public TypeDeclaration getDeepestTypeDeclarationAtOffset(int offs) {
    TypeDeclaration td = getTypeDeclarationAtOffset(offs);
    
    if (td != null) {
      TypeDeclaration next = td.getChildTypeAtOffset(offs);
      while (next != null) {
        td = next;
        next = td.getChildTypeAtOffset(offs);
      } 
    } 
    
    return td;
  }










  
  public Point getEnclosingMethodRange(int offs) {
    Point range = null;
    
    for (Iterator<TypeDeclaration> i = getTypeDeclarationIterator(); i.hasNext(); ) {
      
      TypeDeclaration td = i.next();
      int start = td.getBodyStartOffset();
      int end = td.getBodyEndOffset();
      
      if (offs >= start && offs <= end) {
        
        if (td instanceof NormalClassDeclaration) {
          NormalClassDeclaration ncd = (NormalClassDeclaration)td;
          for (Iterator<Member> j = ncd.getMemberIterator(); j.hasNext(); ) {
            Member m = j.next();
            if (m instanceof Method) {
              Method method = (Method)m;
              CodeBlock body = method.getBody();
              if (body != null) {
                int start2 = method.getNameStartOffset();
                
                int end2 = body.getNameEndOffset();
                if (offs >= start2 && offs <= end2) {
                  range = new Point(start2, end2);
                  
                  break;
                } 
              } 
            } 
          } 
        } 
        if (range == null) {
          range = new Point(start, end);
        }
      } 
    } 


    
    return range;
  }


  
  public int getImportCount() {
    return this.imports.size();
  }









  
  public List<ImportDeclaration> getImports() {
    return new ArrayList<>(this.imports);
  }

  
  public Iterator<ImportDeclaration> getImportIterator() {
    return this.imports.iterator();
  }








  
  public Package getPackage() {
    return this.pkg;
  }








  
  public String getPackageName() {
    return (this.pkg == null) ? null : this.pkg.getName();
  }

  
  public ParserNotice getParserNotice(int index) {
    if (this.notices == null) {
      throw new IndexOutOfBoundsException("No parser notices available");
    }
    return this.notices.get(index);
  }

  
  public int getParserNoticeCount() {
    return (this.notices == null) ? 0 : this.notices.size();
  }

  
  public TypeDeclaration getTypeDeclaration(int index) {
    return this.typeDeclarations.get(index);
  }











  
  public TypeDeclaration getTypeDeclarationAtOffset(int offs) {
    TypeDeclaration typeDec = null;
    
    for (TypeDeclaration td : this.typeDeclarations) {
      if (td.getBodyContainsOffset(offs)) {
        typeDec = td;
        
        break;
      } 
    } 
    return typeDec;
  }


  
  public int getTypeDeclarationCount() {
    return this.typeDeclarations.size();
  }

  
  public Iterator<TypeDeclaration> getTypeDeclarationIterator() {
    return this.typeDeclarations.iterator();
  }

  
  public void setPackage(Package pkg) {
    this.pkg = pkg;
  }

  
  private static class ZeroOffset
    implements Offset
  {
    private ZeroOffset() {}

    
    public int getOffset() {
      return 0;
    }
  }
}
