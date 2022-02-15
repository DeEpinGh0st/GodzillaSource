package org.fife.rsta.ac.less;

import org.fife.rsta.ac.css.CssCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
























class LessCompletionProvider
  extends CssCompletionProvider
{
  protected CompletionProvider createCodeCompletionProvider() {
    return (CompletionProvider)new LessCodeCompletionProvider();
  }
}
