package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;










































public class Hkern
  extends SVGElement
{
  public static final String TAG_NAME = "hkern";
  String u1;
  String u2;
  int k;
  
  public String getTagName() {
    return "hkern";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();


    
    if (getPres(sty.setName("u1")))
    {
      this.u1 = sty.getStringValue();
    }
    
    if (getPres(sty.setName("u2")))
    {
      this.u2 = sty.getStringValue();
    }
    
    if (getPres(sty.setName("k")))
    {
      this.k = sty.getIntValue();
    }
  }



  
  public boolean updateTime(double curTime) throws SVGException {
    return false;
  }
}
