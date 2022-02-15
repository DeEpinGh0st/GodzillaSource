package org.fife.rsta.ac.css;

import javax.swing.Icon;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;




















class PropertyCompletion
  extends ShorthandCompletion
{
  private String iconKey;
  
  public PropertyCompletion(CompletionProvider provider, String property, String iconKey) {
    super(provider, property, property + ": ");
    this.iconKey = iconKey;
  }


  
  public Icon getIcon() {
    return IconFactory.get().getIcon(this.iconKey);
  }
}
