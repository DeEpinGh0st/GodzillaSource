package org.fife.ui.autocomplete;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ListUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.PopupWindowDecorator;


































































class AutoCompletePopupWindow
  extends JWindow
  implements CaretListener, ListSelectionListener, MouseListener
{
  private AutoCompletion ac;
  private PopupList list;
  private CompletionListModel model;
  private Completion lastSelection;
  private AutoCompleteDescWindow descWindow;
  private Dimension preferredDescWindowSize;
  private boolean aboveCaret;
  private Color descWindowColor;
  private int lastLine;
  private boolean keyBindingsInstalled;
  private KeyActionPair escapeKap;
  private KeyActionPair upKap;
  private KeyActionPair downKap;
  private KeyActionPair leftKap;
  private KeyActionPair rightKap;
  private KeyActionPair enterKap;
  private KeyActionPair tabKap;
  private KeyActionPair homeKap;
  private KeyActionPair endKap;
  private KeyActionPair pageUpKap;
  private KeyActionPair pageDownKap;
  private KeyActionPair ctrlCKap;
  private KeyActionPair oldEscape;
  private KeyActionPair oldUp;
  private KeyActionPair oldDown;
  private KeyActionPair oldLeft;
  private KeyActionPair oldRight;
  private KeyActionPair oldEnter;
  private KeyActionPair oldTab;
  private KeyActionPair oldHome;
  private KeyActionPair oldEnd;
  private KeyActionPair oldPageUp;
  private KeyActionPair oldPageDown;
  private KeyActionPair oldCtrlC;
  private static final int VERTICAL_SPACE = 1;
  private static final String SUBSTANCE_LIST_UI = "org.pushingpixels.substance.internal.ui.SubstanceListUI";
  
  AutoCompletePopupWindow(Window parent, AutoCompletion ac) {
    super(parent);
    ComponentOrientation o = ac.getTextComponentOrientation();
    
    this.ac = ac;
    this.model = new CompletionListModel();
    this.list = new PopupList(this.model);
    
    this.list.setCellRenderer(new DelegatingCellRenderer());
    this.list.addListSelectionListener(this);
    this.list.addMouseListener(this);
    
    JPanel contentPane = new JPanel(new BorderLayout());
    JScrollPane sp = new JScrollPane(this.list, 22, 32);






    
    JPanel corner = new SizeGrip();
    
    boolean isLeftToRight = o.isLeftToRight();
    String str = isLeftToRight ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
    
    sp.setCorner(str, corner);
    
    contentPane.add(sp);
    setContentPane(contentPane);
    applyComponentOrientation(o);

    
    if (Util.getShouldAllowDecoratingMainAutoCompleteWindows()) {
      PopupWindowDecorator decorator = PopupWindowDecorator.get();
      if (decorator != null) {
        decorator.decorate(this);
      }
    } 
    
    pack();
    
    setFocusableWindowState(false);
    
    this.lastLine = -1;
  }



  
  public void caretUpdate(CaretEvent e) {
    if (isVisible()) {
      int line = this.ac.getLineOfCaret();
      if (line != this.lastLine) {
        this.lastLine = -1;
        setVisible(false);
      } else {
        
        doAutocomplete();
      }
    
    } else if (AutoCompletion.getDebug()) {
      Thread.dumpStack();
    } 
  }







  
  private AutoCompleteDescWindow createDescriptionWindow() {
    AutoCompleteDescWindow dw = new AutoCompleteDescWindow(this, this.ac);
    dw.applyComponentOrientation(this.ac.getTextComponentOrientation());
    
    Dimension size = this.preferredDescWindowSize;
    if (size == null) {
      size = getSize();
    }
    dw.setSize(size);
    
    if (this.descWindowColor != null) {
      dw.setBackground(this.descWindowColor);
    } else {
      
      this.descWindowColor = dw.getBackground();
    } 
    
    return dw;
  }







  
  private void createKeyActionPairs() {
    EnterAction enterAction = new EnterAction();
    this.escapeKap = new KeyActionPair("Escape", new EscapeAction());
    this.upKap = new KeyActionPair("Up", new UpAction());
    this.downKap = new KeyActionPair("Down", new DownAction());
    this.leftKap = new KeyActionPair("Left", new LeftAction());
    this.rightKap = new KeyActionPair("Right", new RightAction());
    this.enterKap = new KeyActionPair("Enter", enterAction);
    this.tabKap = new KeyActionPair("Tab", enterAction);
    this.homeKap = new KeyActionPair("Home", new HomeAction());
    this.endKap = new KeyActionPair("End", new EndAction());
    this.pageUpKap = new KeyActionPair("PageUp", new PageUpAction());
    this.pageDownKap = new KeyActionPair("PageDown", new PageDownAction());
    this.ctrlCKap = new KeyActionPair("CtrlC", new CopyAction());

    
    this.oldEscape = new KeyActionPair();
    this.oldUp = new KeyActionPair();
    this.oldDown = new KeyActionPair();
    this.oldLeft = new KeyActionPair();
    this.oldRight = new KeyActionPair();
    this.oldEnter = new KeyActionPair();
    this.oldTab = new KeyActionPair();
    this.oldHome = new KeyActionPair();
    this.oldEnd = new KeyActionPair();
    this.oldPageUp = new KeyActionPair();
    this.oldPageDown = new KeyActionPair();
    this.oldCtrlC = new KeyActionPair();
  }


  
  protected void doAutocomplete() {
    this.lastLine = this.ac.refreshPopupWindow();
  }






  
  private static KeyStroke getCopyKeyStroke() {
    int key = 67;
    int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    return KeyStroke.getKeyStroke(key, mask);
  }






  
  public Color getDescriptionWindowColor() {
    if (this.descWindow != null) {
      return this.descWindow.getBackground();
    }
    return this.descWindowColor;
  }









  
  public ListCellRenderer getListCellRenderer() {
    DelegatingCellRenderer dcr = (DelegatingCellRenderer)this.list.getCellRenderer();
    return dcr.getFallbackCellRenderer();
  }






  
  public Completion getSelection() {
    return isShowing() ? this.list.getSelectedValue() : this.lastSelection;
  }






  
  private void insertSelectedCompletion() {
    Completion comp = getSelection();
    this.ac.insertCompletion(comp);
  }








  
  private void installKeyBindings() {
    if (AutoCompletion.getDebug()) {
      System.out.println("PopupWindow: Installing keybindings");
    }
    if (this.keyBindingsInstalled) {
      System.err.println("Error: key bindings were already installed");
      Thread.dumpStack();
      
      return;
    } 
    if (this.escapeKap == null) {
      createKeyActionPairs();
    }
    
    JTextComponent comp = this.ac.getTextComponent();
    InputMap im = comp.getInputMap();
    ActionMap am = comp.getActionMap();
    
    replaceAction(im, am, 27, this.escapeKap, this.oldEscape);
    if (AutoCompletion.getDebug() && this.oldEscape.action == this.escapeKap.action) {
      Thread.dumpStack();
    }
    replaceAction(im, am, 38, this.upKap, this.oldUp);
    replaceAction(im, am, 37, this.leftKap, this.oldLeft);
    replaceAction(im, am, 40, this.downKap, this.oldDown);
    replaceAction(im, am, 39, this.rightKap, this.oldRight);
    replaceAction(im, am, 10, this.enterKap, this.oldEnter);
    replaceAction(im, am, 9, this.tabKap, this.oldTab);
    replaceAction(im, am, 36, this.homeKap, this.oldHome);
    replaceAction(im, am, 35, this.endKap, this.oldEnd);
    replaceAction(im, am, 33, this.pageUpKap, this.oldPageUp);
    replaceAction(im, am, 34, this.pageDownKap, this.oldPageDown);



    
    KeyStroke ks = getCopyKeyStroke();
    this.oldCtrlC.key = im.get(ks);
    im.put(ks, this.ctrlCKap.key);
    this.oldCtrlC.action = am.get(this.ctrlCKap.key);
    am.put(this.ctrlCKap.key, this.ctrlCKap.action);
    
    comp.addCaretListener(this);
    
    this.keyBindingsInstalled = true;
  }



  
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      insertSelectedCompletion();
    }
  }




  
  public void mouseEntered(MouseEvent e) {}




  
  public void mouseExited(MouseEvent e) {}




  
  public void mousePressed(MouseEvent e) {}



  
  public void mouseReleased(MouseEvent e) {}



  
  private void positionDescWindow() {
    int x;
    boolean showDescWindow = (this.descWindow != null && this.ac.getShowDescWindow());
    if (!showDescWindow) {
      return;
    }



    
    Point p = getLocation();
    Rectangle screenBounds = Util.getScreenBoundsForPoint(p.x, p.y);




    
    if (this.ac.getTextComponentOrientation().isLeftToRight()) {
      x = getX() + getWidth() + 5;
      if (x + this.descWindow.getWidth() > screenBounds.x + screenBounds.width) {
        x = getX() - 5 - this.descWindow.getWidth();
      }
    } else {
      
      x = getX() - 5 - this.descWindow.getWidth();
      if (x < screenBounds.x) {
        x = getX() + getWidth() + 5;
      }
    } 
    
    int y = getY();
    if (this.aboveCaret) {
      y = y + getHeight() - this.descWindow.getHeight();
    }
    
    if (x != this.descWindow.getX() || y != this.descWindow.getY()) {
      this.descWindow.setLocation(x, y);
    }
  }













  
  private void putBackAction(InputMap im, ActionMap am, int key, KeyActionPair kap) {
    KeyStroke ks = KeyStroke.getKeyStroke(key, 0);
    am.put(im.get(ks), kap.action);
    im.put(ks, kap.key);
  }













  
  private void replaceAction(InputMap im, ActionMap am, int key, KeyActionPair kap, KeyActionPair old) {
    KeyStroke ks = KeyStroke.getKeyStroke(key, 0);
    old.key = im.get(ks);
    im.put(ks, kap.key);
    old.action = am.get(kap.key);
    am.put(kap.key, kap.action);
  }






  
  private void selectFirstItem() {
    if (this.model.getSize() > 0) {
      this.list.setSelectedIndex(0);
      this.list.ensureIndexIsVisible(0);
    } 
  }






  
  private void selectLastItem() {
    int index = this.model.getSize() - 1;
    if (index > -1) {
      this.list.setSelectedIndex(index);
      this.list.ensureIndexIsVisible(index);
    } 
  }






  
  private void selectNextItem() {
    int index = this.list.getSelectedIndex();
    if (index > -1) {
      index = (index + 1) % this.model.getSize();
      this.list.setSelectedIndex(index);
      this.list.ensureIndexIsVisible(index);
    } 
  }







  
  private void selectPageDownItem() {
    int visibleRowCount = this.list.getVisibleRowCount();
    int i = Math.min(this.list.getModel().getSize() - 1, this.list
        .getSelectedIndex() + visibleRowCount);
    this.list.setSelectedIndex(i);
    this.list.ensureIndexIsVisible(i);
  }







  
  private void selectPageUpItem() {
    int visibleRowCount = this.list.getVisibleRowCount();
    int i = Math.max(0, this.list.getSelectedIndex() - visibleRowCount);
    this.list.setSelectedIndex(i);
    this.list.ensureIndexIsVisible(i);
  }






  
  private void selectPreviousItem() {
    int index = this.list.getSelectedIndex();
    switch (index) {
      case 0:
        index = this.list.getModel().getSize() - 1;
        break;
      case -1:
        index = this.list.getModel().getSize() - 1;
        if (index == -1) {
          return;
        }
        break;
      default:
        index--;
        break;
    } 
    this.list.setSelectedIndex(index);
    this.list.ensureIndexIsVisible(index);
  }







  
  public void setCompletions(List<Completion> completions) {
    this.model.setContents(completions);
    selectFirstItem();
  }






  
  public void setDescriptionWindowSize(Dimension size) {
    if (this.descWindow != null) {
      this.descWindow.setSize(size);
    } else {
      
      this.preferredDescWindowSize = size;
    } 
  }







  
  public void setDescriptionWindowColor(Color color) {
    if (this.descWindow != null) {
      this.descWindow.setBackground(color);
    } else {
      
      this.descWindowColor = color;
    } 
  }










  
  public void setListCellRenderer(ListCellRenderer<Object> renderer) {
    DelegatingCellRenderer dcr = (DelegatingCellRenderer)this.list.getCellRenderer();
    dcr.setFallbackCellRenderer(renderer);
  }














  
  public void setLocationRelativeTo(Rectangle r) {
    Rectangle screenBounds = Util.getScreenBoundsForPoint(r.x, r.y);

    
    boolean showDescWindow = (this.descWindow != null && this.ac.getShowDescWindow());
    int totalH = getHeight();
    if (showDescWindow) {
      totalH = Math.max(totalH, this.descWindow.getHeight());
    }


    
    this.aboveCaret = false;
    int y = r.y + r.height + 1;
    if (y + totalH > screenBounds.height) {
      y = r.y - 1 - getHeight();
      this.aboveCaret = true;
    } 


    
    int x = r.x;
    if (!this.ac.getTextComponentOrientation().isLeftToRight()) {
      x -= getWidth();
    }
    if (x < screenBounds.x) {
      x = screenBounds.x;
    }
    else if (x + getWidth() > screenBounds.x + screenBounds.width) {
      x = screenBounds.x + screenBounds.width - getWidth();
    } 
    
    setLocation(x, y);

    
    if (showDescWindow) {
      positionDescWindow();
    }
  }









  
  public void setVisible(boolean visible) {
    if (visible != isVisible()) {
      
      if (visible) {
        installKeyBindings();
        this.lastLine = this.ac.getLineOfCaret();
        selectFirstItem();
        if (this.descWindow == null && this.ac.getShowDescWindow()) {
          this.descWindow = createDescriptionWindow();
          positionDescWindow();
        } 


        
        if (this.descWindow != null) {
          Completion c = this.list.getSelectedValue();
          if (c != null) {
            this.descWindow.setDescriptionFor(c);
          }
        } 
      } else {
        
        uninstallKeyBindings();
      } 
      
      super.setVisible(visible);










      
      if (!visible) {
        this.lastSelection = this.list.getSelectedValue();
        this.model.clear();
      } 



      
      if (this.descWindow != null) {
        this.descWindow.setVisible((visible && this.ac.getShowDescWindow()));
      }
    } 
  }









  
  private void uninstallKeyBindings() {
    if (AutoCompletion.getDebug()) {
      System.out.println("PopupWindow: Removing keybindings");
    }
    if (!this.keyBindingsInstalled) {
      return;
    }
    
    JTextComponent comp = this.ac.getTextComponent();
    InputMap im = comp.getInputMap();
    ActionMap am = comp.getActionMap();
    
    putBackAction(im, am, 27, this.oldEscape);
    putBackAction(im, am, 38, this.oldUp);
    putBackAction(im, am, 40, this.oldDown);
    putBackAction(im, am, 37, this.oldLeft);
    putBackAction(im, am, 39, this.oldRight);
    putBackAction(im, am, 10, this.oldEnter);
    putBackAction(im, am, 9, this.oldTab);
    putBackAction(im, am, 36, this.oldHome);
    putBackAction(im, am, 35, this.oldEnd);
    putBackAction(im, am, 33, this.oldPageUp);
    putBackAction(im, am, 34, this.oldPageDown);

    
    KeyStroke ks = getCopyKeyStroke();
    am.put(im.get(ks), this.oldCtrlC.action);
    im.put(ks, this.oldCtrlC.key);
    
    comp.removeCaretListener(this);
    
    this.keyBindingsInstalled = false;
  }






  
  public void updateUI() {
    SwingUtilities.updateComponentTreeUI(this);
    if (this.descWindow != null) {
      this.descWindow.updateUI();
    }
  }







  
  public void valueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      Completion value = this.list.getSelectedValue();
      if (value != null && this.descWindow != null) {
        this.descWindow.setDescriptionFor(value);
        positionDescWindow();
      } 
    } 
  }




  
  class CopyAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      boolean doNormalCopy = false;
      if (AutoCompletePopupWindow.this.descWindow != null && AutoCompletePopupWindow.this.descWindow.isVisible()) {
        doNormalCopy = !AutoCompletePopupWindow.this.descWindow.copy();
      }
      if (doNormalCopy) {
        AutoCompletePopupWindow.this.ac.getTextComponent().copy();
      }
    }
  }





  
  class DownAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        AutoCompletePopupWindow.this.selectNextItem();
      }
    }
  }





  
  class EndAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        AutoCompletePopupWindow.this.selectLastItem();
      }
    }
  }





  
  class EnterAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        AutoCompletePopupWindow.this.insertSelectedCompletion();
      }
    }
  }





  
  class EscapeAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        AutoCompletePopupWindow.this.setVisible(false);
      }
    }
  }





  
  class HomeAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        AutoCompletePopupWindow.this.selectFirstItem();
      }
    }
  }


  
  private static class KeyActionPair
  {
    private Object key;

    
    private Action action;

    
    KeyActionPair() {}

    
    KeyActionPair(Object key, Action a) {
      this.key = key;
      this.action = a;
    }
  }





  
  class LeftAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        JTextComponent comp = AutoCompletePopupWindow.this.ac.getTextComponent();
        Caret c = comp.getCaret();
        int dot = c.getDot();
        if (dot > 0) {
          c.setDot(--dot);

          
          if (comp.isVisible() && 
            AutoCompletePopupWindow.this.lastLine != -1) {
            AutoCompletePopupWindow.this.doAutocomplete();
          }
        } 
      } 
    }
  }






  
  class PageDownAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        AutoCompletePopupWindow.this.selectPageDownItem();
      }
    }
  }





  
  class PageUpAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        AutoCompletePopupWindow.this.selectPageUpItem();
      }
    }
  }




  
  private class PopupList
    extends JList<Completion>
  {
    PopupList(CompletionListModel model) {
      super(model);
    }

    
    public void setUI(ListUI ui) {
      if (Util.getUseSubstanceRenderers() && "org.pushingpixels.substance.internal.ui.SubstanceListUI"
        .equals(ui.getClass().getName())) {





        
        CompletionProvider p = AutoCompletePopupWindow.this.ac.getCompletionProvider();
        BasicCompletion bc = new BasicCompletion(p, "Hello world");
        setPrototypeCellValue(bc);
      }
      else {
        
        ui = new FastListUI();
        setPrototypeCellValue((Completion)null);
      } 
      super.setUI(ui);
    }
  }





  
  class RightAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible()) {
        JTextComponent comp = AutoCompletePopupWindow.this.ac.getTextComponent();
        Caret c = comp.getCaret();
        int dot = c.getDot();
        if (dot < comp.getDocument().getLength()) {
          c.setDot(++dot);

          
          if (comp.isVisible() && 
            AutoCompletePopupWindow.this.lastLine != -1) {
            AutoCompletePopupWindow.this.doAutocomplete();
          }
        } 
      } 
    }
  }






  
  class UpAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletePopupWindow.this.isVisible())
        AutoCompletePopupWindow.this.selectPreviousItem(); 
    }
  }
}
