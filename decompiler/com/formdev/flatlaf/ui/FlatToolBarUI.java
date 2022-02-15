package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;



































public class FlatToolBarUI
  extends BasicToolBarUI
{
  public static ComponentUI createUI(JComponent c) {
    return new FlatToolBarUI();
  }

  
  protected ContainerListener createToolBarContListener() {
    return new BasicToolBarUI.ToolBarContListener()
      {
        public void componentAdded(ContainerEvent e) {
          super.componentAdded(e);
          
          Component c = e.getChild();
          if (c instanceof javax.swing.AbstractButton) {
            c.setFocusable(false);
          }
        }
        
        public void componentRemoved(ContainerEvent e) {
          super.componentRemoved(e);
          
          Component c = e.getChild();
          if (c instanceof javax.swing.AbstractButton)
            c.setFocusable(true); 
        }
      };
  }
  
  protected void setBorderToRollover(Component c) {}
  
  protected void setBorderToNonRollover(Component c) {}
  
  protected void setBorderToNormal(Component c) {}
  
  protected Border createRolloverBorder() {
    return null; } protected void installRolloverBorders(JComponent c) {} protected void installNonRolloverBorders(JComponent c) {} protected void installNormalBorders(JComponent c) {} protected Border createNonRolloverBorder() {
    return null;
  }
  
  public void setOrientation(int orientation) {
    if (orientation != this.toolBar.getOrientation()) {
      
      Insets margin = this.toolBar.getMargin();
      Insets newMargin = new Insets(margin.left, margin.top, margin.right, margin.bottom);
      if (!newMargin.equals(margin)) {
        this.toolBar.setMargin(newMargin);
      }
    } 
    super.setOrientation(orientation);
  }
}
