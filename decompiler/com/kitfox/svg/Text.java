package com.kitfox.svg;

import com.kitfox.svg.util.FontSystem;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;








































public class Text
  extends ShapeElement
{
  public static final String TAG_NAME = "text";
  float x = 0.0F;
  float y = 0.0F;
  AffineTransform transform = null;
  
  String fontFamily;
  float fontSize;
  LinkedList<Serializable> content = new LinkedList<Serializable>();
  Shape textShape;
  public static final int TXAN_START = 0;
  public static final int TXAN_MIDDLE = 1;
  public static final int TXAN_END = 2;
  int textAnchor = 0;
  
  public static final int TXST_NORMAL = 0;
  public static final int TXST_ITALIC = 1;
  public static final int TXST_OBLIQUE = 2;
  int fontStyle;
  public static final int TXWE_NORMAL = 0;
  public static final int TXWE_BOLD = 1;
  public static final int TXWE_BOLDER = 2;
  public static final int TXWE_LIGHTER = 3;
  public static final int TXWE_100 = 4;
  public static final int TXWE_200 = 5;
  public static final int TXWE_300 = 6;
  public static final int TXWE_400 = 7;
  public static final int TXWE_500 = 8;
  public static final int TXWE_600 = 9;
  public static final int TXWE_700 = 10;
  public static final int TXWE_800 = 11;
  public static final int TXWE_900 = 12;
  int fontWeight;
  float textLength = -1.0F;
  String lengthAdjust = "spacing";









  
  public String getTagName() {
    return "text";
  }




  
  public void clearContent() {
    this.content.clear();
  }

  
  public void appendText(String text) {
    this.content.addLast(text);
  }

  
  public void appendTspan(Tspan tspan) throws SVGElementException {
    super.loaderAddChild(null, tspan);
    this.content.addLast(tspan);
  }




  
  public void rebuild() throws SVGException {
    build();
  }

  
  public List<Serializable> getContent() {
    return this.content;
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
    
    this.content.addLast(child);
  }





  
  public void loaderAddText(SVGLoaderHelper helper, String text) {
    Matcher matchWs = Pattern.compile("\\s*").matcher(text);
    if (!matchWs.matches())
    {
      this.content.addLast(text);
    }
  }


  
  public void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("x")))
    {
      this.x = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("y")))
    {
      this.y = sty.getFloatValueWithUnits();
    }
    
    if (getStyle(sty.setName("font-family"))) {
      
      this.fontFamily = sty.getStringValue();
    }
    else {
      
      this.fontFamily = "SansSerif";
    } 
    
    if (getStyle(sty.setName("font-size"))) {
      
      this.fontSize = sty.getFloatValueWithUnits();
    }
    else {
      
      this.fontSize = 12.0F;
    } 
    
    if (getStyle(sty.setName("textLength"))) {
      
      this.textLength = sty.getFloatValueWithUnits();
    }
    else {
      
      this.textLength = -1.0F;
    } 
    
    if (getStyle(sty.setName("lengthAdjust"))) {
      
      this.lengthAdjust = sty.getStringValue();
    }
    else {
      
      this.lengthAdjust = "spacing";
    } 
    
    if (getStyle(sty.setName("font-style"))) {
      
      String s = sty.getStringValue();
      if ("normal".equals(s)) {
        
        this.fontStyle = 0;
      } else if ("italic".equals(s)) {
        
        this.fontStyle = 1;
      } else if ("oblique".equals(s)) {
        
        this.fontStyle = 2;
      } 
    } else {
      
      this.fontStyle = 0;
    } 
    
    if (getStyle(sty.setName("font-weight"))) {
      
      String s = sty.getStringValue();
      if ("normal".equals(s)) {
        
        this.fontWeight = 0;
      } else if ("bold".equals(s)) {
        
        this.fontWeight = 1;
      } 
    } else {
      
      this.fontWeight = 0;
    } 
    
    if (getStyle(sty.setName("text-anchor"))) {
      
      String s = sty.getStringValue();
      if (s.equals("middle")) {
        
        this.textAnchor = 1;
      } else if (s.equals("end")) {
        
        this.textAnchor = 2;
      } else {
        
        this.textAnchor = 0;
      } 
    } else {
      
      this.textAnchor = 0;
    } 




    
    buildText();
  }
  
  protected void buildText() throws SVGException {
    FontSystem fontSystem;
    AffineTransform at;
    String[] families = this.fontFamily.split(",");
    Font font = null;
    for (int i = 0; i < families.length; i++) {
      
      font = this.diagram.getUniverse().getFont(families[i]);
      if (font != null) {
        break;
      }
    } 

    
    if (font == null)
    {
      
      fontSystem = FontSystem.createFont(this.fontFamily, this.fontStyle, this.fontWeight, this.fontSize);
    }
    
    if (fontSystem == null) {
      
      Logger.getLogger(Text.class.getName()).log(Level.WARNING, "Could not create font " + this.fontFamily);
      fontSystem = FontSystem.createFont("Serif", this.fontStyle, this.fontWeight, this.fontSize);
    } 
    
    GeneralPath textPath = new GeneralPath();
    this.textShape = textPath;
    
    float cursorX = this.x, cursorY = this.y;

    
    AffineTransform xform = new AffineTransform();
    
    for (Serializable obj : this.content) {
      if (obj instanceof String) {
        
        String text = (String)obj;
        if (text != null)
        {
          text = text.trim();
        }


        
        for (int j = 0; j < text.length(); j++) {
          
          xform.setToIdentity();
          xform.setToTranslation(cursorX, cursorY);


          
          String unicode = text.substring(j, j + 1);
          MissingGlyph glyph = fontSystem.getGlyph(unicode);
          
          Shape path = glyph.getPath();
          if (path != null) {
            
            path = xform.createTransformedShape(path);
            textPath.append(path, false);
          } 


          
          cursorX += glyph.getHorizAdvX();
        } 


        
        this.strokeWidthScalar = 1.0F; continue;
      } 
      if (obj instanceof Tspan) {
















        
        Tspan tspan = (Tspan)obj;
        Point2D cursor = new Point2D.Float(cursorX, cursorY);

        
        tspan.appendToShape(textPath, cursor);

        
        cursorX = (float)cursor.getX();
        cursorY = (float)cursor.getY();
      } 
    } 


    
    switch (this.textAnchor) {

      
      case 1:
        at = new AffineTransform();
        at.translate(-textPath.getBounds().getWidth() / 2.0D, 0.0D);
        textPath.transform(at);
        break;

      
      case 2:
        at = new AffineTransform();
        at.translate(-textPath.getBounds().getWidth(), 0.0D);
        textPath.transform(at);
        break;
    } 
  }





















































































  
  public void render(Graphics2D g) throws SVGException {
    beginLayer(g);
    renderShape(g, this.textShape);
    finishLayer(g);
  }


  
  public Shape getShape() {
    return shapeToParent(this.textShape);
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    return boundsToParent(includeStrokeInBounds(this.textShape.getBounds2D()));
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);

    
    StyleAttribute sty = new StyleAttribute();
    boolean shapeChange = false;
    
    if (getPres(sty.setName("x"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.x) {
        
        this.x = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("y"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.y) {
        
        this.y = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getStyle(sty.setName("textLength"))) {
      
      this.textLength = sty.getFloatValueWithUnits();
    }
    else {
      
      this.textLength = -1.0F;
    } 
    
    if (getStyle(sty.setName("lengthAdjust"))) {
      
      this.lengthAdjust = sty.getStringValue();
    }
    else {
      
      this.lengthAdjust = "spacing";
    } 
    
    if (getPres(sty.setName("font-family"))) {
      
      String newVal = sty.getStringValue();
      if (!newVal.equals(this.fontFamily)) {
        
        this.fontFamily = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("font-size"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.fontSize) {
        
        this.fontSize = newVal;
        shapeChange = true;
      } 
    } 

    
    if (getStyle(sty.setName("font-style"))) {
      
      String s = sty.getStringValue();
      int newVal = this.fontStyle;
      if ("normal".equals(s)) {
        
        newVal = 0;
      } else if ("italic".equals(s)) {
        
        newVal = 1;
      } else if ("oblique".equals(s)) {
        
        newVal = 2;
      } 
      if (newVal != this.fontStyle) {
        
        this.fontStyle = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getStyle(sty.setName("font-weight"))) {
      
      String s = sty.getStringValue();
      int newVal = this.fontWeight;
      if ("normal".equals(s)) {
        
        newVal = 0;
      } else if ("bold".equals(s)) {
        
        newVal = 1;
      } 
      if (newVal != this.fontWeight) {
        
        this.fontWeight = newVal;
        shapeChange = true;
      } 
    } 
    
    if (shapeChange)
    {
      build();
    }


    
    return (changeState || shapeChange);
  }
}
