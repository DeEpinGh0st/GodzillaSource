package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;


public class JavaScriptInScriptFunctionCompletion
  extends FunctionCompletion
  implements JSCompletion
{
  private TypeDeclaration returnType;
  
  public JavaScriptInScriptFunctionCompletion(CompletionProvider provider, String name, TypeDeclaration returnType) {
    super(provider, name, null);
    setRelevance(4);
    this.returnType = returnType;
  }


  
  public String getSummary() {
    String summary = getShortDescription();


    
    if (summary != null && summary.startsWith("/**")) {
      summary = Util.docCommentToHtml(summary);
    }
    
    return summary;
  }


  
  public Icon getIcon() {
    return IconFactory.getIcon("default_function");
  }


  
  public String getLookupName() {
    StringBuilder sb = new StringBuilder(getName());
    sb.append('(');
    int count = getParamCount();
    for (int i = 0; i < count; i++) {
      sb.append("p");
      if (i < count - 1) {
        sb.append(",");
      }
    } 
    sb.append(')');
    return sb.toString();
  }


  
  public String getType() {
    String value = getType(true);
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(value, false);
  }


  
  public String getType(boolean qualified) {
    String type = (this.returnType != null) ? this.returnType.getQualifiedName() : null;
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(type, qualified);
  }



  
  public String getEnclosingClassName(boolean fullyQualified) {
    return null;
  }


  
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof JSCompletion) {
      
      JSCompletion jsComp = (JSCompletion)obj;
      return getLookupName().equals(jsComp.getLookupName());
    } 
    return super.equals(obj);
  }


  
  public int hashCode() {
    return getLookupName().hashCode();
  }

  
  public String toString() {
    return getLookupName();
  }




  
  public int compareTo(Completion other) {
    if (other == this) {
      return 0;
    }
    if (other instanceof JSCompletion) {
      JSCompletion c2 = (JSCompletion)other;
      return getLookupName().compareTo(c2.getLookupName());
    } 
    if (other != null) {
      return toString().compareTo(other.toString());
    }
    return -1;
  }
}
