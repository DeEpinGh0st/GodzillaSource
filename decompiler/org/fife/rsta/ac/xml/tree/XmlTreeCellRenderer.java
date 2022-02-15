package org.fife.rsta.ac.xml.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;



















class XmlTreeCellRenderer
  extends DefaultTreeCellRenderer
{
  private Icon elemIcon;
  private String elem;
  private String attr;
  private boolean selected;
  private static final XmlTreeCellUI UI = new XmlTreeCellUI();
  private static final Color ATTR_COLOR = new Color(8421504);

  
  public XmlTreeCellRenderer() {
    URL url = getClass().getResource("tag.png");
    if (url != null) {
      this.elemIcon = new ImageIcon(url);
    }
    setUI(UI);
  }




  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focused) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focused);
    
    this.selected = sel;
    if (value instanceof XmlTreeNode) {
      
      XmlTreeNode node = (XmlTreeNode)value;
      this.elem = node.getElement();
      this.attr = node.getMainAttr();
    } else {
      
      this.elem = this.attr = null;
    } 
    setIcon(this.elemIcon);
    return this;
  }




  
  public void updateUI() {
    super.updateUI();
    setUI(UI);
  }





  
  private static class XmlTreeCellUI
    extends BasicLabelUI
  {
    private XmlTreeCellUI() {}




    
    protected void installDefaults(JLabel label) {}




    
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
      XmlTreeCellRenderer r = (XmlTreeCellRenderer)l;
      Graphics2D g2d = (Graphics2D)g;
      Map<?, ?> hints = RSyntaxUtilities.getDesktopAntiAliasHints();
      if (hints != null) {
        g2d.addRenderingHints(hints);
      }
      g2d.setColor(l.getForeground());
      g2d.drawString(r.elem, textX, textY);
      if (r.attr != null) {
        textX += g2d.getFontMetrics().stringWidth(r.elem + " ");
        if (!r.selected) {
          g2d.setColor(XmlTreeCellRenderer.ATTR_COLOR);
        }
        g2d.drawString(r.attr, textX, textY);
      } 
      g2d.dispose();
    }
    
    protected void uninstallDefaults(JLabel label) {}
  }
}
