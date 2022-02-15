package org.fife.ui.rsyntaxtextarea;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import org.fife.ui.rtextarea.SmartHighlightPainter;

















public class XmlOccurrenceMarker
  implements OccurrenceMarker
{
  private static final char[] CLOSE_TAG_START = new char[] { '<', '/' };
  private static final char[] TAG_SELF_CLOSE = new char[] { '/', '>' };


  
  public Token getTokenToMark(RSyntaxTextArea textArea) {
    return HtmlOccurrenceMarker.getTagNameTokenForCaretOffset(textArea, this);
  }



  
  public boolean isValidType(RSyntaxTextArea textArea, Token t) {
    return textArea.getMarkOccurrencesOfTokenType(t.getType());
  }




  
  public void markOccurrences(RSyntaxDocument doc, Token t, RSyntaxTextAreaHighlighter h, SmartHighlightPainter p) {
    char[] lexeme = t.getLexeme().toCharArray();
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
          found = true;
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
