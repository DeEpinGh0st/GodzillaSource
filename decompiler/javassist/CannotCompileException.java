package javassist;

import javassist.compiler.CompileError;























public class CannotCompileException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  private Throwable myCause;
  private String message;
  
  public synchronized Throwable getCause() {
    return (this.myCause == this) ? null : this.myCause;
  }





  
  public synchronized Throwable initCause(Throwable cause) {
    this.myCause = cause;
    return this;
  }





  
  public String getReason() {
    if (this.message != null)
      return this.message; 
    return toString();
  }





  
  public CannotCompileException(String msg) {
    super(msg);
    this.message = msg;
    initCause(null);
  }






  
  public CannotCompileException(Throwable e) {
    super("by " + e.toString());
    this.message = null;
    initCause(e);
  }







  
  public CannotCompileException(String msg, Throwable e) {
    this(msg);
    initCause(e);
  }




  
  public CannotCompileException(NotFoundException e) {
    this("cannot find " + e.getMessage(), e);
  }



  
  public CannotCompileException(CompileError e) {
    this("[source error] " + e.getMessage(), (Throwable)e);
  }




  
  public CannotCompileException(ClassNotFoundException e, String name) {
    this("cannot find " + name, e);
  }



  
  public CannotCompileException(ClassFormatError e, String name) {
    this("invalid class format: " + name, e);
  }
}
