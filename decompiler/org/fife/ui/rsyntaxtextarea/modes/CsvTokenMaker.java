package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexCTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;


























































public class CsvTokenMaker
  extends AbstractJFlexCTokenMaker
{
  public static final int YYEOF = -1;
  public static final int STRING = 1;
  public static final int YYINITIAL = 0;
  private static final String ZZ_CMAP_PACKED = "\n\000\001\003\027\000\001\001\t\000\001\002ￓ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\n\000\001\003\027\000\001\001\t\000\001\002ￓ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();

  
  private static final String ZZ_ACTION_PACKED_0 = "\002\000\001\001\001\002\001\003\001\004\001\005\001\006\001\007\001\005";

  
  private static int[] zzUnpackAction() {
    int[] result = new int[10];
    int offset = 0;
    offset = zzUnpackAction("\002\000\001\001\001\002\001\003\001\004\001\005\001\006\001\007\001\005", offset, result);
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

  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000\004\000\b\000\f\000\f\000\f\000\020\000\024\000\f\000\f";

  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[10];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000\004\000\b\000\f\000\f\000\f\000\020\000\024\000\f\000\f", offset, result);
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
  private static final String ZZ_TRANS_PACKED_0 = "\001\003\001\004\001\005\001\006\001\007\001\b\001\007\001\t\001\003\007\000\001\007\001\000\001\007\002\000\001\n\002\000";
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;
  
  private static int[] zzUnpackTrans() {
    int[] result = new int[24];
    int offset = 0;
    offset = zzUnpackTrans("\001\003\001\004\001\005\001\006\001\007\001\b\001\007\001\t\001\003\007\000\001\007\001\000\001\007\002\000\001\n\002\000", offset, result);
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
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\002\000\001\001\003\t\002\001\002\t";
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[10];
    int offset = 0;
    offset = zzUnpackAttribute("\002\000\001\001\003\t\002\001\002\t", offset, result);
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



  
  public static final int INTERNAL_STRING = -2048;



  
  private int evenOdd;




  
  public CsvTokenMaker() {}



  
  private void addEndToken(int tokenType) {
    addToken(this.zzMarkedPos, this.zzMarkedPos, tokenType);
  }




  
  private void addEvenOrOddColumnToken() {
    addEvenOrOddColumnToken(this.zzStartRead, this.zzMarkedPos - 1);
  }




  
  private void addEvenOrOddColumnToken(int start, int end) {
    addToken(start, end, (this.evenOdd == 0) ? 20 : 16);
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






  
  public int getClosestStandardTokenTypeForInternalType(int type) {
    return (type == -2048) ? 13 : type;
  }


  
  public boolean getMarkOccurrencesOfTokenType(int type) {
    return (type == 20 || type == 16);
  }














  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;

    
    int state = 0;
    this.evenOdd = 0;
    if (initialTokenType < -1024) {
      state = 1;
      this.evenOdd = initialTokenType & 0x1;
      this.start = text.offset;
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






  
  public boolean isIdentifierChar(int languageIndex, char ch) {
    return (Character.isLetterOrDigit(ch) || ch == '-' || ch == '.' || ch == '_');
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









  
  public CsvTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public CsvTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 14) {
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
        case 6:
          yybegin(0); addEvenOrOddColumnToken(this.start, this.zzStartRead); continue;
        case 8:
          continue;
        case 4:
          addNullToken(); return (Token)this.firstToken;
        case 9:
          continue;
        case 7:
          addEvenOrOddColumnToken(this.start, this.zzEndRead);
          addEndToken(0xFFFFF800 | this.evenOdd); return (Token)this.firstToken;
        case 10:
          continue;
        case 3:
          addToken(23);
          this.evenOdd = this.evenOdd + 1 & 0x1; continue;
        case 11:
          continue;
        case 1:
          addEvenOrOddColumnToken(); continue;
        case 12:
          continue;
        case 2:
          this.start = this.zzMarkedPos - 1; yybegin(1);
          continue;
        
        case 13:
        case 5:
        case 14:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 1:
            addEvenOrOddColumnToken(this.start, this.zzEndRead);
            addEndToken(0xFFFFF800 | this.evenOdd); return (Token)this.firstToken;
          case 11:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 12:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
