package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;


































public class ConfigurableCaret
  extends DefaultCaret
{
  private static transient Action selectWord = null;



  
  private static transient Action selectLine = null;



  
  private transient MouseEvent selectedWordEvent = null;



  
  private transient Segment seg;



  
  private CaretStyle style;



  
  private ChangeableHighlightPainter selectionPainter;



  
  private boolean alwaysVisible;



  
  private boolean pasteOnMiddleMouseClick;



  
  public ConfigurableCaret() {
    this(CaretStyle.THICK_VERTICAL_LINE_STYLE);
  }








  
  public ConfigurableCaret(CaretStyle style) {
    this.seg = new Segment();
    setStyle(style);
    this.selectionPainter = new ChangeableHighlightPainter();
    this.pasteOnMiddleMouseClick = true;
  }




  
  private void adjustCaret(MouseEvent e) {
    if ((e.getModifiers() & 0x1) != 0 && getDot() != -1) {
      moveCaret(e);
    } else {
      
      positionCaret(e);
    } 
  }






  
  private void adjustFocus(boolean inWindow) {
    RTextArea textArea = getTextArea();
    if (textArea != null && textArea.isEnabled() && textArea
      .isRequestFocusEnabled()) {
      if (inWindow) {
        textArea.requestFocusInWindow();
      } else {
        
        textArea.requestFocus();
      } 
    }
  }








  
  protected synchronized void damage(Rectangle r) {
    if (r != null) {
      validateWidth(r);
      this.x = r.x - 1;
      this.y = r.y;
      this.width = r.width + 4;
      this.height = r.height;
      repaint();
    } 
  }











  
  public void deinstall(JTextComponent c) {
    if (!(c instanceof RTextArea)) {
      throw new IllegalArgumentException("c must be instance of RTextArea");
    }
    
    super.deinstall(c);
    c.setNavigationFilter((NavigationFilter)null);
  }








  
  public boolean getPasteOnMiddleMouseClick() {
    return this.pasteOnMiddleMouseClick;
  }






  
  protected RTextArea getTextArea() {
    return (RTextArea)getComponent();
  }







  
  public boolean getRoundedSelectionEdges() {
    return ((ChangeableHighlightPainter)getSelectionPainter())
      .getRoundedEdges();
  }








  
  protected Highlighter.HighlightPainter getSelectionPainter() {
    return this.selectionPainter;
  }







  
  public CaretStyle getStyle() {
    return this.style;
  }








  
  public void install(JTextComponent c) {
    if (!(c instanceof RTextArea)) {
      throw new IllegalArgumentException("c must be instance of RTextArea");
    }
    
    super.install(c);
    c.setNavigationFilter(new FoldAwareNavigationFilter());
  }










  
  public boolean isAlwaysVisible() {
    return this.alwaysVisible;
  }










  
  public void mouseClicked(MouseEvent e) {
    if (!e.isConsumed()) {
      
      RTextArea textArea = getTextArea();
      int nclicks = e.getClickCount();
      
      if (SwingUtilities.isLeftMouseButton(e)) {
        if (nclicks > 2) {
          Action a; ActionMap map; nclicks %= 2;
          switch (nclicks) {
            case 0:
              selectWord(e);
              this.selectedWordEvent = null;
              break;
            case 1:
              a = null;
              map = textArea.getActionMap();
              if (map != null) {
                a = map.get("select-line");
              }
              if (a == null) {
                if (selectLine == null) {
                  selectLine = new RTextAreaEditorKit.SelectLineAction();
                }
                a = selectLine;
              } 
              a.actionPerformed(new ActionEvent(textArea, 1001, null, e
                    
                    .getWhen(), e.getModifiers()));
              break;
          } 
        
        } 
      } else if (SwingUtilities.isMiddleMouseButton(e) && 
        getPasteOnMiddleMouseClick() && 
        nclicks == 1 && textArea.isEditable() && textArea.isEnabled()) {



        
        JTextComponent c = (JTextComponent)e.getSource();
        if (c != null) {
          try {
            Toolkit tk = c.getToolkit();
            Clipboard buffer = tk.getSystemSelection();

            
            if (buffer != null) {
              adjustCaret(e);
              TransferHandler th = c.getTransferHandler();
              if (th != null) {
                Transferable trans = buffer.getContents(null);
                if (trans != null) {
                  th.importData(c, trans);
                }
              } 
              adjustFocus(true);
            
            }
            else {
              
              textArea.paste();
            } 
          } catch (HeadlessException headlessException) {}
        }
      } 
    } 
  }












  
  public void mousePressed(MouseEvent e) {
    super.mousePressed(e);
    if (!e.isConsumed() && SwingUtilities.isRightMouseButton(e)) {
      JTextComponent c = getComponent();
      if (c != null && c.isEnabled() && c.isRequestFocusEnabled()) {
        c.requestFocusInWindow();
      }
    } 
  }









  
  public void paint(Graphics g) {
    if (isVisible() || this.alwaysVisible) {
      try {
        Color textAreaBg;
        int y;
        RTextArea textArea = getTextArea();
        g.setColor(textArea.getCaretColor());
        TextUI mapper = textArea.getUI();
        Rectangle r = mapper.modelToView(textArea, getDot());









        
        validateWidth(r);





        
        if (this.width > 0 && this.height > 0 && 
          !contains(r.x, r.y, r.width, r.height)) {
          Rectangle clip = g.getClipBounds();
          if (clip != null && !clip.contains(this))
          {
            
            repaint();
          }


          
          damage(r);
        } 


        
        r.height -= 2;
        
        switch (this.style) {

          
          case BLOCK_STYLE:
            textAreaBg = textArea.getBackground();
            if (textAreaBg == null) {
              textAreaBg = Color.white;
            }
            g.setXORMode(textAreaBg);
            
            g.fillRect(r.x, r.y, r.width, r.height);
            return;


          
          case BLOCK_BORDER_STYLE:
            g.drawRect(r.x, r.y, r.width - 1, r.height);
            return;

          
          case UNDERLINE_STYLE:
            textAreaBg = textArea.getBackground();
            if (textAreaBg == null) {
              textAreaBg = Color.white;
            }
            g.setXORMode(textAreaBg);
            y = r.y + r.height;
            g.drawLine(r.x, y, r.x + r.width - 1, y);
            return;


          
          default:
            g.drawLine(r.x, r.y, r.x, r.y + r.height);
            return;
          case THICK_VERTICAL_LINE_STYLE:
            break;
        } 
        g.drawLine(r.x, r.y, r.x, r.y + r.height);
        r.x++;
        g.drawLine(r.x, r.y, r.x, r.y + r.height);


      
      }
      catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }
  }






  
  private void selectWord(MouseEvent e) {
    if (this.selectedWordEvent != null && this.selectedWordEvent
      .getX() == e.getX() && this.selectedWordEvent
      .getY() == e.getY()) {
      return;
    }
    
    Action a = null;
    RTextArea textArea = getTextArea();
    ActionMap map = textArea.getActionMap();
    if (map != null) {
      a = map.get("select-word");
    }
    if (a == null) {
      if (selectWord == null) {
        selectWord = new RTextAreaEditorKit.SelectWordAction();
      }
      a = selectWord;
    } 
    a.actionPerformed(new ActionEvent(textArea, 1001, null, e
          
          .getWhen(), e.getModifiers()));
    this.selectedWordEvent = e;
  }










  
  public void setAlwaysVisible(boolean alwaysVisible) {
    if (alwaysVisible != this.alwaysVisible) {
      this.alwaysVisible = alwaysVisible;
      if (!isVisible())
      {
        
        repaint();
      }
    } 
  }








  
  public void setPasteOnMiddleMouseClick(boolean paste) {
    this.pasteOnMiddleMouseClick = paste;
  }







  
  public void setRoundedSelectionEdges(boolean rounded) {
    ((ChangeableHighlightPainter)getSelectionPainter())
      .setRoundedEdges(rounded);
  }









  
  public void setSelectionVisible(boolean visible) {
    super.setSelectionVisible(true);
  }







  
  public void setStyle(CaretStyle style) {
    if (style == null) {
      style = CaretStyle.THICK_VERTICAL_LINE_STYLE;
    }
    if (style != this.style) {
      this.style = style;
      repaint();
    } 
  }



























  
  private void validateWidth(Rectangle rect) {
    if (rect != null && rect.width <= 1) {
      
      try {









        
        RTextArea textArea = getTextArea();
        textArea.getDocument().getText(getDot(), 1, this.seg);
        Font font = textArea.getFont();
        FontMetrics fm = textArea.getFontMetrics(font);
        rect.width = fm.charWidth(this.seg.array[this.seg.offset]);




        
        if (rect.width == 0) {
          rect.width = fm.charWidth(' ');
        }
      }
      catch (BadLocationException ble) {
        
        ble.printStackTrace();
        rect.width = 8;
      } 
    }
  }






  
  private class FoldAwareNavigationFilter
    extends NavigationFilter
  {
    private FoldAwareNavigationFilter() {}





    
    public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
      RTextArea textArea = ConfigurableCaret.this.getTextArea();
      if (textArea instanceof RSyntaxTextArea) {
        
        RSyntaxTextArea rsta = (RSyntaxTextArea)ConfigurableCaret.this.getTextArea();
        if (rsta.isCodeFoldingEnabled()) {
          
          int lastDot = ConfigurableCaret.this.getDot();
          FoldManager fm = rsta.getFoldManager();
          int line = 0;
          try {
            line = textArea.getLineOfOffset(dot);
          } catch (Exception e) {
            e.printStackTrace();
          } 
          
          if (fm.isLineHidden(line)) {
            
            try {
              
              if (dot > lastDot) {
                int lineCount = textArea.getLineCount();
                while (++line < lineCount && fm
                  .isLineHidden(line));
                if (line < lineCount) {
                  dot = textArea.getLineStartOffset(line);
                } else {
                  
                  UIManager.getLookAndFeel()
                    .provideErrorFeedback(textArea);
                  
                  return;
                } 
              } else if (dot < lastDot) {
                while (--line >= 0 && fm.isLineHidden(line));
                if (line >= 0) {
                  dot = textArea.getLineEndOffset(line) - 1;
                }
              } 
            } catch (Exception e) {
              e.printStackTrace();

              
              return;
            } 
          }
        } 
      } 

      
      super.setDot(fb, dot, bias);
    }


    
    public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
      super.moveDot(fb, dot, bias);
    }
  }
}
