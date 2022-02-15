package org.springframework.expression.spel.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;



























class Tokenizer
{
  private static final String[] ALTERNATIVE_OPERATOR_NAMES = new String[] { "DIV", "EQ", "GE", "GT", "LE", "LT", "MOD", "NE", "NOT" };

  
  private static final byte[] FLAGS = new byte[256]; private static final byte IS_DIGIT = 1; private static final byte IS_HEXDIGIT = 2;
  private static final byte IS_ALPHA = 4;
  private String expressionString;
  private char[] charsToProcess;
  private int pos;
  private int max;
  
  static {
    int ch;
    for (ch = 48; ch <= 57; ch++) {
      FLAGS[ch] = (byte)(FLAGS[ch] | 0x3);
    }
    for (ch = 65; ch <= 70; ch++) {
      FLAGS[ch] = (byte)(FLAGS[ch] | 0x2);
    }
    for (ch = 97; ch <= 102; ch++) {
      FLAGS[ch] = (byte)(FLAGS[ch] | 0x2);
    }
    for (ch = 65; ch <= 90; ch++) {
      FLAGS[ch] = (byte)(FLAGS[ch] | 0x4);
    }
    for (ch = 97; ch <= 122; ch++) {
      FLAGS[ch] = (byte)(FLAGS[ch] | 0x4);
    }
  }









  
  private List<Token> tokens = new ArrayList<>();

  
  public Tokenizer(String inputData) {
    this.expressionString = inputData;
    this.charsToProcess = (inputData + "\000").toCharArray();
    this.max = this.charsToProcess.length;
    this.pos = 0;
  }

  
  public List<Token> process() {
    while (this.pos < this.max) {
      char ch = this.charsToProcess[this.pos];
      if (isAlphabetic(ch)) {
        lexIdentifier();
        continue;
      } 
      switch (ch) {
        case '+':
          if (isTwoCharToken(TokenKind.INC)) {
            pushPairToken(TokenKind.INC);
            continue;
          } 
          pushCharToken(TokenKind.PLUS);
          continue;
        
        case '_':
          lexIdentifier();
          continue;
        case '-':
          if (isTwoCharToken(TokenKind.DEC)) {
            pushPairToken(TokenKind.DEC);
            continue;
          } 
          pushCharToken(TokenKind.MINUS);
          continue;
        
        case ':':
          pushCharToken(TokenKind.COLON);
          continue;
        case '.':
          pushCharToken(TokenKind.DOT);
          continue;
        case ',':
          pushCharToken(TokenKind.COMMA);
          continue;
        case '*':
          pushCharToken(TokenKind.STAR);
          continue;
        case '/':
          pushCharToken(TokenKind.DIV);
          continue;
        case '%':
          pushCharToken(TokenKind.MOD);
          continue;
        case '(':
          pushCharToken(TokenKind.LPAREN);
          continue;
        case ')':
          pushCharToken(TokenKind.RPAREN);
          continue;
        case '[':
          pushCharToken(TokenKind.LSQUARE);
          continue;
        case '#':
          pushCharToken(TokenKind.HASH);
          continue;
        case ']':
          pushCharToken(TokenKind.RSQUARE);
          continue;
        case '{':
          pushCharToken(TokenKind.LCURLY);
          continue;
        case '}':
          pushCharToken(TokenKind.RCURLY);
          continue;
        case '@':
          pushCharToken(TokenKind.BEAN_REF);
          continue;
        case '^':
          if (isTwoCharToken(TokenKind.SELECT_FIRST)) {
            pushPairToken(TokenKind.SELECT_FIRST);
            continue;
          } 
          pushCharToken(TokenKind.POWER);
          continue;
        
        case '!':
          if (isTwoCharToken(TokenKind.NE)) {
            pushPairToken(TokenKind.NE); continue;
          } 
          if (isTwoCharToken(TokenKind.PROJECT)) {
            pushPairToken(TokenKind.PROJECT);
            continue;
          } 
          pushCharToken(TokenKind.NOT);
          continue;
        
        case '=':
          if (isTwoCharToken(TokenKind.EQ)) {
            pushPairToken(TokenKind.EQ);
            continue;
          } 
          pushCharToken(TokenKind.ASSIGN);
          continue;
        
        case '&':
          if (isTwoCharToken(TokenKind.SYMBOLIC_AND)) {
            pushPairToken(TokenKind.SYMBOLIC_AND);
            continue;
          } 
          pushCharToken(TokenKind.FACTORY_BEAN_REF);
          continue;
        
        case '|':
          if (!isTwoCharToken(TokenKind.SYMBOLIC_OR)) {
            raiseParseException(this.pos, SpelMessage.MISSING_CHARACTER, new Object[] { "|" });
          }
          pushPairToken(TokenKind.SYMBOLIC_OR);
          continue;
        case '?':
          if (isTwoCharToken(TokenKind.SELECT)) {
            pushPairToken(TokenKind.SELECT); continue;
          } 
          if (isTwoCharToken(TokenKind.ELVIS)) {
            pushPairToken(TokenKind.ELVIS); continue;
          } 
          if (isTwoCharToken(TokenKind.SAFE_NAVI)) {
            pushPairToken(TokenKind.SAFE_NAVI);
            continue;
          } 
          pushCharToken(TokenKind.QMARK);
          continue;
        
        case '$':
          if (isTwoCharToken(TokenKind.SELECT_LAST)) {
            pushPairToken(TokenKind.SELECT_LAST);
            continue;
          } 
          lexIdentifier();
          continue;
        
        case '>':
          if (isTwoCharToken(TokenKind.GE)) {
            pushPairToken(TokenKind.GE);
            continue;
          } 
          pushCharToken(TokenKind.GT);
          continue;
        
        case '<':
          if (isTwoCharToken(TokenKind.LE)) {
            pushPairToken(TokenKind.LE);
            continue;
          } 
          pushCharToken(TokenKind.LT);
          continue;
        
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          lexNumericLiteral((ch == '0'));
          continue;
        
        case '\t':
        case '\n':
        case '\r':
        case ' ':
          this.pos++;
          continue;
        case '\'':
          lexQuotedStringLiteral();
          continue;
        case '"':
          lexDoubleQuotedStringLiteral();
          continue;
        
        case '\000':
          this.pos++;
          continue;
        case '\\':
          raiseParseException(this.pos, SpelMessage.UNEXPECTED_ESCAPE_CHAR, new Object[0]);
          continue;
      } 
      throw new IllegalStateException("Cannot handle (" + ch + ") '" + ch + "'");
    } 

    
    return this.tokens;
  }


  
  private void lexQuotedStringLiteral() {
    int start = this.pos;
    boolean terminated = false;
    while (!terminated) {
      this.pos++;
      char ch = this.charsToProcess[this.pos];
      if (ch == '\'')
      {
        if (this.charsToProcess[this.pos + 1] == '\'') {
          this.pos++;
        } else {
          
          terminated = true;
        } 
      }
      if (isExhausted()) {
        raiseParseException(start, SpelMessage.NON_TERMINATING_QUOTED_STRING, new Object[0]);
      }
    } 
    this.pos++;
    this.tokens.add(new Token(TokenKind.LITERAL_STRING, subarray(start, this.pos), start, this.pos));
  }

  
  private void lexDoubleQuotedStringLiteral() {
    int start = this.pos;
    boolean terminated = false;
    while (!terminated) {
      this.pos++;
      char ch = this.charsToProcess[this.pos];
      if (ch == '"')
      {
        if (this.charsToProcess[this.pos + 1] == '"') {
          this.pos++;
        } else {
          
          terminated = true;
        } 
      }
      if (isExhausted()) {
        raiseParseException(start, SpelMessage.NON_TERMINATING_DOUBLE_QUOTED_STRING, new Object[0]);
      }
    } 
    this.pos++;
    this.tokens.add(new Token(TokenKind.LITERAL_STRING, subarray(start, this.pos), start, this.pos));
  }
















  
  private void lexNumericLiteral(boolean firstCharIsZero) {
    boolean isReal = false;
    int start = this.pos;
    char ch = this.charsToProcess[this.pos + 1];
    boolean isHex = (ch == 'x' || ch == 'X');

    
    if (firstCharIsZero && isHex) {
      this.pos++;
      while (true) {
        this.pos++;
        
        if (!isHexadecimalDigit(this.charsToProcess[this.pos])) {
          if (isChar('L', 'l')) {
            pushHexIntToken(subarray(start + 2, this.pos), true, start, this.pos);
            this.pos++;
          } else {
            
            pushHexIntToken(subarray(start + 2, this.pos), false, start, this.pos);
          } 
          
          return;
        } 
      } 
    } 
    
    do {
      this.pos++;
    }
    while (isDigit(this.charsToProcess[this.pos]));

    
    ch = this.charsToProcess[this.pos];
    if (ch == '.') {
      isReal = true;
      int dotpos = this.pos;
      
      while (true) {
        this.pos++;
        
        if (!isDigit(this.charsToProcess[this.pos])) {
          if (this.pos == dotpos + 1) {


            
            this.pos = dotpos;
            pushIntToken(subarray(start, this.pos), false, start, this.pos); return;
          }  break;
        } 
      } 
    } 
    int endOfNumber = this.pos;



    
    if (isChar('L', 'l')) {
      if (isReal) {
        raiseParseException(start, SpelMessage.REAL_CANNOT_BE_LONG, new Object[0]);
      }
      pushIntToken(subarray(start, endOfNumber), true, start, endOfNumber);
      this.pos++;
    } else {
      if (isExponentChar(this.charsToProcess[this.pos])) {
        isReal = true;
        this.pos++;
        char possibleSign = this.charsToProcess[this.pos];
        if (isSign(possibleSign)) {
          this.pos++;
        }

        
        while (true) {
          this.pos++;
          
          if (!isDigit(this.charsToProcess[this.pos])) {
            boolean bool = false;
            if (isFloatSuffix(this.charsToProcess[this.pos])) {
              bool = true;
              endOfNumber = ++this.pos;
            }
            else if (isDoubleSuffix(this.charsToProcess[this.pos])) {
              endOfNumber = ++this.pos;
            } 
            pushRealToken(subarray(start, this.pos), bool, start, this.pos); return;
          } 
        } 
      }  ch = this.charsToProcess[this.pos];
      boolean isFloat = false;
      if (isFloatSuffix(ch)) {
        isReal = true;
        isFloat = true;
        endOfNumber = ++this.pos;
      }
      else if (isDoubleSuffix(ch)) {
        isReal = true;
        endOfNumber = ++this.pos;
      } 
      if (isReal) {
        pushRealToken(subarray(start, endOfNumber), isFloat, start, endOfNumber);
      } else {
        
        pushIntToken(subarray(start, endOfNumber), false, start, endOfNumber);
      } 
    } 
  }
  
  private void lexIdentifier() {
    int start = this.pos;
    while (true) {
      this.pos++;
      
      if (!isIdentifier(this.charsToProcess[this.pos])) {
        char[] subarray = subarray(start, this.pos);


        
        if (this.pos - start == 2 || this.pos - start == 3) {
          String asString = (new String(subarray)).toUpperCase();
          int idx = Arrays.binarySearch((Object[])ALTERNATIVE_OPERATOR_NAMES, asString);
          if (idx >= 0) {
            pushOneCharOrTwoCharToken(TokenKind.valueOf(asString), start, subarray);
            return;
          } 
        } 
        this.tokens.add(new Token(TokenKind.IDENTIFIER, subarray, start, this.pos));
        return;
      } 
    }  } private void pushIntToken(char[] data, boolean isLong, int start, int end) {
    if (isLong) {
      this.tokens.add(new Token(TokenKind.LITERAL_LONG, data, start, end));
    } else {
      
      this.tokens.add(new Token(TokenKind.LITERAL_INT, data, start, end));
    } 
  }
  
  private void pushHexIntToken(char[] data, boolean isLong, int start, int end) {
    if (data.length == 0) {
      if (isLong) {
        raiseParseException(start, SpelMessage.NOT_A_LONG, new Object[] { this.expressionString.substring(start, end + 1) });
      } else {
        
        raiseParseException(start, SpelMessage.NOT_AN_INTEGER, new Object[] { this.expressionString.substring(start, end) });
      } 
    }
    if (isLong) {
      this.tokens.add(new Token(TokenKind.LITERAL_HEXLONG, data, start, end));
    } else {
      
      this.tokens.add(new Token(TokenKind.LITERAL_HEXINT, data, start, end));
    } 
  }
  
  private void pushRealToken(char[] data, boolean isFloat, int start, int end) {
    if (isFloat) {
      this.tokens.add(new Token(TokenKind.LITERAL_REAL_FLOAT, data, start, end));
    } else {
      
      this.tokens.add(new Token(TokenKind.LITERAL_REAL, data, start, end));
    } 
  }
  
  private char[] subarray(int start, int end) {
    return Arrays.copyOfRange(this.charsToProcess, start, end);
  }



  
  private boolean isTwoCharToken(TokenKind kind) {
    return (kind.tokenChars.length == 2 && this.charsToProcess[this.pos] == kind.tokenChars[0] && this.charsToProcess[this.pos + 1] == kind.tokenChars[1]);
  }





  
  private void pushCharToken(TokenKind kind) {
    this.tokens.add(new Token(kind, this.pos, this.pos + 1));
    this.pos++;
  }



  
  private void pushPairToken(TokenKind kind) {
    this.tokens.add(new Token(kind, this.pos, this.pos + 2));
    this.pos += 2;
  }
  
  private void pushOneCharOrTwoCharToken(TokenKind kind, int pos, char[] data) {
    this.tokens.add(new Token(kind, data, pos, pos + kind.getLength()));
  }

  
  private boolean isIdentifier(char ch) {
    return (isAlphabetic(ch) || isDigit(ch) || ch == '_' || ch == '$');
  }
  
  private boolean isChar(char a, char b) {
    char ch = this.charsToProcess[this.pos];
    return (ch == a || ch == b);
  }
  
  private boolean isExponentChar(char ch) {
    return (ch == 'e' || ch == 'E');
  }
  
  private boolean isFloatSuffix(char ch) {
    return (ch == 'f' || ch == 'F');
  }
  
  private boolean isDoubleSuffix(char ch) {
    return (ch == 'd' || ch == 'D');
  }
  
  private boolean isSign(char ch) {
    return (ch == '+' || ch == '-');
  }
  
  private boolean isDigit(char ch) {
    if (ch > 'ÿ') {
      return false;
    }
    return ((FLAGS[ch] & 0x1) != 0);
  }
  
  private boolean isAlphabetic(char ch) {
    if (ch > 'ÿ') {
      return false;
    }
    return ((FLAGS[ch] & 0x4) != 0);
  }
  
  private boolean isHexadecimalDigit(char ch) {
    if (ch > 'ÿ') {
      return false;
    }
    return ((FLAGS[ch] & 0x2) != 0);
  }
  
  private boolean isExhausted() {
    return (this.pos == this.max - 1);
  }
  
  private void raiseParseException(int start, SpelMessage msg, Object... inserts) {
    throw new InternalParseException(new SpelParseException(this.expressionString, start, msg, inserts));
  }
}
