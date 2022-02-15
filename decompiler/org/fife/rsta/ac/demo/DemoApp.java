package org.fife.rsta.ac.demo;

import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
















public class DemoApp
  extends JFrame
{
  public DemoApp() {
    setRootPane(new DemoRootPane());
    setDefaultCloseOperation(3);
    setTitle("RSTA Language Support Demo Application");
    pack();
  }








  
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    if (visible) {
      ((DemoRootPane)getRootPane()).focusTextArea();
    }
  }

  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {

          
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          } catch (Exception e) {
            e.printStackTrace();
          } 
          Toolkit.getDefaultToolkit().setDynamicLayout(true);
          (new DemoApp()).setVisible(true);
        });
  }
}
