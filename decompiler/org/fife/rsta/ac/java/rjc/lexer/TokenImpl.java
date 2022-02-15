package org.fife.rsta.ac.java.rjc.lexer;






































class TokenImpl
  implements Token
{
  private int type;
  private String lexeme;
  private int line;
  private int column;
  private int offset;
  private boolean invalid;
  
  public TokenImpl(int type, String lexeme, int line, int column, int offs) {
    this(type, lexeme, line, column, offs, false);
  }


  
  public TokenImpl(int type, String lexeme, int line, int column, int offs, boolean invalid) {
    this.type = type;
    this.lexeme = lexeme;
    this.line = line;
    this.column = column;
    this.offset = offs;
    this.invalid = invalid;
  }


  
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Token) {
      Token t2 = (Token)obj;
      return (this.type == t2.getType() && this.lexeme.equals(t2.getLexeme()) && this.line == t2
        .getLine() && this.column == t2.getColumn() && this.invalid == t2
        .isInvalid());
    } 
    return false;
  }


  
  public int getColumn() {
    return this.column;
  }


  
  public int getLength() {
    return this.lexeme.length();
  }


  
  public String getLexeme() {
    return this.lexeme;
  }


  
  public int getLine() {
    return this.line;
  }


  
  public int getOffset() {
    return this.offset;
  }


  
  public int getType() {
    return this.type;
  }


  
  public int hashCode() {
    return this.lexeme.hashCode();
  }


  
  public boolean isBasicType() {
    switch (getType()) {
      case 131075:
      case 131077:
      case 131080:
      case 131086:
      case 131092:
      case 131099:
      case 131101:
      case 131109:
        return true;
    } 
    return false;
  }



  
  public boolean isIdentifier() {
    return ((getType() & 0x40000) > 0);
  }


  
  public boolean isInvalid() {
    return this.invalid;
  }


  
  public boolean isOperator() {
    return ((getType() & 0x1000000) > 0);
  }


  
  public boolean isType(int type) {
    return (this.type == type);
  }


  
  public String toString() {
    return "[TokenImpl: type=" + this.type + "; lexeme=\"" + this.lexeme + "\"; line=" + 

      
      getLine() + "; col=" + 
      getColumn() + "; offs=" + 
      getOffset() + "; invalid=" + 
      isInvalid() + "]";
  }
}
