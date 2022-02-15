package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rtextarea.IconRowHeader;
























public class FoldingAwareIconRowHeader
  extends IconRowHeader
{
  public FoldingAwareIconRowHeader(RSyntaxTextArea textArea) {
    super(textArea);
  }




  
  protected void paintComponent(Graphics g) {
    if (this.textArea == null) {
      return;
    }
    RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
    FoldManager fm = rsta.getFoldManager();
    if (!fm.isCodeFoldingSupportedAndEnabled()) {
      super.paintComponent(g);
      
      return;
    } 
    this.visibleRect = g.getClipBounds(this.visibleRect);
    if (this.visibleRect == null) {
      this.visibleRect = getVisibleRect();
    }
    
    if (this.visibleRect == null) {
      return;
    }
    paintBackgroundImpl(g, this.visibleRect);
    
    if (this.textArea.getLineWrap()) {
      paintComponentWrapped(g);
      
      return;
    } 
    Document doc = this.textArea.getDocument();
    Element root = doc.getDefaultRootElement();
    this.textAreaInsets = this.textArea.getInsets(this.textAreaInsets);
    if (this.visibleRect.y < this.textAreaInsets.top) {
      this.visibleRect.height -= this.textAreaInsets.top - this.visibleRect.y;
      this.visibleRect.y = this.textAreaInsets.top;
    } 

    
    int cellHeight = this.textArea.getLineHeight();
    int topLine = (this.visibleRect.y - this.textAreaInsets.top) / cellHeight;



    
    int y = topLine * cellHeight + this.textAreaInsets.top;

    
    topLine += fm.getHiddenLineCountAbove(topLine, true);

    
    if (this.activeLineRangeStart > -1 && this.activeLineRangeEnd > -1) {
      Color activeLineRangeColor = getActiveLineRangeColor();
      g.setColor(activeLineRangeColor);
      
      try {
        int realY1 = rsta.yForLine(this.activeLineRangeStart);
        if (realY1 > -1)
        {
          int y1 = realY1;
          
          int y2 = rsta.yForLine(this.activeLineRangeEnd);
          if (y2 == -1) {
            y2 = y1;
          }
          y2 += cellHeight - 1;
          
          if (y2 < this.visibleRect.y || y1 > this.visibleRect.y + this.visibleRect.height) {
            return;
          }
          
          y1 = Math.max(y, realY1);
          y2 = Math.min(y2, this.visibleRect.y + this.visibleRect.height);

          
          int j = y1;
          while (j <= y2) {
            int yEnd = Math.min(y2, j + getWidth());
            int xEnd = yEnd - j;
            g.drawLine(0, j, xEnd, yEnd);
            j += 2;
          } 
          
          int i = 2;
          while (i < getWidth()) {
            int yEnd = y1 + getWidth() - i;
            g.drawLine(i, y1, getWidth(), yEnd);
            i += 2;
          } 
          
          if (realY1 >= y && realY1 < this.visibleRect.y + this.visibleRect.height) {
            g.drawLine(0, realY1, getWidth(), realY1);
          }
          if (y2 >= y && y2 < this.visibleRect.y + this.visibleRect.height) {
            g.drawLine(0, y2, getWidth(), y2);
          }
        }
      
      }
      catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } 

    
    if (this.trackingIcons != null) {
      int lastLine = this.textArea.getLineCount() - 1;
      for (int i = this.trackingIcons.size() - 1; i >= 0; i--) {
        IconRowHeader.GutterIconImpl gutterIconImpl = getTrackingIcon(i);
        int offs = gutterIconImpl.getMarkedOffset();
        if (offs >= 0 && offs <= doc.getLength()) {
          int line = root.getElementIndex(offs);
          if (line <= lastLine && line >= topLine) {
            try {
              Icon icon = gutterIconImpl.getIcon();
              if (icon != null) {
                int lineY = rsta.yForLine(line);
                if (lineY >= y && lineY <= this.visibleRect.y + this.visibleRect.height) {
                  int y2 = lineY + (cellHeight - icon.getIconHeight()) / 2;
                  icon.paintIcon((Component)this, g, 0, y2);
                  lastLine = line - 1;
                } 
              } 
            } catch (BadLocationException ble) {
              ble.printStackTrace();
            }
          
          } else if (line < topLine) {
            break;
          } 
        } 
      } 
    } 
  }






















  
  private void paintComponentWrapped(Graphics g) {
    RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
    
    Document doc = this.textArea.getDocument();
    Element root = doc.getDefaultRootElement();
    int topPosition = this.textArea.viewToModel(new Point(this.visibleRect.x, this.visibleRect.y));
    
    int topLine = root.getElementIndex(topPosition);
    
    int topY = this.visibleRect.y;
    int bottomY = this.visibleRect.y + this.visibleRect.height;
    int cellHeight = this.textArea.getLineHeight();

    
    if (this.trackingIcons != null) {
      int lastLine = this.textArea.getLineCount() - 1;
      for (int i = this.trackingIcons.size() - 1; i >= 0; i--) {
        IconRowHeader.GutterIconImpl gutterIconImpl = getTrackingIcon(i);
        Icon icon = gutterIconImpl.getIcon();
        if (icon != null) {
          int iconH = icon.getIconHeight();
          int offs = gutterIconImpl.getMarkedOffset();
          if (offs >= 0 && offs <= doc.getLength()) {
            int line = root.getElementIndex(offs);
            if (line <= lastLine && line >= topLine) {
              try {
                int lineY = rsta.yForLine(line);
                if (lineY <= bottomY && lineY + iconH >= topY) {
                  int y2 = lineY + (cellHeight - iconH) / 2;
                  gutterIconImpl.getIcon().paintIcon((Component)this, g, 0, y2);
                  lastLine = line - 1;
                } 
              } catch (BadLocationException ble) {
                ble.printStackTrace();
              }
            
            } else if (line < topLine) {
              break;
            } 
          } 
        } 
      } 
    } 
  }
}
