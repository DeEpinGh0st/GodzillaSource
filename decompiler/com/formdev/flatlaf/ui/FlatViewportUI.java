package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;




























public class FlatViewportUI
  extends BasicViewportUI
{
  public static ComponentUI createUI(JComponent c) {
    return FlatUIUtils.createSharedUI(FlatViewportUI.class, FlatViewportUI::new);
  }

  
  public void update(Graphics g, JComponent c) {
    Component view = ((JViewport)c).getView();
    if (c.isOpaque() && view instanceof javax.swing.JTable) {
      
      g.setColor(view.getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      
      paint(g, c);
    } else {
      super.update(g, c);
    } 
  }
}
