package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;





















public class FlatInternalFrameTitlePane
  extends BasicInternalFrameTitlePane
{
  private JLabel titleLabel;
  private JPanel buttonPanel;
  
  public FlatInternalFrameTitlePane(JInternalFrame f) {
    super(f);
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    LookAndFeel.installBorder(this, "InternalFrameTitlePane.border");
  }

  
  protected PropertyChangeListener createPropertyChangeListener() {
    return new FlatPropertyChangeHandler();
  }

  
  protected LayoutManager createLayout() {
    return new BorderLayout(UIScale.scale(4), 0);
  }

  
  protected void createButtons() {
    super.createButtons();
    
    this.iconButton.setContentAreaFilled(false);
    this.maxButton.setContentAreaFilled(false);
    this.closeButton.setContentAreaFilled(false);
    
    Border emptyBorder = BorderFactory.createEmptyBorder();
    this.iconButton.setBorder(emptyBorder);
    this.maxButton.setBorder(emptyBorder);
    this.closeButton.setBorder(emptyBorder);
    
    updateButtonsVisibility();
  }

  
  protected void addSubComponents() {
    this.titleLabel = new JLabel(this.frame.getTitle());
    this.titleLabel.setFont(FlatUIUtils.nonUIResource(getFont()));
    this.titleLabel.setMinimumSize(new Dimension(UIScale.scale(32), 1));
    updateFrameIcon();
    updateColors();
    
    this.buttonPanel = new JPanel()
      {
        public Dimension getPreferredSize() {
          Dimension size = super.getPreferredSize();
          int height = size.height;
          
          if (!FlatInternalFrameTitlePane.this.iconButton.isVisible())
            height = Math.max(height, (FlatInternalFrameTitlePane.this.iconButton.getPreferredSize()).height); 
          if (!FlatInternalFrameTitlePane.this.maxButton.isVisible())
            height = Math.max(height, (FlatInternalFrameTitlePane.this.maxButton.getPreferredSize()).height); 
          if (!FlatInternalFrameTitlePane.this.closeButton.isVisible())
            height = Math.max(height, (FlatInternalFrameTitlePane.this.closeButton.getPreferredSize()).height); 
          return new Dimension(size.width, height);
        }
      };
    this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, 2));
    this.buttonPanel.setOpaque(false);
    
    this.buttonPanel.add(this.iconButton);
    this.buttonPanel.add(this.maxButton);
    this.buttonPanel.add(this.closeButton);
    
    add(this.titleLabel, "Center");
    add(this.buttonPanel, "After");
  }
  protected void updateFrameIcon() {
    ScaledImageIcon scaledImageIcon;
    Icon frameIcon = this.frame.getFrameIcon();
    if (frameIcon != null && (frameIcon.getIconWidth() == 0 || frameIcon.getIconHeight() == 0)) {
      frameIcon = null;
    } else if (frameIcon instanceof ImageIcon) {
      scaledImageIcon = new ScaledImageIcon((ImageIcon)frameIcon);
    }  this.titleLabel.setIcon((Icon)scaledImageIcon);
  }
  
  protected void updateColors() {
    Color background = FlatUIUtils.nonUIResource(this.frame.isSelected() ? this.selectedTitleColor : this.notSelectedTitleColor);
    Color foreground = FlatUIUtils.nonUIResource(this.frame.isSelected() ? this.selectedTextColor : this.notSelectedTextColor);
    
    this.titleLabel.setForeground(foreground);
    this.iconButton.setBackground(background);
    this.iconButton.setForeground(foreground);
    this.maxButton.setBackground(background);
    this.maxButton.setForeground(foreground);
    this.closeButton.setBackground(background);
    this.closeButton.setForeground(foreground);
  }
  
  protected void updateButtonsVisibility() {
    this.iconButton.setVisible(this.frame.isIconifiable());
    this.maxButton.setVisible(this.frame.isMaximizable());
    this.closeButton.setVisible(this.frame.isClosable());
  }




  
  protected void assembleSystemMenu() {}




  
  protected void showSystemMenu() {}



  
  public void paintComponent(Graphics g) {
    paintTitleBackground(g);
  }



  
  protected class FlatPropertyChangeHandler
    extends BasicInternalFrameTitlePane.PropertyChangeHandler
  {
    public void propertyChange(PropertyChangeEvent e) {
      switch (e.getPropertyName()) {
        case "title":
          FlatInternalFrameTitlePane.this.titleLabel.setText(FlatInternalFrameTitlePane.this.frame.getTitle());
          break;
        
        case "frameIcon":
          FlatInternalFrameTitlePane.this.updateFrameIcon();
          break;
        
        case "selected":
          FlatInternalFrameTitlePane.this.updateColors();
          break;
        
        case "iconable":
        case "maximizable":
        case "closable":
          FlatInternalFrameTitlePane.this.updateButtonsVisibility();
          FlatInternalFrameTitlePane.this.enableActions();
          FlatInternalFrameTitlePane.this.revalidate();
          FlatInternalFrameTitlePane.this.repaint();
          return;


        
        case "componentOrientation":
          FlatInternalFrameTitlePane.this.applyComponentOrientation(FlatInternalFrameTitlePane.this.frame.getComponentOrientation());
          break;
      } 
      
      super.propertyChange(e);
    }
  }
}
