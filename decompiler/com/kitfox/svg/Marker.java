package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;






































public class Marker
  extends Group
{
  public static final String TAG_NAME = "marker";
  AffineTransform viewXform;
  AffineTransform markerXform;
  Rectangle2D viewBox;
  float refX;
  float refY;
  float markerWidth = 1.0F;
  float markerHeight = 1.0F;
  float orient = Float.NaN; boolean markerUnitsStrokeWidth = true;
  public static final int MARKER_START = 0;
  public static final int MARKER_MID = 1;
  public static final int MARKER_END = 2;
  
  public String getTagName() {
    return "marker";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("refX")))
    {
      this.refX = sty.getFloatValueWithUnits();
    }
    if (getPres(sty.setName("refY")))
    {
      this.refY = sty.getFloatValueWithUnits();
    }
    if (getPres(sty.setName("markerWidth")))
    {
      this.markerWidth = sty.getFloatValueWithUnits();
    }
    if (getPres(sty.setName("markerHeight")))
    {
      this.markerHeight = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("orient")))
    {
      if ("auto".equals(sty.getStringValue())) {
        
        this.orient = Float.NaN;
      } else {
        
        this.orient = sty.getFloatValue();
      } 
    }
    
    if (getPres(sty.setName("viewBox"))) {
      
      float[] dim = sty.getFloatList();
      this.viewBox = new Rectangle2D.Float(dim[0], dim[1], dim[2], dim[3]);
    } 
    
    if (this.viewBox == null)
    {
      this.viewBox = new Rectangle(0, 0, 1, 1);
    }
    
    if (getPres(sty.setName("markerUnits"))) {
      
      String markerUnits = sty.getStringValue();
      if (markerUnits != null && markerUnits.equals("userSpaceOnUse"))
      {
        this.markerUnitsStrokeWidth = false;
      }
    } 

    
    this.viewXform = new AffineTransform();
    this.viewXform.scale(1.0D / this.viewBox.getWidth(), 1.0D / this.viewBox.getHeight());
    this.viewXform.translate(-this.viewBox.getX(), -this.viewBox.getY());
    
    this.markerXform = new AffineTransform();
    this.markerXform.scale(this.markerWidth, this.markerHeight);
    this.markerXform.concatenate(this.viewXform);
    this.markerXform.translate(-this.refX, -this.refY);
  }


  
  protected boolean outsideClip(Graphics2D g) throws SVGException {
    Shape clip = g.getClip();
    Rectangle2D rect = super.getBoundingBox();
    if (clip == null || clip.intersects(rect))
    {
      return false;
    }
    
    return true;
  }



  
  public void render(Graphics2D g) throws SVGException {
    AffineTransform oldXform = g.getTransform();
    g.transform(this.markerXform);
    
    super.render(g);
    
    g.setTransform(oldXform);
  }

  
  public void render(Graphics2D g, MarkerPos pos, float strokeWidth) throws SVGException {
    AffineTransform cacheXform = g.getTransform();
    
    g.translate(pos.x, pos.y);
    if (this.markerUnitsStrokeWidth)
    {
      g.scale(strokeWidth, strokeWidth);
    }
    
    g.rotate(Math.atan2(pos.dy, pos.dx));
    
    g.transform(this.markerXform);
    
    super.render(g);
    
    g.setTransform(cacheXform);
  }


  
  public Shape getShape() {
    Shape shape = super.getShape();
    return this.markerXform.createTransformedShape(shape);
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    Rectangle2D rect = super.getBoundingBox();
    return this.markerXform.createTransformedShape(rect).getBounds2D();
  }









  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);
    
    build();

    
    return changeState;
  }


  
  public static class MarkerPos
  {
    int type;
    
    double x;
    
    double y;
    
    double dx;
    
    double dy;

    
    public MarkerPos(int type, double x, double y, double dx, double dy) {
      this.type = type;
      this.x = x;
      this.y = y;
      this.dx = dx;
      this.dy = dy;
    }
  }

  
  public static class MarkerLayout
  {
    private ArrayList<Marker.MarkerPos> markerList = new ArrayList<Marker.MarkerPos>();
    
    boolean started = false;
    
    public void layout(Shape shape) {
      double px = 0.0D;
      double py = 0.0D;
      double[] coords = new double[6];
      PathIterator it = shape.getPathIterator(null);
      for (; !it.isDone(); it.next()) {
        double x; double k0x; double y; double k0y; double d1; double k1x; double d2; double k1y; double d3; double d4;
        switch (it.currentSegment(coords)) {
          
          case 0:
            px = coords[0];
            py = coords[1];
            this.started = false;
            break;
          case 4:
            this.started = false;
            break;
          
          case 1:
            x = coords[0];
            y = coords[1];
            markerIn(px, py, x - px, y - py);
            markerOut(x, y, x - px, y - py);
            px = x;
            py = y;
            break;

          
          case 2:
            k0x = coords[0];
            k0y = coords[1];
            d1 = coords[2];
            d2 = coords[3];


            
            if (px != k0x || py != k0y) {
              
              markerIn(px, py, k0x - px, k0y - py);
            } else {
              
              markerIn(px, py, d1 - px, d2 - py);
            } 

            
            if (d1 != k0x || d2 != k0y) {
              
              markerOut(d1, d2, d1 - k0x, d2 - k0y);
            } else {
              
              markerOut(d1, d2, d1 - px, d2 - py);
            } 
            
            markerIn(px, py, k0x - px, k0y - py);
            markerOut(d1, d2, d1 - k0x, d2 - k0y);
            px = d1;
            py = d2;
            break;

          
          case 3:
            k0x = coords[0];
            k0y = coords[1];
            k1x = coords[2];
            k1y = coords[3];
            d3 = coords[4];
            d4 = coords[5];

            
            if (px != k0x || py != k0y) {
              
              markerIn(px, py, k0x - px, k0y - py);
            } else if (px != k1x || py != k1y) {
              
              markerIn(px, py, k1x - px, k1y - py);
            } else {
              
              markerIn(px, py, d3 - px, d4 - py);
            } 

            
            if (d3 != k1x || d4 != k1y) {
              
              markerOut(d3, d4, d3 - k1x, d4 - k1y);
            } else if (d3 != k0x || d4 != k0y) {
              
              markerOut(d3, d4, d3 - k0x, d4 - k0y);
            } else {
              
              markerOut(d3, d4, d3 - px, d4 - py);
            } 
            px = d3;
            py = d4;
            break;
        } 

      
      } 
      for (int i = 1; i < this.markerList.size(); i++) {
        
        Marker.MarkerPos prev = this.markerList.get(i - 1);
        Marker.MarkerPos cur = this.markerList.get(i);
        
        if (cur.type == 0)
        {
          prev.type = 2;
        }
      } 
      Marker.MarkerPos last = this.markerList.get(this.markerList.size() - 1);
      last.type = 2;
    }

    
    private void markerIn(double x, double y, double dx, double dy) {
      if (!this.started) {
        
        this.started = true;
        this.markerList.add(new Marker.MarkerPos(0, x, y, dx, dy));
      } 
    }

    
    private void markerOut(double x, double y, double dx, double dy) {
      this.markerList.add(new Marker.MarkerPos(1, x, y, dx, dy));
    }




    
    public ArrayList<Marker.MarkerPos> getMarkerList() {
      return this.markerList;
    }
  }
}
