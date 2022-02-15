package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;



























public class ByteMemberValue
  extends MemberValue
{
  int valueIndex;
  
  public ByteMemberValue(int index, ConstPool cp) {
    super('B', cp);
    this.valueIndex = index;
  }





  
  public ByteMemberValue(byte b, ConstPool cp) {
    super('B', cp);
    setValue(b);
  }



  
  public ByteMemberValue(ConstPool cp) {
    super('B', cp);
    setValue((byte)0);
  }

  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return Byte.valueOf(getValue());
  }

  
  Class<?> getType(ClassLoader cl) {
    return byte.class;
  }



  
  public byte getValue() {
    return (byte)this.cp.getIntegerInfo(this.valueIndex);
  }



  
  public void setValue(byte newValue) {
    this.valueIndex = this.cp.addIntegerInfo(newValue);
  }




  
  public String toString() {
    return Byte.toString(getValue());
  }




  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }




  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitByteMemberValue(this);
  }
}
