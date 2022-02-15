package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;




















































public class FlatSpinnerUI
  extends BasicSpinnerUI
{
  private Handler handler;
  protected int minimumWidth;
  protected String buttonStyle;
  protected String arrowType;
  protected boolean isIntelliJTheme;
  protected Color borderColor;
  protected Color disabledBorderColor;
  protected Color disabledBackground;
  protected Color disabledForeground;
  protected Color buttonBackground;
  protected Color buttonArrowColor;
  protected Color buttonDisabledArrowColor;
  protected Color buttonHoverArrowColor;
  protected Color buttonPressedArrowColor;
  protected Insets padding;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatSpinnerUI();
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    LookAndFeel.installProperty(this.spinner, "opaque", Boolean.valueOf(false));
    
    this.minimumWidth = UIManager.getInt("Component.minimumWidth");
    this.buttonStyle = UIManager.getString("Spinner.buttonStyle");
    this.arrowType = UIManager.getString("Component.arrowType");
    this.isIntelliJTheme = UIManager.getBoolean("Component.isIntelliJTheme");
    this.borderColor = UIManager.getColor("Component.borderColor");
    this.disabledBorderColor = UIManager.getColor("Component.disabledBorderColor");
    this.disabledBackground = UIManager.getColor("Spinner.disabledBackground");
    this.disabledForeground = UIManager.getColor("Spinner.disabledForeground");
    this.buttonBackground = UIManager.getColor("Spinner.buttonBackground");
    this.buttonArrowColor = UIManager.getColor("Spinner.buttonArrowColor");
    this.buttonDisabledArrowColor = UIManager.getColor("Spinner.buttonDisabledArrowColor");
    this.buttonHoverArrowColor = UIManager.getColor("Spinner.buttonHoverArrowColor");
    this.buttonPressedArrowColor = UIManager.getColor("Spinner.buttonPressedArrowColor");
    this.padding = UIManager.getInsets("Spinner.padding");

    
    this.padding = UIScale.scale(this.padding);
    
    MigLayoutVisualPadding.install(this.spinner);
  }

  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    this.borderColor = null;
    this.disabledBorderColor = null;
    this.disabledBackground = null;
    this.disabledForeground = null;
    this.buttonBackground = null;
    this.buttonArrowColor = null;
    this.buttonDisabledArrowColor = null;
    this.buttonHoverArrowColor = null;
    this.buttonPressedArrowColor = null;
    this.padding = null;
    
    MigLayoutVisualPadding.uninstall(this.spinner);
  }

  
  protected void installListeners() {
    super.installListeners();
    
    addEditorFocusListener(this.spinner.getEditor());
    this.spinner.addFocusListener(getHandler());
    this.spinner.addPropertyChangeListener(getHandler());
  }

  
  protected void uninstallListeners() {
    super.uninstallListeners();
    
    removeEditorFocusListener(this.spinner.getEditor());
    this.spinner.removeFocusListener(getHandler());
    this.spinner.removePropertyChangeListener(getHandler());
    
    this.handler = null;
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(); 
    return this.handler;
  }

  
  protected JComponent createEditor() {
    JComponent editor = super.createEditor();

    
    editor.setOpaque(false);
    JTextField textField = getEditorTextField(editor);
    if (textField != null) {
      textField.setOpaque(false);
    }
    updateEditorColors();
    return editor;
  }

  
  protected void replaceEditor(JComponent oldEditor, JComponent newEditor) {
    super.replaceEditor(oldEditor, newEditor);
    
    removeEditorFocusListener(oldEditor);
    addEditorFocusListener(newEditor);
    updateEditorColors();
  }
  
  private void addEditorFocusListener(JComponent editor) {
    JTextField textField = getEditorTextField(editor);
    if (textField != null)
      textField.addFocusListener(getHandler()); 
  }
  
  private void removeEditorFocusListener(JComponent editor) {
    JTextField textField = getEditorTextField(editor);
    if (textField != null)
      textField.removeFocusListener(getHandler()); 
  }
  
  private void updateEditorColors() {
    JTextField textField = getEditorTextField(this.spinner.getEditor());
    if (textField != null) {


      
      textField.setForeground(FlatUIUtils.nonUIResource(getForeground(true)));
      textField.setDisabledTextColor(FlatUIUtils.nonUIResource(getForeground(false)));
    } 
  }
  
  private static JTextField getEditorTextField(JComponent editor) {
    return (editor instanceof JSpinner.DefaultEditor) ? ((JSpinner.DefaultEditor)editor)
      .getTextField() : null;
  }

  
  protected Color getBackground(boolean enabled) {
    return enabled ? this.spinner
      .getBackground() : (this.isIntelliJTheme ? 
      FlatUIUtils.getParentBackground(this.spinner) : this.disabledBackground);
  }
  
  protected Color getForeground(boolean enabled) {
    return enabled ? this.spinner.getForeground() : this.disabledForeground;
  }

  
  protected LayoutManager createLayout() {
    return getHandler();
  }

  
  protected Component createNextButton() {
    return createArrowButton(1, "Spinner.nextButton");
  }

  
  protected Component createPreviousButton() {
    return createArrowButton(5, "Spinner.previousButton");
  }
  
  private Component createArrowButton(int direction, String name) {
    FlatArrowButton button = new FlatArrowButton(direction, this.arrowType, this.buttonArrowColor, this.buttonDisabledArrowColor, this.buttonHoverArrowColor, null, this.buttonPressedArrowColor, null);
    
    button.setName(name);
    button.setYOffset((direction == 1) ? 1 : -1);
    if (direction == 1) {
      installNextButtonListeners(button);
    } else {
      installPreviousButtonListeners(button);
    }  return button;
  }

  
  public void update(Graphics g, JComponent c) {
    float focusWidth = FlatUIUtils.getBorderFocusWidth(c);
    float arc = FlatUIUtils.getBorderArc(c);

    
    if (c.isOpaque() && (focusWidth > 0.0F || arc > 0.0F)) {
      FlatUIUtils.paintParentBackground(g, c);
    }
    Graphics2D g2 = (Graphics2D)g;
    Object[] oldRenderingHints = FlatUIUtils.setRenderingHints(g2);
    
    int width = c.getWidth();
    int height = c.getHeight();
    boolean enabled = this.spinner.isEnabled();

    
    g2.setColor(getBackground(enabled));
    FlatUIUtils.paintComponentBackground(g2, 0, 0, width, height, focusWidth, arc);

    
    boolean paintButton = !"none".equals(this.buttonStyle);
    Handler handler = getHandler();
    if (paintButton && (handler.nextButton != null || handler.previousButton != null)) {
      Component button = (handler.nextButton != null) ? handler.nextButton : handler.previousButton;
      int arrowX = button.getX();
      int arrowWidth = button.getWidth();
      boolean isLeftToRight = this.spinner.getComponentOrientation().isLeftToRight();

      
      if (enabled) {
        g2.setColor(this.buttonBackground);
        Shape oldClip = g2.getClip();
        if (isLeftToRight) {
          g2.clipRect(arrowX, 0, width - arrowX, height);
        } else {
          g2.clipRect(0, 0, arrowX + arrowWidth, height);
        }  FlatUIUtils.paintComponentBackground(g2, 0, 0, width, height, focusWidth, arc);
        g2.setClip(oldClip);
      } 

      
      g2.setColor(enabled ? this.borderColor : this.disabledBorderColor);
      float lw = UIScale.scale(1.0F);
      float lx = isLeftToRight ? arrowX : ((arrowX + arrowWidth) - lw);
      g2.fill(new Rectangle2D.Float(lx, focusWidth, lw, (height - 1) - focusWidth * 2.0F));
    } 
    
    paint(g, c);
    
    FlatUIUtils.resetRenderingHints(g, oldRenderingHints);
  }




  
  private class Handler
    implements LayoutManager, FocusListener, PropertyChangeListener
  {
    private Component editor = null;
    
    private Component nextButton;
    private Component previousButton;
    
    public void addLayoutComponent(String name, Component c) {
      switch (name) { case "Editor":
          this.editor = c; break;
        case "Next": this.nextButton = c; break;
        case "Previous": this.previousButton = c;
          break; }
    
    }
    
    public void removeLayoutComponent(Component c) {
      if (c == this.editor) {
        this.editor = null;
      } else if (c == this.nextButton) {
        this.nextButton = null;
      } else if (c == this.previousButton) {
        this.previousButton = null;
      } 
    }
    
    public Dimension preferredLayoutSize(Container parent) {
      Insets insets = parent.getInsets();
      Dimension editorSize = (this.editor != null) ? this.editor.getPreferredSize() : new Dimension(0, 0);

      
      int minimumWidth = FlatUIUtils.minimumWidth(FlatSpinnerUI.this.spinner, FlatSpinnerUI.this.minimumWidth);
      int innerHeight = editorSize.height + FlatSpinnerUI.this.padding.top + FlatSpinnerUI.this.padding.bottom;
      float focusWidth = FlatUIUtils.getBorderFocusWidth(FlatSpinnerUI.this.spinner);
      return new Dimension(
          Math.max(insets.left + insets.right + editorSize.width + FlatSpinnerUI.this.padding.left + FlatSpinnerUI.this.padding.right + innerHeight, UIScale.scale(minimumWidth) + Math.round(focusWidth * 2.0F)), insets.top + insets.bottom + innerHeight);
    }


    
    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }

    
    public void layoutContainer(Container parent) {
      Dimension size = parent.getSize();
      Insets insets = parent.getInsets();
      Rectangle r = FlatUIUtils.subtractInsets(new Rectangle(size), insets);
      
      if (this.nextButton == null && this.previousButton == null) {
        if (this.editor != null) {
          this.editor.setBounds(FlatUIUtils.subtractInsets(r, FlatSpinnerUI.this.padding));
        }
        return;
      } 
      Rectangle editorRect = new Rectangle(r);
      Rectangle buttonsRect = new Rectangle(r);

      
      int buttonsWidth = r.height;
      buttonsRect.width = buttonsWidth;
      
      if (parent.getComponentOrientation().isLeftToRight()) {
        editorRect.width -= buttonsWidth;
        buttonsRect.x += editorRect.width;
      } else {
        editorRect.x += buttonsWidth;
        editorRect.width -= buttonsWidth;
      } 
      
      if (this.editor != null) {
        this.editor.setBounds(FlatUIUtils.subtractInsets(editorRect, FlatSpinnerUI.this.padding));
      }
      int nextHeight = buttonsRect.height / 2 + buttonsRect.height % 2;
      if (this.nextButton != null)
        this.nextButton.setBounds(buttonsRect.x, buttonsRect.y, buttonsRect.width, nextHeight); 
      if (this.previousButton != null) {

        
        int previousY = buttonsRect.y + buttonsRect.height - nextHeight;
        this.previousButton.setBounds(buttonsRect.x, previousY, buttonsRect.width, nextHeight);
      } 
    }



    
    public void focusGained(FocusEvent e) {
      FlatSpinnerUI.this.spinner.repaint();

      
      if (e.getComponent() == FlatSpinnerUI.this.spinner) {
        JTextField textField = FlatSpinnerUI.getEditorTextField(FlatSpinnerUI.this.spinner.getEditor());
        if (textField != null) {
          textField.requestFocusInWindow();
        }
      } 
    }
    
    public void focusLost(FocusEvent e) {
      FlatSpinnerUI.this.spinner.repaint();
    }



    
    public void propertyChange(PropertyChangeEvent e) {
      switch (e.getPropertyName()) {
        case "foreground":
        case "enabled":
          FlatSpinnerUI.this.updateEditorColors();
          break;
        
        case "JComponent.roundRect":
          FlatSpinnerUI.this.spinner.repaint();
          break;
        
        case "JComponent.minimumWidth":
          FlatSpinnerUI.this.spinner.revalidate();
          break;
      } 
    }
    
    private Handler() {}
  }
}
