package org.fife.rsta.ac.css;

import javax.swing.Icon;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;





















class BasicCssCompletion
  extends BasicCompletion
{
  private String iconKey;
  
  public BasicCssCompletion(CompletionProvider provider, String value, String iconKey) {
    super(provider, value);
    this.iconKey = iconKey;
  }


  
  public Icon getIcon() {
    return IconFactory.get().getIcon(this.iconKey);
  }
}
