package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.lf5.LogRecord;





































public class CategoryExplorerModel
  extends DefaultTreeModel
{
  private static final long serialVersionUID = -3413887384316015901L;
  protected boolean _renderFatal = true;
  protected ActionListener _listener = null;
  protected ActionEvent _event = new ActionEvent(this, 1001, "Nodes Selection changed");










  
  public CategoryExplorerModel(CategoryNode node) {
    super(node);
  }



  
  public void addLogRecord(LogRecord lr) {
    CategoryPath path = new CategoryPath(lr.getCategory());
    addCategory(path);
    CategoryNode node = getCategoryNode(path);
    node.addRecord();
    if (this._renderFatal && lr.isFatal()) {
      TreeNode[] nodes = getPathToRoot(node);
      int len = nodes.length;



      
      for (int i = 1; i < len - 1; i++) {
        CategoryNode parent = (CategoryNode)nodes[i];
        parent.setHasFatalChildren(true);
        nodeChanged(parent);
      } 
      node.setHasFatalRecords(true);
      nodeChanged(node);
    } 
  }
  
  public CategoryNode getRootCategoryNode() {
    return (CategoryNode)getRoot();
  }
  
  public CategoryNode getCategoryNode(String category) {
    CategoryPath path = new CategoryPath(category);
    return getCategoryNode(path);
  }



  
  public CategoryNode getCategoryNode(CategoryPath path) {
    CategoryNode root = (CategoryNode)getRoot();
    CategoryNode parent = root;
    
    for (int i = 0; i < path.size(); i++) {
      CategoryElement element = path.categoryElementAt(i);

      
      Enumeration children = parent.children();
      
      boolean categoryAlreadyExists = false;
      while (children.hasMoreElements()) {
        CategoryNode node = children.nextElement();
        String title = node.getTitle().toLowerCase();
        
        String pathLC = element.getTitle().toLowerCase();
        if (title.equals(pathLC)) {
          categoryAlreadyExists = true;
          
          parent = node;
          
          break;
        } 
      } 
      if (!categoryAlreadyExists) {
        return null;
      }
    } 
    
    return parent;
  }




  
  public boolean isCategoryPathActive(CategoryPath path) {
    CategoryNode root = (CategoryNode)getRoot();
    CategoryNode parent = root;
    boolean active = false;
    
    for (int i = 0; i < path.size(); i++) {
      CategoryElement element = path.categoryElementAt(i);

      
      Enumeration children = parent.children();
      
      boolean categoryAlreadyExists = false;
      active = false;
      
      while (children.hasMoreElements()) {
        CategoryNode node = children.nextElement();
        String title = node.getTitle().toLowerCase();
        
        String pathLC = element.getTitle().toLowerCase();
        if (title.equals(pathLC)) {
          categoryAlreadyExists = true;
          
          parent = node;
          
          if (parent.isSelected()) {
            active = true;
          }
          
          break;
        } 
      } 
      
      if (!active || !categoryAlreadyExists) {
        return false;
      }
    } 
    
    return active;
  }








  
  public CategoryNode addCategory(CategoryPath path) {
    CategoryNode root = (CategoryNode)getRoot();
    CategoryNode parent = root;
    
    for (int i = 0; i < path.size(); i++) {
      CategoryElement element = path.categoryElementAt(i);

      
      Enumeration children = parent.children();
      
      boolean categoryAlreadyExists = false;
      while (children.hasMoreElements()) {
        CategoryNode node = children.nextElement();
        String title = node.getTitle().toLowerCase();
        
        String pathLC = element.getTitle().toLowerCase();
        if (title.equals(pathLC)) {
          categoryAlreadyExists = true;
          
          parent = node;
          
          break;
        } 
      } 
      if (!categoryAlreadyExists) {
        
        CategoryNode newNode = new CategoryNode(element.getTitle());






        
        insertNodeInto(newNode, parent, parent.getChildCount());
        refresh(newNode);

        
        parent = newNode;
      } 
    } 

    
    return parent;
  }
  
  public void update(CategoryNode node, boolean selected) {
    if (node.isSelected() == selected) {
      return;
    }
    
    if (selected) {
      setParentSelection(node, true);
    } else {
      setDescendantSelection(node, false);
    } 
  }
  
  public void setDescendantSelection(CategoryNode node, boolean selected) {
    Enumeration descendants = node.depthFirstEnumeration();
    
    while (descendants.hasMoreElements()) {
      CategoryNode current = descendants.nextElement();
      
      if (current.isSelected() != selected) {
        current.setSelected(selected);
        nodeChanged(current);
      } 
    } 
    notifyActionListeners();
  }
  
  public void setParentSelection(CategoryNode node, boolean selected) {
    TreeNode[] nodes = getPathToRoot(node);
    int len = nodes.length;



    
    for (int i = 1; i < len; i++) {
      CategoryNode parent = (CategoryNode)nodes[i];
      if (parent.isSelected() != selected) {
        parent.setSelected(selected);
        nodeChanged(parent);
      } 
    } 
    notifyActionListeners();
  }

  
  public synchronized void addActionListener(ActionListener l) {
    this._listener = AWTEventMulticaster.add(this._listener, l);
  }
  
  public synchronized void removeActionListener(ActionListener l) {
    this._listener = AWTEventMulticaster.remove(this._listener, l);
  }
  
  public void resetAllNodeCounts() {
    Enumeration nodes = getRootCategoryNode().depthFirstEnumeration();
    
    while (nodes.hasMoreElements()) {
      CategoryNode current = nodes.nextElement();
      current.resetNumberOfContainedRecords();
      nodeChanged(current);
    } 
  }






  
  public TreePath getTreePathToRoot(CategoryNode node) {
    if (node == null) {
      return null;
    }
    return new TreePath((Object[])getPathToRoot(node));
  }



  
  protected void notifyActionListeners() {
    if (this._listener != null) {
      this._listener.actionPerformed(this._event);
    }
  }



  
  protected void refresh(final CategoryNode node) {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            CategoryExplorerModel.this.nodeChanged(node);
          }
          
          private final CategoryNode val$node;
          private final CategoryExplorerModel this$0;
        });
  }
}
