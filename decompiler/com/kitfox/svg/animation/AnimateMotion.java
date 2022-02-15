package com.kitfox.svg.animation;

import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGLoaderHelper;
import com.kitfox.svg.animation.parser.AnimTimeParser;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;










































public class AnimateMotion
  extends AnimateXform
{
  public static final String TAG_NAME = "animateMotion";
  static final Matcher matchPoint = Pattern.compile("\\s*(\\d+)[^\\d]+(\\d+)\\s*").matcher("");

  
  private GeneralPath path;
  
  private int rotateType = 0;
  
  private double rotate;
  
  public static final int RT_ANGLE = 0;
  public static final int RT_AUTO = 1;
  final ArrayList<Bezier> bezierSegs = new ArrayList<Bezier>();



  
  double curveLength;



  
  public String getTagName() {
    return "animateMotion";
  }



  
  public void loaderStartElement(SVGLoaderHelper helper, Attributes attrs, SVGElement parent) throws SAXException {
    super.loaderStartElement(helper, attrs, parent);

    
    if (this.attribName == null) {
      
      this.attribName = "transform";
      this.attribType = 2;
      setAdditiveType(1);
    } 

    
    String path = attrs.getValue("path");
    if (path != null)
    {
      this.path = buildPath(path, 1);
    }

    
    String rotate = attrs.getValue("rotate");
    if (rotate != null)
    {
      if (rotate.equals("auto")) {
        
        this.rotateType = 1;
      } else {

        
        try { this.rotate = Math.toRadians(Float.parseFloat(rotate)); } catch (Exception exception) {}
      } 
    }

    
    String from = attrs.getValue("from");
    String to = attrs.getValue("to");
    
    buildPath(from, to);
  }
  
  protected static void setPoint(Point2D.Float pt, String x, String y) {
    
    try { pt.x = Float.parseFloat(x); } catch (Exception exception) {}
    
    try { pt.y = Float.parseFloat(y); } catch (Exception exception) {}
  }

  
  private void buildPath(String from, String to) {
    if (from != null && to != null) {
      
      Point2D.Float ptFrom = new Point2D.Float(), ptTo = new Point2D.Float();
      
      matchPoint.reset(from);
      if (matchPoint.matches())
      {
        setPoint(ptFrom, matchPoint.group(1), matchPoint.group(2));
      }
      
      matchPoint.reset(to);
      if (matchPoint.matches())
      {
        setPoint(ptFrom, matchPoint.group(1), matchPoint.group(2));
      }
      
      if (ptFrom != null && ptTo != null) {
        
        this.path = new GeneralPath();
        this.path.moveTo(ptFrom.x, ptFrom.y);
        this.path.lineTo(ptTo.x, ptTo.y);
      } 
    } 
    
    paramaterizePath();
  }

  
  private void paramaterizePath() {
    this.bezierSegs.clear();
    this.curveLength = 0.0D;
    
    double[] coords = new double[6];
    double sx = 0.0D, sy = 0.0D;
    
    for (PathIterator pathIt = this.path.getPathIterator(new AffineTransform()); !pathIt.isDone(); pathIt.next()) {
      
      Bezier bezier = null;
      
      int segType = pathIt.currentSegment(coords);
      
      switch (segType) {

        
        case 1:
          bezier = new Bezier(sx, sy, coords, 1);
          sx = coords[0];
          sy = coords[1];
          break;

        
        case 2:
          bezier = new Bezier(sx, sy, coords, 2);
          sx = coords[2];
          sy = coords[3];
          break;

        
        case 3:
          bezier = new Bezier(sx, sy, coords, 3);
          sx = coords[4];
          sy = coords[5];
          break;

        
        case 0:
          sx = coords[0];
          sy = coords[1];
          break;
      } 





      
      if (bezier != null) {
        
        this.bezierSegs.add(bezier);
        this.curveLength += bezier.getLength();
      } 
    } 
  }






  
  public AffineTransform eval(AffineTransform xform, double interp) {
    Point2D.Double point = new Point2D.Double();
    
    if (interp >= 1.0D) {
      
      Bezier last = this.bezierSegs.get(this.bezierSegs.size() - 1);
      last.getFinalPoint(point);
      xform.setToTranslation(point.x, point.y);
      return xform;
    } 
    
    double curLength = this.curveLength * interp;
    for (Bezier bez : this.bezierSegs) {
      double bezLength = bez.getLength();
      if (curLength < bezLength) {
        
        double param = curLength / bezLength;
        bez.eval(param, point);
        
        break;
      } 
      curLength -= bezLength;
    } 
    
    xform.setToTranslation(point.x, point.y);
    
    return xform;
  }



  
  protected void rebuild(AnimTimeParser animTimeParser) throws SVGException {
    super.rebuild(animTimeParser);
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("path"))) {
      
      String strn = sty.getStringValue();
      this.path = buildPath(strn, 1);
    } 
    
    if (getPres(sty.setName("rotate"))) {
      
      String strn = sty.getStringValue();
      if (strn.equals("auto")) {
        
        this.rotateType = 1;
      } else {

        
        try { this.rotate = Math.toRadians(Float.parseFloat(strn)); } catch (Exception exception) {}
      } 
    } 
    
    String from = null;
    if (getPres(sty.setName("from")))
    {
      from = sty.getStringValue();
    }
    
    String to = null;
    if (getPres(sty.setName("to")))
    {
      to = sty.getStringValue();
    }
    
    buildPath(from, to);
  }




  
  public GeneralPath getPath() {
    return this.path;
  }




  
  public void setPath(GeneralPath path) {
    this.path = path;
  }




  
  public int getRotateType() {
    return this.rotateType;
  }




  
  public void setRotateType(int rotateType) {
    this.rotateType = rotateType;
  }




  
  public double getRotate() {
    return this.rotate;
  }




  
  public void setRotate(double rotate) {
    this.rotate = rotate;
  }
}
