package com.formdev.flatlaf.ui;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;



























public class FlatDesktopPaneUI
  extends BasicDesktopPaneUI
{
  public static ComponentUI createUI(JComponent c) {
    return new FlatDesktopPaneUI();
  }

  
  protected void installDesktopManager() {
    this.desktopManager = this.desktop.getDesktopManager();
    if (this.desktopManager == null) {
      this.desktopManager = new FlatDesktopManager();
      this.desktop.setDesktopManager(this.desktopManager);
    } 
  }

  
  private class FlatDesktopManager
    extends DefaultDesktopManager
    implements UIResource
  {
    private FlatDesktopManager() {}
    
    public void iconifyFrame(JInternalFrame f) {
      super.iconifyFrame(f);
      
      ((FlatDesktopIconUI)f.getDesktopIcon().getUI()).updateDockIcon();
    }
  }
}
