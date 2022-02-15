package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Paint;
import javax.swing.AbstractButton;
import javax.swing.UIManager;









































public class FlatButtonBorder
  extends FlatBorder
{
  protected final Color borderColor = FlatUIUtils.getUIColor("Button.startBorderColor", "Button.borderColor");
  protected final Color endBorderColor = UIManager.getColor("Button.endBorderColor");
  protected final Color disabledBorderColor = UIManager.getColor("Button.disabledBorderColor");
  protected final Color focusedBorderColor = UIManager.getColor("Button.focusedBorderColor");
  protected final Color hoverBorderColor = UIManager.getColor("Button.hoverBorderColor");
  protected final Color defaultBorderColor = FlatUIUtils.getUIColor("Button.default.startBorderColor", "Button.default.borderColor");
  protected final Color defaultEndBorderColor = UIManager.getColor("Button.default.endBorderColor");
  protected final Color defaultHoverBorderColor = UIManager.getColor("Button.default.hoverBorderColor");
  protected final Color defaultFocusedBorderColor = UIManager.getColor("Button.default.focusedBorderColor");
  protected final Color defaultFocusColor = UIManager.getColor("Button.default.focusColor");
  protected final int borderWidth = UIManager.getInt("Button.borderWidth");
  protected final int defaultBorderWidth = UIManager.getInt("Button.default.borderWidth");
  protected final Insets toolbarMargin = UIManager.getInsets("Button.toolbar.margin");
  protected final Insets toolbarSpacingInsets = UIManager.getInsets("Button.toolbar.spacingInsets");
  protected final int arc = UIManager.getInt("Button.arc");

  
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    if (FlatButtonUI.isContentAreaFilled(c) && 
      !FlatButtonUI.isToolBarButton(c) && 
      !FlatButtonUI.isHelpButton(c) && 
      !FlatToggleButtonUI.isTabButton(c)) {
      super.paintBorder(c, g, x, y, width, height);
    }
  }
  
  protected Color getFocusColor(Component c) {
    return FlatButtonUI.isDefaultButton(c) ? this.defaultFocusColor : super.getFocusColor(c);
  }

  
  protected boolean isFocused(Component c) {
    return (FlatButtonUI.isFocusPainted(c) && super.isFocused(c));
  }

  
  protected Paint getBorderColor(Component c) {
    boolean def = FlatButtonUI.isDefaultButton(c);
    Paint color = FlatButtonUI.buttonStateColor(c, def ? this.defaultBorderColor : this.borderColor, this.disabledBorderColor, def ? this.defaultFocusedBorderColor : this.focusedBorderColor, def ? this.defaultHoverBorderColor : this.hoverBorderColor, null);






    
    Color startBg = def ? this.defaultBorderColor : this.borderColor;
    Color endBg = def ? this.defaultEndBorderColor : this.endBorderColor;
    if (color == startBg && endBg != null && !startBg.equals(endBg)) {
      color = new GradientPaint(0.0F, 0.0F, startBg, 0.0F, c.getHeight(), endBg);
    }
    return color;
  }

  
  public Insets getBorderInsets(Component c, Insets insets) {
    if (FlatButtonUI.isToolBarButton(c)) {


      
      Insets margin = (c instanceof AbstractButton) ? ((AbstractButton)c).getMargin() : null;

      
      FlatUIUtils.setInsets(insets, UIScale.scale(FlatUIUtils.addInsets(this.toolbarSpacingInsets, (margin != null && !(margin instanceof javax.swing.plaf.UIResource)) ? margin : this.toolbarMargin)));
    } else {
      
      insets = super.getBorderInsets(c, insets);

      
      if (FlatButtonUI.isIconOnlyOrSingleCharacterButton(c) && ((AbstractButton)c).getMargin() instanceof javax.swing.plaf.UIResource) {
        insets.left = insets.right = Math.min(insets.top, insets.bottom);
      }
    } 
    return insets;
  }

  
  protected int getFocusWidth(Component c) {
    return FlatToggleButtonUI.isTabButton(c) ? 0 : super.getFocusWidth(c);
  }

  
  protected int getBorderWidth(Component c) {
    return FlatButtonUI.isDefaultButton(c) ? this.defaultBorderWidth : this.borderWidth;
  }

  
  protected int getArc(Component c) {
    if (isCellEditor(c)) {
      return 0;
    }
    switch (FlatButtonUI.getButtonType(c)) { case 0:
        return 0;
      case 1: return 32767; }
     return this.arc;
  }
}
