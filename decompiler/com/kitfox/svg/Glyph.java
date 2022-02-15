package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;

























































public class Glyph
  extends MissingGlyph
{
  public static final String TAG_NAME = "missingglyph";
  String unicode;
  
  public String getTagName() {
    return "missingglyph";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("unicode")))
    {
      this.unicode = sty.getStringValue();
    }
  }

  
  public String getUnicode() {
    return this.unicode;
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    return false;
  }
}
