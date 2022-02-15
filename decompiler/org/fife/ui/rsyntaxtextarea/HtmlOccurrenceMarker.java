package org.fife.ui.rsyntaxtextarea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import org.fife.ui.rtextarea.SmartHighlightPainter;




















public class HtmlOccurrenceMarker
  implements OccurrenceMarker
{
  private static final char[] CLOSE_TAG_START = new char[] { '<', '/' };
  private static final char[] TAG_SELF_CLOSE = new char[] { '/', '>' };

  
  private static final Set<String> TAGS_REQUIRING_CLOSING = getRequiredClosingTags();
  
  public static final Set<String> getRequiredClosingTags() {
    String[] tags = { "html", "head", "title", "style", "script", "noscript", "body", "section", "nav", "article", "aside", "h1", "h2", "h3", "h4", "h5", "h6", "header", "footer", "address", "pre", "dialog", "blockquote", "ol", "ul", "dl", "a", "q", "cite", "em", "strong", "small", "mark", "dfn", "abbr", "time", "progress", "meter", "code", "var", "samp", "kbd", "sub", "sup", "span", "i", "b", "bdo", "ruby", "rt", "rp", "ins", "del", "figure", "iframe", "object", "video", "audio", "canvas", "map", "table", "caption", "form", "fieldset", "label", "button", "select", "datalist", "textarea", "output", "details", "bb", "menu", "legend", "div", "acronym", "applet", "big", "blink", "center", "dir", "font", "frame", "frameset", "isindex", "listing", "marquee", "nobr", "noembed", "noframes", "plaintext", "s", "spacer", "strike", "tt", "u", "xmp" };


































































































    
    return new HashSet<>(Arrays.asList(tags));
  }




















  
  public static final Token getTagNameTokenForCaretOffset(RSyntaxTextArea textArea, OccurrenceMarker occurrenceMarker) {
    int dot = textArea.getCaretPosition();
    Token t = textArea.getTokenListForLine(textArea.getCaretLineNumber());
    Token toMark = null;
    
    while (t != null && t.isPaintable()) {
      if (t.getType() == 26) {
        toMark = t;
      }

      
      if (t.getEndOffset() == dot || t.containsPosition(dot)) {

        
        if (occurrenceMarker.isValidType(textArea, t) && t
          .getType() != 26) {
          return t;
        }
        if (t.containsPosition(dot)) {
          break;
        }
      } 
      if (t.getType() == 25 && (
        t.isSingleChar('>') || t.is(TAG_SELF_CLOSE))) {
        toMark = null;
      }
      
      t = t.getNextToken();
    } 
    
    return toMark;
  }



  
  public Token getTokenToMark(RSyntaxTextArea textArea) {
    return getTagNameTokenForCaretOffset(textArea, this);
  }


  
  public boolean isValidType(RSyntaxTextArea textArea, Token t) {
    return textArea.getMarkOccurrencesOfTokenType(t.getType());
  }




  
  public void markOccurrences(RSyntaxDocument doc, Token t, RSyntaxTextAreaHighlighter h, SmartHighlightPainter p) {
    if (t.getType() != 26) {
      DefaultOccurrenceMarker.markOccurrencesOfToken(doc, t, h, p);
      
      return;
    } 
    String lexemeStr = t.getLexeme();
    char[] lexeme = lexemeStr.toCharArray();
    lexemeStr = lexemeStr.toLowerCase();
    int tokenOffs = t.getOffset();
    Element root = doc.getDefaultRootElement();
    int lineCount = root.getElementCount();
    int curLine = root.getElementIndex(t.getOffset());
    int depth = 0;


    
    boolean found = false;
    boolean forward = true;
    t = doc.getTokenListForLine(curLine);
    while (t != null && t.isPaintable()) {
      if (t.getType() == 25) {
        if (t.isSingleChar('<') && t.getOffset() + 1 == tokenOffs) {

          
          if (TAGS_REQUIRING_CLOSING.contains(lexemeStr)) {
            found = true;
          }
          break;
        } 
        if (t.is(CLOSE_TAG_START) && t.getOffset() + 2 == tokenOffs) {

          
          found = true;
          forward = false;
          break;
        } 
      } 
      t = t.getNextToken();
    } 
    
    if (!found) {
      return;
    }
    
    if (forward) {
      
      t = t.getNextToken().getNextToken();

      
      while (true) {
        if (t != null && t.isPaintable()) {
          if (t.getType() == 25) {
            if (t.is(CLOSE_TAG_START)) {
              Token match = t.getNextToken();
              if (match != null && match.is(lexeme)) {
                if (depth > 0) {
                  depth--;
                } else {
                  
                  try {
                    int end = match.getOffset() + match.length();
                    h.addMarkedOccurrenceHighlight(match.getOffset(), end, p);
                    end = tokenOffs + match.length();
                    h.addMarkedOccurrenceHighlight(tokenOffs, end, p);
                  } catch (BadLocationException ble) {
                    ble.printStackTrace();
                  } 
                  
                  return;
                } 
              }
            } else if (t.isSingleChar('<')) {
              t = t.getNextToken();
              if (t != null && t.is(lexeme)) {
                depth++;
              }
            } 
          }
          t = (t == null) ? null : t.getNextToken();
          continue;
        } 
        if (++curLine < lineCount) {
          t = doc.getTokenListForLine(curLine);
        }
        
        if (curLine >= lineCount)
        {
          break;
        
        }
      
      }

    
    }
    else {
      
      List<Entry> openCloses = new ArrayList<>();
      boolean inPossibleMatch = false;
      t = doc.getTokenListForLine(curLine);
      int endBefore = tokenOffs - 2;

      
      while (true) {
        if (t != null && t.getOffset() < endBefore && t.isPaintable()) {
          if (t.getType() == 25) {
            if (t.isSingleChar('<')) {
              Token next = t.getNextToken();
              if (next != null) {
                if (next.is(lexeme)) {
                  openCloses.add(new Entry(true, next));
                  inPossibleMatch = true;
                } else {
                  
                  inPossibleMatch = false;
                } 
                t = next;
              }
            
            } else if (t.isSingleChar('>')) {
              inPossibleMatch = false;
            }
            else if (inPossibleMatch && t.is(TAG_SELF_CLOSE)) {
              openCloses.remove(openCloses.size() - 1);
              inPossibleMatch = false;
            }
            else if (t.is(CLOSE_TAG_START)) {
              Token next = t.getNextToken();
              if (next != null) {
                
                if (next.is(lexeme)) {
                  openCloses.add(new Entry(false, next));
                }
                t = next;
              } 
            } 
          }
          t = t.getNextToken();
          continue;
        } 
        for (int i = openCloses.size() - 1; i >= 0; i--) {
          Entry entry = openCloses.get(i);
          depth += entry.open ? -1 : 1;
          if (depth == -1) {
            try {
              Token match = entry.t;
              int end = match.getOffset() + match.length();
              h.addMarkedOccurrenceHighlight(match.getOffset(), end, p);
              end = tokenOffs + match.length();
              h.addMarkedOccurrenceHighlight(tokenOffs, end, p);
            } catch (BadLocationException ble) {
              ble.printStackTrace();
            } 
            openCloses.clear();
            
            return;
          } 
        } 
        openCloses.clear();
        if (--curLine >= 0) {
          t = doc.getTokenListForLine(curLine);
        }
        
        if (curLine < 0) {
          break;
        }
      } 
    } 
  }


  
  private static class Entry
  {
    private boolean open;
    
    private Token t;

    
    Entry(boolean open, Token t) {
      this.open = open;
      this.t = t;
    }
  }
}
