package org.fife.rsta.ac.java;

import java.awt.Graphics;
import javax.swing.Icon;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;




















public class JavaTemplateCompletion
  extends TemplateCompletion
  implements JavaSourceCompletion
{
  private String icon;
  
  public JavaTemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template) {
    this(provider, inputText, definitionString, template, (String)null);
  }



  
  public JavaTemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template, String shortDesc) {
    this(provider, inputText, definitionString, template, shortDesc, (String)null);
  }



  
  public JavaTemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template, String shortDesc, String summary) {
    super(provider, inputText, definitionString, template, shortDesc, summary);
    setIcon("templateIcon");
  }


  
  public Icon getIcon() {
    return IconFactory.get().getIcon(this.icon);
  }


  
  public void rendererText(Graphics g, int x, int y, boolean selected) {
    JavaShorthandCompletion.renderText(g, getInputText(), 
        getShortDescription(), x, y, selected);
  }

  
  public void setIcon(String iconId) {
    this.icon = iconId;
  }
}
