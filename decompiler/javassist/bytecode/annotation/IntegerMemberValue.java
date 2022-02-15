package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;




























public class IntegerMemberValue
  extends MemberValue
{
  int valueIndex;
  
  public IntegerMemberValue(int index, ConstPool cp) {
    super('I', cp);
    this.valueIndex = index;
  }











  
  public IntegerMemberValue(ConstPool cp, int value) {
    super('I', cp);
    setValue(value);
  }



  
  public IntegerMemberValue(ConstPool cp) {
    super('I', cp);
    setValue(0);
  }

  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return Integer.valueOf(getValue());
  }

  
  Class<?> getType(ClassLoader cl) {
    return int.class;
  }



  
  public int getValue() {
    return this.cp.getIntegerInfo(this.valueIndex);
  }



  
  public void setValue(int newValue) {
    this.valueIndex = this.cp.addIntegerInfo(newValue);
  }




  
  public String toString() {
    return Integer.toString(getValue());
  }




  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }




  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitIntegerMemberValue(this);
  }
}
