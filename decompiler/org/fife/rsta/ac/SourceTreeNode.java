package org.fife.rsta.ac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.fife.ui.autocomplete.Util;






















public class SourceTreeNode
  extends DefaultMutableTreeNode
  implements Comparable<SourceTreeNode>
{
  private boolean sortable;
  private boolean sorted;
  private Pattern pattern;
  private List<TreeNode> visibleChildren;
  private int sortPriority;
  
  public SourceTreeNode(Object userObject) {
    this(userObject, false);
  }








  
  public SourceTreeNode(Object userObject, boolean sorted) {
    super(userObject);
    this.visibleChildren = new ArrayList<>();
    setSortable(true);
    setSorted(sorted);
  }









  
  public void add(MutableTreeNode child) {
    if (child != null && child.getParent() == this) {
      insert(child, super.getChildCount() - 1);
    } else {
      
      insert(child, super.getChildCount());
    } 
    if (this.sortable && this.sorted) {
      refreshVisibleChildren();
    }
  }







  
  public Enumeration<TreeNode> children() {
    return Collections.enumeration(this.visibleChildren);
  }









  
  public Object clone() {
    SourceTreeNode node = (SourceTreeNode)super.clone();
    
    node.visibleChildren = new ArrayList<>();
    return node;
  }







  
  public SourceTreeNode cloneWithChildren() {
    SourceTreeNode clone = (SourceTreeNode)clone();
    for (int i = 0; i < super.getChildCount(); i++) {
      clone.add(((SourceTreeNode)super.getChildAt(i)).cloneWithChildren());
    }
    return clone;
  }












  
  public int compareTo(SourceTreeNode stn2) {
    int res = -1;
    if (stn2 != null) {
      res = getSortPriority() - stn2.getSortPriority();
      if (res == 0 && ((SourceTreeNode)getParent()).isSorted()) {
        res = toString().compareToIgnoreCase(stn2.toString());
      }
    } 
    return res;
  }







  
  protected void filter(Pattern pattern) {
    this.pattern = pattern;
    refreshVisibleChildren();
    for (int i = 0; i < super.getChildCount(); i++) {
      Object child = this.children.get(i);
      if (child instanceof SourceTreeNode) {
        ((SourceTreeNode)child).filter(pattern);
      }
    } 
  }









  
  public TreeNode getChildAfter(TreeNode child) {
    if (child == null) {
      throw new IllegalArgumentException("child cannot be null");
    }
    int index = getIndex(child);
    if (index == -1) {
      throw new IllegalArgumentException("child node not contained");
    }
    return (index < getChildCount() - 1) ? getChildAt(index + 1) : null;
  }









  
  public TreeNode getChildAt(int index) {
    return this.visibleChildren.get(index);
  }









  
  public TreeNode getChildBefore(TreeNode child) {
    if (child == null) {
      throw new IllegalArgumentException("child cannot be null");
    }
    int index = getIndex(child);
    if (index == -1) {
      throw new IllegalArgumentException("child node not contained");
    }
    return (index > 0) ? getChildAt(index - 1) : null;
  }







  
  public int getChildCount() {
    return this.visibleChildren.size();
  }










  
  public int getIndex(TreeNode child) {
    if (child == null) {
      throw new IllegalArgumentException("child cannot be null");
    }
    for (int i = 0; i < this.visibleChildren.size(); i++) {
      TreeNode node = this.visibleChildren.get(i);
      if (node.equals(child)) {
        return i;
      }
    } 
    return -1;
  }








  
  public int getSortPriority() {
    return this.sortPriority;
  }







  
  public boolean isSortable() {
    return this.sortable;
  }






  
  public boolean isSorted() {
    return this.sorted;
  }

  
  public void refresh() {
    refreshVisibleChildren();
    for (int i = 0; i < getChildCount(); i++) {
      TreeNode child = getChildAt(i);
      if (child instanceof SourceTreeNode) {
        ((SourceTreeNode)child).refresh();
      }
    } 
  }





  
  private void refreshVisibleChildren() {
    this.visibleChildren.clear();
    if (this.children != null) {
      this.visibleChildren.addAll(this.children);
      if (this.sortable && this.sorted) {
        this.visibleChildren.sort(null);
      }
      if (this.pattern != null) {
        for (Iterator<TreeNode> i = this.visibleChildren.iterator(); i.hasNext(); ) {
          TreeNode node = i.next();
          if (node.isLeaf()) {
            String text = node.toString();
            text = Util.stripHtml(text);
            if (!this.pattern.matcher(text).find())
            {
              i.remove();
            }
          } 
        } 
      }
    } 
  }









  
  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }








  
  public void setSorted(boolean sorted) {
    if (sorted != this.sorted) {

      
      this.sorted = sorted;
      
      if (this.sortable) {
        refreshVisibleChildren();
      }
      
      for (int i = 0; i < super.getChildCount(); i++) {
        Object child = this.children.get(i);
        if (child instanceof SourceTreeNode) {
          ((SourceTreeNode)child).setSorted(sorted);
        }
      } 
    } 
  }








  
  public void setSortPriority(int priority) {
    this.sortPriority = priority;
  }
}
