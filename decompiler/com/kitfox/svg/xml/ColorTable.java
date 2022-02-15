package com.kitfox.svg.xml;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;







































public class ColorTable
{
  static final Map<String, Color> colorTable;
  
  static {
    HashMap<String, Color> table = new HashMap<String, Color>();



    
    table.put("currentcolor", new Color(0));
    
    table.put("aliceblue", new Color(15792383));
    table.put("antiquewhite", new Color(16444375));
    table.put("aqua", new Color(65535));
    table.put("aquamarine", new Color(8388564));
    table.put("azure", new Color(15794175));
    table.put("beige", new Color(16119260));
    table.put("bisque", new Color(16770244));
    table.put("black", new Color(0));
    table.put("blanchedalmond", new Color(16772045));
    table.put("blue", new Color(255));
    table.put("blueviolet", new Color(9055202));
    table.put("brown", new Color(10824234));
    table.put("burlywood", new Color(14596231));
    table.put("cadetblue", new Color(6266528));
    table.put("chartreuse", new Color(8388352));
    table.put("chocolate", new Color(13789470));
    table.put("coral", new Color(16744272));
    table.put("cornflowerblue", new Color(6591981));
    table.put("cornsilk", new Color(16775388));
    table.put("crimson", new Color(14423100));
    table.put("cyan", new Color(65535));
    table.put("darkblue", new Color(139));
    table.put("darkcyan", new Color(35723));
    table.put("darkgoldenrod", new Color(12092939));
    table.put("darkgray", new Color(11119017));
    table.put("darkgreen", new Color(25600));
    table.put("darkkhaki", new Color(12433259));
    table.put("darkmagenta", new Color(9109643));
    table.put("darkolivegreen", new Color(5597999));
    table.put("darkorange", new Color(16747520));
    table.put("darkorchid", new Color(10040012));
    table.put("darkred", new Color(9109504));
    table.put("darksalmon", new Color(15308410));
    table.put("darkseagreen", new Color(9419919));
    table.put("darkslateblue", new Color(4734347));
    table.put("darkslategray", new Color(3100495));
    table.put("darkturquoise", new Color(52945));
    table.put("darkviolet", new Color(9699539));
    table.put("deeppink", new Color(16716947));
    table.put("deepskyblue", new Color(49151));
    table.put("dimgray", new Color(6908265));
    table.put("dodgerblue", new Color(2003199));
    table.put("feldspar", new Color(13734517));
    table.put("firebrick", new Color(11674146));
    table.put("floralwhite", new Color(16775920));
    table.put("forestgreen", new Color(2263842));
    table.put("fuchsia", new Color(16711935));
    table.put("gainsboro", new Color(14474460));
    table.put("ghostwhite", new Color(16316671));
    table.put("gold", new Color(16766720));
    table.put("goldenrod", new Color(14329120));
    table.put("gray", new Color(8421504));
    table.put("green", new Color(32768));
    table.put("greenyellow", new Color(11403055));
    table.put("honeydew", new Color(15794160));
    table.put("hotpink", new Color(16738740));
    table.put("indianred", new Color(13458524));
    table.put("indigo", new Color(4915330));
    table.put("ivory", new Color(16777200));
    table.put("khaki", new Color(15787660));
    table.put("lavender", new Color(15132410));
    table.put("lavenderblush", new Color(16773365));
    table.put("lawngreen", new Color(8190976));
    table.put("lemonchiffon", new Color(16775885));
    table.put("lightblue", new Color(11393254));
    table.put("lightcoral", new Color(15761536));
    table.put("lightcyan", new Color(14745599));
    table.put("lightgoldenrodyellow", new Color(16448210));
    table.put("lightgrey", new Color(13882323));
    table.put("lightgreen", new Color(9498256));
    table.put("lightpink", new Color(16758465));
    table.put("lightsalmon", new Color(16752762));
    table.put("lightseagreen", new Color(2142890));
    table.put("lightskyblue", new Color(8900346));
    table.put("lightslateblue", new Color(8679679));
    table.put("lightslategray", new Color(7833753));
    table.put("lightsteelblue", new Color(11584734));
    table.put("lightyellow", new Color(16777184));
    table.put("lime", new Color(65280));
    table.put("limegreen", new Color(3329330));
    table.put("linen", new Color(16445670));
    table.put("magenta", new Color(16711935));
    table.put("maroon", new Color(8388608));
    table.put("mediumaquamarine", new Color(6737322));
    table.put("mediumblue", new Color(205));
    table.put("mediumorchid", new Color(12211667));
    table.put("mediumpurple", new Color(9662680));
    table.put("mediumseagreen", new Color(3978097));
    table.put("mediumslateblue", new Color(8087790));
    table.put("mediumspringgreen", new Color(64154));
    table.put("mediumturquoise", new Color(4772300));
    table.put("mediumvioletred", new Color(13047173));
    table.put("midnightblue", new Color(1644912));
    table.put("mintcream", new Color(16121850));
    table.put("mistyrose", new Color(16770273));
    table.put("moccasin", new Color(16770229));
    table.put("navajowhite", new Color(16768685));
    table.put("navy", new Color(128));
    table.put("oldlace", new Color(16643558));
    table.put("olive", new Color(8421376));
    table.put("olivedrab", new Color(7048739));
    table.put("orange", new Color(16753920));
    table.put("orangered", new Color(16729344));
    table.put("orchid", new Color(14315734));
    table.put("palegoldenrod", new Color(15657130));
    table.put("palegreen", new Color(10025880));
    table.put("paleturquoise", new Color(11529966));
    table.put("palevioletred", new Color(14184595));
    table.put("papayawhip", new Color(16773077));
    table.put("peachpuff", new Color(16767673));
    table.put("peru", new Color(13468991));
    table.put("pink", new Color(16761035));
    table.put("plum", new Color(14524637));
    table.put("powderblue", new Color(11591910));
    table.put("purple", new Color(8388736));
    table.put("red", new Color(16711680));
    table.put("rosybrown", new Color(12357519));
    table.put("royalblue", new Color(4286945));
    table.put("saddlebrown", new Color(9127187));
    table.put("salmon", new Color(16416882));
    table.put("sandybrown", new Color(16032864));
    table.put("seagreen", new Color(3050327));
    table.put("seashell", new Color(16774638));
    table.put("sienna", new Color(10506797));
    table.put("silver", new Color(12632256));
    table.put("skyblue", new Color(8900331));
    table.put("slateblue", new Color(6970061));
    table.put("slategray", new Color(7372944));
    table.put("snow", new Color(16775930));
    table.put("springgreen", new Color(65407));
    table.put("steelblue", new Color(4620980));
    table.put("tan", new Color(13808780));
    table.put("teal", new Color(32896));
    table.put("thistle", new Color(14204888));
    table.put("tomato", new Color(16737095));
    table.put("turquoise", new Color(4251856));
    table.put("violet", new Color(15631086));
    table.put("violetred", new Color(13639824));
    table.put("wheat", new Color(16113331));
    table.put("white", new Color(16777215));
    table.put("whitesmoke", new Color(16119285));
    table.put("yellow", new Color(16776960));
    table.put("yellowgreen", new Color(10145074));
    
    colorTable = Collections.unmodifiableMap(table);
  }
  
  static ColorTable singleton = new ColorTable();




  
  public static ColorTable instance() {
    return singleton;
  }
  public Color lookupColor(String name) {
    return colorTable.get(name.toLowerCase());
  }

  
  public static Color parseColor(String val) {
    Color retVal = null;
    
    if ("".equals(val))
    {
      return null;
    }
    
    if (val.charAt(0) == '#') {
      
      String hexStrn = val.substring(1);
      
      if (hexStrn.length() == 3)
      {
        hexStrn = "" + hexStrn.charAt(0) + hexStrn.charAt(0) + hexStrn.charAt(1) + hexStrn.charAt(1) + hexStrn.charAt(2) + hexStrn.charAt(2);
      }
      int hexVal = parseHex(hexStrn);
      
      retVal = new Color(hexVal);
    }
    else {
      
      String number = "\\s*(((\\d+)(\\.\\d*)?)|(\\.\\d+))(%)?\\s*";
      Matcher rgbMatch = Pattern.compile("rgb\\(\\s*(((\\d+)(\\.\\d*)?)|(\\.\\d+))(%)?\\s*,\\s*(((\\d+)(\\.\\d*)?)|(\\.\\d+))(%)?\\s*,\\s*(((\\d+)(\\.\\d*)?)|(\\.\\d+))(%)?\\s*\\)", 2).matcher("");
      
      rgbMatch.reset(val);
      if (rgbMatch.matches()) {
        
        float rr = Float.parseFloat(rgbMatch.group(1));
        float gg = Float.parseFloat(rgbMatch.group(7));
        float bb = Float.parseFloat(rgbMatch.group(13));
        rr /= "%".equals(rgbMatch.group(6)) ? 100.0F : 255.0F;
        gg /= "%".equals(rgbMatch.group(12)) ? 100.0F : 255.0F;
        bb /= "%".equals(rgbMatch.group(18)) ? 100.0F : 255.0F;
        retVal = new Color(rr, gg, bb);
      }
      else {
        
        Color lookupCol = instance().lookupColor(val);
        if (lookupCol != null) retVal = lookupCol;
      
      } 
    } 
    return retVal;
  }

  
  public static int parseHex(String val) {
    int retVal = 0;
    
    for (int i = 0; i < val.length(); i++) {
      
      retVal <<= 4;
      
      char ch = val.charAt(i);
      if (ch >= '0' && ch <= '9') {
        
        retVal |= ch - 48;
      }
      else if (ch >= 'a' && ch <= 'z') {
        
        retVal |= ch - 97 + 10;
      }
      else if (ch >= 'A' && ch <= 'Z') {
        
        retVal |= ch - 65 + 10;
      } else {
        throw new RuntimeException();
      } 
    } 
    return retVal;
  }
}
