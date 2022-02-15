package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import com.kitfox.svg.xml.XMLParseUtil;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;








































public class Polyline
  extends ShapeElement
{
  public static final String TAG_NAME = "polyline";
  int fillRule = 1;
  String pointsStrn = "";




  
  GeneralPath path;




  
  public String getTagName() {
    return "polyline";
  }


  
  public void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("points")))
    {
      this.pointsStrn = sty.getStringValue();
    }
    
    String fillRuleStrn = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
    this.fillRule = fillRuleStrn.equals("evenodd") ? 0 : 1;
    
    buildPath();
  }

  
  protected void buildPath() {
    float[] points = XMLParseUtil.parseFloatList(this.pointsStrn);
    this.path = new GeneralPath(this.fillRule, points.length / 2);
    
    this.path.moveTo(points[0], points[1]);
    for (int i = 2; i < points.length; i += 2)
    {
      this.path.lineTo(points[i], points[i + 1]);
    }
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
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("points"))) {
      
      String newVal = sty.getStringValue();
      if (!newVal.equals(this.pointsStrn)) {
        
        this.pointsStrn = newVal;
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
