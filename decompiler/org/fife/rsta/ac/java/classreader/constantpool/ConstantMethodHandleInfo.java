package org.fife.rsta.ac.java.classreader.constantpool;
























public class ConstantMethodHandleInfo
  extends ConstantPoolInfo
{
  private int referenceKind;
  private int referenceIndex;
  
  public ConstantMethodHandleInfo(int referenceKind, int referenceIndex) {
    super(15);
    this.referenceKind = referenceKind;
    this.referenceIndex = referenceIndex;
  }

  
  public int getReferenceKind() {
    return this.referenceKind;
  }
  
  public int getReferenceIndex() {
    return this.referenceIndex;
  }







  
  public String toString() {
    return "[ConstantMethodHandleInfo: referenceKind=" + 
      getReferenceKind() + "; referenceIndex=" + 
      getReferenceIndex() + "]";
  }
}
