package org.mozilla.javascript.ast;

import java.util.SortedSet;
import java.util.TreeSet;
import org.mozilla.javascript.Node;























public class AstRoot
  extends ScriptNode
{
  private SortedSet<Comment> comments;
  private boolean inStrictMode;
  
  public AstRoot() {}
  
  public AstRoot(int pos) {
    super(pos);
  }




  
  public SortedSet<Comment> getComments() {
    return this.comments;
  }





  
  public void setComments(SortedSet<Comment> comments) {
    if (comments == null) {
      this.comments = null;
    } else {
      if (this.comments != null)
        this.comments.clear(); 
      for (Comment c : comments) {
        addComment(c);
      }
    } 
  }




  
  public void addComment(Comment comment) {
    assertNotNull(comment);
    if (this.comments == null) {
      this.comments = new TreeSet<Comment>(new AstNode.PositionComparator());
    }
    this.comments.add(comment);
    comment.setParent(this);
  }
  
  public void setInStrictMode(boolean inStrictMode) {
    this.inStrictMode = inStrictMode;
  }
  
  public boolean isInStrictMode() {
    return this.inStrictMode;
  }







  
  public void visitComments(NodeVisitor visitor) {
    if (this.comments != null) {
      for (Comment c : this.comments) {
        visitor.visit(c);
      }
    }
  }







  
  public void visitAll(NodeVisitor visitor) {
    visit(visitor);
    visitComments(visitor);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    for (Node node : this) {
      sb.append(((AstNode)node).toSource(depth));
    }
    return sb.toString();
  }




  
  public String debugPrint() {
    AstNode.DebugPrintVisitor dpv = new AstNode.DebugPrintVisitor(new StringBuilder(1000));
    visitAll(dpv);
    return dpv.toString();
  }





  
  public void checkParentLinks() {
    visit(new NodeVisitor() {
          public boolean visit(AstNode node) {
            int type = node.getType();
            if (type == 136)
              return true; 
            if (node.getParent() == null) {
              throw new IllegalStateException("No parent for node: " + node + "\n" + node.toSource(0));
            }
            
            return true;
          }
        });
  }
}
