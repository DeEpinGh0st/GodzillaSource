package org.fife.rsta.ac.js;

import javax.swing.Icon;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;




















public class JsDocCompletionProvider
  extends DefaultCompletionProvider
{
  public JsDocCompletionProvider() {
    String[] simpleTags = { "abstract", "access", "alias", "augments", "author", "borrows", "callback", "classdesc", "constant", "constructor", "constructs", "copyright", "default", "deprecated", "desc", "enum", "event", "example", "exports", "external", "file", "fires", "global", "ignore", "inner", "instance", "kind", "lends", "license", "member", "memberof", "method", "mixes", "mixin", "module", "name", "namespace", "private", "property", "protected", "public", "readonly", "requires", "see", "since", "static", "summary", "this", "throws", "todo", "type", "typedef", "variation", "version" };










    
    for (int i = 0; i < simpleTags.length; i++) {
      addCompletion((Completion)new JsDocCompletion((CompletionProvider)this, "@" + simpleTags[i]));
    }

    
    addCompletion((Completion)new JsDocParameterizedCompletion((CompletionProvider)this, "@param", "@param {type} varName", "@param {${}} ${varName} ${cursor}"));
    
    addCompletion((Completion)new JsDocParameterizedCompletion((CompletionProvider)this, "@return", "@return {type} description", "@return {${type}} ${}"));
    
    addCompletion((Completion)new JsDocParameterizedCompletion((CompletionProvider)this, "@returns", "@returns {type} description", "@returns {${type}} ${}"));


    
    addCompletion((Completion)new JsDocParameterizedCompletion((CompletionProvider)this, "{@link}", "{@link}", "{@link ${}}${cursor}"));
    addCompletion((Completion)new JsDocParameterizedCompletion((CompletionProvider)this, "{@linkplain}", "{@linkplain}", "{@linkplain ${}}${cursor}"));
    addCompletion((Completion)new JsDocParameterizedCompletion((CompletionProvider)this, "{@linkcode}", "{@linkcode}", "{@linkcode ${}}${cursor}"));
    addCompletion((Completion)new JsDocParameterizedCompletion((CompletionProvider)this, "{@tutorial}", "{@tutorial}", "{@tutorial ${tutorialID}}${cursor}"));

    
    addCompletion((Completion)new JsDocCompletion((CompletionProvider)this, "null", "<code>null</code>", "&lt;code>null&lt;/code>", "template"));
    addCompletion((Completion)new JsDocCompletion((CompletionProvider)this, "true", "<code>true</code>", "&lt;code>true&lt;/code>", "template"));
    addCompletion((Completion)new JsDocCompletion((CompletionProvider)this, "false", "<code>false</code>", "&lt;code>false&lt;/code>", "template"));
    
    setAutoActivationRules(false, "{@");
  }






  
  protected boolean isValidChar(char ch) {
    return (Character.isLetterOrDigit(ch) || ch == '_' || ch == '@' || ch == '{' || ch == '}');
  }


  
  private static class JsDocCompletion
    extends BasicCompletion
  {
    private String inputText;

    
    private String icon;

    
    public JsDocCompletion(CompletionProvider provider, String replacementText) {
      super(provider, replacementText);
      this.inputText = replacementText;
      this.icon = "jsdoc_item";
    }


    
    public JsDocCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc, String icon) {
      super(provider, replacementText, shortDesc, shortDesc);
      this.inputText = inputText;
      this.icon = icon;
    }

    
    public Icon getIcon() {
      return IconFactory.getIcon(this.icon);
    }

    
    public String getInputText() {
      return this.inputText;
    }
  }

  
  private static class JsDocParameterizedCompletion
    extends TemplateCompletion
  {
    private String icon;

    
    public JsDocParameterizedCompletion(CompletionProvider provider, String inputText, String definitionString, String template) {
      this(provider, inputText, definitionString, template, "jsdoc_item");
    }



    
    public JsDocParameterizedCompletion(CompletionProvider provider, String inputText, String definitionString, String template, String icon) {
      super(provider, inputText, definitionString, template);
      setIcon(icon);
    }

    
    public Icon getIcon() {
      return IconFactory.getIcon(this.icon);
    }
    
    public void setIcon(String icon) {
      this.icon = icon;
    }
  }
}
