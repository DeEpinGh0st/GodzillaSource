package com.kitfox.svg;














































public class SVGElementException
  extends SVGException
{
  public static final long serialVersionUID = 0L;
  private final SVGElement element;
  
  public SVGElementException(SVGElement element) {
    this(element, null, null);
  }







  
  public SVGElementException(SVGElement element, String msg) {
    this(element, msg, null);
  }

  
  public SVGElementException(SVGElement element, String msg, Throwable cause) {
    super(msg, cause);
    this.element = element;
  }

  
  public SVGElementException(SVGElement element, Throwable cause) {
    this(element, null, cause);
  }

  
  public SVGElement getElement() {
    return this.element;
  }
}
