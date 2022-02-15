package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreePath;
































public class CategoryImmediateEditor
  extends DefaultTreeCellEditor
{
  private CategoryNodeRenderer renderer;
  protected Icon editingIcon = null;









  
  public CategoryImmediateEditor(JTree tree, CategoryNodeRenderer renderer, CategoryNodeEditor editor) {
    super(tree, renderer, editor);
    this.renderer = renderer;
    renderer.setIcon(null);
    renderer.setLeafIcon((Icon)null);
    renderer.setOpenIcon((Icon)null);
    renderer.setClosedIcon((Icon)null);
    
    super.editingIcon = null;
  }



  
  public boolean shouldSelectCell(EventObject e) {
    boolean rv = false;
    
    if (e instanceof MouseEvent) {
      MouseEvent me = (MouseEvent)e;
      TreePath path = this.tree.getPathForLocation(me.getX(), me.getY());
      
      CategoryNode node = (CategoryNode)path.getLastPathComponent();

      
      rv = node.isLeaf();
    } 
    return rv;
  }
  
  public boolean inCheckBoxHitRegion(MouseEvent e) {
    TreePath path = this.tree.getPathForLocation(e.getX(), e.getY());
    
    if (path == null) {
      return false;
    }
    CategoryNode node = (CategoryNode)path.getLastPathComponent();
    boolean rv = false;




    
    Rectangle bounds = this.tree.getRowBounds(this.lastRow);
    Dimension checkBoxOffset = this.renderer.getCheckBoxOffset();

    
    bounds.translate(this.offset + checkBoxOffset.width, checkBoxOffset.height);

    
    rv = bounds.contains(e.getPoint());
    
    return true;
  }




  
  protected boolean canEditImmediately(EventObject e) {
    boolean rv = false;
    
    if (e instanceof MouseEvent) {
      MouseEvent me = (MouseEvent)e;
      rv = inCheckBoxHitRegion(me);
    } 
    
    return rv;
  }



  
  protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
    this.offset = 0;
  }
}
