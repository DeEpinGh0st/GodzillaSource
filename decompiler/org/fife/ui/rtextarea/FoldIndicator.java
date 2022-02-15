package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.focusabletip.TipUtil;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;









































































public class FoldIndicator
  extends AbstractGutterComponent
{
  private Insets textAreaInsets;
  private Rectangle visibleRect;
  private Fold foldWithOutlineShowing;
  private Color foldIconBackground;
  private Color foldIconArmedBackground;
  private Icon collapsedFoldIcon;
  private Icon expandedFoldIcon;
  private boolean mouseOverFoldIcon;
  private boolean paintFoldArmed;
  private boolean showFoldRegionTips;
  private int additionalLeftMargin;
  public static final Color DEFAULT_FOREGROUND = Color.GRAY;



  
  public static final Color DEFAULT_FOLD_BACKGROUND = Color.WHITE;


  
  private Listener listener;


  
  private static final int WIDTH = 12;



  
  public FoldIndicator(RTextArea textArea) {
    super(textArea);
  }









  
  public JToolTip createToolTip() {
    JToolTip tip = super.createToolTip();
    tip.setBackground(TipUtil.getToolTipBackground(this.textArea));
    tip.setBorder(TipUtil.getToolTipBorder(this.textArea));
    return tip;
  }









  
  public int getAdditionalLeftMargin() {
    return this.additionalLeftMargin;
  }


  
  private Fold findOpenFoldClosestTo(Point p) {
    Fold fold = null;
    this.mouseOverFoldIcon = false;
    
    RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
    if (rsta.isCodeFoldingEnabled()) {
      int offs = rsta.viewToModel(p);
      if (offs > -1) {
        try {
          int line = rsta.getLineOfOffset(offs);
          FoldManager fm = rsta.getFoldManager();
          fold = fm.getFoldForLine(line);
          if (fold != null) {
            
            this.mouseOverFoldIcon = true;
          } else {
            
            fold = fm.getDeepestOpenFoldContaining(offs);
          } 
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        } 
      }
    } 
    
    return fold;
  }











  
  public Color getFoldIconArmedBackground() {
    return this.foldIconArmedBackground;
  }









  
  public Color getFoldIconBackground() {
    return this.foldIconBackground;
  }


  
  public Dimension getPreferredSize() {
    int h = (this.textArea != null) ? this.textArea.getHeight() : 100;
    return new Dimension(12 + this.additionalLeftMargin, h);
  }








  
  public boolean getShowCollapsedRegionToolTips() {
    return this.showFoldRegionTips;
  }














  
  public Point getToolTipLocation(MouseEvent e) {
    String text = getToolTipText(e);
    if (text == null) {
      return null;
    }

    
    Point p = e.getPoint();
    p.y = p.y / this.textArea.getLineHeight() * this.textArea.getLineHeight();
    p.x = getWidth() + (this.textArea.getMargin()).left;
    Gutter gutter = getGutter();
    int gutterMargin = (gutter.getInsets()).right;
    p.x += gutterMargin;
    JToolTip tempTip = createToolTip();
    p.x -= (tempTip.getInsets()).left;
    p.y += 16;
    return p;
  }








  
  public String getToolTipText(MouseEvent e) {
    String text = null;
    
    RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
    if (rsta.isCodeFoldingEnabled()) {
      FoldManager fm = rsta.getFoldManager();
      int pos = rsta.viewToModel(new Point(0, e.getY()));
      if (pos >= 0) {
        int line = 0;
        try {
          line = rsta.getLineOfOffset(pos);
        } catch (BadLocationException ble) {
          ble.printStackTrace();
          return null;
        } 
        Fold fold = fm.getFoldForLine(line);
        if (fold != null && fold.isCollapsed()) {
          
          int endLine = fold.getEndLine();
          if (fold.getLineCount() > 25) {
            endLine = fold.getStartLine() + 25;
          }
          
          StringBuilder sb = new StringBuilder("<html><nobr>");
          while (line <= endLine && line < rsta.getLineCount()) {
            Token t = rsta.getTokenListForLine(line);
            while (t != null && t.isPaintable()) {
              t.appendHTMLRepresentation(sb, rsta, true, true);
              t = t.getNextToken();
            } 
            sb.append("<br>");
            line++;
          } 
          
          text = sb.toString();
        } 
      } 
    } 

    
    return text;
  }



  
  void handleDocumentEvent(DocumentEvent e) {
    int newLineCount = this.textArea.getLineCount();
    if (newLineCount != this.currentLineCount) {
      this.currentLineCount = newLineCount;
      repaint();
    } 
  }


  
  protected void init() {
    super.init();
    setForeground(DEFAULT_FOREGROUND);
    setFoldIconBackground(DEFAULT_FOLD_BACKGROUND);
    this.collapsedFoldIcon = new FoldIcon(true);
    this.expandedFoldIcon = new FoldIcon(false);
    this.listener = new Listener(this);
    this.visibleRect = new Rectangle();
    setShowCollapsedRegionToolTips(true);
  }




  
  void lineHeightsChanged() {}



  
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
    g.fillRect(0, this.visibleRect.y, getWidth(), this.visibleRect.height);
    
    RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
    if (!rsta.isCodeFoldingEnabled()) {
      return;
    }
    
    if (this.textArea.getLineWrap()) {
      paintComponentWrapped(g);

      
      return;
    } 

    
    this.textAreaInsets = this.textArea.getInsets(this.textAreaInsets);
    if (this.visibleRect.y < this.textAreaInsets.top) {
      this.visibleRect.height -= this.textAreaInsets.top - this.visibleRect.y;
      this.visibleRect.y = this.textAreaInsets.top;
    } 
    int cellHeight = this.textArea.getLineHeight();
    int topLine = (this.visibleRect.y - this.textAreaInsets.top) / cellHeight;
    
    int y = topLine * cellHeight + (cellHeight - this.collapsedFoldIcon.getIconHeight()) / 2;
    y += this.textAreaInsets.top;

    
    FoldManager fm = rsta.getFoldManager();
    topLine += fm.getHiddenLineCountAbove(topLine, true);
    
    int width = getWidth();
    int x = width - 10;
    int line = topLine;
    
    boolean paintingOutlineLine = (this.foldWithOutlineShowing != null && this.foldWithOutlineShowing.containsLine(line));
    
    while (y < this.visibleRect.y + this.visibleRect.height) {
      if (paintingOutlineLine) {
        g.setColor(getForeground());
        int w2 = width - 6;
        if (line == this.foldWithOutlineShowing.getEndLine()) {
          int y2 = y + cellHeight / 2;
          g.drawLine(w2, y, w2, y2);
          g.drawLine(w2, y2, width - 2, y2);
          paintingOutlineLine = false;
        } else {
          
          g.drawLine(w2, y, w2, y + cellHeight);
        } 
      } 
      Fold fold = fm.getFoldForLine(line);
      if (fold != null) {
        if (fold == this.foldWithOutlineShowing) {
          if (!fold.isCollapsed()) {
            g.setColor(getForeground());
            int w2 = width - 6;
            g.drawLine(w2, y + cellHeight / 2, w2, y + cellHeight);
            paintingOutlineLine = true;
          } 
          if (this.mouseOverFoldIcon) {
            this.paintFoldArmed = true;
          }
        } 
        if (fold.isCollapsed()) {
          this.collapsedFoldIcon.paintIcon(this, g, x, y);

          
          do {
            int hiddenLineCount = fold.getLineCount();
            if (hiddenLineCount == 0) {
              break;
            }


            
            line += hiddenLineCount;
            fold = fm.getFoldForLine(line);
          } while (fold != null && fold.isCollapsed());
        } else {
          
          this.expandedFoldIcon.paintIcon(this, g, x, y);
        } 
        this.paintFoldArmed = false;
      } 
      line++;
      y += cellHeight;
    } 
  }






































  
  private void paintComponentWrapped(Graphics g) {
    int width = getWidth();
    
    RTextAreaUI ui = (RTextAreaUI)this.textArea.getUI();
    View v = ui.getRootView(this.textArea).getView(0);
    Document doc = this.textArea.getDocument();
    Element root = doc.getDefaultRootElement();
    int topPosition = this.textArea.viewToModel(new Point(this.visibleRect.x, this.visibleRect.y));
    
    int topLine = root.getElementIndex(topPosition);
    int cellHeight = this.textArea.getLineHeight();
    FoldManager fm = ((RSyntaxTextArea)this.textArea).getFoldManager();




    
    Rectangle visibleEditorRect = ui.getVisibleEditorRect();
    Rectangle r = LineNumberList.getChildViewBounds(v, topLine, visibleEditorRect);
    
    int y = r.y;
    y += (cellHeight - this.collapsedFoldIcon.getIconHeight()) / 2;
    
    int visibleBottom = this.visibleRect.y + this.visibleRect.height;
    int x = width - 10;
    int line = topLine;
    
    boolean paintingOutlineLine = (this.foldWithOutlineShowing != null && this.foldWithOutlineShowing.containsLine(line));
    int lineCount = root.getElementCount();
    
    while (y < visibleBottom && line < lineCount) {
      
      int curLineH = (LineNumberList.getChildViewBounds(v, line, visibleEditorRect)).height;

      
      if (paintingOutlineLine) {
        g.setColor(getForeground());
        int w2 = width - 6;
        if (line == this.foldWithOutlineShowing.getEndLine()) {
          int y2 = y + curLineH - cellHeight / 2;
          g.drawLine(w2, y, w2, y2);
          g.drawLine(w2, y2, width - 2, y2);
          paintingOutlineLine = false;
        } else {
          
          g.drawLine(w2, y, w2, y + curLineH);
        } 
      } 
      Fold fold = fm.getFoldForLine(line);
      if (fold != null) {
        if (fold == this.foldWithOutlineShowing) {
          if (!fold.isCollapsed()) {
            g.setColor(getForeground());
            int w2 = width - 6;
            g.drawLine(w2, y + cellHeight / 2, w2, y + curLineH);
            paintingOutlineLine = true;
          } 
          if (this.mouseOverFoldIcon) {
            this.paintFoldArmed = true;
          }
        } 
        if (fold.isCollapsed()) {
          this.collapsedFoldIcon.paintIcon(this, g, x, y);
          y += (LineNumberList.getChildViewBounds(v, line, visibleEditorRect)).height;
          
          line += fold.getLineCount() + 1;
        } else {
          
          this.expandedFoldIcon.paintIcon(this, g, x, y);
          y += curLineH;
          line++;
        } 
        this.paintFoldArmed = false;
        continue;
      } 
      y += curLineH;
      line++;
    } 
  }




  
  private int rowAtPoint(Point p) {
    int line = 0;
    
    try {
      int offs = this.textArea.viewToModel(p);
      if (offs > -1) {
        line = this.textArea.getLineOfOffset(offs);
      }
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    
    return line;
  }












  
  public void setAdditionalLeftMargin(int leftMargin) {
    if (leftMargin < 0) {
      throw new IllegalArgumentException("leftMargin must be >= 0");
    }
    
    this.additionalLeftMargin = leftMargin;
    revalidate();
  }










  
  public void setFoldIconArmedBackground(Color bg) {
    this.foldIconArmedBackground = bg;
  }









  
  public void setFoldIconBackground(Color bg) {
    this.foldIconBackground = bg;
  }









  
  public void setFoldIcons(Icon collapsedIcon, Icon expandedIcon) {
    this.collapsedFoldIcon = collapsedIcon;
    this.expandedFoldIcon = expandedIcon;
    revalidate();
    repaint();
  }








  
  public void setShowCollapsedRegionToolTips(boolean show) {
    if (show != this.showFoldRegionTips) {
      if (show) {
        ToolTipManager.sharedInstance().registerComponent(this);
      } else {
        
        ToolTipManager.sharedInstance().unregisterComponent(this);
      } 
      this.showFoldRegionTips = show;
    } 
  }





  
  public void setTextArea(RTextArea textArea) {
    if (this.textArea != null) {
      this.textArea.removePropertyChangeListener("RSTA.codeFolding", this.listener);
    }
    
    super.setTextArea(textArea);
    if (this.textArea != null) {
      this.textArea.addPropertyChangeListener("RSTA.codeFolding", this.listener);
    }
  }


  
  private class FoldIcon
    implements Icon
  {
    private boolean collapsed;


    
    FoldIcon(boolean collapsed) {
      this.collapsed = collapsed;
    }

    
    public int getIconHeight() {
      return 8;
    }

    
    public int getIconWidth() {
      return 8;
    }

    
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Color bg = FoldIndicator.this.foldIconBackground;
      if (FoldIndicator.this.paintFoldArmed && FoldIndicator.this.foldIconArmedBackground != null) {
        bg = FoldIndicator.this.foldIconArmedBackground;
      }
      g.setColor(bg);
      g.fillRect(x, y, 8, 8);
      g.setColor(FoldIndicator.this.getForeground());
      g.drawRect(x, y, 8, 8);
      g.drawLine(x + 2, y + 4, x + 2 + 4, y + 4);
      if (this.collapsed) {
        g.drawLine(x + 4, y + 2, x + 4, y + 6);
      }
    }
  }




  
  private class Listener
    extends MouseInputAdapter
    implements PropertyChangeListener
  {
    Listener(FoldIndicator fgc) {
      fgc.addMouseListener(this);
      fgc.addMouseMotionListener(this);
    }


    
    public void mouseClicked(MouseEvent e) {
      Point p = e.getPoint();
      int line = FoldIndicator.this.rowAtPoint(p);
      
      RSyntaxTextArea rsta = (RSyntaxTextArea)FoldIndicator.this.textArea;
      FoldManager fm = rsta.getFoldManager();
      
      Fold fold = fm.getFoldForLine(line);
      if (fold != null) {
        fold.toggleCollapsedState();
        FoldIndicator.this.getGutter().repaint();
        FoldIndicator.this.textArea.repaint();
      } 
    }


    
    public void mouseExited(MouseEvent e) {
      if (FoldIndicator.this.foldWithOutlineShowing != null) {
        FoldIndicator.this.foldWithOutlineShowing = null;
        FoldIndicator.this.mouseOverFoldIcon = false;
        FoldIndicator.this.repaint();
      } 
    }

    
    public void mouseMoved(MouseEvent e) {
      boolean oldMouseOverFoldIcon = FoldIndicator.this.mouseOverFoldIcon;
      Fold newSelectedFold = FoldIndicator.this.findOpenFoldClosestTo(e.getPoint());
      if (newSelectedFold != FoldIndicator.this.foldWithOutlineShowing && newSelectedFold != null && 
        !newSelectedFold.isOnSingleLine()) {
        FoldIndicator.this.foldWithOutlineShowing = newSelectedFold;
        FoldIndicator.this.repaint();
      }
      else if (FoldIndicator.this.mouseOverFoldIcon != oldMouseOverFoldIcon) {
        FoldIndicator.this.repaint();
      } 
    }


    
    public void propertyChange(PropertyChangeEvent e) {
      FoldIndicator.this.repaint();
    }
  }
}
