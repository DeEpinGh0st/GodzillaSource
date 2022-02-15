package org.fife.rsta.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;


























public abstract class EscapableDialog
  extends JDialog
{
  private static final String ESCAPE_KEY = "OnEsc";
  
  public EscapableDialog() {
    init();
  }






  
  public EscapableDialog(Dialog owner) {
    super(owner);
    init();
  }







  
  public EscapableDialog(Dialog owner, boolean modal) {
    super(owner, modal);
    init();
  }







  
  public EscapableDialog(Dialog owner, String title) {
    super(owner, title);
    init();
  }








  
  public EscapableDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
    init();
  }






  
  public EscapableDialog(Frame owner) {
    super(owner);
    init();
  }







  
  public EscapableDialog(Frame owner, boolean modal) {
    super(owner, modal);
    init();
  }







  
  public EscapableDialog(Frame owner, String title) {
    super(owner, title);
    init();
  }








  
  public EscapableDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    init();
  }






  
  protected void escapePressed() {
    setVisible(false);
  }




  
  private void init() {
    setEscapeClosesDialog(true);
  }









  
  public void setEscapeClosesDialog(boolean closes) {
    JRootPane rootPane = getRootPane();
    InputMap im = rootPane.getInputMap(2);
    
    ActionMap actionMap = rootPane.getActionMap();
    KeyStroke ks = KeyStroke.getKeyStroke(27, 0);
    
    if (closes) {
      im.put(ks, "OnEsc");
      actionMap.put("OnEsc", new AbstractAction()
          {
            public void actionPerformed(ActionEvent e) {
              EscapableDialog.this.escapePressed();
            }
          });
    } else {
      
      im.remove(ks);
      actionMap.remove("OnEsc");
    } 
  }
}
