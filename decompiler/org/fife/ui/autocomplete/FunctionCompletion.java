package org.fife.ui.autocomplete;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;








































public class FunctionCompletion
  extends VariableCompletion
  implements ParameterizedCompletion
{
  private List<ParameterizedCompletion.Parameter> params;
  private String returnValDesc;
  private String compareString;
  
  public FunctionCompletion(CompletionProvider provider, String name, String returnType) {
    super(provider, name, returnType);
  }


  
  protected void addDefinitionString(StringBuilder sb) {
    sb.append("<html><b>");
    sb.append(getDefinitionString());
    sb.append("</b>");
  }









  
  protected void addParameters(StringBuilder sb) {
    int paramCount = getParamCount();
    if (paramCount > 0) {
      sb.append("<b>Parameters:</b><br>");
      sb.append("<center><table width='90%'><tr><td>");
      for (int i = 0; i < paramCount; i++) {
        ParameterizedCompletion.Parameter param = getParam(i);
        sb.append("<b>");
        sb.append((param.getName() != null) ? param.getName() : param
            .getType());
        sb.append("</b>&nbsp;");
        String desc = param.getDescription();
        if (desc != null) {
          sb.append(desc);
        }
        sb.append("<br>");
      } 
      sb.append("</td></tr></table></center><br><br>");
    } 
    
    if (this.returnValDesc != null) {
      sb.append("<b>Returns:</b><br><center><table width='90%'><tr><td>");
      sb.append(this.returnValDesc);
      sb.append("</td></tr></table></center><br><br>");
    } 
  }











  
  public int compareTo(Completion c2) {
    int rc;
    if (c2 == this) {
      rc = 0;
    
    }
    else if (c2 instanceof FunctionCompletion) {
      rc = getCompareString().compareTo(((FunctionCompletion)c2)
          .getCompareString());
    }
    else {
      
      rc = super.compareTo(c2);
    } 
    
    return rc;
  }









  
  public boolean equals(Object other) {
    return (other instanceof Completion && compareTo((Completion)other) == 0);
  }















  
  private String getCompareString() {
    if (this.compareString == null) {
      StringBuilder sb = new StringBuilder(getName());
      
      int paramCount = getParamCount();
      if (paramCount < 10) {
        sb.append('0');
      }
      sb.append(paramCount);
      for (int i = 0; i < paramCount; i++) {
        String type = getParam(i).getType();
        sb.append(type);
        if (i < paramCount - 1) {
          sb.append(',');
        }
      } 
      this.compareString = sb.toString();
    } 
    
    return this.compareString;
  }











  
  public String getDefinitionString() {
    StringBuilder sb = new StringBuilder();

    
    String type = getType();
    if (type != null) {
      sb.append(type).append(' ');
    }

    
    sb.append(getName());

    
    CompletionProvider provider = getProvider();
    char start = provider.getParameterListStart();
    if (start != '\000') {
      sb.append(start);
    }
    for (int i = 0; i < getParamCount(); i++) {
      ParameterizedCompletion.Parameter param = getParam(i);
      type = param.getType();
      String name = param.getName();
      if (type != null) {
        sb.append(type);
        if (name != null) {
          sb.append(' ');
        }
      } 
      if (name != null) {
        sb.append(name);
      }
      if (i < this.params.size() - 1) {
        sb.append(provider.getParameterListSeparator());
      }
    } 
    char end = provider.getParameterListEnd();
    if (end != '\000') {
      sb.append(end);
    }
    
    return sb.toString();
  }





  
  public ParameterizedCompletionInsertionInfo getInsertionInfo(JTextComponent tc, boolean replaceTabsWithSpaces) {
    ParameterizedCompletionInsertionInfo info = new ParameterizedCompletionInsertionInfo();

    
    StringBuilder sb = new StringBuilder();
    char paramListStart = getProvider().getParameterListStart();
    if (paramListStart != '\000') {
      sb.append(paramListStart);
    }
    int dot = tc.getCaretPosition() + sb.length();
    int paramCount = getParamCount();


    
    int minPos = dot;
    Position maxPos = null;
    try {
      maxPos = tc.getDocument().createPosition(dot - sb.length() + 1);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    info.setCaretRange(minPos, maxPos);
    int firstParamLen = 0;


    
    int start = dot;
    for (int i = 0; i < paramCount; i++) {
      ParameterizedCompletion.Parameter param = getParam(i);
      String paramText = getParamText(param);
      if (i == 0) {
        firstParamLen = paramText.length();
      }
      sb.append(paramText);
      int end = start + paramText.length();
      info.addReplacementLocation(start, end);

      
      String sep = getProvider().getParameterListSeparator();
      if (i < paramCount - 1 && sep != null) {
        sb.append(sep);
        start = end + sep.length();
      } 
    } 
    sb.append(getProvider().getParameterListEnd());
    int endOffs = dot + sb.length();
    endOffs--;
    info.addReplacementLocation(endOffs, endOffs);
    info.setDefaultEndOffs(endOffs);
    
    int selectionEnd = (paramCount > 0) ? (dot + firstParamLen) : dot;
    info.setInitialSelection(dot, selectionEnd);
    info.setTextToInsert(sb.toString());
    return info;
  }






  
  public ParameterizedCompletion.Parameter getParam(int index) {
    return this.params.get(index);
  }








  
  public int getParamCount() {
    return (this.params == null) ? 0 : this.params.size();
  }





  
  public boolean getShowParameterToolTip() {
    return true;
  }







  
  private String getParamText(ParameterizedCompletion.Parameter param) {
    String text = param.getName();
    if (text == null) {
      text = param.getType();
      if (text == null) {
        text = "arg";
      }
    } 
    return text;
  }







  
  public String getReturnValueDescription() {
    return this.returnValDesc;
  }





  
  public String getSummary() {
    StringBuilder sb = new StringBuilder();
    addDefinitionString(sb);
    if (!possiblyAddDescription(sb)) {
      sb.append("<br><br><br>");
    }
    addParameters(sb);
    possiblyAddDefinedIn(sb);
    return sb.toString();
  }






  
  @SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"}, justification = "Subclasses could return null")
  public String getToolTipText() {
    String text = getSummary();
    if (text == null) {
      text = getDefinitionString();
    }
    return text;
  }


  
  public int hashCode() {
    int hashCode = super.hashCode();
    
    for (int i = 0; i < getParamCount(); i++) {
      hashCode ^= getParam(i).hashCode();
    }
    
    hashCode ^= (this.returnValDesc != null) ? this.returnValDesc.hashCode() : 0;
    hashCode ^= (this.compareString != null) ? this.compareString.hashCode() : 0;
    
    return hashCode;
  }









  
  public void setParams(List<ParameterizedCompletion.Parameter> params) {
    if (params != null)
    {
      this.params = new ArrayList<>(params);
    }
  }







  
  public void setReturnValueDescription(String desc) {
    this.returnValDesc = desc;
  }
}
