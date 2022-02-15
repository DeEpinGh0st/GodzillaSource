package com.jediterm.terminal.util;

import com.jediterm.terminal.emulator.charset.CharacterSets;
import com.jediterm.terminal.model.CharBuffer;
import java.util.Arrays;













public class CharUtils
{
  public static final int ESC = 27;
  public static final int DEL = 127;
  public static final char NUL_CHAR = '\000';
  public static final char EMPTY_CHAR = ' ';
  public static final char DWC = '';
  private static final String[] NONPRINTING_NAMES = new String[] { "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "TAB", "LF", "VT", "FF", "CR", "S0", "S1", "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB", "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US" };



  
  public static byte[] VT102_RESPONSE = makeCode(new int[] { 27, 91, 63, 54, 99 });
  
  public static String getNonControlCharacters(int maxChars, char[] buf, int offset, int charsLength) {
    int len = Math.min(maxChars, charsLength);
    
    int origLen = len;
    
    while (len > 0) {
      char tmp = buf[offset++];
      if (' ' <= tmp) {
        len--;
        continue;
      } 
      offset--;
    } 

    
    int length = origLen - len;
    
    return new String(buf, offset - length, length);
  }
  
  public static int countDoubleWidthCharacters(char[] buf, int start, int length, boolean ambiguousIsDWC) {
    int cnt = 0;
    for (int i = 0; i < length; i++) {
      int ucs = Character.codePointAt(buf, i + start);
      if (isDoubleWidthCharacter(ucs, ambiguousIsDWC)) {
        cnt++;
      }
    } 
    
    return cnt;
  }
  
  public enum CharacterType {
    NONPRINTING,
    PRINTING,
    NONASCII, NONE;
  }
  
  public static CharacterType appendChar(StringBuilder sb, CharacterType last, char c) {
    if (c <= '\037') {
      sb.append(' ');
      sb.append(NONPRINTING_NAMES[c]);
      return CharacterType.NONPRINTING;
    }  if (c == '') {
      sb.append(" DEL");
      return CharacterType.NONPRINTING;
    }  if (c > '\037' && c <= '~') {
      if (last != CharacterType.PRINTING) sb.append(' '); 
      sb.append(c);
      return CharacterType.PRINTING;
    } 
    sb.append(" 0x").append(Integer.toHexString(c));
    return CharacterType.NONASCII;
  }

  
  public static void appendBuf(StringBuilder sb, char[] bs, int begin, int length) {
    CharacterType last = CharacterType.NONPRINTING;
    int end = begin + length;
    for (int i = begin; i < end; i++) {
      char c = bs[i];
      last = appendChar(sb, last, c);
    } 
  }

  
  public static byte[] makeCode(int... bytesAsInt) {
    byte[] bytes = new byte[bytesAsInt.length];
    int i = 0;
    for (int byteAsInt : bytesAsInt) {
      bytes[i] = (byte)byteAsInt;
      i++;
    } 
    return bytes;
  }




  
  public static int getTextLengthDoubleWidthAware(char[] buffer, int start, int length, boolean ambiguousIsDWC) {
    int result = 0;
    for (int i = start; i < start + length; i++) {
      result += (buffer[i] != '' && isDoubleWidthCharacter(buffer[i], ambiguousIsDWC) && (i + 1 >= start + length || buffer[i + 1] != '')) ? 2 : 1;
    }
    return result;
  }
  
  public static boolean isDoubleWidthCharacter(int c, boolean ambiguousIsDWC) {
    if (c == 57344 || c <= 160 || (c > 1106 && c < 4352)) {
      return false;
    }
    
    return (mk_wcwidth(c, ambiguousIsDWC) == 2);
  }

  
  public static CharBuffer heavyDecCompatibleBuffer(CharBuffer buf) {
    char[] c = Arrays.copyOfRange(buf.getBuf(), 0, (buf.getBuf()).length);
    for (int i = 0; i < c.length; i++) {
      c[i] = CharacterSets.getHeavyDecBoxChar(c[i]);
    }
    return new CharBuffer(c, buf.getStart(), buf.length());
  }





  
  private static final char[][] COMBINING = new char[][] { { '̀', 'ͯ' }, { '҃', '҆' }, { '҈', '҉' }, { '֑', 'ֽ' }, { 'ֿ', 'ֿ' }, { 'ׁ', 'ׂ' }, { 'ׄ', 'ׅ' }, { 'ׇ', 'ׇ' }, { '؀', '؃' }, { 'ؐ', 'ؕ' }, { 'ً', 'ٞ' }, { 'ٰ', 'ٰ' }, { 'ۖ', 'ۤ' }, { 'ۧ', 'ۨ' }, { '۪', 'ۭ' }, { '܏', '܏' }, { 'ܑ', 'ܑ' }, { 'ܰ', '݊' }, { 'ަ', 'ް' }, { '߫', '߳' }, { 'ँ', 'ं' }, { '़', '़' }, { 'ु', 'ै' }, { '्', '्' }, { '॑', '॔' }, { 'ॢ', 'ॣ' }, { 'ঁ', 'ঁ' }, { '়', '়' }, { 'ু', 'ৄ' }, { '্', '্' }, { 'ৢ', 'ৣ' }, { 'ਁ', 'ਂ' }, { '਼', '਼' }, { 'ੁ', 'ੂ' }, { 'ੇ', 'ੈ' }, { 'ੋ', '੍' }, { 'ੰ', 'ੱ' }, { 'ઁ', 'ં' }, { '઼', '઼' }, { 'ુ', 'ૅ' }, { 'ે', 'ૈ' }, { '્', '્' }, { 'ૢ', 'ૣ' }, { 'ଁ', 'ଁ' }, { '଼', '଼' }, { 'ି', 'ି' }, { 'ୁ', 'ୃ' }, { '୍', '୍' }, { 'ୖ', 'ୖ' }, { 'ஂ', 'ஂ' }, { 'ீ', 'ீ' }, { '்', '்' }, { 'ా', 'ీ' }, { 'ె', 'ై' }, { 'ొ', '్' }, { 'ౕ', 'ౖ' }, { '಼', '಼' }, { 'ಿ', 'ಿ' }, { 'ೆ', 'ೆ' }, { 'ೌ', '್' }, { 'ೢ', 'ೣ' }, { 'ു', 'ൃ' }, { '്', '്' }, { '්', '්' }, { 'ි', 'ු' }, { 'ූ', 'ූ' }, { 'ั', 'ั' }, { 'ิ', 'ฺ' }, { '็', '๎' }, { 'ັ', 'ັ' }, { 'ິ', 'ູ' }, { 'ົ', 'ຼ' }, { '່', 'ໍ' }, { '༘', '༙' }, { '༵', '༵' }, { '༷', '༷' }, { '༹', '༹' }, { 'ཱ', 'ཾ' }, { 'ྀ', '྄' }, { '྆', '྇' }, { 'ྐ', 'ྗ' }, { 'ྙ', 'ྼ' }, { '࿆', '࿆' }, { 'ိ', 'ူ' }, { 'ဲ', 'ဲ' }, { 'ံ', '့' }, { '္', '္' }, { 'ၘ', 'ၙ' }, { 'ᅠ', 'ᇿ' }, { '፟', '፟' }, { 'ᜒ', '᜔' }, { 'ᜲ', '᜴' }, { 'ᝒ', 'ᝓ' }, { 'ᝲ', 'ᝳ' }, { '឴', '឵' }, { 'ិ', 'ួ' }, { 'ំ', 'ំ' }, { '៉', '៓' }, { '៝', '៝' }, { '᠋', '᠍' }, { 'ᢩ', 'ᢩ' }, { 'ᤠ', 'ᤢ' }, { 'ᤧ', 'ᤨ' }, { 'ᤲ', 'ᤲ' }, { '᤹', '᤻' }, { 'ᨗ', 'ᨘ' }, { 'ᬀ', 'ᬃ' }, { '᬴', '᬴' }, { 'ᬶ', 'ᬺ' }, { 'ᬼ', 'ᬼ' }, { 'ᭂ', 'ᭂ' }, { '᭫', '᭳' }, { '᷀', '᷊' }, { '᷾', '᷿' }, { '​', '‏' }, { '‪', '‮' }, { '⁠', '⁣' }, { '⁪', '⁯' }, { '⃐', '⃯' }, { '〪', '〯' }, { '゙', '゚' }, { '꠆', '꠆' }, { 'ꠋ', 'ꠋ' }, { 'ꠥ', 'ꠦ' }, { 'ﬞ', 'ﬞ' }, { '︀', '️' }, { '︠', '︣' }, { '﻿', '﻿' }, { '￹', '￻' } };












































  
  private static final char[][] AMBIGUOUS = new char[][] { { '¡', '¡' }, { '¤', '¤' }, { '§', '¨' }, { 'ª', 'ª' }, { '®', '®' }, { '°', '´' }, { '¶', 'º' }, { '¼', '¿' }, { 'Æ', 'Æ' }, { 'Ð', 'Ð' }, { '×', 'Ø' }, { 'Þ', 'á' }, { 'æ', 'æ' }, { 'è', 'ê' }, { 'ì', 'í' }, { 'ð', 'ð' }, { 'ò', 'ó' }, { '÷', 'ú' }, { 'ü', 'ü' }, { 'þ', 'þ' }, { 'ā', 'ā' }, { 'đ', 'đ' }, { 'ē', 'ē' }, { 'ě', 'ě' }, { 'Ħ', 'ħ' }, { 'ī', 'ī' }, { 'ı', 'ĳ' }, { 'ĸ', 'ĸ' }, { 'Ŀ', 'ł' }, { 'ń', 'ń' }, { 'ň', 'ŋ' }, { 'ō', 'ō' }, { 'Œ', 'œ' }, { 'Ŧ', 'ŧ' }, { 'ū', 'ū' }, { 'ǎ', 'ǎ' }, { 'ǐ', 'ǐ' }, { 'ǒ', 'ǒ' }, { 'ǔ', 'ǔ' }, { 'ǖ', 'ǖ' }, { 'ǘ', 'ǘ' }, { 'ǚ', 'ǚ' }, { 'ǜ', 'ǜ' }, { 'ɑ', 'ɑ' }, { 'ɡ', 'ɡ' }, { '˄', '˄' }, { 'ˇ', 'ˇ' }, { 'ˉ', 'ˋ' }, { 'ˍ', 'ˍ' }, { 'ː', 'ː' }, { '˘', '˛' }, { '˝', '˝' }, { '˟', '˟' }, { 'Α', 'Ρ' }, { 'Σ', 'Ω' }, { 'α', 'ρ' }, { 'σ', 'ω' }, { 'Ё', 'Ё' }, { 'А', 'я' }, { 'ё', 'ё' }, { '‐', '‐' }, { '–', '‖' }, { '‘', '’' }, { '“', '”' }, { '†', '•' }, { '․', '‧' }, { '‰', '‰' }, { '′', '″' }, { '‵', '‵' }, { '※', '※' }, { '‾', '‾' }, { '⁴', '⁴' }, { 'ⁿ', 'ⁿ' }, { '₁', '₄' }, { '€', '€' }, { '℃', '℃' }, { '℅', '℅' }, { '℉', '℉' }, { 'ℓ', 'ℓ' }, { '№', '№' }, { '℡', '™' }, { 'Ω', 'Ω' }, { 'Å', 'Å' }, { '⅓', '⅔' }, { '⅛', '⅞' }, { 'Ⅰ', 'Ⅻ' }, { 'ⅰ', 'ⅹ' }, { '←', '↙' }, { '↸', '↹' }, { '⇒', '⇒' }, { '⇔', '⇔' }, { '⇧', '⇧' }, { '∀', '∀' }, { '∂', '∃' }, { '∇', '∈' }, { '∋', '∋' }, { '∏', '∏' }, { '∑', '∑' }, { '∕', '∕' }, { '√', '√' }, { '∝', '∠' }, { '∣', '∣' }, { '∥', '∥' }, { '∧', '∬' }, { '∮', '∮' }, { '∴', '∷' }, { '∼', '∽' }, { '≈', '≈' }, { '≌', '≌' }, { '≒', '≒' }, { '≠', '≡' }, { '≤', '≧' }, { '≪', '≫' }, { '≮', '≯' }, { '⊂', '⊃' }, { '⊆', '⊇' }, { '⊕', '⊕' }, { '⊙', '⊙' }, { '⊥', '⊥' }, { '⊿', '⊿' }, { '⌒', '⌒' }, { '①', 'ⓩ' }, { '⓫', '╋' }, { '═', '╳' }, { '▀', '▏' }, { '▒', '▕' }, { '■', '□' }, { '▣', '▩' }, { '▲', '△' }, { '▶', '▷' }, { '▼', '▽' }, { '◀', '◁' }, { '◆', '◈' }, { '○', '○' }, { '◎', '◑' }, { '◢', '◥' }, { '◯', '◯' }, { '★', '☆' }, { '☉', '☉' }, { '☎', '☏' }, { '☔', '☕' }, { '☜', '☜' }, { '☞', '☞' }, { '♀', '♀' }, { '♂', '♂' }, { '♠', '♡' }, { '♣', '♥' }, { '♧', '♪' }, { '♬', '♭' }, { '♯', '♯' }, { '✽', '✽' }, { '❶', '❿' }, { '', '' }, { '�', '�' } };






















































  
  static int bisearch(char ucs, char[][] table, int max) {
    int min = 0;

    
    if (ucs < table[0][0] || ucs > table[max][1])
      return 0; 
    while (max >= min) {
      int mid = (min + max) / 2;
      if (ucs > table[mid][1]) {
        min = mid + 1; continue;
      }  if (ucs < table[mid][0]) {
        max = mid - 1; continue;
      } 
      return 1;
    } 
    
    return 0;
  }




  
  private static int mk_wcwidth(int ucs, boolean ambiguousIsDoubleWidth) {
    if (ucs == 0)
      return 0; 
    if (ucs < 32 || (ucs >= 127 && ucs < 160)) {
      return -1;
    }
    if (ambiguousIsDoubleWidth && 
      bisearch((char)ucs, AMBIGUOUS, AMBIGUOUS.length - 1) > 0) {
      return 2;
    }



    
    if (bisearch((char)ucs, COMBINING, COMBINING.length - 1) > 0) {
      return 0;
    }


    
    return 1 + ((ucs >= 4352 && (ucs <= 4447 || ucs == 9001 || ucs == 9002 || (ucs >= 11904 && ucs <= 42191 && ucs != 12351) || (ucs >= 44032 && ucs <= 55203) || (ucs >= 63744 && ucs <= 64255) || (ucs >= 65040 && ucs <= 65049) || (ucs >= 65072 && ucs <= 65135) || (ucs >= 65280 && ucs <= 65376) || (ucs >= 65504 && ucs <= 65510) || (ucs >= 131072 && ucs <= 196605) || (ucs >= 196608 && ucs <= 262141))) ? 1 : 0);
  }
}
