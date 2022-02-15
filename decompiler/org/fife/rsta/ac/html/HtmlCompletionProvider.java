package org.fife.rsta.ac.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;





































public class HtmlCompletionProvider
  extends DefaultCompletionProvider
{
  private Map<String, List<AttributeCompletion>> tagToAttrs;
  private boolean isTagName;
  private String lastTagName;
  
  public HtmlCompletionProvider() {
    initCompletions();
    
    this.tagToAttrs = new HashMap<>();
    for (Completion comp : this.completions) {
      MarkupTagCompletion c = (MarkupTagCompletion)comp;
      String tag = c.getName();
      List<AttributeCompletion> attrs = new ArrayList<>();
      this.tagToAttrs.put(tag.toLowerCase(), attrs);
      for (int j = 0; j < c.getAttributeCount(); j++) {
        ParameterizedCompletion.Parameter param = c.getAttribute(j);
        attrs.add(new AttributeCompletion((CompletionProvider)this, param));
      } 
    } 
    
    setAutoActivationRules(false, "<");
  }










  
  protected String defaultGetAlreadyEnteredText(JTextComponent comp) {
    return super.getAlreadyEnteredText(comp);
  }













  
  private boolean findLastTagNameBefore(RSyntaxDocument doc, Token tokenList, int offs) {
    this.lastTagName = null;
    boolean foundOpenTag = false;
    
    for (Token t = tokenList; t != null && 
      !t.containsPosition(offs); t = t.getNextToken()) {

      
      if (t.getType() == 26) {
        this.lastTagName = t.getLexeme();
      }
      else if (t.getType() == 25) {
        this.lastTagName = null;
        foundOpenTag = t.isSingleChar('<');
        t = t.getNextToken();

        
        if (t != null && !t.isWhitespace()) {
          this.lastTagName = t.getLexeme();
        }
      } 
    } 
    
    if (this.lastTagName == null && !foundOpenTag) {
      
      Element root = doc.getDefaultRootElement();
      int prevLine = root.getElementIndex(offs) - 1;
      while (prevLine >= 0) {
        tokenList = doc.getTokenListForLine(prevLine);
        for (Token token = tokenList; token != null; token = token.getNextToken()) {
          if (token.getType() == 26) {
            this.lastTagName = token.getLexeme();
          }
          else if (token.getType() == 25) {
            this.lastTagName = null;
            foundOpenTag = token.isSingleChar('<');
            token = token.getNextToken();

            
            if (token != null && !token.isWhitespace()) {
              this.lastTagName = token.getLexeme();
            }
          } 
        } 
        if (this.lastTagName != null || foundOpenTag) {
          break;
        }
        prevLine--;
      } 
    } 

    
    return (this.lastTagName != null);
  }







  
  public String getAlreadyEnteredText(JTextComponent comp) {
    this.isTagName = true;
    this.lastTagName = null;
    
    String text = super.getAlreadyEnteredText(comp);
    if (text != null) {

      
      int dot = comp.getCaretPosition();
      if (dot > 0) {
        
        RSyntaxTextArea textArea = (RSyntaxTextArea)comp;

        
        try {
          int line = textArea.getLineOfOffset(dot - 1);
          Token list = textArea.getTokenListForLine(line);
          
          if (list != null) {
            
            Token t = RSyntaxUtilities.getTokenAtOffset(list, dot - 1);
            
            if (t == null) {
              text = null;



            
            }
            else if (t.getType() == 25) {
              if (!isTagOpeningToken(t)) {
                text = null;

              
              }
            
            }
            else if (t.getType() == 21) {
              if (!insideMarkupTag(textArea, list, line, dot)) {
                text = null;

              
              }

            
            }
            else if (t.getType() != 27 && t
              .getType() != 26) {






              
              if (t.getType() > -1 || t.getType() < -9) {
                text = null;
              }
            } 

            
            if (text != null) {
              t = getTokenBeforeOffset(list, dot - text.length());
              this.isTagName = (t != null && isTagOpeningToken(t));
              if (!this.isTagName) {
                RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
                findLastTagNameBefore(doc, list, dot);
              }
            
            }
          
          } 
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        }
      
      }
      else {
        
        text = null;
      } 
    } 

    
    return text;
  }












  
  protected List<AttributeCompletion> getAttributeCompletionsForTag(String tagName) {
    return this.tagToAttrs.get(this.lastTagName);
  }






  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    List<Completion> retVal = new ArrayList<>();
    String text = getAlreadyEnteredText(comp);
    List<? extends Completion> completions = getTagCompletions();
    if (this.lastTagName != null) {
      this.lastTagName = this.lastTagName.toLowerCase();
      completions = (List)getAttributeCompletionsForTag(this.lastTagName);
    } 

    
    if (text != null && completions != null) {

      
      int index = Collections.binarySearch(completions, text, (Comparator<?>)this.comparator);
      if (index < 0) {
        index = -index - 1;
      }
      
      while (index < completions.size()) {
        Completion c = completions.get(index);
        if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
          retVal.add(c);
          index++;
        } 
      } 
    } 




    
    return retVal;
  }








  
  protected List<Completion> getTagCompletions() {
    return this.completions;
  }










  
  private static Token getTokenBeforeOffset(Token tokenList, int offs) {
    if (tokenList != null) {
      Token prev = tokenList;
      for (Token t = tokenList.getNextToken(); t != null; t = t.getNextToken()) {
        if (t.containsPosition(offs)) {
          return prev;
        }
        prev = t;
      } 
    } 
    return null;
  }






  
  protected void initCompletions() {
    try {
      loadFromXML("data/html.xml");
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } 
  }














  
  private static boolean insideMarkupTag(RSyntaxTextArea textArea, Token list, int line, int offs) {
    int inside = -1;
    
    for (Token t = list; t != null && 
      !t.containsPosition(offs); t = t.getNextToken()) {

      
      switch (t.getType()) {
        case 26:
        case 27:
          inside = 1;
          break;
        case 25:
          inside = t.isSingleChar('>') ? 0 : 1;
          break;
      } 

    
    } 
    if (inside == -1) {
      if (line == 0) {
        inside = 0;
      } else {
        
        RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
        int prevLastToken = doc.getLastTokenTypeOnLine(line - 1);



        
        if (prevLastToken <= -1 && prevLastToken >= -9) {
          inside = 1;
        } else {
          
          inside = 0;
        } 
      } 
    }
    
    return (inside == 1);
  }







  
  public boolean isAutoActivateOkay(JTextComponent tc) {
    boolean okay = super.isAutoActivateOkay(tc);
    
    if (okay) {
      
      RSyntaxTextArea textArea = (RSyntaxTextArea)tc;
      int dot = textArea.getCaretPosition();

      
      try {
        int line = textArea.getLineOfOffset(dot);
        Token list = textArea.getTokenListForLine(line);
        
        if (list != null) {
          return !insideMarkupTag(textArea, list, line, dot);
        }
      }
      catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } 

    
    return okay;
  }








  
  private static boolean isTagOpeningToken(Token t) {
    return (t.isSingleChar('<') || (t
      .length() == 2 && t.charAt(0) == '<' && t
      .charAt(1) == '/'));
  }
}
