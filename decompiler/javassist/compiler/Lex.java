package javassist.compiler;



























public class Lex
  implements TokenId
{
  private int lastChar;
  private StringBuffer textBuffer;
  private Token currentToken;
  private Token lookAheadTokens;
  private String input;
  private int position;
  private int maxlen;
  private int lineNumber;
  
  public Lex(String s) {
    this.lastChar = -1;
    this.textBuffer = new StringBuffer();
    this.currentToken = new Token();
    this.lookAheadTokens = null;
    
    this.input = s;
    this.position = 0;
    this.maxlen = s.length();
    this.lineNumber = 0;
  }
  
  public int get() {
    if (this.lookAheadTokens == null) {
      return get(this.currentToken);
    }
    Token t = this.lookAheadTokens;
    this.lookAheadTokens = this.lookAheadTokens.next;
    return t.tokenId;
  }



  
  public int lookAhead() {
    return lookAhead(0);
  }
  
  public int lookAhead(int i) {
    Token tk = this.lookAheadTokens;
    if (tk == null) {
      this.lookAheadTokens = tk = this.currentToken;
      tk.next = null;
      get(tk);
    } 
    
    for (; i-- > 0; tk = tk.next) {
      if (tk.next == null) {
        
        Token tk2 = new Token();
        get(tk2);
      } 
    } 
    this.currentToken = tk;
    return tk.tokenId;
  }
  
  public String getString() {
    return this.currentToken.textValue;
  }
  
  public long getLong() {
    return this.currentToken.longValue;
  }
  
  public double getDouble() {
    return this.currentToken.doubleValue;
  }

  
  private int get(Token token) {
    while (true) {
      int t = readLine(token);
      if (t != 10) {
        token.tokenId = t;
        return t;
      } 
    } 
  } private int readLine(Token token) {
    int c = getNextNonWhiteChar();
    if (c < 0)
      return c; 
    if (c == 10) {
      this.lineNumber++;
      return 10;
    } 
    if (c == 39)
      return readCharConst(token); 
    if (c == 34)
      return readStringL(token); 
    if (48 <= c && c <= 57)
      return readNumber(c, token); 
    if (c == 46) {
      c = getc();
      if (48 <= c && c <= 57) {
        StringBuffer tbuf = this.textBuffer;
        tbuf.setLength(0);
        tbuf.append('.');
        return readDouble(tbuf, c, token);
      } 
      ungetc(c);
      return readSeparator(46);
    } 
    if (Character.isJavaIdentifierStart((char)c))
      return readIdentifier(c, token); 
    return readSeparator(c);
  }

  
  private int getNextNonWhiteChar() {
    while (true) {
      int c = getc();
      if (c == 47) {
        c = getc();
        if (c == 47)
        { do {
            c = getc();
          } while (c != 10 && c != 13 && c != -1); }
        else if (c == 42)
        { while (true) {
            c = getc();
            if (c == -1)
              break; 
            if (c == 42) {
              if ((c = getc()) == 47) {
                c = 32;
                
                break;
              } 
              ungetc(c);
            } 
          }  }
        else { ungetc(c);
          c = 47; }
      
      } 
      if (!isBlank(c))
        return c; 
    } 
  }
  
  private int readCharConst(Token token) {
    int value = 0; int c;
    while ((c = getc()) != 39) {
      if (c == 92) {
        value = readEscapeChar(); continue;
      }  if (c < 32) {
        if (c == 10) {
          this.lineNumber++;
        }
        return 500;
      } 
      
      value = c;
    } 
    token.longValue = value;
    return 401;
  }
  
  private int readEscapeChar() {
    int c = getc();
    if (c == 110) {
      c = 10;
    } else if (c == 116) {
      c = 9;
    } else if (c == 114) {
      c = 13;
    } else if (c == 102) {
      c = 12;
    } else if (c == 10) {
      this.lineNumber++;
    } 
    return c;
  }

  
  private int readStringL(Token token) {
    StringBuffer tbuf = this.textBuffer;
    tbuf.setLength(0); while (true) {
      int c;
      while ((c = getc()) != 34) {
        if (c == 92) {
          c = readEscapeChar();
        } else if (c == 10 || c < 0) {
          this.lineNumber++;
          return 500;
        } 
        
        tbuf.append((char)c);
      } 
      
      while (true) {
        c = getc();
        if (c == 10) {
          this.lineNumber++; continue;
        }  if (!isBlank(c)) {
          break;
        }
      } 
      if (c != 34) {
        ungetc(c);



        
        token.textValue = tbuf.toString();
        return 406;
      } 
    } 
  } private int readNumber(int c, Token token) {
    long value = 0L;
    int c2 = getc();
    if (c == 48) {
      if (c2 == 88 || c2 == 120) {
        while (true) {
          c = getc();
          if (48 <= c && c <= 57) {
            value = value * 16L + (c - 48); continue;
          }  if (65 <= c && c <= 70) {
            value = value * 16L + (c - 65 + 10); continue;
          }  if (97 <= c && c <= 102) {
            value = value * 16L + (c - 97 + 10); continue;
          }  break;
        }  token.longValue = value;
        if (c == 76 || c == 108)
          return 403; 
        ungetc(c);
        return 402;
      } 
      
      if (48 <= c2 && c2 <= 55) {
        value = (c2 - 48);
        while (true) {
          c = getc();
          if (48 <= c && c <= 55) {
            value = value * 8L + (c - 48); continue;
          }  break;
        }  token.longValue = value;
        if (c == 76 || c == 108)
          return 403; 
        ungetc(c);
        return 402;
      } 
    } 

    
    value = (c - 48);
    while (48 <= c2 && c2 <= 57) {
      value = value * 10L + c2 - 48L;
      c2 = getc();
    } 
    
    token.longValue = value;
    if (c2 == 70 || c2 == 102) {
      token.doubleValue = value;
      return 404;
    } 
    if (c2 == 69 || c2 == 101 || c2 == 68 || c2 == 100 || c2 == 46) {
      
      StringBuffer tbuf = this.textBuffer;
      tbuf.setLength(0);
      tbuf.append(value);
      return readDouble(tbuf, c2, token);
    } 
    if (c2 == 76 || c2 == 108) {
      return 403;
    }
    ungetc(c2);
    return 402;
  }

  
  private int readDouble(StringBuffer sbuf, int c, Token token) {
    if (c != 69 && c != 101 && c != 68 && c != 100) {
      sbuf.append((char)c);
      while (true) {
        c = getc();
        if (48 <= c && c <= 57) {
          sbuf.append((char)c);
          continue;
        } 
        break;
      } 
    } 
    if (c == 69 || c == 101) {
      sbuf.append((char)c);
      c = getc();
      if (c == 43 || c == 45) {
        sbuf.append((char)c);
        c = getc();
      } 
      
      while (48 <= c && c <= 57) {
        sbuf.append((char)c);
        c = getc();
      } 
    } 
    
    try {
      token.doubleValue = Double.parseDouble(sbuf.toString());
    }
    catch (NumberFormatException e) {
      return 500;
    } 
    
    if (c == 70 || c == 102)
      return 404; 
    if (c != 68 && c != 100) {
      ungetc(c);
    }
    return 405;
  }

  
  private static final int[] equalOps = new int[] { 350, 0, 0, 0, 351, 352, 0, 0, 0, 353, 354, 0, 355, 0, 356, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 357, 358, 359, 0 };




  
  private int readSeparator(int c) {
    int c2;
    if (33 <= c && c <= 63) {
      int t = equalOps[c - 33];
      if (t == 0)
        return c; 
      c2 = getc();
      if (c == c2) {
        int c3; switch (c) {
          case 61:
            return 358;
          case 43:
            return 362;
          case 45:
            return 363;
          case 38:
            return 369;
          case 60:
            c3 = getc();
            if (c3 == 61)
              return 365; 
            ungetc(c3);
            return 364;
          case 62:
            c3 = getc();
            if (c3 == 61)
              return 367; 
            if (c3 == 62) {
              c3 = getc();
              if (c3 == 61)
                return 371; 
              ungetc(c3);
              return 370;
            } 
            
            ungetc(c3);
            return 366;
        } 


      
      } else if (c2 == 61) {
        return t;
      } 
    } else if (c == 94) {
      c2 = getc();
      if (c2 == 61) {
        return 360;
      }
    } else if (c == 124) {
      c2 = getc();
      if (c2 == 61)
        return 361; 
      if (c2 == 124) {
        return 368;
      }
    } else {
      return c;
    } 
    ungetc(c2);
    return c;
  }
  
  private int readIdentifier(int c, Token token) {
    StringBuffer tbuf = this.textBuffer;
    tbuf.setLength(0);
    
    do {
      tbuf.append((char)c);
      c = getc();
    } while (Character.isJavaIdentifierPart((char)c));
    
    ungetc(c);
    
    String name = tbuf.toString();
    int t = ktable.lookup(name);
    if (t >= 0) {
      return t;
    }





    
    token.textValue = name;
    return 400;
  }
  
  private static final KeywordTable ktable = new KeywordTable();
  
  static {
    ktable.append("abstract", 300);
    ktable.append("boolean", 301);
    ktable.append("break", 302);
    ktable.append("byte", 303);
    ktable.append("case", 304);
    ktable.append("catch", 305);
    ktable.append("char", 306);
    ktable.append("class", 307);
    ktable.append("const", 308);
    ktable.append("continue", 309);
    ktable.append("default", 310);
    ktable.append("do", 311);
    ktable.append("double", 312);
    ktable.append("else", 313);
    ktable.append("extends", 314);
    ktable.append("false", 411);
    ktable.append("final", 315);
    ktable.append("finally", 316);
    ktable.append("float", 317);
    ktable.append("for", 318);
    ktable.append("goto", 319);
    ktable.append("if", 320);
    ktable.append("implements", 321);
    ktable.append("import", 322);
    ktable.append("instanceof", 323);
    ktable.append("int", 324);
    ktable.append("interface", 325);
    ktable.append("long", 326);
    ktable.append("native", 327);
    ktable.append("new", 328);
    ktable.append("null", 412);
    ktable.append("package", 329);
    ktable.append("private", 330);
    ktable.append("protected", 331);
    ktable.append("public", 332);
    ktable.append("return", 333);
    ktable.append("short", 334);
    ktable.append("static", 335);
    ktable.append("strictfp", 347);
    ktable.append("super", 336);
    ktable.append("switch", 337);
    ktable.append("synchronized", 338);
    ktable.append("this", 339);
    ktable.append("throw", 340);
    ktable.append("throws", 341);
    ktable.append("transient", 342);
    ktable.append("true", 410);
    ktable.append("try", 343);
    ktable.append("void", 344);
    ktable.append("volatile", 345);
    ktable.append("while", 346);
  }
  
  private static boolean isBlank(int c) {
    return (c == 32 || c == 9 || c == 12 || c == 13 || c == 10);
  }


  
  private static boolean isDigit(int c) {
    return (48 <= c && c <= 57);
  }
  
  private void ungetc(int c) {
    this.lastChar = c;
  }
  
  public String getTextAround() {
    int begin = this.position - 10;
    if (begin < 0) {
      begin = 0;
    }
    int end = this.position + 10;
    if (end > this.maxlen) {
      end = this.maxlen;
    }
    return this.input.substring(begin, end);
  }
  
  private int getc() {
    if (this.lastChar < 0) {
      if (this.position < this.maxlen) {
        return this.input.charAt(this.position++);
      }
      return -1;
    }  int c = this.lastChar;
    this.lastChar = -1;
    return c;
  }
}
