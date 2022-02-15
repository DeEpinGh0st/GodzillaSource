package org.fife.ui.autocomplete;































public class ShorthandCompletion
  extends BasicCompletion
{
  private String inputText;
  
  public ShorthandCompletion(CompletionProvider provider, String inputText, String replacementText) {
    super(provider, replacementText);
    this.inputText = inputText;
  }











  
  public ShorthandCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc) {
    super(provider, replacementText, shortDesc);
    this.inputText = inputText;
  }













  
  public ShorthandCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc, String summary) {
    super(provider, replacementText, shortDesc, summary);
    this.inputText = inputText;
  }







  
  public String getInputText() {
    return this.inputText;
  }










  
  public String getSummary() {
    String summary = super.getSummary();
    return (summary != null) ? summary : ("<html><body>" + getSummaryBody());
  }









  
  protected String getSummaryBody() {
    return "<code>" + getReplacementText();
  }
}
