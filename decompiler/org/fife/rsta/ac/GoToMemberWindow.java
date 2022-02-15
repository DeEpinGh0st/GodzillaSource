package org.fife.rsta.ac;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.fife.ui.rsyntaxtextarea.PopupWindowDecorator;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.focusabletip.TipUtil;































public class GoToMemberWindow
  extends JWindow
{
  private RSyntaxTextArea textArea;
  private JTextField field;
  private AbstractSourceTree tree;
  private Listener listener;
  
  public GoToMemberWindow(Window parent, RSyntaxTextArea textArea, AbstractSourceTree tree) {
    super(parent);
    this.textArea = textArea;
    ComponentOrientation o = parent.getComponentOrientation();
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.setBorder(TipUtil.getToolTipBorder());
    
    this.listener = new Listener();
    addWindowFocusListener(this.listener);
    parent.addComponentListener(this.listener);
    
    this.field = createTextField();
    contentPane.add(this.field, "North");
    
    this.tree = tree;
    tweakTreeBorder(this.tree);
    tree.setSorted(true);
    tree.setShowMajorElementsOnly(true);
    tree.setGotoSelectedElementOnClick(false);
    tree.setFocusable(false);
    tree.listenTo(textArea);
    tree.addMouseListener(this.listener);
    JScrollPane sp = new JScrollPane(tree);
    sp.setBorder((Border)null);
    sp.setViewportBorder(BorderFactory.createEmptyBorder());
    contentPane.add(sp);
    
    Color bg = TipUtil.getToolTipBackground();
    setBackground(bg);
    this.field.setBackground(bg);
    tree.setBackground(bg);
    ((DefaultTreeCellRenderer)tree.getCellRenderer()).setBackgroundNonSelectionColor(bg);

    
    setContentPane(contentPane);
    PopupWindowDecorator decorator = PopupWindowDecorator.get();
    
    if (decorator != null) {
      decorator.decorate(this);
    }
    
    applyComponentOrientation(o);
    pack();
    JRootPane pane = getRootPane();
    InputMap im = pane.getInputMap(1);
    im.put(KeyStroke.getKeyStroke(27, 0), "EscapePressed");
    ActionMap am = pane.getActionMap();
    am.put("EscapePressed", new AbstractAction()
        {
          public void actionPerformed(ActionEvent e) {
            GoToMemberWindow.this.dispose();
          }
        });
  }







  
  private JTextField createTextField() {
    JTextField field = new JTextField(30);
    field.setUI(new BasicTextFieldUI());
    field.setBorder(new TextFieldBorder());
    field.addActionListener(this.listener);
    field.addKeyListener(this.listener);
    field.getDocument().addDocumentListener(this.listener);
    return field;
  }


  
  public void dispose() {
    this.listener.uninstall();
    super.dispose();


    
    this.textArea.requestFocusInWindow();
  }







  
  private void tweakTreeBorder(AbstractSourceTree tree) {
    Border treeBorder = tree.getBorder();
    if (treeBorder == null) {
      treeBorder = BorderFactory.createEmptyBorder(2, 0, 0, 0);
      tree.setBorder(treeBorder);
    }
    else if (treeBorder instanceof EmptyBorder && 
      (((EmptyBorder)treeBorder).getBorderInsets()).top == 0) {
      treeBorder = BorderFactory.createCompoundBorder(
          BorderFactory.createEmptyBorder(2, 0, 0, 0), treeBorder);
      tree.setBorder(treeBorder);
    } 
  }

  
  private class Listener
    extends MouseAdapter
    implements WindowFocusListener, ComponentListener, DocumentListener, ActionListener, KeyListener
  {
    private Listener() {}

    
    public void actionPerformed(ActionEvent e) {
      if (GoToMemberWindow.this.tree.gotoSelectedElement()) {
        GoToMemberWindow.this.dispose();
      }
    }

    
    public void changedUpdate(DocumentEvent e) {
      handleDocumentEvent(e);
    }

    
    public void componentHidden(ComponentEvent e) {
      GoToMemberWindow.this.dispose();
    }

    
    public void componentMoved(ComponentEvent e) {
      GoToMemberWindow.this.dispose();
    }

    
    public void componentResized(ComponentEvent e) {
      GoToMemberWindow.this.dispose();
    }

    
    public void componentShown(ComponentEvent e) {}

    
    private void handleDocumentEvent(DocumentEvent e) {
      GoToMemberWindow.this.tree.filter(GoToMemberWindow.this.field.getText());
      GoToMemberWindow.this.tree.selectFirstNodeMatchingFilter();
    }

    
    public void insertUpdate(DocumentEvent e) {
      handleDocumentEvent(e);
    }

    
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case 40:
          GoToMemberWindow.this.tree.selectNextVisibleRow();
          break;
        case 38:
          GoToMemberWindow.this.tree.selectPreviousVisibleRow();
          break;
      } 
    }


    
    public void keyReleased(KeyEvent e) {}


    
    public void keyTyped(KeyEvent e) {}

    
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        GoToMemberWindow.this.tree.gotoSelectedElement();
        GoToMemberWindow.this.dispose();
      } 
    }

    
    public void removeUpdate(DocumentEvent e) {
      handleDocumentEvent(e);
    }
    
    public void uninstall() {
      GoToMemberWindow.this.field.removeActionListener(this);
      GoToMemberWindow.this.field.getDocument().removeDocumentListener(this);
      GoToMemberWindow.this.tree.removeMouseListener(this);
      GoToMemberWindow.this.removeWindowFocusListener(this);
    }


    
    public void windowGainedFocus(WindowEvent e) {}

    
    public void windowLostFocus(WindowEvent e) {
      GoToMemberWindow.this.dispose();
    }
  }


  
  private static class TextFieldBorder
    implements Border
  {
    private TextFieldBorder() {}

    
    public Insets getBorderInsets(Component c) {
      return new Insets(2, 5, 3, 5);
    }

    
    public boolean isBorderOpaque() {
      return false;
    }


    
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
      g.setColor(UIManager.getColor("controlDkShadow"));
      g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
    }
  }
}
