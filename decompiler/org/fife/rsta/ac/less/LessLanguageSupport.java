package org.fife.rsta.ac.less;

import org.fife.rsta.ac.css.CssCompletionProvider;
import org.fife.rsta.ac.css.CssLanguageSupport;





















public class LessLanguageSupport
  extends CssLanguageSupport
{
  public LessLanguageSupport() {
    setShowDescWindow(true);
  }







  
  protected CssCompletionProvider createProvider() {
    return new LessCompletionProvider();
  }
}
