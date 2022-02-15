package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;


































































public class DtdTokenMaker
  extends AbstractJFlexTokenMaker
{
  public static final int YYEOF = -1;
  public static final int INTAG_START = 2;
  public static final int INTAG_ELEMENT = 3;
  public static final int YYINITIAL = 0;
  public static final int INTAG_ATTLIST = 4;
  public static final int COMMENT = 1;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\001\001\002\001\000\001\001\023\000\001\001\001\024\001\003\001!\001\007\001\005\001\005\001\004\005\005\001\025\001\022\001\006\n\007\001\020\001\005\001\023\001\005\001\026\002\005\001\034\001\007\001\037\001 \001\027\003\007\001\035\002\007\001\030\001\031\001\032\001\007\001\"\001$\001#\001\036\001\033\001%\005\007\001\005\001\000\001\005\001\000\001\005\001\000\004\007\001\017\001\f\001\007\001\b\001\r\002\007\001\016\003\007\001\n\002\007\001\013\001\t\002\007\001\021\003\007\003\000\001\005ﾁ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\001\001\002\001\000\001\001\023\000\001\001\001\024\001\003\001!\001\007\001\005\001\005\001\004\005\005\001\025\001\022\001\006\n\007\001\020\001\005\001\023\001\005\001\026\002\005\001\034\001\007\001\037\001 \001\027\003\007\001\035\002\007\001\030\001\031\001\032\001\007\001\"\001$\001#\001\036\001\033\001%\005\007\001\005\001\000\001\005\001\000\001\005\001\000\004\007\001\017\001\f\001\007\001\b\001\r\002\007\001\016\003\007\001\n\002\007\001\013\001\t\002\007\001\021\003\007\003\000\001\005ﾁ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();


  
  private static final String ZZ_ACTION_PACKED_0 = "\005\000\001\001\001\002\001\003\001\004\001\005\004\004\001\003\001\006\002\003\002\007\002\b\002\007\001\t\005\000\002\003\001\b\003\007\005\000\001\n\002\003\003\007\001\013\002\000\001\f\002\003\003\007\002\000\002\003\001\r\002\007\002\003\002\007\001\016\001\017\001\007";



  
  private static int[] zzUnpackAction() {
    int[] result = new int[70];
    int offset = 0;
    offset = zzUnpackAction("\005\000\001\001\001\002\001\003\001\004\001\005\004\004\001\003\001\006\002\003\002\007\002\b\002\007\001\t\005\000\002\003\001\b\003\007\005\000\001\n\002\003\003\007\001\013\002\000\001\f\002\003\003\007\002\000\002\003\001\r\002\007\002\003\002\007\001\016\001\017\001\007", offset, result);
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




  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000&\000L\000r\000\000¾\000ä\000Ċ\000İ\000Ŗ\000ż\000Ƣ\000ǈ\000Ǯ\000Ȕ\000Ŗ\000Ⱥ\000ɠ\000ʆ\000ʬ\000˒\000˸\000̞\000̈́\000ͪ\000ΐ\000ζ\000Ϝ\000Ђ\000Ш\000ю\000Ѵ\000Ŗ\000Қ\000Ӏ\000Ӧ\000Ԍ\000Բ\000՘\000վ\000֤\000Ŗ\000׊\000װ\000ؖ\000ؼ\000٢\000Ŗ\000ڈ\000ڮ\000۔\000ۺ\000ܠ\000݆\000ݬ\000ޒ\000޸\000۔\000ߞ\000ࠄ\000ʬ\000ࠪ\000ࡐ\000ࡶ\000࢜\000ࣂ\000ࣨ\000Ȕ\000Ȕ\000ऎ";





  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[70];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000&\000L\000r\000\000¾\000ä\000Ċ\000İ\000Ŗ\000ż\000Ƣ\000ǈ\000Ǯ\000Ȕ\000Ŗ\000Ⱥ\000ɠ\000ʆ\000ʬ\000˒\000˸\000̞\000̈́\000ͪ\000ΐ\000ζ\000Ϝ\000Ђ\000Ш\000ю\000Ѵ\000Ŗ\000Қ\000Ӏ\000Ӧ\000Ԍ\000Բ\000՘\000վ\000֤\000Ŗ\000׊\000װ\000ؖ\000ؼ\000٢\000Ŗ\000ڈ\000ڮ\000۔\000ۺ\000ܠ\000݆\000ݬ\000ޒ\000޸\000۔\000ߞ\000ࠄ\000ʬ\000ࠪ\000ࡐ\000ࡶ\000࢜\000ࣂ\000ࣨ\000Ȕ\000Ȕ\000ऎ", offset, result);
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








  
  private static final String ZZ_TRANS_PACKED_0 = "\001\006\001\007\021\006\001\b\022\006\002\t\001\n\005\t\001\013\003\t\001\f\004\t\001\r\003\t\001\016\020\t\001\017\001\007\024\017\001\020\001\021\004\017\001\022\t\017\001\023\001\007\024\023\001\020\017\023\001\024\001\007\001\024\001\025\001\026\021\024\001\020\b\024\001\027\001\024\001\030\004\024\001\006\001\000\021\006\001\000\022\006\001\000\001\0078\000\001\031\021\000\002\t\001\000\005\t\001\000\003\t\001\000\004\t\001\000\003\t\001\000\020\t/\000\001\032%\000\001\033\003\000\001\034)\000\001\035)\000\001\036\020\000\001\017\001\000\024\017\001\000\020\017\001\000\024\017\001\000\001\017\001\037\016\017\001\000\024\017\001\000\004\017\001 \n\017\001\023\001\000\024\023\001\000\017\023\001\024\001\000\001\024\002\000\021\024\001\000\017\024\003\025\001!\"\025\004\026\001!!\026\001\024\001\000\001\024\002\000\021\024\001\000\t\024\001\"\006\024\001\000\001\024\002\000\021\024\001\000\006\024\001#\005\024\001$\002\024\025\000\001%\031\000\001&&\000\001')\000\001((\000\001)*\000\001*\017\000\001\017\001\000\024\017\001\000\001+\017\017\001\000\024\017\001\000\004\017\001,\n\017\001\024\001\000\001\024\002\000\021\024\001\000\005\024\001-\n\024\001\000\001\024\002\000\021\024\001\000\002\024\001.\r\024\001\000\001\024\002\000\021\024\001\000\001/\016\024\025\000\0010\032\000\0011+\000\0012$\000\001'(\000\0013\023\000\001\017\001\000\024\017\001\000\002\017\0014\r\017\001\000\024\017\001\000\001\017\0015\r\017\001\024\001\000\001\024\002\000\021\024\001\000\004\024\0016\013\024\001\000\001\024\002\000\021\024\001\000\013\024\0017\004\024\001\000\001\024\002\000\021\024\001\000\r\024\0018\001\024\013\000\001'\004\000\0012\033\000\0019#\000\002:\n3\001:\0013\001:\001\000\002:\001\000\n3\001:\0043\001\017\001\000\024\017\001\000\001;\017\017\001\000\024\017\001\000\006\017\001<\b\017\001\024\001\000\001\024\002\000\021\024\001\000\005\024\001=\n\024\001\000\001\024\002\000\021\024\001\000\001\024\001>\016\024\001\000\001\024\002\000\021\024\001\000\016\024\001?\006\000\0013\037\000\001\017\001\000\024\017\001\000\003\017\001@\f\017\001\000\024\017\001\000\007\017\001A\007\017\001\024\001\000\001\024\002\000\021\024\001\000\006\024\001B\t\024\001\000\001\024\002\000\021\024\001\000\006\024\001C\b\024\001\017\001\000\024\017\001\000\004\017\001D\013\017\001\000\024\017\001\000\004\017\001E\n\017\001\024\001\000\001\024\002\000\021\024\001\000\001F\017\024\001\000\001\024\002\000\021\024\001\000\f\024\001B\003\024\001\000\001\024\002\000\021\024\001\000\t\024\001=\005\024";








  
  private static final int ZZ_UNKNOWN_ERROR = 0;







  
  private static final int ZZ_NO_MATCH = 1;







  
  private static final int ZZ_PUSHBACK_2BIG = 2;








  
  private static int[] zzUnpackTrans() {
    int[] result = new int[2356];
    int offset = 0;
    offset = zzUnpackTrans("\001\006\001\007\021\006\001\b\022\006\002\t\001\n\005\t\001\013\003\t\001\f\004\t\001\r\003\t\001\016\020\t\001\017\001\007\024\017\001\020\001\021\004\017\001\022\t\017\001\023\001\007\024\023\001\020\017\023\001\024\001\007\001\024\001\025\001\026\021\024\001\020\b\024\001\027\001\024\001\030\004\024\001\006\001\000\021\006\001\000\022\006\001\000\001\0078\000\001\031\021\000\002\t\001\000\005\t\001\000\003\t\001\000\004\t\001\000\003\t\001\000\020\t/\000\001\032%\000\001\033\003\000\001\034)\000\001\035)\000\001\036\020\000\001\017\001\000\024\017\001\000\020\017\001\000\024\017\001\000\001\017\001\037\016\017\001\000\024\017\001\000\004\017\001 \n\017\001\023\001\000\024\023\001\000\017\023\001\024\001\000\001\024\002\000\021\024\001\000\017\024\003\025\001!\"\025\004\026\001!!\026\001\024\001\000\001\024\002\000\021\024\001\000\t\024\001\"\006\024\001\000\001\024\002\000\021\024\001\000\006\024\001#\005\024\001$\002\024\025\000\001%\031\000\001&&\000\001')\000\001((\000\001)*\000\001*\017\000\001\017\001\000\024\017\001\000\001+\017\017\001\000\024\017\001\000\004\017\001,\n\017\001\024\001\000\001\024\002\000\021\024\001\000\005\024\001-\n\024\001\000\001\024\002\000\021\024\001\000\002\024\001.\r\024\001\000\001\024\002\000\021\024\001\000\001/\016\024\025\000\0010\032\000\0011+\000\0012$\000\001'(\000\0013\023\000\001\017\001\000\024\017\001\000\002\017\0014\r\017\001\000\024\017\001\000\001\017\0015\r\017\001\024\001\000\001\024\002\000\021\024\001\000\004\024\0016\013\024\001\000\001\024\002\000\021\024\001\000\013\024\0017\004\024\001\000\001\024\002\000\021\024\001\000\r\024\0018\001\024\013\000\001'\004\000\0012\033\000\0019#\000\002:\n3\001:\0013\001:\001\000\002:\001\000\n3\001:\0043\001\017\001\000\024\017\001\000\001;\017\017\001\000\024\017\001\000\006\017\001<\b\017\001\024\001\000\001\024\002\000\021\024\001\000\005\024\001=\n\024\001\000\001\024\002\000\021\024\001\000\001\024\001>\016\024\001\000\001\024\002\000\021\024\001\000\016\024\001?\006\000\0013\037\000\001\017\001\000\024\017\001\000\003\017\001@\f\017\001\000\024\017\001\000\007\017\001A\007\017\001\024\001\000\001\024\002\000\021\024\001\000\006\024\001B\t\024\001\000\001\024\002\000\021\024\001\000\006\024\001C\b\024\001\017\001\000\024\017\001\000\004\017\001D\013\017\001\000\024\017\001\000\004\017\001E\n\017\001\024\001\000\001\024\002\000\021\024\001\000\001F\017\024\001\000\001\024\002\000\021\024\001\000\f\024\001B\003\024\001\000\001\024\002\000\021\024\001\000\t\024\001=\005\024", offset, result);
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
  
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\005\000\004\001\001\t\005\001\001\t\t\001\005\000\002\001\001\t\003\001\005\000\001\t\005\001\001\t\002\000\006\001\002\000\f\001";
  
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[70];
    int offset = 0;
    offset = zzUnpackAttribute("\005\000\004\001\001\t\005\001\001\t\t\001\005\000\002\001\001\t\003\001\005\000\001\t\005\001\001\t\002\000\006\001\002\000\f\001", offset, result);
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




  
  private char[] zzBuffer;




  
  private int zzMarkedPos;




  
  private int zzCurrentPos;



  
  private int zzStartRead;



  
  private int zzEndRead;



  
  private boolean zzAtEOF;



  
  public static final int INTERNAL_INTAG_START = -1;



  
  public static final int INTERNAL_INTAG_ELEMENT = -2;



  
  public static final int INTERNAL_INTAG_ATTLIST = -3;



  
  public static final int INTERNAL_IN_COMMENT = -2048;



  
  private int prevState;




  
  public DtdTokenMaker() {}




  
  private void addEndToken(int tokenType) {
    addToken(this.zzMarkedPos, this.zzMarkedPos, tokenType);
  }







  
  private void addHyperlinkToken(int start, int end, int tokenType) {
    int so = start + this.offsetShift;
    addToken(this.zzBuffer, start, end, tokenType, so, true);
  }






  
  private void addToken(int tokenType) {
    addToken(this.zzStartRead, this.zzMarkedPos - 1, tokenType);
  }






  
  private void addToken(int start, int end, int tokenType) {
    int so = start + this.offsetShift;
    addToken(this.zzBuffer, start, end, tokenType, so);
  }












  
  public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
    super.addToken(array, start, end, tokenType, startOffset);
    this.zzStartRead = this.zzMarkedPos;
  }










  
  public boolean getMarkOccurrencesOfTokenType(int type) {
    return false;
  }















  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;
    this.prevState = 0;

    
    int state = 0;
    switch (initialTokenType) {
      case -1:
        state = 2;
        break;
      case -2:
        state = 3;
        break;
      case -3:
        state = 4;
        break;
      default:
        if (initialTokenType < -1024) {
          int main = -(-initialTokenType & 0xFFFFFF00);
          switch (main) {
          
          } 
          state = 1;

          
          this.prevState = -initialTokenType & 0xFF;
          break;
        } 
        state = 0;
        break;
    } 
    
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









  
  public DtdTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public DtdTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 138) {
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
    
    while (true) {
      int zzInput, temp, zzMarkedPosL = this.zzMarkedPos;
      
      int zzAction = -1;
      
      int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
      
      this.zzState = this.zzLexicalState;



      
      while (true) {
        if (zzCurrentPosL < zzEndReadL)
        { zzInput = zzBufferL[zzCurrentPosL++]; }
        else { if (this.zzAtEOF) {
            int i = -1;
            
            break;
          } 
          
          this.zzCurrentPos = zzCurrentPosL;
          this.zzMarkedPos = zzMarkedPosL;
          boolean eof = zzRefill();
          
          zzCurrentPosL = this.zzCurrentPos;
          zzMarkedPosL = this.zzMarkedPos;
          zzBufferL = this.zzBuffer;
          zzEndReadL = this.zzEndRead;
          if (eof) {
            int i = -1;
            
            break;
          } 
          zzInput = zzBufferL[zzCurrentPosL++]; }

        
        int zzNext = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];
        if (zzNext == -1)
          break;  this.zzState = zzNext;
        
        int zzAttributes = zzAttrL[this.zzState];
        if ((zzAttributes & 0x1) == 1) {
          zzAction = this.zzState;
          zzMarkedPosL = zzCurrentPosL;
          if ((zzAttributes & 0x8) == 8) {
            break;
          }
        } 
      } 

      
      this.zzMarkedPos = zzMarkedPosL;
      
      switch ((zzAction < 0) ? zzAction : ZZ_ACTION[zzAction]) {
        case 3:
          addToken(20); continue;
        case 16:
          continue;
        case 2:
          addToken(21); continue;
        case 17:
          continue;
        case 1:
          addToken(20); continue;
        case 18:
          continue;
        case 12:
          temp = this.zzStartRead; addToken(this.start, this.zzStartRead - 1, 29); addHyperlinkToken(temp, this.zzMarkedPos - 1, 29); this.start = this.zzMarkedPos; continue;
        case 19:
          continue;
        case 9:
          addToken(25); yybegin(2); continue;
        case 20:
          continue;
        case 6:
          addToken(25); yybegin(0); continue;
        case 21:
          continue;
        case 10:
          temp = this.zzMarkedPos; addToken(this.start, this.zzStartRead + 2, 29); this.start = temp; yybegin(this.prevState); continue;
        case 22:
          continue;
        case 11:
          this.start = this.zzStartRead; this.prevState = this.zzLexicalState; yybegin(1); continue;
        case 23:
          continue;
        case 7:
          addToken(27); continue;
        case 24:
          continue;
        case 15:
          addToken(26); yybegin(4); continue;
        case 25:
          continue;
        case 14:
          addToken(26); yybegin(3); continue;
        case 26:
          continue;
        case 13:
          addToken(31);
          continue;
        
        case 27:
        case 4:
        case 28:
          continue;
        case 5:
          addToken(this.start, this.zzStartRead - 1, 29); addEndToken(-2048 - this.prevState); return (Token)this.firstToken;
        case 29:
          continue;
        case 8:
          addToken(28); continue;
        case 30:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 2:
            addEndToken(-1); return (Token)this.firstToken;
          case 71:
            continue;
          case 3:
            addEndToken(-2); return (Token)this.firstToken;
          case 72:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 73:
            continue;
          case 4:
            addEndToken(-3); return (Token)this.firstToken;
          case 74:
            continue;
          case 1:
            addToken(this.start, this.zzStartRead - 1, 29); addEndToken(-2048 - this.prevState); return (Token)this.firstToken;
          case 75:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
