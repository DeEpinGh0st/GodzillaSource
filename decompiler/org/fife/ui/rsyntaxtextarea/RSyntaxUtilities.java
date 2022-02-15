package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.View;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;










































public final class RSyntaxUtilities
  implements SwingConstants
{
  public static final int OS_WINDOWS = 1;
  public static final int OS_MAC_OSX = 2;
  public static final int OS_LINUX = 4;
  public static final int OS_OTHER = 8;
  private static final Color LIGHT_HYPERLINK_FG = new Color(14221311);
  
  private static final int OS = getOSImpl();


  
  private static final int LETTER_MASK = 2;

  
  private static final int HEX_CHARACTER_MASK = 16;

  
  private static final int LETTER_OR_DIGIT_MASK = 32;

  
  private static final int BRACKET_MASK = 64;

  
  private static final int JAVA_OPERATOR_MASK = 128;

  
  private static final int[] DATA_TABLE = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 128, 0, 0, 0, 128, 128, 0, 64, 64, 128, 128, 0, 128, 0, 128, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 128, 0, 128, 128, 128, 128, 0, 58, 58, 58, 58, 58, 58, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 64, 0, 64, 128, 0, 0, 50, 50, 50, 50, 50, 50, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 64, 128, 64, 128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };




















  
  private static Segment charSegment = new Segment();



  
  private static final TokenImpl TEMP_TOKEN = new TokenImpl();



  
  private static final char[] JS_KEYWORD_RETURN = new char[] { 'r', 'e', 't', 'u', 'r', 'n' };
  private static final char[] JS_AND = new char[] { '&', '&' };
  private static final char[] JS_OR = new char[] { '|', '|' };














  
  private static final String BRACKETS = "{([})]";














  
  public static String escapeForHtml(String s, String newlineReplacement, boolean inPreBlock) {
    if (s == null) {
      return null;
    }
    if (newlineReplacement == null) {
      newlineReplacement = "";
    }
    String tabString = "   ";
    boolean lastWasSpace = false;
    
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case ' ':
          if (inPreBlock || !lastWasSpace) {
            sb.append(' ');
          } else {
            
            sb.append("&nbsp;");
          } 
          lastWasSpace = true;
          break;
        case '\n':
          sb.append(newlineReplacement);
          lastWasSpace = false;
          break;
        case '&':
          sb.append("&amp;");
          lastWasSpace = false;
          break;
        case '\t':
          sb.append("   ");
          lastWasSpace = false;
          break;
        case '<':
          sb.append("&lt;");
          lastWasSpace = false;
          break;
        case '>':
          sb.append("&gt;");
          lastWasSpace = false;
          break;
        case '\'':
          sb.append("&#39;");
          lastWasSpace = false;
          break;
        case '"':
          sb.append("&#34;");
          lastWasSpace = false;
          break;
        case '/':
          sb.append("&#47;");
          lastWasSpace = false;
          break;
        default:
          sb.append(ch);
          lastWasSpace = false;
          break;
      } 
    
    } 
    return sb.toString();
  }









  
  public static Map<?, ?> getDesktopAntiAliasHints() {
    return (Map<?, ?>)Toolkit.getDefaultToolkit()
      .getDesktopProperty("awt.font.desktophints");
  }







  
  public static Color getFoldedLineBottomColor(RSyntaxTextArea textArea) {
    Color color = Color.GRAY;
    Gutter gutter = getGutter(textArea);
    if (gutter != null) {
      color = gutter.getFoldIndicatorForeground();
    }
    return color;
  }










  
  public static Gutter getGutter(RTextArea textArea) {
    Gutter gutter = null;
    Container parent = textArea.getParent();
    if (parent instanceof javax.swing.JViewport) {
      parent = parent.getParent();
      if (parent instanceof RTextScrollPane) {
        RTextScrollPane sp = (RTextScrollPane)parent;
        gutter = sp.getGutter();
      } 
    } 
    return gutter;
  }













  
  public static Color getHyperlinkForeground() {
    Color fg = UIManager.getColor("Label.foreground");
    if (fg == null) {
      fg = (new JLabel()).getForeground();
    }
    
    return isLightForeground(fg) ? LIGHT_HYPERLINK_FG : Color.blue;
  }









  
  public static String getLeadingWhitespace(String text) {
    int count = 0;
    int len = text.length();
    while (count < len && isWhitespace(text.charAt(count))) {
      count++;
    }
    return text.substring(0, count);
  }












  
  public static String getLeadingWhitespace(Document doc, int offs) throws BadLocationException {
    Element root = doc.getDefaultRootElement();
    int line = root.getElementIndex(offs);
    Element elem = root.getElement(line);
    int startOffs = elem.getStartOffset();
    int endOffs = elem.getEndOffset() - 1;
    String text = doc.getText(startOffs, endOffs - startOffs);
    return getLeadingWhitespace(text);
  }

  
  private static Element getLineElem(Document doc, int offs) {
    Element root = doc.getDefaultRootElement();
    int line = root.getElementIndex(offs);
    Element elem = root.getElement(line);
    if (offs >= elem.getStartOffset() && offs < elem.getEndOffset()) {
      return elem;
    }
    return null;
  }
































  
  public static Rectangle getLineWidthUpTo(RSyntaxTextArea textArea, Segment s, int p0, int p1, TabExpander e, Rectangle rect, int x0) throws BadLocationException {
    RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();

    
    if (p0 < 0) {
      throw new BadLocationException("Invalid document position", p0);
    }
    if (p1 > doc.getLength()) {
      throw new BadLocationException("Invalid document position", p1);
    }


    
    Element map = doc.getDefaultRootElement();
    int lineNum = map.getElementIndex(p0);


    
    if (Math.abs(lineNum - map.getElementIndex(p1)) > 1) {
      throw new IllegalArgumentException("p0 and p1 are not on the same line (" + p0 + ", " + p1 + ").");
    }


    
    Token t = doc.getTokenListForLine(lineNum);



    
    TokenUtils.TokenSubList subList = TokenUtils.getSubTokenList(t, p0, e, textArea, 0.0F, TEMP_TOKEN);
    
    t = subList.tokenList;
    
    rect = t.listOffsetToView(textArea, e, p1, x0, rect);
    return rect;
  }


















  
  public static Point getMatchingBracketPosition(RSyntaxTextArea textArea, Point input) {
    if (input == null) {
      input = new Point();
    }
    input.setLocation(-1, -1);
    
    try {
      char bracketMatch;
      boolean goForward;
      int caretPosition = textArea.getCaretPosition() - 1;
      RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
      char bracket = Character.MIN_VALUE;

      
      if (caretPosition >= 0) {
        bracket = doc.charAt(caretPosition);
      }


      
      int index = "{([})]".indexOf(bracket);
      if (index == -1 && caretPosition < doc.getLength() - 1) {
        bracket = doc.charAt(++caretPosition);
      }

      
      if (index == -1) {
        index = "{([})]".indexOf(bracket);
        if (index == -1) {
          return input;
        }
      } 





      
      Element map = doc.getDefaultRootElement();
      int curLine = map.getElementIndex(caretPosition);
      Element line = map.getElement(curLine);
      int start = line.getStartOffset();
      int end = line.getEndOffset();
      Token token = doc.getTokenListForLine(curLine);
      token = getTokenAtOffset(token, caretPosition);
      
      if (token.getType() != 22) {
        return input;
      }
      int languageIndex = token.getLanguageIndex();
      if (index < 3) {
        goForward = true;
        bracketMatch = "{([})]".charAt(index + 3);
      } else {
        
        goForward = false;
        bracketMatch = "{([})]".charAt(index - 3);
      } 
      
      if (goForward) {
        
        int lastLine = map.getElementCount();


        
        start = caretPosition + 1;
        int i = 0;
        boolean bool = false;

        
        while (true) {
          doc.getText(start, end - start, charSegment);
          int segOffset = charSegment.offset;
          
          for (int j = segOffset; j < segOffset + charSegment.count; j++) {
            
            char ch = charSegment.array[j];
            
            if (ch == bracket) {
              if (!bool) {
                token = doc.getTokenListForLine(curLine);
                bool = true;
              } 
              int offset = start + j - segOffset;
              token = getTokenAtOffset(token, offset);
              if (token.getType() == 22 && token
                .getLanguageIndex() == languageIndex) {
                i++;
              
              }
            }
            else if (ch == bracketMatch) {
              if (!bool) {
                token = doc.getTokenListForLine(curLine);
                bool = true;
              } 
              int offset = start + j - segOffset;
              token = getTokenAtOffset(token, offset);
              if (token.getType() == 22 && token
                .getLanguageIndex() == languageIndex) {
                if (i == 0) {
                  if (textArea.isCodeFoldingEnabled() && textArea
                    .getFoldManager().isLineHidden(curLine)) {
                    return input;
                  }
                  input.setLocation(caretPosition, offset);
                  return input;
                } 
                i--;
              } 
            } 
          } 



          
          if (++curLine == lastLine) {
            return input;
          }

          
          bool = false;
          line = map.getElement(curLine);
          start = line.getStartOffset();
          end = line.getEndOffset();
        } 
      } 









      
      end = caretPosition;
      int numEmbedded = 0;
      boolean haveTokenList = false;


      
      while (true) {
        doc.getText(start, end - start, charSegment);
        int segOffset = charSegment.offset;
        int iStart = segOffset + charSegment.count - 1;
        
        for (int i = iStart; i >= segOffset; i--) {
          
          char ch = charSegment.array[i];
          
          if (ch == bracket) {
            if (!haveTokenList) {
              token = doc.getTokenListForLine(curLine);
              haveTokenList = true;
            } 
            int offset = start + i - segOffset;
            Token t2 = getTokenAtOffset(token, offset);
            if (t2.getType() == 22 && token
              .getLanguageIndex() == languageIndex) {
              numEmbedded++;
            
            }
          }
          else if (ch == bracketMatch) {
            if (!haveTokenList) {
              token = doc.getTokenListForLine(curLine);
              haveTokenList = true;
            } 
            int offset = start + i - segOffset;
            Token t2 = getTokenAtOffset(token, offset);
            if (t2.getType() == 22 && token
              .getLanguageIndex() == languageIndex) {
              if (numEmbedded == 0) {
                input.setLocation(caretPosition, offset);
                return input;
              } 
              numEmbedded--;
            } 
          } 
        } 



        
        if (--curLine == -1) {
          return input;
        }


        
        haveTokenList = false;
        line = map.getElement(curLine);
        start = line.getStartOffset();
        end = line.getEndOffset();
      
      }

    
    }
    catch (BadLocationException ble) {
      
      ble.printStackTrace();


      
      return input;
    } 
  }













  
  public static Token getNextImportantToken(Token t, RSyntaxTextArea textArea, int line) {
    while (t != null && t.isPaintable() && t.isCommentOrWhitespace()) {
      t = t.getNextToken();
    }
    if ((t == null || !t.isPaintable()) && line < textArea.getLineCount() - 1) {
      t = textArea.getTokenListForLine(++line);
      return getNextImportantToken(t, textArea, line);
    } 
    return t;
  }

































  
  public static int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet, View view) throws BadLocationException {
    int endOffs;
    RSyntaxTextArea target = (RSyntaxTextArea)view.getContainer();
    biasRet[0] = Position.Bias.Forward;

    
    switch (direction) {
      
      case 1:
      case 5:
        if (pos == -1) {

          
          pos = (direction == 1) ? Math.max(0, view.getEndOffset() - 1) : view.getStartOffset();
        } else {
          Point mcp; int x;
          Caret c = (target != null) ? target.getCaret() : null;


          
          if (c != null) {
            mcp = c.getMagicCaretPosition();
          } else {
            
            mcp = null;
          } 
          
          if (mcp == null) {
            Rectangle loc = target.modelToView(pos);
            x = (loc == null) ? 0 : loc.x;
          } else {
            
            x = mcp.x;
          } 
          if (direction == 1) {
            pos = getPositionAbove(target, pos, x, (TabExpander)view);
          } else {
            
            pos = getPositionBelow(target, pos, x, (TabExpander)view);
          } 
        } 


















































        
        return pos;case 7: endOffs = view.getEndOffset(); if (pos == -1) { pos = Math.max(0, endOffs - 1); } else { pos = Math.max(0, pos - 1); if (target.isCodeFoldingEnabled()) { int last = (pos == endOffs - 1) ? (target.getLineCount() - 1) : target.getLineOfOffset(pos + 1); int current = target.getLineOfOffset(pos); if (last != current) { FoldManager fm = target.getFoldManager(); if (fm.isLineHidden(current)) { while (--current > 0 && fm.isLineHidden(current)); pos = target.getLineEndOffset(current) - 1; }  }  }  }  return pos;case 3: if (pos == -1) { pos = view.getStartOffset(); } else { pos = Math.min(pos + 1, view.getDocument().getLength()); if (target.isCodeFoldingEnabled()) { int last = (pos == 0) ? 0 : target.getLineOfOffset(pos - 1); int current = target.getLineOfOffset(pos); if (last != current) { FoldManager fm = target.getFoldManager(); if (fm.isLineHidden(current)) { int lineCount = target.getLineCount(); while (++current < lineCount && fm.isLineHidden(current)); pos = (current == lineCount) ? (target.getLineEndOffset(last) - 1) : target.getLineStartOffset(current); }  }  }  }  return pos;
    } 
    throw new IllegalArgumentException("Bad direction: " + direction);
  }







  
  public static int getOS() {
    return OS;
  }








  
  private static int getOSImpl() {
    int os = 8;
    String osName = System.getProperty("os.name");
    if (osName != null) {
      osName = osName.toLowerCase();
      if (osName.contains("windows")) {
        os = 1;
      }
      else if (osName.contains("mac os x")) {
        os = 2;
      }
      else if (osName.contains("linux")) {
        os = 4;
      } else {
        
        os = 8;
      } 
    } 
    return os;
  }








  
  public static int getPatternFlags(boolean matchCase, int others) {
    if (!matchCase) {
      others |= 0x42;
    }
    return others;
  }
















  
  public static int getPositionAbove(RSyntaxTextArea c, int offs, float x, TabExpander e) throws BadLocationException {
    TokenOrientedView tov = (TokenOrientedView)e;
    Token token = tov.getTokenListForPhysicalLineAbove(offs);
    if (token == null) {
      return -1;
    }
    if (token.getType() == 0) {
      int line = c.getLineOfOffset(offs);
      return c.getLineStartOffset(line - 1);
    } 

    
    return token.getListOffset(c, e, (c.getMargin()).left, x);
  }


















  
  public static int getPositionBelow(RSyntaxTextArea c, int offs, float x, TabExpander e) throws BadLocationException {
    TokenOrientedView tov = (TokenOrientedView)e;
    Token token = tov.getTokenListForPhysicalLineBelow(offs);
    if (token == null) {
      return -1;
    }
    if (token.getType() == 0) {
      int line = c.getLineOfOffset(offs);
      
      FoldManager fm = c.getFoldManager();
      line = fm.getVisibleLineBelow(line);
      return c.getLineStartOffset(line);
    } 

    
    return token.getListOffset(c, e, (c.getMargin()).left, x);
  }















  
  public static Token getPreviousImportantToken(RSyntaxDocument doc, int line) {
    if (line < 0) {
      return null;
    }
    Token t = doc.getTokenListForLine(line);
    if (t != null) {
      t = t.getLastNonCommentNonWhitespaceToken();
      if (t != null) {
        return t;
      }
    } 
    return getPreviousImportantToken(doc, line - 1);
  }














  
  public static Token getPreviousImportantTokenFromOffs(RSyntaxDocument doc, int offs) {
    Element root = doc.getDefaultRootElement();
    int line = root.getElementIndex(offs);
    Token t = doc.getTokenListForLine(line);

    
    Token target = null;
    while (t != null && t.isPaintable() && !t.containsPosition(offs)) {
      if (!t.isCommentOrWhitespace()) {
        target = t;
      }
      t = t.getNextToken();
    } 

    
    if (target == null) {
      target = getPreviousImportantToken(doc, line - 1);
    }
    
    return target;
  }












  
  public static Token getTokenAtOffset(RSyntaxTextArea textArea, int offset) {
    RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
    return getTokenAtOffset(doc, offset);
  }











  
  public static Token getTokenAtOffset(RSyntaxDocument doc, int offset) {
    Element root = doc.getDefaultRootElement();
    int lineIndex = root.getElementIndex(offset);
    Token t = doc.getTokenListForLine(lineIndex);
    return getTokenAtOffset(t, offset);
  }














  
  public static Token getTokenAtOffset(Token tokenList, int offset) {
    for (Token t = tokenList; t != null && t.isPaintable(); t = t.getNextToken()) {
      if (t.containsPosition(offset)) {
        return t;
      }
    } 
    return null;
  }












  
  public static int getWordEnd(RSyntaxTextArea textArea, int offs) throws BadLocationException {
    Document doc = textArea.getDocument();
    int endOffs = textArea.getLineEndOffsetOfCurrentLine();
    int lineEnd = Math.min(endOffs, doc.getLength());
    if (offs == lineEnd) {
      return offs;
    }
    
    String s = doc.getText(offs, lineEnd - offs - 1);
    if (s != null && s.length() > 0) {
      int i = 0;
      int count = s.length();
      char ch = s.charAt(i);
      if (Character.isWhitespace(ch)) {
        while (i < count && Character.isWhitespace(s.charAt(i++)));
      }
      else if (Character.isLetterOrDigit(ch)) {
        while (i < count && Character.isLetterOrDigit(s.charAt(i++)));
      } else {
        
        i = 2;
      } 
      offs += i - 1;
    } 
    
    return offs;
  }












  
  public static int getWordStart(RSyntaxTextArea textArea, int offs) throws BadLocationException {
    Document doc = textArea.getDocument();
    Element line = getLineElem(doc, offs);
    if (line == null) {
      throw new BadLocationException("No word at " + offs, offs);
    }
    
    int lineStart = line.getStartOffset();
    if (offs == lineStart) {
      return offs;
    }
    
    int endOffs = Math.min(offs + 1, doc.getLength());
    String s = doc.getText(lineStart, endOffs - lineStart);
    if (s != null && s.length() > 0) {
      int i = s.length() - 1;
      char ch = s.charAt(i);
      if (Character.isWhitespace(ch)) {
        while (i > 0 && Character.isWhitespace(s.charAt(i - 1))) {
          i--;
        }
        offs = lineStart + i;
      }
      else if (Character.isLetterOrDigit(ch)) {
        while (i > 0 && Character.isLetterOrDigit(s.charAt(i - 1))) {
          i--;
        }
        offs = lineStart + i;
      } 
    } 

    
    return offs;
  }

















  
  public static float getTokenListWidth(Token tokenList, RSyntaxTextArea textArea, TabExpander e) {
    return getTokenListWidth(tokenList, textArea, e, 0.0F);
  }















  
  public static float getTokenListWidth(Token tokenList, RSyntaxTextArea textArea, TabExpander e, float x0) {
    float width = x0;
    for (Token t = tokenList; t != null && t.isPaintable(); t = t.getNextToken()) {
      width += t.getWidth(textArea, e, width);
    }
    return width - x0;
  }





















  
  public static float getTokenListWidthUpTo(Token tokenList, RSyntaxTextArea textArea, TabExpander e, float x0, int upTo) {
    float width = 0.0F;
    for (Token t = tokenList; t != null && t.isPaintable(); t = t.getNextToken()) {
      if (t.containsPosition(upTo)) {
        return width + t.getWidthUpTo(upTo - t.getOffset(), textArea, e, x0 + width);
      }
      
      width += t.getWidth(textArea, e, x0 + width);
    } 
    return width;
  }












  
  public static boolean isBracket(char ch) {
    return (ch <= '}' && (DATA_TABLE[ch] & 0x40) > 0);
  }










  
  public static boolean isDigit(char ch) {
    return (ch >= '0' && ch <= '9');
  }












  
  public static boolean isHexCharacter(char ch) {
    return (ch <= 'f' && (DATA_TABLE[ch] & 0x10) > 0);
  }











  
  public static boolean isJavaOperator(char ch) {
    return (ch <= '~' && (DATA_TABLE[ch] & 0x80) > 0);
  }









  
  public static boolean isLetter(char ch) {
    return (ch <= 'z' && (DATA_TABLE[ch] & 0x2) > 0);
  }









  
  public static boolean isLetterOrDigit(char ch) {
    return (ch <= 'z' && (DATA_TABLE[ch] & 0x20) > 0);
  }










  
  public static boolean isLightForeground(Color fg) {
    return (fg.getRed() > 160 && fg.getGreen() > 160 && fg.getBlue() > 160);
  }











  
  public static boolean isNonWordChar(Token t) {
    return (t.length() == 1 && !isLetter(t.charAt(0)));
  }












  
  public static boolean isWhitespace(char ch) {
    return (ch == ' ' || ch == '\t');
  }






  
  public static void possiblyRepaintGutter(RTextArea textArea) {
    Gutter gutter = getGutter(textArea);
    if (gutter != null) {
      gutter.repaint();
    }
  }










  
  public static boolean regexCanFollowInJavaScript(Token t) {
    char ch;
    return (t == null || (t
      
      .length() == 1 && ((
      ch = t.charAt(0)) == '=' || ch == '(' || ch == ',' || ch == '?' || ch == ':' || ch == '[' || ch == '!' || ch == '&')) || (t








      
      .getType() == 23 && (t
      .charAt(t.length() - 1) == '=' || t
      .is(JS_AND) || t.is(JS_OR))) || t
      .is(7, JS_KEYWORD_RETURN));
  }










  
  public static void selectAndPossiblyCenter(JTextArea textArea, DocumentRange range, boolean select) {
    Rectangle r;
    int start = range.getStartOffset();
    int end = range.getEndOffset();
    
    boolean foldsExpanded = false;
    if (textArea instanceof RSyntaxTextArea) {
      RSyntaxTextArea rsta = (RSyntaxTextArea)textArea;
      FoldManager fm = rsta.getFoldManager();
      if (fm.isCodeFoldingSupportedAndEnabled()) {
        foldsExpanded = fm.ensureOffsetNotInClosedFold(start);
        foldsExpanded |= fm.ensureOffsetNotInClosedFold(end);
      } 
    } 
    
    if (select) {
      textArea.setSelectionStart(start);
      textArea.setSelectionEnd(end);
    } 

    
    try {
      r = textArea.modelToView(start);
      if (r == null) {
        return;
      }
      if (end != start) {
        r = r.union(textArea.modelToView(end));
      }
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      if (select) {
        textArea.setSelectionStart(start);
        textArea.setSelectionEnd(end);
      } 
      
      return;
    } 
    Rectangle visible = textArea.getVisibleRect();


    
    if (!foldsExpanded && visible.contains(r)) {
      if (select) {
        textArea.setSelectionStart(start);
        textArea.setSelectionEnd(end);
      } 
      
      return;
    } 
    r.x -= (visible.width - r.width) / 2;
    r.y -= (visible.height - r.height) / 2;
    
    Rectangle bounds = textArea.getBounds();
    Insets i = textArea.getInsets();
    bounds.x = i.left;
    bounds.y = i.top;
    bounds.width -= i.left + i.right;
    bounds.height -= i.top + i.bottom;
    
    if (visible.x < bounds.x) {
      visible.x = bounds.x;
    }
    
    if (visible.x + visible.width > bounds.x + bounds.width) {
      visible.x = bounds.x + bounds.width - visible.width;
    }
    
    if (visible.y < bounds.y) {
      visible.y = bounds.y;
    }
    
    if (visible.y + visible.height > bounds.y + bounds.height) {
      visible.y = bounds.y + bounds.height - visible.height;
    }
    
    textArea.scrollRectToVisible(visible);
  }

















  
  public static char toLowerCase(char ch) {
    if (ch >= 'A' && ch <= 'Z') {
      return (char)(ch | 0x20);
    }
    return ch;
  }











  
  public static Pattern wildcardToPattern(String wildcard, boolean matchCase, boolean escapeStartChar) {
    Pattern p;
    int flags = getPatternFlags(matchCase, 0);
    
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < wildcard.length(); i++) {
      char ch = wildcard.charAt(i);
      switch (ch) {
        case '*':
          sb.append(".*");
          break;
        case '?':
          sb.append('.');
          break;
        case '^':
          if (i > 0 || escapeStartChar) {
            sb.append('\\');
          }
          sb.append('^'); break;
        case '$': case '(': case ')': case '+': case '-':
        case '.':
        case '[':
        case '\\':
        case ']':
        case '{':
        case '|':
        case '}':
          sb.append('\\').append(ch);
          break;
        default:
          sb.append(ch);
          break;
      } 

    
    } 
    try {
      p = Pattern.compile(sb.toString(), flags);
    } catch (PatternSyntaxException pse) {
      pse.printStackTrace();
      p = Pattern.compile(".+");
    } 
    
    return p;
  }
}
