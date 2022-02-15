package org.fife.rsta.ac.xml.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.xml.XmlLanguageSupport;
import org.fife.rsta.ac.xml.XmlParser;
import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;

































public class XmlOutlineTree
  extends AbstractSourceTree
{
  private XmlParser parser;
  private XmlEditorListener listener;
  private DefaultTreeModel model;
  private XmlTreeCellRenderer xmlTreeCellRenderer;
  
  public XmlOutlineTree() {
    this(false);
  }









  
  public XmlOutlineTree(boolean sorted) {
    setSorted(sorted);
    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
    setRootVisible(false);
    this.xmlTreeCellRenderer = new XmlTreeCellRenderer();
    setCellRenderer(this.xmlTreeCellRenderer);
    this.model = new DefaultTreeModel((TreeNode)new XmlTreeNode("Nothing"));
    setModel(this.model);
    this.listener = new XmlEditorListener();
    addTreeSelectionListener(this.listener);
  }







  
  private void checkForXmlParsing() {
    if (this.parser != null) {
      this.parser.removePropertyChangeListener("XmlAST", this.listener);
      this.parser = null;
    } 


    
    LanguageSupportFactory lsf = LanguageSupportFactory.get();
    LanguageSupport support = lsf.getSupportFor("text/xml");
    
    XmlLanguageSupport xls = (XmlLanguageSupport)support;

    
    this.parser = xls.getParser(this.textArea);
    if (this.parser != null) {
      this.parser.addPropertyChangeListener("XmlAST", this.listener);
      
      XmlTreeNode root = this.parser.getAst();
      update(root);
    } else {
      
      update((XmlTreeNode)null);
    } 
  }






  
  public void expandInitialNodes() {
    fastExpandAll(new TreePath(getModel().getRoot()), true);
  }





  
  private void gotoElementAtPath(TreePath path) {
    Object node = path.getLastPathComponent();
    if (node instanceof XmlTreeNode) {
      XmlTreeNode xtn = (XmlTreeNode)node;
      
      DocumentRange range = new DocumentRange(xtn.getStartOffset(), xtn.getEndOffset());
      RSyntaxUtilities.selectAndPossiblyCenter((JTextArea)this.textArea, range, true);
    } 
  }


  
  public boolean gotoSelectedElement() {
    TreePath path = getLeadSelectionPath();
    if (path != null) {
      gotoElementAtPath(path);
      return true;
    } 
    return false;
  }



  
  public void listenTo(RSyntaxTextArea textArea) {
    if (this.textArea != null) {
      uninstall();
    }

    
    if (textArea == null) {
      return;
    }

    
    this.textArea = textArea;
    textArea.addPropertyChangeListener("RSTA.syntaxStyle", this.listener);

    
    checkForXmlParsing();
  }




  
  public void uninstall() {
    if (this.parser != null) {
      this.parser.removePropertyChangeListener("XmlAST", this.listener);
      this.parser = null;
    } 
    
    if (this.textArea != null) {
      this.textArea.removePropertyChangeListener("RSTA.syntaxStyle", this.listener);
      
      this.textArea = null;
    } 
  }


  
  private void update(XmlTreeNode root) {
    if (root != null) {
      root = (XmlTreeNode)root.cloneWithChildren();
    }
    this.model.setRoot((TreeNode)root);
    if (root != null) {
      root.setSorted(isSorted());
    }
    refresh();
  }





  
  public void updateUI() {
    super.updateUI();
    this.xmlTreeCellRenderer = new XmlTreeCellRenderer();
    setCellRenderer(this.xmlTreeCellRenderer);
  }





  
  private class XmlEditorListener
    implements PropertyChangeListener, TreeSelectionListener
  {
    private XmlEditorListener() {}




    
    public void propertyChange(PropertyChangeEvent e) {
      String name = e.getPropertyName();

      
      if ("RSTA.syntaxStyle".equals(name)) {
        XmlOutlineTree.this.checkForXmlParsing();
      
      }
      else if ("XmlAST".equals(name)) {
        XmlTreeNode root = (XmlTreeNode)e.getNewValue();
        XmlOutlineTree.this.update(root);
      } 
    }






    
    public void valueChanged(TreeSelectionEvent e) {
      if (XmlOutlineTree.this.getGotoSelectedElementOnClick()) {
        
        TreePath newPath = e.getNewLeadSelectionPath();
        if (newPath != null)
          XmlOutlineTree.this.gotoElementAtPath(newPath); 
      } 
    }
  }
}
