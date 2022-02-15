package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;

















public class LineNumberList
  extends AbstractGutterComponent
  implements MouseInputListener
{
  private int currentLine;
  private int lastY = -1;


  
  private int lastVisibleLine;


  
  private int cellHeight;


  
  private int cellWidth;


  
  private int ascent;


  
  private Map<?, ?> aaHints;


  
  private int mouseDragStartOffset;


  
  private Listener l;


  
  private Insets textAreaInsets;


  
  private Rectangle visibleRect;

  
  private int lineNumberingStartIndex;


  
  public LineNumberList(RTextArea textArea) {
    this(textArea, (Color)null);
  }










  
  public LineNumberList(RTextArea textArea, Color numberColor) {
    super(textArea);
    
    if (numberColor != null) {
      setForeground(numberColor);
    } else {
      
      setForeground(Color.GRAY);
    } 
  }








  
  public void addNotify() {
    super.addNotify();
    if (this.textArea != null) {
      this.l.install(this.textArea);
    }
    updateCellWidths();
    updateCellHeights();
  }






  
  private int calculateLastVisibleLineNumber() {
    int lastLine = 0;
    if (this.textArea != null) {
      lastLine = this.textArea.getLineCount() + getLineNumberingStartIndex() - 1;
    }
    return lastLine;
  }








  
  public int getLineNumberingStartIndex() {
    return this.lineNumberingStartIndex;
  }


  
  public Dimension getPreferredSize() {
    int h = (this.textArea != null) ? this.textArea.getHeight() : 100;
    return new Dimension(this.cellWidth, h);
  }







  
  private int getRhsBorderWidth() {
    int w = 4;
    if (this.textArea instanceof RSyntaxTextArea && (
      (RSyntaxTextArea)this.textArea).isCodeFoldingEnabled()) {
      w = 0;
    }
    
    return w;
  }


  
  void handleDocumentEvent(DocumentEvent e) {
    int newLastLine = calculateLastVisibleLineNumber();
    if (newLastLine != this.lastVisibleLine) {

      
      if (newLastLine / 10 != this.lastVisibleLine / 10) {
        updateCellWidths();
      }
      this.lastVisibleLine = newLastLine;
      repaint();
    } 
  }



  
  protected void init() {
    super.init();


    
    this.currentLine = 0;
    setLineNumberingStartIndex(1);
    
    this.visibleRect = new Rectangle();
    
    addMouseListener(this);
    addMouseMotionListener(this);
    
    this.aaHints = RSyntaxUtilities.getDesktopAntiAliasHints();
  }



  
  void lineHeightsChanged() {
    updateCellHeights();
  }



  
  public void mouseClicked(MouseEvent e) {}


  
  public void mouseDragged(MouseEvent e) {
    if (this.mouseDragStartOffset > -1) {
      int pos = this.textArea.viewToModel(new Point(0, e.getY()));
      if (pos >= 0) {
        this.textArea.setCaretPosition(this.mouseDragStartOffset);
        this.textArea.moveCaretPosition(pos);
      } 
    } 
  }



  
  public void mouseEntered(MouseEvent e) {}



  
  public void mouseExited(MouseEvent e) {}



  
  public void mouseMoved(MouseEvent e) {}


  
  public void mousePressed(MouseEvent e) {
    if (this.textArea == null) {
      return;
    }
    if (e.getButton() == 1) {
      int pos = this.textArea.viewToModel(new Point(0, e.getY()));
      if (pos >= 0) {
        this.textArea.setCaretPosition(pos);
      }
      this.mouseDragStartOffset = pos;
    } else {
      
      this.mouseDragStartOffset = -1;
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
    
    Color bg = getBackground();
    if (getGutter() != null) {
      bg = getGutter().getBackground();
    }
    g.setColor(bg);
    g.fillRect(0, this.visibleRect.y, this.cellWidth, this.visibleRect.height);
    g.setFont(getFont());
    if (this.aaHints != null) {
      ((Graphics2D)g).addRenderingHints(this.aaHints);
    }
    
    if (this.textArea.getLineWrap()) {
      paintWrappedLineNumbers(g, this.visibleRect);


      
      return;
    } 

    
    this.textAreaInsets = this.textArea.getInsets(this.textAreaInsets);
    if (this.visibleRect.y < this.textAreaInsets.top) {
      this.visibleRect.height -= this.textAreaInsets.top - this.visibleRect.y;
      this.visibleRect.y = this.textAreaInsets.top;
    } 
    int topLine = (this.visibleRect.y - this.textAreaInsets.top) / this.cellHeight;
    int actualTopY = topLine * this.cellHeight + this.textAreaInsets.top;
    int y = actualTopY + this.ascent;

    
    FoldManager fm = null;
    if (this.textArea instanceof RSyntaxTextArea) {
      fm = ((RSyntaxTextArea)this.textArea).getFoldManager();
      topLine += fm.getHiddenLineCountAbove(topLine, true);
    } 
    int rhsBorderWidth = getRhsBorderWidth();











    
    g.setColor(getForeground());
    boolean ltr = getComponentOrientation().isLeftToRight();
    if (ltr) {
      FontMetrics metrics = g.getFontMetrics();
      int rhs = getWidth() - rhsBorderWidth;
      int line = topLine + 1;
      while (y < this.visibleRect.y + this.visibleRect.height + this.ascent && line <= this.textArea.getLineCount()) {
        String number = Integer.toString(line + getLineNumberingStartIndex() - 1);
        int width = metrics.stringWidth(number);
        g.drawString(number, rhs - width, y);
        y += this.cellHeight;
        if (fm != null) {
          Fold fold = fm.getFoldForLine(line - 1);

          
          while (fold != null && fold.isCollapsed()) {
            int hiddenLineCount = fold.getLineCount();
            if (hiddenLineCount == 0) {
              break;
            }

            
            line += hiddenLineCount;
            fold = fm.getFoldForLine(line - 1);
          } 
        } 
        line++;
      } 
    } else {
      
      int line = topLine + 1;
      while (y < this.visibleRect.y + this.visibleRect.height && line < this.textArea.getLineCount()) {
        String number = Integer.toString(line + getLineNumberingStartIndex() - 1);
        g.drawString(number, rhsBorderWidth, y);
        y += this.cellHeight;
        if (fm != null) {
          Fold fold = fm.getFoldForLine(line - 1);

          
          while (fold != null && fold.isCollapsed()) {
            line += fold.getLineCount();
            fold = fm.getFoldForLine(line);
          } 
        } 
        line++;
      } 
    } 
  }







































  
  private void paintWrappedLineNumbers(Graphics g, Rectangle visibleRect) {
    int rhs, width = getWidth();
    
    RTextAreaUI ui = (RTextAreaUI)this.textArea.getUI();
    View v = ui.getRootView(this.textArea).getView(0);
    
    Document doc = this.textArea.getDocument();
    Element root = doc.getDefaultRootElement();
    int lineCount = root.getElementCount();
    int topPosition = this.textArea.viewToModel(new Point(visibleRect.x, visibleRect.y));
    
    int topLine = root.getElementIndex(topPosition);
    FoldManager fm = null;
    if (this.textArea instanceof RSyntaxTextArea) {
      fm = ((RSyntaxTextArea)this.textArea).getFoldManager();
    }




    
    Rectangle visibleEditorRect = ui.getVisibleEditorRect();
    Rectangle r = getChildViewBounds(v, topLine, visibleEditorRect);
    
    int y = r.y;
    int rhsBorderWidth = getRhsBorderWidth();
    
    boolean ltr = getComponentOrientation().isLeftToRight();
    if (ltr) {
      rhs = width - rhsBorderWidth;
    } else {
      
      rhs = rhsBorderWidth;
    } 
    int visibleBottom = visibleRect.y + visibleRect.height;
    FontMetrics metrics = g.getFontMetrics();


    
    g.setColor(getForeground());
    
    while (y < visibleBottom) {
      
      r = getChildViewBounds(v, topLine, visibleEditorRect);










      
      int index = topLine + 1 + getLineNumberingStartIndex() - 1;
      String number = Integer.toString(index);
      if (ltr) {
        int strWidth = metrics.stringWidth(number);
        g.drawString(number, rhs - strWidth, y + this.ascent);
      } else {
        
        int x = rhsBorderWidth;
        g.drawString(number, x, y + this.ascent);
      } 


      
      y += r.height;


      
      if (fm != null) {
        Fold fold = fm.getFoldForLine(topLine);
        if (fold != null && fold.isCollapsed()) {
          topLine += fold.getCollapsedLineCount();
        }
      } 
      topLine++;
      if (topLine >= lineCount) {
        break;
      }
    } 
  }







  
  public void removeNotify() {
    super.removeNotify();
    if (this.textArea != null) {
      this.l.uninstall(this.textArea);
    }
  }






  
  private void repaintLine(int line) {
    int y = (this.textArea.getInsets()).top;
    y += line * this.cellHeight;
    repaint(0, y, this.cellWidth, this.cellHeight);
  }








  
  public void setFont(Font font) {
    super.setFont(font);
    updateCellWidths();
    updateCellHeights();
  }









  
  public void setLineNumberingStartIndex(int index) {
    if (index != this.lineNumberingStartIndex) {
      this.lineNumberingStartIndex = index;
      updateCellWidths();
      repaint();
    } 
  }








  
  public void setTextArea(RTextArea textArea) {
    if (this.l == null) {
      this.l = new Listener();
    }
    
    if (this.textArea != null) {
      this.l.uninstall(textArea);
    }
    
    super.setTextArea(textArea);
    this.lastVisibleLine = calculateLastVisibleLineNumber();
    
    if (textArea != null) {
      this.l.install(textArea);
      updateCellHeights();
      updateCellWidths();
    } 
  }







  
  private void updateCellHeights() {
    if (this.textArea != null) {
      this.cellHeight = this.textArea.getLineHeight();
      this.ascent = this.textArea.getMaxAscent();
    } else {
      
      this.cellHeight = 20;
      this.ascent = 5;
    } 
    repaint();
  }






  
  void updateCellWidths() {
    int oldCellWidth = this.cellWidth;
    this.cellWidth = getRhsBorderWidth();

    
    if (this.textArea != null) {
      Font font = getFont();
      if (font != null) {
        FontMetrics fontMetrics = getFontMetrics(font);
        int count = 0;
        
        int lineCount = this.textArea.getLineCount() + getLineNumberingStartIndex() - 1;
        while (true) {
          lineCount /= 10;
          count++;
          if (lineCount < 10) {
            this.cellWidth += fontMetrics.charWidth('9') * (count + 1) + 3; break;
          } 
        } 
      } 
    }  if (this.cellWidth != oldCellWidth) {
      revalidate();
    }
  }


  
  private class Listener
    implements CaretListener, PropertyChangeListener
  {
    private boolean installed;

    
    private Listener() {}

    
    public void caretUpdate(CaretEvent e) {
      int dot = LineNumberList.this.textArea.getCaretPosition();








      
      if (!LineNumberList.this.textArea.getLineWrap()) {
        
        int line = LineNumberList.this.textArea.getDocument().getDefaultRootElement().getElementIndex(dot);
        if (LineNumberList.this.currentLine != line) {
          LineNumberList.this.repaintLine(line);
          LineNumberList.this.repaintLine(LineNumberList.this.currentLine);
          LineNumberList.this.currentLine = line;
        } 
      } else {
        
        try {
          int y = LineNumberList.this.textArea.yForLineContaining(dot);
          if (y != LineNumberList.this.lastY) {
            LineNumberList.this.lastY = y;
            LineNumberList.this.currentLine = LineNumberList.this.textArea.getDocument()
              .getDefaultRootElement().getElementIndex(dot);
            LineNumberList.this.repaint();
          } 
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        } 
      } 
    }

    
    public void install(RTextArea textArea) {
      if (!this.installed) {
        
        textArea.addCaretListener(this);
        textArea.addPropertyChangeListener(this);
        caretUpdate(null);
        this.installed = true;
      } 
    }


    
    public void propertyChange(PropertyChangeEvent e) {
      String name = e.getPropertyName();

      
      if ("RTA.currentLineHighlight".equals(name) || "RTA.currentLineHighlightColor"
        .equals(name)) {
        LineNumberList.this.repaintLine(LineNumberList.this.currentLine);
      }
    }

    
    public void uninstall(RTextArea textArea) {
      if (this.installed) {
        
        textArea.removeCaretListener(this);
        textArea.removePropertyChangeListener(this);
        this.installed = false;
      } 
    }
  }
}
