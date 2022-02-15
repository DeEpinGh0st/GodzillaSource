package org.fife.rsta.ac.java.tree;

import javax.swing.Icon;
import org.fife.rsta.ac.SourceTreeNode;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.rjc.ast.ASTNode;




















class JavaTreeNode
  extends SourceTreeNode
{
  private ASTNode astNode;
  private Icon icon;
  protected static final int PRIORITY_TYPE = 0;
  protected static final int PRIORITY_FIELD = 1;
  protected static final int PRIORITY_CONSTRUCTOR = 2;
  protected static final int PRIORITY_METHOD = 3;
  protected static final int PRIORITY_LOCAL_VAR = 4;
  protected static final int PRIORITY_BOOST_STATIC = -16;
  
  protected JavaTreeNode(ASTNode node) {
    this(node, (String)null);
  }

  
  protected JavaTreeNode(ASTNode node, String iconName) {
    this(node, iconName, false);
  }

  
  protected JavaTreeNode(ASTNode node, String iconName, boolean sorted) {
    super(node, sorted);
    this.astNode = node;
    if (iconName != null) {
      setIcon(IconFactory.get().getIcon(iconName));
    }
  }

  
  public JavaTreeNode(String text, String iconName) {
    this(text, iconName, false);
  }

  
  public JavaTreeNode(String text, String iconName, boolean sorted) {
    super(text, sorted);
    if (iconName != null) {
      this.icon = IconFactory.get().getIcon(iconName);
    }
  }





  
  public int compareTo(SourceTreeNode obj) {
    int res = -1;
    if (obj instanceof JavaTreeNode) {
      JavaTreeNode jtn2 = (JavaTreeNode)obj;
      res = getSortPriority() - jtn2.getSortPriority();
      if (res == 0 && ((SourceTreeNode)getParent()).isSorted()) {
        res = getText(false).compareToIgnoreCase(jtn2.getText(false));
      }
    } 
    return res;
  }

  
  public ASTNode getASTNode() {
    return this.astNode;
  }

  
  public Icon getIcon() {
    return this.icon;
  }

  
  public String getText(boolean selected) {
    Object obj = getUserObject();
    return (obj != null) ? obj.toString() : null;
  }

  
  public void setIcon(Icon icon) {
    this.icon = icon;
  }








  
  public String toString() {
    return getText(false);
  }
}
