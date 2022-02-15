package org.fife.rsta.ac.java;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;















































public class JavaCellRenderer
  extends DefaultListCellRenderer
{
  private JList<?> list;
  private boolean selected;
  private boolean evenRow;
  private JavaSourceCompletion jsc;
  private static Color altBG;
  private Completion nonJavaCompletion;
  private boolean simpleText;
  
  public static Color getAlternateBackground() {
    return altBG;
  }













  
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
    super.getListCellRendererComponent(list, value, index, selected, hasFocus);
    
    setText("Foobar");
    this.list = list;
    this.selected = selected;
    
    if (value instanceof JavaSourceCompletion) {
      this.jsc = (JavaSourceCompletion)value;
      this.nonJavaCompletion = null;
      setIcon(this.jsc.getIcon());
    } else {
      
      this.jsc = null;
      this.nonJavaCompletion = (Completion)value;
      setIcon(this.nonJavaCompletion.getIcon());
    } 
    
    this.evenRow = ((index & 0x1) == 0);
    if (altBG != null && this.evenRow && !selected) {
      setBackground(altBG);
    }
    
    return this;
  }




  
  protected void paintComponent(Graphics g) {
    Object old;
    Graphics2D g2d = (Graphics2D)g;


    
    Map<?, ?> hints = RSyntaxUtilities.getDesktopAntiAliasHints();
    if (hints != null) {
      old = g2d.getRenderingHints();
      g2d.addRenderingHints(hints);
    }
    else {
      
      old = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    } 






    
    int iconW = 18;
    int h = getHeight();
    if (!this.selected) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), h);
    } else {
      
      g.setColor((altBG != null && this.evenRow) ? altBG : this.list.getBackground());
      g.fillRect(0, 0, 18, h);
      g.setColor(getBackground());
      g.fillRect(18, 0, getWidth() - 18, h);
    } 
    if (getIcon() != null) {
      int y = (h - getIcon().getIconHeight()) / 2;
      getIcon().paintIcon(this, g, 0, y);
    } 
    
    int x = getX() + 18 + 2;
    g.setColor(this.selected ? this.list.getSelectionForeground() : this.list
        .getForeground());
    if (this.jsc != null && !this.simpleText) {
      this.jsc.rendererText(g, x, g.getFontMetrics().getHeight(), this.selected);
    } else {
      
      Completion c = (this.jsc != null) ? this.jsc : this.nonJavaCompletion;
      if (c != null) {
        g.drawString(c.toString(), x, g.getFontMetrics().getHeight());
      }
    } 

    
    if (hints != null) {
      if (old instanceof Map) {
        g2d.addRenderingHints((Map<?, ?>)old);
      } else {
        
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, old);
      } 
    } else {
      
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, old);
    } 
  }










  
  public static void setAlternateBackground(Color altBG) {
    JavaCellRenderer.altBG = altBG;
  }







  
  public void setSimpleText(boolean simple) {
    this.simpleText = simple;
  }
}
