package org.fife.rsta.ui;

import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.JPanel;























public class ResizableFrameContentPane
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  private SizeGripIcon gripIcon;
  
  public ResizableFrameContentPane() {
    this.gripIcon = new SizeGripIcon();
  }






  
  public ResizableFrameContentPane(LayoutManager layout) {
    super(layout);
    this.gripIcon = new SizeGripIcon();
  }












  
  public void paint(Graphics g) {
    super.paint(g);
    this.gripIcon.paintIcon(this, g, getX(), getY());
  }
}
