package org.fife.rsta.ac.html;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;


















public class AttributeCompletion
  extends AbstractCompletion
{
  private ParameterizedCompletion.Parameter param;
  
  public AttributeCompletion(CompletionProvider provider, ParameterizedCompletion.Parameter param) {
    super(provider);
    this.param = param;
  }


  
  public String getSummary() {
    return this.param.getDescription();
  }


  
  public String getReplacementText() {
    return this.param.getName();
  }
}
