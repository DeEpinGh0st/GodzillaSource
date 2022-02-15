package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;































































public class DockerTokenMaker
  extends AbstractJFlexTokenMaker
{
  public static final int YYEOF = -1;
  public static final int STRING = 1;
  public static final int CHAR_LITERAL = 2;
  public static final int YYINITIAL = 0;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\002\001\037\001\000\001\002\023\000\001\002\001\000\001\034\001\036\003\000\001\035\005\000\002\001\001\000\n\001\004\000\001\033\002\000\001\004\001\f\001\024\001\017\001\b\001\n\001\030\001\001\001\005\001\001\001\027\001\016\001\003\001\006\001\013\001\021\001\001\001\t\001\022\001\007\001\r\001\023\001\026\001\020\001\025\001\001\001\031\001 \001\031\001\000\001\001\001\000\001\004\001\f\001\024\001\017\001\b\001\n\001\030\001\001\001\005\001\001\001\027\001\016\001\003\001\006\001\013\001\021\001\001\001\t\001\022\001\007\001\r\001\023\001\026\001\020\001\025\001\001\001\000\001\032ﾃ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\002\001\037\001\000\001\002\023\000\001\002\001\000\001\034\001\036\003\000\001\035\005\000\002\001\001\000\n\001\004\000\001\033\002\000\001\004\001\f\001\024\001\017\001\b\001\n\001\030\001\001\001\005\001\001\001\027\001\016\001\003\001\006\001\013\001\021\001\001\001\t\001\022\001\007\001\r\001\023\001\026\001\020\001\025\001\001\001\031\001 \001\031\001\000\001\001\001\000\001\004\001\f\001\024\001\017\001\b\001\n\001\030\001\001\001\005\001\001\001\027\001\016\001\003\001\006\001\013\001\021\001\001\001\t\001\022\001\007\001\r\001\023\001\026\001\020\001\025\001\001\001\000\001\032ﾃ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();


  
  private static final String ZZ_ACTION_PACKED_0 = "\001\001\002\000\002\001\001\002\f\001\001\003\002\004\001\005\001\006\001\007\001\b\001\t\001\n\001\013\001\f\001\t\001\r\001\016\001\017\016\001\001\f\001\017\001\001\001\020!\001";


  
  private static int[] zzUnpackAction() {
    int[] result = new int[84];
    int offset = 0;
    offset = zzUnpackAction("\001\001\002\000\002\001\001\002\f\001\001\003\002\004\001\005\001\006\001\007\001\b\001\t\001\n\001\013\001\f\001\t\001\r\001\016\001\017\016\001\001\f\001\017\001\001\001\020!\001", offset, result);
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





  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000!\000B\000c\000\000¥\000Æ\000ç\000Ĉ\000ĩ\000Ŋ\000ū\000ƌ\000ƭ\000ǎ\000ǯ\000Ȑ\000ȱ\000c\000c\000ɒ\000c\000c\000ɳ\000c\000ʔ\000c\000c\000ʵ\000˖\000c\000c\000˷\000̘\000̹\000͚\000ͻ\000Μ\000ν\000Ϟ\000Ͽ\000Р\000с\000Ѣ\000҃\000Ҥ\000Ӆ\000c\000c\000Ӧ\000\000ԇ\000Ԩ\000Չ\000ժ\000֋\000֬\000׍\000׮\000؏\000ذ\000ّ\000ٲ\000ړ\000ڴ\000ە\000۶\000ܗ\000ܸ\000ݙ\000ݺ\000ޛ\000޼\000ߝ\000߾\000ࠟ\000ࡀ\000ࡡ\000ࢂ\000ࢣ\000ࣄ\000ࣥ\000आ\000ध";






  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[84];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000!\000B\000c\000\000¥\000Æ\000ç\000Ĉ\000ĩ\000Ŋ\000ū\000ƌ\000ƭ\000ǎ\000ǯ\000Ȑ\000ȱ\000c\000c\000ɒ\000c\000c\000ɳ\000c\000ʔ\000c\000c\000ʵ\000˖\000c\000c\000˷\000̘\000̹\000͚\000ͻ\000Μ\000ν\000Ϟ\000Ͽ\000Р\000с\000Ѣ\000҃\000Ҥ\000Ӆ\000c\000c\000Ӧ\000\000ԇ\000Ԩ\000Չ\000ժ\000֋\000֬\000׍\000׮\000؏\000ذ\000ّ\000ٲ\000ړ\000ڴ\000ە\000۶\000ܗ\000ܸ\000ݙ\000ݺ\000ޛ\000޼\000ߝ\000߾\000ࠟ\000ࡀ\000ࡡ\000ࢂ\000ࢣ\000ࣄ\000ࣥ\000आ\000ध", offset, result);
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










  
  private static final String ZZ_TRANS_PACKED_0 = "\001\004\001\005\001\006\001\007\001\b\003\005\001\t\001\n\001\013\001\f\001\005\001\r\001\016\003\005\001\017\001\020\001\021\001\005\001\022\002\005\001\023\001\024\001\025\001\026\001\027\001\030\001\031\001\004\034\032\001\033\002\032\001\034\001\035\035\036\001\037\001\036\001 \001!\"\000\001\005\001\000\026\005\n\000\001\006\037\000\001\005\001\000\001\005\001\"\024\005\t\000\001\005\001\000\006\005\001#\005\005\001$\t\005\t\000\001\005\001\000\003\005\001%\t\005\001&\b\005\t\000\001\005\001\000\n\005\001'\013\005\t\000\001\005\001\000\006\005\001(\017\005\t\000\001\005\001\000\003\005\001)\022\005\t\000\001\005\001\000\017\005\001*\006\005\t\000\001\005\001\000\001\005\001+\024\005\t\000\001\005\001\000\004\005\001,\021\005\t\000\001\005\001\000\b\005\001-\r\005\t\000\001\005\001\000\001$\007\005\001.\r\005\t\000\001\005\001\000\b\005\001/\r\005#\000\001\024\005\000\037\030\001\000\001\030\034\032\001\000\002\032\002\000\0370\001\000\0010\035\036\001\000\001\036\002\000\0371\001\000\0011\001\000\001\005\001\000\002\005\0012\023\005\t\000\001\005\001\000\025\005\0013\t\000\001\005\001\000\f\005\0013\t\005\t\000\001\005\001\000\004\005\0014\013\005\0013\005\005\t\000\001\005\001\000\016\005\0015\007\005\t\000\001\005\001\000\003\005\0013\022\005\t\000\001\005\001\000\b\005\0016\r\005\t\000\001\005\001\000\t\005\0017\f\005\t\000\001\005\001\000\005\005\0018\020\005\t\000\001\005\001\000\t\005\0019\f\005\t\000\001\005\001\000\b\005\001:\r\005\t\000\001\005\001\000\013\005\001;\n\005\t\000\001\005\001\000\016\005\001<\007\005\t\000\001\005\001\000\006\005\001=\017\005\t\000\001\005\001\000\003\005\001>\022\005\t\000\001\005\001\000\006\005\001?\017\005\t\000\001\005\001\000\b\005\001@\r\005\t\000\001\005\001\000\0013\025\005\t\000\001\005\001\000\n\005\001A\013\005\t\000\001\005\001\000\006\005\0013\017\005\t\000\001\005\001\000\005\005\001B\020\005\t\000\001\005\001\000\016\005\001C\007\005\t\000\001\005\001\000\n\005\001D\013\005\t\000\001\005\001\000\022\005\0013\003\005\t\000\001\005\001\000\024\005\001E\001\005\t\000\001\005\001\000\004\005\001F\021\005\t\000\001\005\001\000\022\005\001G\003\005\t\000\001\005\001\000\017\005\001H\006\005\t\000\001\005\001\000\002\005\001I\023\005\t\000\001\005\001\000\013\005\0013\n\005\t\000\001\005\001\000\017\005\001J\006\005\t\000\001\005\001\000\001H\025\005\t\000\001\005\001\000\f\005\001K\t\005\t\000\001\005\001\000\001\005\001L\024\005\t\000\001\005\001\000\016\005\001M\007\005\t\000\001\005\001\000\005\005\0013\020\005\t\000\001\005\001\000\013\005\001$\n\005\t\000\001\005\001\000\002\005\001N\023\005\t\000\001\005\001\000\002\005\0018\023\005\t\000\001\005\001\000\002\005\001O\023\005\t\000\001\005\001\000\b\005\001P\r\005\t\000\001\005\001\000\025\005\001Q\t\000\001\005\001\000\003\005\001*\022\005\t\000\001\005\001\000\002\005\001R\023\005\t\000\001\005\001\000\003\005\001S\022\005\t\000\001\005\001\000\003\005\001T\022\005\t\000\001\005\001\000\001\005\001B\024\005\t\000\001\005\001\000\004\005\0013\021\005\b\000";










  
  private static final int ZZ_UNKNOWN_ERROR = 0;









  
  private static final int ZZ_NO_MATCH = 1;









  
  private static final int ZZ_PUSHBACK_2BIG = 2;










  
  private static int[] zzUnpackTrans() {
    int[] result = new int[2376];
    int offset = 0;
    offset = zzUnpackTrans("\001\004\001\005\001\006\001\007\001\b\003\005\001\t\001\n\001\013\001\f\001\005\001\r\001\016\003\005\001\017\001\020\001\021\001\005\001\022\002\005\001\023\001\024\001\025\001\026\001\027\001\030\001\031\001\004\034\032\001\033\002\032\001\034\001\035\035\036\001\037\001\036\001 \001!\"\000\001\005\001\000\026\005\n\000\001\006\037\000\001\005\001\000\001\005\001\"\024\005\t\000\001\005\001\000\006\005\001#\005\005\001$\t\005\t\000\001\005\001\000\003\005\001%\t\005\001&\b\005\t\000\001\005\001\000\n\005\001'\013\005\t\000\001\005\001\000\006\005\001(\017\005\t\000\001\005\001\000\003\005\001)\022\005\t\000\001\005\001\000\017\005\001*\006\005\t\000\001\005\001\000\001\005\001+\024\005\t\000\001\005\001\000\004\005\001,\021\005\t\000\001\005\001\000\b\005\001-\r\005\t\000\001\005\001\000\001$\007\005\001.\r\005\t\000\001\005\001\000\b\005\001/\r\005#\000\001\024\005\000\037\030\001\000\001\030\034\032\001\000\002\032\002\000\0370\001\000\0010\035\036\001\000\001\036\002\000\0371\001\000\0011\001\000\001\005\001\000\002\005\0012\023\005\t\000\001\005\001\000\025\005\0013\t\000\001\005\001\000\f\005\0013\t\005\t\000\001\005\001\000\004\005\0014\013\005\0013\005\005\t\000\001\005\001\000\016\005\0015\007\005\t\000\001\005\001\000\003\005\0013\022\005\t\000\001\005\001\000\b\005\0016\r\005\t\000\001\005\001\000\t\005\0017\f\005\t\000\001\005\001\000\005\005\0018\020\005\t\000\001\005\001\000\t\005\0019\f\005\t\000\001\005\001\000\b\005\001:\r\005\t\000\001\005\001\000\013\005\001;\n\005\t\000\001\005\001\000\016\005\001<\007\005\t\000\001\005\001\000\006\005\001=\017\005\t\000\001\005\001\000\003\005\001>\022\005\t\000\001\005\001\000\006\005\001?\017\005\t\000\001\005\001\000\b\005\001@\r\005\t\000\001\005\001\000\0013\025\005\t\000\001\005\001\000\n\005\001A\013\005\t\000\001\005\001\000\006\005\0013\017\005\t\000\001\005\001\000\005\005\001B\020\005\t\000\001\005\001\000\016\005\001C\007\005\t\000\001\005\001\000\n\005\001D\013\005\t\000\001\005\001\000\022\005\0013\003\005\t\000\001\005\001\000\024\005\001E\001\005\t\000\001\005\001\000\004\005\001F\021\005\t\000\001\005\001\000\022\005\001G\003\005\t\000\001\005\001\000\017\005\001H\006\005\t\000\001\005\001\000\002\005\001I\023\005\t\000\001\005\001\000\013\005\0013\n\005\t\000\001\005\001\000\017\005\001J\006\005\t\000\001\005\001\000\001H\025\005\t\000\001\005\001\000\f\005\001K\t\005\t\000\001\005\001\000\001\005\001L\024\005\t\000\001\005\001\000\016\005\001M\007\005\t\000\001\005\001\000\005\005\0013\020\005\t\000\001\005\001\000\013\005\001$\n\005\t\000\001\005\001\000\002\005\001N\023\005\t\000\001\005\001\000\002\005\0018\023\005\t\000\001\005\001\000\002\005\001O\023\005\t\000\001\005\001\000\b\005\001P\r\005\t\000\001\005\001\000\025\005\001Q\t\000\001\005\001\000\003\005\001*\022\005\t\000\001\005\001\000\002\005\001R\023\005\t\000\001\005\001\000\003\005\001S\022\005\t\000\001\005\001\000\003\005\001T\022\005\t\000\001\005\001\000\001\005\001B\024\005\t\000\001\005\001\000\004\005\0013\021\005\b\000", offset, result);
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
  
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\001\001\002\000\001\t\016\001\002\t\001\001\002\t\001\001\001\t\001\001\002\t\002\001\002\t\017\001\002\t#\001";
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[84];
    int offset = 0;
    offset = zzUnpackAttribute("\001\001\002\000\001\t\016\001\002\t\001\001\002\t\001\001\001\t\001\001\002\t\002\001\002\t\017\001\002\t#\001", offset, result);
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



  
  public DockerTokenMaker() {}



  
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
    return (type == 20 || type == 6);
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









  
  public DockerTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public DockerTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 160) {
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
        case 16:
          addToken(6); continue;
        case 17:
          continue;
        case 1:
          addToken(20); continue;
        case 18:
          continue;
        case 7:
          addToken(1); addNullToken(); return (Token)this.firstToken;
        case 19:
          continue;
        case 2:
          addToken(21); continue;
        case 20:
          continue;
        case 11:
          addToken(this.start, this.zzStartRead - 1, 13); return (Token)this.firstToken;
        case 21:
          continue;
        case 14:
          addToken(this.start, this.zzStartRead - 1, 14); return (Token)this.firstToken;
        
        case 22:
        case 12:
        case 23:
          continue;
        
        case 4:
          addToken(23);
          continue;
        
        case 24:
        case 15:
        case 25:
          continue;
        case 5:
          this.start = this.zzMarkedPos - 1; yybegin(1); continue;
        case 26:
          continue;
        case 13:
          yybegin(0); addToken(this.start, this.zzStartRead, 14); continue;
        case 27:
          continue;
        case 10:
          yybegin(0); addToken(this.start, this.zzStartRead, 13); continue;
        case 28:
          continue;
        case 8:
          addNullToken(); return (Token)this.firstToken;
        
        case 29:
        case 9:
        case 30:
          continue;
        
        case 3:
          addToken(22); continue;
        case 31:
          continue;
        case 6:
          this.start = this.zzMarkedPos - 1; yybegin(2); continue;
        case 32:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 1:
            addToken(this.start, this.zzStartRead - 1, 13); return (Token)this.firstToken;
          case 85:
            continue;
          case 2:
            addToken(this.start, this.zzStartRead - 1, 14); return (Token)this.firstToken;
          case 86:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 87:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
