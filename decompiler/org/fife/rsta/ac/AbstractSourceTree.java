package org.fife.rsta.ac;

import java.util.Enumeration;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;








































public abstract class AbstractSourceTree
  extends JTree
{
  protected RSyntaxTextArea textArea;
  private boolean sorted;
  private Pattern pattern;
  private boolean gotoSelectedElementOnClick;
  private boolean showMajorElementsOnly;
  
  public AbstractSourceTree() {
    getSelectionModel().setSelectionMode(1);
    
    this.gotoSelectedElementOnClick = true;
    this.showMajorElementsOnly = false;
  }








  
  public abstract void expandInitialNodes();







  
  protected boolean fastExpandAll(TreePath parent, boolean expand) {
    TreeExpansionListener[] listeners = getTreeExpansionListeners();
    for (TreeExpansionListener listener : listeners) {
      removeTreeExpansionListener(listener);
    }
    
    boolean result = fastExpandAllImpl(parent, expand);
    
    for (TreeExpansionListener listener : listeners) {
      addTreeExpansionListener(listener);
    }

    
    collapsePath(parent);
    expandPath(parent);
    
    return result;
  }


  
  private boolean fastExpandAllImpl(TreePath parent, boolean expand) {
    TreeNode node = (TreeNode)parent.getLastPathComponent();
    if (node.getChildCount() > 0) {
      boolean childExpandCalled = false;
      for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
        TreeNode n = (TreeNode)e.nextElement();
        TreePath path = parent.pathByAddingChild(n);

        
        childExpandCalled = (fastExpandAllImpl(path, expand) || childExpandCalled);
      } 


      
      if (!childExpandCalled)
      {
        
        if (expand) {
          expandPath(parent);
        } else {
          
          collapsePath(parent);
        } 
      }
      return true;
    } 
    
    return false;
  }








  
  public void filter(String pattern) {
    if ((pattern == null && this.pattern != null) || (pattern != null && this.pattern == null) || (pattern != null && 
      
      !pattern.equals(this.pattern.pattern()))) {
      this
        .pattern = (pattern == null || pattern.length() == 0) ? null : RSyntaxUtilities.wildcardToPattern("^" + pattern, false, false);
      Object root = getModel().getRoot();
      if (root instanceof SourceTreeNode) {
        ((SourceTreeNode)root).filter(this.pattern);
      }
      ((DefaultTreeModel)getModel()).reload();
      expandInitialNodes();
    } 
  }








  
  public boolean getGotoSelectedElementOnClick() {
    return this.gotoSelectedElementOnClick;
  }









  
  public boolean getShowMajorElementsOnly() {
    return this.showMajorElementsOnly;
  }







  
  public abstract boolean gotoSelectedElement();






  
  public boolean isSorted() {
    return this.sorted;
  }








  
  public abstract void listenTo(RSyntaxTextArea paramRSyntaxTextArea);








  
  public void refresh() {
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    Object root = model.getRoot();
    if (root instanceof SourceTreeNode) {
      SourceTreeNode node = (SourceTreeNode)root;
      node.refresh();
      
      model.reload();
      expandInitialNodes();
    } 
  }





  
  public void selectFirstNodeMatchingFilter() {
    if (this.pattern == null) {
      return;
    }
    
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
    Enumeration<?> en = root.depthFirstEnumeration();
    
    while (en.hasMoreElements()) {
      SourceTreeNode stn = (SourceTreeNode)en.nextElement();
      
      JLabel renderer = (JLabel)getCellRenderer().getTreeCellRendererComponent(this, stn, true, true, stn
          .isLeaf(), 0, true);
      String text = renderer.getText();
      if (text != null && this.pattern.matcher(text).find()) {
        setSelectionPath(new TreePath((Object[])model.getPathToRoot(stn)));
        return;
      } 
    } 
  }







  
  public void selectNextVisibleRow() {
    int currentRow = getLeadSelectionRow();
    if (++currentRow < getRowCount()) {
      TreePath path = getPathForRow(currentRow);
      setSelectionPath(path);
      scrollPathToVisible(path);
    } 
  }






  
  public void selectPreviousVisibleRow() {
    int currentRow = getLeadSelectionRow();
    if (--currentRow >= 0) {
      TreePath path = getPathForRow(currentRow);
      setSelectionPath(path);
      scrollPathToVisible(path);
    } 
  }









  
  public void setGotoSelectedElementOnClick(boolean gotoSelectedElement) {
    this.gotoSelectedElementOnClick = gotoSelectedElement;
  }









  
  public void setShowMajorElementsOnly(boolean show) {
    this.showMajorElementsOnly = show;
  }







  
  public void setSorted(boolean sorted) {
    if (this.sorted != sorted) {
      this.sorted = sorted;
      Object root = getModel().getRoot();
      if (root instanceof SourceTreeNode) {
        ((SourceTreeNode)root).setSorted(sorted);
      }
      ((DefaultTreeModel)getModel()).reload();
      expandInitialNodes();
    } 
  }
  
  public abstract void uninstall();
}
