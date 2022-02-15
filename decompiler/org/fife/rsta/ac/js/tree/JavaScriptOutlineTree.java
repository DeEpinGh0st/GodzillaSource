package org.fife.rsta.ac.js.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.JavaScriptParser;
import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.mozilla.javascript.ast.AstRoot;
































public class JavaScriptOutlineTree
  extends AbstractSourceTree
{
  private DefaultTreeModel model;
  private RSyntaxTextArea textArea;
  private JavaScriptParser parser;
  private Listener listener;
  static final int PRIORITY_FUNCTION = 1;
  static final int PRIORITY_VARIABLE = 2;
  
  public JavaScriptOutlineTree() {
    this(false);
  }









  
  public JavaScriptOutlineTree(boolean sorted) {
    setSorted(sorted);
    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
    setRootVisible(false);
    setCellRenderer(new JavaScriptTreeCellRenderer());
    this.model = new DefaultTreeModel(new DefaultMutableTreeNode("Nothing"));
    setModel(this.model);
    this.listener = new Listener();
    addTreeSelectionListener(this.listener);
  }







  
  private void checkForJavaScriptParsing() {
    if (this.parser != null) {
      this.parser.removePropertyChangeListener("AST", this.listener);
      
      this.parser = null;
    } 


    
    LanguageSupportFactory lsf = LanguageSupportFactory.get();
    LanguageSupport support = lsf.getSupportFor("text/javascript");
    
    JavaScriptLanguageSupport jls = (JavaScriptLanguageSupport)support;

    
    this.parser = jls.getParser(this.textArea);
    if (this.parser != null) {
      this.parser.addPropertyChangeListener("AST", this.listener);

      
      AstRoot ast = this.parser.getAstRoot();
      update(ast);
    } else {
      
      update((AstRoot)null);
    } 
  }








  
  public void expandInitialNodes() {
    int j = 0;
    while (j < getRowCount()) {
      collapseRow(j++);
    }

    
    expandRow(0);
    j = 1;
    while (j < getRowCount()) {
      TreePath path = getPathForRow(j);

      
      expandPath(path);
      
      j++;
    } 
  }


  
  private void gotoElementAtPath(TreePath path) {
    Object node = path.getLastPathComponent();
    if (node instanceof JavaScriptTreeNode) {
      JavaScriptTreeNode jstn = (JavaScriptTreeNode)node;
      int len = jstn.getLength();
      if (len > -1) {
        int offs = jstn.getOffset();
        DocumentRange range = new DocumentRange(offs, offs + len);
        RSyntaxUtilities.selectAndPossiblyCenter((JTextArea)this.textArea, range, true);
      } 
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


    
    checkForJavaScriptParsing();
  }







  
  public void uninstall() {
    if (this.parser != null) {
      this.parser.removePropertyChangeListener("AST", this.listener);
      
      this.parser = null;
    } 
    
    if (this.textArea != null) {
      this.textArea.removePropertyChangeListener("RSTA.syntaxStyle", this.listener);
      
      this.textArea = null;
    } 
  }








  
  private void update(AstRoot ast) {
    JavaScriptOutlineTreeGenerator generator = new JavaScriptOutlineTreeGenerator(this.textArea, ast);
    
    JavaScriptTreeNode root = generator.getTreeRoot();
    this.model.setRoot((TreeNode)root);
    root.setSorted(isSorted());
    refresh();
  }





  
  public void updateUI() {
    super.updateUI();

    
    setCellRenderer(new JavaScriptTreeCellRenderer());
  }





  
  private class Listener
    implements PropertyChangeListener, TreeSelectionListener
  {
    private Listener() {}




    
    public void propertyChange(PropertyChangeEvent e) {
      String name = e.getPropertyName();

      
      if ("RSTA.syntaxStyle".equals(name)) {
        JavaScriptOutlineTree.this.checkForJavaScriptParsing();
      
      }
      else if ("AST".equals(name)) {
        AstRoot ast = (AstRoot)e.getNewValue();
        JavaScriptOutlineTree.this.update(ast);
      } 
    }






    
    public void valueChanged(TreeSelectionEvent e) {
      if (JavaScriptOutlineTree.this.getGotoSelectedElementOnClick()) {
        
        TreePath newPath = e.getNewLeadSelectionPath();
        if (newPath != null)
          JavaScriptOutlineTree.this.gotoElementAtPath(newPath); 
      } 
    }
  }
}
