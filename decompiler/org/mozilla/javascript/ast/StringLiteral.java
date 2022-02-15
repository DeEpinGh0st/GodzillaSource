package org.mozilla.javascript.ast;

import org.mozilla.javascript.ScriptRuntime;
















public class StringLiteral
  extends AstNode
{
  private String value;
  private char quoteChar;
  
  public StringLiteral() {}
  
  public StringLiteral(int pos) {
    super(pos);
  }




  
  public StringLiteral(int pos, int len) {
    super(pos, len);
  }





  
  public String getValue() {
    return this.value;
  }



  
  public String getValue(boolean includeQuotes) {
    if (!includeQuotes)
      return this.value; 
    return this.quoteChar + this.value + this.quoteChar;
  }





  
  public void setValue(String value) {
    assertNotNull(value);
    this.value = value;
  }



  
  public char getQuoteCharacter() {
    return this.quoteChar;
  }
  
  public void setQuoteCharacter(char c) {
    this.quoteChar = c;
  }

  
  public String toSource(int depth) {
    return makeIndent(depth) + this.quoteChar + ScriptRuntime.escapeString(this.value, this.quoteChar) + this.quoteChar;
  }








  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
