package com.kitfox.svg;










































public class Desc
  extends SVGElement
{
  public static final String TAG_NAME = "desc";
  StringBuffer text = new StringBuffer();









  
  public String getTagName() {
    return "desc";
  }





  
  public void loaderAddText(SVGLoaderHelper helper, String text) {
    this.text.append(text);
  }

  
  public String getText() {
    return this.text.toString();
  }


  
  public boolean updateTime(double curTime) {
    return false;
  }
}
