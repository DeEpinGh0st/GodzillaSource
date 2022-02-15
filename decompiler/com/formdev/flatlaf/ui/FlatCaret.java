package com.formdev.flatlaf.ui;

import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

























public class FlatCaret
  extends DefaultCaret
  implements UIResource
{
  private final String selectAllOnFocusPolicy;
  private final boolean selectAllOnMouseClick;
  private boolean wasFocused;
  private boolean wasTemporaryLost;
  private boolean isMousePressed;
  
  public FlatCaret(String selectAllOnFocusPolicy, boolean selectAllOnMouseClick) {
    this.selectAllOnFocusPolicy = selectAllOnFocusPolicy;
    this.selectAllOnMouseClick = selectAllOnMouseClick;
  }

  
  public void install(JTextComponent c) {
    super.install(c);


    
    Document doc = c.getDocument();
    if (doc != null && getDot() == 0 && getMark() == 0) {
      int length = doc.getLength();
      if (length > 0) {
        setDot(length);
      }
    } 
  }
  
  public void focusGained(FocusEvent e) {
    if (!this.wasTemporaryLost && (!this.isMousePressed || this.selectAllOnMouseClick))
      selectAllOnFocusGained(); 
    this.wasTemporaryLost = false;
    this.wasFocused = true;
    
    super.focusGained(e);
  }

  
  public void focusLost(FocusEvent e) {
    this.wasTemporaryLost = e.isTemporary();
    super.focusLost(e);
  }

  
  public void mousePressed(MouseEvent e) {
    this.isMousePressed = true;
    super.mousePressed(e);
  }

  
  public void mouseReleased(MouseEvent e) {
    this.isMousePressed = false;
    super.mouseReleased(e);
  }
  
  protected void selectAllOnFocusGained() {
    JTextComponent c = getComponent();
    Document doc = c.getDocument();
    if (doc == null || !c.isEnabled() || !c.isEditable()) {
      return;
    }
    Object selectAllOnFocusPolicy = c.getClientProperty("JTextField.selectAllOnFocusPolicy");
    if (selectAllOnFocusPolicy == null) {
      selectAllOnFocusPolicy = this.selectAllOnFocusPolicy;
    }
    if ("never".equals(selectAllOnFocusPolicy)) {
      return;
    }
    if (!"always".equals(selectAllOnFocusPolicy)) {


      
      if (this.wasFocused) {
        return;
      }
      
      int dot = getDot();
      int mark = getMark();
      if (dot != mark || dot != doc.getLength()) {
        return;
      }
    } 
    
    if (c instanceof javax.swing.JFormattedTextField) {
      EventQueue.invokeLater(() -> {
            setDot(0);
            moveDot(doc.getLength());
          });
    } else {
      setDot(0);
      moveDot(doc.getLength());
    } 
  }
}
