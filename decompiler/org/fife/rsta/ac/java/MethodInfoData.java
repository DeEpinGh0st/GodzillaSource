package org.fife.rsta.ac.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.classreader.Util;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;






































class MethodInfoData
  implements MemberCompletion.Data
{
  private SourceCompletionProvider provider;
  private MethodInfo info;
  private List<String> paramNames;
  
  public MethodInfoData(MethodInfo info, SourceCompletionProvider provider) {
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
      key = "methodDefaultIcon";
    }
    else if (Util.isPrivate(flags)) {
      key = "methodPrivateIcon";
    }
    else if (Util.isProtected(flags)) {
      key = "methodProtectedIcon";
    }
    else if (Util.isPublic(flags)) {
      key = "methodPublicIcon";
    } else {
      
      key = "methodDefaultIcon";
    } 
    
    return key;
  }













  
  private Method getMethodFromSourceLoc(SourceLocation loc, ClassFile cf) {
    Method res = null;
    
    CompilationUnit cu = Util.getCompilationUnitFromDisk(loc, cf);


    
    if (cu != null) {
      
      Iterator<TypeDeclaration> i = cu.getTypeDeclarationIterator();
      while (i.hasNext()) {
        
        TypeDeclaration td = i.next();
        String typeName = td.getName();

        
        if (typeName.equals(cf.getClassName(false))) {



          
          List<Method> contenders = null;
          for (int j = 0; j < td.getMemberCount(); j++) {
            Member member = td.getMember(j);
            if (member instanceof Method && member
              .getName().equals(this.info.getName())) {
              Method m2 = (Method)member;
              if (m2.getParameterCount() == this.info.getParameterCount()) {
                if (contenders == null) {
                  contenders = new ArrayList<>(1);
                }
                contenders.add(m2);
              } 
            } 
          } 

          
          if (contenders != null) {


            
            if (contenders.size() == 1) {
              res = contenders.get(0);


              
              break;
            } 

            
            for (Method method : contenders) {
              boolean match = true;
              for (int p = 0; p < this.info.getParameterCount(); p++) {
                String type1 = this.info.getParameterType(p, false);
                FormalParameter fp = method.getParameter(p);
                String type2 = fp.getType().toString();
                if (!type1.equals(type2)) {
                  match = false;
                  break;
                } 
              } 
              if (match) {
                res = method;


                
                break;
              } 
            } 
          } 

          
          break;
        } 
      } 
    } 

    
    return res;
  }















  
  public String getParameterName(int index) {
    String name = this.info.getParameterName(index);

    
    if (name == null) {

      
      if (this.paramNames == null) {
        
        this.paramNames = new ArrayList<>(1);
        int offs = 0;
        String rawSummary = getSummary();

        
        if (rawSummary != null && rawSummary.startsWith("/**")) {

          
          int summaryLen = rawSummary.length();
          int nextParam;
          while ((nextParam = rawSummary.indexOf("@param", offs)) > -1) {
            int temp = nextParam + "@param".length() + 1;
            while (temp < summaryLen && 
              Character.isWhitespace(rawSummary.charAt(temp))) {
              temp++;
            }
            if (temp < summaryLen) {
              int start = temp;
              int end = start + 1;
              while (end < summaryLen && 
                Character.isJavaIdentifierPart(rawSummary.charAt(end))) {
                end++;
              }
              this.paramNames.add(rawSummary.substring(start, end));
              offs = end;
            } 
          } 
        } 
      } 





      
      if (index < this.paramNames.size()) {
        name = this.paramNames.get(index);
      }
    } 


    
    if (name == null) {
      name = "arg" + index;
    }
    
    return name;
  }








  
  public String getSignature() {
    StringBuilder sb = new StringBuilder(this.info.getName());
    
    sb.append('(');
    int paramCount = this.info.getParameterCount();
    for (int i = 0; i < paramCount; i++) {
      sb.append(this.info.getParameterType(i, false));
      sb.append(' ');
      sb.append(getParameterName(i));
      if (i < paramCount - 1) {
        sb.append(", ");
      }
    } 
    sb.append(')');
    
    return sb.toString();
  }




  
  public String getSummary() {
    ClassFile cf = this.info.getClassFile();
    SourceLocation loc = this.provider.getSourceLocForClass(cf.getClassName(true));
    String summary = null;


    
    if (loc != null) {
      summary = getSummaryFromSourceLoc(loc, cf);
    }

    
    if (summary == null) {
      summary = this.info.getSignature();
    }
    return summary;
  }












  
  private String getSummaryFromSourceLoc(SourceLocation loc, ClassFile cf) {
    Method method = getMethodFromSourceLoc(loc, cf);
    return (method != null) ? method.getDocComment() : null;
  }





  
  public String getType() {
    return this.info.getReturnTypeString(false);
  }


  
  public boolean isAbstract() {
    return this.info.isAbstract();
  }





  
  public boolean isConstructor() {
    return this.info.isConstructor();
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
