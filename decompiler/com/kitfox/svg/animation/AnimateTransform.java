package com.kitfox.svg.animation;

import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGLoaderHelper;
import com.kitfox.svg.animation.parser.AnimTimeParser;
import com.kitfox.svg.xml.StyleAttribute;
import com.kitfox.svg.xml.XMLParseUtil;
import java.awt.geom.AffineTransform;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
















































public class AnimateTransform
  extends AnimateXform
{
  public static final String TAG_NAME = "animateTransform";
  private double[][] values;
  private double[] keyTimes;
  public static final int AT_REPLACE = 0;
  public static final int AT_SUM = 1;
  private int additive = 0;
  
  public static final int TR_TRANSLATE = 0;
  
  public static final int TR_ROTATE = 1;
  public static final int TR_SCALE = 2;
  public static final int TR_SKEWY = 3;
  public static final int TR_SKEWX = 4;
  public static final int TR_INVALID = 5;
  private int xformType = 5;







  
  public String getTagName() {
    return "animateTransform";
  }



  
  public void loaderStartElement(SVGLoaderHelper helper, Attributes attrs, SVGElement parent) throws SAXException {
    super.loaderStartElement(helper, attrs, parent);




    
    String type = attrs.getValue("type").toLowerCase();
    if (type.equals("translate")) this.xformType = 0; 
    if (type.equals("rotate")) this.xformType = 1; 
    if (type.equals("scale")) this.xformType = 2; 
    if (type.equals("skewx")) this.xformType = 4; 
    if (type.equals("skewy")) this.xformType = 3;
    
    String fromStrn = attrs.getValue("from");
    String toStrn = attrs.getValue("to");
    if (fromStrn != null && toStrn != null) {

      
      double[] fromValue = XMLParseUtil.parseDoubleList(fromStrn);
      fromValue = validate(fromValue);

      
      double[] toValue = XMLParseUtil.parseDoubleList(toStrn);
      toValue = validate(toValue);
      
      this.values = new double[][] { fromValue, toValue };
      this.keyTimes = new double[] { 0.0D, 1.0D };
    } 
    
    String keyTimeStrn = attrs.getValue("keyTimes");
    String valuesStrn = attrs.getValue("values");
    if (keyTimeStrn != null && valuesStrn != null) {
      
      this.keyTimes = XMLParseUtil.parseDoubleList(keyTimeStrn);
      
      String[] valueList = Pattern.compile(";").split(valuesStrn);
      this.values = new double[valueList.length][];
      for (int i = 0; i < valueList.length; i++) {
        
        double[] list = XMLParseUtil.parseDoubleList(valueList[i]);
        this.values[i] = validate(list);
      } 
    } 

    
    String additive = attrs.getValue("additive");
    if (additive != null)
    {
      if (additive.equals("sum")) this.additive = 1;
    
    }
  }




  
  private double[] validate(double[] paramList) {
    switch (this.xformType) {

      
      case 2:
        if (paramList == null) {
          
          paramList = new double[] { 1.0D, 1.0D }; break;
        } 
        if (paramList.length == 1)
        {
          paramList = new double[] { paramList[0], paramList[0] };
        }
        break;
    } 




    
    return paramList;
  }





  
  public AffineTransform eval(AffineTransform xform, double interp) {
    double d1, x1, x0, x, y, d3, y1, d2, d4, x2, y0, d6, y2, d5, d8, theta, d7, d11, d10, d9, d12;
    int idx = 0;
    for (; idx < this.keyTimes.length - 1; idx++) {
      
      if (interp >= this.keyTimes[idx]) {
        
        idx--;
        if (idx < 0) idx = 0;
        
        break;
      } 
    } 
    double spanStartTime = this.keyTimes[idx];
    double spanEndTime = this.keyTimes[idx + 1];

    
    interp = (interp - spanStartTime) / (spanEndTime - spanStartTime);
    double[] fromValue = this.values[idx];
    double[] toValue = this.values[idx + 1];
    
    switch (this.xformType)
    
    { 
      case 0:
        d1 = (fromValue.length >= 1) ? fromValue[0] : 0.0D;
        d3 = (toValue.length >= 1) ? toValue[0] : 0.0D;
        d4 = (fromValue.length >= 2) ? fromValue[1] : 0.0D;
        d6 = (toValue.length >= 2) ? toValue[1] : 0.0D;
        
        d8 = lerp(d1, d3, interp);
        d11 = lerp(d4, d6, interp);
        
        xform.setToTranslation(d8, d11);











































        
        return xform;case 1: x1 = (fromValue.length == 3) ? fromValue[1] : 0.0D; y1 = (fromValue.length == 3) ? fromValue[2] : 0.0D; x2 = (toValue.length == 3) ? toValue[1] : 0.0D; y2 = (toValue.length == 3) ? toValue[2] : 0.0D; theta = lerp(fromValue[0], toValue[0], interp); d10 = lerp(x1, x2, interp); d12 = lerp(y1, y2, interp); xform.setToRotation(Math.toRadians(theta), d10, d12); return xform;case 2: x0 = (fromValue.length >= 1) ? fromValue[0] : 1.0D; d2 = (toValue.length >= 1) ? toValue[0] : 1.0D; y0 = (fromValue.length >= 2) ? fromValue[1] : 1.0D; d5 = (toValue.length >= 2) ? toValue[1] : 1.0D; d7 = lerp(x0, d2, interp); d9 = lerp(y0, d5, interp); xform.setToScale(d7, d9); return xform;case 4: x = lerp(fromValue[0], toValue[0], interp); xform.setToShear(Math.toRadians(x), 0.0D); return xform;case 3: y = lerp(fromValue[0], toValue[0], interp); xform.setToShear(0.0D, Math.toRadians(y)); return xform; }  xform.setToIdentity(); return xform;
  }


  
  protected void rebuild(AnimTimeParser animTimeParser) throws SVGException {
    super.rebuild(animTimeParser);
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("type"))) {
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("translate")) this.xformType = 0; 
      if (strn.equals("rotate")) this.xformType = 1; 
      if (strn.equals("scale")) this.xformType = 2; 
      if (strn.equals("skewx")) this.xformType = 4; 
      if (strn.equals("skewy")) this.xformType = 3;
    
    } 
    String fromStrn = null;
    if (getPres(sty.setName("from")))
    {
      fromStrn = sty.getStringValue();
    }
    
    String toStrn = null;
    if (getPres(sty.setName("to")))
    {
      toStrn = sty.getStringValue();
    }
    
    if (fromStrn != null && toStrn != null) {
      
      double[] fromValue = XMLParseUtil.parseDoubleList(fromStrn);
      fromValue = validate(fromValue);
      
      double[] toValue = XMLParseUtil.parseDoubleList(toStrn);
      toValue = validate(toValue);
      
      this.values = new double[][] { fromValue, toValue };
    } 
    
    String keyTimeStrn = null;
    if (getPres(sty.setName("keyTimes")))
    {
      keyTimeStrn = sty.getStringValue();
    }
    
    String valuesStrn = null;
    if (getPres(sty.setName("values")))
    {
      valuesStrn = sty.getStringValue();
    }
    
    if (keyTimeStrn != null && valuesStrn != null) {
      
      this.keyTimes = XMLParseUtil.parseDoubleList(keyTimeStrn);
      
      String[] valueList = Pattern.compile(";").split(valuesStrn);
      this.values = new double[valueList.length][];
      for (int i = 0; i < valueList.length; i++) {
        
        double[] list = XMLParseUtil.parseDoubleList(valueList[i]);
        this.values[i] = validate(list);
      } 
    } 


    
    if (getPres(sty.setName("additive"))) {
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("sum")) this.additive = 1;
    
    } 
  }



  
  public double[][] getValues() {
    return this.values;
  }




  
  public void setValues(double[][] values) {
    this.values = values;
  }




  
  public double[] getKeyTimes() {
    return this.keyTimes;
  }




  
  public void setKeyTimes(double[] keyTimes) {
    this.keyTimes = keyTimes;
  }




  
  public int getAdditive() {
    return this.additive;
  }




  
  public void setAdditive(int additive) {
    this.additive = additive;
  }




  
  public int getXformType() {
    return this.xformType;
  }




  
  public void setXformType(int xformType) {
    this.xformType = xformType;
  }
}
