package org.fife.ui.rsyntaxtextarea.modes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Stack;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;
































































public class MakefileTokenMaker
  extends AbstractJFlexTokenMaker
{
  public static final int YYEOF = -1;
  public static final int VAR = 1;
  public static final int YYINITIAL = 0;
  private static final String ZZ_CMAP_PACKED = "\t\000\001\b\001\007\001\000\001\b\023\000\001\b\001\000\001\013\001\r\001\004\002\000\001\t\001\006\001'\001\000\001\016\001\000\001\037\001\001\001\000\n\002\001\003\002\000\001\017\001\000\001\016\001\000\032\001\001\000\001\n\002\000\001\001\001\f\001\020\001\032\001#\001\021\001\024\001\025\001!\001$\001\026\001%\001\001\001\035\001\034\001\033\001 \001\022\001&\001\023\001\030\001\036\001\031\001\001\001\"\001\027\002\001\001\005\001\000\001(ﾂ\000";
  private static final char[] ZZ_CMAP = zzUnpackCMap("\t\000\001\b\001\007\001\000\001\b\023\000\001\b\001\000\001\013\001\r\001\004\002\000\001\t\001\006\001'\001\000\001\016\001\000\001\037\001\001\001\000\n\002\001\003\002\000\001\017\001\000\001\016\001\000\032\001\001\000\001\n\002\000\001\001\001\f\001\020\001\032\001#\001\021\001\024\001\025\001!\001$\001\026\001%\001\001\001\035\001\034\001\033\001 \001\022\001&\001\023\001\030\001\036\001\031\001\001\001\"\001\027\002\001\001\005\001\000\001(ﾂ\000");



  
  private static final int[] ZZ_ACTION = zzUnpackAction();



  
  private static final String ZZ_ACTION_PACKED_0 = "\002\000\002\001\001\002\002\001\001\003\001\004\001\005\001\006\001\001\001\007\001\b\f\001\002\t\001\n\001\013\001\f\001\r\001\016\001\017\001\020\001\005\001\006\001\021\002\000\001\022\023\001\001\023\001\024\001\020\001\021\001\022\002\001\001\025#\001\001\025\f\001\001\025\005\001\001\000\002\001\001\000\001\001\001\000\001\025";



  
  private static int[] zzUnpackAction() {
    int[] result = new int[129];
    int offset = 0;
    offset = zzUnpackAction("\002\000\002\001\001\002\002\001\001\003\001\004\001\005\001\006\001\001\001\007\001\b\f\001\002\t\001\n\001\013\001\f\001\r\001\016\001\017\001\020\001\005\001\006\001\021\002\000\001\022\023\001\001\023\001\024\001\020\001\021\001\022\002\001\001\025#\001\001\025\f\001\001\025\005\001\001\000\002\001\001\000\001\001\001\000\001\025", offset, result);
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








  
  private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000)\000R\000{\000¤\000Í\000ö\000R\000ğ\000ň\000ű\000ƚ\000ǃ\000R\000Ǭ\000ȕ\000Ⱦ\000ɧ\000ʐ\000ʹ\000ˢ\000̋\000̴\000͝\000Ά\000ί\000Ϙ\000Ё\000Ъ\000R\000R\000R\000R\000R\000R\000ѓ\000Ѽ\000R\000ƚ\000ҥ\000R\000ӎ\000ӷ\000Ԡ\000Չ\000ղ\000֛\000ׄ\000׭\000ؖ\000ؿ\000٨\000ڑ\000ں\000ۣ\000܌\000ܵ\000ݞ\000އ\000ް\000R\000R\000ň\000ű\000ƚ\000ߙ\000ࠂ\000{\000ࠫ\000ࡔ\000ࡽ\000ࢦ\000࣏\000ࣸ\000ड\000ॊ\000ॳ\000জ\000৅\000৮\000ਗ\000ੀ\000੩\000઒\000઻\000૤\000଍\000ଶ\000ୟ\000ஈ\000ற\000௚\000ః\000బ\000ౕ\000౾\000ಧ\000೐\000೹\000ഢ\000ോ\000൴\000ඝ\000ෆ\000෯\000ธ\000แ\000๪\000ຓ\000ຼ\000໥\000༎\000༷\000འ\000ྉ\000ྲ\000࿛\000င\000ိ\000ၖ\000ၿ\000Ⴈ\000ბ\000ჺ\000ᄣ\000ᅌ\000ᅵ\000ᆞ\000R";









  
  private static int[] zzUnpackRowMap() {
    int[] result = new int[129];
    int offset = 0;
    offset = zzUnpackRowMap("\000\000\000)\000R\000{\000¤\000Í\000ö\000R\000ğ\000ň\000ű\000ƚ\000ǃ\000R\000Ǭ\000ȕ\000Ⱦ\000ɧ\000ʐ\000ʹ\000ˢ\000̋\000̴\000͝\000Ά\000ί\000Ϙ\000Ё\000Ъ\000R\000R\000R\000R\000R\000R\000ѓ\000Ѽ\000R\000ƚ\000ҥ\000R\000ӎ\000ӷ\000Ԡ\000Չ\000ղ\000֛\000ׄ\000׭\000ؖ\000ؿ\000٨\000ڑ\000ں\000ۣ\000܌\000ܵ\000ݞ\000އ\000ް\000R\000R\000ň\000ű\000ƚ\000ߙ\000ࠂ\000{\000ࠫ\000ࡔ\000ࡽ\000ࢦ\000࣏\000ࣸ\000ड\000ॊ\000ॳ\000জ\000৅\000৮\000ਗ\000ੀ\000੩\000઒\000઻\000૤\000଍\000ଶ\000ୟ\000ஈ\000ற\000௚\000ః\000బ\000ౕ\000౾\000ಧ\000೐\000೹\000ഢ\000ോ\000൴\000ඝ\000ෆ\000෯\000ธ\000แ\000๪\000ຓ\000ຼ\000໥\000༎\000༷\000འ\000ྉ\000ྲ\000࿛\000င\000ိ\000ၖ\000ၿ\000Ⴈ\000ბ\000ჺ\000ᄣ\000ᅌ\000ᅵ\000ᆞ\000R", offset, result);
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























  
  private static final String ZZ_TRANS_PACKED_0 = "\001\003\001\004\001\005\001\006\001\007\002\003\001\b\001\t\001\n\001\003\001\013\001\f\001\r\001\006\001\016\001\017\001\020\001\021\001\004\001\022\001\023\001\024\001\004\001\025\001\004\001\026\001\027\003\004\001\003\001\030\001\004\001\031\002\004\001\032\001\004\002\003\004\033\001\034\b\033\001\035\031\033\001\036\001\037*\000\002\004\001 \f\000\017\004\001\000\007\004\004\000\001\0055\000\001\016\036\000\001!\001\"*\000\001\t \000\007\n\001\000\001\n\001#\001$\036\n\007\013\001\000\002\013\001%\001&\035\013\007'\001\000\002'\001(\001'\001)\034'\007\r\001\000!\r\001\000\002\004\001 \f\000\001\004\001*\r\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001+\001\004\001,\b\004\001\000\007\004\003\000\002\004\001 \f\000\001-\016\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001.\001\004\001/\001\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\0010\b\004\001\000\0011\006\004\003\000\002\004\001 \f\000\005\004\0012\t\004\001\000\007\004\003\000\002\004\001 \f\000\t\004\0013\004\004\0014\001\000\0015\003\004\0016\002\004\003\000\002\004\001 \f\000\0017\016\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\0018\006\004\003\000\002\004\001 \f\000\003\004\0019\013\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001:\b\004\001\000\001;\006\004\003\000\002\004\001 \f\000\017\004\001\000\001<\006\004\002\000\004\033\001\000\b\033\001\000\031\033\007\000\001=\001>\"\000\007\035\001\000!\035\007\n\001\000\001\n\001?\001$\036\n\007\013\001\000\002\013\001%\001@\035\013\007'\001\000\002'\001(\001'\001A\034'\001\000\002\004\001 \f\000\001\004\001B\r\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001C\t\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001D\013\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001E\001\000\007\004\003\000\002\004\001 \f\000\001\004\001F\r\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001G\006\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001H\007\004\001I\001\004\001J\001\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001K\013\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001L\002\004\001M\006\004\001N\003\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001O\t\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001P\013\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001Q\013\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001R\n\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001S\006\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001T\001\000\007\004\003\000\002\004\001 \f\000\006\004\001U\b\004\001\000\007\004\003\000\002\004\001 \f\000\r\004\001V\001\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001W\013\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001X\b\004\001\000\007\004\003\000\002\004\001 \f\000\002\004\001Y\005\004\001Z\006\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001[\b\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\004\004\001\\\002\004\003\000\002\004\001 \f\000\004\004\001]\001\004\001]\b\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001D\n\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001^\006\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001_\r\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001`\001\000\007\004\003\000\002\004\001 \f\000\004\004\001a\n\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001]\n\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\006\004\001D\003\000\002\004\001 \f\000\001\004\001L\002\004\001M\n\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001b\t\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001c\b\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001D\001\000\007\004\003\000\002\004\001 \f\000\r\004\001d\001\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001e\n\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001f\r\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\001\004\001<\005\004\003\000\002\004\001 \f\000\001\004\001g\r\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001h\r\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001D\003\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001i\013\004\001\000\007\004\003\000\002\004\001 \f\000\t\004\0013\005\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001G\003\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001j\006\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001D\t\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001k\001\000\007\004\003\000\002\004\001 \f\000\b\004\001l\006\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001m\n\004\001\000\007\004\003\000\002\004\001 \f\000\001n\016\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001o\b\004\001\000\007\004\003\000\002\004\001 \f\000\002\004\001D\f\004\001\000\007\004\003\000\002\004\001 \f\000\r\004\001D\001\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001p\003\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001,\b\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\003\004\001q\003\004\003\000\002\004\001 \f\000\b\004\001D\006\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001O\n\004\001\000\007\004\003\000\002\004\001 \f\000\t\004\001r\005\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\002\004\001s\004\004\003\000\002\004\001 \f\000\016\004\001t\001\000\007\004\003\000\002\004\001 \f\000\003\004\001u\013\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\003\004\001v\003\004\003\000\002\004\001 \f\000\007\004\001D\007\004\001\000\007\004\003\000\002\004\001 \f\000\001w\016\004\001\000\007\004\003\000\002\004\001 \f\000\001x\016\004\001\000\007\004\003\000\002\004\001 \f\000\n\004\001y\004\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\001x\006\004\003\000\002\004\001 \f\000\003\004\001z\013\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001{\007\004\003\000\002\004\001 \f\000\017\004\001\000\004\004\001D\002\004\003\000\002\004\001 \f\000\f\004\001G\002\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001|\013\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001Q\006\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001}\b\004\001\000\007\004\"\000\001~\t\000\002\004\001 \f\000\001\004\001D\r\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001\003\004\001\000\007\004\033\000\001\020\000\002\004\001 \f\000\017\004\001\000\001\004\001D\005\004 \000\001\n\000";






















  
  private static final int ZZ_UNKNOWN_ERROR = 0;






















  
  private static final int ZZ_NO_MATCH = 1;






















  
  private static final int ZZ_PUSHBACK_2BIG = 2;























  
  private static int[] zzUnpackTrans() {
    int[] result = new int[4551];
    int offset = 0;
    offset = zzUnpackTrans("\001\003\001\004\001\005\001\006\001\007\002\003\001\b\001\t\001\n\001\003\001\013\001\f\001\r\001\006\001\016\001\017\001\020\001\021\001\004\001\022\001\023\001\024\001\004\001\025\001\004\001\026\001\027\003\004\001\003\001\030\001\004\001\031\002\004\001\032\001\004\002\003\004\033\001\034\b\033\001\035\031\033\001\036\001\037*\000\002\004\001 \f\000\017\004\001\000\007\004\004\000\001\0055\000\001\016\036\000\001!\001\"*\000\001\t \000\007\n\001\000\001\n\001#\001$\036\n\007\013\001\000\002\013\001%\001&\035\013\007'\001\000\002'\001(\001'\001)\034'\007\r\001\000!\r\001\000\002\004\001 \f\000\001\004\001*\r\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001+\001\004\001,\b\004\001\000\007\004\003\000\002\004\001 \f\000\001-\016\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001.\001\004\001/\001\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\0010\b\004\001\000\0011\006\004\003\000\002\004\001 \f\000\005\004\0012\t\004\001\000\007\004\003\000\002\004\001 \f\000\t\004\0013\004\004\0014\001\000\0015\003\004\0016\002\004\003\000\002\004\001 \f\000\0017\016\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\0018\006\004\003\000\002\004\001 \f\000\003\004\0019\013\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001:\b\004\001\000\001;\006\004\003\000\002\004\001 \f\000\017\004\001\000\001<\006\004\002\000\004\033\001\000\b\033\001\000\031\033\007\000\001=\001>\"\000\007\035\001\000!\035\007\n\001\000\001\n\001?\001$\036\n\007\013\001\000\002\013\001%\001@\035\013\007'\001\000\002'\001(\001'\001A\034'\001\000\002\004\001 \f\000\001\004\001B\r\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001C\t\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001D\013\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001E\001\000\007\004\003\000\002\004\001 \f\000\001\004\001F\r\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001G\006\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001H\007\004\001I\001\004\001J\001\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001K\013\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001L\002\004\001M\006\004\001N\003\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001O\t\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001P\013\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001Q\013\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001R\n\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001S\006\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001T\001\000\007\004\003\000\002\004\001 \f\000\006\004\001U\b\004\001\000\007\004\003\000\002\004\001 \f\000\r\004\001V\001\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001W\013\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001X\b\004\001\000\007\004\003\000\002\004\001 \f\000\002\004\001Y\005\004\001Z\006\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001[\b\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\004\004\001\\\002\004\003\000\002\004\001 \f\000\004\004\001]\001\004\001]\b\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001D\n\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001^\006\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001_\r\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001`\001\000\007\004\003\000\002\004\001 \f\000\004\004\001a\n\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001]\n\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\006\004\001D\003\000\002\004\001 \f\000\001\004\001L\002\004\001M\n\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001b\t\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001c\b\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001D\001\000\007\004\003\000\002\004\001 \f\000\r\004\001d\001\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001e\n\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001f\r\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\001\004\001<\005\004\003\000\002\004\001 \f\000\001\004\001g\r\004\001\000\007\004\003\000\002\004\001 \f\000\001\004\001h\r\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001D\003\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001i\013\004\001\000\007\004\003\000\002\004\001 \f\000\t\004\0013\005\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001G\003\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001j\006\004\001\000\007\004\003\000\002\004\001 \f\000\005\004\001D\t\004\001\000\007\004\003\000\002\004\001 \f\000\016\004\001k\001\000\007\004\003\000\002\004\001 \f\000\b\004\001l\006\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001m\n\004\001\000\007\004\003\000\002\004\001 \f\000\001n\016\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001o\b\004\001\000\007\004\003\000\002\004\001 \f\000\002\004\001D\f\004\001\000\007\004\003\000\002\004\001 \f\000\r\004\001D\001\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001p\003\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001,\b\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\003\004\001q\003\004\003\000\002\004\001 \f\000\b\004\001D\006\004\001\000\007\004\003\000\002\004\001 \f\000\004\004\001O\n\004\001\000\007\004\003\000\002\004\001 \f\000\t\004\001r\005\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\002\004\001s\004\004\003\000\002\004\001 \f\000\016\004\001t\001\000\007\004\003\000\002\004\001 \f\000\003\004\001u\013\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\003\004\001v\003\004\003\000\002\004\001 \f\000\007\004\001D\007\004\001\000\007\004\003\000\002\004\001 \f\000\001w\016\004\001\000\007\004\003\000\002\004\001 \f\000\001x\016\004\001\000\007\004\003\000\002\004\001 \f\000\n\004\001y\004\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001\000\001x\006\004\003\000\002\004\001 \f\000\003\004\001z\013\004\001\000\007\004\003\000\002\004\001 \f\000\017\004\001{\007\004\003\000\002\004\001 \f\000\017\004\001\000\004\004\001D\002\004\003\000\002\004\001 \f\000\f\004\001G\002\004\001\000\007\004\003\000\002\004\001 \f\000\003\004\001|\013\004\001\000\007\004\003\000\002\004\001 \f\000\b\004\001Q\006\004\001\000\007\004\003\000\002\004\001 \f\000\006\004\001}\b\004\001\000\007\004\"\000\001~\t\000\002\004\001 \f\000\001\004\001D\r\004\001\000\007\004\003\000\002\004\001 \f\000\013\004\001\003\004\001\000\007\004\033\000\001\020\000\002\004\001 \f\000\017\004\001\000\001\004\001D\005\004 \000\001\n\000", offset, result);
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
  
  private static final String ZZ_ATTRIBUTE_PACKED_0 = "\002\000\001\t\004\001\001\t\005\001\001\t\017\001\006\t\002\001\001\t\002\000\001\t\023\001\002\t<\001\001\000\002\001\001\000\001\001\001\000\001\t";
  
  private Reader zzReader;
  private int zzState;
  
  private static int[] zzUnpackAttribute() {
    int[] result = new int[129];
    int offset = 0;
    offset = zzUnpackAttribute("\002\000\001\t\004\001\001\t\005\001\001\t\017\001\006\t\002\001\001\t\002\000\001\t\023\001\002\t<\001\001\000\002\001\001\000\001\001\001\000\001\t", offset, result);
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


  
  private Stack<Boolean> varDepths;



  
  public MakefileTokenMaker() {}



  
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
    return (type == 20 || type == 17);
  }















  
  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    resetTokenList();
    this.offsetShift = -text.offset + startOffset;
    
    this.s = text;
    try {
      yyreset(this.zzReader);
      yybegin(0);
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









  
  public MakefileTokenMaker(Reader in) {
    this.zzReader = in;
  }






  
  public MakefileTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }






  
  private static char[] zzUnpackCMap(String packed) {
    char[] map = new char[65536];
    int i = 0;
    int j = 0;
    label10: while (i < 126) {
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
      int zzInput, temp1, temp2, zzMarkedPosL = this.zzMarkedPos;
      
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
        case 13:
          addToken(24); continue;
        case 22:
          continue;
        case 15:
          if (this.varDepths == null) { this.varDepths = new Stack<>(); } else { this.varDepths.clear(); }  this.varDepths.push(Boolean.FALSE); this.start = this.zzMarkedPos - 2; yybegin(1); continue;
        case 23:
          continue;
        case 3:
          addNullToken(); return (Token)this.firstToken;
        case 24:
          continue;
        case 16:
          addToken(14); continue;
        case 25:
          continue;
        case 12:
          if (!this.varDepths.empty() && Boolean.TRUE.equals(this.varDepths.peek())) {
            this.varDepths.pop();
            if (this.varDepths.empty()) {
              addToken(this.start, this.zzStartRead, 17); yybegin(0);
            } 
          }  continue;
        case 26:
          continue;
        case 11:
          if (!this.varDepths.empty() && Boolean.FALSE.equals(this.varDepths.peek())) {
            this.varDepths.pop();
            if (this.varDepths.empty()) {
              addToken(this.start, this.zzStartRead, 17); yybegin(0);
            } 
          }  continue;
        case 27:
          continue;
        case 4:
          addToken(21); continue;
        case 28:
          continue;
        case 21:
          addToken(6); continue;
        case 29:
          continue;
        case 20:
          this.varDepths.push(Boolean.FALSE); continue;
        case 30:
          continue;
        case 18:
          addToken(15); continue;
        case 31:
          continue;
        case 19:
          this.varDepths.push(Boolean.TRUE); continue;
        case 32:
          continue;
        case 1:
          addToken(20); continue;
        case 33:
          continue;
        case 14:
          if (this.varDepths == null) { this.varDepths = new Stack<>(); } else { this.varDepths.clear(); }  this.varDepths.push(Boolean.TRUE); this.start = this.zzMarkedPos - 2; yybegin(1); continue;
        case 34:
          continue;
        case 5:
          addToken(38); addNullToken(); return (Token)this.firstToken;
        case 35:
          continue;
        case 6:
          addToken(37); addNullToken(); return (Token)this.firstToken;
        case 36:
          continue;
        case 10:
          temp1 = this.zzStartRead; temp2 = this.zzMarkedPos; addToken(this.start, this.zzStartRead - 1, 17); addToken(temp1, temp2 - 1, 1); addNullToken(); return (Token)this.firstToken;
        case 37:
          continue;
        case 17:
          addToken(13); continue;
        case 38:
          continue;
        case 7:
          addToken(1); addNullToken(); return (Token)this.firstToken;
        case 39:
          continue;
        case 2:
          addToken(10); continue;
        case 40:
          continue;
        case 8:
          addToken(23);
          continue;
        
        case 41:
        case 9:
        case 42:
          continue;
      } 
      if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
        this.zzAtEOF = true;
        switch (this.zzLexicalState) {
          case 1:
            addToken(this.start, this.zzStartRead - 1, 17); addNullToken(); return (Token)this.firstToken;
          case 130:
            continue;
          case 0:
            addNullToken(); return (Token)this.firstToken;
          case 131:
            continue;
        } 
        return null;
      } 

      
      zzScanError(1);
    } 
  }
}
