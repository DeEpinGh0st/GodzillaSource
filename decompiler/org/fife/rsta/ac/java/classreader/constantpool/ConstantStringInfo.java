package org.fife.rsta.ac.java.classreader.constantpool;

import org.fife.rsta.ac.java.classreader.ClassFile;























public class ConstantStringInfo
  extends ConstantPoolInfo
{
  private ClassFile cf;
  private int stringIndex;
  
  public ConstantStringInfo(ClassFile cf, int stringIndex) {
    super(8);
    this.cf = cf;
    this.stringIndex = stringIndex;
  }

  
  public int getStringIndex() {
    return this.stringIndex;
  }






  
  public String getStringValue() {
    return '"' + this.cf.getUtf8ValueFromConstantPool(getStringIndex()) + '"';
  }








  
  public String toString() {
    return "[ConstantStringInfo: stringIndex=" + 
      getStringIndex() + "]";
  }
}
