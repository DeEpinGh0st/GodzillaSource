package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.StyleContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.fife.io.UnicodeWriter;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextArea;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
















































public class Theme
{
  public Font baseFont;
  public Color bgColor;
  public Color caretColor;
  public boolean useSelectionFG;
  public Color selectionFG;
  public Color selectionBG;
  public boolean selectionRoundedEdges;
  public Color currentLineHighlight;
  public boolean fadeCurrentLineHighlight;
  public Color tabLineColor;
  public Color marginLineColor;
  public Color markAllHighlightColor;
  public Color markOccurrencesColor;
  public boolean markOccurrencesBorder;
  public Color matchedBracketFG;
  public Color matchedBracketBG;
  public boolean matchedBracketHighlightBoth;
  public boolean matchedBracketAnimate;
  public Color hyperlinkFG;
  public Color[] secondaryLanguages;
  public SyntaxScheme scheme;
  public Color gutterBackgroundColor;
  public Color gutterBorderColor;
  public Color activeLineRangeColor;
  public boolean iconRowHeaderInheritsGutterBG;
  public Color lineNumberColor;
  public String lineNumberFont;
  public int lineNumberFontSize;
  public Color foldIndicatorFG;
  public Color foldBG;
  public Color armedFoldBG;
  
  private Theme(Font baseFont) {
    this.baseFont = (baseFont != null) ? baseFont : RTextArea.getDefaultFont();
    this.secondaryLanguages = new Color[3];
    this.activeLineRangeColor = Gutter.DEFAULT_ACTIVE_LINE_RANGE_COLOR;
  }








  
  public Theme(RSyntaxTextArea textArea) {
    this.baseFont = textArea.getFont();
    this.bgColor = textArea.getBackground();
    this.caretColor = textArea.getCaretColor();
    this.useSelectionFG = textArea.getUseSelectedTextColor();
    this.selectionFG = textArea.getSelectedTextColor();
    this.selectionBG = textArea.getSelectionColor();
    this.selectionRoundedEdges = textArea.getRoundedSelectionEdges();
    this.currentLineHighlight = textArea.getCurrentLineHighlightColor();
    this.fadeCurrentLineHighlight = textArea.getFadeCurrentLineHighlight();
    this.tabLineColor = textArea.getTabLineColor();
    this.marginLineColor = textArea.getMarginLineColor();
    this.markAllHighlightColor = textArea.getMarkAllHighlightColor();
    this.markOccurrencesColor = textArea.getMarkOccurrencesColor();
    this.markOccurrencesBorder = textArea.getPaintMarkOccurrencesBorder();
    this.matchedBracketBG = textArea.getMatchedBracketBGColor();
    this.matchedBracketFG = textArea.getMatchedBracketBorderColor();
    this.matchedBracketHighlightBoth = textArea.getPaintMatchedBracketPair();
    this.matchedBracketAnimate = textArea.getAnimateBracketMatching();
    this.hyperlinkFG = textArea.getHyperlinkForeground();
    
    int count = textArea.getSecondaryLanguageCount();
    this.secondaryLanguages = new Color[count];
    for (int i = 0; i < count; i++) {
      this.secondaryLanguages[i] = textArea.getSecondaryLanguageBackground(i + 1);
    }
    
    this.scheme = textArea.getSyntaxScheme();
    
    Gutter gutter = RSyntaxUtilities.getGutter(textArea);
    if (gutter != null) {
      this.gutterBackgroundColor = gutter.getBackground();
      this.gutterBorderColor = gutter.getBorderColor();
      this.activeLineRangeColor = gutter.getActiveLineRangeColor();
      this.iconRowHeaderInheritsGutterBG = gutter.getIconRowHeaderInheritsGutterBackground();
      this.lineNumberColor = gutter.getLineNumberColor();
      this.lineNumberFont = gutter.getLineNumberFont().getFamily();
      this.lineNumberFontSize = gutter.getLineNumberFont().getSize();
      this.foldIndicatorFG = gutter.getFoldIndicatorForeground();
      this.foldBG = gutter.getFoldBackground();
      this.armedFoldBG = gutter.getArmedFoldBackground();
    } 
  }








  
  public void apply(RSyntaxTextArea textArea) {
    textArea.setFont(this.baseFont);
    textArea.setBackground(this.bgColor);
    textArea.setCaretColor(this.caretColor);
    textArea.setUseSelectedTextColor(this.useSelectionFG);
    textArea.setSelectedTextColor(this.selectionFG);
    textArea.setSelectionColor(this.selectionBG);
    textArea.setRoundedSelectionEdges(this.selectionRoundedEdges);
    textArea.setCurrentLineHighlightColor(this.currentLineHighlight);
    textArea.setFadeCurrentLineHighlight(this.fadeCurrentLineHighlight);
    textArea.setTabLineColor(this.tabLineColor);
    textArea.setMarginLineColor(this.marginLineColor);
    textArea.setMarkAllHighlightColor(this.markAllHighlightColor);
    textArea.setMarkOccurrencesColor(this.markOccurrencesColor);
    textArea.setPaintMarkOccurrencesBorder(this.markOccurrencesBorder);
    textArea.setMatchedBracketBGColor(this.matchedBracketBG);
    textArea.setMatchedBracketBorderColor(this.matchedBracketFG);
    textArea.setPaintMatchedBracketPair(this.matchedBracketHighlightBoth);
    textArea.setAnimateBracketMatching(this.matchedBracketAnimate);
    textArea.setHyperlinkForeground(this.hyperlinkFG);
    
    int count = this.secondaryLanguages.length;
    for (int i = 0; i < count; i++) {
      textArea.setSecondaryLanguageBackground(i + 1, this.secondaryLanguages[i]);
    }
    
    textArea.setSyntaxScheme(this.scheme);
    
    Gutter gutter = RSyntaxUtilities.getGutter(textArea);
    if (gutter != null) {
      gutter.setBackground(this.gutterBackgroundColor);
      gutter.setBorderColor(this.gutterBorderColor);
      gutter.setActiveLineRangeColor(this.activeLineRangeColor);
      gutter.setIconRowHeaderInheritsGutterBackground(this.iconRowHeaderInheritsGutterBG);
      gutter.setLineNumberColor(this.lineNumberColor);
      
      String fontName = (this.lineNumberFont != null) ? this.lineNumberFont : this.baseFont.getFamily();
      
      int fontSize = (this.lineNumberFontSize > 0) ? this.lineNumberFontSize : this.baseFont.getSize();
      Font font = getFont(fontName, 0, fontSize);
      gutter.setLineNumberFont(font);
      gutter.setFoldIndicatorForeground(this.foldIndicatorFG);
      gutter.setFoldBackground(this.foldBG);
      gutter.setArmedFoldBackground(this.armedFoldBG);
    } 
  }


  
  private static String colorToString(Color c) {
    int color = c.getRGB() & 0xFFFFFF;
    StringBuilder stringBuilder = new StringBuilder(Integer.toHexString(color));
    while (stringBuilder.length() < 6) {
      stringBuilder.insert(0, "0");
    }
    return stringBuilder.toString();
  }








  
  private static Color getDefaultBG() {
    Color c = UIManager.getColor("nimbusLightBackground");
    if (c == null) {



      
      c = UIManager.getColor("TextArea.background");
      if (c == null) {
        c = new ColorUIResource(SystemColor.text);
      }
    } 
    return c;
  }








  
  private static Color getDefaultSelectionBG() {
    Color c = UIManager.getColor("TextArea.selectionBackground");
    if (c == null) {
      c = UIManager.getColor("textHighlight");
      if (c == null) {
        c = UIManager.getColor("nimbusSelectionBackground");
        if (c == null) {
          c = new ColorUIResource(SystemColor.textHighlight);
        }
      } 
    } 
    return c;
  }








  
  private static Color getDefaultSelectionFG() {
    Color c = UIManager.getColor("TextArea.selectionForeground");
    if (c == null) {
      c = UIManager.getColor("textHighlightText");
      if (c == null) {
        c = UIManager.getColor("nimbusSelectedText");
        if (c == null) {
          c = new ColorUIResource(SystemColor.textHighlightText);
        }
      } 
    } 
    return c;
  }










  
  private static Font getFont(String family, int style, int size) {
    StyleContext sc = StyleContext.getDefaultStyleContext();
    return sc.getFont(family, style, size);
  }










  
  public static Theme load(InputStream in) throws IOException {
    return load(in, null);
  }














  
  public static Theme load(InputStream in, Font baseFont) throws IOException {
    Theme theme = new Theme(baseFont);
    
    try (BufferedInputStream bin = new BufferedInputStream(in)) {
      XmlHandler.load(theme, bin);
    } 
    
    return theme;
  }









  
  public void save(OutputStream out) throws IOException {
    try (BufferedOutputStream bout = new BufferedOutputStream(out)) {

      
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      DOMImplementation impl = db.getDOMImplementation();
      
      Document doc = impl.createDocument(null, "RSyntaxTheme", null);
      Element root = doc.getDocumentElement();
      root.setAttribute("version", "1.0");
      
      Element elem = doc.createElement("baseFont");
      if (!this.baseFont.getFamily().equals(
          RSyntaxTextArea.getDefaultFont().getFamily())) {
        elem.setAttribute("family", this.baseFont.getFamily());
      }
      elem.setAttribute("size", Integer.toString(this.baseFont.getSize()));
      root.appendChild(elem);
      
      elem = doc.createElement("background");
      elem.setAttribute("color", colorToString(this.bgColor));
      root.appendChild(elem);
      
      elem = doc.createElement("caret");
      elem.setAttribute("color", colorToString(this.caretColor));
      root.appendChild(elem);
      
      elem = doc.createElement("selection");
      elem.setAttribute("useFG", Boolean.toString(this.useSelectionFG));
      elem.setAttribute("fg", colorToString(this.selectionFG));
      elem.setAttribute("bg", colorToString(this.selectionBG));
      elem.setAttribute("roundedEdges", Boolean.toString(this.selectionRoundedEdges));
      root.appendChild(elem);
      
      elem = doc.createElement("currentLineHighlight");
      elem.setAttribute("color", colorToString(this.currentLineHighlight));
      elem.setAttribute("fade", Boolean.toString(this.fadeCurrentLineHighlight));
      root.appendChild(elem);
      
      elem = doc.createElement("tabLine");
      elem.setAttribute("color", colorToString(this.tabLineColor));
      root.appendChild(elem);
      
      elem = doc.createElement("marginLine");
      elem.setAttribute("fg", colorToString(this.marginLineColor));
      root.appendChild(elem);
      
      elem = doc.createElement("markAllHighlight");
      elem.setAttribute("color", colorToString(this.markAllHighlightColor));
      root.appendChild(elem);
      
      elem = doc.createElement("markOccurrencesHighlight");
      elem.setAttribute("color", colorToString(this.markOccurrencesColor));
      elem.setAttribute("border", Boolean.toString(this.markOccurrencesBorder));
      root.appendChild(elem);
      
      elem = doc.createElement("matchedBracket");
      elem.setAttribute("fg", colorToString(this.matchedBracketFG));
      elem.setAttribute("bg", colorToString(this.matchedBracketBG));
      elem.setAttribute("highlightBoth", Boolean.toString(this.matchedBracketHighlightBoth));
      elem.setAttribute("animate", Boolean.toString(this.matchedBracketAnimate));
      root.appendChild(elem);
      
      elem = doc.createElement("hyperlinks");
      elem.setAttribute("fg", colorToString(this.hyperlinkFG));
      root.appendChild(elem);
      
      elem = doc.createElement("secondaryLanguages");
      for (int i = 0; i < this.secondaryLanguages.length; i++) {
        Color color = this.secondaryLanguages[i];
        Element elem2 = doc.createElement("language");
        elem2.setAttribute("index", Integer.toString(i + 1));
        elem2.setAttribute("bg", (color == null) ? "" : colorToString(color));
        elem.appendChild(elem2);
      } 
      root.appendChild(elem);
      
      elem = doc.createElement("gutterBackground");
      elem.setAttribute("color", colorToString(this.gutterBackgroundColor));
      root.appendChild(elem);
      
      elem = doc.createElement("gutterBorder");
      elem.setAttribute("color", colorToString(this.gutterBorderColor));
      root.appendChild(elem);
      
      elem = doc.createElement("lineNumbers");
      elem.setAttribute("fg", colorToString(this.lineNumberColor));
      if (this.lineNumberFont != null) {
        elem.setAttribute("fontFamily", this.lineNumberFont);
      }
      if (this.lineNumberFontSize > 0) {
        elem.setAttribute("fontSize", 
            Integer.toString(this.lineNumberFontSize));
      }
      root.appendChild(elem);
      
      elem = doc.createElement("foldIndicator");
      elem.setAttribute("fg", colorToString(this.foldIndicatorFG));
      elem.setAttribute("iconBg", colorToString(this.foldBG));
      elem.setAttribute("iconArmedBg", colorToString(this.armedFoldBG));
      root.appendChild(elem);
      
      elem = doc.createElement("iconRowHeader");
      elem.setAttribute("activeLineRange", colorToString(this.activeLineRangeColor));
      elem.setAttribute("inheritsGutterBG", Boolean.toString(this.iconRowHeaderInheritsGutterBG));
      root.appendChild(elem);
      
      elem = doc.createElement("tokenStyles");
      Field[] fields = TokenTypes.class.getFields();
      for (Field field : fields) {
        int value = field.getInt(null);
        if (value != 39) {
          Style style = this.scheme.getStyle(value);
          if (style != null) {
            Element elem2 = doc.createElement("style");
            elem2.setAttribute("token", field.getName());
            Color fg = style.foreground;
            if (fg != null) {
              elem2.setAttribute("fg", colorToString(fg));
            }
            Color bg = style.background;
            if (bg != null) {
              elem2.setAttribute("bg", colorToString(bg));
            }
            Font font = style.font;
            if (font != null) {
              if (!font.getFamily().equals(this.baseFont
                  .getFamily())) {
                elem2.setAttribute("fontFamily", font.getFamily());
              }
              if (font.getSize() != this.baseFont.getSize()) {
                elem2.setAttribute("fontSize", Integer.toString(font.getSize()));
              }
              if (font.isBold()) {
                elem2.setAttribute("bold", "true");
              }
              if (font.isItalic()) {
                elem2.setAttribute("italic", "true");
              }
            } 
            if (style.underline) {
              elem2.setAttribute("underline", "true");
            }
            elem.appendChild(elem2);
          } 
        } 
      } 
      root.appendChild(elem);
      
      DOMSource source = new DOMSource(doc);

      
      StreamResult result = new StreamResult(new PrintWriter((Writer)new UnicodeWriter(bout, "UTF-8")));
      
      TransformerFactory transFac = TransformerFactory.newInstance();
      Transformer transformer = transFac.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.setOutputProperty("encoding", "UTF-8");
      transformer.setOutputProperty("doctype-system", "theme.dtd");
      transformer.transform(source, result);
    }
    catch (RuntimeException re) {
      throw re;
    } catch (Exception e) {
      throw new IOException("Error generating XML: " + e.getMessage(), e);
    } 
  }















  
  private static Color stringToColor(String s) {
    return stringToColor(s, null);
  }
















  
  private static Color stringToColor(String s, Color defaultVal) {
    if (s == null || "default".equalsIgnoreCase(s)) {
      return defaultVal;
    }
    if (s.length() == 6 || s.length() == 7) {
      if (s.charAt(0) == '$') {
        s = s.substring(1);
      }
      return new Color(Integer.parseInt(s, 16));
    } 
    return null;
  }


  
  private static class XmlHandler
    extends DefaultHandler
  {
    private Theme theme;


    
    public void error(SAXParseException e) throws SAXException {
      throw e;
    }

    
    public void fatalError(SAXParseException e) throws SAXException {
      throw e;
    }
    
    public static void load(Theme theme, InputStream in) throws IOException {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setValidating(true);
      try {
        SAXParser parser = spf.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        XmlHandler handler = new XmlHandler();
        handler.theme = theme;
        reader.setEntityResolver(handler);
        reader.setContentHandler(handler);
        reader.setDTDHandler(handler);
        reader.setErrorHandler(handler);
        InputSource is = new InputSource(in);
        is.setEncoding("UTF-8");
        reader.parse(is);
      } catch (Exception se) {
        throw new IOException(se.toString());
      } 
    }

    
    private static int parseInt(Attributes attrs, String attr, int def) {
      int value = def;
      String temp = attrs.getValue(attr);
      if (temp != null) {
        try {
          value = Integer.parseInt(temp);
        } catch (NumberFormatException nfe) {
          nfe.printStackTrace();
        } 
      }
      return value;
    }

    
    public InputSource resolveEntity(String publicID, String systemID) {
      return new InputSource(getClass()
          .getResourceAsStream("themes/theme.dtd"));
    }



    
    public void startElement(String uri, String localName, String qName, Attributes attrs) {
      if ("background".equals(qName)) {
        
        String color = attrs.getValue("color");
        if (color != null) {
          this.theme.bgColor = Theme.stringToColor(color, Theme.getDefaultBG());
          this.theme.gutterBackgroundColor = this.theme.bgColor;
        } else {
          
          String img = attrs.getValue("image");
          if (img != null) {
            throw new IllegalArgumentException("Not yet implemented");
          
          }
        }
      
      }
      else if ("baseFont".equals(qName)) {
        int size = this.theme.baseFont.getSize();
        String sizeStr = attrs.getValue("size");
        if (sizeStr != null) {
          size = Integer.parseInt(sizeStr);
        }
        String family = attrs.getValue("family");
        if (family != null) {
          this.theme.baseFont = Theme.getFont(family, 0, size);
        }
        else if (sizeStr != null) {
          
          this.theme.baseFont = this.theme.baseFont.deriveFont(size * 1.0F);
        }
      
      }
      else if ("caret".equals(qName)) {
        String color = attrs.getValue("color");
        this.theme.caretColor = Theme.stringToColor(color);
      
      }
      else if ("currentLineHighlight".equals(qName)) {
        String color = attrs.getValue("color");
        this.theme.currentLineHighlight = Theme.stringToColor(color);
        String fadeStr = attrs.getValue("fade");
        boolean fade = Boolean.parseBoolean(fadeStr);
        this.theme.fadeCurrentLineHighlight = fade;
      
      }
      else if ("tabLine".equals(qName)) {
        String color = attrs.getValue("color");
        this.theme.tabLineColor = Theme.stringToColor(color);
      
      }
      else if ("foldIndicator".equals(qName)) {
        String color = attrs.getValue("fg");
        this.theme.foldIndicatorFG = Theme.stringToColor(color);
        color = attrs.getValue("iconBg");
        this.theme.foldBG = Theme.stringToColor(color);
        color = attrs.getValue("iconArmedBg");


        
        this.theme.armedFoldBG = Theme.stringToColor(color, this.theme.foldBG);
      
      }
      else if ("gutterBackground".equals(qName)) {
        String color = attrs.getValue("color");
        if (color != null) {
          this.theme.gutterBackgroundColor = Theme.stringToColor(color);
        
        }
      }
      else if ("gutterBorder".equals(qName)) {
        String color = attrs.getValue("color");
        this.theme.gutterBorderColor = Theme.stringToColor(color);
      
      }
      else if ("iconRowHeader".equals(qName)) {
        String color = attrs.getValue("activeLineRange");
        this.theme.activeLineRangeColor = Theme.stringToColor(color);
        String inheritBGStr = attrs.getValue("inheritsGutterBG");
        this.theme
          .iconRowHeaderInheritsGutterBG = Boolean.parseBoolean(inheritBGStr);
      
      }
      else if ("lineNumbers".equals(qName)) {
        String color = attrs.getValue("fg");
        this.theme.lineNumberColor = Theme.stringToColor(color);
        this.theme.lineNumberFont = attrs.getValue("fontFamily");
        this.theme.lineNumberFontSize = parseInt(attrs, "fontSize", -1);
      
      }
      else if ("marginLine".equals(qName)) {
        String color = attrs.getValue("fg");
        this.theme.marginLineColor = Theme.stringToColor(color);
      
      }
      else if ("markAllHighlight".equals(qName)) {
        String color = attrs.getValue("color");
        this.theme.markAllHighlightColor = Theme.stringToColor(color);
      
      }
      else if ("markOccurrencesHighlight".equals(qName)) {
        String color = attrs.getValue("color");
        this.theme.markOccurrencesColor = Theme.stringToColor(color);
        String border = attrs.getValue("border");
        this.theme.markOccurrencesBorder = Boolean.parseBoolean(border);
      
      }
      else if ("matchedBracket".equals(qName)) {
        String fg = attrs.getValue("fg");
        this.theme.matchedBracketFG = Theme.stringToColor(fg);
        String bg = attrs.getValue("bg");
        this.theme.matchedBracketBG = Theme.stringToColor(bg);
        String highlightBoth = attrs.getValue("highlightBoth");
        this.theme.matchedBracketHighlightBoth = Boolean.parseBoolean(highlightBoth);
        String animate = attrs.getValue("animate");
        this.theme.matchedBracketAnimate = Boolean.parseBoolean(animate);
      
      }
      else if ("hyperlinks".equals(qName)) {
        String fg = attrs.getValue("fg");
        this.theme.hyperlinkFG = Theme.stringToColor(fg);
      
      }
      else if ("language".equals(qName)) {
        String indexStr = attrs.getValue("index");
        int index = Integer.parseInt(indexStr) - 1;
        if (this.theme.secondaryLanguages.length > index) {
          Color bg = Theme.stringToColor(attrs.getValue("bg"));
          this.theme.secondaryLanguages[index] = bg;
        }
      
      }
      else if ("selection".equals(qName)) {
        String useStr = attrs.getValue("useFG");
        this.theme.useSelectionFG = Boolean.parseBoolean(useStr);
        String color = attrs.getValue("fg");
        this.theme.selectionFG = Theme.stringToColor(color, Theme
            .getDefaultSelectionFG());
        
        color = attrs.getValue("bg");
        this.theme.selectionBG = Theme.stringToColor(color, Theme
            .getDefaultSelectionBG());
        String roundedStr = attrs.getValue("roundedEdges");
        this.theme.selectionRoundedEdges = Boolean.parseBoolean(roundedStr);

      
      }
      else if ("tokenStyles".equals(qName)) {
        this.theme.scheme = new SyntaxScheme(this.theme.baseFont, false);

      
      }
      else if ("style".equals(qName)) {
        
        String type = attrs.getValue("token");
        Field field = null;
        try {
          field = Token.class.getField(type);
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception e) {
          System.err.println("Invalid token type: " + type);
          
          return;
        } 
        if (field.getType() == int.class) {
          
          int index = 0;
          try {
            index = field.getInt(this.theme.scheme);
          } catch (IllegalArgumentException|IllegalAccessException e) {
            e.printStackTrace();
            
            return;
          } 
          String fgStr = attrs.getValue("fg");
          Color fg = Theme.stringToColor(fgStr);
          (this.theme.scheme.getStyle(index)).foreground = fg;
          
          String bgStr = attrs.getValue("bg");
          Color bg = Theme.stringToColor(bgStr);
          (this.theme.scheme.getStyle(index)).background = bg;
          
          Font font = this.theme.baseFont;
          String familyName = attrs.getValue("fontFamily");
          if (familyName != null) {
            font = Theme.getFont(familyName, font.getStyle(), font
                .getSize());
          }
          String sizeStr = attrs.getValue("fontSize");
          if (sizeStr != null) {
            try {
              float size = Float.parseFloat(sizeStr);
              size = Math.max(size, 1.0F);
              font = font.deriveFont(size);
            } catch (NumberFormatException nfe) {
              nfe.printStackTrace();
            } 
          }
          (this.theme.scheme.getStyle(index)).font = font;
          
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
            Font orig = (this.theme.scheme.getStyle(index)).font;
            (this.theme.scheme.getStyle(index))
              .font = orig.deriveFont(style);
          } 
          
          String ulineStr = attrs.getValue("underline");
          if (ulineStr != null) {
            boolean uline = Boolean.parseBoolean(ulineStr);
            (this.theme.scheme.getStyle(index)).underline = uline;
          } 
        } 
      } 
    }




    
    public void warning(SAXParseException e) throws SAXException {
      throw e;
    }
  }
}
