package org.fife.rsta.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import org.fife.ui.autocomplete.EmptyIcon;






























public class DecorativeIconPanel
  extends JPanel
{
  private static final int DEFAULT_WIDTH = 8;
  private JLabel iconLabel;
  private boolean showIcon;
  private String tip;
  private EmptyIcon emptyIcon;
  
  public DecorativeIconPanel() {
    this(8);
  }









  
  public DecorativeIconPanel(int iconWidth) {
    setLayout(new BorderLayout());
    this.emptyIcon = new EmptyIcon(iconWidth);
    
    this.iconLabel = new JLabel((Icon)this.emptyIcon)
      {
        public String getToolTipText(MouseEvent e) {
          return DecorativeIconPanel.this.showIcon ? DecorativeIconPanel.this.tip : null;
        }
      };
    this.iconLabel.setVerticalAlignment(1);
    ToolTipManager.sharedInstance().registerComponent(this.iconLabel);
    add(this.iconLabel, "North");
  }







  
  public Icon getIcon() {
    return this.iconLabel.getIcon();
  }







  
  public boolean getShowIcon() {
    return this.showIcon;
  }









  
  public String getToolTipText() {
    return this.tip;
  }









  
  protected void paintChildren(Graphics g) {
    if (this.showIcon) {
      super.paintChildren(g);
    }
  }






  
  public void setIcon(Icon icon) {
    EmptyIcon emptyIcon;
    if (icon == null) {
      emptyIcon = this.emptyIcon;
    }
    this.iconLabel.setIcon((Icon)emptyIcon);
  }







  
  public void setShowIcon(boolean show) {
    if (show != this.showIcon) {
      this.showIcon = show;
      repaint();
    } 
  }









  
  public void setToolTipText(String tip) {
    this.tip = tip;
  }
}
