package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;









































public class Path
  extends ShapeElement
{
  public static final String TAG_NAME = "path";
  int fillRule = 1;
  String d = "";




  
  GeneralPath path;





  
  public String getTagName() {
    return "path";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();

    
    String fillRuleStrn = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
    this.fillRule = fillRuleStrn.equals("evenodd") ? 0 : 1;
    
    if (getPres(sty.setName("d")))
    {
      this.d = sty.getStringValue();
    }
    
    this.path = buildPath(this.d, this.fillRule);
  }


  
  public void render(Graphics2D g) throws SVGException {
    beginLayer(g);
    renderShape(g, this.path);
    finishLayer(g);
  }


  
  public Shape getShape() {
    return shapeToParent(this.path);
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    return boundsToParent(includeStrokeInBounds(this.path.getBounds2D()));
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);

    
    StyleAttribute sty = new StyleAttribute();
    boolean shapeChange = false;
    
    if (getStyle(sty.setName("fill-rule"))) {
      
      int newVal = sty.getStringValue().equals("evenodd") ? 0 : 1;

      
      if (newVal != this.fillRule) {
        
        this.fillRule = newVal;
        changeState = true;
      } 
    } 
    
    if (getPres(sty.setName("d"))) {
      
      String newVal = sty.getStringValue();
      if (!newVal.equals(this.d)) {
        
        this.d = newVal;
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
