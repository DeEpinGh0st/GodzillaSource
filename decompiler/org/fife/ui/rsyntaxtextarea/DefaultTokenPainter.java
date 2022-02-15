package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import javax.swing.text.TabExpander;





























class DefaultTokenPainter
  implements TokenPainter
{
  private Rectangle2D.Float bgRect = new Rectangle2D.Float();

  
  private static char[] tabBuf;

  
  public final float paint(Token token, Graphics2D g, float x, float y, RSyntaxTextArea host, TabExpander e) {
    return paint(token, g, x, y, host, e, 0.0F);
  }



  
  public float paint(Token token, Graphics2D g, float x, float y, RSyntaxTextArea host, TabExpander e, float clipStart) {
    return paintImpl(token, g, x, y, host, e, clipStart, false, false);
  }




  
  public float paint(Token token, Graphics2D g, float x, float y, RSyntaxTextArea host, TabExpander e, float clipStart, boolean paintBG) {
    return paintImpl(token, g, x, y, host, e, clipStart, !paintBG, false);
  }
















  
  protected void paintBackground(float x, float y, float width, float height, Graphics2D g, int fontAscent, RSyntaxTextArea host, Color color) {
    g.setColor(color);
    this.bgRect.setRect(x, y - fontAscent, width, height);
    
    g.fillRect((int)x, (int)(y - fontAscent), (int)width, (int)height);
  }







  
  protected float paintImpl(Token token, Graphics2D g, float x, float y, RSyntaxTextArea host, TabExpander e, float clipStart, boolean selected, boolean useSTC) {
    int origX = (int)x;
    int textOffs = token.getTextOffset();
    char[] text = token.getTextArray();
    int end = textOffs + token.length();
    float nextX = x;
    int flushLen = 0;
    int flushIndex = textOffs;
    
    Color fg = useSTC ? host.getSelectedTextColor() : host.getForegroundForToken(token);
    Color bg = selected ? null : host.getBackgroundForToken(token);
    g.setFont(host.getFontForTokenType(token.getType()));
    FontMetrics fm = host.getFontMetricsForTokenType(token.getType());
    
    for (int i = textOffs; i < end; i++) {
      switch (text[i]) {
        case '\t':
          nextX = e.nextTabStop(x + fm
              .charsWidth(text, flushIndex, flushLen), 0);
          if (bg != null) {
            paintBackground(x, y, nextX - x, fm.getHeight(), g, fm
                .getAscent(), host, bg);
          }
          if (flushLen > 0) {
            g.setColor(fg);
            g.drawChars(text, flushIndex, flushLen, (int)x, (int)y);
            flushLen = 0;
          } 
          flushIndex = i + 1;
          x = nextX;
          break;
        default:
          flushLen++;
          break;
      } 
    
    } 
    nextX = x + fm.charsWidth(text, flushIndex, flushLen);
    Rectangle r = host.getMatchRectangle();
    
    if (flushLen > 0 && nextX >= clipStart) {
      if (bg != null) {
        paintBackground(x, y, nextX - x, fm.getHeight(), g, fm
            .getAscent(), host, bg);
        if (token.length() == 1 && r != null && r.x == x) {
          ((RSyntaxTextAreaUI)host.getUI()).paintMatchedBracketImpl(g, host, r);
        }
      } 
      
      g.setColor(fg);
      g.drawChars(text, flushIndex, flushLen, (int)x, (int)y);
    } 
    
    if (host.getUnderlineForToken(token)) {
      g.setColor(fg);
      int y2 = (int)(y + 1.0F);
      g.drawLine(origX, y2, (int)nextX, y2);
    } 



    
    if (host.getPaintTabLines() && origX == (host.getMargin()).left) {
      paintTabLines(token, origX, (int)y, (int)nextX, g, e, host);
    }
    
    return nextX;
  }




  
  public float paintSelected(Token token, Graphics2D g, float x, float y, RSyntaxTextArea host, TabExpander e, boolean useSTC) {
    return paintSelected(token, g, x, y, host, e, 0.0F, useSTC);
  }




  
  public float paintSelected(Token token, Graphics2D g, float x, float y, RSyntaxTextArea host, TabExpander e, float clipStart, boolean useSTC) {
    return paintImpl(token, g, x, y, host, e, clipStart, true, useSTC);
  }























  
  protected void paintTabLines(Token token, int x, int y, int endX, Graphics2D g, TabExpander e, RSyntaxTextArea host) {
    if (token.getType() != 21) {
      int offs = 0;
      for (; offs < token.length() && 
        RSyntaxUtilities.isWhitespace(token.charAt(offs)); offs++);


      
      if (offs < 2) {
        return;
      }
      
      endX = (int)token.getWidthUpTo(offs, host, e, 0.0F);
    } 

    
    FontMetrics fm = host.getFontMetricsForTokenType(token.getType());
    int tabSize = host.getTabSize();
    if (tabBuf == null || tabBuf.length < tabSize) {
      tabBuf = new char[tabSize];
      for (int i = 0; i < tabSize; i++) {
        tabBuf[i] = ' ';
      }
    } 




    
    int tabW = fm.charsWidth(tabBuf, 0, tabSize);


    
    g.setColor(host.getTabLineColor());
    int x0 = x + tabW;
    int y0 = y - fm.getAscent();
    if ((y0 & 0x1) > 0)
    {
      y0++;
    }

    
    Token next = token.getNextToken();
    if (next == null || !next.isPaintable()) {
      endX++;
    }
    while (x0 < endX) {
      int y1 = y0;
      int y2 = y0 + host.getLineHeight();
      while (y1 < y2) {
        g.drawLine(x0, y1, x0, y1);
        y1 += 2;
      } 
      
      x0 += tabW;
    } 
  }
}
