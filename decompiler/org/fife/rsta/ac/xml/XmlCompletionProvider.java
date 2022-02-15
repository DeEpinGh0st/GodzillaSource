package org.fife.rsta.ac.xml;

import java.awt.Component;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.html.AttributeCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;





























class XmlCompletionProvider
  extends DefaultCompletionProvider
{
  private static final char[] TAG_SELF_CLOSE = new char[] { '/', '>' };

  
  public XmlCompletionProvider() {
    setAutoActivationRules(false, "<");
  }








  
  private void addCompletionImpl(String word, int desiredType) {
    AttributeCompletion attributeCompletion;
    if (desiredType == 26) {
      MarkupTagCompletion markupTagCompletion = new MarkupTagCompletion((CompletionProvider)this, word);
    } else {
      
      ParameterizedCompletion.Parameter param = new ParameterizedCompletion.Parameter(null, word);
      
      attributeCompletion = new AttributeCompletion((CompletionProvider)this, param);
    } 
    this.completions.add(attributeCompletion);
  }













  
  private Set<String> collectCompletionWordsAttribute(RSyntaxDocument doc, Token inTag, int currentWordStart) {
    Set<String> possibleAttrs = new HashSet<>();
    Set<String> attrs = new HashSet<>();
    Set<String> attrsAlreadySpecified = new HashSet<>();
    String desiredTagName = inTag.getLexeme();
    boolean collectAttrs = false;
    boolean inCurTag = false;
    
    for (Token t2 : doc) {
      int type = t2.getType();
      if (type == 26) {
        collectAttrs = desiredTagName.equals(t2.getLexeme());
        inCurTag = (t2.getOffset() == inTag.getOffset());
        if (!attrs.isEmpty()) {
          possibleAttrs.addAll(attrs);
          attrs.clear();
        }  continue;
      } 
      if (type == 27 && collectAttrs && 
        t2.getOffset() != currentWordStart) {
        String word = t2.getLexeme();
        if (inCurTag) {
          if (word.indexOf('<') > -1) {
            collectAttrs = false;
            attrs.clear();

            
            continue;
          } 
          
          attrsAlreadySpecified.add(word);


          
          continue;
        } 


        
        if (word.indexOf('<') > -1) {
          
          collectAttrs = false;
          attrs.clear();
          attrsAlreadySpecified.clear();
          continue;
        } 
        attrs.add(word);
      } 
    } 



    
    if (!attrs.isEmpty()) {
      possibleAttrs.addAll(attrs);
    }
    possibleAttrs.removeAll(attrsAlreadySpecified);
    return possibleAttrs;
  }











  
  private Set<String> collectCompletionWordsTag(RSyntaxDocument doc, int currentWordStart) {
    Set<String> words = new HashSet<>();
    for (Token t2 : doc) {
      if (t2.getType() == 26 && t2
        .getOffset() != currentWordStart) {
        words.add(t2.getLexeme());
      }
    } 
    return words;
  }


  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    Set<String> words;
    this.completions.clear();
    
    String text = getAlreadyEnteredText(comp);
    if (text == null) {
      return this.completions;
    }
    
    RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
    int dot = textArea.getCaretPosition();
    RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
    Token t = RSyntaxUtilities.getPreviousImportantTokenFromOffs(doc, dot);
    if (t == null) {
      UIManager.getLookAndFeel().provideErrorFeedback((Component)textArea);
      return this.completions;
    } 
    
    int desiredType = getDesiredTokenType(t, dot);
    if (desiredType == 0) {
      UIManager.getLookAndFeel().provideErrorFeedback((Component)textArea);
      return this.completions;
    } 
    
    int currentWordStart = dot - text.length();
    
    if (desiredType == 26) {
      words = collectCompletionWordsTag(doc, currentWordStart);
    } else {
      
      Token tagNameToken = getTagNameTokenForCaretOffset(textArea);
      if (tagNameToken != null) {
        TokenImpl tokenImpl = new TokenImpl(tagNameToken);
        words = collectCompletionWordsAttribute(doc, (Token)tokenImpl, currentWordStart);
      }
      else {
        
        UIManager.getLookAndFeel().provideErrorFeedback((Component)textArea);
        return this.completions;
      } 
    } 
    
    for (String word : words) {
      addCompletionImpl(word, desiredType);
    }
    Collections.sort(this.completions);
    
    return super.getCompletionsImpl(comp);
  }











  
  private static final int getDesiredTokenType(Token t, int dot) {
    switch (t.getType()) {
      case 26:
        if (t.containsPosition(dot - 1)) {
          return t.getType();
        }
        return 27;
      case 27:
        return t.getType();
      case 28:
        if (t.containsPosition(dot)) {
          return 0;
        }
        return 27;
      case 25:
        if (t.isSingleChar('<')) {
          return 26;
        }
        return 0;
    } 
    return 0;
  }












  
  public static final Token getTagNameTokenForCaretOffset(RSyntaxTextArea textArea) {
    int dot = textArea.getCaretPosition();
    int line = textArea.getCaretLineNumber();
    Token toMark = null;

    
    do {
      Token t = textArea.getTokenListForLine(line);
      
      while (t != null && t.isPaintable()) {
        if (t.getType() == 26) {
          toMark = t;
        }
        if (t.getEndOffset() == dot || t.containsPosition(dot)) {
          break;
        }
        if (t.getType() == 25 && (
          t.isSingleChar('>') || t.is(TAG_SELF_CLOSE))) {
          toMark = null;
        }
        
        t = t.getNextToken();
      }
    
    } while (toMark == null && --line >= 0);
    
    return toMark;
  }
}
