package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;


















class LineHighlightManager
{
  private RTextArea textArea;
  private List<LineHighlightInfo> lineHighlights;
  private LineHighlightInfoComparator comparator;
  
  LineHighlightManager(RTextArea textArea) {
    this.textArea = textArea;
    this.comparator = new LineHighlightInfoComparator();
  }












  
  public Object addLineHighlight(int line, Color color) throws BadLocationException {
    int offs = this.textArea.getLineStartOffset(line);
    
    LineHighlightInfo lhi = new LineHighlightInfo(this.textArea.getDocument().createPosition(offs), color);
    if (this.lineHighlights == null) {
      this.lineHighlights = new ArrayList<>(1);
    }
    int index = Collections.binarySearch(this.lineHighlights, lhi, this.comparator);
    if (index < 0) {
      index = -(index + 1);
    }
    this.lineHighlights.add(index, lhi);
    repaintLine(lhi);
    return lhi;
  }







  
  protected List<Object> getCurrentLineHighlightTags() {
    return (this.lineHighlights == null) ? Collections.<Object>emptyList() : new ArrayList(this.lineHighlights);
  }







  
  protected int getLineHighlightCount() {
    return (this.lineHighlights == null) ? 0 : this.lineHighlights.size();
  }







  
  public void paintLineHighlights(Graphics g) {
    int count = (this.lineHighlights == null) ? 0 : this.lineHighlights.size();
    if (count > 0) {
      
      int docLen = this.textArea.getDocument().getLength();
      Rectangle vr = this.textArea.getVisibleRect();
      int lineHeight = this.textArea.getLineHeight();

      
      try {
        for (int i = 0; i < count; i++) {
          LineHighlightInfo lhi = this.lineHighlights.get(i);
          int offs = lhi.getOffset();
          if (offs >= 0 && offs <= docLen) {
            int y = this.textArea.yForLineContaining(offs);
            if (y > vr.y - lineHeight) {
              if (y < vr.y + vr.height) {
                g.setColor(lhi.getColor());
                g.fillRect(0, y, this.textArea.getWidth(), lineHeight);
              }
              else {
                
                break;
              } 
            }
          } 
        } 
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } 
  }







  
  public void removeAllLineHighlights() {
    if (this.lineHighlights != null) {
      this.lineHighlights.clear();
      this.textArea.repaint();
    } 
  }







  
  public void removeLineHighlight(Object tag) {
    if (tag instanceof LineHighlightInfo) {
      this.lineHighlights.remove(tag);
      repaintLine((LineHighlightInfo)tag);
    } 
  }






  
  private void repaintLine(LineHighlightInfo lhi) {
    int offs = lhi.getOffset();
    
    if (offs >= 0 && offs <= this.textArea.getDocument().getLength()) {
      try {
        int y = this.textArea.yForLineContaining(offs);
        if (y > -1) {
          this.textArea.repaint(0, y, this.textArea
              .getWidth(), this.textArea.getLineHeight());
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }
  }


  
  private static class LineHighlightInfo
  {
    private Position offs;
    
    private Color color;

    
    LineHighlightInfo(Position offs, Color c) {
      this.offs = offs;
      this.color = c;
    }

    
    public boolean equals(Object other) {
      if (other instanceof LineHighlightInfo) {
        LineHighlightInfo lhi2 = (LineHighlightInfo)other;
        return (getOffset() == lhi2.getOffset() && 
          Objects.equals(getColor(), lhi2.getColor()));
      } 
      return false;
    }
    
    public Color getColor() {
      return this.color;
    }
    
    public int getOffset() {
      return this.offs.getOffset();
    }

    
    public int hashCode() {
      return getOffset();
    }
  }





  
  private static class LineHighlightInfoComparator
    implements Comparator<LineHighlightInfo>
  {
    private LineHighlightInfoComparator() {}




    
    public int compare(LineHighlightManager.LineHighlightInfo lhi1, LineHighlightManager.LineHighlightInfo lhi2) {
      if (lhi1.getOffset() < lhi2.getOffset()) {
        return -1;
      }
      return (lhi1.getOffset() == lhi2.getOffset()) ? 0 : 1;
    }
  }
}
