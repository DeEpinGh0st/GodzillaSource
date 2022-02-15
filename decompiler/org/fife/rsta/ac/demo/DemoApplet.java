package org.fife.rsta.ac.demo;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
























public class DemoApplet
  extends JApplet
{
  public void init() {
    super.init();
    SwingUtilities.invokeLater(() -> {
          String laf = UIManager.getSystemLookAndFeelClassName();
          try {
            UIManager.setLookAndFeel(laf);
          } catch (Exception e) {
            e.printStackTrace();
          } 
          setRootPane(new DemoRootPane());
        });
  }










  
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    if (visible)
      ((DemoRootPane)getRootPane()).focusTextArea(); 
  }
}
