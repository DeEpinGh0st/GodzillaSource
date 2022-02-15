package org.apache.log4j.lf5.viewer;

import java.awt.Adjustable;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;






















































public class LF5SwingUtils
{
  public static void selectRow(int row, JTable table, JScrollPane pane) {
    if (table == null || pane == null) {
      return;
    }
    if (!contains(row, table.getModel())) {
      return;
    }
    moveAdjustable(row * table.getRowHeight(), pane.getVerticalScrollBar());
    selectRow(row, table.getSelectionModel());


    
    repaintLater(table);
  }




  
  public static void makeScrollBarTrack(Adjustable scrollBar) {
    if (scrollBar == null) {
      return;
    }
    scrollBar.addAdjustmentListener(new TrackingAdjustmentListener());
  }





  
  public static void makeVerticalScrollBarTrack(JScrollPane pane) {
    if (pane == null) {
      return;
    }
    makeScrollBarTrack(pane.getVerticalScrollBar());
  }



  
  protected static boolean contains(int row, TableModel model) {
    if (model == null) {
      return false;
    }
    if (row < 0) {
      return false;
    }
    if (row >= model.getRowCount()) {
      return false;
    }
    return true;
  }
  
  protected static void selectRow(int row, ListSelectionModel model) {
    if (model == null) {
      return;
    }
    model.setSelectionInterval(row, row);
  }
  
  protected static void moveAdjustable(int location, Adjustable scrollBar) {
    if (scrollBar == null) {
      return;
    }
    scrollBar.setValue(location);
  }




  
  protected static void repaintLater(final JComponent component) {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            component.repaint();
          }
          
          private final JComponent val$component;
        });
  }
}
