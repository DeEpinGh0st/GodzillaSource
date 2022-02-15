package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Shape;
import java.awt.geom.Area;








































public class ClipPath
  extends SVGElement
{
  public static final String TAG_NAME = "clippath";
  public static final int CP_USER_SPACE_ON_USE = 0;
  public static final int CP_OBJECT_BOUNDING_BOX = 1;
  int clipPathUnits = 0;









  
  public String getTagName() {
    return "clippath";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    this
      .clipPathUnits = (getPres(sty.setName("clipPathUnits")) && sty.getStringValue().equals("objectBoundingBox")) ? 1 : 0;
  }



  
  public int getClipPathUnits() {
    return this.clipPathUnits;
  }

  
  public Shape getClipPathShape() {
    if (this.children.isEmpty())
    {
      return null;
    }
    if (this.children.size() == 1)
    {
      return ((ShapeElement)this.children.get(0)).getShape();
    }
    
    Area clipArea = null;
    for (SVGElement svgElement : this.children) {
      ShapeElement se = (ShapeElement)svgElement;
      
      if (clipArea == null) {
        
        Shape shape1 = se.getShape();
        if (shape1 != null)
        {
          clipArea = new Area(se.getShape());
        }
        
        continue;
      } 
      Shape shape = se.getShape();
      if (shape != null)
      {
        clipArea.intersect(new Area(shape));
      }
    } 
    
    return clipArea;
  }












  
  public boolean updateTime(double curTime) throws SVGException {
    StyleAttribute sty = new StyleAttribute();
    boolean shapeChange = false;

    
    if (getPres(sty.setName("clipPathUnits"))) {
      
      String newUnitsStrn = sty.getStringValue();
      int newUnits = newUnitsStrn.equals("objectBoundingBox") ? 1 : 0;


      
      if (newUnits != this.clipPathUnits) {
        
        this.clipPathUnits = newUnits;
        shapeChange = true;
      } 
    } 
    
    if (shapeChange)
    {
      build();
    }
    
    for (int i = 0; i < this.children.size(); i++) {
      
      SVGElement ele = this.children.get(i);
      ele.updateTime(curTime);
    } 
    
    return shapeChange;
  }
}
