package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.function.Supplier;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
































public class FlatLabelUI
  extends BasicLabelUI
{
  private Color disabledForeground;
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent c) {
    return FlatUIUtils.createSharedUI(FlatLabelUI.class, FlatLabelUI::new);
  }

  
  protected void installDefaults(JLabel c) {
    super.installDefaults(c);
    
    if (!this.defaults_initialized) {
      this.disabledForeground = UIManager.getColor("Label.disabledForeground");
      
      this.defaults_initialized = true;
    } 
  }

  
  protected void uninstallDefaults(JLabel c) {
    super.uninstallDefaults(c);
    this.defaults_initialized = false;
  }

  
  protected void installComponents(JLabel c) {
    super.installComponents(c);

    
    updateHTMLRenderer(c, c.getText(), false);
  }

  
  public void propertyChange(PropertyChangeEvent e) {
    String name = e.getPropertyName();
    if (name == "text" || name == "font" || name == "foreground") {
      JLabel label = (JLabel)e.getSource();
      updateHTMLRenderer(label, label.getText(), true);
    } else {
      super.propertyChange(e);
    } 
  }



  
  static void updateHTMLRenderer(JComponent c, String text, boolean always) {
    if (BasicHTML.isHTMLString(text) && c
      .getClientProperty("html.disable") != Boolean.TRUE && text
      .contains("<h") && (text
      .contains("<h1") || text.contains("<h2") || text.contains("<h3") || text
      .contains("<h4") || text.contains("<h5") || text.contains("<h6"))) {
      
      int headIndex = text.indexOf("<head>");
      
      String style = "<style>BASE_SIZE " + c.getFont().getSize() + "</style>";
      if (headIndex < 0) {
        style = "<head>" + style + "</head>";
      }
      int insertIndex = (headIndex >= 0) ? (headIndex + "<head>".length()) : "<html>".length();

      
      text = text.substring(0, insertIndex) + style + text.substring(insertIndex);
    } else if (!always) {
      return;
    } 
    BasicHTML.updateRenderer(c, text);
  }
  
  static Graphics createGraphicsHTMLTextYCorrection(Graphics g, JComponent c) {
    return (c.getClientProperty("html") != null) ? 
      HiDPIUtils.createGraphicsTextYCorrection((Graphics2D)g) : g;
  }


  
  public void paint(Graphics g, JComponent c) {
    super.paint(createGraphicsHTMLTextYCorrection(g, c), c);
  }

  
  protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
    int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
    g.setColor(l.getForeground());
    FlatUIUtils.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
  }

  
  protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
    int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
    g.setColor(this.disabledForeground);
    FlatUIUtils.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
  }






  
  protected String layoutCL(JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR, Rectangle iconR, Rectangle textR) {
    return SwingUtilities.layoutCompoundLabel(label, fontMetrics, text, icon, label
        .getVerticalAlignment(), label.getHorizontalAlignment(), label
        .getVerticalTextPosition(), label.getHorizontalTextPosition(), viewR, iconR, textR, 
        
        UIScale.scale(label.getIconTextGap()));
  }
}
