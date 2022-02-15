package com.formdev.flatlaf.demo;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;
























public class ScrollablePanel
  extends JPanel
  implements Scrollable
{
  public Dimension getPreferredScrollableViewportSize() {
    return UIScale.scale(new Dimension(400, 400));
  }

  
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return UIScale.scale(50);
  }

  
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    return (orientation == 1) ? visibleRect.height : visibleRect.width;
  }

  
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
}
