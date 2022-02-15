package org.fife.rsta.ac.js.tree;

import java.util.List;
import javax.swing.Icon;
import javax.swing.text.Position;
import org.fife.rsta.ac.SourceTreeNode;
import org.fife.rsta.ac.js.util.RhinoUtil;
import org.mozilla.javascript.ast.AstNode;































public class JavaScriptTreeNode
  extends SourceTreeNode
{
  private Position pos;
  private String text;
  private Icon icon;
  
  public JavaScriptTreeNode(List<AstNode> userObject) {
    super(userObject);
  }

  
  public JavaScriptTreeNode(AstNode userObject) {
    this(RhinoUtil.toList(new AstNode[] { userObject }));
  }

  
  public JavaScriptTreeNode(AstNode userObject, boolean sorted) {
    super(RhinoUtil.toList(new AstNode[] { userObject }, ), sorted);
  }

  
  public Icon getIcon() {
    return this.icon;
  }








  
  public int getLength() {
    int length = 0;
    List<AstNode> nodes = (List<AstNode>)getUserObject();
    for (AstNode node : nodes) {
      length += node.getLength();
    }
    length += nodes.size() - 1;
    return length;
  }









  
  public int getOffset() {
    return this.pos.getOffset();
  }

  
  public String getText(boolean selected) {
    return this.text;
  }

  
  public void setIcon(Icon icon) {
    this.icon = icon;
  }







  
  public void setOffset(Position offs) {
    this.pos = offs;
  }






  
  public void setText(String text) {
    this.text = text;
  }








  
  public String toString() {
    return getText(false);
  }
}
