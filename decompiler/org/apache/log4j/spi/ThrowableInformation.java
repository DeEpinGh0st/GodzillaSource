package org.apache.log4j.spi;

import java.io.Serializable;
import org.apache.log4j.Category;
import org.apache.log4j.DefaultThrowableRenderer;































public class ThrowableInformation
  implements Serializable
{
  static final long serialVersionUID = -4748765566864322735L;
  private transient Throwable throwable;
  private transient Category category;
  private String[] rep;
  
  public ThrowableInformation(Throwable throwable) {
    this.throwable = throwable;
  }






  
  public ThrowableInformation(Throwable throwable, Category category) {
    this.throwable = throwable;
    this.category = category;
  }





  
  public ThrowableInformation(String[] r) {
    if (r != null) {
      this.rep = (String[])r.clone();
    }
  }


  
  public Throwable getThrowable() {
    return this.throwable;
  }
  
  public synchronized String[] getThrowableStrRep() {
    if (this.rep == null) {
      ThrowableRenderer renderer = null;
      if (this.category != null) {
        LoggerRepository repo = this.category.getLoggerRepository();
        if (repo instanceof ThrowableRendererSupport) {
          renderer = ((ThrowableRendererSupport)repo).getThrowableRenderer();
        }
      } 
      if (renderer == null) {
        this.rep = DefaultThrowableRenderer.render(this.throwable);
      } else {
        this.rep = renderer.doRender(this.throwable);
      } 
    } 
    return (String[])this.rep.clone();
  }
}
