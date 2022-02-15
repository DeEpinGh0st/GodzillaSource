package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;




























































public class HostsTokenMaker
  extends AbstractJFlexTokenMaker
{
  public static final int YYEOF = -1;
  public static final int EOL_COMMENT = 1;
  public static final int YYINITIAL = 0;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\002\001\001\025\000\001\002\001\004\001\000\001\003\001\006\001\004\007\004\001\004\001\021\001\005\n\006\001\017\001\004\001\000\001\004\001\000\002\004\032\006\001\004\001\000\001\004\001\000\001\004\001\000\004\006\001\016\001\013\001\006\001\007\001\f\002\006\001\r\003\006\001\t\002\006\001\n\001\b\002\006\001\020\003\006\003\000\001\004ﾁ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\002\001\001\025\000\001\002\001\004\001\000\001\003\001\006\001\004\007\004\001\004\001\021\001\005\n\006\001\017\001\004\001\000\001\004\001\000\002\004\032\006\001\004\001\000\001\004\001\000\001\004\001\000\004\006\001\016\001\013\001\006\001\007\001\f\002\006\001\r\003\006\001\t\002\006\001\n\001\b\002\006\001\020\003\006\003\000\001\004ﾁ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();

  
  private static final String ZZ_ACTION_PACKED_0 = "\002\000\001\001\001\002\001\003\001\004\001\005\001\006\003\005\n\000\001\007\002\000";

  
  private static int[] zzUnpackAction() {
    int[] result = new int[24];
    int offset = 0;
    offset = zzUnpackAction("\002\000\001\001\001\002\001\003\001\004\001\005\001\006\003\005\n\000\001\007\002\000", offset, result);
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

  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000\022\000$\0006\000H\0006\000Z\0006\000l\000~\000\000¢\000´\000Æ\000Ø\000ê\000ü\000Ď\000Ġ\000Ĳ\000ń\000Ŗ\000Ũ\000Ŗ";


  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[24];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000\022\000$\0006\000H\0006\000Z\0006\000l\000~\000\000¢\000´\000Æ\000Ø\000ê\000ü\000Ď\000Ġ\000Ĳ\000ń\000Ŗ\000Ũ\000Ŗ", offset, result);
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

  
  private static final String ZZ_TRANS_PACKED_0 = "\001\003\001\004\001\005\001\006\016\003\001\007\001\b\005\007\001\t\003\007\001\n\004\007\001\013\001\007\001\003\003\000\016\003\024\000\001\005\017\000\001\007\001\000\005\007\001\000\003\007\001\000\004\007\001\000\001\007\b\000\001\f\021\000\001\r\003\000\001\016\025\000\001\017\t\000\001\020\022\000\001\021\025\000\001\022\024\000\001\023\n\000\001\024\027\000\001\025\020\000\001\021\024\000\001\026\n\000\001\021\004\000\001\025\007\000\001\027\017\000\002\030\n\026\001\030\001\026\001\030\005\000\001\026\f\000";

  
  private static final int ZZ_UNKNOWN_ERROR = 0;
  
  private static final int ZZ_NO_MATCH = 1;
  
  private static final int ZZ_PUSHBACK_2BIG = 2;

  
  private static int[] zzUnpackTrans() {
    int[] result = new int[378];
    int offset = 0;
    offset = zzUnpackTrans("\001\003\001\004\001\005\001\006\016\003\001\007\001\b\005\007\001\t\003\007\001\n\004\007\001\013\001\007\001\003\003\000\016\003\024\000\001\005\017\000\001\007\001\000\005\007\001\000\003\007\001\000\004\007\001\000\001\007\b\000\001\f\021\000\001\r\003\000\001\016\025\000\001\017\t\000\001\020\022\000\001\021\025\000\001\022\024\000\001\023\n\000\001\024\027\000\001\025\020\000\001\021\024\000\001\026\n\000\001\021\004\000\001\025\007\000\001\027\017\000\002\030\n\026\001\030\001\026\001\030\005\000\001\026\f\000", offset, result);
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
  
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\002\000\001\001\001\t\001\001\001\t\001\001\001\t\003\001\n\000\001\001\002\000";
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[24];
    int offset = 0;
    offset = zzUnpackAttribute("\002\000\001\001\001\t\001\001\001\t\001\001\001\t\003\001\n\000\001\001\002\000", offset, result);
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


  
  private boolean first;



  
  public HostsTokenMaker() {}



  
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





  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] { "#", null };
  }





  
  public boolean getMarkOccurrencesOfTokenType(int type) {
    return (type == 6);
  }















  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;
    this.first = true;

    
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









  
  public HostsTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public HostsTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 94) {
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
        case 2:
          addNullToken(); return (Token)this.firstToken;
        case 8:
          continue;
        case 7:
          temp = this.zzStartRead; addToken(this.start, this.zzStartRead - 1, 1); addHyperlinkToken(temp, this.zzMarkedPos - 1, 1); this.start = this.zzMarkedPos; continue;
        case 9:
          continue;
        case 4:
          this.start = this.zzMarkedPos - 1; yybegin(1); continue;
        case 10:
          continue;
        case 3:
          addToken(21); continue;
        case 11:
          continue;
        case 6:
          addToken(this.start, this.zzStartRead - 1, 1); addNullToken(); return (Token)this.firstToken;
        case 12:
          continue;
        case 1:
          addToken(this.first ? 6 : 20);
          this.first = false;
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
            addToken(this.start, this.zzStartRead - 1, 1); addNullToken(); return (Token)this.firstToken;
          case 25:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 26:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
