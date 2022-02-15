package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.util.HashMap;












































public class Font
  extends SVGElement
{
  public static final String TAG_NAME = "font";
  int horizOriginX = 0;
  int horizOriginY = 0;
  int horizAdvX = -1;
  int vertOriginX = -1;
  int vertOriginY = -1;
  int vertAdvY = -1;
  FontFace fontFace = null;
  MissingGlyph missingGlyph = null;
  final HashMap<String, SVGElement> glyphs = new HashMap<String, SVGElement>();









  
  public String getTagName() {
    return "font";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
    
    if (child instanceof Glyph) {
      
      this.glyphs.put(((Glyph)child).getUnicode(), child);
    } else if (child instanceof MissingGlyph) {
      
      this.missingGlyph = (MissingGlyph)child;
    } else if (child instanceof FontFace) {
      
      this.fontFace = (FontFace)child;
    } 
  }


  
  public void loaderEndElement(SVGLoaderHelper helper) throws SVGParseException {
    super.loaderEndElement(helper);


    
    helper.universe.registerFont(this);
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("horiz-origin-x")))
    {
      this.horizOriginX = sty.getIntValue();
    }
    
    if (getPres(sty.setName("horiz-origin-y")))
    {
      this.horizOriginY = sty.getIntValue();
    }
    
    if (getPres(sty.setName("horiz-adv-x")))
    {
      this.horizAdvX = sty.getIntValue();
    }
    
    if (getPres(sty.setName("vert-origin-x")))
    {
      this.vertOriginX = sty.getIntValue();
    }
    
    if (getPres(sty.setName("vert-origin-y")))
    {
      this.vertOriginY = sty.getIntValue();
    }
    
    if (getPres(sty.setName("vert-adv-y")))
    {
      this.vertAdvY = sty.getIntValue();
    }
  }

  
  public FontFace getFontFace() {
    return this.fontFace;
  }

  
  public void setFontFace(FontFace face) {
    this.fontFace = face;
  }

  
  public MissingGlyph getGlyph(String unicode) {
    Glyph retVal = (Glyph)this.glyphs.get(unicode);
    if (retVal == null)
    {
      return this.missingGlyph;
    }
    return retVal;
  }

  
  public int getHorizOriginX() {
    return this.horizOriginX;
  }

  
  public int getHorizOriginY() {
    return this.horizOriginY;
  }

  
  public int getHorizAdvX() {
    return this.horizAdvX;
  }

  
  public int getVertOriginX() {
    if (this.vertOriginX != -1)
    {
      return this.vertOriginX;
    }
    this.vertOriginX = getHorizAdvX() / 2;
    return this.vertOriginX;
  }

  
  public int getVertOriginY() {
    if (this.vertOriginY != -1)
    {
      return this.vertOriginY;
    }
    this.vertOriginY = this.fontFace.getAscent();
    return this.vertOriginY;
  }

  
  public int getVertAdvY() {
    if (this.vertAdvY != -1)
    {
      return this.vertAdvY;
    }
    this.vertAdvY = this.fontFace.getUnitsPerEm();
    return this.vertAdvY;
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    return false;
  }
}
