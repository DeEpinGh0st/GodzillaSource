package com.kitfox.svg.animation;

import com.kitfox.svg.SVGElementException;
import com.kitfox.svg.pathcmd.PathUtil;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.geom.GeneralPath;
















































public class TrackPath
  extends TrackBase
{
  public TrackPath(AnimationElement ele) throws SVGElementException {
    super(ele.getParent(), ele);
  }


  
  public boolean getValue(StyleAttribute attrib, double curTime) {
    GeneralPath path = getValue(curTime);
    if (path == null) return false;
    
    attrib.setStringValue(PathUtil.buildPathString(path));
    return true;
  }

  
  public GeneralPath getValue(double curTime) {
    GeneralPath retVal = null;
    AnimationTimeEval state = new AnimationTimeEval();
    
    for (AnimationElement animationElement : this.animEvents) {
      AnimateBase ele = (AnimateBase)animationElement;
      Animate eleAnim = (Animate)ele;
      ele.evalParametric(state, curTime);

      
      if (Double.isNaN(state.interp))
        continue; 
      if (retVal == null) {
        
        retVal = eleAnim.evalPath(state.interp);
        
        continue;
      } 
      GeneralPath curPath = eleAnim.evalPath(state.interp);
      switch (ele.getAdditiveType()) {
        
        case 0:
          retVal = curPath;
          continue;
        case 1:
          throw new RuntimeException("Not implemented");
      } 

      
      throw new RuntimeException();
    } 

    
    return retVal;
  }
}
