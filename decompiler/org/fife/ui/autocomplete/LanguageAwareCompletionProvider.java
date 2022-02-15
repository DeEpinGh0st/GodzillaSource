package org.fife.ui.autocomplete;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.ToolTipSupplier;




































































public class LanguageAwareCompletionProvider
  extends CompletionProviderBase
  implements ToolTipSupplier
{
  private CompletionProvider defaultProvider;
  private CompletionProvider stringCompletionProvider;
  private CompletionProvider commentCompletionProvider;
  private CompletionProvider docCommentCompletionProvider;
  
  protected LanguageAwareCompletionProvider() {}
  
  public LanguageAwareCompletionProvider(CompletionProvider defaultProvider) {
    setDefaultCompletionProvider(defaultProvider);
  }











  
  public void clearParameterizedCompletionParams() {
    throw new UnsupportedOperationException();
  }





  
  public String getAlreadyEnteredText(JTextComponent comp) {
    if (!(comp instanceof RSyntaxTextArea)) {
      return "";
    }
    CompletionProvider provider = getProviderFor(comp);
    return (provider != null) ? provider.getAlreadyEnteredText(comp) : null;
  }







  
  public CompletionProvider getCommentCompletionProvider() {
    return this.commentCompletionProvider;
  }





  
  public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
    return (this.defaultProvider == null) ? null : this.defaultProvider
      .getCompletionsAt(tc, p);
  }









  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    if (comp instanceof RSyntaxTextArea) {
      CompletionProvider provider = getProviderFor(comp);
      if (provider != null) {
        return provider.getCompletions(comp);
      }
    } 
    return Collections.emptyList();
  }








  
  public CompletionProvider getDefaultCompletionProvider() {
    return this.defaultProvider;
  }







  
  public CompletionProvider getDocCommentCompletionProvider() {
    return this.docCommentCompletionProvider;
  }









  
  public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
    CompletionProvider provider = getProviderFor(tc);
    return (provider == this.defaultProvider) ? provider
      .getParameterizedCompletions(tc) : null;
  }





  
  public char getParameterListEnd() {
    return this.defaultProvider.getParameterListEnd();
  }





  
  public String getParameterListSeparator() {
    return this.defaultProvider.getParameterListSeparator();
  }





  
  public char getParameterListStart() {
    return this.defaultProvider.getParameterListStart();
  }









  
  private CompletionProvider getProviderFor(JTextComponent comp) {
    RSyntaxTextArea rsta = (RSyntaxTextArea)comp;
    RSyntaxDocument doc = (RSyntaxDocument)rsta.getDocument();
    int line = rsta.getCaretLineNumber();
    Token t = doc.getTokenListForLine(line);
    if (t == null) {
      return getDefaultCompletionProvider();
    }
    
    int dot = rsta.getCaretPosition();
    Token curToken = RSyntaxUtilities.getTokenAtOffset(t, dot);
    
    if (curToken == null) {
      
      int type = doc.getLastTokenTypeOnLine(line);
      if (type == 0) {
        Token temp = t.getLastPaintableToken();
        if (temp == null) {
          return getDefaultCompletionProvider();
        }
        type = temp.getType();



      
      }
      else if (type < 0) {
        type = doc.getClosestStandardTokenTypeForInternalType(type);
      } 
      
      switch (type) {
        case 37:
          return getStringCompletionProvider();
        case 1:
        case 2:
          return getCommentCompletionProvider();
        case 3:
          return getDocCommentCompletionProvider();
      } 
      return getDefaultCompletionProvider();
    } 



    
    if (dot == curToken.getOffset())
    {
      
      return getDefaultCompletionProvider();
    }
    
    switch (curToken.getType()) {
      case 13:
      case 37:
        return getStringCompletionProvider();
      case 1:
      case 2:
        return getCommentCompletionProvider();
      case 3:
        return getDocCommentCompletionProvider();
      case 0:
      case 8:
      case 16:
      case 17:
      case 20:
      case 21:
      case 23:
      case 24:
        return getDefaultCompletionProvider();
    } 
    
    return null;
  }








  
  public CompletionProvider getStringCompletionProvider() {
    return this.stringCompletionProvider;
  }





  
  public boolean isAutoActivateOkay(JTextComponent tc) {
    CompletionProvider provider = getProviderFor(tc);
    return (provider != null) ? provider.isAutoActivateOkay(tc) : false;
  }







  
  public void setCommentCompletionProvider(CompletionProvider provider) {
    this.commentCompletionProvider = provider;
  }








  
  public void setDefaultCompletionProvider(CompletionProvider provider) {
    if (provider == null) {
      throw new IllegalArgumentException("provider cannot be null");
    }
    this.defaultProvider = provider;
  }







  
  public void setDocCommentCompletionProvider(CompletionProvider provider) {
    this.docCommentCompletionProvider = provider;
  }












  
  public void setParameterizedCompletionParams(char listStart, String separator, char listEnd) {
    throw new UnsupportedOperationException();
  }







  
  public void setStringCompletionProvider(CompletionProvider provider) {
    this.stringCompletionProvider = provider;
  }

















  
  public String getToolTipText(RTextArea textArea, MouseEvent e) {
    String tip = null;
    
    List<Completion> completions = getCompletionsAt((JTextComponent)textArea, e.getPoint());
    if (completions != null && completions.size() > 0) {
      
      Completion c = completions.get(0);
      tip = c.getToolTipText();
    } 
    
    return tip;
  }
}
