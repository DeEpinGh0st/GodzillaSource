package org.fife.rsta.ac.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fife.rsta.ac.html.AttributeCompletion;
import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;





















public class JspCompletionProvider
  extends HtmlCompletionProvider
{
  private Map<String, TldFile> prefixToTld;
  
  public JspCompletionProvider() {
    this.prefixToTld = new HashMap<>();














    
    setAutoActivationRules(false, "<:");
  }









  
  protected List<AttributeCompletion> getAttributeCompletionsForTag(String tagName) {
    List<AttributeCompletion> list = super.getAttributeCompletionsForTag(tagName);
    
    if (list == null) {
      
      int colon = tagName.indexOf(':');
      if (colon > -1) {
        
        String prefix = tagName.substring(0, colon);
        tagName = tagName.substring(colon + 1);
        
        TldFile tldFile = this.prefixToTld.get(prefix);
        if (tldFile != null) {
          List<ParameterizedCompletion.Parameter> attrs = tldFile.getAttributesForTag(tagName);
          if (attrs != null && attrs.size() > -1) {
            list = new ArrayList<>();
            for (ParameterizedCompletion.Parameter param : attrs) {
              list.add(new AttributeCompletion((CompletionProvider)this, param));
            }
          } 
        } 
      } 
    } 


    
    return list;
  }











  
  protected List<Completion> getTagCompletions() {
    List<Completion> completions = new ArrayList<>(super.getTagCompletions());
    
    for (Map.Entry<String, TldFile> entry : this.prefixToTld.entrySet()) {
      String prefix = entry.getKey();
      TldFile tld = entry.getValue();
      for (int j = 0; j < tld.getElementCount(); j++) {
        TldElement elem = tld.getElement(j);
        
        MarkupTagCompletion mtc = new MarkupTagCompletion((CompletionProvider)this, prefix + ":" + elem.getName());
        mtc.setDescription(elem.getDescription());
        completions.add(mtc);
      } 
    } 
    
    Collections.sort(completions);
    return completions;
  }








  
  protected void initCompletions() {
    super.initCompletions();

    
    try {
      loadFromXML("data/jsp.xml");
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } 


    
    this.completions.sort((Comparator)this.comparator);
  }



  
  protected boolean isValidChar(char ch) {
    return (super.isValidChar(ch) || ch == ':');
  }
}
