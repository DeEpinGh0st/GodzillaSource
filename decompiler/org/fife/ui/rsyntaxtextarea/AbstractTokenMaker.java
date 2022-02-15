package org.fife.ui.rsyntaxtextarea;

































public abstract class AbstractTokenMaker
  extends TokenMakerBase
{
  protected TokenMap wordsToHighlight = getWordsToHighlight();







  
  public abstract TokenMap getWordsToHighlight();







  
  public void removeLastToken() {
    if (this.previousToken == null) {
      this.firstToken = this.currentToken = null;
    } else {
      
      this.currentToken = this.previousToken;
      this.currentToken.setNextToken(null);
    } 
  }
}
