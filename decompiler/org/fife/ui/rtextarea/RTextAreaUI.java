package org.fife.ui.rtextarea;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.WrappedPlainView;

public class RTextAreaUI extends BasicTextAreaUI {
  private static final String SHARED_ACTION_MAP_NAME = "RTextAreaUI.actionMap";
  private static final EditorKit DEFAULT_KIT = new RTextAreaEditorKit(); private static final String SHARED_INPUT_MAP_NAME = "RTextAreaUI.inputMap"; protected RTextArea textArea;
  private static final TransferHandler DEFAULT_TRANSFER_HANDLER = new RTATextTransferHandler();




  
  private static final String RTEXTAREA_KEYMAP_NAME = "RTextAreaKeymap";




  
  public static ComponentUI createUI(JComponent textArea) {
    return new RTextAreaUI(textArea);
  }








  
  public RTextAreaUI(JComponent textArea) {
    if (!(textArea instanceof RTextArea)) {
      throw new IllegalArgumentException("RTextAreaUI is for instances of RTextArea only!");
    }
    
    this.textArea = (RTextArea)textArea;
  }




















  
  private void correctNimbusDefaultProblems(JTextComponent editor) {
    Color c = editor.getCaretColor();
    if (c == null) {
      editor.setCaretColor(RTextArea.getDefaultCaretColor());
    }
    
    c = editor.getSelectionColor();
    if (c == null) {
      c = UIManager.getColor("nimbusSelectionBackground");
      if (c == null) {
        c = UIManager.getColor("textHighlight");
        if (c == null) {
          c = new ColorUIResource(Color.BLUE);
        }
      } 
      editor.setSelectionColor(c);
    } 
    
    c = editor.getSelectedTextColor();
    if (c == null) {
      c = UIManager.getColor("nimbusSelectedText");
      if (c == null) {
        c = UIManager.getColor("textHighlightText");
        if (c == null) {
          c = new ColorUIResource(Color.WHITE);
        }
      } 
      editor.setSelectedTextColor(c);
    } 
    
    c = editor.getDisabledTextColor();
    if (c == null) {
      c = UIManager.getColor("nimbusDisabledText");
      if (c == null) {
        c = UIManager.getColor("textInactiveText");
        if (c == null) {
          c = new ColorUIResource(Color.DARK_GRAY);
        }
      } 
      editor.setDisabledTextColor(c);
    } 
    
    Border border = editor.getBorder();
    if (border == null) {
      editor.setBorder(new BasicBorders.MarginBorder());
    }
    
    Insets margin = editor.getMargin();
    if (margin == null) {
      editor.setMargin(new InsetsUIResource(2, 2, 2, 2));
    }
  }










  
  public View create(Element elem) {
    if (this.textArea.getLineWrap()) {
      return new WrappedPlainView(elem, this.textArea.getWrapStyleWord());
    }
    
    return new PlainView(elem);
  }









  
  protected Caret createCaret() {
    Caret caret = new ConfigurableCaret();
    caret.setBlinkRate(500);
    return caret;
  }


  
  protected Highlighter createHighlighter() {
    return new RTextAreaHighlighter();
  }













  
  protected Keymap createKeymap() {
    Keymap map = JTextComponent.getKeymap("RTextAreaKeymap");
    if (map == null) {
      Keymap parent = JTextComponent.getKeymap("default");
      map = JTextComponent.addKeymap("RTextAreaKeymap", parent);
      map.setDefaultAction(new RTextAreaEditorKit.DefaultKeyTypedAction());
    } 
    
    return map;
  }
















  
  protected ActionMap createRTextAreaActionMap() {
    ActionMap map = new ActionMapUIResource();
    Action[] actions = this.textArea.getActions();
    int n = actions.length;
    for (Action a : actions) {
      map.put(a.getValue("Name"), a);
    }


    
    map.put(TransferHandler.getCutAction().getValue("Name"), 
        TransferHandler.getCutAction());
    map.put(TransferHandler.getCopyAction().getValue("Name"), 
        TransferHandler.getCopyAction());
    map.put(TransferHandler.getPasteAction().getValue("Name"), 
        TransferHandler.getPasteAction());
    
    return map;
  }









  
  protected String getActionMapName() {
    return "RTextAreaUI.actionMap";
  }









  
  public EditorKit getEditorKit(JTextComponent tc) {
    return DEFAULT_KIT;
  }






  
  public RTextArea getRTextArea() {
    return this.textArea;
  }
















  
  private ActionMap getRTextAreaActionMap() {
    ActionMap map = (ActionMap)UIManager.get(getActionMapName());
    if (map == null) {
      map = createRTextAreaActionMap();
      UIManager.put(getActionMapName(), map);
    } 
    
    ActionMap componentMap = new ActionMapUIResource();
    componentMap.put("requestFocus", new FocusAction());
    
    if (map != null) {
      componentMap.setParent(map);
    }
    return componentMap;
  }











  
  protected InputMap getRTextAreaInputMap() {
    InputMap map = new InputMapUIResource();
    InputMap shared = (InputMap)UIManager.get("RTextAreaUI.inputMap");
    if (shared == null) {
      shared = new RTADefaultInputMap();
      UIManager.put("RTextAreaUI.inputMap", shared);
    } 


    
    map.setParent(shared);
    return map;
  }












  
  protected Rectangle getVisibleEditorRect() {
    Rectangle alloc = this.textArea.getBounds();
    if (alloc.width > 0 && alloc.height > 0) {
      alloc.x = alloc.y = 0;
      Insets insets = this.textArea.getInsets();
      alloc.x += insets.left;
      alloc.y += insets.top;
      alloc.width -= insets.left + insets.right;
      alloc.height -= insets.top + insets.bottom;
      return alloc;
    } 
    return null;
  }



  
  protected void installDefaults() {
    super.installDefaults();
    
    JTextComponent editor = getComponent();
    editor.setFont(RTextAreaBase.getDefaultFont());


    
    correctNimbusDefaultProblems(editor);
    
    editor.setTransferHandler(DEFAULT_TRANSFER_HANDLER);
  }














  
  protected void installKeyboardActions() {
    RTextArea textArea = getRTextArea();


    
    textArea.setKeymap(createKeymap());


    
    InputMap map = getRTextAreaInputMap();
    SwingUtilities.replaceUIInputMap(textArea, 0, map);

    
    ActionMap am = getRTextAreaActionMap();
    if (am != null) {
      SwingUtilities.replaceUIActionMap(textArea, am);
    }
  }







  
  public void installUI(JComponent c) {
    if (!(c instanceof RTextArea)) {
      throw new Error("RTextAreaUI needs an instance of RTextArea!");
    }
    super.installUI(c);
  }




  
  protected void paintBackground(Graphics g) {
    Color bg = this.textArea.getBackground();
    if (bg != null) {
      g.setColor(bg);
      
      Rectangle r = g.getClipBounds();
      g.fillRect(r.x, r.y, r.width, r.height);
    } 
    
    paintEditorAugmentations(g);
  }









  
  protected void paintCurrentLineHighlight(Graphics g, Rectangle visibleRect) {
    if (this.textArea.getHighlightCurrentLine()) {
      
      Caret caret = this.textArea.getCaret();
      if (caret.getDot() == caret.getMark()) {
        
        Color highlight = this.textArea.getCurrentLineHighlightColor();






        
        int height = this.textArea.getLineHeight();
        
        if (this.textArea.getFadeCurrentLineHighlight()) {
          Graphics2D g2d = (Graphics2D)g;
          Color bg = this.textArea.getBackground();
          GradientPaint paint = new GradientPaint(visibleRect.x, 0.0F, highlight, (visibleRect.x + visibleRect.width), 0.0F, (bg == null) ? Color.WHITE : bg);


          
          g2d.setPaint(paint);
          g2d.fillRect(visibleRect.x, this.textArea.currentCaretY, visibleRect.width, height);
        }
        else {
          
          g.setColor(highlight);
          g.fillRect(visibleRect.x, this.textArea.currentCaretY, visibleRect.width, height);
        } 
      } 
    } 
  }











  
  protected void paintEditorAugmentations(Graphics g) {
    Rectangle visibleRect = this.textArea.getVisibleRect();
    paintLineHighlights(g);
    paintCurrentLineHighlight(g, visibleRect);
    paintMarginLine(g, visibleRect);
  }






  
  protected void paintLineHighlights(Graphics g) {
    LineHighlightManager lhm = this.textArea.getLineHighlightManager();
    if (lhm != null) {
      lhm.paintLineHighlights(g);
    }
  }







  
  protected void paintMarginLine(Graphics g, Rectangle visibleRect) {
    if (this.textArea.isMarginLineEnabled()) {
      g.setColor(this.textArea.getMarginLineColor());
      Insets insets = this.textArea.getInsets();
      int marginLineX = this.textArea.getMarginLinePixelLocation() + ((insets == null) ? 0 : insets.left);
      
      g.drawLine(marginLineX, visibleRect.y, marginLineX, visibleRect.y + visibleRect.height);
    } 
  }





  
  protected void paintSafely(Graphics g) {
    if (!this.textArea.isOpaque()) {
      paintEditorAugmentations(g);
    }
    super.paintSafely(g);
  }






















  
  public int yForLine(int line) throws BadLocationException {
    int startOffs = this.textArea.getLineStartOffset(line);
    return yForLineContaining(startOffs);
  }























  
  public int yForLineContaining(int offs) throws BadLocationException {
    Rectangle r = modelToView(this.textArea, offs);
    return (r != null) ? r.y : -1;
  }




  
  class FocusAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      RTextAreaUI.this.textArea.requestFocus();
    }

    
    public boolean isEnabled() {
      return RTextAreaUI.this.textArea.isEditable();
    }
  }
}
