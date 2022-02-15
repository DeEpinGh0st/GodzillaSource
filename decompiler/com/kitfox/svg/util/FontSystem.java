package com.kitfox.svg.util;

import com.kitfox.svg.Font;
import com.kitfox.svg.FontFace;
import com.kitfox.svg.Glyph;
import com.kitfox.svg.MissingGlyph;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;










































public class FontSystem
  extends Font
{
  Font sysFont;
  FontMetrics fm;
  HashMap<String, Glyph> glyphCache;
  static HashSet<String> sysFontNames = new HashSet<String>();

  
  public static boolean checkIfSystemFontExists(String fontName) {
    if (sysFontNames.isEmpty())
    {
      for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ENGLISH))
      {
        sysFontNames.add(name);
      }
    }
    
    return sysFontNames.contains(fontName);
  }

  
  public static FontSystem createFont(String fontFamily, int fontStyle, int fontWeight, float fontSize) {
    String[] families = fontFamily.split(",");
    for (String fontName : families) {
      
      String javaFontName = mapJavaFontName(fontName);
      if (checkIfSystemFontExists(javaFontName))
      {
        return new FontSystem(javaFontName, fontStyle, fontWeight, fontSize);
      }
    } 
    
    return null;
  }

  
  private static String mapJavaFontName(String fontName) {
    if ("serif".equals(fontName))
    {
      return "Serif";
    }
    if ("sans-serif".equals(fontName))
    {
      return "SansSerif";
    }
    if ("monospace".equals(fontName))
    {
      return "Monospaced";
    }
    return fontName;
  }
  
  private FontSystem(String fontFamily, int fontStyle, int fontWeight, float fontSize) {
    int style, weight;
    this.glyphCache = new HashMap<String, Glyph>();
    switch (fontStyle) {
      
      case 1:
        style = 2;
        break;
      default:
        style = 0;
        break;
    } 

    
    switch (fontWeight) {
      
      case 1:
      case 2:
        weight = 1;
        break;
      default:
        weight = 0;
        break;
    } 
    
    this.sysFont = (new Font(fontFamily, style | weight, 1)).deriveFont(fontSize);
    
    Canvas c = new Canvas();
    this.fm = c.getFontMetrics(this.sysFont);
    
    FontFace face = new FontFace();
    face.setAscent(this.fm.getAscent());
    face.setDescent(this.fm.getDescent());
    face.setUnitsPerEm(this.fm.charWidth('M'));
    setFontFace(face);
  }


  
  public MissingGlyph getGlyph(String unicode) {
    FontRenderContext frc = new FontRenderContext(null, true, true);
    GlyphVector vec = this.sysFont.createGlyphVector(frc, unicode);
    
    Glyph glyph = this.glyphCache.get(unicode);
    if (glyph == null) {
      
      glyph = new Glyph();
      glyph.setPath(vec.getGlyphOutline(0));
      
      GlyphMetrics gm = vec.getGlyphMetrics(0);
      glyph.setHorizAdvX(gm.getAdvanceX());
      glyph.setVertAdvY(gm.getAdvanceY());
      glyph.setVertOriginX(0.0F);
      glyph.setVertOriginY(0.0F);
      
      this.glyphCache.put(unicode, glyph);
    } 
    
    return (MissingGlyph)glyph;
  }
}
