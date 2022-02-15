package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.TabExpander;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;





































public class SyntaxView
  extends View
  implements TabExpander, TokenOrientedView, RSTAView
{
  private Font font;
  private FontMetrics metrics;
  private Element longLine;
  private float longLineWidth;
  private int tabSize;
  private int tabBase;
  private RSyntaxTextArea host;
  private int lineHeight = 0;


  
  private int ascent;


  
  private int clipStart;

  
  private int clipEnd;

  
  private TokenImpl tempToken;


  
  public SyntaxView(Element elem) {
    super(elem);
    this.tempToken = new TokenImpl();
  }









  
  void calculateLongestLine() {
    Component c = getContainer();
    this.font = c.getFont();
    this.metrics = c.getFontMetrics(this.font);
    this.tabSize = getTabSize() * this.metrics.charWidth(' ');
    Element lines = getElement();
    int n = lines.getElementCount();
    for (int i = 0; i < n; i++) {
      Element line = lines.getElement(i);
      float w = getLineWidth(i);
      if (w > this.longLineWidth) {
        this.longLineWidth = w;
        this.longLine = line;
      } 
    } 
  }











  
  public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
    updateDamage(changes, a, f);
  }












  
  protected void damageLineRange(int line0, int line1, Shape a, Component host) {
    if (a != null) {
      Rectangle area0 = lineToRect(a, line0);
      Rectangle area1 = lineToRect(a, line1);
      if (area0 != null && area1 != null) {
        Rectangle dmg = area0.union(area1);
        host.repaint(dmg.x, dmg.y, dmg.width, dmg.height);
      } else {
        
        host.repaint();
      } 
    } 
  }
















  
  private float drawLine(TokenPainter painter, Token token, Graphics2D g, float x, float y, int line) {
    float nextX = x;
    boolean paintBG = this.host.getPaintTokenBackgrounds(line, y);
    
    while (token != null && token.isPaintable() && nextX < this.clipEnd) {
      nextX = painter.paint(token, g, nextX, y, this.host, this, this.clipStart, paintBG);
      
      token = token.getNextToken();
    } 


    
    if (this.host.getEOLMarkersVisible()) {
      g.setColor(this.host.getForegroundForTokenType(21));
      g.setFont(this.host.getFontForTokenType(21));
      g.drawString("¶", nextX, y);
    } 

    
    return nextX;
  }


















  
  private float drawLineWithSelection(TokenPainter painter, Token token, Graphics2D g, float x, float y, int selStart, int selEnd) {
    float nextX = x;
    boolean useSTC = this.host.getUseSelectedTextColor();
    
    while (token != null && token.isPaintable() && nextX < this.clipEnd) {

      
      if (token.containsPosition(selStart)) {
        
        if (selStart > token.getOffset()) {
          this.tempToken.copyFrom(token);
          this.tempToken.textCount = selStart - this.tempToken.getOffset();
          nextX = painter.paint(this.tempToken, g, nextX, y, this.host, this, this.clipStart);
          this.tempToken.textCount = token.length();
          this.tempToken.makeStartAt(selStart);

          
          token = new TokenImpl(this.tempToken);
        } 
        
        int tokenLen = token.length();
        int selCount = Math.min(tokenLen, selEnd - token.getOffset());
        if (selCount == tokenLen) {
          nextX = painter.paintSelected(token, g, nextX, y, this.host, this, this.clipStart, useSTC);
        }
        else {
          
          this.tempToken.copyFrom(token);
          this.tempToken.textCount = selCount;
          nextX = painter.paintSelected(this.tempToken, g, nextX, y, this.host, this, this.clipStart, useSTC);
          
          this.tempToken.textCount = token.length();
          this.tempToken.makeStartAt(token.getOffset() + selCount);
          token = this.tempToken;
          nextX = painter.paint(token, g, nextX, y, this.host, this, this.clipStart);

        
        }

      
      }
      else if (token.containsPosition(selEnd)) {
        this.tempToken.copyFrom(token);
        this.tempToken.textCount = selEnd - this.tempToken.getOffset();
        nextX = painter.paintSelected(this.tempToken, g, nextX, y, this.host, this, this.clipStart, useSTC);
        
        this.tempToken.textCount = token.length();
        this.tempToken.makeStartAt(selEnd);
        token = this.tempToken;
        nextX = painter.paint(token, g, nextX, y, this.host, this, this.clipStart);

      
      }
      else if (token.getOffset() >= selStart && token
        .getEndOffset() <= selEnd) {
        nextX = painter.paintSelected(token, g, nextX, y, this.host, this, this.clipStart, useSTC);
      
      }
      else {

        
        nextX = painter.paint(token, g, nextX, y, this.host, this, this.clipStart);
      } 
      
      token = token.getNextToken();
    } 



    
    if (this.host.getEOLMarkersVisible()) {
      g.setColor(this.host.getForegroundForTokenType(21));
      g.setFont(this.host.getFontForTokenType(21));
      g.drawString("¶", nextX, y);
    } 

    
    return nextX;
  }









  
  private float getLineWidth(int lineNumber) {
    Token tokenList = ((RSyntaxDocument)getDocument()).getTokenListForLine(lineNumber);
    return RSyntaxUtilities.getTokenListWidth(tokenList, (RSyntaxTextArea)
        getContainer(), this);
  }






















  
  public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet) throws BadLocationException {
    return RSyntaxUtilities.getNextVisualPositionFrom(pos, b, a, direction, biasRet, this);
  }












  
  public float getPreferredSpan(int axis) {
    float span;
    int visibleLineCount;
    updateMetrics();
    switch (axis) {
      case 0:
        span = this.longLineWidth + getRhsCorrection();
        if (this.host.getEOLMarkersVisible()) {
          span += this.metrics.charWidth('¶');
        }
        return span;


      
      case 1:
        this.lineHeight = (this.host != null) ? this.host.getLineHeight() : this.lineHeight;
        
        visibleLineCount = getElement().getElementCount();
        if (this.host.isCodeFoldingEnabled()) {
          visibleLineCount -= this.host.getFoldManager().getHiddenLineCount();
        }
        return visibleLineCount * this.lineHeight;
    } 
    throw new IllegalArgumentException("Invalid axis: " + axis);
  }








  
  private int getRhsCorrection() {
    int rhsCorrection = 10;
    if (this.host != null) {
      rhsCorrection = this.host.getRightHandSideCorrection();
    }
    return rhsCorrection;
  }






  
  private int getTabSize() {
    Integer i = (Integer)getDocument().getProperty("tabSize");
    
    int size = (i != null) ? i.intValue() : 5;
    return size;
  }














  
  public Token getTokenListForPhysicalLineAbove(int offset) {
    RSyntaxDocument document = (RSyntaxDocument)getDocument();
    Element map = document.getDefaultRootElement();
    int line = map.getElementIndex(offset);
    FoldManager fm = this.host.getFoldManager();
    if (fm == null) {
      line--;
      if (line >= 0) {
        return document.getTokenListForLine(line);
      }
    } else {
      
      line = fm.getVisibleLineAbove(line);
      if (line >= 0) {
        return document.getTokenListForLine(line);
      }
    } 


    
    return null;
  }














  
  public Token getTokenListForPhysicalLineBelow(int offset) {
    RSyntaxDocument document = (RSyntaxDocument)getDocument();
    Element map = document.getDefaultRootElement();
    int lineCount = map.getElementCount();
    int line = map.getElementIndex(offset);
    if (!this.host.isCodeFoldingEnabled()) {
      if (line < lineCount - 1) {
        return document.getTokenListForLine(line + 1);
      }
    } else {
      
      FoldManager fm = this.host.getFoldManager();
      line = fm.getVisibleLineBelow(line);
      if (line >= 0 && line < lineCount) {
        return document.getTokenListForLine(line);
      }
    } 



    
    return null;
  }










  
  public void insertUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
    updateDamage(changes, a, f);
  }








  
  protected Rectangle lineToRect(Shape a, int line) {
    Rectangle r = null;
    updateMetrics();
    if (this.metrics != null) {
      Rectangle alloc = a.getBounds();


      
      this.lineHeight = (this.host != null) ? this.host.getLineHeight() : this.lineHeight;
      if (this.host != null && this.host.isCodeFoldingEnabled()) {
        FoldManager fm = this.host.getFoldManager();
        int hiddenCount = fm.getHiddenLineCountAbove(line);
        line -= hiddenCount;
      } 
      r = new Rectangle(alloc.x, alloc.y + line * this.lineHeight, alloc.width, this.lineHeight);
    } 
    
    return r;
  }
















  
  public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
    Element map = getElement();
    RSyntaxDocument doc = (RSyntaxDocument)getDocument();
    int lineIndex = map.getElementIndex(pos);
    Token tokenList = doc.getTokenListForLine(lineIndex);
    Rectangle lineArea = lineToRect(a, lineIndex);
    this.tabBase = lineArea.x;





    
    lineArea = tokenList.listOffsetToView((RSyntaxTextArea)
        getContainer(), this, pos, this.tabBase, lineArea);

    
    return lineArea;
  }









































  
  public Shape modelToView(int p0, Position.Bias b0, int p1, Position.Bias b1, Shape a) throws BadLocationException {
    Shape s1, s0 = modelToView(p0, a, b0);
    
    if (p1 == getEndOffset()) {
      try {
        s1 = modelToView(p1, a, b1);
      } catch (BadLocationException ble) {
        s1 = null;
      } 
      if (s1 == null)
      {
        
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
        s1 = new Rectangle(alloc.x + alloc.width - 1, alloc.y, 1, alloc.height);
      }
    
    } else {
      
      s1 = modelToView(p1, a, b1);
    } 
    Rectangle r0 = (s0 instanceof Rectangle) ? (Rectangle)s0 : s0.getBounds();
    Rectangle r1 = (s1 instanceof Rectangle) ? (Rectangle)s1 : s1.getBounds();
    if (r0.y != r1.y) {

      
      Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
      r0.x = alloc.x;
      r0.width = alloc.width;
    } 
    
    r0.add(r1);






    
    if (p1 > p0) {
      r0.width -= r1.width;
    }
    
    return r0;
  }













  
  public float nextTabStop(float x, int tabOffset) {
    if (this.tabSize == 0) {
      return x;
    }
    int ntabs = ((int)x - this.tabBase) / this.tabSize;
    return this.tabBase + (ntabs + 1.0F) * this.tabSize;
  }










  
  public void paint(Graphics g, Shape a) {
    RSyntaxDocument document = (RSyntaxDocument)getDocument();
    
    Rectangle alloc = a.getBounds();
    
    this.tabBase = alloc.x;
    this.host = (RSyntaxTextArea)getContainer();
    
    Rectangle clip = g.getClipBounds();


    
    this.clipStart = clip.x;
    this.clipEnd = this.clipStart + clip.width;
    
    this.lineHeight = this.host.getLineHeight();
    this.ascent = this.host.getMaxAscent();
    int heightAbove = clip.y - alloc.y;
    int linesAbove = Math.max(0, heightAbove / this.lineHeight);
    
    FoldManager fm = this.host.getFoldManager();
    linesAbove += fm.getHiddenLineCountAbove(linesAbove, true);
    Rectangle lineArea = lineToRect(a, linesAbove);
    int y = lineArea.y + this.ascent;
    int x = lineArea.x;
    Element map = getElement();
    int lineCount = map.getElementCount();

    
    int selStart = this.host.getSelectionStart();
    int selEnd = this.host.getSelectionEnd();

    
    RSyntaxTextAreaHighlighter h = (RSyntaxTextAreaHighlighter)this.host.getHighlighter();
    
    Graphics2D g2d = (Graphics2D)g;


    
    TokenPainter painter = this.host.getTokenPainter();
    int line = linesAbove;
    
    while (y < clip.y + clip.height + this.ascent && line < lineCount) {
      
      Fold fold = fm.getFoldForLine(line);
      Element lineElement = map.getElement(line);
      int startOffset = lineElement.getStartOffset();

      
      int endOffset = lineElement.getEndOffset() - 1;
      h.paintLayeredHighlights(g2d, startOffset, endOffset, a, (JTextComponent)this.host, this);


      
      Token token = document.getTokenListForLine(line);
      if (selStart == selEnd || startOffset >= selEnd || endOffset < selStart) {
        
        drawLine(painter, token, g2d, x, y, line);
      }
      else {
        
        drawLineWithSelection(painter, token, g2d, x, y, selStart, selEnd);
      } 


      
      h.paintParserHighlights(g2d, startOffset, endOffset, a, (JTextComponent)this.host, this);

      
      if (fold != null && fold.isCollapsed()) {

        
        Color c = RSyntaxUtilities.getFoldedLineBottomColor(this.host);
        if (c != null) {
          g.setColor(c);
          g.drawLine(x, y + this.lineHeight - this.ascent - 1, this.host
              .getWidth(), y + this.lineHeight - this.ascent - 1);
        } 


        
        do {
          int hiddenLineCount = fold.getLineCount();
          if (hiddenLineCount == 0) {
            break;
          }


          
          line += hiddenLineCount;
          fold = fm.getFoldForLine(line);
        } while (fold != null && fold.isCollapsed());
      } 

      
      y += this.lineHeight;
      line++;
    } 
  }














  
  private boolean possiblyUpdateLongLine(Element line, int lineNumber) {
    float w = getLineWidth(lineNumber);
    if (w > this.longLineWidth) {
      this.longLineWidth = w;
      this.longLine = line;
      return true;
    } 
    return false;
  }










  
  public void removeUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
    updateDamage(changes, a, f);
  }


  
  public void setSize(float width, float height) {
    super.setSize(width, height);
    updateMetrics();
  }









  
  protected void updateDamage(DocumentEvent changes, Shape a, ViewFactory f) {
    Component host = getContainer();
    updateMetrics();
    Element elem = getElement();
    DocumentEvent.ElementChange ec = changes.getChange(elem);
    Element[] added = (ec != null) ? ec.getChildrenAdded() : null;
    Element[] removed = (ec != null) ? ec.getChildrenRemoved() : null;
    if ((added != null && added.length > 0) || (removed != null && removed.length > 0)) {

      
      if (added != null) {
        int addedAt = ec.getIndex();
        for (int i = 0; i < added.length; i++) {
          possiblyUpdateLongLine(added[i], addedAt + i);
        }
      } 
      if (removed != null) {
        for (Element element : removed) {
          if (element == this.longLine) {
            this.longLineWidth = -1.0F;
            calculateLongestLine();
            break;
          } 
        } 
      }
      preferenceChanged(null, true, true);
      host.repaint();


    
    }
    else if (changes.getType() == DocumentEvent.EventType.CHANGE) {
      
      int startLine = changes.getOffset();
      int endLine = changes.getLength();
      damageLineRange(startLine, endLine, a, host);
    }
    else {
      
      Element map = getElement();
      int line = map.getElementIndex(changes.getOffset());
      damageLineRange(line, line, a, host);
      if (changes.getType() == DocumentEvent.EventType.INSERT) {

        
        Element e = map.getElement(line);
        if (e == this.longLine) {

          
          this.longLineWidth = getLineWidth(line);
          preferenceChanged(null, true, false);

        
        }
        else if (possiblyUpdateLongLine(e, line)) {
          preferenceChanged(null, true, false);
        }
      
      }
      else if (changes.getType() == DocumentEvent.EventType.REMOVE && 
        map.getElement(line) == this.longLine) {
        
        this.longLineWidth = -1.0F;
        calculateLongestLine();
        preferenceChanged(null, true, false);
      } 
    } 
  }





  
  private void updateMetrics() {
    this.host = (RSyntaxTextArea)getContainer();
    Font f = this.host.getFont();
    if (this.font != f)
    {
      
      calculateLongestLine();
    }
  }













  
  public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
    bias[0] = Position.Bias.Forward;
    
    Rectangle alloc = a.getBounds();
    RSyntaxDocument doc = (RSyntaxDocument)getDocument();
    int x = (int)fx;
    int y = (int)fy;



    
    if (y < alloc.y) {
      return getStartOffset();
    }


    
    if (y > alloc.y + alloc.height) {
      return this.host.getLastVisibleOffset();
    }







    
    Element map = doc.getDefaultRootElement();
    this.lineHeight = this.host.getLineHeight();
    int lineIndex = Math.abs((y - alloc.y) / this.lineHeight);
    FoldManager fm = this.host.getFoldManager();
    
    lineIndex += fm.getHiddenLineCountAbove(lineIndex, true);
    
    if (lineIndex >= map.getElementCount()) {
      return this.host.getLastVisibleOffset();
    }
    
    Element line = map.getElement(lineIndex);

    
    if (x < alloc.x) {
      return line.getStartOffset();
    }
    if (x > alloc.x + alloc.width) {
      return line.getEndOffset() - 1;
    }

    
    int p0 = line.getStartOffset();
    Token tokenList = doc.getTokenListForLine(lineIndex);
    this.tabBase = alloc.x;
    int offs = tokenList.getListOffset((RSyntaxTextArea)
        getContainer(), this, this.tabBase, x);
    
    return (offs != -1) ? offs : p0;
  }








  
  public int yForLine(Rectangle alloc, int line) throws BadLocationException {
    updateMetrics();
    if (this.metrics != null) {


      
      this.lineHeight = (this.host != null) ? this.host.getLineHeight() : this.lineHeight;
      if (this.host != null) {
        FoldManager fm = this.host.getFoldManager();
        if (!fm.isLineHidden(line)) {
          line -= fm.getHiddenLineCountAbove(line);
          return alloc.y + line * this.lineHeight;
        } 
      } 
    } 
    
    return -1;
  }




  
  public int yForLineContaining(Rectangle alloc, int offs) throws BadLocationException {
    Element map = getElement();
    int line = map.getElementIndex(offs);
    return yForLine(alloc, line);
  }
}
