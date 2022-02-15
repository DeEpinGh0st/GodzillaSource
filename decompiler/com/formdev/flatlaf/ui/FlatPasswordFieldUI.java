package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.HiDPIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

















































public class FlatPasswordFieldUI
  extends BasicPasswordFieldUI
{
  protected int minimumWidth;
  protected boolean isIntelliJTheme;
  protected Color placeholderForeground;
  protected boolean showCapsLock;
  protected Icon capsLockIcon;
  private FocusListener focusListener;
  private KeyListener capsLockListener;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatPasswordFieldUI();
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    String prefix = getPropertyPrefix();
    this.minimumWidth = UIManager.getInt("Component.minimumWidth");
    this.isIntelliJTheme = UIManager.getBoolean("Component.isIntelliJTheme");
    this.placeholderForeground = UIManager.getColor(prefix + ".placeholderForeground");
    this.showCapsLock = UIManager.getBoolean("PasswordField.showCapsLock");
    this.capsLockIcon = UIManager.getIcon("PasswordField.capsLockIcon");
    
    LookAndFeel.installProperty(getComponent(), "opaque", Boolean.valueOf(false));
    
    MigLayoutVisualPadding.install(getComponent());
  }

  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    this.placeholderForeground = null;
    this.capsLockIcon = null;
    
    MigLayoutVisualPadding.uninstall(getComponent());
  }

  
  protected void installListeners() {
    super.installListeners();
    
    this.focusListener = new FlatUIUtils.RepaintFocusListener(getComponent());
    this.capsLockListener = new KeyAdapter()
      {
        public void keyPressed(KeyEvent e) {
          repaint(e);
        }
        
        public void keyReleased(KeyEvent e) {
          repaint(e);
        }
        private void repaint(KeyEvent e) {
          if (e.getKeyCode() == 20) {
            e.getComponent().repaint();
          }
        }
      };
    getComponent().addFocusListener(this.focusListener);
    getComponent().addKeyListener(this.capsLockListener);
  }

  
  protected void uninstallListeners() {
    super.uninstallListeners();
    
    getComponent().removeFocusListener(this.focusListener);
    getComponent().removeKeyListener(this.capsLockListener);
    this.focusListener = null;
    this.capsLockListener = null;
  }

  
  protected Caret createCaret() {
    return new FlatCaret(UIManager.getString("TextComponent.selectAllOnFocusPolicy"), 
        UIManager.getBoolean("TextComponent.selectAllOnMouseClick"));
  }

  
  protected void propertyChange(PropertyChangeEvent e) {
    super.propertyChange(e);
    FlatTextFieldUI.propertyChange(getComponent(), e);
  }

  
  protected void paintSafely(Graphics g) {
    FlatTextFieldUI.paintBackground(g, getComponent(), this.isIntelliJTheme);
    FlatTextFieldUI.paintPlaceholder(g, getComponent(), this.placeholderForeground);
    paintCapsLock(g);
    
    super.paintSafely(HiDPIUtils.createGraphicsTextYCorrection((Graphics2D)g));
  }
  
  protected void paintCapsLock(Graphics g) {
    if (!this.showCapsLock) {
      return;
    }
    JTextComponent c = getComponent();
    if (!FlatUIUtils.isPermanentFocusOwner(c) || 
      !Toolkit.getDefaultToolkit().getLockingKeyState(20)) {
      return;
    }
    int y = (c.getHeight() - this.capsLockIcon.getIconHeight()) / 2;
    int x = c.getWidth() - this.capsLockIcon.getIconWidth() - y;
    this.capsLockIcon.paintIcon(c, g, x, y);
  }


  
  protected void paintBackground(Graphics g) {}


  
  public Dimension getPreferredSize(JComponent c) {
    return FlatTextFieldUI.applyMinimumWidth(c, super.getPreferredSize(c), this.minimumWidth);
  }

  
  public Dimension getMinimumSize(JComponent c) {
    return FlatTextFieldUI.applyMinimumWidth(c, super.getMinimumSize(c), this.minimumWidth);
  }
}
