package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.swing.text.TabExpander;













































class VisibleWhitespaceTokenPainter
  extends DefaultTokenPainter
{
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
    
    int ascent = fm.getAscent();
    int height = fm.getHeight();
    
    for (int i = textOffs; i < end; i++) {
      float nextNextX; int halfHeight; int quarterHeight; int ymid; int width; int dotX; int dotY;
      switch (text[i]) {


        
        case '\t':
          nextX = x + fm.charsWidth(text, flushIndex, flushLen);
          nextNextX = e.nextTabStop(nextX, 0);
          if (bg != null) {
            paintBackground(x, y, nextNextX - x, height, g, ascent, host, bg);
          }
          
          g.setColor(fg);

          
          if (flushLen > 0) {
            g.drawChars(text, flushIndex, flushLen, (int)x, (int)y);
            flushLen = 0;
          } 
          flushIndex = i + 1;

          
          halfHeight = height / 2;
          quarterHeight = halfHeight / 2;
          ymid = (int)y - ascent + halfHeight;
          g.drawLine((int)nextX, ymid, (int)nextNextX, ymid);
          g.drawLine((int)nextNextX, ymid, (int)nextNextX - 4, ymid - quarterHeight);
          g.drawLine((int)nextNextX, ymid, (int)nextNextX - 4, ymid + quarterHeight);
          
          x = nextNextX;
          break;











        
        case ' ':
          nextX = x + fm.charsWidth(text, flushIndex, flushLen + 1);
          width = fm.charWidth(' ');

          
          if (bg != null) {
            paintBackground(x, y, nextX - x, height, g, ascent, host, bg);
          }
          
          g.setColor(fg);

          
          if (flushLen > 0) {
            g.drawChars(text, flushIndex, flushLen, (int)x, (int)y);
            flushLen = 0;
          } 

          
          dotX = (int)(nextX - width / 2.0F);
          dotY = (int)(y - ascent + height / 2.0F);
          g.drawLine(dotX, dotY, dotX, dotY);
          flushIndex = i + 1;
          x = nextX;
          break;





        
        default:
          flushLen++;
          break;
      } 

    
    } 
    nextX = x + fm.charsWidth(text, flushIndex, flushLen);
    
    if (flushLen > 0 && nextX >= clipStart) {
      if (bg != null) {
        paintBackground(x, y, nextX - x, height, g, ascent, host, bg);
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
}
