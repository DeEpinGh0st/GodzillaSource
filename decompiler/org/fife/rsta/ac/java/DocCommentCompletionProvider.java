package org.fife.rsta.ac.java;

import java.awt.Graphics;
import javax.swing.Icon;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;





















class DocCommentCompletionProvider
  extends DefaultCompletionProvider
{
  public DocCommentCompletionProvider() {
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@author"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@deprecated"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@exception"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@param"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@return"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@see"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@serial"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@serialData"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@serialField"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@since"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@throws"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@version"));

    
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@category"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@example"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@tutorial"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@index"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@exclude"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@todo"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@internal"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@obsolete"));
    addCompletion(new JavadocCompletion((CompletionProvider)this, "@threadsafety"));

    
    addCompletion(new JavadocTemplateCompletion((CompletionProvider)this, "{@code}", "{@code}", "{@code ${}}${cursor}"));
    addCompletion(new JavadocTemplateCompletion((CompletionProvider)this, "{@docRoot}", "{@docRoot}", "{@docRoot ${}}${cursor}"));
    addCompletion(new JavadocTemplateCompletion((CompletionProvider)this, "{@inheritDoc}", "{@inheritDoc}", "{@inheritDoc ${}}${cursor}"));
    addCompletion(new JavadocTemplateCompletion((CompletionProvider)this, "{@link}", "{@link}", "{@link ${}}${cursor}"));
    addCompletion(new JavadocTemplateCompletion((CompletionProvider)this, "{@linkplain}", "{@linkplain}", "{@linkplain ${}}${cursor}"));
    addCompletion(new JavadocTemplateCompletion((CompletionProvider)this, "{@literal}", "{@literal}", "{@literal ${}}${cursor}"));
    addCompletion(new JavadocTemplateCompletion((CompletionProvider)this, "{@value}", "{@value}", "{@value ${}}${cursor}"));

    
    addCompletion(new JavaShorthandCompletion((CompletionProvider)this, "null", "<code>null</code>", "<code>null</code>"));
    addCompletion(new JavaShorthandCompletion((CompletionProvider)this, "true", "<code>true</code>", "<code>true</code>"));
    addCompletion(new JavaShorthandCompletion((CompletionProvider)this, "false", "<code>false</code>", "<code>false</code>"));
    
    setAutoActivationRules(false, "{@");
  }






  
  protected boolean isValidChar(char ch) {
    return (Character.isLetterOrDigit(ch) || ch == '_' || ch == '@' || ch == '{' || ch == '}');
  }





  
  private static class JavadocCompletion
    extends BasicCompletion
    implements JavaSourceCompletion
  {
    public JavadocCompletion(CompletionProvider provider, String replacementText) {
      super(provider, replacementText);
    }

    
    public Icon getIcon() {
      return IconFactory.get().getIcon("javadocItemIcon");
    }

    
    public void rendererText(Graphics g, int x, int y, boolean selected) {
      g.drawString(getReplacementText(), x, y);
    }
  }



  
  private static class JavadocTemplateCompletion
    extends JavaTemplateCompletion
  {
    public JavadocTemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template) {
      super(provider, inputText, definitionString, template);
      setIcon("javadocItemIcon");
    }
  }
}
