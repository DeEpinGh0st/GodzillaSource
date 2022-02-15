package javassist.tools.reflect;



















public class CannotCreateException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  
  public CannotCreateException(String s) {
    super(s);
  }
  
  public CannotCreateException(Exception e) {
    super("by " + e.toString());
  }
}
