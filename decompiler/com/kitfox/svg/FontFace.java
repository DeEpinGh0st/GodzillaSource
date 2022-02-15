package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;















































public class FontFace
  extends SVGElement
{
  public static final String TAG_NAME = "fontface";
  String fontFamily;
  private int unitsPerEm = 1000;
  private int ascent = -1;
  private int descent = -1;
  private int accentHeight = -1;
  private int underlinePosition = -1;
  private int underlineThickness = -1;
  private int strikethroughPosition = -1;
  private int strikethroughThickness = -1;
  private int overlinePosition = -1;
  private int overlineThickness = -1;









  
  public String getTagName() {
    return "fontface";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("font-family")))
    {
      this.fontFamily = sty.getStringValue();
    }
    
    if (getPres(sty.setName("units-per-em")))
    {
      this.unitsPerEm = sty.getIntValue();
    }
    if (getPres(sty.setName("ascent")))
    {
      this.ascent = sty.getIntValue();
    }
    if (getPres(sty.setName("descent")))
    {
      this.descent = sty.getIntValue();
    }
    if (getPres(sty.setName("accent-height")))
    {
      this.accentHeight = sty.getIntValue();
    }
    
    if (getPres(sty.setName("underline-position")))
    {
      this.underlinePosition = sty.getIntValue();
    }
    if (getPres(sty.setName("underline-thickness")))
    {
      this.underlineThickness = sty.getIntValue();
    }
    if (getPres(sty.setName("strikethrough-position")))
    {
      this.strikethroughPosition = sty.getIntValue();
    }
    if (getPres(sty.setName("strikethrough-thickenss")))
    {
      this.strikethroughThickness = sty.getIntValue();
    }
    if (getPres(sty.setName("overline-position")))
    {
      this.overlinePosition = sty.getIntValue();
    }
    if (getPres(sty.setName("overline-thickness")))
    {
      this.overlineThickness = sty.getIntValue();
    }
  }

  
  public String getFontFamily() {
    return this.fontFamily;
  }

  
  public int getUnitsPerEm() {
    return this.unitsPerEm;
  }

  
  public int getAscent() {
    if (this.ascent == -1)
    {
      this.ascent = this.unitsPerEm - ((Font)this.parent).getVertOriginY();
    }
    return this.ascent;
  }

  
  public int getDescent() {
    if (this.descent == -1)
    {
      this.descent = ((Font)this.parent).getVertOriginY();
    }
    return this.descent;
  }

  
  public int getAccentHeight() {
    if (this.accentHeight == -1)
    {
      this.accentHeight = getAscent();
    }
    return this.accentHeight;
  }

  
  public int getUnderlinePosition() {
    if (this.underlinePosition == -1)
    {
      this.underlinePosition = this.unitsPerEm * 5 / 6;
    }
    return this.underlinePosition;
  }

  
  public int getUnderlineThickness() {
    if (this.underlineThickness == -1)
    {
      this.underlineThickness = this.unitsPerEm / 20;
    }
    return this.underlineThickness;
  }

  
  public int getStrikethroughPosition() {
    if (this.strikethroughPosition == -1)
    {
      this.strikethroughPosition = this.unitsPerEm * 3 / 6;
    }
    return this.strikethroughPosition;
  }

  
  public int getStrikethroughThickness() {
    if (this.strikethroughThickness == -1)
    {
      this.strikethroughThickness = this.unitsPerEm / 20;
    }
    return this.strikethroughThickness;
  }

  
  public int getOverlinePosition() {
    if (this.overlinePosition == -1)
    {
      this.overlinePosition = this.unitsPerEm * 5 / 6;
    }
    return this.overlinePosition;
  }

  
  public int getOverlineThickness() {
    if (this.overlineThickness == -1)
    {
      this.overlineThickness = this.unitsPerEm / 20;
    }
    return this.overlineThickness;
  }










  
  public boolean updateTime(double curTime) {
    return false;
  }




  
  public void setUnitsPerEm(int unitsPerEm) {
    this.unitsPerEm = unitsPerEm;
  }




  
  public void setAscent(int ascent) {
    this.ascent = ascent;
  }




  
  public void setDescent(int descent) {
    this.descent = descent;
  }




  
  public void setAccentHeight(int accentHeight) {
    this.accentHeight = accentHeight;
  }




  
  public void setUnderlinePosition(int underlinePosition) {
    this.underlinePosition = underlinePosition;
  }




  
  public void setUnderlineThickness(int underlineThickness) {
    this.underlineThickness = underlineThickness;
  }




  
  public void setStrikethroughPosition(int strikethroughPosition) {
    this.strikethroughPosition = strikethroughPosition;
  }




  
  public void setStrikethroughThickness(int strikethroughThickness) {
    this.strikethroughThickness = strikethroughThickness;
  }




  
  public void setOverlinePosition(int overlinePosition) {
    this.overlinePosition = overlinePosition;
  }




  
  public void setOverlineThickness(int overlineThickness) {
    this.overlineThickness = overlineThickness;
  }
}
