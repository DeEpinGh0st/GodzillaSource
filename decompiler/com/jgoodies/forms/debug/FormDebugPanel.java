package com.jgoodies.forms.debug;

import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.JPanel;

























































public class FormDebugPanel
  extends JPanel
{
  public static boolean paintRowsDefault = true;
  private static final Color DEFAULT_GRID_COLOR = Color.red;





  
  private boolean paintInBackground;




  
  private boolean paintDiagonals;




  
  private boolean paintRows = paintRowsDefault;




  
  private Color gridColor = DEFAULT_GRID_COLOR;






  
  public FormDebugPanel() {
    this((FormLayout)null);
  }







  
  public FormDebugPanel(FormLayout layout) {
    this(layout, false, false);
  }













  
  public FormDebugPanel(boolean paintInBackground, boolean paintDiagonals) {
    this((FormLayout)null, paintInBackground, paintDiagonals);
  }
















  
  public FormDebugPanel(FormLayout layout, boolean paintInBackground, boolean paintDiagonals) {
    super((LayoutManager)layout);
    setPaintInBackground(paintInBackground);
    setPaintDiagonals(paintDiagonals);
    setGridColor(DEFAULT_GRID_COLOR);
  }








  
  public void setPaintInBackground(boolean b) {
    this.paintInBackground = b;
  }





  
  public void setPaintDiagonals(boolean b) {
    this.paintDiagonals = b;
  }








  
  public void setPaintRows(boolean b) {
    this.paintRows = b;
  }





  
  public void setGridColor(Color color) {
    this.gridColor = color;
  }













  
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (this.paintInBackground) {
      paintGrid(g);
    }
  }












  
  public void paint(Graphics g) {
    super.paint(g);
    if (!this.paintInBackground) {
      paintGrid(g);
    }
  }






  
  private void paintGrid(Graphics g) {
    if (!(getLayout() instanceof FormLayout)) {
      return;
    }
    FormLayout.LayoutInfo layoutInfo = FormDebugUtils.getLayoutInfo(this);
    int left = layoutInfo.getX();
    int top = layoutInfo.getY();
    int width = layoutInfo.getWidth();
    int height = layoutInfo.getHeight();
    
    g.setColor(this.gridColor);

    
    int last = layoutInfo.columnOrigins.length - 1;
    for (int col = 0; col <= last; col++) {
      boolean firstOrLast = (col == 0 || col == last);
      int x = layoutInfo.columnOrigins[col];
      int start = firstOrLast ? 0 : top;
      int stop = firstOrLast ? getHeight() : (top + height);
      for (int i = start; i < stop; i += 5) {
        int length = Math.min(3, stop - i);
        g.fillRect(x, i, 1, length);
      } 
    } 

    
    last = layoutInfo.rowOrigins.length - 1;
    for (int row = 0; row <= last; row++) {
      boolean firstOrLast = (row == 0 || row == last);
      int y = layoutInfo.rowOrigins[row];
      int start = firstOrLast ? 0 : left;
      int stop = firstOrLast ? getWidth() : (left + width);
      if (firstOrLast || this.paintRows) {
        for (int i = start; i < stop; i += 5) {
          int length = Math.min(3, stop - i);
          g.fillRect(i, y, length, 1);
        } 
      }
    } 
    
    if (this.paintDiagonals) {
      g.drawLine(left, top, left + width, top + height);
      g.drawLine(left, top + height, left + width, top);
    } 
  }
}
