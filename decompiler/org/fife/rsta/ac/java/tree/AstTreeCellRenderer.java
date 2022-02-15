package org.fife.rsta.ac.java.tree;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;





















class AstTreeCellRenderer
  extends DefaultTreeCellRenderer
{
  private static final long serialVersionUID = 1L;
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    
    if (value instanceof JavaTreeNode) {
      JavaTreeNode node = (JavaTreeNode)value;
      setText(node.getText(sel));
      setIcon(node.getIcon());
    } 
    return this;
  }
}
