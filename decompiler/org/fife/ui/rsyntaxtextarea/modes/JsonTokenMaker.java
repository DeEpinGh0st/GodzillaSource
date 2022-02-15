package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexCTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;


































































public class JsonTokenMaker
  extends AbstractJFlexCTokenMaker
{
  public static final int YYEOF = -1;
  private static final int ZZ_BUFFERSIZE = 16384;
  public static final int EOL_COMMENT = 1;
  public static final int YYINITIAL = 0;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\001\001\b\001\000\001\001\023\000\001\001\001\003\001\t\001\003\001\002\001\003\005\003\001\021\001\006\001\007\001\017\001\016\n\004\001\032\001\003\001\000\001\003\001\000\002\003\004\005\001\020\001\005\024\002\001\033\001\013\001\033\001\000\001\003\001\000\001\026\001\r\002\005\001\024\001\025\001\002\001\034\001\036\002\002\001\027\001\002\001\f\001\002\001\035\001\002\001\023\001\030\001\022\001\n\001\002\001\037\003\002\001\031\001\000\001\031\001\003ﾁ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\001\001\b\001\000\001\001\023\000\001\001\001\003\001\t\001\003\001\002\001\003\005\003\001\021\001\006\001\007\001\017\001\016\n\004\001\032\001\003\001\000\001\003\001\000\002\003\004\005\001\020\001\005\024\002\001\033\001\013\001\033\001\000\001\003\001\000\001\026\001\r\002\005\001\024\001\025\001\002\001\034\001\036\002\002\001\027\001\002\001\f\001\002\001\035\001\002\001\023\001\030\001\022\001\n\001\002\001\037\003\002\001\031\001\000\001\031\001\003ﾁ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();


  
  private static final String ZZ_ACTION_PACKED_0 = "\002\000\001\001\001\002\001\003\002\001\001\004\004\001\001\005\001\006\001\007\003\006\002\000\001\004\001\b\001\004\001\001\001\t\002\001\004\000\002\n\001\000\001\013\001\004\001\000\001\f\001\004\003\001\004\000\001\004\001\r\001\016\002\000\001\017\001\004\002\000\001\004";



  
  private static int[] zzUnpackAction() {
    int[] result = new int[56];
    int offset = 0;
    offset = zzUnpackAction("\002\000\001\001\001\002\001\003\002\001\001\004\004\001\001\005\001\006\001\007\003\006\002\000\001\004\001\b\001\004\001\001\001\t\002\001\004\000\002\n\001\000\001\013\001\004\001\000\001\f\001\004\003\001\004\000\001\004\001\r\001\016\002\000\001\017\001\004\002\000\001\004", offset, result);
    return result;
  }
  
  private static int zzUnpackAction(String packed, int offset, int[] result) {
    int i = 0;
    int j = offset;
    int l = packed.length();
    label10: while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++); while (true)
      { result[j++] = value; if (--count <= 0)
          continue label10;  } 
    }  return j;
  }




  
  private static final int[] ZZ_ROWMAP = zzUnpackRowMap();



  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000 \000@\000`\000\000 \000À\000à\000Ā\000Ġ\000ŀ\000Š\000 \000ƀ\000 \000Ơ\000ǀ\000Ǡ\000Ȁ\000Ƞ\000ɀ\000ɠ\000ʀ\000ʠ\000 \000ˀ\000ˠ\000̀\000̠\000̀\000͠\000΀\000Π\000Π\000 \000π\000ɠ\000 \000Ϡ\000Ѐ\000Р\000р\000Ѡ\000Ҁ\000Ҡ\000Ӏ\000Ӡ\000@\000@\000Ԁ\000Ԡ\000Հ\000ՠ\000ր\000Հ\000֠";




  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[56];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000 \000@\000`\000\000 \000À\000à\000Ā\000Ġ\000ŀ\000Š\000 \000ƀ\000 \000Ơ\000ǀ\000Ǡ\000Ȁ\000Ƞ\000ɀ\000ɠ\000ʀ\000ʠ\000 \000ˀ\000ˠ\000̀\000̠\000̀\000͠\000΀\000Π\000Π\000 \000π\000ɠ\000 \000Ϡ\000Ѐ\000Р\000р\000Ѡ\000Ҁ\000Ҡ\000Ӏ\000Ӡ\000@\000@\000Ԁ\000Ԡ\000Հ\000ՠ\000ր\000Հ\000֠", offset, result);
    return result;
  }
  
  private static int zzUnpackRowMap(String packed, int offset, int[] result) {
    int i = 0;
    int j = offset;
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    } 
    return j;
  }



  
  private static final int[] ZZ_TRANS = zzUnpackTrans();







  
  private static final String ZZ_TRANS_PACKED_0 = "\001\003\001\004\002\003\001\005\001\003\001\006\001\007\001\003\001\b\002\003\001\t\001\003\001\n\003\003\001\013\002\003\001\f\003\003\001\r\001\006\001\r\004\003\b\016\001\017\f\016\001\020\006\016\001\021\002\016\001\022\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\n\003\003\000\004\003\001\000\001\004\"\000\001\005\n\000\001\023\001\024\003\000\001\024/\000\001\005\033\000\b\b\001\025\001\026\001\b\001\027\024\b\001\003\001\000\004\003\001\000\002\003\001\000\001\030\003\003\001\000\n\003\003\000\004\003\016\000\001\031\021\000\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\004\003\001\032\005\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\007\003\001\033\002\003\003\000\004\003\b\016\001\000\f\016\001\000\006\016\001\000\002\016\023\000\001\034\013\000\001\035\023\000\001\036,\000\001\037\004\000\001 \037\000\001!\002\000\001\"\t\000\001\"\016\000\t\025\001#\001\025\001$\024\025\001\000\001%\030\000\001&\005\000\b\025\001\000\001\b\001'\004\b\003\025\002\b\001\025\001\b\n\025\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\b\003\001(\001\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\001)\003\003\001\000\n\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\b\003\001*\001\003\003\000\004\003\035\000\001+\031\000\001,\032\000\001-,\000\001.\004\000\001 \013\000\001\024\003\000\001\024\017\000\001!\033\000\b\025\001\000\033\025\002/\003\025\001#\001\025\001$\001\025\001/\002\025\001/\003\025\003/\t\025\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\b\003\0010\001\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\005\003\0011\004\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\t\003\001)\003\000\004\003\032\000\0012\031\000\001+(\000\0013\021\000\0014\020\000\004\025\0025\003\025\001#\001\025\001$\001\025\0015\002\025\0015\003\025\0035\t\025\016\000\0016)\000\001+\001\000\0012\007\000\0014\0017\0024\0027\002\000\0014\001\000\0034\0017\0014\0017\0074\001\000\0027\0044\004\025\0028\003\025\001#\001\025\001$\001\025\0018\002\025\0018\003\025\0038\t\025\016\000\0014\021\000\004\025\002\b\003\025\001#\001\025\001$\001\025\001\b\002\025\001\b\003\025\003\b\t\025";







  
  private static final int ZZ_UNKNOWN_ERROR = 0;







  
  private static final int ZZ_NO_MATCH = 1;






  
  private static final int ZZ_PUSHBACK_2BIG = 2;







  
  private static int[] zzUnpackTrans() {
    int[] result = new int[1472];
    int offset = 0;
    offset = zzUnpackTrans("\001\003\001\004\002\003\001\005\001\003\001\006\001\007\001\003\001\b\002\003\001\t\001\003\001\n\003\003\001\013\002\003\001\f\003\003\001\r\001\006\001\r\004\003\b\016\001\017\f\016\001\020\006\016\001\021\002\016\001\022\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\n\003\003\000\004\003\001\000\001\004\"\000\001\005\n\000\001\023\001\024\003\000\001\024/\000\001\005\033\000\b\b\001\025\001\026\001\b\001\027\024\b\001\003\001\000\004\003\001\000\002\003\001\000\001\030\003\003\001\000\n\003\003\000\004\003\016\000\001\031\021\000\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\004\003\001\032\005\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\007\003\001\033\002\003\003\000\004\003\b\016\001\000\f\016\001\000\006\016\001\000\002\016\023\000\001\034\013\000\001\035\023\000\001\036,\000\001\037\004\000\001 \037\000\001!\002\000\001\"\t\000\001\"\016\000\t\025\001#\001\025\001$\024\025\001\000\001%\030\000\001&\005\000\b\025\001\000\001\b\001'\004\b\003\025\002\b\001\025\001\b\n\025\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\b\003\001(\001\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\001)\003\003\001\000\n\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\b\003\001*\001\003\003\000\004\003\035\000\001+\031\000\001,\032\000\001-,\000\001.\004\000\001 \013\000\001\024\003\000\001\024\017\000\001!\033\000\b\025\001\000\033\025\002/\003\025\001#\001\025\001$\001\025\001/\002\025\001/\003\025\003/\t\025\001\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\b\003\0010\001\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\005\003\0011\004\003\003\000\005\003\001\000\004\003\001\000\002\003\001\000\004\003\001\000\t\003\001)\003\000\004\003\032\000\0012\031\000\001+(\000\0013\021\000\0014\020\000\004\025\0025\003\025\001#\001\025\001$\001\025\0015\002\025\0015\003\025\0035\t\025\016\000\0016)\000\001+\001\000\0012\007\000\0014\0017\0024\0027\002\000\0014\001\000\0034\0017\0014\0017\0074\001\000\0027\0044\004\025\0028\003\025\001#\001\025\001$\001\025\0018\002\025\0018\003\025\0038\t\025\016\000\0014\021\000\004\025\002\b\003\025\001#\001\025\001$\001\025\001\b\002\025\001\b\003\025\003\b\t\025", offset, result);
    return result;
  }
  
  private static int zzUnpackTrans(String packed, int offset, int[] result) {
    int i = 0;
    int j = offset;
    int l = packed.length();
    label10: while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--; while (true)
      { result[j++] = value; if (--count <= 0)
          continue label10;  } 
    }  return j;
  }







  
  private static final String[] ZZ_ERROR_MSG = new String[] { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };







  
  private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
  
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\002\000\003\001\001\t\006\001\001\t\001\001\001\t\003\001\002\000\001\001\001\003\002\001\001\t\002\001\004\000\002\001\001\000\001\t\001\001\001\000\001\r\004\001\004\000\003\001\002\000\002\001\002\000\001\001";
  
  private Reader zzReader;
  
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[56];
    int offset = 0;
    offset = zzUnpackAttribute("\002\000\003\001\001\t\006\001\001\t\001\001\001\t\003\001\002\000\001\001\001\003\002\001\001\t\002\001\004\000\002\001\001\000\001\t\001\001\001\000\001\r\004\001\004\000\003\001\002\000\002\001\002\000\001\001", offset, result);
    return result;
  }
  
  private static int zzUnpackAttribute(String packed, int offset, int[] result) {
    int i = 0;
    int j = offset;
    int l = packed.length();
    label10: while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++); while (true)
      { result[j++] = value; if (--count <= 0)
          continue label10;  } 
    }  return j;
  }







  
  private int zzLexicalState = 0;


  
  private char[] zzBuffer = new char[16384];



  
  private int zzMarkedPos;



  
  private int zzPushbackPos;


  
  private int zzCurrentPos;


  
  private int zzStartRead;


  
  private int zzEndRead;


  
  private boolean zzAtEOF;


  
  private boolean highlightEolComments;



  
  public JsonTokenMaker() {}



  
  private void addHyperlinkToken(int start, int end, int tokenType) {
    int so = start + this.offsetShift;
    addToken(this.zzBuffer, start, end, tokenType, so, true);
  }






  
  private void addToken(int tokenType) {
    addToken(this.zzStartRead, this.zzMarkedPos - 1, tokenType);
  }






  
  private void addToken(int start, int end, int tokenType) {
    int so = start + this.offsetShift;
    addToken(this.zzBuffer, start, end, tokenType, so, false);
  }














  
  public void addToken(char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink) {
    super.addToken(array, start, end, tokenType, startOffset, hyperlink);
    this.zzStartRead = this.zzMarkedPos;
  }







  
  public boolean getCurlyBracesDenoteCodeBlocks() {
    return true;
  }


  
  public boolean getMarkOccurrencesOfTokenType(int type) {
    return false;
  }


  
  public boolean getShouldIndentNextLineAfter(Token t) {
    if (t != null && t.length() == 1) {
      char ch = t.charAt(0);
      return (ch == '{' || ch == '[');
    } 
    return false;
  }














  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;

    
    int state = 0;
    this.start = text.offset;
    
    this.s = text;
    try {
      yyreset(this.zzReader);
      yybegin(state);
      return yylex();
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return (Token)new TokenImpl();
    } 
  }


  
  protected void setHighlightEolComments(boolean highlightEolComments) {
    this.highlightEolComments = highlightEolComments;
  }







  
  private boolean zzRefill() {
    return (this.zzCurrentPos >= this.s.offset + this.s.count);
  }












  
  public final void yyreset(Reader reader) {
    this.zzBuffer = this.s.array;






    
    this.zzStartRead = this.s.offset;
    this.zzEndRead = this.zzStartRead + this.s.count - 1;
    this.zzCurrentPos = this.zzMarkedPos = this.s.offset;
    this.zzLexicalState = 0;
    this.zzReader = reader;
    this.zzAtEOF = false;
  }









  
  public JsonTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public JsonTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 124) {
      int count = packed.charAt(i++);
      char value = packed.charAt(i++); while (true)
      { map[j++] = value; if (--count <= 0)
          continue label10;  } 
    }  return map;
  }




  
  public final void yyclose() throws IOException {
    this.zzAtEOF = true;
    this.zzEndRead = this.zzStartRead;
    
    if (this.zzReader != null) {
      this.zzReader.close();
    }
  }



  
  public final int yystate() {
    return this.zzLexicalState;
  }






  
  public final void yybegin(int newState) {
    this.zzLexicalState = newState;
  }




  
  public final String yytext() {
    return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
  }












  
  public final char yycharat(int pos) {
    return this.zzBuffer[this.zzStartRead + pos];
  }




  
  public final int yylength() {
    return this.zzMarkedPos - this.zzStartRead;
  }















  
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[0];
    } 
    
    throw new Error(message);
  }









  
  public void yypushback(int number) {
    if (number > yylength()) {
      zzScanError(2);
    }
    this.zzMarkedPos -= number;
  }














  
  public Token yylex() throws IOException {
    int zzEndReadL = this.zzEndRead;
    char[] zzBufferL = this.zzBuffer;
    char[] zzCMapL = ZZ_CMAP;
    
    int[] zzTransL = ZZ_TRANS;
    int[] zzRowMapL = ZZ_ROWMAP;
    int[] zzAttrL = ZZ_ATTRIBUTE;
    int zzPushbackPosL = this.zzPushbackPos = -1;

    
    while (true) {
      int zzInput, temp, zzMarkedPosL = this.zzMarkedPos;
      
      int zzAction = -1;
      
      int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
      
      this.zzState = this.zzLexicalState;
      
      boolean zzWasPushback = false;


      
      while (true) {
        if (zzCurrentPosL < zzEndReadL)
        { zzInput = zzBufferL[zzCurrentPosL++]; }
        else { if (this.zzAtEOF) {
            int i = -1;
            
            break;
          } 
          
          this.zzCurrentPos = zzCurrentPosL;
          this.zzMarkedPos = zzMarkedPosL;
          this.zzPushbackPos = zzPushbackPosL;
          boolean eof = zzRefill();
          
          zzCurrentPosL = this.zzCurrentPos;
          zzMarkedPosL = this.zzMarkedPos;
          zzBufferL = this.zzBuffer;
          zzEndReadL = this.zzEndRead;
          zzPushbackPosL = this.zzPushbackPos;
          if (eof) {
            int i = -1;
            
            break;
          } 
          zzInput = zzBufferL[zzCurrentPosL++]; }

        
        int zzNext = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];
        if (zzNext == -1)
          break;  this.zzState = zzNext;
        
        int zzAttributes = zzAttrL[this.zzState];
        if ((zzAttributes & 0x2) == 2) {
          zzPushbackPosL = zzCurrentPosL;
        }
        if ((zzAttributes & 0x1) == 1) {
          zzWasPushback = ((zzAttributes & 0x4) == 4);
          zzAction = this.zzState;
          zzMarkedPosL = zzCurrentPosL;
          if ((zzAttributes & 0x8) == 8) {
            break;
          }
        } 
      } 

      
      this.zzMarkedPos = zzMarkedPosL;
      if (zzWasPushback) {
        this.zzMarkedPos = zzPushbackPosL;
      }
      switch ((zzAction < 0) ? zzAction : ZZ_ACTION[zzAction]) {
        case 13:
          addToken(6); continue;
        case 16:
          continue;
        case 1:
          addToken(20); continue;
        case 17:
          continue;
        case 10:
          addToken(11); continue;
        case 18:
          continue;
        case 8:
          addToken(13); continue;
        case 19:
          continue;
        case 12:
          addToken(17); continue;
        case 20:
          continue;
        case 2:
          addToken(21); continue;
        case 21:
          continue;
        case 15:
          temp = this.zzStartRead; addToken(this.start, this.zzStartRead - 1, 1); addHyperlinkToken(temp, this.zzMarkedPos - 1, 1); this.start = this.zzMarkedPos; continue;
        case 22:
          continue;
        case 3:
          addToken(10); continue;
        case 23:
          continue;
        case 14:
          addToken(9); continue;
        case 24:
          continue;
        case 9:
          if (this.highlightEolComments) {
            this.start = this.zzMarkedPos - 2; yybegin(1);
            continue;
          } 
          addToken(20);
          continue;
        case 25:
          continue;
        case 4:
          addToken(37); addNullToken(); return (Token)this.firstToken;
        case 26:
          continue;
        case 7:
          addToken(this.start, this.zzStartRead - 1, 1); addNullToken(); return (Token)this.firstToken;
        case 27:
          continue;
        case 11:
          addToken(37);
          continue;
        
        case 28:
        case 6:
        case 29:
          continue;
        case 5:
          addToken(22); continue;
        case 30:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 1:
            addToken(this.start, this.zzStartRead - 1, 1); addNullToken(); return (Token)this.firstToken;
          case 57:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 58:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
