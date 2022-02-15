package com.kitfox.svg;

import com.kitfox.svg.pathcmd.BuildHistory;
import com.kitfox.svg.pathcmd.PathCommand;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;













































public class MissingGlyph
  extends ShapeElement
{
  public static final String TAG_NAME = "missingglyph";
  private Shape path = null;
  
  private float horizAdvX = -1.0F;
  private float vertOriginX = -1.0F;
  private float vertOriginY = -1.0F;
  private float vertAdvY = -1.0F;









  
  public String getTagName() {
    return "missingglyph";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    String commandList = "";
    if (getPres(sty.setName("d")))
    {
      commandList = sty.getStringValue();
    }


    
    if (commandList != null) {
      
      String fillRule = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
      
      PathCommand[] commands = parsePathList(commandList);

      
      GeneralPath buildPath = new GeneralPath(fillRule.equals("evenodd") ? 0 : 1, commands.length);

      
      BuildHistory hist = new BuildHistory();
      
      for (int i = 0; i < commands.length; i++) {
        
        PathCommand cmd = commands[i];
        cmd.appendPath(buildPath, hist);
      } 

      
      AffineTransform at = new AffineTransform();
      at.scale(1.0D, -1.0D);
      this.path = at.createTransformedShape(buildPath);
    } 


    
    if (getPres(sty.setName("horiz-adv-x")))
    {
      this.horizAdvX = sty.getFloatValue();
    }
    
    if (getPres(sty.setName("vert-origin-x")))
    {
      this.vertOriginX = sty.getFloatValue();
    }
    
    if (getPres(sty.setName("vert-origin-y")))
    {
      this.vertOriginY = sty.getFloatValue();
    }
    
    if (getPres(sty.setName("vert-adv-y")))
    {
      this.vertAdvY = sty.getFloatValue();
    }
  }

  
  public Shape getPath() {
    return this.path;
  }




  
  public void render(Graphics2D g) throws SVGException {
    if (this.path != null)
    {
      renderShape(g, this.path);
    }
    
    Iterator<SVGElement> it = this.children.iterator();
    while (it.hasNext()) {
      
      SVGElement ele = it.next();
      if (ele instanceof RenderableElement)
      {
        ((RenderableElement)ele).render(g);
      }
    } 
  }



  
  public float getHorizAdvX() {
    if (this.horizAdvX == -1.0F)
    {
      this.horizAdvX = ((Font)this.parent).getHorizAdvX();
    }
    return this.horizAdvX;
  }

  
  public float getVertOriginX() {
    if (this.vertOriginX == -1.0F)
    {
      this.vertOriginX = getHorizAdvX() / 2.0F;
    }
    return this.vertOriginX;
  }

  
  public float getVertOriginY() {
    if (this.vertOriginY == -1.0F)
    {
      this.vertOriginY = ((Font)this.parent).getFontFace().getAscent();
    }
    return this.vertOriginY;
  }

  
  public float getVertAdvY() {
    if (this.vertAdvY == -1.0F)
    {
      this.vertAdvY = ((Font)this.parent).getFontFace().getUnitsPerEm();
    }
    return this.vertAdvY;
  }



  
  public Shape getShape() {
    if (this.path != null)
    {
      return shapeToParent(this.path);
    }
    return null;
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    if (this.path != null)
    {
      return boundsToParent(includeStrokeInBounds(this.path.getBounds2D()));
    }
    return null;
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    return false;
  }




  
  public void setPath(Shape path) {
    this.path = path;
  }




  
  public void setHorizAdvX(float horizAdvX) {
    this.horizAdvX = horizAdvX;
  }




  
  public void setVertOriginX(float vertOriginX) {
    this.vertOriginX = vertOriginX;
  }




  
  public void setVertOriginY(float vertOriginY) {
    this.vertOriginY = vertOriginY;
  }




  
  public void setVertAdvY(float vertAdvY) {
    this.vertAdvY = vertAdvY;
  }
}
