package org.fife.rsta.ac.groovy;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;





















public class GroovyCompletionProvider
  extends LanguageAwareCompletionProvider
{
  public GroovyCompletionProvider() {
    setDefaultCompletionProvider(createCodeCompletionProvider());
    setStringCompletionProvider(createStringCompletionProvider());
    setCommentCompletionProvider(createCommentCompletionProvider());
  }








  
  protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {}








  
  protected CompletionProvider createCodeCompletionProvider() {
    return (CompletionProvider)new GroovySourceCompletionProvider();
  }









  
  protected CompletionProvider createCommentCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "TODO:", "A to-do reminder"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "FIXME:", "A bug that needs to be fixed"));
    return (CompletionProvider)cp;
  }








  
  protected CompletionProvider createStringCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "%c", "char", "Prints a character"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "%i", "signed int", "Prints a signed integer"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "%f", "float", "Prints a float"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "%s", "string", "Prints a string"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "%u", "unsigned int", "Prints an unsigned integer"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "\\n", "Newline", "Prints a newline"));
    return (CompletionProvider)cp;
  }
}
