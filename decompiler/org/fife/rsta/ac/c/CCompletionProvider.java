package org.fife.rsta.ac.c;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;























public class CCompletionProvider
  extends LanguageAwareCompletionProvider
{
  public CCompletionProvider() {
    setDefaultCompletionProvider(createCodeCompletionProvider());
    setStringCompletionProvider(createStringCompletionProvider());
    setCommentCompletionProvider(createCommentCompletionProvider());
  }






  
  protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {
    codeCP.addCompletion((Completion)new ShorthandCompletion((CompletionProvider)codeCP, "main", "int main(int argc, char **argv)"));
    
    codeCP.setAutoActivationRules(true, null);
  }













  
  protected CompletionProvider createCodeCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    loadCodeCompletionsFromXml(cp);
    addShorthandCompletions(cp);
    cp.setAutoActivationRules(true, null);
    return (CompletionProvider)cp;
  }









  
  protected CompletionProvider createCommentCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "TODO:", "A to-do reminder"));
    cp.addCompletion((Completion)new BasicCompletion((CompletionProvider)cp, "FIXME:", "A bug that needs to be fixed"));
    cp.setAutoActivationRules(true, null);
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






  
  protected String getXmlResource() {
    return "data/c.xml";
  }










  
  protected void loadCodeCompletionsFromXml(DefaultCompletionProvider cp) {
    ClassLoader cl = getClass().getClassLoader();
    String res = getXmlResource();
    if (res != null) {
      InputStream in = cl.getResourceAsStream(res);
      try {
        if (in != null) {
          cp.loadFromXML(in);
          in.close();
        } else {
          
          cp.loadFromXML(new File(res));
        } 
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } 
    } 
  }
}
