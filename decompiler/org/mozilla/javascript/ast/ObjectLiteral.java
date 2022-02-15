package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

























public class ObjectLiteral
  extends AstNode
  implements DestructuringForm
{
  private static final List<ObjectProperty> NO_ELEMS = Collections.unmodifiableList(new ArrayList<ObjectProperty>());

  
  private List<ObjectProperty> elements;

  
  boolean isDestructuring;


  
  public ObjectLiteral() {}

  
  public ObjectLiteral(int pos) {
    super(pos);
  }
  
  public ObjectLiteral(int pos, int len) {
    super(pos, len);
  }




  
  public List<ObjectProperty> getElements() {
    return (this.elements != null) ? this.elements : NO_ELEMS;
  }





  
  public void setElements(List<ObjectProperty> elements) {
    if (elements == null) {
      this.elements = null;
    } else {
      if (this.elements != null)
        this.elements.clear(); 
      for (ObjectProperty o : elements) {
        addElement(o);
      }
    } 
  }




  
  public void addElement(ObjectProperty element) {
    assertNotNull(element);
    if (this.elements == null) {
      this.elements = new ArrayList<ObjectProperty>();
    }
    this.elements.add(element);
    element.setParent(this);
  }





  
  public void setIsDestructuring(boolean destructuring) {
    this.isDestructuring = destructuring;
  }





  
  public boolean isDestructuring() {
    return this.isDestructuring;
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("{");
    if (this.elements != null) {
      printList(this.elements, sb);
    }
    sb.append("}");
    return sb.toString();
  }





  
  public void visit(NodeVisitor v) {
    if (v.visit(this))
      for (ObjectProperty prop : getElements())
        prop.visit(v);  
  }
}
