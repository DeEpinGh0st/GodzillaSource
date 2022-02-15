package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;









































public abstract class Gradient
  extends FillElement
{
  public static final String TAG_NAME = "gradient";
  public static final int SM_PAD = 0;
  public static final int SM_REPEAT = 1;
  public static final int SM_REFLECT = 2;
  int spreadMethod = 0;
  public static final int GU_OBJECT_BOUNDING_BOX = 0;
  public static final int GU_USER_SPACE_ON_USE = 1;
  protected int gradientUnits = 0;

  
  ArrayList<Stop> stops = new ArrayList<Stop>();
  URI stopRef = null;
  protected AffineTransform gradientTransform = null;



  
  float[] stopFractions;



  
  Color[] stopColors;



  
  public String getTagName() {
    return "gradient";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
    
    if (!(child instanceof Stop)) {
      return;
    }
    
    appendStop((Stop)child);
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();

    
    if (getPres(sty.setName("spreadMethod"))) {
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("repeat")) {
        
        this.spreadMethod = 1;
      } else if (strn.equals("reflect")) {
        
        this.spreadMethod = 2;
      } else {
        
        this.spreadMethod = 0;
      } 
    } 
    
    if (getPres(sty.setName("gradientUnits"))) {
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("userspaceonuse")) {
        
        this.gradientUnits = 1;
      } else {
        
        this.gradientUnits = 0;
      } 
    } 
    
    if (getPres(sty.setName("gradientTransform")))
    {
      this.gradientTransform = parseTransform(sty.getStringValue());
    }
    
    if (this.gradientTransform == null)
    {
      this.gradientTransform = new AffineTransform();
    }


    
    if (getPres(sty.setName("xlink:href"))) {
      
      try {
        
        this.stopRef = sty.getURIValue(getXMLBase());

      
      }
      catch (Exception e) {
        
        throw new SVGException("Could not resolve relative URL in Gradient: " + sty.getStringValue() + ", " + getXMLBase(), e);
      } 
    }
  }

  
  private void buildStops() {
    ArrayList<Stop> stopList = new ArrayList<Stop>(this.stops);
    stopList.sort(new Comparator<Stop>()
        {
          public int compare(Stop o1, Stop o2) {
            return Float.compare(o1.offset, o2.offset);
          }
        });

    
    for (int i = stopList.size() - 2; i > 0; i--) {
      
      if (((Stop)stopList.get(i + 1)).offset == ((Stop)stopList.get(i)).offset)
      {
        stopList.remove(i + 1);
      }
    } 

    
    this.stopFractions = new float[stopList.size()];
    this.stopColors = new Color[stopList.size()];
    int idx = 0;
    for (Stop stop : stopList) {
      
      int stopColorVal = stop.color.getRGB();
      Color stopColor = new Color(stopColorVal >> 16 & 0xFF, stopColorVal >> 8 & 0xFF, stopColorVal & 0xFF, clamp((int)(stop.opacity * 255.0F), 0, 255));
      
      this.stopColors[idx] = stopColor;
      this.stopFractions[idx] = stop.offset;
      idx++;
    } 
  }


  
  public float[] getStopFractions() {
    if (this.stopRef != null) {
      
      Gradient grad = (Gradient)this.diagram.getUniverse().getElement(this.stopRef);
      return grad.getStopFractions();
    } 
    
    if (this.stopFractions != null)
    {
      return this.stopFractions;
    }
    
    buildStops();
    
    return this.stopFractions;
  }

  
  public Color[] getStopColors() {
    if (this.stopRef != null) {
      
      Gradient grad = (Gradient)this.diagram.getUniverse().getElement(this.stopRef);
      return grad.getStopColors();
    } 
    
    if (this.stopColors != null)
    {
      return this.stopColors;
    }
    
    buildStops();
    
    return this.stopColors;
  }













  
  private int clamp(int val, int min, int max) {
    if (val < min)
    {
      return min;
    }
    if (val > max)
    {
      return max;
    }
    return val;
  }

  
  public void setStopRef(URI grad) {
    this.stopRef = grad;
  }

  
  public void appendStop(Stop stop) {
    this.stops.add(stop);
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    boolean stateChange = false;

    
    StyleAttribute sty = new StyleAttribute();


    
    if (getPres(sty.setName("spreadMethod"))) {
      int newVal;
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("repeat")) {
        
        newVal = 1;
      } else if (strn.equals("reflect")) {
        
        newVal = 2;
      } else {
        
        newVal = 0;
      } 
      if (this.spreadMethod != newVal) {
        
        this.spreadMethod = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("gradientUnits"))) {
      int newVal;
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("userspaceonuse")) {
        
        newVal = 1;
      } else {
        
        newVal = 0;
      } 
      if (newVal != this.gradientUnits) {
        
        this.gradientUnits = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("gradientTransform"))) {
      
      AffineTransform newVal = parseTransform(sty.getStringValue());
      if (newVal != null && newVal.equals(this.gradientTransform)) {
        
        this.gradientTransform = newVal;
        stateChange = true;
      } 
    } 


    
    if (getPres(sty.setName("xlink:href"))) {
      
      try {
        
        URI newVal = sty.getURIValue(getXMLBase());
        if ((newVal == null && this.stopRef != null) || !newVal.equals(this.stopRef)) {
          
          this.stopRef = newVal;
          stateChange = true;
        } 
      } catch (Exception e) {
        
        Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Could not parse xlink:href", e);
      } 
    }


    
    for (Stop stop : this.stops) {
      if (stop.updateTime(curTime)) {
        
        stateChange = true;
        this.stopFractions = null;
        this.stopColors = null;
      } 
    } 
    
    return stateChange;
  }
}
