package com.kitfox.svg;










































public class Title
  extends SVGElement
{
  public static final String TAG_NAME = "title";
  StringBuffer text = new StringBuffer();









  
  public String getTagName() {
    return "title";
  }





  
  public void loaderAddText(SVGLoaderHelper helper, String text) {
    this.text.append(text);
  }

  
  public String getText() {
    return this.text.toString();
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    return false;
  }
}
