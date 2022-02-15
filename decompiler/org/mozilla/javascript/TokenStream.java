package org.mozilla.javascript;

import java.io.IOException;
import java.io.Reader;




































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































class TokenStream
{
  private static final int EOF_CHAR = -1;
  private static final char BYTE_ORDER_MARK = '﻿';
  private boolean dirtyLine;
  String regExpFlags;
  private String string;
  private double number;
  private boolean isOctal;
  private boolean isHex;
  private int quoteChar;
  private char[] stringBuffer;
  private int stringBufferTop;
  private ObjToIntMap allStrings;
  private final int[] ungetBuffer;
  private int ungetCursor;
  private boolean hitEOF;
  private int lineStart;
  private int lineEndChar;
  int lineno;
  private String sourceString;
  private Reader sourceReader;
  private char[] sourceBuffer;
  private int sourceEnd;
  int sourceCursor;
  int cursor;
  int tokenBeg;
  int tokenEnd;
  Token.CommentType commentType;
  private boolean xmlIsAttribute;
  private boolean xmlIsTagContent;
  private int xmlOpenTagsCount;
  private Parser parser;
  private String commentPrefix;
  private int commentCursor;
  
  TokenStream(Parser parser, Reader sourceReader, String sourceString, int lineno) {
    this.string = "";






    
    this.stringBuffer = new char[128];
    
    this.allStrings = new ObjToIntMap(50);

    
    this.ungetBuffer = new int[3];

    
    this.hitEOF = false;
    
    this.lineStart = 0;
    this.lineEndChar = -1;





























    
    this.commentPrefix = "";
    this.commentCursor = -1;
    this.parser = parser;
    this.lineno = lineno;
    if (sourceReader != null) {
      if (sourceString != null)
        Kit.codeBug(); 
      this.sourceReader = sourceReader;
      this.sourceBuffer = new char[512];
      this.sourceEnd = 0;
    } else {
      if (sourceString == null)
        Kit.codeBug(); 
      this.sourceString = sourceString;
      this.sourceEnd = sourceString.length();
    } 
    this.sourceCursor = this.cursor = 0;
  }
  
  String tokenToString(int token) {
    return "";
  }
  
  static boolean isKeyword(String s) {
    return (0 != stringToKeyword(s));
  }
  
  private static int stringToKeyword(String name) {
    int c, Id_break = 120;
    int Id_case = 115;
    int Id_continue = 121;
    int Id_default = 116;
    int Id_delete = 31;
    int Id_do = 118;
    int Id_else = 113;
    int Id_export = 127;
    int Id_false = 44;
    int Id_for = 119;
    int Id_function = 109;
    int Id_if = 112;
    int Id_in = 52;
    int Id_let = 153;
    int Id_new = 30;
    int Id_null = 42;
    int Id_return = 4;
    int Id_switch = 114;
    int Id_this = 43;
    int Id_true = 45;
    int Id_typeof = 32;
    int Id_var = 122;
    int Id_void = 126;
    int Id_while = 117;
    int Id_with = 123;
    int Id_yield = 72;
    int Id_abstract = 127;
    int Id_boolean = 127;
    int Id_byte = 127;
    int Id_catch = 124;
    int Id_char = 127;
    int Id_class = 127;
    int Id_const = 154;
    int Id_debugger = 160;
    int Id_double = 127;
    int Id_enum = 127;
    int Id_extends = 127;
    int Id_final = 127;
    int Id_finally = 125;
    int Id_float = 127;
    int Id_goto = 127;
    int Id_implements = 127;
    int Id_import = 127;
    int Id_instanceof = 53;
    int Id_int = 127;
    int Id_interface = 127;
    int Id_long = 127;
    int Id_native = 127;
    int Id_package = 127;
    int Id_private = 127;
    int Id_protected = 127;
    int Id_public = 127;
    int Id_short = 127;
    int Id_static = 127;
    int Id_super = 127;
    int Id_synchronized = 127;
    int Id_throw = 50;
    int Id_throws = 127;
    int Id_transient = 127;
    int Id_try = 81;
    int Id_volatile = 127;
    String s = name;
    int id = 0;
    String X = null;
    switch (s.length()) {
      case 2:
        c = s.charAt(1);
        if (c == 102) {
          if (s.charAt(0) == 'i') {
            id = 112;
            break;
          } 
        } else if (c == 110) {
          if (s.charAt(0) == 'i') {
            id = 52;
            break;
          } 
        } else if (c == 111 && s.charAt(0) == 'd') {
          id = 118;
          break;
        } 
      case 3:
        switch (s.charAt(0)) {
          case 'f':
            if (s.charAt(2) == 'r' && s.charAt(1) == 'o')
              id = 119; 
            break;
          case 'i':
            if (s.charAt(2) == 't' && s.charAt(1) == 'n')
              id = 127; 
            break;
          case 'l':
            if (s.charAt(2) == 't' && s.charAt(1) == 'e')
              id = 153; 
            break;
          case 'n':
            if (s.charAt(2) == 'w' && s.charAt(1) == 'e')
              id = 30; 
            break;
          case 't':
            if (s.charAt(2) == 'y' && s.charAt(1) == 'r')
              id = 81; 
            break;
          case 'v':
            if (s.charAt(2) == 'r' && s.charAt(1) == 'a')
              id = 122; 
            break;
        } 
      case 4:
        switch (s.charAt(0)) {
          case 'b':
            X = "byte";
            id = 127;
            break;
          case 'c':
            c = s.charAt(3);
            if (c == 101) {
              if (s.charAt(2) == 's' && s.charAt(1) == 'a')
                id = 115; 
              break;
            } 
            if (c == 114 && s.charAt(2) == 'a' && s.charAt(1) == 'h')
              id = 127; 
            break;
          case 'e':
            c = s.charAt(3);
            if (c == 101) {
              if (s.charAt(2) == 's' && s.charAt(1) == 'l')
                id = 113; 
              break;
            } 
            if (c == 109 && s.charAt(2) == 'u' && s.charAt(1) == 'n')
              id = 127; 
            break;
          case 'g':
            X = "goto";
            id = 127;
            break;
          case 'l':
            X = "long";
            id = 127;
            break;
          case 'n':
            X = "null";
            id = 42;
            break;
          case 't':
            c = s.charAt(3);
            if (c == 101) {
              if (s.charAt(2) == 'u' && s.charAt(1) == 'r')
                id = 45; 
              break;
            } 
            if (c == 115 && s.charAt(2) == 'i' && s.charAt(1) == 'h')
              id = 43; 
            break;
          case 'v':
            X = "void";
            id = 126;
            break;
          case 'w':
            X = "with";
            id = 123;
            break;
        } 
      case 5:
        switch (s.charAt(2)) {
          case 'a':
            X = "class";
            id = 127;
            break;
          case 'e':
            c = s.charAt(0);
            if (c == 98) {
              X = "break";
              id = 120;
              break;
            } 
            if (c == 121) {
              X = "yield";
              id = 72;
            } 
            break;
          case 'i':
            X = "while";
            id = 117;
            break;
          case 'l':
            X = "false";
            id = 44;
            break;
          case 'n':
            c = s.charAt(0);
            if (c == 99) {
              X = "const";
              id = 154;
              break;
            } 
            if (c == 102) {
              X = "final";
              id = 127;
            } 
            break;
          case 'o':
            c = s.charAt(0);
            if (c == 102) {
              X = "float";
              id = 127;
              break;
            } 
            if (c == 115) {
              X = "short";
              id = 127;
            } 
            break;
          case 'p':
            X = "super";
            id = 127;
            break;
          case 'r':
            X = "throw";
            id = 50;
            break;
          case 't':
            X = "catch";
            id = 124;
            break;
        } 
      case 6:
        switch (s.charAt(1)) {
          case 'a':
            X = "native";
            id = 127;
            break;
          case 'e':
            c = s.charAt(0);
            if (c == 100) {
              X = "delete";
              id = 31;
              break;
            } 
            if (c == 114) {
              X = "return";
              id = 4;
            } 
            break;
          case 'h':
            X = "throws";
            id = 127;
            break;
          case 'm':
            X = "import";
            id = 127;
            break;
          case 'o':
            X = "double";
            id = 127;
            break;
          case 't':
            X = "static";
            id = 127;
            break;
          case 'u':
            X = "public";
            id = 127;
            break;
          case 'w':
            X = "switch";
            id = 114;
            break;
          case 'x':
            X = "export";
            id = 127;
            break;
          case 'y':
            X = "typeof";
            id = 32;
            break;
        } 
      case 7:
        switch (s.charAt(1)) {
          case 'a':
            X = "package";
            id = 127;
            break;
          case 'e':
            X = "default";
            id = 116;
            break;
          case 'i':
            X = "finally";
            id = 125;
            break;
          case 'o':
            X = "boolean";
            id = 127;
            break;
          case 'r':
            X = "private";
            id = 127;
            break;
          case 'x':
            X = "extends";
            id = 127;
            break;
        } 
      case 8:
        switch (s.charAt(0)) {
          case 'a':
            X = "abstract";
            id = 127;
            break;
          case 'c':
            X = "continue";
            id = 121;
            break;
          case 'd':
            X = "debugger";
            id = 160;
            break;
          case 'f':
            X = "function";
            id = 109;
            break;
          case 'v':
            X = "volatile";
            id = 127;
            break;
        } 
      case 9:
        c = s.charAt(0);
        if (c == 105) {
          X = "interface";
          id = 127;
        } else if (c == 112) {
          X = "protected";
          id = 127;
        } else if (c == 116) {
          X = "transient";
          id = 127;
        } 
      case 10:
        c = s.charAt(1);
        if (c == 109) {
          X = "implements";
          id = 127;
        } else if (c == 110) {
          X = "instanceof";
          id = 53;
        } 
      case 12:
        X = "synchronized";
        id = 127;
      default:
        if (X != null && X != s && !X.equals(s))
          id = 0; 
        break;
    } 
    if (id == 0)
      return 0; 
    return id & 0xFF;
  }
  
  final String getSourceString() {
    return this.sourceString;
  }
  
  final int getLineno() {
    return this.lineno;
  }
  
  final String getString() {
    return this.string;
  }
  
  final char getQuoteChar() {
    return (char)this.quoteChar;
  }
  
  final double getNumber() {
    return this.number;
  }
  
  final boolean isNumberOctal() {
    return this.isOctal;
  }
  
  final boolean isNumberHex() {
    return this.isHex;
  }
  
  final boolean eof() {
    return this.hitEOF;
  }
  
  final int getToken() throws IOException {
    int c;
    while (true) {
      c = getChar();
      if (c == -1) {
        this.tokenBeg = this.cursor - 1;
        this.tokenEnd = this.cursor;
        return 0;
      } 
      if (c == 10) {
        this.dirtyLine = false;
        this.tokenBeg = this.cursor - 1;
        this.tokenEnd = this.cursor;
        return 1;
      } 
      if (!isJSSpace(c)) {
        boolean identifierStart;
        if (c != 45)
          this.dirtyLine = true; 
        this.tokenBeg = this.cursor - 1;
        this.tokenEnd = this.cursor;
        if (c == 64)
          return 147; 
        boolean isUnicodeEscapeStart = false;
        if (c == 92) {
          c = getChar();
          if (c == 117) {
            identifierStart = true;
            isUnicodeEscapeStart = true;
            this.stringBufferTop = 0;
          } else {
            identifierStart = false;
            ungetChar(c);
            c = 92;
          } 
        } else {
          identifierStart = Character.isJavaIdentifierStart((char)c);
          if (identifierStart) {
            this.stringBufferTop = 0;
            addToString(c);
          } 
        } 
        if (identifierStart) {
          boolean containsEscape = isUnicodeEscapeStart;
          while (true) {
            while (isUnicodeEscapeStart) {
              int escapeVal = 0;
              for (int i = 0; i != 4; i++) {
                c = getChar();
                escapeVal = Kit.xDigitToInt(c, escapeVal);
                if (escapeVal < 0)
                  break; 
              } 
              if (escapeVal < 0) {
                this.parser.addError("msg.invalid.escape");
                return -1;
              } 
              addToString(escapeVal);
              isUnicodeEscapeStart = false;
            } 
            c = getChar();
            if (c == 92) {
              c = getChar();
              if (c == 117) {
                isUnicodeEscapeStart = true;
                containsEscape = true;
                continue;
              } 
              this.parser.addError("msg.illegal.character");
              return -1;
            } 
            if (c == -1 || c == 65279 || !Character.isJavaIdentifierPart((char)c))
              break; 
            addToString(c);
          } 
          ungetChar(c);
          String str = getStringFromBuffer();
          if (!containsEscape) {
            int result = stringToKeyword(str);
            if (result != 0) {
              if ((result == 153 || result == 72) && this.parser.compilerEnv.getLanguageVersion() < 170) {
                this.string = (result == 153) ? "let" : "yield";
                result = 39;
              } 
              this.string = (String)this.allStrings.intern(str);
              if (result != 127)
                return result; 
              if (!this.parser.compilerEnv.isReservedKeywordAsIdentifier())
                return result; 
            } 
          } else if (isKeyword(str)) {
            str = convertLastCharToHex(str);
          } 
          this.string = (String)this.allStrings.intern(str);
          return 39;
        } 
        if (isDigit(c) || (c == 46 && isDigit(peekChar()))) {
          double d;
          this.isOctal = false;
          this.stringBufferTop = 0;
          int base = 10;
          this.isHex = this.isOctal = false;
          if (c == 48) {
            c = getChar();
            if (c == 120 || c == 88) {
              base = 16;
              this.isHex = true;
              c = getChar();
            } else if (isDigit(c)) {
              base = 8;
              this.isOctal = true;
            } else {
              addToString(48);
            } 
          } 
          if (base == 16) {
            while (0 <= Kit.xDigitToInt(c, 0)) {
              addToString(c);
              c = getChar();
            } 
          } else {
            while (48 <= c && c <= 57) {
              if (base == 8 && c >= 56) {
                this.parser.addWarning("msg.bad.octal.literal", (c == 56) ? "8" : "9");
                base = 10;
              } 
              addToString(c);
              c = getChar();
            } 
          } 
          boolean isInteger = true;
          if (base == 10 && (c == 46 || c == 101 || c == 69)) {
            isInteger = false;
            if (c == 46)
              do {
                addToString(c);
                c = getChar();
              } while (isDigit(c)); 
            if (c == 101 || c == 69) {
              addToString(c);
              c = getChar();
              if (c == 43 || c == 45) {
                addToString(c);
                c = getChar();
              } 
              if (!isDigit(c)) {
                this.parser.addError("msg.missing.exponent");
                return -1;
              } 
              do {
                addToString(c);
                c = getChar();
              } while (isDigit(c));
            } 
          } 
          ungetChar(c);
          String numString = getStringFromBuffer();
          this.string = numString;
          if (base == 10 && !isInteger) {
            try {
              d = Double.parseDouble(numString);
            } catch (NumberFormatException ex) {
              this.parser.addError("msg.caught.nfe");
              return -1;
            } 
          } else {
            d = ScriptRuntime.stringToNumber(numString, 0, base);
          } 
          this.number = d;
          return 40;
        } 
        if (c == 34 || c == 39) {
          this.quoteChar = c;
          this.stringBufferTop = 0;
          c = getChar(false);
          label306: while (c != this.quoteChar) {
            if (c == 10 || c == -1) {
              ungetChar(c);
              this.tokenEnd = this.cursor;
              this.parser.addError("msg.unterminated.string.lit");
              return -1;
            } 
            if (c == 92) {
              int escapeVal, escapeStart, i, c1;
              c = getChar();
              switch (c) {
                case 98:
                  c = 8;
                  break;
                case 102:
                  c = 12;
                  break;
                case 110:
                  c = 10;
                  break;
                case 114:
                  c = 13;
                  break;
                case 116:
                  c = 9;
                  break;
                case 118:
                  c = 11;
                  break;
                case 117:
                  escapeStart = this.stringBufferTop;
                  addToString(117);
                  escapeVal = 0;
                  for (i = 0; i != 4; i++) {
                    c = getChar();
                    escapeVal = Kit.xDigitToInt(c, escapeVal);
                    if (escapeVal < 0)
                      continue label306; 
                    addToString(c);
                  } 
                  this.stringBufferTop = escapeStart;
                  c = escapeVal;
                  break;
                case 120:
                  c = getChar();
                  escapeVal = Kit.xDigitToInt(c, 0);
                  if (escapeVal < 0) {
                    addToString(120);
                    continue;
                  } 
                  c1 = c;
                  c = getChar();
                  escapeVal = Kit.xDigitToInt(c, escapeVal);
                  if (escapeVal < 0) {
                    addToString(120);
                    addToString(c1);
                    continue;
                  } 
                  c = escapeVal;
                  break;
                case 10:
                  c = getChar();
                  continue;
                default:
                  if (48 <= c && c < 56) {
                    int val = c - 48;
                    c = getChar();
                    if (48 <= c && c < 56) {
                      val = 8 * val + c - 48;
                      c = getChar();
                      if (48 <= c && c < 56 && val <= 31) {
                        val = 8 * val + c - 48;
                        c = getChar();
                      } 
                    } 
                    ungetChar(c);
                    c = val;
                  } 
                  break;
              } 
            } 
            addToString(c);
            c = getChar(false);
          } 
          String str = getStringFromBuffer();
          this.string = (String)this.allStrings.intern(str);
          return 41;
        } 
        break;
      } 
    } 
    switch (c) {
      case 59:
        return 82;
      case 91:
        return 83;
      case 93:
        return 84;
      case 123:
        return 85;
      case 125:
        return 86;
      case 40:
        return 87;
      case 41:
        return 88;
      case 44:
        return 89;
      case 63:
        return 102;
      case 58:
        if (matchChar(58))
          return 144; 
        return 103;
      case 46:
        if (matchChar(46))
          return 143; 
        if (matchChar(40))
          return 146; 
        return 108;
      case 124:
        if (matchChar(124))
          return 104; 
        if (matchChar(61))
          return 91; 
        return 9;
      case 94:
        if (matchChar(61))
          return 92; 
        return 10;
      case 38:
        if (matchChar(38))
          return 105; 
        if (matchChar(61))
          return 93; 
        return 11;
      case 61:
        if (matchChar(61)) {
          if (matchChar(61))
            return 46; 
          return 12;
        } 
        return 90;
      case 33:
        if (matchChar(61)) {
          if (matchChar(61))
            return 47; 
          return 13;
        } 
        return 26;
      case 60:
        if (matchChar(33)) {
          if (matchChar(45)) {
            if (matchChar(45)) {
              this.tokenBeg = this.cursor - 4;
              skipLine();
              this.commentType = Token.CommentType.HTML;
              return 161;
            } 
            ungetCharIgnoreLineEnd(45);
          } 
          ungetCharIgnoreLineEnd(33);
        } 
        if (matchChar(60)) {
          if (matchChar(61))
            return 94; 
          return 18;
        } 
        if (matchChar(61))
          return 15; 
        return 14;
      case 62:
        if (matchChar(62)) {
          if (matchChar(62)) {
            if (matchChar(61))
              return 96; 
            return 20;
          } 
          if (matchChar(61))
            return 95; 
          return 19;
        } 
        if (matchChar(61))
          return 17; 
        return 16;
      case 42:
        if (matchChar(61))
          return 99; 
        return 23;
      case 47:
        markCommentStart();
        if (matchChar(47)) {
          this.tokenBeg = this.cursor - 2;
          skipLine();
          this.commentType = Token.CommentType.LINE;
          return 161;
        } 
        if (matchChar(42)) {
          boolean lookForSlash = false;
          this.tokenBeg = this.cursor - 2;
          if (matchChar(42)) {
            lookForSlash = true;
            this.commentType = Token.CommentType.JSDOC;
          } else {
            this.commentType = Token.CommentType.BLOCK_COMMENT;
          } 
          while (true) {
            c = getChar();
            if (c == -1) {
              this.tokenEnd = this.cursor - 1;
              this.parser.addError("msg.unterminated.comment");
              return 161;
            } 
            if (c == 42) {
              lookForSlash = true;
              continue;
            } 
            if (c == 47) {
              if (lookForSlash) {
                this.tokenEnd = this.cursor;
                return 161;
              } 
              continue;
            } 
            lookForSlash = false;
            this.tokenEnd = this.cursor;
          } 
        } 
        if (matchChar(61))
          return 100; 
        return 24;
      case 37:
        if (matchChar(61))
          return 101; 
        return 25;
      case 126:
        return 27;
      case 43:
        if (matchChar(61))
          return 97; 
        if (matchChar(43))
          return 106; 
        return 21;
      case 45:
        if (matchChar(61)) {
          c = 98;
        } else if (matchChar(45)) {
          if (!this.dirtyLine)
            if (matchChar(62)) {
              markCommentStart("--");
              skipLine();
              this.commentType = Token.CommentType.HTML;
              return 161;
            }  
          c = 107;
        } else {
          c = 22;
        } 
        this.dirtyLine = true;
        return c;
    } 
    this.parser.addError("msg.illegal.character");
    return -1;
  }
  
  private static boolean isAlpha(int c) {
    if (c <= 90)
      return (65 <= c); 
    return (97 <= c && c <= 122);
  }
  
  static boolean isDigit(int c) {
    return (48 <= c && c <= 57);
  }
  
  static boolean isJSSpace(int c) {
    if (c <= 127)
      return (c == 32 || c == 9 || c == 12 || c == 11); 
    return (c == 160 || c == 65279 || Character.getType((char)c) == 12);
  }
  
  private static boolean isJSFormatChar(int c) {
    return (c > 127 && Character.getType((char)c) == 16);
  }
  
  void readRegExp(int startToken) throws IOException {
    int start = this.tokenBeg;
    this.stringBufferTop = 0;
    if (startToken == 100) {
      addToString(61);
    } else if (startToken != 24) {
      Kit.codeBug();
    } 
    boolean inCharSet = false;
    int c;
    while ((c = getChar()) != 47 || inCharSet) {
      if (c == 10 || c == -1) {
        ungetChar(c);
        this.tokenEnd = this.cursor - 1;
        this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
        this.parser.reportError("msg.unterminated.re.lit");
        return;
      } 
      if (c == 92) {
        addToString(c);
        c = getChar();
      } else if (c == 91) {
        inCharSet = true;
      } else if (c == 93) {
        inCharSet = false;
      } 
      addToString(c);
    } 
    int reEnd = this.stringBufferTop;
    while (true) {
      while (matchChar(103))
        addToString(103); 
      if (matchChar(105)) {
        addToString(105);
        continue;
      } 
      if (matchChar(109)) {
        addToString(109);
        continue;
      } 
      if (matchChar(121)) {
        addToString(121);
        continue;
      } 
      break;
    } 
    this.tokenEnd = start + this.stringBufferTop + 2;
    if (isAlpha(peekChar()))
      this.parser.reportError("msg.invalid.re.flag"); 
    this.string = new String(this.stringBuffer, 0, reEnd);
    this.regExpFlags = new String(this.stringBuffer, reEnd, this.stringBufferTop - reEnd);
  }
  
  String readAndClearRegExpFlags() {
    String flags = this.regExpFlags;
    this.regExpFlags = null;
    return flags;
  }
  
  boolean isXMLAttribute() {
    return this.xmlIsAttribute;
  }
  
  int getFirstXMLToken() throws IOException {
    this.xmlOpenTagsCount = 0;
    this.xmlIsAttribute = false;
    this.xmlIsTagContent = false;
    if (!canUngetChar())
      return -1; 
    ungetChar(60);
    return getNextXMLToken();
  }
  
  int getNextXMLToken() throws IOException {
    this.tokenBeg = this.cursor;
    this.stringBufferTop = 0;
    for (int c = getChar(); c != -1; c = getChar()) {
      if (this.xmlIsTagContent) {
        switch (c) {
          case 62:
            addToString(c);
            this.xmlIsTagContent = false;
            this.xmlIsAttribute = false;
            break;
          case 47:
            addToString(c);
            if (peekChar() == 62) {
              c = getChar();
              addToString(c);
              this.xmlIsTagContent = false;
              this.xmlOpenTagsCount--;
            } 
            break;
          case 123:
            ungetChar(c);
            this.string = getStringFromBuffer();
            return 145;
          case 34:
          case 39:
            addToString(c);
            if (!readQuotedString(c))
              return -1; 
            break;
          case 61:
            addToString(c);
            this.xmlIsAttribute = true;
            break;
          case 9:
          case 10:
          case 13:
          case 32:
            addToString(c);
            break;
          default:
            addToString(c);
            this.xmlIsAttribute = false;
            break;
        } 
        if (!this.xmlIsTagContent && this.xmlOpenTagsCount == 0) {
          this.string = getStringFromBuffer();
          return 148;
        } 
      } else {
        switch (c) {
          case 60:
            addToString(c);
            c = peekChar();
            switch (c) {
              case 33:
                c = getChar();
                addToString(c);
                c = peekChar();
                switch (c) {
                  case 45:
                    c = getChar();
                    addToString(c);
                    c = getChar();
                    if (c == 45) {
                      addToString(c);
                      if (!readXmlComment())
                        return -1; 
                      break;
                    } 
                    this.stringBufferTop = 0;
                    this.string = null;
                    this.parser.addError("msg.XML.bad.form");
                    return -1;
                  case 91:
                    c = getChar();
                    addToString(c);
                    if (getChar() == 67 && getChar() == 68 && getChar() == 65 && getChar() == 84 && getChar() == 65 && getChar() == 91) {
                      addToString(67);
                      addToString(68);
                      addToString(65);
                      addToString(84);
                      addToString(65);
                      addToString(91);
                      if (!readCDATA())
                        return -1; 
                      break;
                    } 
                    this.stringBufferTop = 0;
                    this.string = null;
                    this.parser.addError("msg.XML.bad.form");
                    return -1;
                } 
                if (!readEntity())
                  return -1; 
                break;
              case 63:
                c = getChar();
                addToString(c);
                if (!readPI())
                  return -1; 
                break;
              case 47:
                c = getChar();
                addToString(c);
                if (this.xmlOpenTagsCount == 0) {
                  this.stringBufferTop = 0;
                  this.string = null;
                  this.parser.addError("msg.XML.bad.form");
                  return -1;
                } 
                this.xmlIsTagContent = true;
                this.xmlOpenTagsCount--;
                break;
            } 
            this.xmlIsTagContent = true;
            this.xmlOpenTagsCount++;
            break;
          case 123:
            ungetChar(c);
            this.string = getStringFromBuffer();
            return 145;
          default:
            addToString(c);
            break;
        } 
      } 
    } 
    this.tokenEnd = this.cursor;
    this.stringBufferTop = 0;
    this.string = null;
    this.parser.addError("msg.XML.bad.form");
    return -1;
  }
  
  private boolean readQuotedString(int quote) throws IOException {
    for (int c = getChar(); c != -1; c = getChar()) {
      addToString(c);
      if (c == quote)
        return true; 
    } 
    this.stringBufferTop = 0;
    this.string = null;
    this.parser.addError("msg.XML.bad.form");
    return false;
  }
  
  private boolean readXmlComment() throws IOException {
    for (int c = getChar(); c != -1; ) {
      addToString(c);
      if (c == 45 && peekChar() == 45) {
        c = getChar();
        addToString(c);
        if (peekChar() == 62) {
          c = getChar();
          addToString(c);
          return true;
        } 
        continue;
      } 
      c = getChar();
    } 
    this.stringBufferTop = 0;
    this.string = null;
    this.parser.addError("msg.XML.bad.form");
    return false;
  }
  
  private boolean readCDATA() throws IOException {
    for (int c = getChar(); c != -1; ) {
      addToString(c);
      if (c == 93 && peekChar() == 93) {
        c = getChar();
        addToString(c);
        if (peekChar() == 62) {
          c = getChar();
          addToString(c);
          return true;
        } 
        continue;
      } 
      c = getChar();
    } 
    this.stringBufferTop = 0;
    this.string = null;
    this.parser.addError("msg.XML.bad.form");
    return false;
  }
  
  private boolean readEntity() throws IOException {
    int declTags = 1;
    for (int c = getChar(); c != -1; c = getChar()) {
      addToString(c);
      switch (c) {
        case 60:
          declTags++;
          break;
        case 62:
          declTags--;
          if (declTags == 0)
            return true; 
          break;
      } 
    } 
    this.stringBufferTop = 0;
    this.string = null;
    this.parser.addError("msg.XML.bad.form");
    return false;
  }
  
  private boolean readPI() throws IOException {
    for (int c = getChar(); c != -1; c = getChar()) {
      addToString(c);
      if (c == 63 && peekChar() == 62) {
        c = getChar();
        addToString(c);
        return true;
      } 
    } 
    this.stringBufferTop = 0;
    this.string = null;
    this.parser.addError("msg.XML.bad.form");
    return false;
  }
  
  private String getStringFromBuffer() {
    this.tokenEnd = this.cursor;
    return new String(this.stringBuffer, 0, this.stringBufferTop);
  }
  
  private void addToString(int c) {
    int N = this.stringBufferTop;
    if (N == this.stringBuffer.length) {
      char[] tmp = new char[this.stringBuffer.length * 2];
      System.arraycopy(this.stringBuffer, 0, tmp, 0, N);
      this.stringBuffer = tmp;
    } 
    this.stringBuffer[N] = (char)c;
    this.stringBufferTop = N + 1;
  }
  
  private boolean canUngetChar() {
    return (this.ungetCursor == 0 || this.ungetBuffer[this.ungetCursor - 1] != 10);
  }
  
  private void ungetChar(int c) {
    if (this.ungetCursor != 0 && this.ungetBuffer[this.ungetCursor - 1] == 10)
      Kit.codeBug(); 
    this.ungetBuffer[this.ungetCursor++] = c;
    this.cursor--;
  }
  
  private boolean matchChar(int test) throws IOException {
    int c = getCharIgnoreLineEnd();
    if (c == test) {
      this.tokenEnd = this.cursor;
      return true;
    } 
    ungetCharIgnoreLineEnd(c);
    return false;
  }
  
  private int peekChar() throws IOException {
    int c = getChar();
    ungetChar(c);
    return c;
  }
  
  private int getChar() throws IOException {
    return getChar(true);
  }
  
  private int getChar(boolean skipFormattingChars) throws IOException {
    int c;
    if (this.ungetCursor != 0) {
      this.cursor++;
      return this.ungetBuffer[--this.ungetCursor];
    } 
    while (true) {
      if (this.sourceString != null) {
        if (this.sourceCursor == this.sourceEnd) {
          this.hitEOF = true;
          return -1;
        } 
        this.cursor++;
        c = this.sourceString.charAt(this.sourceCursor++);
      } else {
        if (this.sourceCursor == this.sourceEnd && !fillSourceBuffer()) {
          this.hitEOF = true;
          return -1;
        } 
        this.cursor++;
        c = this.sourceBuffer[this.sourceCursor++];
      } 
      if (this.lineEndChar >= 0) {
        if (this.lineEndChar == 13 && c == 10) {
          this.lineEndChar = 10;
          continue;
        } 
        this.lineEndChar = -1;
        this.lineStart = this.sourceCursor - 1;
        this.lineno++;
      } 
      if (c <= 127) {
        if (c == 10 || c == 13) {
          this.lineEndChar = c;
          c = 10;
        } 
        break;
      } 
      if (c == 65279)
        return c; 
      if (skipFormattingChars && isJSFormatChar(c))
        continue; 
      if (ScriptRuntime.isJSLineTerminator(c)) {
        this.lineEndChar = c;
        c = 10;
      } 
      break;
    } 
    return c;
  }
  
  private int getCharIgnoreLineEnd() throws IOException {
    int c;
    if (this.ungetCursor != 0) {
      this.cursor++;
      return this.ungetBuffer[--this.ungetCursor];
    } 
    while (true) {
      if (this.sourceString != null) {
        if (this.sourceCursor == this.sourceEnd) {
          this.hitEOF = true;
          return -1;
        } 
        this.cursor++;
        c = this.sourceString.charAt(this.sourceCursor++);
      } else {
        if (this.sourceCursor == this.sourceEnd && !fillSourceBuffer()) {
          this.hitEOF = true;
          return -1;
        } 
        this.cursor++;
        c = this.sourceBuffer[this.sourceCursor++];
      } 
      if (c <= 127) {
        if (c == 10 || c == 13) {
          this.lineEndChar = c;
          c = 10;
        } 
        break;
      } 
      if (c == 65279)
        return c; 
      if (isJSFormatChar(c))
        continue; 
      if (ScriptRuntime.isJSLineTerminator(c)) {
        this.lineEndChar = c;
        c = 10;
      } 
      break;
    } 
    return c;
  }
  
  private void ungetCharIgnoreLineEnd(int c) {
    this.ungetBuffer[this.ungetCursor++] = c;
    this.cursor--;
  }
  
  private void skipLine() throws IOException {
    int c;
    while ((c = getChar()) != -1 && c != 10);
    ungetChar(c);
    this.tokenEnd = this.cursor;
  }
  
  final int getOffset() {
    int n = this.sourceCursor - this.lineStart;
    if (this.lineEndChar >= 0)
      n--; 
    return n;
  }
  
  private final int charAt(int index) {
    if (index < 0)
      return -1; 
    if (this.sourceString != null) {
      if (index >= this.sourceEnd)
        return -1; 
      return this.sourceString.charAt(index);
    } 
    if (index >= this.sourceEnd) {
      int oldSourceCursor = this.sourceCursor;
      try {
        if (!fillSourceBuffer())
          return -1; 
      } catch (IOException ioe) {
        return -1;
      } 
      index -= oldSourceCursor - this.sourceCursor;
    } 
    return this.sourceBuffer[index];
  }
  
  private final String substring(int beginIndex, int endIndex) {
    if (this.sourceString != null)
      return this.sourceString.substring(beginIndex, endIndex); 
    int count = endIndex - beginIndex;
    return new String(this.sourceBuffer, beginIndex, count);
  }
  
  final String getLine() {
    int lineEnd = this.sourceCursor;
    if (this.lineEndChar >= 0) {
      lineEnd--;
      if (this.lineEndChar == 10 && charAt(lineEnd - 1) == 13)
        lineEnd--; 
    } else {
      int lineLength = lineEnd - this.lineStart;
      for (;; lineLength++) {
        int c = charAt(this.lineStart + lineLength);
        if (c == -1 || ScriptRuntime.isJSLineTerminator(c))
          break; 
      } 
      lineEnd = this.lineStart + lineLength;
    } 
    return substring(this.lineStart, lineEnd);
  }
  
  final String getLine(int position, int[] linep) {
    assert position >= 0 && position <= this.cursor;
    assert linep.length == 2;
    int delta = this.cursor + this.ungetCursor - position;
    int cur = this.sourceCursor;
    if (delta > cur)
      return null; 
    int end = 0, lines = 0;
    for (; delta > 0; delta--, cur--) {
      assert cur > 0;
      int c = charAt(cur - 1);
      if (ScriptRuntime.isJSLineTerminator(c)) {
        if (c == 10 && charAt(cur - 2) == 13) {
          delta--;
          cur--;
        } 
        lines++;
        end = cur - 1;
      } 
    } 
    int start = 0, offset = 0;
    for (; cur > 0; cur--, offset++) {
      int c = charAt(cur - 1);
      if (ScriptRuntime.isJSLineTerminator(c)) {
        start = cur;
        break;
      } 
    } 
    linep[0] = this.lineno - lines + ((this.lineEndChar >= 0) ? 1 : 0);
    linep[1] = offset;
    if (lines == 0)
      return getLine(); 
    return substring(start, end);
  }
  
  private boolean fillSourceBuffer() throws IOException {
    if (this.sourceString != null)
      Kit.codeBug(); 
    if (this.sourceEnd == this.sourceBuffer.length)
      if (this.lineStart != 0 && !isMarkingComment()) {
        System.arraycopy(this.sourceBuffer, this.lineStart, this.sourceBuffer, 0, this.sourceEnd - this.lineStart);
        this.sourceEnd -= this.lineStart;
        this.sourceCursor -= this.lineStart;
        this.lineStart = 0;
      } else {
        char[] tmp = new char[this.sourceBuffer.length * 2];
        System.arraycopy(this.sourceBuffer, 0, tmp, 0, this.sourceEnd);
        this.sourceBuffer = tmp;
      }  
    int n = this.sourceReader.read(this.sourceBuffer, this.sourceEnd, this.sourceBuffer.length - this.sourceEnd);
    if (n < 0)
      return false; 
    this.sourceEnd += n;
    return true;
  }
  
  public int getCursor() {
    return this.cursor;
  }
  
  public int getTokenBeg() {
    return this.tokenBeg;
  }
  
  public int getTokenEnd() {
    return this.tokenEnd;
  }
  
  public int getTokenLength() {
    return this.tokenEnd - this.tokenBeg;
  }
  
  public Token.CommentType getCommentType() {
    return this.commentType;
  }
  
  private void markCommentStart() {
    markCommentStart("");
  }
  
  private void markCommentStart(String prefix) {
    if (this.parser.compilerEnv.isRecordingComments() && this.sourceReader != null) {
      this.commentPrefix = prefix;
      this.commentCursor = this.sourceCursor - 1;
    } 
  }
  
  private boolean isMarkingComment() {
    return (this.commentCursor != -1);
  }
  
  final String getAndResetCurrentComment() {
    if (this.sourceString != null) {
      if (isMarkingComment())
        Kit.codeBug(); 
      return this.sourceString.substring(this.tokenBeg, this.tokenEnd);
    } 
    if (!isMarkingComment())
      Kit.codeBug(); 
    StringBuilder comment = new StringBuilder(this.commentPrefix);
    comment.append(this.sourceBuffer, this.commentCursor, getTokenLength() - this.commentPrefix.length());
    this.commentCursor = -1;
    return comment.toString();
  }
  
  private String convertLastCharToHex(String str) {
    int lastIndex = str.length() - 1;
    StringBuffer buf = new StringBuffer(str.substring(0, lastIndex));
    buf.append("\\u");
    String hexCode = Integer.toHexString(str.charAt(lastIndex));
    for (int i = 0; i < 4 - hexCode.length(); i++)
      buf.append('0'); 
    buf.append(hexCode);
    return buf.toString();
  }
}
