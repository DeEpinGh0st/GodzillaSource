package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.net.URI;
















































public class A
  extends Group
{
  public static final String TAG_NAME = "a";
  URI href;
  String title;
  
  public String getTagName() {
    return "a";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("xlink:href")))
    {
      this.href = sty.getURIValue(getXMLBase());
    }
    
    if (getPres(sty.setName("xlink:title")))
    {
      this.title = sty.getStringValue();
    }
  }








  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);


    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("xlink:href")))
    {
      this.href = sty.getURIValue(getXMLBase());
    }
    
    if (getPres(sty.setName("xlink:title")))
    {
      this.title = sty.getStringValue();
    }
    
    return changeState;
  }
}
