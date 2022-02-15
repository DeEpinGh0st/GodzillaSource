package org.mozilla.javascript.tools.debugger.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;











































public class TreeTableModelAdapter
  extends AbstractTableModel
{
  private static final long serialVersionUID = 48741114609209052L;
  JTree tree;
  TreeTableModel treeTableModel;
  
  public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
    this.tree = tree;
    this.treeTableModel = treeTableModel;
    
    tree.addTreeExpansionListener(new TreeExpansionListener()
        {
          public void treeExpanded(TreeExpansionEvent event)
          {
            TreeTableModelAdapter.this.fireTableDataChanged();
          }
          public void treeCollapsed(TreeExpansionEvent event) {
            TreeTableModelAdapter.this.fireTableDataChanged();
          }
        });




    
    treeTableModel.addTreeModelListener(new TreeModelListener() {
          public void treeNodesChanged(TreeModelEvent e) {
            TreeTableModelAdapter.this.delayedFireTableDataChanged();
          }
          
          public void treeNodesInserted(TreeModelEvent e) {
            TreeTableModelAdapter.this.delayedFireTableDataChanged();
          }
          
          public void treeNodesRemoved(TreeModelEvent e) {
            TreeTableModelAdapter.this.delayedFireTableDataChanged();
          }
          
          public void treeStructureChanged(TreeModelEvent e) {
            TreeTableModelAdapter.this.delayedFireTableDataChanged();
          }
        });
  }


  
  public int getColumnCount() {
    return this.treeTableModel.getColumnCount();
  }

  
  public String getColumnName(int column) {
    return this.treeTableModel.getColumnName(column);
  }

  
  public Class<?> getColumnClass(int column) {
    return this.treeTableModel.getColumnClass(column);
  }
  
  public int getRowCount() {
    return this.tree.getRowCount();
  }
  
  protected Object nodeForRow(int row) {
    TreePath treePath = this.tree.getPathForRow(row);
    return treePath.getLastPathComponent();
  }
  
  public Object getValueAt(int row, int column) {
    return this.treeTableModel.getValueAt(nodeForRow(row), column);
  }

  
  public boolean isCellEditable(int row, int column) {
    return this.treeTableModel.isCellEditable(nodeForRow(row), column);
  }

  
  public void setValueAt(Object value, int row, int column) {
    this.treeTableModel.setValueAt(value, nodeForRow(row), column);
  }




  
  protected void delayedFireTableDataChanged() {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            TreeTableModelAdapter.this.fireTableDataChanged();
          }
        });
  }
}
