package org.fife.rsta.ac.java.rjc.lexer;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;











































public class Scanner
{
  private static final boolean DEBUG = false;
  private SourceCodeScanner s;
  private Stack<Token> stack;
  private int typeArgLevel;
  private Document doc;
  private Token mostRecentToken;
  private Stack<Stack<Token>> resetPositions;
  private Stack<Token> currentResetTokenStack;
  private int currentResetStartOffset;
  
  public Scanner() {
    this((Reader)null);
  }






  
  public Scanner(List<Token> tokens) {
    this.stack = new Stack<>();
    for (int i = tokens.size() - 1; i >= 0; i--) {
      this.stack.push(tokens.get(i));
    }
  }






  
  public Scanner(Reader r) {
    this.s = (r != null) ? new SourceCodeScanner(r) : null;
    this.s.setKeepLastDocComment(true);
    this.stack = new Stack<>();
  }







  
  private void pushOntoStack(Token t) {
    if (t != null && !this.stack.isEmpty() && t.equals(this.stack.peek())) {
      System.err.println("ERROR: Token being duplicated: " + t);
      Thread.dumpStack();
      System.exit(5);
    }
    else if (t == null) {
      System.err.println("ERROR: null token pushed onto stack");
      Thread.dumpStack();
      System.exit(6);
    } 
    this.stack.push(t);
  }







  
  public void decreaseTypeArgumentsLevel() {
    if (--this.typeArgLevel < 0) {
      throw new InternalError("typeArgLevel dipped below 0");
    }
  }









  
  public Offset createOffset(int offs) {
    if (this.doc != null) {
      try {
        return new DocumentOffset(this.doc.createPosition(offs));
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }
    return () -> offs;
  }









  
  private void debugPrintToken(Token t) {}








  
  public int getColumn() {
    return this.s.getColumn();
  }








  
  public String getLastDocComment() {
    return this.s.getLastDocComment();
  }







  
  public int getLine() {
    return this.s.getLine();
  }






  
  public Token getMostRecentToken() {
    return this.mostRecentToken;
  }






  
  public int getOffset() {
    return this.s.getOffset();
  }










  
  public void eatParenPairs() throws IOException {
    Token t = yylex();
    if (t == null || t.getType() != 8388609) {
      throw new InternalError("'(' expected, found: " + t);
    }
    
    int blockDepth = 0;
    int parenDepth = 1;
    
    while ((t = yylex()) != null) {
      int type = t.getType();
      switch (type) {
        case 8388611:
          blockDepth++;
        
        case 8388612:
          blockDepth = Math.max(blockDepth - 1, 0);
        
        case 8388609:
          if (blockDepth == 0) {
            parenDepth++;
          }
        
        case 8388610:
          if (blockDepth == 0 && --parenDepth == 0) {
            return;
          }
      } 
    } 
  }










  
  public void eatThroughNext(int tokenType) throws IOException {
    Token t;
    while ((t = yylex()) != null && t.getType() != tokenType);
  }











  
  public void eatThroughNextSkippingBlocks(int tokenType) throws IOException {
    int blockDepth = 0; Token t;
    while ((t = yylex()) != null) {
      int type = t.getType();
      if (type == 8388611) {
        blockDepth++; continue;
      } 
      if (type == 8388612) {
        blockDepth--; continue;
      } 
      if (type == tokenType && 
        blockDepth <= 0) {
        return;
      }
    } 
  }

















  
  public Token eatThroughNextSkippingBlocks(int tokenType1, int tokenType2) throws IOException {
    int blockDepth = 0; Token t;
    while ((t = yylex()) != null) {
      int type = t.getType();
      if (type == 8388611) {
        blockDepth++; continue;
      } 
      if (type == 8388612) {
        blockDepth--; continue;
      } 
      if ((type == tokenType1 || type == tokenType2) && 
        blockDepth <= 0) {
        return t;
      }
    } 
    
    return null;
  }

















  
  public Token eatThroughNextSkippingBlocksAndStuffInParens(int tokenType1, int tokenType2) throws IOException {
    int blockDepth = 0;
    int parenDepth = 0;
    Token t;
    while ((t = yylex()) != null) {
      int type = t.getType();
      switch (type) {
        case 8388611:
          blockDepth++;
          continue;
        case 8388612:
          blockDepth--;
          continue;
        case 8388609:
          parenDepth++;
          continue;
        case 8388610:
          parenDepth--;
          continue;
      } 
      if ((type == tokenType1 || type == tokenType2) && 
        blockDepth <= 0 && parenDepth <= 0) {
        return t;
      }
    } 


    
    return null;
  }


  
  public void eatUntilNext(int type1, int type2) throws IOException {
    Token t;
    while ((t = yylex()) != null) {
      int type = t.getType();
      if (type == type1 || type == type2) {
        yyPushback(t);
        break;
      } 
    } 
  }

  
  public void eatUntilNext(int type1, int type2, int type3) throws IOException {
    Token t;
    while ((t = yylex()) != null) {
      int type = t.getType();
      if (type == type1 || type == type2 || type == type3) {
        yyPushback(t);
        break;
      } 
    } 
  }








  
  public int getTypeArgumentsLevel() {
    return this.typeArgLevel;
  }







  
  public void increaseTypeArgumentsLevel() {
    this.typeArgLevel++;
  }




  
  public void markResetPosition() {
    if (this.s != null) {
      if (this.resetPositions == null) {
        this.resetPositions = new Stack<>();
      }
      this.currentResetTokenStack = new Stack<>();
      this.resetPositions.push(this.currentResetTokenStack);
      this.currentResetStartOffset = this.s.getOffset();
    } 
  }
  public void resetToLastMarkedPosition() {
    if (this.s != null) {
      if (this.currentResetTokenStack == null) {
        throw new InternalError("No resetTokenStack!");
      }
      
      while (!this.stack.isEmpty()) {
        Token t = this.stack.peek();
        if (t.getOffset() >= this.currentResetStartOffset) {
          this.stack.pop();
        }
      } 



      
      while (!this.currentResetTokenStack.isEmpty()) {
        Token t = this.currentResetTokenStack.pop();
        this.stack.push(t);
      } 
      this.resetPositions.pop();
      this.currentResetTokenStack = this.resetPositions.isEmpty() ? null : this.resetPositions.peek();
      this.currentResetStartOffset = -1;
    } 
  }
  public void clearResetPosition() {
    if (this.s != null) {
      if (this.currentResetTokenStack == null) {
        throw new InternalError("No resetTokenStack!");
      }
      this.resetPositions.pop();
      this.currentResetTokenStack = this.resetPositions.isEmpty() ? null : this.resetPositions.peek();
      this.currentResetStartOffset = -1;
    } 
  }









  
  public void setDocument(Document doc) {
    this.doc = doc;
  }








  
  public int skipBracketPairs() throws IOException {
    int count = 0;
    
    while (yyPeekCheckType() == 8388613 && 
      yyPeekCheckType(2) == 8388614) {
      yylex();
      yylex();
      count++;
    } 
    
    return count;
  }












  
  public Token yylex() throws IOException {
    Token t;
    if (this.stack.isEmpty()) {
      t = (this.s != null) ? this.s.yylex() : null;
    } else {
      
      t = this.stack.pop();
    } 


    
    if (this.typeArgLevel > 0 && t != null && t.isOperator()) {
      String lexeme = t.getLexeme();
      if (lexeme.length() > 1) {
        char ch = lexeme.charAt(0);
        if (ch == '<') {
          Token rest = null;
          switch (t.getType()) {
            
            case 16777225:
              rest = new TokenImpl(33554433, "=", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
            
            case 16777240:
              rest = new TokenImpl(16777219, "<", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
            
            case 33554467:
              rest = new TokenImpl(16777225, "<=", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
          } 
          this.stack.push(rest);
          
          t = new TokenImpl(16777219, "<", t.getLine(), t.getColumn(), t.getOffset());
        }
        else if (ch == '>') {
          Token rest = null;
          switch (t.getType()) {
            
            case 16777226:
              rest = new TokenImpl(33554433, "=", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
            
            case 16777241:
              rest = new TokenImpl(16777218, ">", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
            
            case 16777242:
              rest = new TokenImpl(16777241, ">>", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
            
            case 33554468:
              rest = new TokenImpl(16777226, ">=", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
            
            case 33554469:
              rest = new TokenImpl(33554468, ">>=", t.getLine(), t.getColumn() + 1, t.getOffset() + 1);
              break;
          } 
          this.stack.push(rest);
          
          t = new TokenImpl(16777218, ">", t.getLine(), t.getColumn(), t.getOffset());
        } 
      } 
    } 
    
    debugPrintToken(t);
    if (this.currentResetTokenStack != null) {
      this.currentResetTokenStack.push(t);
    }
    if (t != null) {
      this.mostRecentToken = t;
    }
    return t;
  }












  
  public Token yylexNonNull(String error) throws IOException {
    Token t = yylex();
    if (t == null) {
      throw new EOFException(error);
    }
    return t;
  }













  
  public Token yylexNonNull(int type, String error) throws IOException {
    return yylexNonNull(type, -1, error);
  }
















  
  public Token yylexNonNull(int type1, int type2, String error) throws IOException {
    return yylexNonNull(type1, type2, -1, error);
  }


















  
  public Token yylexNonNull(int type1, int type2, int type3, String error) throws IOException {
    Token t = yylex();
    if (t == null) {
      throw new IOException(error);
    }
    if (t.getType() != type1 && (type2 == -1 || t.getType() != type2) && (type3 == -1 || t
      .getType() != type3)) {
      throw new IOException(error + ", found '" + t.getLexeme() + "'");
    }
    return t;
  }








  
  public Token yyPeek() throws IOException {
    Token t = yylex();
    if (t != null) {
      pushOntoStack(t);
    }
    return t;
  }










  
  public Token yyPeek(int depth) throws IOException {
    if (depth < 1) {
      throw new IllegalArgumentException("depth must be >= 1");
    }
    Stack<Token> read = new Stack<>();
    for (int i = 0; i < depth; i++) {
      Token token = yylex();
      if (token != null) {
        read.push(token);
      } else {
        
        while (!read.isEmpty()) {
          yyPushback(read.pop());
        }
        return null;
      } 
    } 
    Token t = read.peek();
    while (!read.isEmpty()) {
      yyPushback(read.pop());
    }
    return t;
  }








  
  public int yyPeekCheckType() throws IOException {
    Token t = yyPeek();
    return (t != null) ? t.getType() : -1;
  }









  
  public int yyPeekCheckType(int index) throws IOException {
    Token t = yyPeek(index);
    return (t != null) ? t.getType() : -1;
  }








  
  public Token yyPeekNonNull(String error) throws IOException {
    Token t = yyPeek();
    if (t == null) {
      throw new IOException(error);
    }
    return t;
  }










  
  public Token yyPeekNonNull(int type, String error) throws IOException {
    return yyPeekNonNull(type, -1, error);
  }












  
  public Token yyPeekNonNull(int type1, int type2, String error) throws IOException {
    return yyPeekNonNull(type1, type2, -1, error);
  }













  
  public Token yyPeekNonNull(int type1, int type2, int type3, String error) throws IOException {
    Token t = yyPeek();
    if (t == null) {
      throw new IOException(error);
    }
    if (t.getType() != type1 && (type2 == -1 || t.getType() != type2) && (type3 == -1 || t
      .getType() != type3)) {
      throw new IOException(error + ", found '" + t.getLexeme() + "'");
    }
    return t;
  }






  
  public void yyPushback(Token t) {
    if (t != null) {
      pushOntoStack(t);
    }
  }
  
  private static class DocumentOffset
    implements Offset
  {
    public Position pos;
    
    public DocumentOffset(Position pos) {
      this.pos = pos;
    }

    
    public int getOffset() {
      return this.pos.getOffset();
    }
  }
}
