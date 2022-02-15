package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;



















































public class FlatCheckBoxIcon
  extends FlatAbstractIcon
{
  protected final String style = UIManager.getString("CheckBox.icon.style");
  public final int focusWidth = getUIInt("CheckBox.icon.focusWidth", 
      UIManager.getInt("Component.focusWidth"), this.style);
  protected final Color focusColor = FlatUIUtils.getUIColor("CheckBox.icon.focusColor", 
      UIManager.getColor("Component.focusColor"));
  protected final int arc = FlatUIUtils.getUIInt("CheckBox.arc", 2);

  
  protected final Color borderColor = getUIColor("CheckBox.icon.borderColor", this.style);
  protected final Color background = getUIColor("CheckBox.icon.background", this.style);
  protected final Color selectedBorderColor = getUIColor("CheckBox.icon.selectedBorderColor", this.style);
  protected final Color selectedBackground = getUIColor("CheckBox.icon.selectedBackground", this.style);
  protected final Color checkmarkColor = getUIColor("CheckBox.icon.checkmarkColor", this.style);

  
  protected final Color disabledBorderColor = getUIColor("CheckBox.icon.disabledBorderColor", this.style);
  protected final Color disabledBackground = getUIColor("CheckBox.icon.disabledBackground", this.style);
  protected final Color disabledCheckmarkColor = getUIColor("CheckBox.icon.disabledCheckmarkColor", this.style);

  
  protected final Color focusedBorderColor = getUIColor("CheckBox.icon.focusedBorderColor", this.style);
  protected final Color focusedBackground = getUIColor("CheckBox.icon.focusedBackground", this.style);
  protected final Color selectedFocusedBorderColor = getUIColor("CheckBox.icon.selectedFocusedBorderColor", this.style);
  protected final Color selectedFocusedBackground = getUIColor("CheckBox.icon.selectedFocusedBackground", this.style);
  protected final Color selectedFocusedCheckmarkColor = getUIColor("CheckBox.icon.selectedFocusedCheckmarkColor", this.style);

  
  protected final Color hoverBorderColor = getUIColor("CheckBox.icon.hoverBorderColor", this.style);
  protected final Color hoverBackground = getUIColor("CheckBox.icon.hoverBackground", this.style);
  protected final Color selectedHoverBackground = getUIColor("CheckBox.icon.selectedHoverBackground", this.style);

  
  protected final Color pressedBackground = getUIColor("CheckBox.icon.pressedBackground", this.style);
  protected final Color selectedPressedBackground = getUIColor("CheckBox.icon.selectedPressedBackground", this.style);
  
  protected static Color getUIColor(String key, String style) {
    if (style != null) {
      Color color = UIManager.getColor(styleKey(key, style));
      if (color != null)
        return color; 
    } 
    return UIManager.getColor(key);
  }
  static final int ICON_SIZE = 15;
  protected static int getUIInt(String key, int defaultValue, String style) {
    if (style != null) {
      Object value = UIManager.get(styleKey(key, style));
      if (value instanceof Integer)
        return ((Integer)value).intValue(); 
    } 
    return FlatUIUtils.getUIInt(key, defaultValue);
  }
  
  private static String styleKey(String key, String style) {
    return key.replace(".icon.", ".icon[" + style + "].");
  }


  
  public FlatCheckBoxIcon() {
    super(15, 15, null);
  }

  
  protected void paintIcon(Component c, Graphics2D g) {
    boolean indeterminate = isIndeterminate(c);
    boolean selected = (indeterminate || isSelected(c));
    boolean isFocused = FlatUIUtils.isPermanentFocusOwner(c);

    
    if (isFocused && this.focusWidth > 0 && FlatButtonUI.isFocusPainted(c)) {
      g.setColor(getFocusColor(c));
      paintFocusBorder(c, g);
    } 

    
    g.setColor(getBorderColor(c, selected));
    paintBorder(c, g);

    
    g.setColor(FlatUIUtils.deriveColor(getBackground(c, selected), selected ? this.selectedBackground : this.background));
    
    paintBackground(c, g);

    
    if (selected || indeterminate) {
      g.setColor(getCheckmarkColor(c, selected, isFocused));
      if (indeterminate) {
        paintIndeterminate(c, g);
      } else {
        paintCheckmark(c, g);
      } 
    } 
  }
  
  protected void paintFocusBorder(Component c, Graphics2D g) {
    int wh = 14 + this.focusWidth * 2;
    int arcwh = this.arc + this.focusWidth * 2;
    g.fillRoundRect(-this.focusWidth + 1, -this.focusWidth, wh, wh, arcwh, arcwh);
  }
  
  protected void paintBorder(Component c, Graphics2D g) {
    int arcwh = this.arc;
    g.fillRoundRect(1, 0, 14, 14, arcwh, arcwh);
  }
  
  protected void paintBackground(Component c, Graphics2D g) {
    int arcwh = this.arc - 1;
    g.fillRoundRect(2, 1, 12, 12, arcwh, arcwh);
  }
  
  protected void paintCheckmark(Component c, Graphics2D g) {
    Path2D.Float path = new Path2D.Float();
    path.moveTo(4.5F, 7.5F);
    path.lineTo(6.6F, 10.0F);
    path.lineTo(11.25F, 3.5F);
    
    g.setStroke(new BasicStroke(1.9F, 1, 1));
    g.draw(path);
  }
  
  protected void paintIndeterminate(Component c, Graphics2D g) {
    g.fill(new RoundRectangle2D.Float(3.75F, 5.75F, 8.5F, 2.5F, 2.0F, 2.0F));
  }
  
  protected boolean isIndeterminate(Component c) {
    return (c instanceof JComponent && FlatClientProperties.clientPropertyEquals((JComponent)c, "JButton.selectedState", "indeterminate"));
  }
  
  protected boolean isSelected(Component c) {
    return (c instanceof AbstractButton && ((AbstractButton)c).isSelected());
  }
  
  protected Color getFocusColor(Component c) {
    return this.focusColor;
  }
  
  protected Color getBorderColor(Component c, boolean selected) {
    return FlatButtonUI.buttonStateColor(c, selected ? this.selectedBorderColor : this.borderColor, this.disabledBorderColor, (selected && this.selectedFocusedBorderColor != null) ? this.selectedFocusedBorderColor : this.focusedBorderColor, this.hoverBorderColor, null);
  }





  
  protected Color getBackground(Component c, boolean selected) {
    return FlatButtonUI.buttonStateColor(c, selected ? this.selectedBackground : this.background, this.disabledBackground, (selected && this.selectedFocusedBackground != null) ? this.selectedFocusedBackground : this.focusedBackground, (selected && this.selectedHoverBackground != null) ? this.selectedHoverBackground : this.hoverBackground, (selected && this.selectedPressedBackground != null) ? this.selectedPressedBackground : this.pressedBackground);
  }





  
  protected Color getCheckmarkColor(Component c, boolean selected, boolean isFocused) {
    return c.isEnabled() ? ((selected && isFocused && this.selectedFocusedCheckmarkColor != null) ? this.selectedFocusedCheckmarkColor : this.checkmarkColor) : this.disabledCheckmarkColor;
  }
}
