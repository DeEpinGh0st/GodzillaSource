package org.fife.rsta.ac.js.completion;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;












public class JSFunctionCompletion
  extends FunctionCompletion
  implements JSCompletion
{
  private JSMethodData methodData;
  private String compareString;
  private String nameAndParameters;
  
  public JSFunctionCompletion(CompletionProvider provider, MethodInfo method) {
    this(provider, method, false);
  }


  
  public JSFunctionCompletion(CompletionProvider provider, MethodInfo methodInfo, boolean showParameterType) {
    super(provider, getMethodName(methodInfo, provider), null);
    this
      .methodData = new JSMethodData(methodInfo, ((SourceCompletionProvider)provider).getJarManager());
    List<ParameterizedCompletion.Parameter> params = populateParams(this.methodData, showParameterType);
    setParams(params);
  }

  
  private static String getMethodName(MethodInfo info, CompletionProvider provider) {
    if (info.isConstructor()) {
      return ((SourceCompletionProvider)provider).getTypesFactory().convertJavaScriptType(info.getClassFile().getClassName(true), false);
    }
    return info.getName();
  }


  
  private List<ParameterizedCompletion.Parameter> populateParams(JSMethodData methodData, boolean showParameterType) {
    MethodInfo methodInfo = methodData.getMethodInfo();
    int count = methodInfo.getParameterCount();
    String[] paramTypes = methodInfo.getParameterTypes();
    List<ParameterizedCompletion.Parameter> params = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      String name = methodData.getParameterName(i);
      String type = methodData.getParameterType(paramTypes, i, getProvider());
      params.add(new JSFunctionParam(type, name, showParameterType, getProvider()));
    } 
    
    return params;
  }





  
  public int compareTo(Completion other) {
    int rc = -1;
    if (other == this) {
      rc = 0;
    }
    else if (other instanceof JSCompletion) {
      JSCompletion c2 = (JSCompletion)other;
      rc = getLookupName().compareTo(c2.getLookupName());
    }
    else if (other != null) {
      rc = toString().compareTo(other.toString());
      if (rc == 0) {
        String clazz1 = getClass().getName();
        clazz1 = clazz1.substring(clazz1.lastIndexOf('.'));
        String clazz2 = other.getClass().getName();
        clazz2 = clazz2.substring(clazz2.lastIndexOf('.'));
        rc = clazz1.compareTo(clazz2);
      } 
    } 
    
    return rc;
  }


  
  public boolean equals(Object obj) {
    return (obj instanceof JSCompletion && ((JSCompletion)obj)
      .getLookupName().equals(
        getLookupName()));
  }


  
  public String getAlreadyEntered(JTextComponent comp) {
    String temp = getProvider().getAlreadyEnteredText(comp);
    
    int lastDot = JavaScriptHelper.findLastIndexOfJavaScriptIdentifier(temp);
    if (lastDot > -1) {
      temp = temp.substring(lastDot + 1);
    }
    return temp;
  }










  
  private String getCompareString() {
    if (this.compareString == null)
    {
      this.compareString = getLookupName();
    }
    
    return this.compareString;
  }



  
  public String getLookupName() {
    SourceCompletionProvider provider = (SourceCompletionProvider)getProvider();
    return provider.getJavaScriptEngine().getJavaScriptResolver(provider).getLookupText(this.methodData, getName());
  }


  
  public String getDefinitionString() {
    return getSignature();
  }





  
  private String getMethodSummary() {
    Method method = this.methodData.getMethod();
    String summary = (method != null) ? method.getDocComment() : null;
    
    if (summary != null && summary.startsWith("/**")) {
      summary = Util.docCommentToHtml(summary);
    }
    
    return (summary != null) ? summary : getNameAndParameters();
  }

  
  private String getNameAndParameters() {
    if (this.nameAndParameters == null) {
      this.nameAndParameters = formatMethodAtString(getName(), this.methodData);
    }
    return this.nameAndParameters;
  }

  
  private static String formatMethodAtString(String name, JSMethodData method) {
    StringBuilder sb = new StringBuilder(name);
    sb.append('(');
    int count = method.getParameterCount();
    for (int i = 0; i < count; i++) {
      sb.append(method.getParameterName(i));
      if (i < count - 1) {
        sb.append(", ");
      }
    } 
    sb.append(')');
    return sb.toString();
  }

  
  public String getSignature() {
    return getNameAndParameters();
  }


  
  public String getSummary() {
    String summary = getMethodSummary();

    
    if (summary != null && summary.startsWith("/**")) {
      summary = Util.docCommentToHtml(summary);
    }
    
    return summary;
  }


  
  public int hashCode() {
    return getCompareString().hashCode();
  }





  
  public String toString() {
    return getSignature();
  }


  
  public String getType() {
    String value = getType(true);
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(value, false);
  }


  
  public String getType(boolean qualified) {
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(this.methodData
        .getType(qualified), qualified);
  }


  
  public Icon getIcon() {
    return this.methodData.isStatic() ? 
      IconFactory.getIcon("public_static_function") : 
      IconFactory.getIcon("default_function");
  }


  
  public int getRelevance() {
    return 4;
  }


  
  public String getEnclosingClassName(boolean fullyQualified) {
    return this.methodData.getEnclosingClassName(fullyQualified);
  }

  
  public JSMethodData getMethodData() {
    return this.methodData;
  }


  
  public static class JSFunctionParam
    extends ParameterizedCompletion.Parameter
  {
    private boolean showParameterType;

    
    private CompletionProvider provider;

    
    public JSFunctionParam(Object type, String name, boolean showParameterType, CompletionProvider provider) {
      super(type, name);
      this.showParameterType = showParameterType;
      this.provider = provider;
    }


    
    public String getType() {
      return this.showParameterType ? ((SourceCompletionProvider)this.provider).getTypesFactory()
        .convertJavaScriptType(super.getType(), false) : null;
    }
  }
}
