package org.springframework.objenesis;
























public class ObjenesisException
  extends RuntimeException
{
  private static final long serialVersionUID = -2677230016262426968L;
  
  public ObjenesisException(String msg) {
    super(msg);
  }



  
  public ObjenesisException(Throwable cause) {
    super(cause);
  }




  
  public ObjenesisException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
