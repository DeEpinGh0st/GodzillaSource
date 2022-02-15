package javassist.tools.web;


















public class BadHttpRequest
  extends Exception
{
  private static final long serialVersionUID = 1L;
  private Exception e;
  
  public BadHttpRequest() {
    this.e = null;
  } public BadHttpRequest(Exception _e) {
    this.e = _e;
  }
  
  public String toString() {
    if (this.e == null)
      return super.toString(); 
    return this.e.toString();
  }
}
