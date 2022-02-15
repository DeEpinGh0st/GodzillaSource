package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Paint;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;











































public class FlatBorder
  extends BasicBorders.MarginBorder
{
  protected final int focusWidth = UIManager.getInt("Component.focusWidth");
  protected final float innerFocusWidth = FlatUIUtils.getUIFloat("Component.innerFocusWidth", 0.0F);
  protected final float innerOutlineWidth = FlatUIUtils.getUIFloat("Component.innerOutlineWidth", 0.0F);
  protected final Color focusColor = UIManager.getColor("Component.focusColor");
  protected final Color borderColor = UIManager.getColor("Component.borderColor");
  protected final Color disabledBorderColor = UIManager.getColor("Component.disabledBorderColor");
  protected final Color focusedBorderColor = UIManager.getColor("Component.focusedBorderColor");
  
  protected final Color errorBorderColor = UIManager.getColor("Component.error.borderColor");
  protected final Color errorFocusedBorderColor = UIManager.getColor("Component.error.focusedBorderColor");
  protected final Color warningBorderColor = UIManager.getColor("Component.warning.borderColor");
  protected final Color warningFocusedBorderColor = UIManager.getColor("Component.warning.focusedBorderColor");
  protected final Color customBorderColor = UIManager.getColor("Component.custom.borderColor");

  
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Graphics2D g2 = (Graphics2D)g.create();
    try {
      FlatUIUtils.setRenderingHints(g2);
      
      float focusWidth = UIScale.scale(getFocusWidth(c));
      float borderWidth = UIScale.scale(getBorderWidth(c));
      float arc = UIScale.scale(getArc(c));
      Color outlineColor = getOutlineColor(c);

      
      if (outlineColor != null || isFocused(c)) {
        float innerWidth = (!isCellEditor(c) && !(c instanceof JScrollPane)) ? ((outlineColor != null) ? this.innerOutlineWidth : this.innerFocusWidth) : 0.0F;


        
        g2.setColor((outlineColor != null) ? outlineColor : getFocusColor(c));
        FlatUIUtils.paintComponentOuterBorder(g2, x, y, width, height, focusWidth, borderWidth + 
            UIScale.scale(innerWidth), arc);
      } 

      
      g2.setPaint((outlineColor != null) ? outlineColor : getBorderColor(c));
      FlatUIUtils.paintComponentBorder(g2, x, y, width, height, focusWidth, borderWidth, arc);
    } finally {
      g2.dispose();
    } 
  }




  
  protected Color getOutlineColor(Component c) {
    if (!(c instanceof JComponent)) {
      return null;
    }
    Object outline = ((JComponent)c).getClientProperty("JComponent.outline");
    if (outline instanceof String)
    { switch ((String)outline) {
        case "error":
          return isFocused(c) ? this.errorFocusedBorderColor : this.errorBorderColor;
        
        case "warning":
          return isFocused(c) ? this.warningFocusedBorderColor : this.warningBorderColor;
      }  }
    else { if (outline instanceof Color) {
        Color color = (Color)outline;
        
        if (!isFocused(c) && this.customBorderColor instanceof DerivedColor)
          color = ((DerivedColor)this.customBorderColor).derive(color); 
        return color;
      }  if (outline instanceof Color[] && ((Color[])outline).length >= 2)
        return ((Color[])outline)[isFocused(c) ? 0 : 1];  }
    
    return null;
  }
  
  protected Color getFocusColor(Component c) {
    return this.focusColor;
  }
  
  protected Paint getBorderColor(Component c) {
    return isEnabled(c) ? (
      isFocused(c) ? this.focusedBorderColor : this.borderColor) : this.disabledBorderColor;
  }

  
  protected boolean isEnabled(Component c) {
    if (c instanceof JScrollPane) {
      
      JViewport viewport = ((JScrollPane)c).getViewport();
      Component view = (viewport != null) ? viewport.getView() : null;
      if (view != null && !isEnabled(view)) {
        return false;
      }
    } 
    return (c.isEnabled() && (!(c instanceof JTextComponent) || ((JTextComponent)c).isEditable()));
  }
  
  protected boolean isFocused(Component c) {
    if (c instanceof JScrollPane) {
      JViewport viewport = ((JScrollPane)c).getViewport();
      Component view = (viewport != null) ? viewport.getView() : null;
      if (view != null) {
        if (FlatUIUtils.isPermanentFocusOwner(view)) {
          return true;
        }
        if ((view instanceof JTable && ((JTable)view).isEditing()) || (view instanceof JTree && ((JTree)view)
          .isEditing())) {
          
          Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
          if (focusOwner != null)
            return SwingUtilities.isDescendingFrom(focusOwner, view); 
        } 
      } 
      return false;
    }  if (c instanceof JComboBox && ((JComboBox)c).isEditable()) {
      Component editorComponent = ((JComboBox)c).getEditor().getEditorComponent();
      return (editorComponent != null) ? FlatUIUtils.isPermanentFocusOwner(editorComponent) : false;
    }  if (c instanceof JSpinner) {
      if (FlatUIUtils.isPermanentFocusOwner(c)) {
        return true;
      }
      JComponent editor = ((JSpinner)c).getEditor();
      if (editor instanceof JSpinner.DefaultEditor) {
        JTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
        if (textField != null)
          return FlatUIUtils.isPermanentFocusOwner(textField); 
      } 
      return false;
    } 
    return FlatUIUtils.isPermanentFocusOwner(c);
  }
  
  protected boolean isCellEditor(Component c) {
    return FlatUIUtils.isCellEditor(c);
  }

  
  public Insets getBorderInsets(Component c, Insets insets) {
    float focusWidth = UIScale.scale(getFocusWidth(c));
    float ow = focusWidth + UIScale.scale(getLineWidth(c));
    
    insets = super.getBorderInsets(c, insets);
    insets.top = Math.round(UIScale.scale(insets.top) + ow);
    insets.left = Math.round(UIScale.scale(insets.left) + ow);
    insets.bottom = Math.round(UIScale.scale(insets.bottom) + ow);
    insets.right = Math.round(UIScale.scale(insets.right) + ow);
    
    if (isCellEditor(c)) {
      
      insets.top = insets.bottom = 0;

      
      if (c.getComponentOrientation().isLeftToRight()) {
        insets.right = 0;
      } else {
        insets.left = 0;
      } 
    } 
    return insets;
  }



  
  protected int getFocusWidth(Component c) {
    if (isCellEditor(c)) {
      return 0;
    }
    return this.focusWidth;
  }




  
  protected int getLineWidth(Component c) {
    return 1;
  }




  
  protected int getBorderWidth(Component c) {
    return getLineWidth(c);
  }



  
  protected int getArc(Component c) {
    return 0;
  }
}
