package org.fife.ui.autocomplete;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;













































public abstract class AbstractCompletion
  implements Completion
{
  private CompletionProvider provider;
  private Icon icon;
  private int relevance;
  
  protected AbstractCompletion(CompletionProvider provider) {
    this.provider = provider;
  }







  
  protected AbstractCompletion(CompletionProvider provider, Icon icon) {
    this(provider);
    setIcon(icon);
  }





  
  public int compareTo(Completion c2) {
    if (c2 == this) {
      return 0;
    }
    if (c2 != null) {
      return toString().compareToIgnoreCase(c2.toString());
    }
    return -1;
  }





  
  public String getAlreadyEntered(JTextComponent comp) {
    return this.provider.getAlreadyEnteredText(comp);
  }





  
  public Icon getIcon() {
    return this.icon;
  }










  
  public String getInputText() {
    return getReplacementText();
  }





  
  public CompletionProvider getProvider() {
    return this.provider;
  }





  
  public int getRelevance() {
    return this.relevance;
  }








  
  public String getToolTipText() {
    return null;
  }







  
  public void setIcon(Icon icon) {
    this.icon = icon;
  }







  
  public void setRelevance(int relevance) {
    this.relevance = relevance;
  }








  
  public String toString() {
    return getInputText();
  }
}
