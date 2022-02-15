package org.fife.rsta.ac.java.classreader.attributes;

import org.fife.rsta.ac.java.classreader.ClassFile;
























public class UnsupportedAttribute
  extends AttributeInfo
{
  private String name;
  
  public UnsupportedAttribute(ClassFile cf, String name) {
    super(cf);
    this.name = name;
  }








  
  public String getName() {
    return this.name;
  }








  
  public String toString() {
    return "[UnsupportedAttribute: name=" + 
      getName() + "]";
  }
}
