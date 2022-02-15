package org.mozilla.javascript.ast;

















public abstract class XmlFragment
  extends AstNode
{
  public XmlFragment() {}
  
  public XmlFragment(int pos) {
    super(pos);
  }
  
  public XmlFragment(int pos, int len) {
    super(pos, len);
  }
}
