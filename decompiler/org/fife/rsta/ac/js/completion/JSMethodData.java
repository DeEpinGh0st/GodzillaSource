package org.fife.rsta.ac.js.completion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;




public class JSMethodData
{
  private MethodInfo info;
  private JarManager jarManager;
  private ArrayList<String> paramNames;
  
  public JSMethodData(MethodInfo info, JarManager jarManager) {
    this.info = info;
    this.jarManager = jarManager;
  }













  
  public String getParameterName(int index) {
    String name = this.info.getParameterName(index);

    
    Method method = getMethod();
    if (method != null)
    {
      name = method.getParameter(index).getName();
    }

    
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
            while ((temp < summaryLen && 
              !Character.isJavaIdentifierPart(rawSummary.charAt(temp))) || 
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


  
  public String getParameterType(String[] paramTypes, int index, CompletionProvider provider) {
    if (paramTypes != null && index < paramTypes.length)
    {
      return ((SourceCompletionProvider)provider).getTypesFactory().convertJavaScriptType(paramTypes[index], true);
    }
    return null;
  }

  
  public String getSummary() {
    ClassFile cf = this.info.getClassFile();
    SourceLocation loc = this.jarManager.getSourceLocForClass(cf
        .getClassName(true));
    String summary = null;


    
    if (loc != null) {
      summary = getSummaryFromSourceLoc(loc, cf);
    }

    
    if (summary == null) {
      
      this.info.getReturnTypeString(true);
      summary = this.info.getSignature();
    } 
    return summary;
  }


  
  public Method getMethod() {
    ClassFile cf = this.info.getClassFile();
    SourceLocation loc = this.jarManager.getSourceLocForClass(cf
        .getClassName(true));
    return getMethodFromSourceLoc(loc, cf);
  }










  
  private String getSummaryFromSourceLoc(SourceLocation loc, ClassFile cf) {
    Method method = getMethodFromSourceLoc(loc, cf);
    return (method != null) ? method.getDocComment() : null;
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
          for (Iterator<Member> j = td.getMemberIterator(); j.hasNext(); ) {
            Member member = j.next();
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

            
            for (int k = 0; k < contenders.size(); k++) {
              boolean match = true;
              Method method = contenders.get(k);
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


  
  public MethodInfo getMethodInfo() {
    return this.info;
  }

  
  public String getType(boolean qualified) {
    return this.info.getReturnTypeString(qualified);
  }

  
  public int getParameterCount() {
    return this.info.getParameterCount();
  }

  
  public boolean isStatic() {
    return this.info.isStatic();
  }
  
  public String getEnclosingClassName(boolean fullyQualified) {
    return this.info.getClassFile().getClassName(fullyQualified);
  }
}
