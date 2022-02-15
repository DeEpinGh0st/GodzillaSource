package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;



























public class BooleanMemberValue
  extends MemberValue
{
  int valueIndex;
  
  public BooleanMemberValue(int index, ConstPool cp) {
    super('Z', cp);
    this.valueIndex = index;
  }





  
  public BooleanMemberValue(boolean b, ConstPool cp) {
    super('Z', cp);
    setValue(b);
  }



  
  public BooleanMemberValue(ConstPool cp) {
    super('Z', cp);
    setValue(false);
  }

  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return Boolean.valueOf(getValue());
  }

  
  Class<?> getType(ClassLoader cl) {
    return boolean.class;
  }



  
  public boolean getValue() {
    return (this.cp.getIntegerInfo(this.valueIndex) != 0);
  }



  
  public void setValue(boolean newValue) {
    this.valueIndex = this.cp.addIntegerInfo(newValue ? 1 : 0);
  }




  
  public String toString() {
    return getValue() ? "true" : "false";
  }




  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }




  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitBooleanMemberValue(this);
  }
}
