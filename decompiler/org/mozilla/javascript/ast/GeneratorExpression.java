package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;









public class GeneratorExpression
  extends Scope
{
  private AstNode result;
  private List<GeneratorExpressionLoop> loops = new ArrayList<GeneratorExpressionLoop>();
  
  private AstNode filter;
  private int ifPosition = -1;
  private int lp = -1;
  private int rp = -1;



  
  public GeneratorExpression() {}


  
  public GeneratorExpression(int pos) {
    super(pos);
  }
  
  public GeneratorExpression(int pos, int len) {
    super(pos, len);
  }



  
  public AstNode getResult() {
    return this.result;
  }




  
  public void setResult(AstNode result) {
    assertNotNull(result);
    this.result = result;
    result.setParent(this);
  }



  
  public List<GeneratorExpressionLoop> getLoops() {
    return this.loops;
  }




  
  public void setLoops(List<GeneratorExpressionLoop> loops) {
    assertNotNull(loops);
    this.loops.clear();
    for (GeneratorExpressionLoop acl : loops) {
      addLoop(acl);
    }
  }




  
  public void addLoop(GeneratorExpressionLoop acl) {
    assertNotNull(acl);
    this.loops.add(acl);
    acl.setParent(this);
  }



  
  public AstNode getFilter() {
    return this.filter;
  }




  
  public void setFilter(AstNode filter) {
    this.filter = filter;
    if (filter != null) {
      filter.setParent(this);
    }
  }


  
  public int getIfPosition() {
    return this.ifPosition;
  }



  
  public void setIfPosition(int ifPosition) {
    this.ifPosition = ifPosition;
  }



  
  public int getFilterLp() {
    return this.lp;
  }



  
  public void setFilterLp(int lp) {
    this.lp = lp;
  }



  
  public int getFilterRp() {
    return this.rp;
  }



  
  public void setFilterRp(int rp) {
    this.rp = rp;
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder(250);
    sb.append("(");
    sb.append(this.result.toSource(0));
    for (GeneratorExpressionLoop loop : this.loops) {
      sb.append(loop.toSource(0));
    }
    if (this.filter != null) {
      sb.append(" if (");
      sb.append(this.filter.toSource(0));
      sb.append(")");
    } 
    sb.append(")");
    return sb.toString();
  }





  
  public void visit(NodeVisitor v) {
    if (!v.visit(this)) {
      return;
    }
    this.result.visit(v);
    for (GeneratorExpressionLoop loop : this.loops) {
      loop.visit(v);
    }
    if (this.filter != null)
      this.filter.visit(v); 
  }
}
