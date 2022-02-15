package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;
import org.fife.ui.rtextarea.ChangeableHighlightPainter;





























public class SquiggleUnderlineHighlightPainter
  extends ChangeableHighlightPainter
{
  private static final int AMT = 2;
  
  public SquiggleUnderlineHighlightPainter(Color color) {
    super(color);
    setPaint(color);
  }
















  
  public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
    g.setColor((Color)getPaint());
    
    if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
      Rectangle alloc;
      
      if (bounds instanceof Rectangle) {
        alloc = (Rectangle)bounds;
      } else {
        
        alloc = bounds.getBounds();
      } 
      paintSquiggle(g, alloc);
      return alloc;
    } 


    
    try {
      Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);


      
      Rectangle r = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
      paintSquiggle(g, r);
      return r;
    } catch (BadLocationException e) {
      e.printStackTrace();


      
      return null;
    } 
  }







  
  protected void paintSquiggle(Graphics g, Rectangle r) {
    int x = r.x;
    int y = r.y + r.height - 2;
    int delta = -2;
    while (x < r.x + r.width) {
      g.drawLine(x, y, x + 2, y + delta);
      y += delta;
      delta = -delta;
      x += 2;
    } 
  }
}
