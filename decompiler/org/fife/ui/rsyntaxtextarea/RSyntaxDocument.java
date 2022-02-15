package org.fife.ui.rsyntaxtextarea;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.modes.AbstractMarkupTokenMaker;
import org.fife.ui.rtextarea.RDocument;
import org.fife.util.DynamicIntArray;























































public class RSyntaxDocument
  extends RDocument
  implements Iterable<Token>, SyntaxConstants
{
  private transient TokenMakerFactory tokenMakerFactory;
  private transient TokenMaker tokenMaker;
  private String syntaxStyle;
  protected transient DynamicIntArray lastTokensOnLines;
  private transient int lastLine = -1;
  private transient Token cachedTokenList;
  private transient int useCacheCount = 0;
  private transient int tokenRetrievalCount = 0;




  
  private transient Segment s;



  
  private static final boolean DEBUG_TOKEN_CACHING = false;




  
  public RSyntaxDocument(String syntaxStyle) {
    this((TokenMakerFactory)null, syntaxStyle);
  }









  
  public RSyntaxDocument(TokenMakerFactory tmf, String syntaxStyle) {
    putProperty("tabSize", Integer.valueOf(5));
    this.lastTokensOnLines = new DynamicIntArray(400);
    this.lastTokensOnLines.add(0);
    this.s = new Segment();
    setTokenMakerFactory(tmf);
    setSyntaxStyle(syntaxStyle);
  }












  
  protected void fireInsertUpdate(DocumentEvent e) {
    this.cachedTokenList = null;






    
    Element lineMap = getDefaultRootElement();
    DocumentEvent.ElementChange change = e.getChange(lineMap);
    Element[] added = (change == null) ? null : change.getChildrenAdded();
    
    int numLines = lineMap.getElementCount();
    int line = lineMap.getElementIndex(e.getOffset());
    int previousLine = line - 1;
    
    int previousTokenType = (previousLine > -1) ? this.lastTokensOnLines.get(previousLine) : 0;

    
    if (added != null && added.length > 0) {
      
      Element[] removed = change.getChildrenRemoved();
      int numRemoved = (removed != null) ? removed.length : 0;
      
      int endBefore = line + added.length - numRemoved;

      
      for (int i = line; i < endBefore; i++) {
        
        setSharedSegment(i);
        
        int tokenType = this.tokenMaker.getLastTokenTypeOnLine(this.s, previousTokenType);
        this.lastTokensOnLines.add(i, tokenType);

        
        previousTokenType = tokenType;
      } 


      
      updateLastTokensBelow(endBefore, numLines, previousTokenType);

    
    }
    else {


      
      updateLastTokensBelow(line, numLines, previousTokenType);
    } 


    
    super.fireInsertUpdate(e);
  }


















  
  protected void fireRemoveUpdate(DocumentEvent chng) {
    this.cachedTokenList = null;
    Element lineMap = getDefaultRootElement();
    int numLines = lineMap.getElementCount();
    
    DocumentEvent.ElementChange change = chng.getChange(lineMap);
    Element[] removed = (change == null) ? null : change.getChildrenRemoved();

    
    if (removed != null && removed.length > 0) {
      
      int line = change.getIndex();
      int previousLine = line - 1;
      
      int previousTokenType = (previousLine > -1) ? this.lastTokensOnLines.get(previousLine) : 0;
      
      Element[] added = change.getChildrenAdded();
      int numAdded = (added == null) ? 0 : added.length;

      
      int endBefore = line + removed.length - numAdded;


      
      this.lastTokensOnLines.removeRange(line, endBefore);


      
      updateLastTokensBelow(line, numLines, previousTokenType);

    
    }
    else {

      
      int line = lineMap.getElementIndex(chng.getOffset());
      if (line >= this.lastTokensOnLines.getSize()) {
        return;
      }
      
      int previousLine = line - 1;
      
      int previousTokenType = (previousLine > -1) ? this.lastTokensOnLines.get(previousLine) : 0;

      
      updateLastTokensBelow(line, numLines, previousTokenType);
    } 


    
    super.fireRemoveUpdate(chng);
  }










  
  public int getClosestStandardTokenTypeForInternalType(int type) {
    return this.tokenMaker.getClosestStandardTokenTypeForInternalType(type);
  }










  
  public boolean getCompleteMarkupCloseTags() {
    return (getLanguageIsMarkup() && ((AbstractMarkupTokenMaker)this.tokenMaker)
      .getCompleteCloseTags());
  }











  
  public boolean getCurlyBracesDenoteCodeBlocks(int languageIndex) {
    return this.tokenMaker.getCurlyBracesDenoteCodeBlocks(languageIndex);
  }







  
  public boolean getLanguageIsMarkup() {
    return this.tokenMaker.isMarkupLanguage();
  }








  
  public int getLastTokenTypeOnLine(int line) {
    return this.lastTokensOnLines.get(line);
  }











  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return this.tokenMaker.getLineCommentStartAndEnd(languageIndex);
  }









  
  boolean getMarkOccurrencesOfTokenType(int type) {
    return this.tokenMaker.getMarkOccurrencesOfTokenType(type);
  }






  
  OccurrenceMarker getOccurrenceMarker() {
    return this.tokenMaker.getOccurrenceMarker();
  }








  
  public boolean getShouldIndentNextLine(int line) {
    Token t = getTokenListForLine(line);
    t = t.getLastNonCommentNonWhitespaceToken();
    return this.tokenMaker.getShouldIndentNextLineAfter(t);
  }







  
  public String getSyntaxStyle() {
    return this.syntaxStyle;
  }












  
  public final Token getTokenListForLine(int line) {
    this.tokenRetrievalCount++;
    if (line == this.lastLine && this.cachedTokenList != null)
    {



      
      return this.cachedTokenList;
    }
    this.lastLine = line;
    
    Element map = getDefaultRootElement();
    Element elem = map.getElement(line);
    int startOffset = elem.getStartOffset();

    
    int endOffset = elem.getEndOffset() - 1;
    try {
      getText(startOffset, endOffset - startOffset, this.s);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      return null;
    } 
    
    int initialTokenType = (line == 0) ? 0 : getLastTokenTypeOnLine(line - 1);

    
    this.cachedTokenList = this.tokenMaker.getTokenList(this.s, initialTokenType, startOffset);
    return this.cachedTokenList;
  }


  
  boolean insertBreakSpecialHandling(ActionEvent e) {
    Action a = this.tokenMaker.getInsertBreakAction();
    if (a != null) {
      a.actionPerformed(e);
      return true;
    } 
    return false;
  }










  
  public boolean isIdentifierChar(int languageIndex, char ch) {
    return this.tokenMaker.isIdentifierChar(languageIndex, ch);
  }












  
  public Iterator<Token> iterator() {
    return new TokenIterator(this);
  }










  
  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();



    
    setTokenMakerFactory((TokenMakerFactory)null);

    
    this.s = new Segment();
    int lineCount = getDefaultRootElement().getElementCount();
    this.lastTokensOnLines = new DynamicIntArray(lineCount);
    setSyntaxStyle(this.syntaxStyle);
  }










  
  private void setSharedSegment(int line) {
    Element map = getDefaultRootElement();

    
    Element element = map.getElement(line);
    if (element == null) {
      throw new InternalError("Invalid line number: " + line);
    }
    int startOffset = element.getStartOffset();

    
    int endOffset = element.getEndOffset() - 1;
    try {
      getText(startOffset, endOffset - startOffset, this.s);
    } catch (BadLocationException ble) {
      throw new InternalError("Text range not in document: " + startOffset + "-" + endOffset);
    } 
  }
















  
  public void setSyntaxStyle(String styleKey) {
    this.tokenMaker = this.tokenMakerFactory.getTokenMaker(styleKey);
    updateSyntaxHighlightingInformation();
    this.syntaxStyle = styleKey;
  }










  
  public void setSyntaxStyle(TokenMaker tokenMaker) {
    this.tokenMaker = tokenMaker;
    updateSyntaxHighlightingInformation();
    this.syntaxStyle = "text/unknown";
  }







  
  public void setTokenMakerFactory(TokenMakerFactory tmf) {
    this
      .tokenMakerFactory = (tmf != null) ? tmf : TokenMakerFactory.getDefaultInstance();
  }














  
  private int updateLastTokensBelow(int line, int numLines, int previousTokenType) {
    int firstLine = line;




    
    int end = numLines;
    
    while (line < end) {
      
      setSharedSegment(line);
      
      int oldTokenType = this.lastTokensOnLines.get(line);
      int newTokenType = this.tokenMaker.getLastTokenTypeOnLine(this.s, previousTokenType);






      
      if (oldTokenType == newTokenType) {
        
        fireChangedUpdate(new AbstractDocument.DefaultDocumentEvent((AbstractDocument)this, firstLine, line, DocumentEvent.EventType.CHANGE));
        return line;
      } 




      
      this.lastTokensOnLines.setUnsafe(line, newTokenType);
      previousTokenType = newTokenType;
      line++;
    } 







    
    if (line > firstLine)
    {
      fireChangedUpdate(new AbstractDocument.DefaultDocumentEvent((AbstractDocument)this, firstLine, line, DocumentEvent.EventType.CHANGE));
    }

    
    return line;
  }













  
  private void updateSyntaxHighlightingInformation() {
    Element map = getDefaultRootElement();
    int numLines = map.getElementCount();
    int lastTokenType = 0;
    for (int i = 0; i < numLines; i++) {
      setSharedSegment(i);
      lastTokenType = this.tokenMaker.getLastTokenTypeOnLine(this.s, lastTokenType);
      this.lastTokensOnLines.set(i, lastTokenType);
    } 

    
    this.lastLine = -1;
    this.cachedTokenList = null;

    
    fireChangedUpdate(new AbstractDocument.DefaultDocumentEvent((AbstractDocument)this, 0, numLines - 1, DocumentEvent.EventType.CHANGE));
  }
}
