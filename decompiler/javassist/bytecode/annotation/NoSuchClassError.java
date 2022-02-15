package javassist.bytecode.annotation;























public class NoSuchClassError
  extends Error
{
  private static final long serialVersionUID = 1L;
  private String className;
  
  public NoSuchClassError(String className, Error cause) {
    super(cause.toString(), cause);
    this.className = className;
  }



  
  public String getClassName() {
    return this.className;
  }
}
