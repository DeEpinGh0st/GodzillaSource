package org.fife.rsta.ac.jsp;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.MarkupTagCompletion;

















class TldElement
  extends MarkupTagCompletion
{
  public TldElement(JspCompletionProvider provider, String name, String desc) {
    super((CompletionProvider)provider, name);
    setDescription(desc);
  }
}
