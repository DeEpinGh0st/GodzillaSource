package org.fife.rsta.ac.css;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;




















public class CssCompletionProvider
  extends LanguageAwareCompletionProvider
{
  public CssCompletionProvider() {
    setDefaultCompletionProvider(createCodeCompletionProvider());
    setCommentCompletionProvider(createCommentCompletionProvider());
  }







  
  protected CompletionProvider createCodeCompletionProvider() {
    return (CompletionProvider)new PropertyValueCompletionProvider(false);
  }







  
  protected CompletionProvider createCommentCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "TODO:", "A to-do reminder"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "FIXME:", "A bug that needs to be fixed"));
    return (CompletionProvider)cp;
  }
}
