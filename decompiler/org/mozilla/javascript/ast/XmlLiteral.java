package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;















public class XmlLiteral
  extends AstNode
{
  private List<XmlFragment> fragments = new ArrayList<XmlFragment>();



  
  public XmlLiteral() {}


  
  public XmlLiteral(int pos) {
    super(pos);
  }
  
  public XmlLiteral(int pos, int len) {
    super(pos, len);
  }



  
  public List<XmlFragment> getFragments() {
    return this.fragments;
  }






  
  public void setFragments(List<XmlFragment> fragments) {
    assertNotNull(fragments);
    this.fragments.clear();
    for (XmlFragment fragment : fragments) {
      addFragment(fragment);
    }
  }



  
  public void addFragment(XmlFragment fragment) {
    assertNotNull(fragment);
    this.fragments.add(fragment);
    fragment.setParent(this);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder(250);
    for (XmlFragment frag : this.fragments) {
      sb.append(frag.toSource(0));
    }
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this))
      for (XmlFragment frag : this.fragments)
        frag.visit(v);  
  }
}
