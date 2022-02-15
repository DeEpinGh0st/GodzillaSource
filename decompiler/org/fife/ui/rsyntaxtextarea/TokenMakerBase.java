package org.fife.ui.rsyntaxtextarea;

import javax.swing.Action;
import javax.swing.text.Segment;
























































public abstract class TokenMakerBase
  implements TokenMaker
{
  protected TokenImpl firstToken = this.currentToken = this.previousToken = null;
  private TokenFactory tokenFactory = new DefaultTokenFactory(); protected TokenImpl currentToken;
  protected TokenImpl previousToken;
  private OccurrenceMarker occurrenceMarker;
  private int languageIndex;
  
  public void addNullToken() {
    if (this.firstToken == null) {
      this.firstToken = this.tokenFactory.createToken();
      this.currentToken = this.firstToken;
    } else {
      
      TokenImpl next = this.tokenFactory.createToken();
      this.currentToken.setNextToken(next);
      this.previousToken = this.currentToken;
      this.currentToken = next;
    } 
    this.currentToken.setLanguageIndex(this.languageIndex);
  }












  
  public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
    addToken(segment.array, start, end, tokenType, startOffset);
  }



  
  public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
    addToken(array, start, end, tokenType, startOffset, false);
  }














  
  public void addToken(char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink) {
    if (this.firstToken == null) {
      this.firstToken = this.tokenFactory.createToken(array, start, end, startOffset, tokenType);
      
      this.currentToken = this.firstToken;
    } else {
      
      TokenImpl next = this.tokenFactory.createToken(array, start, end, startOffset, tokenType);
      
      this.currentToken.setNextToken(next);
      this.previousToken = this.currentToken;
      this.currentToken = next;
    } 
    
    this.currentToken.setLanguageIndex(this.languageIndex);
    this.currentToken.setHyperlink(hyperlink);
  }








  
  protected OccurrenceMarker createOccurrenceMarker() {
    return new DefaultOccurrenceMarker();
  }













  
  public int getClosestStandardTokenTypeForInternalType(int type) {
    return type;
  }















  
  public boolean getCurlyBracesDenoteCodeBlocks(int languageIndex) {
    return false;
  }









  
  public Action getInsertBreakAction() {
    return null;
  }







  
  protected int getLanguageIndex() {
    return this.languageIndex;
  }




  
  public int getLastTokenTypeOnLine(Segment text, int initialTokenType) {
    Token t = getTokenList(text, initialTokenType, 0);
    
    while (t.getNextToken() != null) {
      t = t.getNextToken();
    }
    
    return t.getType();
  }



  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return null;
  }













  
  public boolean getMarkOccurrencesOfTokenType(int type) {
    return (type == 20);
  }








  
  protected boolean getNoTokensIdentifiedYet() {
    return (this.firstToken == null);
  }


  
  public OccurrenceMarker getOccurrenceMarker() {
    if (this.occurrenceMarker == null) {
      this.occurrenceMarker = createOccurrenceMarker();
    }
    return this.occurrenceMarker;
  }









  
  public boolean getShouldIndentNextLineAfter(Token token) {
    return false;
  }







  
  public boolean isIdentifierChar(int languageIndex, char ch) {
    return (Character.isLetterOrDigit(ch) || ch == '_' || ch == '$');
  }









  
  public boolean isMarkupLanguage() {
    return false;
  }








  
  protected void resetTokenList() {
    this.firstToken = this.currentToken = this.previousToken = null;
    this.tokenFactory.resetAllTokens();
  }












  
  protected void setLanguageIndex(int languageIndex) {
    this.languageIndex = Math.max(0, languageIndex);
  }
}
