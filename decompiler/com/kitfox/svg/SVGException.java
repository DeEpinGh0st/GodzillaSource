package com.kitfox.svg;



















































public class SVGException
  extends Exception
{
  public static final long serialVersionUID = 0L;
  
  public SVGException() {}
  
  public SVGException(String msg) {
    super(msg);
  }

  
  public SVGException(String msg, Throwable cause) {
    super(msg, cause);
  }

  
  public SVGException(Throwable cause) {
    super(cause);
  }
}
