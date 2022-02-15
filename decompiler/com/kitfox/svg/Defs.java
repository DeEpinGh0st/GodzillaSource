package com.kitfox.svg;

















































public class Defs
  extends TransformableElement
{
  public static final String TAG_NAME = "defs";
  
  public String getTagName() {
    return "defs";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
  }




  
  public boolean updateTime(double curTime) throws SVGException {
    boolean stateChange = false;
    for (SVGElement ele : this.children) {
      stateChange = (stateChange || ele.updateTime(curTime));
    }
    
    return (super.updateTime(curTime) || stateChange);
  }
}
