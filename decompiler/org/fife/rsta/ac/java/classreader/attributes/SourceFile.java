package org.fife.rsta.ac.java.classreader.attributes;

import org.fife.rsta.ac.java.classreader.ClassFile;


































public class SourceFile
  extends AttributeInfo
{
  private int sourceFileIndex;
  
  public SourceFile(ClassFile cf, int sourceFileIndex) {
    super(cf);
    this.sourceFileIndex = sourceFileIndex;
  }







  
  public String getSourceFileName() {
    return getClassFile().getUtf8ValueFromConstantPool(this.sourceFileIndex);
  }








  
  public String toString() {
    return "[SourceFile: file=" + 
      getSourceFileName() + "]";
  }
}
