package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;































































public class IniTokenMaker
  extends AbstractJFlexTokenMaker
{
  public static final int YYEOF = -1;
  public static final int YYINITIAL = 0;
  public static final int VALUE = 1;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\003\001\005\025\000\001\003\002\000\001\004\027\000\001\004\001\000\001\001\035\000\001\002\001\000\001\006ﾢ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\003\001\005\025\000\001\003\002\000\001\004\027\000\001\004\001\000\001\001\035\000\001\002\001\000\001\006ﾢ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();

  
  private static final String ZZ_ACTION_PACKED_0 = "\001\001\001\002\001\001\001\003\001\004\001\005\001\006\001\002\001\007\001\004";

  
  private static int[] zzUnpackAction() {
    int[] result = new int[10];
    int offset = 0;
    offset = zzUnpackAction("\001\001\001\002\001\001\001\003\001\004\001\005\001\006\001\002\001\007\001\004", offset, result);
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

  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000\007\000\016\000\025\000\034\000#\000*\0001\000\025\000\025";

  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[10];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000\007\000\016\000\025\000\034\000#\000*\0001\000\025\000\025", offset, result);
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
  
  private static final String ZZ_TRANS_PACKED_0 = "\001\003\001\004\001\005\001\006\001\007\001\000\001\003\001\b\001\t\001\005\001\006\001\007\001\000\001\b\001\003\005\000\001\003\007\000\006\005\001\n\003\000\001\006\003\000\005\007\001\000\001\007\001\b\005\000\001\b";
  
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;
  
  private static int[] zzUnpackTrans() {
    int[] result = new int[56];
    int offset = 0;
    offset = zzUnpackTrans("\001\003\001\004\001\005\001\006\001\007\001\000\001\003\001\b\001\t\001\005\001\006\001\007\001\000\001\b\001\003\005\000\001\003\007\000\006\005\001\n\003\000\001\006\003\000\005\007\001\000\001\007\001\b\005\000\001\b", offset, result);
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
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\003\001\001\t\004\001\002\t";
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[10];
    int offset = 0;
    offset = zzUnpackAttribute("\003\001\001\t\004\001\002\t", offset, result);
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



  
  public IniTokenMaker() {}



  
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





  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] { ";", null };
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









  
  public IniTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public IniTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 32) {
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
        case 3:
          this.start = this.zzMarkedPos; addToken(23); yybegin(1); continue;
        case 8:
          continue;
        case 2:
          addToken(20); continue;
        case 9:
          continue;
        case 5:
          addToken(21); continue;
        case 10:
          continue;
        case 7:
          this.start = this.zzMarkedPos; addToken(23); continue;
        case 11:
          continue;
        case 6:
          addToken(1); continue;
        case 12:
          continue;
        case 4:
          addToken(24); continue;
        case 13:
          continue;
        case 1:
          addToken(16); continue;
        case 14:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 11:
            continue;
          case 1:
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
