package org.mozilla.javascript;


































































public class Decompiler
{
  public static final int ONLY_BODY_FLAG = 1;
  public static final int TO_SOURCE_FLAG = 2;
  public static final int INITIAL_INDENT_PROP = 1;
  public static final int INDENT_GAP_PROP = 2;
  public static final int CASE_GAP_PROP = 3;
  private static final int FUNCTION_END = 164;
  
  String getEncodedSource() {
    return sourceToString(0);
  }

  
  int getCurrentOffset() {
    return this.sourceTop;
  }

  
  int markFunctionStart(int functionType) {
    int savedOffset = getCurrentOffset();
    addToken(109);
    append((char)functionType);
    return savedOffset;
  }

  
  int markFunctionEnd(int functionStart) {
    int offset = getCurrentOffset();
    append('¤');
    return offset;
  }

  
  void addToken(int token) {
    if (0 > token || token > 163) {
      throw new IllegalArgumentException();
    }
    append((char)token);
  }

  
  void addEOL(int token) {
    if (0 > token || token > 163) {
      throw new IllegalArgumentException();
    }
    append((char)token);
    append('\001');
  }

  
  void addName(String str) {
    addToken(39);
    appendString(str);
  }

  
  void addString(String str) {
    addToken(41);
    appendString(str);
  }

  
  void addRegexp(String regexp, String flags) {
    addToken(48);
    appendString('/' + regexp + '/' + flags);
  }

  
  void addNumber(double n) {
    addToken(40);

















    
    long lbits = (long)n;
    if (lbits != n) {

      
      lbits = Double.doubleToLongBits(n);
      append('D');
      append((char)(int)(lbits >> 48L));
      append((char)(int)(lbits >> 32L));
      append((char)(int)(lbits >> 16L));
      append((char)(int)lbits);
    
    }
    else {
      
      if (lbits < 0L) Kit.codeBug();


      
      if (lbits <= 65535L) {
        append('S');
        append((char)(int)lbits);
      } else {
        
        append('J');
        append((char)(int)(lbits >> 48L));
        append((char)(int)(lbits >> 32L));
        append((char)(int)(lbits >> 16L));
        append((char)(int)lbits);
      } 
    } 
  }

  
  private void appendString(String str) {
    int L = str.length();
    int lengthEncodingSize = 1;
    if (L >= 32768) {
      lengthEncodingSize = 2;
    }
    int nextTop = this.sourceTop + lengthEncodingSize + L;
    if (nextTop > this.sourceBuffer.length) {
      increaseSourceCapacity(nextTop);
    }
    if (L >= 32768) {

      
      this.sourceBuffer[this.sourceTop] = (char)(0x8000 | L >>> 16);
      this.sourceTop++;
    } 
    this.sourceBuffer[this.sourceTop] = (char)L;
    this.sourceTop++;
    str.getChars(0, L, this.sourceBuffer, this.sourceTop);
    this.sourceTop = nextTop;
  }

  
  private void append(char c) {
    if (this.sourceTop == this.sourceBuffer.length) {
      increaseSourceCapacity(this.sourceTop + 1);
    }
    this.sourceBuffer[this.sourceTop] = c;
    this.sourceTop++;
  }


  
  private void increaseSourceCapacity(int minimalCapacity) {
    if (minimalCapacity <= this.sourceBuffer.length) Kit.codeBug(); 
    int newCapacity = this.sourceBuffer.length * 2;
    if (newCapacity < minimalCapacity) {
      newCapacity = minimalCapacity;
    }
    char[] tmp = new char[newCapacity];
    System.arraycopy(this.sourceBuffer, 0, tmp, 0, this.sourceTop);
    this.sourceBuffer = tmp;
  }

  
  private String sourceToString(int offset) {
    if (offset < 0 || this.sourceTop < offset) Kit.codeBug(); 
    return new String(this.sourceBuffer, offset, this.sourceTop - offset);
  }


















  
  public static String decompile(String source, int flags, UintMap properties) {
    int topFunctionType, length = source.length();
    if (length == 0) return "";
    
    int indent = properties.getInt(1, 0);
    if (indent < 0) throw new IllegalArgumentException(); 
    int indentGap = properties.getInt(2, 4);
    if (indentGap < 0) throw new IllegalArgumentException(); 
    int caseGap = properties.getInt(3, 2);
    if (caseGap < 0) throw new IllegalArgumentException();
    
    StringBuilder result = new StringBuilder();
    boolean justFunctionBody = (0 != (flags & 0x1));
    boolean toSource = (0 != (flags & 0x2));



























    
    int braceNesting = 0;
    boolean afterFirstEOL = false;
    int i = 0;
    
    if (source.charAt(i) == '') {
      i++;
      topFunctionType = -1;
    } else {
      topFunctionType = source.charAt(i + 1);
    } 
    
    if (!toSource) {
      
      result.append('\n');
      for (int j = 0; j < indent; j++) {
        result.append(' ');
      }
    } else if (topFunctionType == 2) {
      result.append('(');
    } 

    
    while (i < length) {
      boolean newLine; switch (source.charAt(i)) {
        case '':
        case '':
          result.append((source.charAt(i) == '') ? "get " : "set ");
          i++;
          i = printSourceString(source, i + 1, false, result);
          
          i++;
          break;
        
        case '\'':
        case '0':
          i = printSourceString(source, i + 1, false, result);
          continue;
        
        case ')':
          i = printSourceString(source, i + 1, true, result);
          continue;
        
        case '(':
          i = printSourceNumber(source, i + 1, result);
          continue;
        
        case '-':
          result.append("true");
          break;
        
        case ',':
          result.append("false");
          break;
        
        case '*':
          result.append("null");
          break;
        
        case '+':
          result.append("this");
          break;
        
        case 'm':
          i++;
          result.append("function ");
          break;

        
        case '¤':
          break;
        
        case 'Y':
          result.append(", ");
          break;
        
        case 'U':
          braceNesting++;
          if (1 == getNext(source, length, i))
            indent += indentGap; 
          result.append('{');
          break;
        
        case 'V':
          braceNesting--;



          
          if (justFunctionBody && braceNesting == 0) {
            break;
          }
          result.append('}');
          switch (getNext(source, length, i)) {
            case 1:
            case 164:
              indent -= indentGap;
              break;
            case 113:
            case 117:
              indent -= indentGap;
              result.append(' ');
              break;
          } 
          
          break;
        case 'W':
          result.append('(');
          break;
        
        case 'X':
          result.append(')');
          if (85 == getNext(source, length, i)) {
            result.append(' ');
          }
          break;
        case 'S':
          result.append('[');
          break;
        
        case 'T':
          result.append(']');
          break;
        
        case '\001':
          if (toSource)
            break;  newLine = true;
          if (!afterFirstEOL) {
            afterFirstEOL = true;
            if (justFunctionBody) {


              
              result.setLength(0);
              indent -= indentGap;
              newLine = false;
            } 
          } 
          if (newLine) {
            result.append('\n');
          }




          
          if (i + 1 < length) {
            int less = 0;
            int nextToken = source.charAt(i + 1);
            if (nextToken == 115 || nextToken == 116) {

              
              less = indentGap - caseGap;
            } else if (nextToken == 86) {
              less = indentGap;



            
            }
            else if (nextToken == 39) {
              int afterName = getSourceStringEnd(source, i + 2);
              if (source.charAt(afterName) == 'g') {
                less = indentGap;
              }
            } 
            for (; less < indent; less++) {
              result.append(' ');
            }
          } 
          break;
        case 'l':
          result.append('.');
          break;
        
        case '\036':
          result.append("new ");
          break;
        
        case '\037':
          result.append("delete ");
          break;
        
        case 'p':
          result.append("if ");
          break;
        
        case 'q':
          result.append("else ");
          break;
        
        case 'w':
          result.append("for ");
          break;
        
        case '4':
          result.append(" in ");
          break;
        
        case '{':
          result.append("with ");
          break;
        
        case 'u':
          result.append("while ");
          break;
        
        case 'v':
          result.append("do ");
          break;
        
        case 'Q':
          result.append("try ");
          break;
        
        case '|':
          result.append("catch ");
          break;
        
        case '}':
          result.append("finally ");
          break;
        
        case '2':
          result.append("throw ");
          break;
        
        case 'r':
          result.append("switch ");
          break;
        
        case 'x':
          result.append("break");
          if (39 == getNext(source, length, i)) {
            result.append(' ');
          }
          break;
        case 'y':
          result.append("continue");
          if (39 == getNext(source, length, i)) {
            result.append(' ');
          }
          break;
        case 's':
          result.append("case ");
          break;
        
        case 't':
          result.append("default");
          break;
        
        case '\004':
          result.append("return");
          if (82 != getNext(source, length, i)) {
            result.append(' ');
          }
          break;
        case 'z':
          result.append("var ");
          break;
        
        case '':
          result.append("let ");
          break;
        
        case 'R':
          result.append(';');
          if (1 != getNext(source, length, i))
          {
            result.append(' ');
          }
          break;
        
        case 'Z':
          result.append(" = ");
          break;
        
        case 'a':
          result.append(" += ");
          break;
        
        case 'b':
          result.append(" -= ");
          break;
        
        case 'c':
          result.append(" *= ");
          break;
        
        case 'd':
          result.append(" /= ");
          break;
        
        case 'e':
          result.append(" %= ");
          break;
        
        case '[':
          result.append(" |= ");
          break;
        
        case '\\':
          result.append(" ^= ");
          break;
        
        case ']':
          result.append(" &= ");
          break;
        
        case '^':
          result.append(" <<= ");
          break;
        
        case '_':
          result.append(" >>= ");
          break;
        
        case '`':
          result.append(" >>>= ");
          break;
        
        case 'f':
          result.append(" ? ");
          break;





        
        case 'B':
          result.append(": ");
          break;
        
        case 'g':
          if (1 == getNext(source, length, i)) {
            
            result.append(':');
            break;
          } 
          result.append(" : ");
          break;
        
        case 'h':
          result.append(" || ");
          break;
        
        case 'i':
          result.append(" && ");
          break;
        
        case '\t':
          result.append(" | ");
          break;
        
        case '\n':
          result.append(" ^ ");
          break;
        
        case '\013':
          result.append(" & ");
          break;
        
        case '.':
          result.append(" === ");
          break;
        
        case '/':
          result.append(" !== ");
          break;
        
        case '\f':
          result.append(" == ");
          break;
        
        case '\r':
          result.append(" != ");
          break;
        
        case '\017':
          result.append(" <= ");
          break;
        
        case '\016':
          result.append(" < ");
          break;
        
        case '\021':
          result.append(" >= ");
          break;
        
        case '\020':
          result.append(" > ");
          break;
        
        case '5':
          result.append(" instanceof ");
          break;
        
        case '\022':
          result.append(" << ");
          break;
        
        case '\023':
          result.append(" >> ");
          break;
        
        case '\024':
          result.append(" >>> ");
          break;
        
        case ' ':
          result.append("typeof ");
          break;
        
        case '~':
          result.append("void ");
          break;
        
        case '':
          result.append("const ");
          break;
        
        case 'H':
          result.append("yield ");
          break;
        
        case '\032':
          result.append('!');
          break;
        
        case '\033':
          result.append('~');
          break;
        
        case '\034':
          result.append('+');
          break;
        
        case '\035':
          result.append('-');
          break;
        
        case 'j':
          result.append("++");
          break;
        
        case 'k':
          result.append("--");
          break;
        
        case '\025':
          result.append(" + ");
          break;
        
        case '\026':
          result.append(" - ");
          break;
        
        case '\027':
          result.append(" * ");
          break;
        
        case '\030':
          result.append(" / ");
          break;
        
        case '\031':
          result.append(" % ");
          break;
        
        case '':
          result.append("::");
          break;
        
        case '':
          result.append("..");
          break;
        
        case '':
          result.append(".(");
          break;
        
        case '':
          result.append('@');
          break;
        
        case ' ':
          result.append("debugger;\n");
          break;

        
        default:
          throw new RuntimeException("Token: " + Token.name(source.charAt(i)));
      } 
      
      i++;
    } 
    
    if (!toSource) {
      
      if (!justFunctionBody) {
        result.append('\n');
      }
    } else if (topFunctionType == 2) {
      result.append(')');
    } 

    
    return result.toString();
  }

  
  private static int getNext(String source, int length, int i) {
    return (i + 1 < length) ? source.charAt(i + 1) : 0;
  }

  
  private static int getSourceStringEnd(String source, int offset) {
    return printSourceString(source, offset, false, null);
  }



  
  private static int printSourceString(String source, int offset, boolean asQuotedString, StringBuilder sb) {
    int length = source.charAt(offset);
    offset++;
    if ((0x8000 & length) != 0) {
      length = (0x7FFF & length) << 16 | source.charAt(offset);
      offset++;
    } 
    if (sb != null) {
      String str = source.substring(offset, offset + length);
      if (!asQuotedString) {
        sb.append(str);
      } else {
        sb.append('"');
        sb.append(ScriptRuntime.escapeString(str));
        sb.append('"');
      } 
    } 
    return offset + length;
  }


  
  private static int printSourceNumber(String source, int offset, StringBuilder sb) {
    double number = 0.0D;
    char type = source.charAt(offset);
    offset++;
    if (type == 'S') {
      if (sb != null) {
        int ival = source.charAt(offset);
        number = ival;
      } 
      offset++;
    } else if (type == 'J' || type == 'D') {
      if (sb != null) {
        
        long lbits = source.charAt(offset) << 48L;
        lbits |= source.charAt(offset + 1) << 32L;
        lbits |= source.charAt(offset + 2) << 16L;
        lbits |= source.charAt(offset + 3);
        if (type == 'J') {
          number = lbits;
        } else {
          number = Double.longBitsToDouble(lbits);
        } 
      } 
      offset += 4;
    } else {
      
      throw new RuntimeException();
    } 
    if (sb != null) {
      sb.append(ScriptRuntime.numberToString(number, 10));
    }
    return offset;
  }
  
  private char[] sourceBuffer = new char[128];
  private int sourceTop;
  private static final boolean printSource = false;
}
