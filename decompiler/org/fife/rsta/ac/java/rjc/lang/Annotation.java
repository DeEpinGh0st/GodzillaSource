package org.fife.rsta.ac.java.rjc.lang;













public class Annotation
{
  private Type type;
  
  public Annotation(Type type) {
    this.type = type;
  }


  
  public String toString() {
    return "@" + this.type.toString();
  }
}
