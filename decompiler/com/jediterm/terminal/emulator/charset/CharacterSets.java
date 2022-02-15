package com.jediterm.terminal.emulator.charset;






public final class CharacterSets
{
  private static final int C0_START = 0;
  private static final int C0_END = 31;
  private static final int C1_START = 128;
  private static final int C1_END = 159;
  private static final int GL_START = 32;
  private static final int GL_END = 127;
  private static final int GR_START = 160;
  private static final int GR_END = 255;
  public static final String[] ASCII_NAMES = new String[] { "<nul>", "<soh>", "<stx>", "<etx>", "<eot>", "<enq>", "<ack>", "<bell>", "\b", "\t", "\n", "<vt>", "<ff>", "\r", "<so>", "<si>", "<dle>", "<dc1>", "<dc2>", "<dc3>", "<dc4>", "<nak>", "<syn>", "<etb>", "<can>", "<em>", "<sub>", "<esc>", "<fs>", "<gs>", "<rs>", "<us>", " ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]", "^", "_", "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "~", "<del>" };










  
  public static final Object[][] C0_CHARS = new Object[][] { { Integer.valueOf(0), "nul"
      }, { Integer.valueOf(0), "soh"
      }, { Integer.valueOf(0), "stx"
      }, { Integer.valueOf(0), "etx"
      }, { Integer.valueOf(0), "eot"
      }, { Integer.valueOf(0), "enq"
      }, { Integer.valueOf(0), "ack"
      }, { Integer.valueOf(0), "bel"
      }, { Integer.valueOf(8), "bs"
      }, { Integer.valueOf(9), "ht" }, 
      { Integer.valueOf(10), "lf"
      }, { Integer.valueOf(0), "vt"
      }, { Integer.valueOf(0), "ff"
      }, { Integer.valueOf(13), "cr"
      }, { Integer.valueOf(0), "so"
      }, { Integer.valueOf(0), "si"
      }, { Integer.valueOf(0), "dle"
      }, { Integer.valueOf(0), "dc1"
      }, { Integer.valueOf(0), "dc2"
      }, { Integer.valueOf(0), "dc3" }, 
      { Integer.valueOf(0), "dc4"
      }, { Integer.valueOf(0), "nak"
      }, { Integer.valueOf(0), "syn"
      }, { Integer.valueOf(0), "etb"
      }, { Integer.valueOf(0), "can"
      }, { Integer.valueOf(0), "em"
      }, { Integer.valueOf(0), "sub"
      }, { Integer.valueOf(0), "esq"
      }, { Integer.valueOf(0), "fs"
      }, { Integer.valueOf(0), "gs" }, 
      { Integer.valueOf(0), "rs"
      }, { Integer.valueOf(0), "us" } };



  
  public static final Object[][] C1_CHARS = new Object[][] { { Integer.valueOf(0), null
      }, { Integer.valueOf(0), null
      }, { Integer.valueOf(0), null
      }, { Integer.valueOf(0), null
      }, { Integer.valueOf(0), "ind"
      }, { Integer.valueOf(0), "nel"
      }, { Integer.valueOf(0), "ssa"
      }, { Integer.valueOf(0), "esa"
      }, { Integer.valueOf(0), "hts"
      }, { Integer.valueOf(0), "htj" }, 
      { Integer.valueOf(0), "vts"
      }, { Integer.valueOf(0), "pld"
      }, { Integer.valueOf(0), "plu"
      }, { Integer.valueOf(0), "ri"
      }, { Integer.valueOf(0), "ss2"
      }, { Integer.valueOf(0), "ss3"
      }, { Integer.valueOf(0), "dcs"
      }, { Integer.valueOf(0), "pu1"
      }, { Integer.valueOf(0), "pu2"
      }, { Integer.valueOf(0), "sts" }, 
      { Integer.valueOf(0), "cch"
      }, { Integer.valueOf(0), "mw"
      }, { Integer.valueOf(0), "spa"
      }, { Integer.valueOf(0), "epa"
      }, { Integer.valueOf(0), null
      }, { Integer.valueOf(0), null
      }, { Integer.valueOf(0), null
      }, { Integer.valueOf(0), "csi"
      }, { Integer.valueOf(0), "st"
      }, { Integer.valueOf(0), "osc" }, 
      { Integer.valueOf(0), "pm"
      }, { Integer.valueOf(0), "apc" } };




  
  public static final Object[][] DEC_SPECIAL_CHARS = new Object[][] { { Character.valueOf('◆'), null
      }, { Character.valueOf('▒'), null
      }, { Character.valueOf('␉'), null
      }, { Character.valueOf('␌'), null
      }, { Character.valueOf('␍'), null
      }, { Character.valueOf('␊'), null
      }, { Character.valueOf('°'), null
      }, { Character.valueOf('±'), null
      }, { Character.valueOf('␤'), null
      }, { Character.valueOf('␋'), null }, 
      { Character.valueOf('┘'), Character.valueOf('┛')
      }, { Character.valueOf('┐'), Character.valueOf('┓')
      }, { Character.valueOf('┌'), Character.valueOf('┏')
      }, { Character.valueOf('└'), Character.valueOf('┗')
      }, { Character.valueOf('┼'), Character.valueOf('╋')
      }, { Character.valueOf('⎺'), null
      }, { Character.valueOf('⎻'), null
      }, { Character.valueOf('─'), Character.valueOf('━')
      }, { Character.valueOf('⎼'), null
      }, { Character.valueOf('⎽'), null }, 
      { Character.valueOf('├'), Character.valueOf('┣')
      }, { Character.valueOf('┤'), Character.valueOf('┫')
      }, { Character.valueOf('┴'), Character.valueOf('┻')
      }, { Character.valueOf('┬'), Character.valueOf('┳')
      }, { Character.valueOf('│'), Character.valueOf('┃')
      }, { Character.valueOf('≤'), null
      }, { Character.valueOf('≥'), null
      }, { Character.valueOf('π'), null
      }, { Character.valueOf('≠'), null
      }, { Character.valueOf('£'), null }, 
      { Character.valueOf('·'), null
      }, { Character.valueOf(' '), null } };

  
  public static boolean isDecBoxChar(char c) {
    if (c < '─' || c >= '▀') {
      return false;
    }
    for (Object[] o : DEC_SPECIAL_CHARS) {
      if (c == ((Character)o[0]).charValue()) {
        return true;
      }
    } 
    return false;
  }
  
  public static char getHeavyDecBoxChar(char c) {
    if (c < '─' || c >= '▀') {
      return c;
    }
    for (Object[] o : DEC_SPECIAL_CHARS) {
      if (c == ((Character)o[0]).charValue()) {
        return (o[1] != null) ? ((Character)o[1]).charValue() : c;
      }
    } 
    return c;
  }


















  
  public static char getChar(char original, GraphicSet gl, GraphicSet gr) {
    Object[] mapping = getMapping(original, gl, gr);
    
    int ch = ((Integer)mapping[0]).intValue();
    if (ch > 0) {
      return (char)ch;
    }
    
    return Character.MIN_VALUE;
  }









  
  public static String getCharName(char original, GraphicSet gl, GraphicSet gr) {
    Object[] mapping = getMapping(original, gl, gr);
    
    String name = (String)mapping[1];
    if (name == null) {
      name = String.format("<%d>", new Object[] { Integer.valueOf(original) });
    }
    
    return name;
  }









  
  private static Object[] getMapping(char original, GraphicSet gl, GraphicSet gr) {
    int mappedChar = original;
    if (original >= '\000' && original <= '\037') {
      int idx = original - 0;
      return C0_CHARS[idx];
    } 
    if (original >= '' && original <= '') {
      int idx = original - 128;
      return C1_CHARS[idx];
    } 
    if (original >= ' ' && original <= '') {
      int idx = original - 32;
      mappedChar = gl.map(original, idx);
    } 







    
    return new Object[] { Integer.valueOf(mappedChar), null };
  }
}
