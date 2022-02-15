package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import com.kitfox.svg.xml.StyleSheet;











































public class Style
  extends SVGElement
{
  public static final String TAG_NAME = "style";
  String type;
  StringBuffer text = new StringBuffer();




  
  StyleSheet styleSheet;





  
  public String getTagName() {
    return "style";
  }





  
  public void loaderAddText(SVGLoaderHelper helper, String text) {
    this.text.append(text);

    
    this.styleSheet = null;
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("type")))
    {
      this.type = sty.getStringValue();
    }
  }



  
  public boolean updateTime(double curTime) throws SVGException {
    return false;
  }

  
  public StyleSheet getStyleSheet() {
    if (this.styleSheet == null && this.text.length() > 0)
    {
      this.styleSheet = StyleSheet.parseSheet(this.text.toString());
    }
    return this.styleSheet;
  }
}
