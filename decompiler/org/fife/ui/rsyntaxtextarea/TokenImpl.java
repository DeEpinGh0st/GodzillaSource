package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;






















































public class TokenImpl
  implements Token
{
  public char[] text;
  public int textOffset;
  public int textCount;
  private int offset;
  private int type;
  private boolean hyperlink;
  private Token nextToken;
  private int languageIndex;
  
  public TokenImpl() {
    this.text = null;
    this.textOffset = -1;
    this.textCount = -1;
    setType(0);
    setOffset(-1);
    this.hyperlink = false;
    this.nextToken = null;
  }













  
  public TokenImpl(Segment line, int beg, int end, int startOffset, int type, int languageIndex) {
    this(line.array, beg, end, startOffset, type, languageIndex);
  }













  
  public TokenImpl(char[] line, int beg, int end, int startOffset, int type, int languageIndex) {
    this();
    set(line, beg, end, startOffset, type);
    setLanguageIndex(languageIndex);
  }






  
  public TokenImpl(Token t2) {
    this();
    copyFrom(t2);
  }




  
  public StringBuilder appendHTMLRepresentation(StringBuilder sb, RSyntaxTextArea textArea, boolean fontFamily) {
    return appendHTMLRepresentation(sb, textArea, fontFamily, false);
  }





  
  public StringBuilder appendHTMLRepresentation(StringBuilder sb, RSyntaxTextArea textArea, boolean fontFamily, boolean tabsToSpaces) {
    SyntaxScheme colorScheme = textArea.getSyntaxScheme();
    Style scheme = colorScheme.getStyle(getType());
    Font font = textArea.getFontForTokenType(getType());
    
    if (font.isBold()) {
      sb.append("<b>");
    }
    if (font.isItalic()) {
      sb.append("<em>");
    }
    if (scheme.underline || isHyperlink()) {
      sb.append("<u>");
    }
    
    boolean needsFontTag = (fontFamily || !isWhitespace());
    if (needsFontTag) {
      sb.append("<font");
      if (fontFamily) {
        sb.append(" face=\"").append(font.getFamily()).append('"');
      }
      if (!isWhitespace()) {
        sb.append(" color=\"").append(
            getHTMLFormatForColor(scheme.foreground)).append('"');
      }
      sb.append('>');
    } 


    
    appendHtmlLexeme(textArea, sb, tabsToSpaces);
    
    if (needsFontTag) {
      sb.append("</font>");
    }
    if (scheme.underline || isHyperlink()) {
      sb.append("</u>");
    }
    if (font.isItalic()) {
      sb.append("</em>");
    }
    if (font.isBold()) {
      sb.append("</b>");
    }
    
    return sb;
  }














  
  private StringBuilder appendHtmlLexeme(RSyntaxTextArea textArea, StringBuilder sb, boolean tabsToSpaces) {
    boolean lastWasSpace = false;
    int i = this.textOffset;
    int lastI = i;
    String tabStr = null;
    
    while (i < this.textOffset + this.textCount) {
      char ch = this.text[i];
      switch (ch) {
        case ' ':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          sb.append(lastWasSpace ? "&nbsp;" : " ");
          lastWasSpace = true;
          break;
        case '\t':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          if (tabsToSpaces && tabStr == null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < textArea.getTabSize(); j++) {
              stringBuilder.append("&nbsp;");
            }
            tabStr = stringBuilder.toString();
          } 
          sb.append(tabsToSpaces ? tabStr : "&#09;");
          lastWasSpace = false;
          break;
        case '&':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          sb.append("&amp;");
          lastWasSpace = false;
          break;
        case '<':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          sb.append("&lt;");
          lastWasSpace = false;
          break;
        case '>':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          sb.append("&gt;");
          lastWasSpace = false;
          break;
        case '\'':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          sb.append("&#39;");
          lastWasSpace = false;
          break;
        case '"':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          sb.append("&#34;");
          lastWasSpace = false;
          break;
        case '/':
          sb.append(this.text, lastI, i - lastI);
          lastI = i + 1;
          sb.append("&#47;");
          lastWasSpace = false;
          break;
        default:
          lastWasSpace = false;
          break;
      } 
      i++;
    } 
    if (lastI < this.textOffset + this.textCount) {
      sb.append(this.text, lastI, this.textOffset + this.textCount - lastI);
    }
    return sb;
  }


  
  public char charAt(int index) {
    return this.text[this.textOffset + index];
  }


  
  public boolean containsPosition(int pos) {
    return (pos >= getOffset() && pos < getOffset() + this.textCount);
  }







  
  public void copyFrom(Token t2) {
    this.text = t2.getTextArray();
    this.textOffset = t2.getTextOffset();
    this.textCount = t2.length();
    setOffset(t2.getOffset());
    setType(t2.getType());
    this.hyperlink = t2.isHyperlink();
    this.languageIndex = t2.getLanguageIndex();
    this.nextToken = t2.getNextToken();
  }


  
  public int documentToToken(int pos) {
    return pos + this.textOffset - getOffset();
  }


  
  public boolean endsWith(char[] ch) {
    if (ch == null || ch.length > this.textCount) {
      return false;
    }
    int start = this.textOffset + this.textCount - ch.length;
    for (int i = 0; i < ch.length; i++) {
      if (this.text[start + i] != ch[i]) {
        return false;
      }
    } 
    return true;
  }



  
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Token)) {
      return false;
    }
    
    Token t2 = (Token)obj;
    return (this.offset == t2.getOffset() && this.type == t2
      .getType() && this.languageIndex == t2
      .getLanguageIndex() && this.hyperlink == t2
      .isHyperlink() && ((
      getLexeme() == null && t2.getLexeme() == null) || (
      getLexeme() != null && getLexeme().equals(t2.getLexeme()))));
  }



  
  public int getEndOffset() {
    return this.offset + this.textCount;
  }









  
  private static String getHTMLFormatForColor(Color color) {
    if (color == null) {
      return "black";
    }
    String hexRed = Integer.toHexString(color.getRed());
    if (hexRed.length() == 1) {
      hexRed = "0" + hexRed;
    }
    String hexGreen = Integer.toHexString(color.getGreen());
    if (hexGreen.length() == 1) {
      hexGreen = "0" + hexGreen;
    }
    String hexBlue = Integer.toHexString(color.getBlue());
    if (hexBlue.length() == 1) {
      hexBlue = "0" + hexBlue;
    }
    return "#" + hexRed + hexGreen + hexBlue;
  }


  
  public String getHTMLRepresentation(RSyntaxTextArea textArea) {
    StringBuilder buf = new StringBuilder();
    appendHTMLRepresentation(buf, textArea, true);
    return buf.toString();
  }


  
  public int getLanguageIndex() {
    return this.languageIndex;
  }



  
  public Token getLastNonCommentNonWhitespaceToken() {
    Token last = null;
    
    for (Token t = this; t != null && t.isPaintable(); t = t.getNextToken()) {
      switch (t.getType()) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 21:
          break;
        default:
          last = t;
          break;
      } 
    
    } 
    return last;
  }



  
  public Token getLastPaintableToken() {
    Token t = this;
    while (t.isPaintable()) {
      Token next = t.getNextToken();
      if (next == null || !next.isPaintable()) {
        return t;
      }
      t = next;
    } 
    return null;
  }


  
  public String getLexeme() {
    if (this.text == null) {
      return null;
    }
    return isPaintable() ? new String(this.text, this.textOffset, this.textCount) : null;
  }





  
  public int getListOffset(RSyntaxTextArea textArea, TabExpander e, float x0, float x) {
    if (x0 >= x) {
      return getOffset();
    }
    
    float currX = x0;
    float nextX = x0;
    float stableX = x0;
    TokenImpl token = this;
    int last = getOffset();
    FontMetrics fm = null;
    
    while (token != null && token.isPaintable()) {
      
      fm = textArea.getFontMetricsForTokenType(token.getType());
      char[] text = token.text;
      int start = token.textOffset;
      int end = start + token.textCount;
      
      for (int i = start; i < end; i++) {
        currX = nextX;
        if (text[i] == '\t') {
          nextX = e.nextTabStop(nextX, 0);
          stableX = nextX;
          start = i + 1;
        } else {
          
          nextX = stableX + fm.charsWidth(text, start, i - start + 1);
        } 
        if (x >= currX && x < nextX) {
          if (x - currX < nextX - x) {
            return last + i - token.textOffset;
          }
          return last + i + 1 - token.textOffset;
        } 
      } 
      
      stableX = nextX;
      last += token.textCount;
      token = (TokenImpl)token.getNextToken();
    } 


    
    return last;
  }



  
  public Token getNextToken() {
    return this.nextToken;
  }


  
  public int getOffset() {
    return this.offset;
  }




  
  public int getOffsetBeforeX(RSyntaxTextArea textArea, TabExpander e, float startX, float endBeforeX) {
    FontMetrics fm = textArea.getFontMetricsForTokenType(getType());
    int i = this.textOffset;
    int stop = i + this.textCount;
    float x = startX;
    
    while (i < stop) {
      if (this.text[i] == '\t') {
        x = e.nextTabStop(x, 0);
      } else {
        
        x += fm.charWidth(this.text[i]);
      } 
      if (x > endBeforeX) {


        
        int intoToken = Math.max(i - this.textOffset, 1);
        return getOffset() + intoToken;
      } 
      i++;
    } 

    
    return getOffset() + this.textCount - 1;
  }



  
  public char[] getTextArray() {
    return this.text;
  }


  
  public int getTextOffset() {
    return this.textOffset;
  }


  
  public int getType() {
    return this.type;
  }


  
  public float getWidth(RSyntaxTextArea textArea, TabExpander e, float x0) {
    return getWidthUpTo(this.textCount, textArea, e, x0);
  }



  
  public float getWidthUpTo(int numChars, RSyntaxTextArea textArea, TabExpander e, float x0) {
    float width = x0;
    FontMetrics fm = textArea.getFontMetricsForTokenType(getType());
    if (fm != null) {
      
      int currentStart = this.textOffset;
      int endBefore = this.textOffset + numChars;
      for (int i = currentStart; i < endBefore; i++) {
        if (this.text[i] == '\t') {



          
          int j = i - currentStart;
          if (j > 0) {
            width += fm.charsWidth(this.text, currentStart, j);
          }
          currentStart = i + 1;
          width = e.nextTabStop(width, 0);
        } 
      } 


      
      int w = endBefore - currentStart;
      width += fm.charsWidth(this.text, currentStart, w);
    } 
    return width - x0;
  }


  
  public int hashCode() {
    return this.offset + ((getLexeme() == null) ? 0 : getLexeme().hashCode());
  }


  
  public boolean is(char[] lexeme) {
    if (this.textCount == lexeme.length) {
      for (int i = 0; i < this.textCount; i++) {
        if (this.text[this.textOffset + i] != lexeme[i]) {
          return false;
        }
      } 
      return true;
    } 
    return false;
  }


  
  public boolean is(int type, char[] lexeme) {
    if (getType() == type && this.textCount == lexeme.length) {
      for (int i = 0; i < this.textCount; i++) {
        if (this.text[this.textOffset + i] != lexeme[i]) {
          return false;
        }
      } 
      return true;
    } 
    return false;
  }


  
  public boolean is(int type, String lexeme) {
    return (getType() == type && this.textCount == lexeme.length() && lexeme
      .equals(getLexeme()));
  }


  
  public boolean isComment() {
    return (getType() >= 1 && getType() <= 5);
  }


  
  public boolean isCommentOrWhitespace() {
    return (isComment() || isWhitespace());
  }


  
  public boolean isHyperlink() {
    return this.hyperlink;
  }


  
  public boolean isIdentifier() {
    return (getType() == 20);
  }


  
  public boolean isLeftCurly() {
    return (getType() == 22 && isSingleChar('{'));
  }


  
  public boolean isRightCurly() {
    return (getType() == 22 && isSingleChar('}'));
  }


  
  public boolean isPaintable() {
    return (getType() > 0);
  }


  
  public boolean isSingleChar(char ch) {
    return (this.textCount == 1 && this.text[this.textOffset] == ch);
  }


  
  public boolean isSingleChar(int type, char ch) {
    return (getType() == type && isSingleChar(ch));
  }


  
  public boolean isWhitespace() {
    return (getType() == 21);
  }


  
  public int length() {
    return this.textCount;
  }




  
  public Rectangle listOffsetToView(RSyntaxTextArea textArea, TabExpander e, int pos, int x0, Rectangle rect) {
    int stableX = x0;
    TokenImpl token = this;
    FontMetrics fm = null;
    Segment s = new Segment();
    
    while (token != null && token.isPaintable()) {
      
      fm = textArea.getFontMetricsForTokenType(token.getType());
      if (fm == null) {
        return rect;
      }
      char[] text = token.text;
      int start = token.textOffset;
      int end = start + token.textCount;


      
      if (token.containsPosition(pos)) {
        
        s.array = token.text;
        s.offset = token.textOffset;
        s.count = pos - token.getOffset();



        
        int w = Utilities.getTabbedTextWidth(s, fm, stableX, e, token
            .getOffset());
        rect.x = stableX + w;
        end = token.documentToToken(pos);
        
        if (text[end] == '\t') {
          rect.width = fm.charWidth(' ');
        } else {
          
          rect.width = fm.charWidth(text[end]);
        } 
        
        return rect;
      } 




      
      s.array = token.text;
      s.offset = token.textOffset;
      s.count = token.textCount;
      stableX += Utilities.getTabbedTextWidth(s, fm, stableX, e, token
          .getOffset());

      
      token = (TokenImpl)token.getNextToken();
    } 





    
    rect.x = stableX;
    rect.width = 1;
    return rect;
  }

















  
  public void makeStartAt(int pos) {
    if (pos < getOffset() || pos >= getOffset() + this.textCount) {
      throw new IllegalArgumentException("pos " + pos + " is not in range " + 
          getOffset() + "-" + (getOffset() + this.textCount - 1));
    }
    int shift = pos - getOffset();
    setOffset(pos);
    this.textOffset += shift;
    this.textCount -= shift;
  }














  
  public void moveOffset(int amt) {
    if (amt < 0 || amt > this.textCount) {
      throw new IllegalArgumentException("amt " + amt + " is not in range 0-" + this.textCount);
    }
    
    setOffset(getOffset() + amt);
    this.textOffset += amt;
    this.textCount -= amt;
  }












  
  public void set(char[] line, int beg, int end, int offset, int type) {
    this.text = line;
    this.textOffset = beg;
    this.textCount = end - beg + 1;
    setType(type);
    setOffset(offset);
    this.nextToken = null;
  }








  
  public void setHyperlink(boolean hyperlink) {
    this.hyperlink = hyperlink;
  }















  
  public void setLanguageIndex(int languageIndex) {
    this.languageIndex = languageIndex;
  }








  
  public void setNextToken(Token nextToken) {
    this.nextToken = nextToken;
  }







  
  public void setOffset(int offset) {
    this.offset = offset;
  }


  
  public void setType(int type) {
    this.type = type;
  }


  
  public boolean startsWith(char[] chars) {
    if (chars.length <= this.textCount) {
      for (int i = 0; i < chars.length; i++) {
        if (this.text[this.textOffset + i] != chars[i]) {
          return false;
        }
      } 
      return true;
    } 
    return false;
  }


  
  public int tokenToDocument(int pos) {
    return pos + getOffset() - this.textOffset;
  }








  
  public String toString() {
    return "[Token: " + (
      (getType() == 0) ? "<null token>" : ("text: '" + ((this.text == null) ? "<null>" : (
      
      getLexeme() + "'; offset: " + 
      getOffset() + "; type: " + getType() + "; isPaintable: " + 
      isPaintable() + "; nextToken==null: " + ((this.nextToken == null) ? 1 : 0))))) + "]";
  }
}
