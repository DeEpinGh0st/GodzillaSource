package org.fife.rsta.ac.java.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.rjc.ast.ASTNode;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;

































public class JavaOutlineTree
  extends AbstractSourceTree
{
  private DefaultTreeModel model;
  private RSyntaxTextArea textArea;
  private JavaParser parser;
  private Listener listener;
  
  public JavaOutlineTree() {
    this(false);
  }









  
  public JavaOutlineTree(boolean sorted) {
    setSorted(sorted);
    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
    setRootVisible(false);
    setCellRenderer(new AstTreeCellRenderer());
    this.model = new DefaultTreeModel(new DefaultMutableTreeNode("Nothing"));
    setModel(this.model);
    this.listener = new Listener();
    addTreeSelectionListener(this.listener);
  }








  
  private void update(CompilationUnit cu) {
    JavaTreeNode root = new JavaTreeNode("Remove me!", "sourceFileIcon");
    
    root.setSortable(false);
    if (cu == null) {
      this.model.setRoot((TreeNode)root);
      
      return;
    } 
    Package pkg = cu.getPackage();
    if (pkg != null) {
      String iconName = "packageIcon";
      root.add((MutableTreeNode)new JavaTreeNode((ASTNode)pkg, iconName, false));
    } 
    
    if (!getShowMajorElementsOnly()) {
      JavaTreeNode importNode = new JavaTreeNode("Imports", "importRootIcon");
      
      Iterator<ImportDeclaration> iterator = cu.getImportIterator();
      while (iterator.hasNext()) {
        ImportDeclaration idec = iterator.next();
        JavaTreeNode iNode = new JavaTreeNode((ASTNode)idec, "importIcon");
        
        importNode.add((MutableTreeNode)iNode);
      } 
      root.add((MutableTreeNode)importNode);
    } 
    
    Iterator<TypeDeclaration> i = cu.getTypeDeclarationIterator();
    while (i.hasNext()) {
      TypeDeclaration td = i.next();
      TypeDeclarationTreeNode dmtn = createTypeDeclarationNode(td);
      root.add((MutableTreeNode)dmtn);
    } 
    
    this.model.setRoot((TreeNode)root);
    root.setSorted(isSorted());
    refresh();
  }








  
  private void checkForJavaParsing() {
    if (this.parser != null) {
      this.parser.removePropertyChangeListener("CompilationUnit", this.listener);
      
      this.parser = null;
    } 


    
    LanguageSupportFactory lsf = LanguageSupportFactory.get();
    LanguageSupport support = lsf.getSupportFor("text/java");
    
    JavaLanguageSupport jls = (JavaLanguageSupport)support;

    
    this.parser = jls.getParser(this.textArea);
    if (this.parser != null) {
      this.parser.addPropertyChangeListener("CompilationUnit", this.listener);

      
      CompilationUnit cu = this.parser.getCompilationUnit();
      update(cu);
    } else {
      
      update((CompilationUnit)null);
    } 
  }



  
  private MemberTreeNode createMemberNode(Member member) {
    MemberTreeNode node;
    if (member instanceof CodeBlock) {
      node = new MemberTreeNode((CodeBlock)member);
    }
    else if (member instanceof Field) {
      node = new MemberTreeNode((Field)member);
    } else {
      
      node = new MemberTreeNode((Method)member);
    } 
    
    CodeBlock body = null;
    if (member instanceof CodeBlock) {
      body = (CodeBlock)member;
    }
    else if (member instanceof Method) {
      body = ((Method)member).getBody();
    } 
    
    if (body != null && !getShowMajorElementsOnly()) {
      for (int i = 0; i < body.getLocalVarCount(); i++) {
        LocalVariable var = body.getLocalVar(i);
        LocalVarTreeNode varNode = new LocalVarTreeNode(var);
        node.add((MutableTreeNode)varNode);
      } 
    }
    
    return node;
  }




  
  private TypeDeclarationTreeNode createTypeDeclarationNode(TypeDeclaration td) {
    TypeDeclarationTreeNode dmtn = new TypeDeclarationTreeNode(td);
    
    if (td instanceof NormalClassDeclaration) {
      NormalClassDeclaration ncd = (NormalClassDeclaration)td;
      for (int j = 0; j < ncd.getChildTypeCount(); j++) {
        TypeDeclaration td2 = ncd.getChildType(j);
        TypeDeclarationTreeNode tdn = createTypeDeclarationNode(td2);
        dmtn.add((MutableTreeNode)tdn);
      } 
      Iterator<Member> i = ncd.getMemberIterator();
      while (i.hasNext()) {
        dmtn.add((MutableTreeNode)createMemberNode(i.next()));
      
      }
    }
    else if (td instanceof NormalInterfaceDeclaration) {
      NormalInterfaceDeclaration nid = (NormalInterfaceDeclaration)td;
      for (int j = 0; j < nid.getChildTypeCount(); j++) {
        TypeDeclaration td2 = nid.getChildType(j);
        TypeDeclarationTreeNode tdn = createTypeDeclarationNode(td2);
        dmtn.add((MutableTreeNode)tdn);
      } 
      Iterator<Member> i = nid.getMemberIterator();
      while (i.hasNext()) {
        dmtn.add((MutableTreeNode)createMemberNode(i.next()));
      }
    } 
    
    return dmtn;
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
      Object comp = path.getLastPathComponent();
      if (comp instanceof TypeDeclarationTreeNode) {
        expandPath(path);
      }
      j++;
    } 
  }



  
  private void gotoElementAtPath(TreePath path) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
    Object obj = node.getUserObject();
    if (obj instanceof ASTNode) {
      ASTNode astNode = (ASTNode)obj;
      int start = astNode.getNameStartOffset();
      int end = astNode.getNameEndOffset();
      DocumentRange range = new DocumentRange(start, end);
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


    
    checkForJavaParsing();
  }







  
  public void uninstall() {
    if (this.parser != null) {
      this.parser.removePropertyChangeListener("CompilationUnit", this.listener);
      
      this.parser = null;
    } 
    
    if (this.textArea != null) {
      this.textArea.removePropertyChangeListener("RSTA.syntaxStyle", this.listener);
      
      this.textArea = null;
    } 
  }






  
  public void updateUI() {
    super.updateUI();

    
    setCellRenderer(new AstTreeCellRenderer());
  }





  
  private class Listener
    implements PropertyChangeListener, TreeSelectionListener
  {
    private Listener() {}




    
    public void propertyChange(PropertyChangeEvent e) {
      String name = e.getPropertyName();

      
      if ("RSTA.syntaxStyle".equals(name)) {
        JavaOutlineTree.this.checkForJavaParsing();
      
      }
      else if ("CompilationUnit".equals(name)) {
        CompilationUnit cu = (CompilationUnit)e.getNewValue();
        JavaOutlineTree.this.update(cu);
      } 
    }






    
    public void valueChanged(TreeSelectionEvent e) {
      if (JavaOutlineTree.this.getGotoSelectedElementOnClick()) {
        
        TreePath newPath = e.getNewLeadSelectionPath();
        if (newPath != null)
          JavaOutlineTree.this.gotoElementAtPath(newPath); 
      } 
    }
  }
}
