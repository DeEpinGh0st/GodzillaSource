package org.fife.rsta.ac.java.classreader.attributes;

import java.io.DataInputStream;
import java.io.IOException;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.Util;













public abstract class AttributeInfo
{
  private ClassFile cf;
  public int attributeNameIndex;
  
  protected AttributeInfo(ClassFile cf) {
    this.cf = cf;
  }

  
  public ClassFile getClassFile() {
    return this.cf;
  }






  
  public String getName() {
    return this.cf.getUtf8ValueFromConstantPool(this.attributeNameIndex);
  }




















  
  public static UnsupportedAttribute readUnsupportedAttribute(ClassFile cf, DataInputStream in, String attrName, int attrLength) throws IOException {
    Util.skipBytes(in, attrLength);
    return new UnsupportedAttribute(cf, attrName);
  }
}
