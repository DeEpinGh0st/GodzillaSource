package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.OccurrenceMarker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;
import org.fife.ui.rsyntaxtextarea.XmlOccurrenceMarker;






























































public class XMLTokenMaker
  extends AbstractMarkupTokenMaker
{
  public static final int YYEOF = -1;
  public static final int INTAG = 4;
  public static final int DTD = 3;
  public static final int INATTR_DOUBLE = 5;
  public static final int YYINITIAL = 0;
  public static final int COMMENT = 1;
  public static final int CDATA = 7;
  public static final int INATTR_SINGLE = 6;
  public static final int PI = 2;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\006\001\004\001\000\001\003\023\000\001\006\001\n\001\t\001\022\001\030\001\022\001\007\001\025\005\022\001\002\001\"\001\024\n\027\001\023\001\b\001\005\001$\001\021\001#\001\022\001\016\001\026\001\f\001\r\017\026\001\017\006\026\001\013\001\000\001\020\001\000\001\001\001\000\004\026\001 \001\035\001\026\001\031\001\036\002\026\001\037\003\026\001\033\002\026\001\034\001\032\002\026\001!\003\026\003\000\001\022ﾁ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\006\001\004\001\000\001\003\023\000\001\006\001\n\001\t\001\022\001\030\001\022\001\007\001\025\005\022\001\002\001\"\001\024\n\027\001\023\001\b\001\005\001$\001\021\001#\001\022\001\016\001\026\001\f\001\r\017\026\001\017\006\026\001\013\001\000\001\020\001\000\001\001\001\000\004\026\001 \001\035\001\026\001\031\001\036\002\026\001\037\003\026\001\033\002\026\001\034\001\032\002\026\001!\003\026\003\000\001\022ﾁ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();



  
  private static final String ZZ_ACTION_PACKED_0 = "\005\000\002\001\001\000\002\002\001\003\001\004\001\005\001\006\002\001\001\007\004\001\001\b\002\001\001\t\001\001\001\n\001\013\001\f\002\r\001\016\001\017\001\020\001\021\001\022\001\001\001\023\003\001\001\024\001\025\001\004\001\026\001\006\005\000\001\027\004\000\001\030\001\031\005\000\001\032\001\033\003\000\001\034\001\035\006\000\001\036";



  
  private static int[] zzUnpackAction() {
    int[] result = new int[77];
    int offset = 0;
    offset = zzUnpackAction("\005\000\002\001\001\000\002\002\001\003\001\004\001\005\001\006\002\001\001\007\004\001\001\b\002\001\001\t\001\001\001\n\001\013\001\f\002\r\001\016\001\017\001\020\001\021\001\022\001\001\001\023\003\001\001\024\001\025\001\004\001\026\001\006\005\000\001\027\004\000\001\030\001\031\005\000\001\032\001\033\003\000\001\034\001\035\006\000\001\036", offset, result);
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





  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000%\000J\000o\000\000¹\000Þ\000ă\000Ĩ\000ō\000Ų\000Ɨ\000Ƽ\000ǡ\000Ȇ\000ȫ\000Ų\000ɐ\000ɵ\000ʚ\000ʿ\000Ų\000ˤ\000̉\000Ų\000̮\000Ų\000Ų\000Ų\000͓\000͸\000Ų\000Ų\000Ν\000Ų\000Ų\000ς\000Ų\000ϧ\000Ќ\000б\000і\000ѻ\000Ҡ\000Ų\000Ų\000Ӆ\000Ӫ\000ԏ\000Դ\000ՙ\000Ų\000վ\000֣\000׈\000׭\000ؒ\000Ų\000ط\000ٜ\000ځ\000ڦ\000ۋ\000Ų\000Ų\000۰\000ܕ\000ܺ\000ݟ\000Ų\000ބ\000ީ\000ݟ\000ߎ\000߳\000࠘\000Ų";





  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[77];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000%\000J\000o\000\000¹\000Þ\000ă\000Ĩ\000ō\000Ų\000Ɨ\000Ƽ\000ǡ\000Ȇ\000ȫ\000Ų\000ɐ\000ɵ\000ʚ\000ʿ\000Ų\000ˤ\000̉\000Ų\000̮\000Ų\000Ų\000Ų\000͓\000͸\000Ų\000Ų\000Ν\000Ų\000Ų\000ς\000Ų\000ϧ\000Ќ\000б\000і\000ѻ\000Ҡ\000Ų\000Ų\000Ӆ\000Ӫ\000ԏ\000Դ\000ՙ\000Ų\000վ\000֣\000׈\000׭\000ؒ\000Ų\000ط\000ٜ\000ځ\000ڦ\000ۋ\000Ų\000Ų\000۰\000ܕ\000ܺ\000ݟ\000Ų\000ބ\000ީ\000ݟ\000ߎ\000߳\000࠘\000Ų", offset, result);
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






  
  private static final String ZZ_TRANS_PACKED_0 = "\003\t\001\n\001\013\001\f\001\r\001\016\035\t\002\017\001\020\001\017\001\021\024\017\001\022\003\017\001\023\003\017\001\024\003\017\004\025\001\026\036\025\001\027\001\025\004\030\001\031\001\032\005\030\001\033\004\030\001\034\001\035\023\030\003\036\001\037\001\000\001\036\001\r\002\036\001 \007\036\001!\002\036\001\"\001#\016\036\001$\t%\001&\033%\025'\001&\017'\020(\001)\024(\004\t\004\000 \t\001\n\002\000\001\r\001\000\035\t&\000\001*\b\000\001+\001\000\004*\003\000\001*\001,\001\000\001*\002\000\t*\001\000\001-\004\000\001\r\002\000\001\r\036\000\006\016\001\000\001\016\001.\034\016\002\017\001\000\001\017\001\000\024\017\001\000\003\017\001\000\003\017\001\000\003\017\002\000\001/<\000\0010$\000\0011\003\000\0012'\000\0013\003\000\004\025\001\000\036\025\001\000\001\025\021\000\0014\023\000\004\030\002\000\005\030\001\000\004\030\002\000\023\030\n\000\0015\032\000\004\036\001\000\001\036\001\000\002\036\001\000\007\036\001\000\002\036\002\000\016\036\001\000\003\036\001\037\001\000\001\036\001\r\002\036\001\000\007\036\001\000\002\036\002\000\016\036\022\000\001!\023\000\t%\001\000\033%\025'\001\000\017'\020(\001\000\024(\020\000\0016\025\000\002*\t\000\004*\003\000\001*\002\000\002*\001\000\n*\004\000\0017\b\000\0018\032\000\0019\n\000\0049\003\000\0019\002\000\0019\002\000\t9\024\000\001:-\000\001;%\000\001<(\000\001=&\000\001>\005\000\001?3\000\001@\025\000\001A.\000\001B\031\000\0029\t\000\0049\003\000\0019\002\000\0029\001\000\n9\035\000\001C\034\000\001D1\000\001<&\000\001E\004\000\001F/\000\001G*\000\001D\b\000\001<\034\000\001H\021\000\002I\004\000\002I\001\000\002I\004E\001I\001\000\002I\001E\001I\fE\003I\016\000\001J*\000\001E\037\000\001K#\000\001L!\000\001M\031\000";





  
  private static final int ZZ_UNKNOWN_ERROR = 0;





  
  private static final int ZZ_NO_MATCH = 1;





  
  private static final int ZZ_PUSHBACK_2BIG = 2;






  
  private static int[] zzUnpackTrans() {
    int[] result = new int[2109];
    int offset = 0;
    offset = zzUnpackTrans("\003\t\001\n\001\013\001\f\001\r\001\016\035\t\002\017\001\020\001\017\001\021\024\017\001\022\003\017\001\023\003\017\001\024\003\017\004\025\001\026\036\025\001\027\001\025\004\030\001\031\001\032\005\030\001\033\004\030\001\034\001\035\023\030\003\036\001\037\001\000\001\036\001\r\002\036\001 \007\036\001!\002\036\001\"\001#\016\036\001$\t%\001&\033%\025'\001&\017'\020(\001)\024(\004\t\004\000 \t\001\n\002\000\001\r\001\000\035\t&\000\001*\b\000\001+\001\000\004*\003\000\001*\001,\001\000\001*\002\000\t*\001\000\001-\004\000\001\r\002\000\001\r\036\000\006\016\001\000\001\016\001.\034\016\002\017\001\000\001\017\001\000\024\017\001\000\003\017\001\000\003\017\001\000\003\017\002\000\001/<\000\0010$\000\0011\003\000\0012'\000\0013\003\000\004\025\001\000\036\025\001\000\001\025\021\000\0014\023\000\004\030\002\000\005\030\001\000\004\030\002\000\023\030\n\000\0015\032\000\004\036\001\000\001\036\001\000\002\036\001\000\007\036\001\000\002\036\002\000\016\036\001\000\003\036\001\037\001\000\001\036\001\r\002\036\001\000\007\036\001\000\002\036\002\000\016\036\022\000\001!\023\000\t%\001\000\033%\025'\001\000\017'\020(\001\000\024(\020\000\0016\025\000\002*\t\000\004*\003\000\001*\002\000\002*\001\000\n*\004\000\0017\b\000\0018\032\000\0019\n\000\0049\003\000\0019\002\000\0019\002\000\t9\024\000\001:-\000\001;%\000\001<(\000\001=&\000\001>\005\000\001?3\000\001@\025\000\001A.\000\001B\031\000\0029\t\000\0049\003\000\0019\002\000\0029\001\000\n9\035\000\001C\034\000\001D1\000\001<&\000\001E\004\000\001F/\000\001G*\000\001D\b\000\001<\034\000\001H\021\000\002I\004\000\002I\001\000\002I\004E\001I\001\000\002I\001E\001I\fE\003I\016\000\001J*\000\001E\037\000\001K#\000\001L!\000\001M\031\000", offset, result);
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
  
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\005\000\002\001\001\000\002\001\001\t\005\001\001\t\004\001\001\t\002\001\001\t\001\001\003\t\002\001\002\t\001\001\002\t\001\001\001\t\006\001\002\t\005\000\001\t\004\000\001\001\001\t\005\000\002\t\003\000\001\001\001\t\006\000\001\t";
  
  private Reader zzReader;
  
  private int zzState;

  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[77];
    int offset = 0;
    offset = zzUnpackAttribute("\005\000\002\001\001\000\002\001\001\t\005\001\001\t\004\001\001\t\002\001\001\t\001\001\003\t\002\001\002\t\001\001\002\t\001\001\001\t\006\001\002\t\005\000\001\t\004\000\001\001\001\t\005\000\002\t\003\000\001\001\001\t\006\000\001\t", offset, result);
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




  
  public static final int INTERNAL_ATTR_DOUBLE = -1;




  
  public static final int INTERNAL_ATTR_SINGLE = -2;




  
  public static final int INTERNAL_INTAG = -3;




  
  public static final int INTERNAL_DTD = -4;




  
  public static final int INTERNAL_DTD_INTERNAL = -5;




  
  public static final int INTERNAL_IN_XML_COMMENT = -2048;




  
  private static boolean completeCloseTags = true;



  
  private boolean inInternalDtd;



  
  private int prevState;




  
  public XMLTokenMaker() {}




  
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





  
  protected OccurrenceMarker createOccurrenceMarker() {
    return (OccurrenceMarker)new XmlOccurrenceMarker();
  }









  
  public boolean getCompleteCloseTags() {
    return completeCloseTags;
  }









  
  public static boolean getCompleteCloseMarkupTags() {
    return completeCloseTags;
  }









  
  public boolean getMarkOccurrencesOfTokenType(int type) {
    return (type == 26);
  }















  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;
    this.prevState = 0;
    this.inInternalDtd = false;

    
    int state = 0;
    switch (initialTokenType) {
      case 29:
        state = 1;
        break;
      case -4:
        state = 3;
        break;
      case -5:
        state = 3;
        this.inInternalDtd = true;
        break;
      case -1:
        state = 5;
        break;
      case -2:
        state = 6;
        break;
      case 31:
        state = 2;
        break;
      case -3:
        state = 4;
        break;
      case 33:
        state = 7;
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









  
  public XMLTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public XMLTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 116) {
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
      int zzInput, k, j, i, count, temp, zzMarkedPosL = this.zzMarkedPos;
      
      int zzAction = -1;
      
      int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
      
      this.zzState = this.zzLexicalState;



      
      while (true) {
        if (zzCurrentPosL < zzEndReadL)
        { zzInput = zzBufferL[zzCurrentPosL++]; }
        else { if (this.zzAtEOF) {
            int m = -1;
            
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
            int m = -1;
            
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
        case 25:
          k = this.zzMarkedPos; addToken(this.start, this.zzStartRead + 2, 29); this.start = k; yybegin(this.prevState); continue;
        case 31:
          continue;
        case 19:
          yybegin(4); addToken(this.start, this.zzStartRead, 28); continue;
        case 32:
          continue;
        case 3:
          addNullToken(); return (Token)this.firstToken;
        case 33:
          continue;
        case 29:
          k = this.zzStartRead; addToken(this.start, this.zzStartRead - 1, 30); this.start = k; this.prevState = this.zzLexicalState; yybegin(1); continue;
        case 34:
          continue;
        case 11:
          this.inInternalDtd = false; continue;
        case 35:
          continue;
        case 4:
          addToken(25); yybegin(4); continue;
        case 36:
          continue;
        case 24:
          j = yylength();
          addToken(this.zzStartRead, this.zzStartRead + 1, 25);
          addToken(this.zzMarkedPos - j - 2, this.zzMarkedPos - 1, 26);
          yybegin(4); continue;
        case 37:
          continue;
        case 9:
          addToken(this.start, this.zzStartRead - 1, 30); addEndToken(this.inInternalDtd ? -5 : -4); return (Token)this.firstToken;
        case 38:
          continue;
        case 16:
          addToken(25); continue;
        case 39:
          continue;
        case 7:
          addToken(this.start, this.zzStartRead - 1, 29); addEndToken(-2048 - this.prevState); return (Token)this.firstToken;
        case 40:
          continue;
        case 5:
          addToken(21); continue;
        case 41:
          continue;
        case 27:
          this.start = this.zzStartRead; this.prevState = this.zzLexicalState; yybegin(1); continue;
        case 42:
          continue;
        case 26:
          i = this.zzStartRead; yybegin(0); addToken(this.start, this.zzStartRead - 1, 33); addToken(i, this.zzMarkedPos - 1, 32); continue;
        case 43:
          continue;
        case 6:
          addToken(34); continue;
        case 44:
          continue;
        case 12:
          if (!this.inInternalDtd) { yybegin(0); addToken(this.start, this.zzStartRead, 30); }  continue;
        case 45:
          continue;
        case 2:
          addToken(20); continue;
        case 46:
          continue;
        case 10:
          this.inInternalDtd = true; continue;
        case 47:
          continue;
        case 23:
          yybegin(0); addToken(this.start, this.zzStartRead + 1, 31); continue;
        case 48:
          continue;
        case 21:
          this.start = this.zzMarkedPos - 2; this.inInternalDtd = false; yybegin(3); continue;
        case 49:
          continue;
        case 20:
          count = yylength();
          addToken(this.zzStartRead, this.zzStartRead, 25);
          addToken(this.zzMarkedPos - count - 1, this.zzMarkedPos - 1, 26);
          yybegin(4); continue;
        case 50:
          continue;
        case 22:
          this.start = this.zzMarkedPos - 2; yybegin(2); continue;
        case 51:
          continue;
        case 8:
          addToken(this.start, this.zzStartRead - 1, 31); return (Token)this.firstToken;
        case 52:
          continue;
        case 14:
          this.start = this.zzMarkedPos - 1; yybegin(5); continue;
        case 53:
          continue;
        case 28:
          temp = this.zzStartRead; addToken(this.start, this.zzStartRead - 1, 29); addHyperlinkToken(temp, this.zzMarkedPos - 1, 29); this.start = this.zzMarkedPos; continue;
        case 54:
          continue;
        case 15:
          yybegin(0); addToken(25); continue;
        case 55:
          continue;
        case 17:
          this.start = this.zzMarkedPos - 1; yybegin(6); continue;
        case 56:
          continue;
        case 18:
          addToken(23); continue;
        case 57:
          continue;
        case 30:
          addToken(32); this.start = this.zzMarkedPos; yybegin(7); continue;
        case 58:
          continue;
        case 13:
          addToken(27);
          continue;
        
        case 59:
        case 1:
        case 60:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 4:
            addToken(this.start, this.zzStartRead - 1, -3); return (Token)this.firstToken;
          case 78:
            continue;
          case 3:
            addToken(this.start, this.zzStartRead - 1, 30); addEndToken(this.inInternalDtd ? -5 : -4); return (Token)this.firstToken;
          case 79:
            continue;
          case 5:
            addToken(this.start, this.zzStartRead - 1, 28); addEndToken(-1); return (Token)this.firstToken;
          case 80:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 81:
            continue;
          case 1:
            addToken(this.start, this.zzStartRead - 1, 29); addEndToken(-2048 - this.prevState); return (Token)this.firstToken;
          case 82:
            continue;
          case 7:
            addToken(this.start, this.zzStartRead - 1, 33); return (Token)this.firstToken;
          case 83:
            continue;
          case 6:
            addToken(this.start, this.zzStartRead - 1, 28); addEndToken(-2); return (Token)this.firstToken;
          case 84:
            continue;
          case 2:
            addToken(this.start, this.zzStartRead - 1, 31); return (Token)this.firstToken;
          case 85:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
