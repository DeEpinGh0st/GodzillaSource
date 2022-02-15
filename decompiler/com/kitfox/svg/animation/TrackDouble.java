package com.kitfox.svg.animation;

import com.kitfox.svg.SVGElementException;
import com.kitfox.svg.xml.StyleAttribute;















































public class TrackDouble
  extends TrackBase
{
  public TrackDouble(AnimationElement ele) throws SVGElementException {
    super(ele.getParent(), ele);
  }


  
  public boolean getValue(StyleAttribute attrib, double curTime) {
    double val = getValue(curTime);
    if (Double.isNaN(val)) return false;
    
    attrib.setStringValue("" + val);
    return true;
  }

  
  public double getValue(double curTime) {
    double retVal = Double.NaN;
    
    StyleAttribute attr = null;
    switch (this.attribType) {
      
      case 0:
        attr = this.parent.getStyleAbsolute(this.attribName);
        retVal = attr.getDoubleValue();
        break;
      case 1:
        attr = this.parent.getPresAbsolute(this.attribName);
        retVal = attr.getDoubleValue();
        break;
      case 2:
        attr = this.parent.getStyleAbsolute(this.attribName);
        if (attr == null) attr = this.parent.getPresAbsolute(this.attribName); 
        retVal = attr.getDoubleValue();
        break;
    } 


    
    AnimationTimeEval state = new AnimationTimeEval();

    
    for (AnimationElement animationElement : this.animEvents) {
      Animate ele = (Animate)animationElement;
      ele.evalParametric(state, curTime);

      
      if (Double.isNaN(state.interp))
        continue; 
      switch (ele.getAdditiveType()) {
        
        case 1:
          retVal += ele.eval(state.interp);
          break;
        case 0:
          retVal = ele.eval(state.interp);
          break;
      } 

      
      if (state.rep > 0)
      {
        switch (ele.getAccumulateType()) {
          
          case 1:
            retVal += ele.repeatSkipSize(state.rep);
        } 


      
      }
    } 
    return retVal;
  }
}
