package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;





























public class FloatMemberValue
  extends MemberValue
{
  int valueIndex;
  
  public FloatMemberValue(int index, ConstPool cp) {
    super('F', cp);
    this.valueIndex = index;
  }





  
  public FloatMemberValue(float f, ConstPool cp) {
    super('F', cp);
    setValue(f);
  }



  
  public FloatMemberValue(ConstPool cp) {
    super('F', cp);
    setValue(0.0F);
  }

  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return Float.valueOf(getValue());
  }

  
  Class<?> getType(ClassLoader cl) {
    return float.class;
  }



  
  public float getValue() {
    return this.cp.getFloatInfo(this.valueIndex);
  }



  
  public void setValue(float newValue) {
    this.valueIndex = this.cp.addFloatInfo(newValue);
  }




  
  public String toString() {
    return Float.toString(getValue());
  }




  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }




  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitFloatMemberValue(this);
  }
}
