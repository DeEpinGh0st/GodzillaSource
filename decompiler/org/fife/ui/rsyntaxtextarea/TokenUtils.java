package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Font;
import javax.swing.text.TabExpander;




















































public final class TokenUtils
{
  public static TokenSubList getSubTokenList(Token tokenList, int pos, TabExpander e, RSyntaxTextArea textArea, float x0) {
    return getSubTokenList(tokenList, pos, e, textArea, x0, null);
  }













































  
  public static TokenSubList getSubTokenList(Token tokenList, int pos, TabExpander e, RSyntaxTextArea textArea, float x0, TokenImpl tempToken) {
    if (tempToken == null) {
      tempToken = new TokenImpl();
    }
    Token t = tokenList;


    
    while (t != null && t.isPaintable() && !t.containsPosition(pos)) {
      x0 += t.getWidth(textArea, e, x0);
      t = t.getNextToken();
    } 

    
    if (t != null && t.isPaintable()) {
      
      if (t.getOffset() != pos) {
        
        int difference = pos - t.getOffset();
        x0 += t.getWidthUpTo(t.length() - difference + 1, textArea, e, x0);
        tempToken.copyFrom(t);
        tempToken.makeStartAt(pos);
        
        return new TokenSubList(tempToken, x0);
      } 

      
      return new TokenSubList(t, x0);
    } 



    
    return new TokenSubList(tokenList, x0);
  }













  
  public static int getWhiteSpaceTokenLength(Token t, int tabSize, int curOffs) {
    int length = 0;
    
    for (int i = 0; i < t.length(); i++) {
      char ch = t.charAt(i);
      if (ch == '\t') {
        int newCurOffs = (curOffs + tabSize) / tabSize * tabSize;
        length += newCurOffs - curOffs;
        curOffs = newCurOffs;
      } else {
        
        length++;
        curOffs++;
      } 
    } 
    
    return length;
  }











  
  public static boolean isBlankOrAllWhiteSpace(Token t) {
    while (t != null && t.isPaintable()) {
      if (!t.isCommentOrWhitespace()) {
        return false;
      }
      t = t.getNextToken();
    } 
    return true;
  }











  
  public static boolean isBlankOrAllWhiteSpaceWithoutComments(Token t) {
    while (t != null && t.isPaintable()) {
      if (!t.isWhitespace()) {
        return false;
      }
      t = t.getNextToken();
    } 
    return true;
  }











  
  public static String tokenToHtml(RSyntaxTextArea textArea, Token token) {
    StringBuilder style = new StringBuilder();
    
    Font font = textArea.getFontForTokenType(token.getType());
    if (font.isBold()) {
      style.append("font-weight: bold;");
    }
    if (font.isItalic()) {
      style.append("font-style: italic;");
    }
    
    Color c = textArea.getForegroundForToken(token);
    style.append("color: ").append(HtmlUtil.getHexString(c)).append(";");
    
    return "<span style=\"" + style + "\">" + 
      HtmlUtil.escapeForHtml(token.getLexeme(), "\n", true) + "</span>";
  }





  
  public static class TokenSubList
  {
    public Token tokenList;




    
    public float x;




    
    public TokenSubList(Token tokenList, float x) {
      this.tokenList = tokenList;
      this.x = x;
    }
  }
}
