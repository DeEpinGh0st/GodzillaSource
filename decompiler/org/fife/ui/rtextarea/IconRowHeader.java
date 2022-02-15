package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.View;
















































































public class IconRowHeader
  extends AbstractGutterComponent
  implements MouseListener
{
  protected List<GutterIconImpl> trackingIcons;
  protected int width;
  private boolean bookmarkingEnabled;
  private Icon bookmarkIcon;
  protected Rectangle visibleRect;
  protected Insets textAreaInsets;
  protected int activeLineRangeStart;
  protected int activeLineRangeEnd;
  private Color activeLineRangeColor;
  private boolean inheritsGutterBackground;
  
  public IconRowHeader(RTextArea textArea) {
    super(textArea);
  }














  
  public GutterIconInfo addOffsetTrackingIcon(int offs, Icon icon) throws BadLocationException {
    return addOffsetTrackingIcon(offs, icon, (String)null);
  }


















  
  public GutterIconInfo addOffsetTrackingIcon(int offs, Icon icon, String tip) throws BadLocationException {
    if (offs < 0 || offs > this.textArea.getDocument().getLength()) {
      throw new BadLocationException("Offset " + offs + " not in required range of 0-" + this.textArea
          .getDocument().getLength(), offs);
    }
    
    Position pos = this.textArea.getDocument().createPosition(offs);
    GutterIconImpl ti = new GutterIconImpl(icon, pos, tip);
    if (this.trackingIcons == null) {
      this.trackingIcons = new ArrayList<>(1);
    }
    int index = Collections.binarySearch((List)this.trackingIcons, ti);
    if (index < 0) {
      index = -(index + 1);
    }
    this.trackingIcons.add(index, ti);
    repaint();
    return ti;
  }






  
  public void clearActiveLineRange() {
    if (this.activeLineRangeStart != -1 || this.activeLineRangeEnd != -1) {
      this.activeLineRangeStart = this.activeLineRangeEnd = -1;
      repaint();
    } 
  }







  
  public Color getActiveLineRangeColor() {
    return this.activeLineRangeColor;
  }









  
  public Icon getBookmarkIcon() {
    return this.bookmarkIcon;
  }








  
  public GutterIconInfo[] getBookmarks() {
    List<GutterIconInfo> retVal = new ArrayList<>(1);
    
    if (this.trackingIcons != null) {
      for (int i = 0; i < this.trackingIcons.size(); i++) {
        GutterIconImpl ti = getTrackingIcon(i);
        if (ti.getIcon() == this.bookmarkIcon) {
          retVal.add(ti);
        }
      } 
    }
    
    GutterIconInfo[] array = new GutterIconInfo[retVal.size()];
    return retVal.<GutterIconInfo>toArray(array);
  }



  
  void handleDocumentEvent(DocumentEvent e) {
    int newLineCount = this.textArea.getLineCount();
    if (newLineCount != this.currentLineCount) {
      this.currentLineCount = newLineCount;
      repaint();
    } 
  }


  
  public Dimension getPreferredSize() {
    int h = (this.textArea != null) ? this.textArea.getHeight() : 100;
    return new Dimension(this.width, h);
  }







  
  public String getToolTipText(MouseEvent e) {
    try {
      int line = viewToModelLine(e.getPoint());
      if (line > -1) {
        GutterIconInfo[] infos = getTrackingIcons(line);
        if (infos.length > 0)
        {
          return infos[infos.length - 1].getToolTip();
        }
      } 
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    return null;
  }

  
  protected GutterIconImpl getTrackingIcon(int index) {
    return this.trackingIcons.get(index);
  }











  
  public GutterIconInfo[] getTrackingIcons(int line) throws BadLocationException {
    List<GutterIconInfo> retVal = new ArrayList<>(1);
    
    if (this.trackingIcons != null) {
      int start = this.textArea.getLineStartOffset(line);
      int end = this.textArea.getLineEndOffset(line);
      if (line == this.textArea.getLineCount() - 1) {
        end++;
      }
      for (int i = 0; i < this.trackingIcons.size(); i++) {
        GutterIconImpl ti = getTrackingIcon(i);
        int offs = ti.getMarkedOffset();
        if (offs >= start && offs < end) {
          retVal.add(ti);
        }
        else if (offs >= end) {
          break;
        } 
      } 
    } 
    
    GutterIconInfo[] array = new GutterIconInfo[retVal.size()];
    return retVal.<GutterIconInfo>toArray(array);
  }




  
  protected void init() {
    super.init();
    
    this.visibleRect = new Rectangle();
    this.width = 16;
    addMouseListener(this);
    this.activeLineRangeStart = this.activeLineRangeEnd = -1;
    setActiveLineRangeColor((Color)null);


    
    updateBackground();
    
    ToolTipManager.sharedInstance().registerComponent(this);
  }








  
  public boolean isBookmarkingEnabled() {
    return this.bookmarkingEnabled;
  }


  
  void lineHeightsChanged() {
    repaint();
  }



  
  public void mouseClicked(MouseEvent e) {}



  
  public void mouseEntered(MouseEvent e) {}



  
  public void mouseExited(MouseEvent e) {}


  
  public void mousePressed(MouseEvent e) {
    if (this.bookmarkingEnabled && this.bookmarkIcon != null) {
      try {
        int line = viewToModelLine(e.getPoint());
        if (line > -1) {
          toggleBookmark(line);
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }
  }



  
  public void mouseReleased(MouseEvent e) {}



  
  protected void paintComponent(Graphics g) {
    if (this.textArea == null) {
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
    int bottomLine = Math.min(topLine + this.visibleRect.height / cellHeight + 1, root
        .getElementCount());



    
    int y = topLine * cellHeight + this.textAreaInsets.top;
    
    if ((this.activeLineRangeStart >= topLine && this.activeLineRangeStart <= bottomLine) || (this.activeLineRangeEnd >= topLine && this.activeLineRangeEnd <= bottomLine) || (this.activeLineRangeStart <= topLine && this.activeLineRangeEnd >= bottomLine)) {


      
      g.setColor(this.activeLineRangeColor);
      int firstLine = Math.max(this.activeLineRangeStart, topLine);
      int y1 = firstLine * cellHeight + this.textAreaInsets.top;
      int lastLine = Math.min(this.activeLineRangeEnd, bottomLine);
      int y2 = (lastLine + 1) * cellHeight + this.textAreaInsets.top - 1;
      
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
      
      if (firstLine == this.activeLineRangeStart) {
        g.drawLine(0, y1, getWidth(), y1);
      }
      if (lastLine == this.activeLineRangeEnd) {
        g.drawLine(0, y2, getWidth(), y2);
      }
    } 

    
    if (this.trackingIcons != null) {
      int lastLine = bottomLine;
      for (int i = this.trackingIcons.size() - 1; i >= 0; i--) {
        GutterIconInfo ti = getTrackingIcon(i);
        int offs = ti.getMarkedOffset();
        if (offs >= 0 && offs <= doc.getLength()) {
          int line = root.getElementIndex(offs);
          if (line <= lastLine && line >= topLine) {
            Icon icon = ti.getIcon();
            if (icon != null) {
              int y2 = y + (line - topLine) * cellHeight;
              y2 += (cellHeight - icon.getIconHeight()) / 2;
              ti.getIcon().paintIcon(this, g, 0, y2);
              lastLine = line - 1;
            }
          
          } else if (line < topLine) {
            break;
          } 
        } 
      } 
    } 
  }








  
  protected void paintBackgroundImpl(Graphics g, Rectangle visibleRect) {
    Color bg = getBackground();
    if (this.inheritsGutterBackground && getGutter() != null) {
      bg = getGutter().getBackground();
    }
    g.setColor(bg);
    g.fillRect(0, visibleRect.y, this.width, visibleRect.height);
  }




































  
  private void paintComponentWrapped(Graphics g) {
    RTextAreaUI ui = (RTextAreaUI)this.textArea.getUI();
    View v = ui.getRootView(this.textArea).getView(0);
    
    Document doc = this.textArea.getDocument();
    Element root = doc.getDefaultRootElement();
    int lineCount = root.getElementCount();
    int topPosition = this.textArea.viewToModel(new Point(this.visibleRect.x, this.visibleRect.y));
    
    int topLine = root.getElementIndex(topPosition);




    
    Rectangle visibleEditorRect = ui.getVisibleEditorRect();
    Rectangle r = getChildViewBounds(v, topLine, visibleEditorRect);
    
    int y = r.y;
    
    int visibleBottom = this.visibleRect.y + this.visibleRect.height;

    
    int currentIcon = -1;
    if (this.trackingIcons != null) {
      for (int i = 0; i < this.trackingIcons.size(); i++) {
        GutterIconImpl icon = getTrackingIcon(i);
        int offs = icon.getMarkedOffset();
        if (offs >= 0 && offs <= doc.getLength()) {
          int line = root.getElementIndex(offs);
          if (line >= topLine) {
            currentIcon = i;

            
            break;
          } 
        } 
      } 
    }
    
    g.setColor(getForeground());
    int cellHeight = this.textArea.getLineHeight();
    while (y < visibleBottom) {
      
      r = getChildViewBounds(v, topLine, visibleEditorRect);











      
      if (currentIcon > -1) {
        
        GutterIconImpl toPaint = null;
        while (currentIcon < this.trackingIcons.size()) {
          GutterIconImpl ti = getTrackingIcon(currentIcon);
          int offs = ti.getMarkedOffset();
          if (offs >= 0 && offs <= doc.getLength()) {
            int line = root.getElementIndex(offs);
            if (line == topLine) {
              toPaint = ti;
            }
            else if (line > topLine) {
              break;
            } 
          } 
          currentIcon++;
        } 
        if (toPaint != null) {
          Icon icon = toPaint.getIcon();
          if (icon != null) {
            int y2 = y + (cellHeight - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g, 0, y2);
          } 
        } 
      } 


      
      y += r.height;


      
      topLine++;
      if (topLine >= lineCount) {
        break;
      }
    } 
  }










  
  public void removeTrackingIcon(GutterIconInfo tag) {
    if (this.trackingIcons != null && this.trackingIcons.remove(tag)) {
      repaint();
    }
  }







  
  public void removeAllTrackingIcons() {
    if (this.trackingIcons != null && this.trackingIcons.size() > 0) {
      this.trackingIcons.clear();
      repaint();
    } 
  }




  
  private void removeBookmarkTrackingIcons() {
    if (this.trackingIcons != null) {
      this.trackingIcons.removeIf(ti -> (ti.getIcon() == this.bookmarkIcon));
    }
  }








  
  public void setActiveLineRange(int startLine, int endLine) {
    if (startLine != this.activeLineRangeStart || endLine != this.activeLineRangeEnd) {
      
      this.activeLineRangeStart = startLine;
      this.activeLineRangeEnd = endLine;
      repaint();
    } 
  }









  
  public void setActiveLineRangeColor(Color color) {
    if (color == null) {
      color = Gutter.DEFAULT_ACTIVE_LINE_RANGE_COLOR;
    }
    if (!color.equals(this.activeLineRangeColor)) {
      this.activeLineRangeColor = color;
      repaint();
    } 
  }










  
  public void setBookmarkIcon(Icon icon) {
    removeBookmarkTrackingIcons();
    this.bookmarkIcon = icon;
    repaint();
  }











  
  public void setBookmarkingEnabled(boolean enabled) {
    if (enabled != this.bookmarkingEnabled) {
      this.bookmarkingEnabled = enabled;
      if (!enabled) {
        removeBookmarkTrackingIcons();
      }
      repaint();
    } 
  }









  
  public void setInheritsGutterBackground(boolean inherits) {
    if (inherits != this.inheritsGutterBackground) {
      this.inheritsGutterBackground = inherits;
      repaint();
    } 
  }








  
  public void setTextArea(RTextArea textArea) {
    removeAllTrackingIcons();
    super.setTextArea(textArea);
  }











  
  public boolean toggleBookmark(int line) throws BadLocationException {
    if (!isBookmarkingEnabled() || getBookmarkIcon() == null) {
      return false;
    }
    
    GutterIconInfo[] icons = getTrackingIcons(line);
    if (icons.length == 0) {
      int offs = this.textArea.getLineStartOffset(line);
      addOffsetTrackingIcon(offs, this.bookmarkIcon);
      return true;
    } 
    
    boolean found = false;
    for (GutterIconInfo icon : icons) {
      if (icon.getIcon() == this.bookmarkIcon) {
        removeTrackingIcon(icon);
        found = true;
      } 
    } 





    
    if (!found) {
      int offs = this.textArea.getLineStartOffset(line);
      addOffsetTrackingIcon(offs, this.bookmarkIcon);
    } 
    
    return !found;
  }







  
  private void updateBackground() {
    Color bg = UIManager.getColor("Panel.background");
    if (bg == null) {
      bg = (new JPanel()).getBackground();
    }
    setBackground(bg);
  }


  
  public void updateUI() {
    super.updateUI();
    updateBackground();
  }








  
  private int viewToModelLine(Point p) throws BadLocationException {
    int offs = this.textArea.viewToModel(p);
    return (offs > -1) ? this.textArea.getLineOfOffset(offs) : -1;
  }

  
  private static class GutterIconImpl
    implements GutterIconInfo, Comparable<GutterIconInfo>
  {
    private Icon icon;
    
    private Position pos;
    
    private String toolTip;

    
    GutterIconImpl(Icon icon, Position pos, String toolTip) {
      this.icon = icon;
      this.pos = pos;
      this.toolTip = toolTip;
    }

    
    public int compareTo(GutterIconInfo other) {
      if (other != null) {
        return this.pos.getOffset() - other.getMarkedOffset();
      }
      return -1;
    }

    
    public boolean equals(Object o) {
      return (o == this);
    }

    
    public Icon getIcon() {
      return this.icon;
    }

    
    public int getMarkedOffset() {
      return this.pos.getOffset();
    }

    
    public String getToolTip() {
      return this.toolTip;
    }

    
    public int hashCode() {
      return this.icon.hashCode();
    }
  }
}
