package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicOptionPaneUI;



































































public class FlatOptionPaneUI
  extends BasicOptionPaneUI
{
  protected int iconMessageGap;
  protected int messagePadding;
  protected int maxCharactersPerLine;
  private int focusWidth;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatOptionPaneUI();
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    this.iconMessageGap = UIManager.getInt("OptionPane.iconMessageGap");
    this.messagePadding = UIManager.getInt("OptionPane.messagePadding");
    this.maxCharactersPerLine = UIManager.getInt("OptionPane.maxCharactersPerLine");
    this.focusWidth = UIManager.getInt("Component.focusWidth");
  }

  
  protected void installComponents() {
    super.installComponents();
    
    updateChildPanels(this.optionPane);
  }

  
  public Dimension getMinimumOptionPaneSize() {
    return UIScale.scale(super.getMinimumOptionPaneSize());
  }

  
  protected int getMaxCharactersPerLineCount() {
    int max = super.getMaxCharactersPerLineCount();
    return (this.maxCharactersPerLine > 0 && max == Integer.MAX_VALUE) ? this.maxCharactersPerLine : max;
  }

  
  protected Container createMessageArea() {
    Container messageArea = super.createMessageArea();

    
    if (this.iconMessageGap > 0) {
      Component iconMessageSeparator = findByName(messageArea, "OptionPane.separator");
      if (iconMessageSeparator != null) {
        iconMessageSeparator.setPreferredSize(new Dimension(UIScale.scale(this.iconMessageGap), 1));
      }
    } 
    return messageArea;
  }

  
  protected Container createButtonArea() {
    Container buttonArea = super.createButtonArea();

    
    if (buttonArea.getLayout() instanceof BasicOptionPaneUI.ButtonAreaLayout) {
      BasicOptionPaneUI.ButtonAreaLayout layout = (BasicOptionPaneUI.ButtonAreaLayout)buttonArea.getLayout();
      layout.setPadding(UIScale.scale(layout.getPadding() - this.focusWidth * 2));
    } 
    
    return buttonArea;
  }




  
  protected void addMessageComponents(Container container, GridBagConstraints cons, Object msg, int maxll, boolean internallyCreated) {
    if (this.messagePadding > 0) {
      cons.insets.bottom = UIScale.scale(this.messagePadding);
    }
    
    if (msg instanceof String && BasicHTML.isHTMLString((String)msg)) {
      maxll = Integer.MAX_VALUE;
    }
    super.addMessageComponents(container, cons, msg, maxll, internallyCreated);
  }
  
  private void updateChildPanels(Container c) {
    for (Component child : c.getComponents()) {
      if (child instanceof JPanel) {
        JPanel panel = (JPanel)child;

        
        panel.setOpaque(false);

        
        Border border = panel.getBorder();
        if (border instanceof javax.swing.plaf.UIResource) {
          panel.setBorder(new NonUIResourceBorder(border));
        }
      } 
      if (child instanceof Container) {
        updateChildPanels((Container)child);
      }
    } 
  }
  
  private Component findByName(Container c, String name) {
    for (Component child : c.getComponents()) {
      if (name.equals(child.getName())) {
        return child;
      }
      if (child instanceof Container) {
        Component c2 = findByName((Container)child, name);
        if (c2 != null)
          return c2; 
      } 
    } 
    return null;
  }

  
  private static class NonUIResourceBorder
    implements Border
  {
    private final Border delegate;

    
    NonUIResourceBorder(Border delegate) {
      this.delegate = delegate;
    }

    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      this.delegate.paintBorder(c, g, x, y, width, height);
    }

    
    public Insets getBorderInsets(Component c) {
      return this.delegate.getBorderInsets(c);
    }

    
    public boolean isBorderOpaque() {
      return this.delegate.isBorderOpaque();
    }
  }
}
