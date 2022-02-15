package org.fife.rsta.ac.java;

import java.util.Iterator;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.Util;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;





















class FieldInfoData
  implements MemberCompletion.Data
{
  private FieldInfo info;
  private SourceCompletionProvider provider;
  
  public FieldInfoData(FieldInfo info, SourceCompletionProvider provider) {
    this.info = info;
    this.provider = provider;
  }





  
  public String getEnclosingClassName(boolean fullyQualified) {
    return this.info.getClassFile().getClassName(fullyQualified);
  }






  
  public String getIcon() {
    String key;
    int flags = this.info.getAccessFlags();
    
    if (Util.isDefault(flags)) {
      key = "fieldDefaultIcon";
    }
    else if (Util.isPrivate(flags)) {
      key = "fieldPrivateIcon";
    }
    else if (Util.isProtected(flags)) {
      key = "fieldProtectedIcon";
    }
    else if (Util.isPublic(flags)) {
      key = "fieldPublicIcon";
    } else {
      
      key = "fieldDefaultIcon";
    } 
    
    return key;
  }






  
  public String getSignature() {
    return this.info.getName();
  }






  
  public String getSummary() {
    ClassFile cf = this.info.getClassFile();
    SourceLocation loc = this.provider.getSourceLocForClass(cf.getClassName(true));
    String summary = null;


    
    if (loc != null) {
      summary = getSummaryFromSourceLoc(loc, cf);
    }

    
    if (summary == null) {
      summary = this.info.getName();
    }
    return summary;
  }













  
  private String getSummaryFromSourceLoc(SourceLocation loc, ClassFile cf) {
    String summary = null;
    
    CompilationUnit cu = Util.getCompilationUnitFromDisk(loc, cf);


    
    if (cu != null) {
      
      Iterator<TypeDeclaration> i = cu.getTypeDeclarationIterator();
      while (i.hasNext()) {
        
        TypeDeclaration td = i.next();
        String typeName = td.getName();

        
        if (typeName.equals(cf.getClassName(false))) {

          
          Iterator<Member> j = td.getMemberIterator();
          while (j.hasNext()) {
            Member member = j.next();
            if (member instanceof Field && member
              .getName().equals(this.info.getName())) {
              Field f2 = (Field)member;
              summary = f2.getDocComment();
            } 
          } 
        } 
      } 
    } 




    
    return summary;
  }






  
  public String getType() {
    return this.info.getTypeString(false);
  }







  
  public boolean isAbstract() {
    return false;
  }







  
  public boolean isConstructor() {
    return false;
  }





  
  public boolean isDeprecated() {
    return this.info.isDeprecated();
  }


  
  public boolean isFinal() {
    return this.info.isFinal();
  }


  
  public boolean isStatic() {
    return this.info.isStatic();
  }
}
