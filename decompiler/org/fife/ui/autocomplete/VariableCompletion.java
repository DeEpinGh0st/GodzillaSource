package org.fife.ui.autocomplete;








































public class VariableCompletion
  extends BasicCompletion
{
  private String type;
  private String definedIn;
  
  public VariableCompletion(CompletionProvider provider, String name, String type) {
    super(provider, name);
    this.type = type;
  }

  
  protected void addDefinitionString(StringBuilder sb) {
    sb.append("<html><b>").append(getDefinitionString()).append("</b>");
  }


  
  public String getDefinitionString() {
    StringBuilder sb = new StringBuilder();

    
    if (this.type != null) {
      sb.append(this.type).append(' ');
    }

    
    sb.append(getName());
    
    return sb.toString();
  }








  
  public String getDefinedIn() {
    return this.definedIn;
  }






  
  public String getName() {
    return getReplacementText();
  }





  
  public String getSummary() {
    StringBuilder sb = new StringBuilder();
    addDefinitionString(sb);
    possiblyAddDescription(sb);
    possiblyAddDefinedIn(sb);
    return sb.toString();
  }


















  
  public String getToolTipText() {
    return getDefinitionString();
  }






  
  public String getType() {
    return this.type;
  }







  
  protected void possiblyAddDefinedIn(StringBuilder sb) {
    if (this.definedIn != null) {
      sb.append("<hr>Defined in:");
      sb.append(" <em>").append(this.definedIn).append("</em>");
    } 
  }








  
  protected boolean possiblyAddDescription(StringBuilder sb) {
    if (getShortDescription() != null) {
      sb.append("<hr><br>");
      sb.append(getShortDescription());
      sb.append("<br><br><br>");
      return true;
    } 
    return false;
  }







  
  public void setDefinedIn(String definedIn) {
    this.definedIn = definedIn;
  }







  
  public String toString() {
    return getName();
  }
}
