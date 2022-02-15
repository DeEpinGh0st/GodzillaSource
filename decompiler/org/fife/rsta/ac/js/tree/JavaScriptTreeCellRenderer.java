package org.fife.rsta.ac.js.tree;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;





















class JavaScriptTreeCellRenderer
  extends DefaultTreeCellRenderer
{
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    
    if (value instanceof JavaScriptTreeNode) {
      JavaScriptTreeNode node = (JavaScriptTreeNode)value;
      setText(node.getText(sel));
      setIcon(node.getIcon());
    } 
    return this;
  }
}
