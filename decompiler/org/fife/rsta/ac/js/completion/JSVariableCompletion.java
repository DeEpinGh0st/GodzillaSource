package org.fife.rsta.ac.js.completion;

import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;















public class JSVariableCompletion
  extends VariableCompletion
  implements JSCompletionUI
{
  private JavaScriptVariableDeclaration dec;
  private boolean localVariable;
  
  public JSVariableCompletion(CompletionProvider provider, JavaScriptVariableDeclaration dec) {
    this(provider, dec, true);
  }


  
  public JSVariableCompletion(CompletionProvider provider, JavaScriptVariableDeclaration dec, boolean localVariable) {
    super(provider, dec.getName(), dec.getJavaScriptTypeName());
    this.dec = dec;
    this.localVariable = localVariable;
  }





  
  public String getType() {
    return getType(false);
  }





  
  public String getType(boolean qualified) {
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(this.dec.getJavaScriptTypeName(), qualified);
  }



  
  public String getAlreadyEntered(JTextComponent comp) {
    String temp = getProvider().getAlreadyEnteredText(comp);
    
    int lastDot = JavaScriptHelper.findLastIndexOfJavaScriptIdentifier(temp);
    if (lastDot > -1) {
      temp = temp.substring(lastDot + 1);
    }
    return temp;
  }


  
  public Icon getIcon() {
    return 
      IconFactory.getIcon(this.localVariable ? "local_variable" : "global_variable");
  }



  
  public int getRelevance() {
    return this.localVariable ? 9 : 8;
  }


  
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof VariableCompletion) {
      VariableCompletion comp = (VariableCompletion)obj;
      return getName().equals(comp.getName());
    } 
    
    return super.equals(obj);
  }

  
  public int compareTo(Completion c2) {
    if (c2 == this) {
      return 0;
    }
    return super.compareTo(c2);
  }


  
  public int hashCode() {
    return getName().hashCode();
  }


  
  public String getSummary() {
    SourceCompletionProvider scp = (SourceCompletionProvider)getProvider();
    ClassFile cf = scp.getJavaScriptTypesFactory().getClassFile(scp.getJarManager(), JavaScriptHelper.createNewTypeDeclaration(getType(true)));
    if (cf != null) {
      
      SourceLocation loc = scp.getSourceLocForClass(cf.getClassName(true));
      
      if (loc != null) {
        
        CompilationUnit cu = Util.getCompilationUnitFromDisk(loc, cf);
        if (cu != null) {
          Iterator<TypeDeclaration> i = cu.getTypeDeclarationIterator();
          while (i.hasNext()) {
            TypeDeclaration td = i.next();
            String typeName = td.getName();
            
            if (typeName.equals(cf.getClassName(false))) {
              String summary = td.getDocComment();
              
              if (summary != null && summary.startsWith("/**")) {
                return Util.docCommentToHtml(summary);
              }
            } 
          } 
        } 
      } 
      
      return cf.getClassName(true);
    } 
    
    return super.getSummary();
  }
}
