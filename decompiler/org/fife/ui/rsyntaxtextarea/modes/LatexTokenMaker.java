package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;


































































public class LatexTokenMaker
  extends AbstractJFlexTokenMaker
{
  public static final int YYEOF = -1;
  public static final int EOL_COMMENT = 1;
  public static final int YYINITIAL = 0;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\003\001\032\001\000\001\003\023\000\001\003\001\005\001\000\001\005\001\007\001\004\007\005\001\002\001\022\001\006\n\001\001\020\001\005\001\000\001\005\001\000\002\005\032\001\001\005\001\023\001\005\001\000\001\002\001\000\001\001\001\025\001\001\001\031\001\017\001\f\001\026\001\b\001\r\002\001\001\016\001\001\001\027\001\001\001\n\002\001\001\013\001\t\002\001\001\021\003\001\001\030\001\000\001\024\001\005ﾁ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\003\001\032\001\000\001\003\023\000\001\003\001\005\001\000\001\005\001\007\001\004\007\005\001\002\001\022\001\006\n\001\001\020\001\005\001\000\001\005\001\000\002\005\032\001\001\005\001\023\001\005\001\000\001\002\001\000\001\001\001\025\001\001\001\031\001\017\001\f\001\026\001\b\001\r\002\001\001\016\001\001\001\027\001\001\001\n\002\001\001\013\001\t\002\001\001\021\003\001\001\030\001\000\001\024\001\005ﾁ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();


  
  private static final String ZZ_ACTION_PACKED_0 = "\002\000\002\001\001\002\001\003\001\001\001\004\001\005\004\006\001\007\001\b\001\t\002\b\004\000\002\b\004\000\002\b\002\000\001\n\001\000\001\b\003\000\001\b\001\013\002\000\001\f";


  
  private static int[] zzUnpackAction() {
    int[] result = new int[43];
    int offset = 0;
    offset = zzUnpackAction("\002\000\002\001\001\002\001\003\001\001\001\004\001\005\004\006\001\007\001\b\001\t\002\b\004\000\002\b\004\000\002\b\002\000\001\n\001\000\001\b\003\000\001\b\001\013\002\000\001\f", offset, result);
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



  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000\033\0006\000Q\0006\0006\000l\0006\0006\000\000¢\000½\000Ø\0006\000ó\0006\000Ď\000ĩ\000ń\000ş\000ź\000ƕ\000ư\000ǋ\000Ǧ\000ȁ\000Ȝ\000ȷ\000ɒ\000ɭ\000ʈ\000ʣ\000ʾ\000˙\000˴\000̏\000ʾ\000̪\000ͅ\0006\000͠\000ͻ\0006";



  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[43];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000\033\0006\000Q\0006\0006\000l\0006\0006\000\000¢\000½\000Ø\0006\000ó\0006\000Ď\000ĩ\000ń\000ş\000ź\000ƕ\000ư\000ǋ\000Ǧ\000ȁ\000Ȝ\000ȷ\000ɒ\000ɭ\000ʈ\000ʣ\000ʾ\000˙\000˴\000̏\000ʾ\000̪\000ͅ\0006\000͠\000ͻ\0006", offset, result);
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





  
  private static final String ZZ_TRANS_PACKED_0 = "\001\003\002\004\001\005\001\006\003\003\b\004\001\003\001\004\001\003\001\007\001\b\003\004\001\b\001\004\001\t\b\n\001\013\003\n\001\f\004\n\001\r\b\n\001\016\034\000\002\004\005\000\b\004\001\000\001\004\003\000\003\004\001\000\001\004\002\000\002\017\001\000\001\020\003\000\007\017\001\021\001\000\001\017\003\000\001\022\002\017\001\000\001\017\001\000\b\n\001\000\003\n\001\000\004\n\001\000\b\n\n\000\001\023\032\000\001\024\003\000\001\025\036\000\001\026\n\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001\000\001\017\002\000\002\017\005\000\b\017\001\000\001\017\003\000\002\017\001\027\001\000\001\017\002\000\002\017\005\000\007\017\001\030\001\000\001\017\003\000\003\017\001\000\001\017\n\000\001\031\033\000\001\032\036\000\001\033\035\000\001\034\n\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001\000\001\035\002\000\002\017\005\000\b\017\001\000\001\017\003\000\001\017\001\036\001\017\001\000\001\017\013\000\001\037 \000\001 \031\000\001\032\035\000\001!\t\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001\"\001\017\002\000\002\017\005\000\005\017\001#\002\017\001\000\001\017\003\000\003\017\001\000\001\017\f\000\001\032\004\000\001 \020\000\001$\025\000\001!\001%\001\000\002%\n!\001%\001!\001%\002\000\003!\001\000\001!\002\000\002&\005\000\b&\001\000\001&\003\000\003&\001\000\001&\002\000\002\017\005\000\b\017\001\000\001\017\003\000\002\017\001'\001\000\001\017\007\000\001!\025\000\002&\005\000\b&\001\000\001&\002\000\001(\003&\001\000\001&\002\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001)\001\017\002\000\002*\005\000\b*\001\000\001*\003\000\003*\001\000\001*\002\000\002*\005\000\b*\001\000\001*\002\000\001+\003*\001\000\001*\001\000";





  
  private static final int ZZ_UNKNOWN_ERROR = 0;





  
  private static final int ZZ_NO_MATCH = 1;




  
  private static final int ZZ_PUSHBACK_2BIG = 2;





  
  private static int[] zzUnpackTrans() {
    int[] result = new int[918];
    int offset = 0;
    offset = zzUnpackTrans("\001\003\002\004\001\005\001\006\003\003\b\004\001\003\001\004\001\003\001\007\001\b\003\004\001\b\001\004\001\t\b\n\001\013\003\n\001\f\004\n\001\r\b\n\001\016\034\000\002\004\005\000\b\004\001\000\001\004\003\000\003\004\001\000\001\004\002\000\002\017\001\000\001\020\003\000\007\017\001\021\001\000\001\017\003\000\001\022\002\017\001\000\001\017\001\000\b\n\001\000\003\n\001\000\004\n\001\000\b\n\n\000\001\023\032\000\001\024\003\000\001\025\036\000\001\026\n\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001\000\001\017\002\000\002\017\005\000\b\017\001\000\001\017\003\000\002\017\001\027\001\000\001\017\002\000\002\017\005\000\007\017\001\030\001\000\001\017\003\000\003\017\001\000\001\017\n\000\001\031\033\000\001\032\036\000\001\033\035\000\001\034\n\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001\000\001\035\002\000\002\017\005\000\b\017\001\000\001\017\003\000\001\017\001\036\001\017\001\000\001\017\013\000\001\037 \000\001 \031\000\001\032\035\000\001!\t\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001\"\001\017\002\000\002\017\005\000\005\017\001#\002\017\001\000\001\017\003\000\003\017\001\000\001\017\f\000\001\032\004\000\001 \020\000\001$\025\000\001!\001%\001\000\002%\n!\001%\001!\001%\002\000\003!\001\000\001!\002\000\002&\005\000\b&\001\000\001&\003\000\003&\001\000\001&\002\000\002\017\005\000\b\017\001\000\001\017\003\000\002\017\001'\001\000\001\017\007\000\001!\025\000\002&\005\000\b&\001\000\001&\002\000\001(\003&\001\000\001&\002\000\002\017\005\000\b\017\001\000\001\017\003\000\003\017\001)\001\017\002\000\002*\005\000\b*\001\000\001*\003\000\003*\001\000\001*\002\000\002*\005\000\b*\001\000\001*\002\000\001+\003*\001\000\001*\001\000", offset, result);
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
  
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\002\000\001\t\001\001\002\t\001\001\002\t\004\001\001\t\001\001\001\t\002\001\004\000\002\001\004\000\002\001\002\000\001\001\001\000\001\001\003\000\001\001\001\t\002\000\001\t";
  
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[43];
    int offset = 0;
    offset = zzUnpackAttribute("\002\000\001\t\001\001\002\t\001\001\002\t\004\001\001\t\001\001\001\t\002\001\004\000\002\001\004\000\002\001\002\000\001\001\001\000\001\001\003\000\001\001\001\t\002\000\001\t", offset, result);
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



  
  public LatexTokenMaker() {}



  
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





  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] { "%", null };
  }















  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;

    
    int state = 0;
    
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









  
  public LatexTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public LatexTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 112) {
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
        case 1:
          addToken(20); continue;
        case 13:
          continue;
        case 8:
          addToken(8); continue;
        case 14:
          continue;
        case 2:
          addToken(21); continue;
        case 15:
          continue;
        case 12:
          temp = this.zzStartRead;
          addToken(temp, temp + 5, 6);
          addToken(temp + 6, temp + 6, 22);
          addToken(temp + 7, this.zzMarkedPos - 2, 6);
          addToken(this.zzMarkedPos - 1, this.zzMarkedPos - 1, 22); continue;
        case 16:
          continue;
        case 10:
          temp = this.zzStartRead; addToken(this.start, this.zzStartRead - 1, 1); addHyperlinkToken(temp, this.zzMarkedPos - 1, 1); this.start = this.zzMarkedPos; continue;
        case 17:
          continue;
        case 3:
          this.start = this.zzMarkedPos - 1; yybegin(1); continue;
        case 18:
          continue;
        case 11:
          temp = this.zzStartRead;
          addToken(temp, temp + 3, 6);
          addToken(temp + 4, temp + 4, 22);
          addToken(temp + 5, this.zzMarkedPos - 2, 6);
          addToken(this.zzMarkedPos - 1, this.zzMarkedPos - 1, 22); continue;
        case 19:
          continue;
        case 5:
          addNullToken(); return (Token)this.firstToken;
        case 20:
          continue;
        case 7:
          addToken(this.start, this.zzStartRead - 1, 1); addNullToken(); return (Token)this.firstToken;
        case 21:
          continue;
        case 9:
          temp = this.zzStartRead;
          addToken(temp, temp, 22);
          addToken(temp + 1, temp + 1, 20);
          continue;
        
        case 22:
        case 6:
        case 23:
          continue;
        case 4:
          addToken(22); continue;
        case 24:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 1:
            addToken(this.start, this.zzStartRead - 1, 1); addNullToken(); return (Token)this.firstToken;
          case 44:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 45:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
