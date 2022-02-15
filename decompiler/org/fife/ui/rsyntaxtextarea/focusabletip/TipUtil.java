package org.fife.ui.rsyntaxtextarea.focusabletip;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.SystemColor;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.html.HTMLDocument;
import org.fife.ui.rsyntaxtextarea.HtmlUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rtextarea.RTextArea;


































public final class TipUtil
{
  public static Rectangle getScreenBoundsForPoint(int x, int y) {
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] devices = env.getScreenDevices();
    for (GraphicsDevice device : devices) {
      GraphicsConfiguration[] configs = device.getConfigurations();
      for (GraphicsConfiguration config : configs) {
        Rectangle gcBounds = config.getBounds();
        if (gcBounds.contains(x, y)) {
          return gcBounds;
        }
      } 
    } 
    
    return env.getMaximumWindowBounds();
  }








  
  public static Color getToolTipBackground() {
    return getToolTipBackground(null);
  }























  
  public static Color getToolTipBackground(RTextArea textArea) {
    if (textArea != null && !Color.WHITE.equals(textArea.getBackground())) {
      return textArea.getBackground();
    }
    
    Color c = UIManager.getColor("ToolTip.background");

    
    boolean isNimbus = isNimbusLookAndFeel();
    if (c == null || isNimbus) {
      c = UIManager.getColor("info");
      if (c == null || (isNimbus && isDerivedColor(c))) {
        c = SystemColor.info;
      }
    } 


    
    if (c instanceof javax.swing.plaf.ColorUIResource) {
      c = new Color(c.getRGB());
    }
    
    return c;
  }









  
  public static Border getToolTipBorder() {
    return getToolTipBorder(null);
  }























  
  public static Border getToolTipBorder(RTextArea textArea) {
    if (textArea != null && !Color.WHITE.equals(textArea.getBackground())) {
      Color color = textArea.getBackground();
      if (color != null) {
        return BorderFactory.createLineBorder(color.brighter());
      }
    } 
    
    Border border = UIManager.getBorder("ToolTip.border");
    
    if (border == null || isNimbusLookAndFeel()) {
      border = UIManager.getBorder("nimbusBorder");
      if (border == null) {
        border = BorderFactory.createLineBorder(SystemColor.controlDkShadow);
      }
    } 
    
    return border;
  }










  
  private static boolean isDerivedColor(Color c) {
    return (c != null && c.getClass().getName().endsWith(".DerivedColor"));
  }






  
  private static boolean isNimbusLookAndFeel() {
    return UIManager.getLookAndFeel().getName().equals("Nimbus");
  }










  
  public static void tweakTipEditorPane(JEditorPane textArea) {
    boolean isNimbus = isNimbusLookAndFeel();
    if (isNimbus) {
      Color selBG = textArea.getSelectionColor();
      Color selFG = textArea.getSelectedTextColor();
      textArea.setUI(new BasicEditorPaneUI());
      textArea.setSelectedTextColor(selFG);
      textArea.setSelectionColor(selBG);
    } 
    
    textArea.setEditable(false);
    textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    
    textArea.getCaret().setSelectionVisible(true);



    
    Color fg = UIManager.getColor("Label.foreground");
    if (fg == null || (isNimbus && isDerivedColor(fg))) {
      fg = SystemColor.textText;
    }
    textArea.setForeground(fg);

    
    textArea.setBackground(getToolTipBackground());


    
    Font font = UIManager.getFont("Label.font");
    if (font == null) {
      font = new Font("SansSerif", 0, 12);
    }
    HTMLDocument doc = (HTMLDocument)textArea.getDocument();
    setFont(doc, font, fg);



    
    Color linkFG = RSyntaxUtilities.getHyperlinkForeground();
    doc.getStyleSheet().addRule("a { color: " + 
        HtmlUtil.getHexString(linkFG) + "; }");
  }











  
  public static void setFont(HTMLDocument doc, Font font, Color fg) {
    doc.getStyleSheet().addRule("body { font-family: " + font
        .getFamily() + "; font-size: " + font
        .getSize() + "pt; color: " + 
        HtmlUtil.getHexString(fg) + "; }");
  }
}
