package org.fife.rsta.ui.search;

import java.awt.Image;
import javax.swing.ComboBoxModel;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ui.ContentAssistable;
import org.fife.rsta.ui.MaxWidthComboBox;
import org.fife.rsta.ui.RComboBoxModel;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

























public class RegexAwareComboBox<E>
  extends MaxWidthComboBox<E>
  implements ContentAssistable
{
  private boolean enabled;
  private boolean replace;
  private AutoCompletion ac;
  private RegexAwareProvider provider;
  private Image contentAssistImage;
  
  public RegexAwareComboBox(boolean replace) {
    this((ComboBoxModel<E>)new RComboBoxModel(), 200, replace);
  }











  
  public RegexAwareComboBox(ComboBoxModel<E> model, int maxWidth, boolean replace) {
    super(model, maxWidth);
    setEditable(true);
    this.replace = replace;
  }









  
  private void addFindFieldCompletions(RegexAwareProvider p) {
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\\\", "\\\\", "\\\\ - Backslash"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\t", "\\t", "\\t - Tab"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\n", "\\n", "\\n - Newline"));

    
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "[", "[", "[abc] - Any of a, b, or c"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "[^", "[^", "[^abc] - Any character except a, b, or c"));

    
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, ".", ".", ". - Any character"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\d", "\\d", "\\d - A digit"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\D", "\\D", "\\D - Not a digit"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\s", "\\s", "\\s - A whitespace"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\S", "\\S", "\\S - Not a whitespace"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\w", "\\w", "\\w - An alphanumeric (word character)"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\W", "\\W", "\\W - Not an alphanumeric"));

    
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "^", "^", "^ - Line Start"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "$", "$", "$ - Line End"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\b", "\b", "\\b - Word beginning or end"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\B", "\\B", "\\B - Not a word beginning or end"));

    
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "?", "?", "X? - Greedy match, 0 or 1 times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "*", "*", "X* - Greedy match, 0 or more times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "+", "+", "X+ - Greedy match, 1 or more times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "{", "{", "X{n} - Greedy match, exactly n times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "{", "{", "X{n,} - Greedy match, at least n times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "{", "{", "X{n,m} - Greedy match, at least n but no more than m times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "??", "??", "X?? - Lazy match, 0 or 1 times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "*?", "*?", "X*? - Lazy match, 0 or more times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "+?", "+?", "X+? - Lazy match, 1 or more times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "?+", "?+", "X?+ - Possessive match, 0 or 1 times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "*+", "*+", "X*+ - Possessive match, 0 or more times"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "++", "++", "X++ - Possessive match, 0 or more times"));

    
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\i", "\\i", "\\i - Match of the capturing group i"));

    
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "(", "(", "(Expr) - Mark Expr as capturing group"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "(?:", "(?:", "(?:Expr) - Non-capturing group"));
  }








  
  private void addReplaceFieldCompletions(RegexAwareProvider p) {
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "$", "$", "$i - Match of the capturing group i"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\", "\\", "\\ - Quote next character"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\t", "\\t", "\\t - Tab"));
    p.addCompletion((Completion)new RegexCompletion((CompletionProvider)p, "\\n", "\\n", "\\n - Newline"));
  }






  
  private AutoCompletion getAutoCompletion() {
    if (this.ac == null) {
      this.ac = new AutoCompletion(getCompletionProvider());
    }
    return this.ac;
  }






  
  protected synchronized CompletionProvider getCompletionProvider() {
    if (this.provider == null) {
      this.provider = new RegexAwareProvider();
      if (this.replace) {
        addReplaceFieldCompletions(this.provider);
      } else {
        
        addFindFieldCompletions(this.provider);
      } 
    } 
    return (CompletionProvider)this.provider;
  }








  
  public Image getContentAssistImage() {
    if (this.contentAssistImage != null) {
      return this.contentAssistImage;
    }
    return AbstractSearchDialog.getContentAssistImage();
  }






  
  public boolean hideAutoCompletePopups() {
    return (this.ac != null && this.ac.hideChildWindows());
  }







  
  public boolean isAutoCompleteEnabled() {
    return this.enabled;
  }









  
  public void setAutoCompleteEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      this.enabled = enabled;
      if (enabled) {
        AutoCompletion ac = getAutoCompletion();
        
        JTextComponent tc = (JTextComponent)getEditor().getEditorComponent();
        ac.install(tc);
      } else {
        
        this.ac.uninstall();
      } 
      String prop = "AssistanceImage";


      
      if (enabled) {
        firePropertyChange(prop, null, getContentAssistImage());
      } else {
        
        firePropertyChange(prop, null, null);
      } 
    } 
  }










  
  public void setContentAssistImage(Image image) {
    this.contentAssistImage = image;
  }

  
  private static class RegexAwareProvider
    extends DefaultCompletionProvider
  {
    private RegexAwareProvider() {}

    
    protected boolean isValidChar(char ch) {
      switch (ch) {
        case '$':
        case '(':
        case '*':
        case '+':
        case '.':
        case ':':
        case '?':
        case '[':
        case '\\':
        case '^':
        case '{':
          return true;
      } 
      return false;
    }
  }








  
  private static class RegexCompletion
    extends BasicCompletion
  {
    private String inputText;







    
    RegexCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc) {
      super(provider, replacementText, shortDesc);
      this.inputText = inputText;
    }

    
    public String getInputText() {
      return this.inputText;
    }

    
    public String toString() {
      return getShortDescription();
    }
  }
}
