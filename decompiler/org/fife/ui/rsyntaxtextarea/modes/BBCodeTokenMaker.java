package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;






























































public class BBCodeTokenMaker
  extends AbstractMarkupTokenMaker
{
  public static final int YYEOF = -1;
  public static final int INTAG = 1;
  public static final int YYINITIAL = 0;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\001\001\002\001\000\001\001\023\000\001\001\016\000\001\027\r\000\001\030\035\000\001\003\001\000\001\004\004\000\001\005\001\013\001\026\001\n\001\000\001\023\001\000\001\006\002\000\001\r\001\022\001\017\001\f\001\000\001\021\001\016\001\b\001\020\001\007\001\025\002\000\001\024\001\tﾅ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\001\001\002\001\000\001\001\023\000\001\001\016\000\001\027\r\000\001\030\035\000\001\003\001\000\001\004\004\000\001\005\001\013\001\026\001\n\001\000\001\023\001\000\001\006\002\000\001\r\001\022\001\017\001\f\001\000\001\021\001\016\001\b\001\020\001\007\001\025\002\000\001\024\001\tﾅ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();

  
  private static final String ZZ_ACTION_PACKED_0 = "\002\000\001\001\001\002\001\003\001\004\001\005\001\006\001\007\004\b\006\005\001\t\001\n\001\004\023\005";

  
  private static int[] zzUnpackAction() {
    int[] result = new int[41];
    int offset = 0;
    offset = zzUnpackAction("\002\000\001\001\001\002\001\003\001\004\001\005\001\006\001\007\004\b\006\005\001\t\001\n\001\004\023\005", offset, result);
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



  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000\031\0002\000K\000d\000}\000\000d\000d\000\000¯\000È\000á\000ú\000ē\000Ĭ\000Ņ\000Ş\000ŷ\000Ɛ\000d\000d\000Ʃ\000ǂ\000Ǜ\000Ǵ\000ȍ\000Ȧ\000ȿ\000ɘ\000ɱ\000ʊ\000ʣ\000ʼ\000˕\000ˮ\000̇\000̠\000̹\000͒\000ͫ";



  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[41];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000\031\0002\000K\000d\000}\000\000d\000d\000\000¯\000È\000á\000ú\000ē\000Ĭ\000Ņ\000Ş\000ŷ\000Ɛ\000d\000d\000Ʃ\000ǂ\000Ǜ\000Ǵ\000ȍ\000Ȧ\000ȿ\000ɘ\000ɱ\000ʊ\000ʣ\000ʼ\000˕\000ˮ\000̇\000̠\000̹\000͒\000ͫ", offset, result);
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




  
  private static final String ZZ_TRANS_PACKED_0 = "\001\003\001\004\001\005\001\006\025\003\001\007\001\004\001\000\001\b\001\t\001\n\001\013\001\f\001\r\002\007\001\016\001\017\001\020\003\007\001\021\001\007\001\022\001\023\002\007\001\024\001\025\001\003\003\000\025\003\001\000\001\004G\000\001\026\001\000\001\007\004\000\022\007\002\000\001\007\004\000\r\007\001\027\004\007\002\000\001\007\004\000\b\007\001\n\001\017\b\007\002\000\001\007\004\000\001\007\001\030\020\007\002\000\001\007\004\000\005\007\001\031\001\007\001\032\n\007\002\000\001\007\004\000\b\007\001\n\t\007\002\000\001\007\004\000\001\007\001\n\020\007\002\000\001\007\004\000\002\007\001\033\017\007\002\000\001\007\004\000\020\007\001\034\001\007\002\000\001\007\004\000\007\007\001\035\n\007\006\000\001\t\024\000\001\007\004\000\016\007\001\n\003\007\002\000\001\007\004\000\004\007\001\036\r\007\002\000\001\007\004\000\n\007\001\037\007\007\002\000\001\007\004\000\b\007\001 \t\007\002\000\001\007\004\000\007\007\001!\n\007\002\000\001\007\004\000\001\007\001\"\020\007\002\000\001\007\004\000\002\007\001#\017\007\002\000\001\007\004\000\005\007\001\n\f\007\002\000\001\007\004\000\013\007\001$\006\007\002\000\001\007\004\000\007\007\001%\n\007\002\000\001\007\004\000\013\007\001\036\006\007\002\000\001\007\004\000\021\007\001&\002\000\001\007\004\000\013\007\001'\006\007\002\000\001\007\004\000\005\007\001%\f\007\002\000\001\007\004\000\t\007\001\n\b\007\002\000\001\007\004\000\005\007\001(\f\007\002\000\001\007\004\000\002\007\001)\017\007\002\000\001\007\004\000\007\007\001\n\n\007\002\000\001\007\004\000\001\036\021\007\002\000";




  
  private static final int ZZ_UNKNOWN_ERROR = 0;




  
  private static final int ZZ_NO_MATCH = 1;




  
  private static final int ZZ_PUSHBACK_2BIG = 2;





  
  private static int[] zzUnpackTrans() {
    int[] result = new int[900];
    int offset = 0;
    offset = zzUnpackTrans("\001\003\001\004\001\005\001\006\025\003\001\007\001\004\001\000\001\b\001\t\001\n\001\013\001\f\001\r\002\007\001\016\001\017\001\020\003\007\001\021\001\007\001\022\001\023\002\007\001\024\001\025\001\003\003\000\025\003\001\000\001\004G\000\001\026\001\000\001\007\004\000\022\007\002\000\001\007\004\000\r\007\001\027\004\007\002\000\001\007\004\000\b\007\001\n\001\017\b\007\002\000\001\007\004\000\001\007\001\030\020\007\002\000\001\007\004\000\005\007\001\031\001\007\001\032\n\007\002\000\001\007\004\000\b\007\001\n\t\007\002\000\001\007\004\000\001\007\001\n\020\007\002\000\001\007\004\000\002\007\001\033\017\007\002\000\001\007\004\000\020\007\001\034\001\007\002\000\001\007\004\000\007\007\001\035\n\007\006\000\001\t\024\000\001\007\004\000\016\007\001\n\003\007\002\000\001\007\004\000\004\007\001\036\r\007\002\000\001\007\004\000\n\007\001\037\007\007\002\000\001\007\004\000\b\007\001 \t\007\002\000\001\007\004\000\007\007\001!\n\007\002\000\001\007\004\000\001\007\001\"\020\007\002\000\001\007\004\000\002\007\001#\017\007\002\000\001\007\004\000\005\007\001\n\f\007\002\000\001\007\004\000\013\007\001$\006\007\002\000\001\007\004\000\007\007\001%\n\007\002\000\001\007\004\000\013\007\001\036\006\007\002\000\001\007\004\000\021\007\001&\002\000\001\007\004\000\013\007\001'\006\007\002\000\001\007\004\000\005\007\001%\f\007\002\000\001\007\004\000\t\007\001\n\b\007\002\000\001\007\004\000\005\007\001(\f\007\002\000\001\007\004\000\002\007\001)\017\007\002\000\001\007\004\000\007\007\001\n\n\007\002\000\001\007\004\000\001\036\021\007\002\000", offset, result);
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
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\002\000\002\001\001\t\002\001\002\t\013\001\002\t\023\001";
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[41];
    int offset = 0;
    offset = zzUnpackAttribute("\002\000\002\001\001\t\002\001\002\t\013\001\002\t\023\001", offset, result);
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



  
  public static final int INTERNAL_INTAG = -1;



  
  private static boolean completeCloseTags = true;



  
  public BBCodeTokenMaker() {}



  
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









  
  public boolean getCompleteCloseTags() {
    return completeCloseTags;
  }







  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return null;
  }















  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;

    
    int state = 0;
    switch (initialTokenType) {
      case -1:
        state = 1;
        this.start = text.offset;
        break;
      default:
        state = 0;
        break;
    } 
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








  
  public static void setCompleteCloseTags(boolean complete) {
    completeCloseTags = complete;
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









  
  public BBCodeTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public BBCodeTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 80) {
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
      int zzInput, zzMarkedPosL = this.zzMarkedPos;
      
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
        case 1:
          addToken(20); continue;
        case 11:
          continue;
        case 9:
          addToken(25); continue;
        case 12:
          continue;
        case 2:
          addToken(21); continue;
        case 13:
          continue;
        case 10:
          addToken(23); continue;
        case 14:
          continue;
        case 8:
          addToken(26); continue;
        case 15:
          continue;
        case 4:
          addToken(25); yybegin(1); continue;
        case 16:
          continue;
        case 6:
          addToken(20); continue;
        case 17:
          continue;
        case 5:
          addToken(27); continue;
        case 18:
          continue;
        case 3:
          addNullToken(); return (Token)this.firstToken;
        case 19:
          continue;
        case 7:
          yybegin(0); addToken(25); continue;
        case 20:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 1:
            addToken(this.zzMarkedPos, this.zzMarkedPos, -1); return (Token)this.firstToken;
          case 42:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 43:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
