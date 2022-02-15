package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import javax.swing.text.StyleContext;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


































public class SyntaxScheme
  implements Cloneable, TokenTypes
{
  private Style[] styles;
  private static final String VERSION = "*ver1";
  
  public SyntaxScheme(boolean useDefaults) {
    this.styles = new Style[39];
    if (useDefaults) {
      restoreDefaults(null);
    }
  }








  
  public SyntaxScheme(Font baseFont) {
    this(baseFont, true);
  }










  
  public SyntaxScheme(Font baseFont, boolean fontStyles) {
    this.styles = new Style[39];
    restoreDefaults(baseFont, fontStyles);
  }













  
  void changeBaseFont(Font old, Font font) {
    for (Style style : this.styles) {
      if (style != null && style.font != null && 
        style.font.getFamily().equals(old.getFamily()) && style.font
        .getSize() == old.getSize()) {
        int s = style.font.getStyle();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        style.font = sc.getFont(font.getFamily(), s, font.getSize());
      } 
    } 
  }








  
  public Object clone() {
    SyntaxScheme shcs;
    try {
      shcs = (SyntaxScheme)super.clone();
    } catch (CloneNotSupportedException cnse) {
      cnse.printStackTrace();
      return null;
    } 
    shcs.styles = new Style[this.styles.length];
    for (int i = 0; i < this.styles.length; i++) {
      Style s = this.styles[i];
      if (s != null) {
        shcs.styles[i] = (Style)s.clone();
      }
    } 
    return shcs;
  }













  
  public boolean equals(Object otherScheme) {
    if (!(otherScheme instanceof SyntaxScheme)) {
      return false;
    }
    
    Style[] otherSchemes = ((SyntaxScheme)otherScheme).styles;
    
    int length = this.styles.length;
    for (int i = 0; i < length; i++) {
      if (this.styles[i] == null) {
        if (otherSchemes[i] != null) {
          return false;
        }
      }
      else if (!this.styles[i].equals(otherSchemes[i])) {
        return false;
      } 
    } 
    return true;
  }









  
  private static String getHexString(Color c) {
    return "$" + Integer.toHexString((c.getRGB() & 0xFFFFFF) + 16777216)
      .substring(1);
  }









  
  public Style getStyle(int index) {
    return this.styles[index];
  }







  
  public int getStyleCount() {
    return this.styles.length;
  }












  
  public Style[] getStyles() {
    return this.styles;
  }










  
  public int hashCode() {
    int hashCode = 0;
    int count = this.styles.length;
    for (Style style : this.styles) {
      if (style != null) {
        hashCode ^= style.hashCode();
        break;
      } 
    } 
    return hashCode;
  }















  
  public static SyntaxScheme load(Font baseFont, InputStream in) throws IOException {
    if (baseFont == null) {
      baseFont = RSyntaxTextArea.getDefaultFont();
    }
    return SyntaxSchemeLoader.load(baseFont, in);
  }













  
  public static SyntaxScheme loadFromString(String string) {
    return loadFromString(string, 39);
  }





















  
  public static SyntaxScheme loadFromString(String string, int tokenTypeCount) {
    SyntaxScheme scheme = new SyntaxScheme(true);

    
    try {
      if (string != null)
      {
        String[] tokens = string.split(",", -1);

        
        if (tokens.length == 0 || !"*ver1".equals(tokens[0])) {
          return scheme;
        }
        
        int tokenCount = tokenTypeCount * 7 + 1;
        if (tokens.length != tokenCount) {
          throw new Exception("Not enough tokens in packed color scheme: expected " + tokenCount + ", found " + tokens.length);
        }




        
        StyleContext sc = StyleContext.getDefaultStyleContext();


        
        for (int i = 0; i < tokenTypeCount; i++)
        {
          int pos = i * 7 + 1;
          int integer = Integer.parseInt(tokens[pos]);
          if (integer != i) {
            throw new Exception("Expected " + i + ", found " + integer);
          }

          
          Color fg = null; String temp = tokens[pos + 1];
          if (!"-".equals(temp)) {
            fg = stringToColor(temp);
          }
          Color bg = null; temp = tokens[pos + 2];
          if (!"-".equals(temp)) {
            bg = stringToColor(temp);
          }



          
          temp = tokens[pos + 3];
          if (!"t".equals(temp) && !"f".equals(temp)) {
            throw new Exception("Expected 't' or 'f', found " + temp);
          }
          boolean underline = "t".equals(temp);
          
          Font font = null;
          String family = tokens[pos + 4];
          if (!"-".equals(family)) {
            font = sc.getFont(family, 
                Integer.parseInt(tokens[pos + 5]), 
                Integer.parseInt(tokens[pos + 6]));
          }
          scheme.styles[i] = new Style(fg, bg, font, underline);
        }
      
      }
    
    }
    catch (Exception e) {
      e.printStackTrace();
    } 
    
    return scheme;
  }



  
  void refreshFontMetrics(Graphics2D g2d) {
    for (Style s : this.styles) {
      if (s != null) {
        s
          .fontMetrics = (s.font == null) ? null : g2d.getFontMetrics(s.font);
      }
    } 
  }








  
  public void restoreDefaults(Font baseFont) {
    restoreDefaults(baseFont, true);
  }












  
  public void restoreDefaults(Font baseFont, boolean fontStyles) {
    Color comment = new Color(0, 128, 0);
    Color docComment = new Color(164, 0, 0);
    Color markupComment = new Color(0, 96, 0);
    Color keyword = Color.BLUE;
    Color dataType = new Color(0, 128, 128);
    Color function = new Color(173, 128, 0);
    Color preprocessor = new Color(128, 128, 128);
    Color operator = new Color(128, 64, 64);
    Color regex = new Color(0, 128, 164);
    Color variable = new Color(255, 153, 0);
    Color literalNumber = new Color(100, 0, 200);
    Color literalString = new Color(220, 0, 156);
    Color error = new Color(148, 148, 0);

    
    if (baseFont == null) {
      baseFont = RSyntaxTextArea.getDefaultFont();
    }
    Font commentFont = baseFont;
    Font keywordFont = baseFont;
    if (fontStyles) {

      
      StyleContext sc = StyleContext.getDefaultStyleContext();
      Font boldFont = sc.getFont(baseFont.getFamily(), 1, baseFont
          .getSize());
      Font italicFont = sc.getFont(baseFont.getFamily(), 2, baseFont
          .getSize());
      commentFont = italicFont;
      keywordFont = boldFont;
    } 
    
    this.styles[1] = new Style(comment, null, commentFont);
    this.styles[2] = new Style(comment, null, commentFont);
    this.styles[3] = new Style(docComment, null, commentFont);
    this.styles[4] = new Style(new Color(255, 152, 0), null, commentFont);
    this.styles[5] = new Style(Color.gray, null, commentFont);
    this.styles[6] = new Style(keyword, null, keywordFont);
    this.styles[7] = new Style(keyword, null, keywordFont);
    this.styles[8] = new Style(function);
    this.styles[9] = new Style(literalNumber);
    this.styles[10] = new Style(literalNumber);
    this.styles[11] = new Style(literalNumber);
    this.styles[12] = new Style(literalNumber);
    this.styles[13] = new Style(literalString);
    this.styles[14] = new Style(literalString);
    this.styles[15] = new Style(literalString);
    this.styles[16] = new Style(dataType, null, keywordFont);
    this.styles[17] = new Style(variable);
    this.styles[18] = new Style(regex);
    this.styles[19] = new Style(Color.gray);
    this.styles[20] = new Style(null);
    this.styles[21] = new Style(Color.gray);
    this.styles[22] = new Style(Color.RED);
    this.styles[23] = new Style(operator);
    this.styles[24] = new Style(preprocessor);
    this.styles[25] = new Style(Color.RED);
    this.styles[26] = new Style(Color.BLUE);
    this.styles[27] = new Style(new Color(63, 127, 127));
    this.styles[28] = new Style(literalString);
    this.styles[29] = new Style(markupComment, null, commentFont);
    this.styles[30] = new Style(function);
    this.styles[31] = new Style(preprocessor);
    this.styles[33] = new Style(new Color(13395456));
    this.styles[32] = new Style(new Color(32896));
    this.styles[34] = new Style(dataType);
    this.styles[35] = new Style(error);
    this.styles[36] = new Style(error);
    this.styles[37] = new Style(error);
    this.styles[38] = new Style(error);


    
    for (int i = 0; i < this.styles.length; i++) {
      if (this.styles[i] == null) {
        this.styles[i] = new Style();
      }
    } 
  }









  
  public void setStyle(int type, Style style) {
    this.styles[type] = style;
  }












  
  public void setStyles(Style[] styles) {
    this.styles = styles;
  }
















  
  private static Color stringToColor(String s) {
    char ch = s.charAt(0);
    return new Color((ch == '$' || ch == '#') ? 
        Integer.parseInt(s.substring(1), 16) : 
        Integer.parseInt(s));
  }





























  
  public String toCommaSeparatedString() {
    StringBuilder sb = new StringBuilder("*ver1");
    sb.append(',');
    
    for (int i = 0; i < this.styles.length; i++) {
      
      sb.append(i).append(',');
      
      Style ss = this.styles[i];
      if (ss == null) {
        sb.append("-,-,f,-,,,");
      }
      else {
        
        Color c = ss.foreground;
        sb.append((c != null) ? (getHexString(c) + ",") : "-,");
        c = ss.background;
        sb.append((c != null) ? (getHexString(c) + ",") : "-,");
        
        sb.append(ss.underline ? "t," : "f,");
        
        Font font = ss.font;
        if (font != null) {
          sb.append(font.getFamily()).append(',')
            .append(font.getStyle()).append(',')
            .append(font.getSize()).append(',');
        } else {
          
          sb.append("-,,,");
        } 
      } 
    } 
    
    return sb.substring(0, sb.length() - 1);
  }

  
  private static class SyntaxSchemeLoader
    extends DefaultHandler
  {
    private Font baseFont;
    
    private SyntaxScheme scheme;

    
    SyntaxSchemeLoader(Font baseFont) {
      this.scheme = new SyntaxScheme(baseFont);
    }

    
    public static SyntaxScheme load(Font baseFont, InputStream in) throws IOException {
      SyntaxSchemeLoader parser;
      try {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        parser = new SyntaxSchemeLoader(baseFont);
        parser.baseFont = baseFont;
        reader.setContentHandler(parser);
        InputSource is = new InputSource(in);
        is.setEncoding("UTF-8");
        reader.parse(is);
      } catch (SAXException se) {
        throw new IOException(se.toString());
      } 
      return parser.scheme;
    }



    
    public void startElement(String uri, String localName, String qName, Attributes attrs) {
      if ("style".equals(qName)) {
        Field field;
        String type = attrs.getValue("token");

        
        try {
          field = SyntaxScheme.class.getField(type);
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception e) {
          System.err.println("Error fetching 'getType' method for Token class");
          
          return;
        } 
        if (field.getType() == int.class) {
          
          int index = 0;
          try {
            index = field.getInt(this.scheme);
          } catch (IllegalArgumentException|IllegalAccessException e) {
            e.printStackTrace();
            
            return;
          } 
          String fgStr = attrs.getValue("fg");
          if (fgStr != null) {
            Color fg = SyntaxScheme.stringToColor(fgStr);
            (this.scheme.styles[index]).foreground = fg;
          } 
          
          String bgStr = attrs.getValue("bg");
          if (bgStr != null) {
            Color bg = SyntaxScheme.stringToColor(bgStr);
            (this.scheme.styles[index]).background = bg;
          } 
          
          boolean styleSpecified = false;
          boolean bold = false;
          boolean italic = false;
          String boldStr = attrs.getValue("bold");
          if (boldStr != null) {
            bold = Boolean.parseBoolean(boldStr);
            styleSpecified = true;
          } 
          String italicStr = attrs.getValue("italic");
          if (italicStr != null) {
            italic = Boolean.parseBoolean(italicStr);
            styleSpecified = true;
          } 
          if (styleSpecified) {
            int style = 0;
            if (bold) {
              style |= 0x1;
            }
            if (italic) {
              style |= 0x2;
            }
            (this.scheme.styles[index]).font = this.baseFont.deriveFont(style);
          } 
          
          String ulineStr = attrs.getValue("underline");
          if (ulineStr != null) {
            boolean uline = Boolean.parseBoolean(ulineStr);
            (this.scheme.styles[index]).underline = uline;
          } 
        } 
      } 
    }
  }
}
