package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;




































public class FlatHelpButtonIcon
  extends FlatAbstractIcon
{
  protected final int focusWidth = UIManager.getInt("Component.focusWidth");
  protected final Color focusColor = UIManager.getColor("Component.focusColor");
  
  protected final Color borderColor = UIManager.getColor("HelpButton.borderColor");
  protected final Color disabledBorderColor = UIManager.getColor("HelpButton.disabledBorderColor");
  protected final Color focusedBorderColor = UIManager.getColor("HelpButton.focusedBorderColor");
  protected final Color hoverBorderColor = UIManager.getColor("HelpButton.hoverBorderColor");
  protected final Color background = UIManager.getColor("HelpButton.background");
  protected final Color disabledBackground = UIManager.getColor("HelpButton.disabledBackground");
  protected final Color focusedBackground = UIManager.getColor("HelpButton.focusedBackground");
  protected final Color hoverBackground = UIManager.getColor("HelpButton.hoverBackground");
  protected final Color pressedBackground = UIManager.getColor("HelpButton.pressedBackground");
  protected final Color questionMarkColor = UIManager.getColor("HelpButton.questionMarkColor");
  protected final Color disabledQuestionMarkColor = UIManager.getColor("HelpButton.disabledQuestionMarkColor");
  
  protected final int iconSize = 22 + this.focusWidth * 2;
  
  public FlatHelpButtonIcon() {
    super(0, 0, null);
  }











  
  protected void paintIcon(Component c, Graphics2D g2) {
    boolean enabled = c.isEnabled();
    boolean focused = FlatUIUtils.isPermanentFocusOwner(c);

    
    if (focused && FlatButtonUI.isFocusPainted(c)) {
      g2.setColor(this.focusColor);
      g2.fill(new Ellipse2D.Float(0.5F, 0.5F, (this.iconSize - 1), (this.iconSize - 1)));
    } 

    
    g2.setColor(FlatButtonUI.buttonStateColor(c, this.borderColor, this.disabledBorderColor, this.focusedBorderColor, this.hoverBorderColor, null));




    
    g2.fill(new Ellipse2D.Float(this.focusWidth + 0.5F, this.focusWidth + 0.5F, 21.0F, 21.0F));

    
    g2.setColor(FlatUIUtils.deriveColor(FlatButtonUI.buttonStateColor(c, this.background, this.disabledBackground, this.focusedBackground, this.hoverBackground, this.pressedBackground), this.background));




    
    g2.fill(new Ellipse2D.Float(this.focusWidth + 1.5F, this.focusWidth + 1.5F, 19.0F, 19.0F));

    
    Path2D q = new Path2D.Float();
    q.moveTo(11.0D, 5.0D);
    q.curveTo(8.8D, 5.0D, 7.0D, 6.8D, 7.0D, 9.0D);
    q.lineTo(9.0D, 9.0D);
    q.curveTo(9.0D, 7.9D, 9.9D, 7.0D, 11.0D, 7.0D);
    q.curveTo(12.1D, 7.0D, 13.0D, 7.9D, 13.0D, 9.0D);
    q.curveTo(13.0D, 11.0D, 10.0D, 10.75D, 10.0D, 14.0D);
    q.lineTo(12.0D, 14.0D);
    q.curveTo(12.0D, 11.75D, 15.0D, 11.5D, 15.0D, 9.0D);
    q.curveTo(15.0D, 6.8D, 13.2D, 5.0D, 11.0D, 5.0D);
    q.closePath();
    
    g2.translate(this.focusWidth, this.focusWidth);
    g2.setColor(enabled ? this.questionMarkColor : this.disabledQuestionMarkColor);
    g2.fill(q);
    g2.fillRect(10, 15, 2, 2);
  }

  
  public int getIconWidth() {
    return UIScale.scale(this.iconSize);
  }

  
  public int getIconHeight() {
    return UIScale.scale(this.iconSize);
  }
}
