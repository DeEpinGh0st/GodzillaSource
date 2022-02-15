package org.fife.ui.rsyntaxtextarea;

import java.util.Iterator;






















class TokenIterator
  implements Iterator<Token>
{
  private RSyntaxDocument doc;
  private int curLine;
  private Token token;
  
  TokenIterator(RSyntaxDocument doc) {
    this.doc = doc;
    loadTokenListForCurLine();
    int lineCount = getLineCount();
    while ((this.token == null || !this.token.isPaintable()) && this.curLine < lineCount - 1) {
      this.curLine++;
      loadTokenListForCurLine();
    } 
  }

  
  private int getLineCount() {
    return this.doc.getDefaultRootElement().getElementCount();
  }








  
  public boolean hasNext() {
    return (this.token != null);
  }

  
  private void loadTokenListForCurLine() {
    this.token = this.doc.getTokenListForLine(this.curLine);
    if (this.token != null && !this.token.isPaintable())
    {
      this.token = null;
    }
  }









  
  public Token next() {
    Token t = this.token;
    boolean tIsCloned = false;
    int lineCount = getLineCount();

    
    if (this.token != null && this.token.isPaintable()) {
      this.token = this.token.getNextToken();
    }
    else if (this.curLine < lineCount - 1) {
      t = new TokenImpl(t);
      tIsCloned = true;
      this.curLine++;
      loadTokenListForCurLine();
    }
    else if (this.token != null && !this.token.isPaintable()) {
      
      this.token = null;
    } 
    
    while ((this.token == null || !this.token.isPaintable()) && this.curLine < lineCount - 1) {
      if (!tIsCloned) {
        t = new TokenImpl(t);
        tIsCloned = true;
      } 
      this.curLine++;
      loadTokenListForCurLine();
    } 
    if (this.token != null && !this.token.isPaintable() && this.curLine == lineCount - 1) {
      this.token = null;
    }
    
    return t;
  }









  
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
