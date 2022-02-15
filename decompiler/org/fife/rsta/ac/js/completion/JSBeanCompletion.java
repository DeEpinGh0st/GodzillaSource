package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;



public class JSBeanCompletion
  extends VariableCompletion
  implements JSCompletion
{
  private JSMethodData methodData;
  private Method method;
  
  public JSBeanCompletion(CompletionProvider provider, MethodInfo methodInfo, JarManager jarManager) {
    super(provider, convertNameToBean(methodInfo.getName()), null);
    setRelevance(5);
    this.methodData = new JSMethodData(methodInfo, jarManager);
    this.method = this.methodData.getMethod();
  }


  
  public boolean equals(Object obj) {
    return (obj instanceof JSBeanCompletion && ((JSBeanCompletion)obj)
      .getName().equals(getName()));
  }


  
  public Icon getIcon() {
    return IconFactory.getIcon("global_variable");
  }


  
  public String getAlreadyEntered(JTextComponent comp) {
    String temp = getProvider().getAlreadyEnteredText(comp);
    
    int lastDot = JavaScriptHelper.findLastIndexOfJavaScriptIdentifier(temp);
    if (lastDot > -1) {
      temp = temp.substring(lastDot + 1);
    }
    return temp;
  }

  
  public String getType() {
    String value = getType(true);
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(value, false);
  }


  
  public String getType(boolean qualified) {
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(this.methodData
        .getType(qualified), qualified);
  }

  
  private String getMethodSummary() {
    String docComment = (this.method != null) ? this.method.getDocComment() : getName();
    return (docComment != null) ? docComment : ((this.method != null) ? this.method
      .toString() : null);
  }


  
  public String getSummary() {
    String summary = getMethodSummary();

    
    if (summary != null && summary.startsWith("/**")) {
      summary = Util.docCommentToHtml(summary);
    }
    
    return summary;
  }


  
  public String getLookupName() {
    return getName();
  }


  
  public String getEnclosingClassName(boolean fullyQualified) {
    return this.methodData.getEnclosingClassName(fullyQualified);
  }

  
  public JSMethodData getMethodData() {
    return this.methodData;
  }

  
  private static String convertNameToBean(String name) {
    boolean memberIsGetMethod = name.startsWith("get");
    boolean memberIsSetMethod = name.startsWith("set");
    boolean memberIsIsMethod = name.startsWith("is");
    if (memberIsGetMethod || memberIsIsMethod || memberIsSetMethod) {
      
      String nameComponent = name.substring(memberIsIsMethod ? 2 : 3);
      if (nameComponent.length() == 0) {
        return name;
      }
      
      String beanPropertyName = nameComponent;
      char ch0 = nameComponent.charAt(0);
      if (Character.isUpperCase(ch0)) {
        if (nameComponent.length() == 1) {
          beanPropertyName = nameComponent.toLowerCase();
        } else {
          
          char ch1 = nameComponent.charAt(1);
          if (!Character.isUpperCase(ch1))
          {
            beanPropertyName = Character.toLowerCase(ch0) + nameComponent.substring(1);
          }
        } 
      }
      name = beanPropertyName;
    } 
    return name;
  }








  
  public int hashCode() {
    return getName().hashCode();
  }




  
  public int compareTo(Completion o) {
    if (o == this) {
      return 0;
    }
    if (o instanceof JSBeanCompletion) {
      JSBeanCompletion c2 = (JSBeanCompletion)o;
      return getLookupName().compareTo(c2.getLookupName());
    } 
    return super.compareTo(o);
  }
}
