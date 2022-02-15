package com.kitfox.svg.animation;

import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGLoaderHelper;
import com.kitfox.svg.animation.parser.AnimTimeParser;
import com.kitfox.svg.xml.ColorTable;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Color;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

















































public class AnimateColor
  extends AnimateBase
  implements AnimateColorIface
{
  public static final String TAG_NAME = "animateColor";
  private Color fromValue;
  private Color toValue;
  
  public String getTagName() {
    return "animateColor";
  }



  
  public void loaderStartElement(SVGLoaderHelper helper, Attributes attrs, SVGElement parent) throws SAXException {
    super.loaderStartElement(helper, attrs, parent);
    
    String strn = attrs.getValue("from");
    this.fromValue = ColorTable.parseColor(strn);
    
    strn = attrs.getValue("to");
    this.toValue = ColorTable.parseColor(strn);
  }






  
  public Color evalColor(double interp) {
    int r1 = this.fromValue.getRed();
    int g1 = this.fromValue.getGreen();
    int b1 = this.fromValue.getBlue();
    int r2 = this.toValue.getRed();
    int g2 = this.toValue.getGreen();
    int b2 = this.toValue.getBlue();
    double invInterp = 1.0D - interp;
    
    return new Color((int)(r1 * invInterp + r2 * interp), (int)(g1 * invInterp + g2 * interp), (int)(b1 * invInterp + b2 * interp));
  }




  
  protected void rebuild(AnimTimeParser animTimeParser) throws SVGException {
    super.rebuild(animTimeParser);
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("from"))) {
      
      String strn = sty.getStringValue();
      this.fromValue = ColorTable.parseColor(strn);
    } 
    
    if (getPres(sty.setName("to"))) {
      
      String strn = sty.getStringValue();
      this.toValue = ColorTable.parseColor(strn);
    } 
  }




  
  public Color getFromValue() {
    return this.fromValue;
  }




  
  public void setFromValue(Color fromValue) {
    this.fromValue = fromValue;
  }




  
  public Color getToValue() {
    return this.toValue;
  }




  
  public void setToValue(Color toValue) {
    this.toValue = toValue;
  }
}
