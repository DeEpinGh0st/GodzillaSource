package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;








































public class CategoryNodeEditor
  extends CategoryAbstractCellEditor
{
  protected CategoryNodeEditorRenderer _renderer;
  protected CategoryNode _lastEditedNode;
  protected JCheckBox _checkBox;
  protected CategoryExplorerModel _categoryModel;
  protected JTree _tree;
  
  public CategoryNodeEditor(CategoryExplorerModel model) {
    this._renderer = new CategoryNodeEditorRenderer();
    this._checkBox = this._renderer.getCheckBox();
    this._categoryModel = model;
    
    this._checkBox.addActionListener(new ActionListener() { private final CategoryNodeEditor this$0;
          public void actionPerformed(ActionEvent e) {
            CategoryNodeEditor.this._categoryModel.update(CategoryNodeEditor.this._lastEditedNode, CategoryNodeEditor.this._checkBox.isSelected());
            CategoryNodeEditor.this.stopCellEditing();
          } }
      );
    
    this._renderer.addMouseListener(new MouseAdapter() { private final CategoryNodeEditor this$0;
          public void mousePressed(MouseEvent e) {
            if ((e.getModifiers() & 0x4) != 0) {
              CategoryNodeEditor.this.showPopup(CategoryNodeEditor.this._lastEditedNode, e.getX(), e.getY());
            }
            CategoryNodeEditor.this.stopCellEditing();
          } }
      );
  }






  
  public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
    this._lastEditedNode = (CategoryNode)value;
    this._tree = tree;
    
    return this._renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
  }



  
  public Object getCellEditorValue() {
    return this._lastEditedNode.getUserObject();
  }



  
  protected JMenuItem createPropertiesMenuItem(final CategoryNode node) {
    JMenuItem result = new JMenuItem("Properties");
    result.addActionListener(new ActionListener() { private final CategoryNode val$node;
          public void actionPerformed(ActionEvent e) {
            CategoryNodeEditor.this.showPropertiesDialog(node);
          } private final CategoryNodeEditor this$0; }
      );
    return result;
  }
  
  protected void showPropertiesDialog(CategoryNode node) {
    JOptionPane.showMessageDialog(this._tree, getDisplayedProperties(node), "Category Properties: " + node.getTitle(), -1);
  }





  
  protected Object getDisplayedProperties(CategoryNode node) {
    ArrayList result = new ArrayList();
    result.add("Category: " + node.getTitle());
    if (node.hasFatalRecords()) {
      result.add("Contains at least one fatal LogRecord.");
    }
    if (node.hasFatalChildren()) {
      result.add("Contains descendants with a fatal LogRecord.");
    }
    result.add("LogRecords in this category alone: " + node.getNumberOfContainedRecords());
    
    result.add("LogRecords in descendant categories: " + node.getNumberOfRecordsFromChildren());
    
    result.add("LogRecords in this category including descendants: " + node.getTotalNumberOfRecords());
    
    return result.toArray();
  }
  
  protected void showPopup(CategoryNode node, int x, int y) {
    JPopupMenu popup = new JPopupMenu();
    popup.setSize(150, 400);


    
    if (node.getParent() == null) {
      popup.add(createRemoveMenuItem());
      popup.addSeparator();
    } 
    popup.add(createSelectDescendantsMenuItem(node));
    popup.add(createUnselectDescendantsMenuItem(node));
    popup.addSeparator();
    popup.add(createExpandMenuItem(node));
    popup.add(createCollapseMenuItem(node));
    popup.addSeparator();
    popup.add(createPropertiesMenuItem(node));
    popup.show(this._renderer, x, y);
  }
  
  protected JMenuItem createSelectDescendantsMenuItem(final CategoryNode node) {
    JMenuItem selectDescendants = new JMenuItem("Select All Descendant Categories");
    
    selectDescendants.addActionListener(new ActionListener() { private final CategoryNode val$node; private final CategoryNodeEditor this$0;
          
          public void actionPerformed(ActionEvent e) {
            CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, true);
          } }
      );
    
    return selectDescendants;
  }
  
  protected JMenuItem createUnselectDescendantsMenuItem(final CategoryNode node) {
    JMenuItem unselectDescendants = new JMenuItem("Deselect All Descendant Categories");
    
    unselectDescendants.addActionListener(new ActionListener() { private final CategoryNode val$node;
          private final CategoryNodeEditor this$0;
          
          public void actionPerformed(ActionEvent e) {
            CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, false);
          } }
      );

    
    return unselectDescendants;
  }
  
  protected JMenuItem createExpandMenuItem(final CategoryNode node) {
    JMenuItem result = new JMenuItem("Expand All Descendant Categories");
    result.addActionListener(new ActionListener() { private final CategoryNode val$node; private final CategoryNodeEditor this$0;
          public void actionPerformed(ActionEvent e) {
            CategoryNodeEditor.this.expandDescendants(node);
          } }
      );
    return result;
  }
  
  protected JMenuItem createCollapseMenuItem(final CategoryNode node) {
    JMenuItem result = new JMenuItem("Collapse All Descendant Categories");
    result.addActionListener(new ActionListener() { private final CategoryNode val$node; private final CategoryNodeEditor this$0;
          public void actionPerformed(ActionEvent e) {
            CategoryNodeEditor.this.collapseDescendants(node);
          } }
      );
    return result;
  }









  
  protected JMenuItem createRemoveMenuItem() {
    JMenuItem result = new JMenuItem("Remove All Empty Categories");
    result.addActionListener(new ActionListener() { private final CategoryNodeEditor this$0;
          public void actionPerformed(ActionEvent e) {
            while (CategoryNodeEditor.this.removeUnusedNodes() > 0);
          } }
      );
    return result;
  }
  
  protected void expandDescendants(CategoryNode node) {
    Enumeration descendants = node.depthFirstEnumeration();
    
    while (descendants.hasMoreElements()) {
      CategoryNode current = descendants.nextElement();
      expand(current);
    } 
  }
  
  protected void collapseDescendants(CategoryNode node) {
    Enumeration descendants = node.depthFirstEnumeration();
    
    while (descendants.hasMoreElements()) {
      CategoryNode current = descendants.nextElement();
      collapse(current);
    } 
  }



  
  protected int removeUnusedNodes() {
    int count = 0;
    CategoryNode root = this._categoryModel.getRootCategoryNode();
    Enumeration enumeration = root.depthFirstEnumeration();
    while (enumeration.hasMoreElements()) {
      CategoryNode node = enumeration.nextElement();
      if (node.isLeaf() && node.getNumberOfContainedRecords() == 0 && node.getParent() != null) {
        
        this._categoryModel.removeNodeFromParent(node);
        count++;
      } 
    } 
    
    return count;
  }
  
  protected void expand(CategoryNode node) {
    this._tree.expandPath(getTreePath(node));
  }
  
  protected TreePath getTreePath(CategoryNode node) {
    return new TreePath((Object[])node.getPath());
  }
  
  protected void collapse(CategoryNode node) {
    this._tree.collapsePath(getTreePath(node));
  }
}
