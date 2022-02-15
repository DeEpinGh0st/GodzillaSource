package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;






























public class FlatMenuBarUI
  extends BasicMenuBarUI
{
  public static ComponentUI createUI(JComponent c) {
    return new FlatMenuBarUI();
  }






  
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    
    ActionMap map = SwingUtilities.getUIActionMap(this.menuBar);
    if (map == null) {
      map = new ActionMapUIResource();
      SwingUtilities.replaceUIActionMap(this.menuBar, map);
    } 
    map.put("takeFocus", new TakeFocus());
  }



  
  private static class TakeFocus
    extends AbstractAction
  {
    private TakeFocus() {}



    
    public void actionPerformed(ActionEvent e) {
      JMenuBar menuBar = (JMenuBar)e.getSource();
      JMenu menu = menuBar.getMenu(0);
      if (menu != null) {
        (new javax.swing.MenuElement[2])[0] = menuBar; (new javax.swing.MenuElement[2])[1] = menu; (new javax.swing.MenuElement[3])[0] = menuBar; (new javax.swing.MenuElement[3])[1] = menu; (new javax.swing.MenuElement[3])[2] = menu
          
          .getPopupMenu();
        MenuSelectionManager.defaultManager().setSelectedPath(SystemInfo.isWindows ? new javax.swing.MenuElement[2] : new javax.swing.MenuElement[3]);
        FlatLaf.showMnemonics(menuBar);
      } 
    }
  }
}
