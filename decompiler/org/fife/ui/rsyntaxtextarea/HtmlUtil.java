package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
























public final class HtmlUtil
{
  public static String escapeForHtml(String s, String newlineReplacement, boolean inPreBlock) {
    if (newlineReplacement == null) {
      newlineReplacement = "";
    }
    String tabString = inPreBlock ? "    " : "&nbsp;&nbsp;&nbsp;&nbsp;";
    
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case ' ':
          if (inPreBlock) {
            sb.append(' ');
            break;
          } 
          sb.append("&nbsp;");
          break;
        
        case '\n':
          sb.append(newlineReplacement);
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '\t':
          sb.append(tabString);
          break;
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        default:
          sb.append(ch);
          break;
      } 
    
    } 
    return sb.toString();
  }









  
  public static String getHexString(Color c) {
    if (c == null) {
      return null;
    }
    
    StringBuilder sb = new StringBuilder("#");
    
    int r = c.getRed();
    if (r < 16) {
      sb.append('0');
    }
    sb.append(Integer.toHexString(r));
    int g = c.getGreen();
    if (g < 16) {
      sb.append('0');
    }
    sb.append(Integer.toHexString(g));
    int b = c.getBlue();
    if (b < 16) {
      sb.append('0');
    }
    sb.append(Integer.toHexString(b));
    
    return sb.toString();
  }



  
  public static String getTextAsHtml(RSyntaxTextArea textArea, int start, int end) {
    StringBuilder sb = (new StringBuilder("<pre style='")).append("font-family: \"").append(textArea.getFont().getFamily()).append("\", courier;");
    if (textArea.getBackground() != null) {
      sb.append(" background: ")
        .append(getHexString(textArea.getBackground()))
        .append("'>");
    }
    
    Token token = textArea.getTokenListFor(start, end);
    for (Token t = token; t != null; t = t.getNextToken()) {
      
      if (t.isPaintable())
      {
        if (t.isSingleChar('\n')) {
          sb.append("<br>");
        } else {
          
          sb.append(TokenUtils.tokenToHtml(textArea, t));
        } 
      }
    } 
    
    sb.append("</pre>");
    return sb.toString();
  }
}
