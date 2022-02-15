package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


























public class ColorBackgroundPainterStrategy
  implements BackgroundPainterStrategy
{
  private Color color;
  
  public ColorBackgroundPainterStrategy(Color color) {
    setColor(color);
  }











  
  public boolean equals(Object o2) {
    return (o2 instanceof ColorBackgroundPainterStrategy && this.color
      .equals(((ColorBackgroundPainterStrategy)o2)
        .getColor()));
  }







  
  public Color getColor() {
    return this.color;
  }









  
  public int hashCode() {
    return this.color.hashCode();
  }









  
  public void paint(Graphics g, Rectangle bounds) {
    Color temp = g.getColor();
    g.setColor(this.color);
    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    g.setColor(temp);
  }







  
  public void setColor(Color color) {
    this.color = color;
  }
}
