package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;


public class JSFieldCompletion
  extends VariableCompletion
  implements JSCompletion
{
  private JSFieldData fieldData;
  private Field field;
  
  public JSFieldCompletion(CompletionProvider provider, FieldInfo fieldInfo) {
    super(provider, fieldInfo.getName(), null);
    this.fieldData = new JSFieldData(fieldInfo, ((SourceCompletionProvider)provider).getJarManager());
    this.field = this.fieldData.getField();
    setRelevance(this.fieldData);
  }

  
  private void setRelevance(JSFieldData data) {
    if (data.isStatic()) {
      setRelevance(6);
    } else {
      setRelevance(8);
    } 
  }


  
  public String getSummary() {
    String summary = (this.field != null) ? this.field.getDocComment() : getName();

    
    if (summary != null && summary.startsWith("/**")) {
      summary = Util.docCommentToHtml(summary);
    }
    
    return summary;
  }



  
  public Icon getIcon() {
    return this.fieldData.isStatic() ? IconFactory.getIcon("static_var") : (
      this.fieldData.isPublic() ? IconFactory.getIcon("global_variable") : IconFactory.getIcon("default_variable"));
  }


  
  public String getEnclosingClassName(boolean fullyQualified) {
    return this.fieldData.getEnclosingClassName(fullyQualified);
  }


  
  public String getAlreadyEntered(JTextComponent comp) {
    String temp = getProvider().getAlreadyEnteredText(comp);
    
    int lastDot = JavaScriptHelper.findLastIndexOfJavaScriptIdentifier(temp);
    if (lastDot > -1) {
      temp = temp.substring(lastDot + 1);
    }
    return temp;
  }


  
  public String getLookupName() {
    return getReplacementText();
  }


  
  public String getType() {
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(this.fieldData.getType(true), false);
  }



  
  public String getType(boolean qualified) {
    return this.fieldData.getType(true);
  }


  
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof JSFieldCompletion) {
      
      JSFieldCompletion jsComp = (JSFieldCompletion)obj;
      return getLookupName().equals(jsComp.getLookupName());
    } 
    return super.equals(obj);
  }




  
  public int compareTo(Completion o) {
    if (o == this) {
      return 0;
    }
    if (o instanceof JSFieldCompletion) {
      JSFieldCompletion c2 = (JSFieldCompletion)o;
      return getLookupName().compareTo(c2.getLookupName());
    } 
    return super.compareTo(o);
  }

  
  public int hashCode() {
    return getLookupName().hashCode();
  }
}
