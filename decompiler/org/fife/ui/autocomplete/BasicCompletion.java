package org.fife.ui.autocomplete;




























public class BasicCompletion
  extends AbstractCompletion
{
  private String replacementText;
  private String shortDesc;
  private String summary;
  
  public BasicCompletion(CompletionProvider provider, String replacementText) {
    this(provider, replacementText, null);
  }










  
  public BasicCompletion(CompletionProvider provider, String replacementText, String shortDesc) {
    this(provider, replacementText, shortDesc, null);
  }












  
  public BasicCompletion(CompletionProvider provider, String replacementText, String shortDesc, String summary) {
    super(provider);
    this.replacementText = replacementText;
    this.shortDesc = shortDesc;
    this.summary = summary;
  }





  
  public String getReplacementText() {
    return this.replacementText;
  }








  
  public String getShortDescription() {
    return this.shortDesc;
  }





  
  public String getSummary() {
    return this.summary;
  }







  
  public void setShortDescription(String shortDesc) {
    this.shortDesc = shortDesc;
  }







  
  public void setSummary(String summary) {
    this.summary = summary;
  }












  
  public String toString() {
    if (this.shortDesc == null) {
      return getInputText();
    }
    return getInputText() + " - " + this.shortDesc;
  }
}
